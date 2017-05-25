package kr.jhha.engquiz.model.local;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import kr.jhha.engquiz.util.FileHelper;
import kr.jhha.engquiz.util.Parsor;
import kr.jhha.engquiz.util.StringHelper;
import kr.jhha.engquiz.util.exception.system.MyIllegalStateException;
import kr.jhha.engquiz.util.ui.MyLog;

import static kr.jhha.engquiz.util.FileHelper.ParsedFile_AndroidPath;
import static kr.jhha.engquiz.util.FileHelper.PlayInfoFileName;
import static kr.jhha.engquiz.util.FileHelper.PlayInfoFolderPath;

/**
 * Created by jhha on 2016-10-14.
 *
 * 문장 업데이트
 * 문장 추가/삭제
 * 스크립트 추가/삭제
 *     List<Sentence> mSelectedScriptIds - reset
 *
 * 퀴즈 폴더 변경
 *     List<Sentence> mSelectedScriptIds - initialize
 */

public class QuizPlayRepository
{
    private static final QuizPlayRepository instance = new QuizPlayRepository();
    private QuizPlayRepository() {}
    public static QuizPlayRepository getInstance() {
        return instance;
    }

    private List<Integer> mSelectedScriptIds = null;
    private Random random = new Random();

    private final ScriptRepository mScriptRepository = ScriptRepository.getInstance();

    public boolean initialize()
    {
        return loadPlayInfo();
    }

    public boolean loadPlayInfo() {
        String playInfo = StringHelper.EMPTY_STRING;
        try {
            String dir = PlayInfoFolderPath;
            String name = PlayInfoFileName;

            final FileHelper file = FileHelper.getInstance();
            boolean bOK = file.createFileIfNotExist(dir, name);
            if( !bOK ){
                // 파싱된 파일이 저장될 디렉토리 생성에 실패하면 더이상 게임진행이 불가능. 앱 종료..
                MyLog.e("Failed create file for playInfo... " +
                        "dir (" + ParsedFile_AndroidPath + ")");
                return false;
            }

            playInfo = file.readFile( dir, name );

        } catch (MyIllegalStateException e){
            MyLog.e("Failed PlayQuiz Init. " +
                    "code:" + e.getErrorCode() +
                    ", msg:" + e.getMessage());
            return false;
        }

        // playInfo 파일 내용이 없는 경우.
        if(StringHelper.isNull(playInfo)) {
            mSelectedScriptIds = new ArrayList<>();
        } else {
            mSelectedScriptIds = unserialize(playInfo);
        }
        return true;
    }

    public boolean savePlayInfo(){
        String playInfo = serialize();

        final FileHelper file = FileHelper.getInstance();
        return file.overwrite( PlayInfoFolderPath,
                PlayInfoFileName,
                playInfo );
    }

    private List<Integer> unserialize( String playInfoString ){
        if(StringHelper.isNull(playInfoString)) {
            MyLog.e("Failed Play Parse. text is null (" + playInfoString + ")");
            return Collections.EMPTY_LIST;
        }

        String[] scriptIdsString = playInfoString.split( Parsor.MainSeperator);
        if( scriptIdsString == null  ) {
            MyLog.e("Failed PlayParse. Invalied PlayInfo. Split Rows are null  (" + playInfoString + ")");
            return Collections.EMPTY_LIST;
        }

        List<Integer> scripts = new ArrayList<>();
        final ScriptRepository scriptRepo = ScriptRepository.getInstance();
        for( String scriptId : scriptIdsString ) {
            if (StringHelper.isNull(scriptId)) {
                MyLog.e("Failed PlayParse. ScriptId is null. but Ignore it and continue");
                continue;
            }

            Script script = scriptRepo.getScript( Integer.parseInt(scriptId) );
            if( Script.isNull(script) ) {
                MyLog.e("Failed PlayParse. Script Obj is null. but Ignore it and continue. scriptId("+scriptId+")");
                continue;
            }

            scripts.add(script.scriptId);
        }

        return scripts;
    }

    private String serialize(){
        StringBuilder serializedString = new StringBuilder();
        for(Integer scriptId : mSelectedScriptIds){
            if( scriptId <= 0 ){
                MyLog.e("Invalid ScriptId. Ignore it(= do not save to play file) and continue. scriptId:"+scriptId);
                continue;
            }
            serializedString .append(scriptId + Parsor.MainSeperator);
        }
        return serializedString.toString();
    }


