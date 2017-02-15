package kr.jhha.engquiz.backend_logic;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import kr.jhha.engquiz.net.EProtocol;
import kr.jhha.engquiz.net.EResultCode;
import kr.jhha.engquiz.net.Http;
import kr.jhha.engquiz.net.Response;
import kr.jhha.engquiz.net.protocols.CheckToNeedSyncProtocol;
import kr.jhha.engquiz.net.protocols.GetScriptsProtocol;
import kr.jhha.engquiz.net.protocols.MatchScriptProtocol;
import kr.jhha.engquiz.net.protocols.SignInProtocol;

/**
 * Created by thyone on 2017-02-10.
 *
 // 퀴즈스크립트 정책:
 //      1. 퀴즈게임용 데이터는 '내 퀴즈' 카테고리의 폴더 중 하나여야 한다.
 //      2. '내 퀴즈' 카테고리 안에 최소 하나 이상의 폴더가 있어야 한다.
 //      3. 폴더 안에 최소 하나 이상의 스크립트가 있어야 한다.
 //      4. 최대 생성 가능한 폴더는 20개. 스크립트는 일단 무제한.

 // 첫 접속시 퀴즈로 사용할 데이터:
 // 0. 첫 접속 여부는 클라 myapp_properties.xml 파일에 적힌 lastestPlayMyQuizIndex가 -1 이면.
 // 1. 서버와 클라의 스크립트 이름을 비교해, 일치하는 것만 내려받는다(=파싱된 스크립트)
 // 비교를 위해 검사하는 클라 내의 폴더는 카카오톡다운로드 폴더.
 // 2. 내 퀴즈 카테고리에 생성한 default my quiz 폴더에 내려받은 스크립트를 넣는다.
 // 3. 퀴즈게임에서는 default my quiz 폴더를 타겟폴더로 설정하고, 타겟 폴더 내의 스크립트들을 대상으로 퀴즈를 만들어 플레이시킨다.
 // * 만약, 클라의 카톡 폴더내에 서버와 일치하는 스크립트가 없다면, 스크립트가 저장된 폴더 경로를 입력받아 1번부터 수행한다.
 // * 클라에게 스크립트가 하나도 없다면, 임의의 예제 스크립트를 서버로부터 내려받아 2번부터 수행한다.

 // 스크립트는 전체 로드하지말고, 현재 플레이용만.
 */

public class Initailizer
{
    private static final Initailizer instance = new Initailizer();
    private Context mContext = null;

    private Initailizer() {}

    public static Initailizer getInstance() {
        return instance;
    }

    // SharedPreferences : 간단한 설정 값들을 xml형태로 저장 및 로드하도록 돕는 클래스.
    //                      선언은 MainActivity에서 해야함.
    public boolean initBackend( Context context, SharedPreferences preferences  )
    {
        Log.i("!!!!!!!!!!!!!!","Init Backend..");
        mContext = context;

        initUser();

        // 게임용 스크립트 구성
        checkToNeedSync();
        {
            // if( need sync )
            //      Map<Integer, Script> parsedScripts = doSync();
            Map<Integer, Script> parsedScripts = new HashMap<>();
            // 컨텐츠 로직 메모리에 셋팅
            ScriptManager.getInstance().init(parsedScripts);
        }
        // else
        {
            // 내퀴즈의 마지막 플레이한 폴더 스크립트들을 게임데이터로 셋팅
        }
        return true;
    }

    private void initUser()
    {
        if( User.isSignInUser() )
        {
            User.getInstance().init();
        }
        else    // user db가 없거나, user db에 row 가 없거나.
        {
            // 회원가입 절차
            // # 닉넴만들기 창으로 이동. ("아이디가 있습니다".. 버튼도추가. 클릭시 로긴창으로 이동.)
            //   : 닉넴을 입력받아 서버로부터 새 user정보를 받아와 (accountid) usr 객체에 셋팅     <= 완전 첫 접속임
            // # 아이디가 있어서, 아이디입력 로긴하면,                                             <= 앱 삭제후 재 설치
            //  : 서버에서 아이디 확인 후, 유저정보(account id)를 usr객체에 셋팅
            //     : 이런 경우, 앱 재설치므로, 스크립트 데이터가 앱에 없을것임.
            //        앱 삭제시 sqlite데이터도 삭제된다면, 유저플레이정보를 서버에 저장해 재설치시 데이터복원을 해야함.
            //
            String nickname = "joy";
            signIn( nickname );
        }
    }

