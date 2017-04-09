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

public class QuizPlayRepository
{
    private static final QuizPlayRepository instance = new QuizPlayRepository();
    private QuizPlayRepository() {}
    public static QuizPlayRepository getInstance() {
        return instance;
    }

    private String mCurrentQuizFolerTitle;

    private List<Sentence> mSelectedQuizs = new ArrayList<Sentence>();
    private Random random = new Random();

    public EResultCode initQuizData( QuizFolder quizfolder, List quizFolderScriptIds ){
        return changePlayingQuizFolder( quizfolder, quizFolderScriptIds );
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

    public EResultCode changePlayingQuizFolder( QuizFolder quizfolder, List quizFolderScriptIds )
    {
        // check
        if( QuizFolder.isNull(quizfolder) ){
            return EResultCode.INVALID_ARGUMENT;
        }

        List<Integer> scriptIds = quizFolderScriptIds;
        if( scriptIds == null ) {
            Integer quizFolderId = quizfolder.getId();
            final QuizFolderRepository quizFolderRepo = QuizFolderRepository.getInstance();
            scriptIds = quizFolderRepo.getQuizFolderScriptIDs(quizFolderId);
            if (scriptIds == null || scriptIds.isEmpty()) {
                return EResultCode.NOEXSITED_SCRIPT;
            }
        }

        this.mCurrentQuizFolerTitle = quizfolder.getTitle();
        this.mSelectedQuizs.clear();
        final ScriptRepository scriptRepo = ScriptRepository.getInstance();
        for(Integer scriptId : scriptIds) {
            Script script = scriptRepo.getScript( scriptId );
            if( Script.isNull(script) ) {
                Log.e("######", "Script is null. scriptId("+scriptId+")");
                return EResultCode.SYSTEM_ERR;
            }
            Log.i("######", "QuizPlayRepository changePlayingQuizFolder() script title:"+script.title);
            for( Sentence sentence : script.sentences ) {
                if( Sentence.isNull(sentence) ){
                    Log.e("######", "Sentence is null. scriptId("+scriptId+")");
                    return EResultCode.SYSTEM_ERR;
                }
                this.mSelectedQuizs.add( sentence );
            }
        }

        Log.i("######", "QuizPlayRepository changePlayingQuizFolder() " +
                "result mSelectedQuizs:"+ mSelectedQuizs.toString());

        return EResultCode.SUCCESS;
    }

    private void rollbackPlayingQuizFolder(String oldQuizFolderTitle,
                                           List<Sentence> oldSelectedQuizs){
        this.mCurrentQuizFolerTitle = oldQuizFolderTitle;
        this.mSelectedQuizs = oldSelectedQuizs;
    }
}