    public Integer getSentencesCount() {
        return mSelectedScriptIds.size();
    }

    public Sentence getQuiz() {
        if(mSelectedScriptIds == null || mSelectedScriptIds.isEmpty())
            return null;

        Script script = randomSelectScript();
        return randomSelectSentence( script );
    }

    private Script randomSelectScript()
    {
        if(mSelectedScriptIds.isEmpty())
            return null;

        // select script
        int scriptCnt = mSelectedScriptIds.size();
        int selectedIndex = random.nextInt(scriptCnt);
        Integer scriptId = mSelectedScriptIds.get(selectedIndex);
        Script script = mScriptRepository.getScript(scriptId);
        // 문제성 스크립트(빈 스크립트 등)가 선택되면,
        // 다른 스크립트를 딱 한번더 셀렉트 한다.
        // 한번더 셀렉트 한것도 문제가 있으면, 퀴즈용스크립트가 없다고 나감.
        if( Script.isNull(script)
                || script.sentences == null
                || script.sentences.isEmpty() )
        {
            if( scriptCnt <= 1 ){
                MyLog.e("Only Null Script or Null Sentence in PlayList ");
                // 현재 퀴즈리스트에는 스크립트가 하나 있는데, 그게 문제인것이므로
                // 다른 스크립트를 유저가 추가할때까지 기다려야한다.
                return null;
            }

            while(true) {
                int anotherScriptIndex = random.nextInt(scriptCnt);
                if( selectedIndex != anotherScriptIndex ){
                    scriptId = mSelectedScriptIds.get(anotherScriptIndex);
                    script = mScriptRepository.getScript(scriptId);
                    break;
                }
            }
        }

        return script;
    }

    private Sentence randomSelectSentence( Script script )
    {
        if( Script.isNull(script)
                || script.sentences == null
                || script.sentences.isEmpty() ){
            MyLog.e("Failed Random Select Sentence. " +
                    "Script or Sentences is null." +
                    "script{"+ ((script==null)?null:script.toString())+"}");
            return null;
        }

        // select sentence
        int sentenceCnt = script.sentences.size();
        int selectedIndex = random.nextInt(sentenceCnt);
        Sentence selectedSentence = script.sentences.get(selectedIndex);
        // 문제성 문장이 선택되면,
        // 다른 문장을 딱 한번더 셀렉트 한다.
        // 한번더 셀렉트 한것도 문제가 있으면, 퀴즈용스크립트가 없다고 나감.
        if( Sentence.isNull(selectedSentence) )
        {
            if( sentenceCnt <= 1 ){
                MyLog.e("Only Null Script or Null Sentence in PlayList ");
                // 현재 script에는 sentence 가 하나 있는데, 그게 문제인것이므로
                // 다른 sentence를 유저가 추가할때까지 기다려야한다.
                return null;
            }

            while(true) {
                int anotherIndex = random.nextInt(sentenceCnt);
                if( selectedIndex != anotherIndex ){
                    selectedSentence = script.sentences.get(anotherIndex);
                    break;
                }
            }
        }
        return selectedSentence;
    }


    public boolean addScript( Integer scriptId ){
        if(false == Script.checkScriptID(scriptId)){
            return false;
        }

        // add into memory
        mSelectedScriptIds.add(scriptId);

        // add into file : add memory 뒤에 해야함
        boolean bOK = updateLocalFile();
        if( !bOK ){
            // rollback
            mSelectedScriptIds.remove(scriptId);
            return false;
        }

        return true;
    }

    public boolean delScript( Integer scriptId ){
        if(false == Script.checkScriptID(scriptId)){
            return false;
        }

        // del into memory
        mSelectedScriptIds.remove(scriptId);

        // del into file : del memory 뒤에 해야함
        boolean bOK = updateLocalFile();
        if( !bOK ){
            // rollback
            mSelectedScriptIds.add(scriptId);
            return false;
        }

        return true;
    }

    private boolean updateLocalFile(){
        return savePlayInfo();
    }

    public boolean hasScript(Integer scriptId){
        return mSelectedScriptIds.contains(scriptId);
    }
}