    private void signIn( String nickname ) {
        Response response = new SignInProtocol( nickname ).callServer();
        EResultCode code = (EResultCode) response.get(EProtocol.CODE);
        if( code.equals( EResultCode.SUCCESS) ) {
            Integer accountID = (Integer) response.get(EProtocol.AccountID);
            User.getInstance().create( accountID, nickname, "macID" );
        } else if ( code.equals( EResultCode.NICKNAME_DUPLICATED) ) {
            Log.e("AppContent", "signIn() NICKNAME_DUPLICATED : "+nickname);
        } else {
            Log.e("AppContent", "signIn() UnkownERROR : "+ code.toString());
        }
    }

    private void checkToNeedSync()
    {
        Map<String, String> cliScriptIndexsAndRevisions = new HashMap<>();

        Response response = new CheckToNeedSyncProtocol( cliScriptIndexsAndRevisions ).callServer();

        String resultCode = (String) response.get(EProtocol.CheckSync_ResultCode);
        Map<Integer, String> newScriptSummary = (Map) response.get(EProtocol.CheckSync_NewScriptList);
        List<Integer> needUpdateScriptIndexes = (List) response.get(EProtocol.CheckSync_NeedUpdateScriptList);

        Log.i("AppContent", "#### checkToNeedSync() ###### " +
                "resultCode:"+resultCode
                 + "newScriptSummary:"+newScriptSummary.toString()
                + "needUpdateScriptIndexes:"+needUpdateScriptIndexes.toString()  );
    }

    private List<String> readPDFFileTitles()
    {
        List<String> pdfFileNames = new LinkedList<String>();
        FileManager manager = FileManager.getInstance();
        String kakaoDownFolder = manager.getAndroidAbsolutePath(manager.KaKaoDownloadFolder_AndroidPath);
        File[] scriptFiles = manager.getFileList( kakaoDownFolder );
        Log.i("!!!!!!!!!!!!!!","read pdf from kakaoDownFolder [" + kakaoDownFolder +"], AllFileCount["+scriptFiles.length+"]");
        for( File f : scriptFiles ){
            String fileName = f.getName();
            boolean bPDF = fileName.contains(".pdf");
            if( bPDF ){
                pdfFileNames.add( fileName );
            }
        }
        Log.i("!!!!!!!!!!!!!!","read pdf from kakaoDownFolder [" + kakaoDownFolder +"], PDFFileCount["+pdfFileNames.size()+"]");
        return pdfFileNames;
    }

    // 서버의 pdf 중, 클라에 있는 것 매치.
    private Map<String, Integer> checkMatchScripts( List<String> pdfFileNames )
    {
        Response response =  new MatchScriptProtocol( pdfFileNames ).callServer();
        Map<String, Integer> matchedScriptNames = (HashMap<String, Integer>) response.get(EProtocol.MatchedScripts);
        return matchedScriptNames;
    }

