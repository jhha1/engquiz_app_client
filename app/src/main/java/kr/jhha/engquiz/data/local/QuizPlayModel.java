package kr.jhha.engquiz.data.local;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import kr.jhha.engquiz.data.remote.AsyncNet;
import kr.jhha.engquiz.data.remote.EProtocol;
import kr.jhha.engquiz.data.remote.EProtocol2;
import kr.jhha.engquiz.data.remote.EResultCode;
import kr.jhha.engquiz.data.remote.Request;
import kr.jhha.engquiz.data.remote.Response;

/**
 * Created by jhha on 2016-10-14.
 */

public class QuizPlayModel
{
    public interface ChangePlayingQuizFolderCallback {
        void onSuccess();
        void onFail(EResultCode resultCode);
    }

    private static final QuizPlayModel instance = new QuizPlayModel();
    private QuizPlayModel() {}
    public static QuizPlayModel getInstance() {
        return instance;
    }

    private String mCurrentQuizFolerTitle;

    private List<Sentence> mSelectedQuizs = new ArrayList<Sentence>();
    private Random random = new Random();

    public EResultCode initPlayingQuizFolder( QuizFolder quizfolder ){
        return changePlayingQuizFolder( quizfolder );
    }

    public String getPlayQuizFolderTitle(){
        return mCurrentQuizFolerTitle;
    }

    public Sentence getQuiz() {
        if(mSelectedQuizs.isEmpty())
            return null;

        int selectedIndex = randomSelectOne();
        return mSelectedQuizs.get(selectedIndex);
    }

    private int randomSelectOne() {
        if(mSelectedQuizs.isEmpty())
            return 0;

        int totalQuizCnt = mSelectedQuizs.size();
        int selectedIndex = random.nextInt(totalQuizCnt);
        return selectedIndex;
    }

    /*
        서버 데이터 변경 성공시, 클라 데이터 변경
        클라 데이터 변경 실패시, 앱 재접속 유도해서
        서버로부터 받은 플레잉퀴즈폴더 데이터로 다시 셋팅하게 함
     */
    public void changePlayingQuizFolder( QuizFolder quizfolder,
                                         final QuizPlayModel.ChangePlayingQuizFolderCallback callback )
    {
        Log.i("AppContent", "QuizPlayModel changePlayingQuizFolder() called.");

        // change folder of Server
        final Integer userId = UserModel.getInstance().getUserID();
        Request request = new Request( EProtocol2.PID.ChangePlayingQuizFolder );
        request.set(EProtocol.UserID, userId);
        request.set(EProtocol.QuizFolderId, quizfolder.getId());
        AsyncNet net = new AsyncNet( request, onChangePlayingQuizFolder(quizfolder, callback) );
        net.execute();
    }

    private AsyncNet.Callback onChangePlayingQuizFolder(final QuizFolder newQuizFolder,
                                                         final QuizPlayModel.ChangePlayingQuizFolderCallback callback ) {
        return new AsyncNet.Callback() {
            @Override
            public void onResponse(Response response) {
                if (response.isSuccess()) {

                    // change folder of Memory
                    EResultCode code = changePlayingQuizFolder( newQuizFolder );
                    if(code != EResultCode.SUCCESS){
                        callback.onFail(code);
                        return;
                    }
                    mCurrentQuizFolerTitle = newQuizFolder.getTitle();
                    callback.onSuccess();

                } else {
                    // 서버 응답 에러
                    Log.e("AppContent", "onGetQuizFolders() UnkownERROR : "+ response.getResultCodeString());
                    callback.onFail( response.getResultCode() );
                }
            }
        };
    }

    private EResultCode changePlayingQuizFolder( QuizFolder quizfolder )
    {
        // check
        if( QuizFolder.isNull(quizfolder) ){
            return EResultCode.INVALID_ARGUMENT;
        }
        Integer quizFolderId = quizfolder.getId();
        final QuizFolderRepository quizFolderRepo = QuizFolderRepository.getInstance();
        List<Integer> scriptIds = quizFolderRepo.getQuizFolderScriptIDs( quizFolderId );
        if( scriptIds == null || scriptIds.isEmpty() ){
            return EResultCode.NOEXSITED_SCRIPT;
        }

        this.mSelectedQuizs.clear();
        final ScriptRepository scriptRepo = ScriptRepository.getInstance();
        for(Integer scriptId : scriptIds) {
            Script script = scriptRepo.getScript( scriptId );
            if( Script.isNull(script) ) {
                Log.e("######", "Script is null. scriptId("+scriptId+")");
                return EResultCode.SYSTEM_ERR;
            }
            Log.i("######", "QuizPlayModel changePlayingQuizFolder() script title:"+script.title);
            for( Sentence sentence : script.sentences ) {
                if( Sentence.isNull(sentence) ){
                    Log.e("######", "Sentence is null. scriptId("+scriptId+")");
                    return EResultCode.SYSTEM_ERR;
                }
                this.mSelectedQuizs.add( sentence );
            }
        }

        Log.i("######", "QuizPlayModel changePlayingQuizFolder() " +
                "result mSelectedQuizs:"+ mSelectedQuizs.toString());

        return EResultCode.SUCCESS;
    }

    private void rollbackPlayingQuizFolder(String oldQuizFolderTitle,
                                           List<Sentence> oldSelectedQuizs){
        this.mCurrentQuizFolerTitle = oldQuizFolderTitle;
        this.mSelectedQuizs = oldSelectedQuizs;
    }
}