    private Map<Integer, Script> downloadMatchScripts( Map<String, Integer> matchedScriptNames )
    {
        // 매치된 스크립트 다운로드 (5개 단위로 끊어 :1MB)
        Map<Integer, Script> parsedScripts = new HashMap<Integer, Script>();
        int requestCount = 0;          // 서버 다운 요청 개수
        int maxRequestCount = 5;   // 서버에 요청 가능한 최대 파일 개수
        int totalFileCounts = 0;  // 누적 파일 개수
        List<Integer> scriptIndexes = new ArrayList<Integer>();
        for( Map.Entry<String, Integer> e : matchedScriptNames.entrySet() )
        {
            if( (requestCount >= maxRequestCount)
                    && (requestCount % maxRequestCount)  == 0 )
            {
                requestCount = 0;
                Map<Integer, Script> downloadPieces = downloadMatchScriptsImpl( scriptIndexes );
                if( downloadPieces == null ){
                    Log.e("AppContent","downloadMatchScripts() download script map is null");
                    continue;
                }
                parsedScripts.putAll( downloadPieces );
            }
            String scriptName = e.getKey();
            Integer scriptIndex = e.getValue();
            scriptIndexes.add(requestCount, scriptIndex);

            Log.i("!!!!!!!!!!!!!!","downloadMatchScripts() requestCount [" + requestCount +"], " +
                    "scriptIndex["+scriptIndexes.get(requestCount)+"], " +
                    "totalFileCounts["+totalFileCounts+"], " +
                    "matchedScriptNames.size() -1["+(matchedScriptNames.size() -1)+"], " +
                    "matchedScriptNames.size() % 5["+(matchedScriptNames.size() % 5)+"]" +
                    "(requestCount % maxRequestCount) ["+(requestCount % maxRequestCount) +"]");

            // 파일 총 개수가 5배수가 아니면, 5로 나눈 나머지 파일은 별도로 받아줘야 함.
            if( (totalFileCounts == matchedScriptNames.size() -1)
                    &&  (matchedScriptNames.size() % 5) != 0  )
            {
                Log.i("!!!!!!!!!!!!!!","downloadMatchScripts() send remain!! requestCount [" + requestCount +"], scriptIndex["+scriptIndexes.get(requestCount)+"]");

                Map<Integer, Script> downloadPieces = downloadMatchScriptsImpl( scriptIndexes );
                if( downloadPieces == null ){
                    Log.e("AppContent","downloadMatchScripts() download script map is null");
                } else {
                    parsedScripts.putAll(downloadPieces);
                }
                break;
            }
            ++requestCount;
            ++totalFileCounts;
        }
        return parsedScripts;
    }

    private Map<Integer, Script> downloadMatchScriptsImpl( List<Integer> scriptIndexes )
    {
        Response response = new GetScriptsProtocol( scriptIndexes ).callServer();
        return (HashMap<Integer, Script>) response.get(EProtocol.ParsedSciprt);
    }

    // 0: first, 1: none first
    private boolean isUserFirstAccess( SharedPreferences preferences  )
    {
        int firstAccess = preferences.getInt("firstAccess", 0);
        if( firstAccess == 0 )
            return true;
        else
            return false;
    }

    private boolean legacy( Context context, SharedPreferences preferences )
    {
        Log.i("!!!!!!!!!!!!!!","Init Backend..");
        mContext = context;

        SharedPreferences.Editor editor = preferences.edit();

        // 게임용 스크립트 구성
        Map<Integer, Script> parsedScripts = null;
        if( isUserFirstAccess( preferences ) )
        {
            Log.i("!!!!!!!!!!!!!!","First Acess to this app ..");
            // 클라 카톡다운폴더에서 pdf파일 이름 리스트 읽어옴
            List<String> pdfFileNames = readPDFFileTitles();
            if( pdfFileNames == null || pdfFileNames.isEmpty() ) {
                // 클라 카톡다운폴더에 pdf파일이 없음.
                Log.i("!!!!!!!!!!!!!!","There are no any PDF files in kakaoFolder. ");

                // pdf 파일이 있는 다른 폴더 입력받아 ?

                // pdf 파일이 아예 없으면, pdf파일이 있어야 게임이용가능하다는 메세지 띄우고 끝.
                return false;

            } else {
                // 서버의 pdf 중, 클라에 있는 것 매치 check.
                Map<String, Integer> matchedScriptNames = checkMatchScripts(pdfFileNames);
                if( matchedScriptNames == null ) {
                    Log.e("!!!!!!!!!!!!!!","matchedScriptNames (cli<->server) is null");
                    return false;
                }
                if( matchedScriptNames.isEmpty() ) {
                    // TODO 클라메세지. 쏘냐의 pdf파일이 없는 것 같습니다. 카톡단톡방에서 pdf파일을 받아주세요. (오류코드:0000)
                    Log.i("!!!!!!!!!!!!!!","No matched Script Names (cli<->server). ");
                    return true;
                }
                Log.i("!!!!!!!!!!!!!!","matchedScriptNames (cli<->server). " + matchedScriptNames.toString());

                // 매치된 스크립트 다운로드
                parsedScripts = downloadMatchScripts(matchedScriptNames);
                if( parsedScripts == null ) {
                    Log.e("!!!!!!!!!!!!!!","parsedScripts from downloadMatchScripts() (cli<->server) is null");
                    return false;
                }
                if( matchedScriptNames.isEmpty() ) {
                    // TODO 클라메세지. pdf파일 분석에 실패했습니다. 앱 종료 후 다시 켜주세요. (오류코드:0000)
                    Log.i("!!!!!!!!!!!!!!","No matched Script Names (cli<->server). ");
                    return true;
                }
                Log.i("!!!!!!!!!!!!!!","parsedScripts (cli<->server). size:" + parsedScripts.size() +",map: "+ parsedScripts.toString());

                // 안드로이드에 오프라인 파일 저장
                Map<Integer, Script> safeOverwroteScripts = new HashMap<>();
                for( Map.Entry<Integer, Script> e : parsedScripts.entrySet() ) {
                    Integer scriptIndex = e.getKey();
                    Script script = e.getValue();

                    boolean bOK = FileManager.getInstance().overwrite( FileManager.ParsedFile_AndroidPath,
                            script.title, script.toTextFileFormat());

                    // 저장 실패 된 스크립트로 데이터 init을 실패해 앱을 종료시키지 않는다.
                    // 정상적으로 저장된 것만 게임용으로 사용한다.
                    // 유저가 (어? 난 추가했는데 왜 안들어갔지?) 하고, 내 퀴즈에서 별도로 pdf를 추가 재시도 해보는 걸로. 그 로직에서는 실패하면 유저에게 메세지가 간다.
                    // 만약, 전체 스크립트가 파일쓰기 실패하면, 유저에게 init실패 메세지와 report, 앱 종료.
                    if( false == bOK ) {
                        Log.e("AppContent",
                                "Failed overwrite script into file. idx["+ script.index +"],name["+ script.title +"]");
                        continue;
                    }
                    // 정상적으로 저장된 것만 추리기.
                    safeOverwroteScripts.put( scriptIndex, script );
                }

                // 모든 파싱된 파일을 오프라인 폴더에 저장 실패
                if( safeOverwroteScripts.isEmpty() ) {
                    Log.e("AppContent",
                            "Failed overwrite script All.");
                    return false;
                }
                parsedScripts = safeOverwroteScripts;

                // 내퀴즈 디폴트 폴더를 만들고, 다운 받은 파일 전체를 삽입
                Log.i("#####################", "Start DB");
                int orderIndex = 0;
                String title = "default";
                List fileIndexes = new ArrayList<Integer>(parsedScripts.keySet());
                String fileIndexesJson = Utils.list2json( fileIndexes );
                DBHelper db = new DBHelper( mContext );
                db.insertNewMyQuiz( orderIndex, title, fileIndexesJson );
                String selectedRows = db.selectMyQuiz();
                Log.i("#####################", selectedRows);


                String lastplayFolderIndex = "lastplay=0";
                FileManager.getInstance().overwrite(
                        FileManager.MyQuizFolder_AndroidPath,
                        FileManager.MyQuizPlayInfo_AndroidPath,
                        lastplayFolderIndex);



                // 내퀴즈 폴더정보를 안드로이드 파일에 저장
            }
        }

        // 내퀴즈의 마지막 플레이한 폴더 스크립트들을 게임데이터로 셋팅
        //  (플레이 이력이 있는 케이스도 해당)

        // 내 퀴즈 라스트 플레이 정보 가져오기

        // 라스트 플레이에 해당하는 내퀴즈 디테일 정보 가져오기 (인덱스,제목)

        // 내퀴즈 디테일 파일에 적힌 스크립트를 읽어오기

        if(false) {
            Log.i("!!!!!!!!!!!!!!", "used to be Client.");
            List<String> scriptFiles = FileManager.getInstance().uploadParsedTextFiles();
            if (scriptFiles == null) {
                Log.e("!!!!!!!!!!!!!!", "parsed files in client is null");
                return false;
            }
            Log.i("!!!!!!!!!!!!!!", "uploadParsedTextFiles. size:" + scriptFiles.size() + ",map: " + scriptFiles.toString());
            parsedScripts = QuizDataMaker.parse(scriptFiles);
            if (parsedScripts == null) {
                Log.e("!!!!!!!!!!!!!!", "Parse ERROR, parsedScripts is null");
                return false;
            }
            Log.i("!!!!!!!!!!!!!!", "parsedScripts. size:" + parsedScripts.size() + ",map: " + parsedScripts.toString());
        }
        // 컨텐츠 로직 메모리에 셋팅
        ScriptManager.getInstance().init( parsedScripts );
        return true;
    }
}
