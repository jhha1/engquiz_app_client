package kr.jhha.engquiz.model.local;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import kr.jhha.engquiz.util.exception.EResultCode;
import kr.jhha.engquiz.util.ui.MyLog;

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

    private Integer mCurrentQuizFolerId;
    private String mCurrentQuizFolerTitle;

    private List<Sentence> mSelectedQuizs = new ArrayList<Sentence>();
    private Random random = new Random();

    public EResultCode initQuizData( QuizFolder quizfolder, List<Integer> quizFolderScriptIds ){
        return changePlayingQuizFolder( quizfolder.getId(), quizfolder.getTitle(), quizFolderScriptIds );
    }

    public Integer getPlayQuizFolderId(){
        return mCurrentQuizFolerId;
    }

    public String getPlayQuizFolderTitle(){
        return mCurrentQuizFolerTitle;
    }

    public Integer getSentencesCount() {
        return mSelectedQuizs.size();
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

    public EResultCode changePlayingQuizFolder( Integer quizFolderId, String quizFolderTitle, List<Integer> quizFolderScriptIds )
    {
        List<Integer> scriptIds = quizFolderScriptIds;
        if( scriptIds == null ) {
            final QuizFolderRepository quizFolderRepo = QuizFolderRepository.getInstance();
            scriptIds = quizFolderRepo.getScriptIDsInFolder(quizFolderId);
            if (scriptIds == null || scriptIds.isEmpty()) {
                return EResultCode.QUIZFOLDER__NOEXIST_SCRIPTS;
            }
        }

        this.mCurrentQuizFolerId = quizFolderId;
        this.mCurrentQuizFolerTitle = quizFolderTitle;
        this.mSelectedQuizs.clear();
        final ScriptRepository scriptRepo = ScriptRepository.getInstance();
        for(Integer scriptId : scriptIds) {
            Script script = scriptRepo.getScript( scriptId );
            if( Script.isNull(script) ) {
                MyLog.e("Script is null. scriptId("+scriptId+")");
                return EResultCode.SYSTEM_ERR;
            }
            MyLog.d("script title:"+script.title);
            for( Sentence sentence : script.sentences ) {
                if( Sentence.isNull(sentence) ){
                    MyLog.e("Sentence is null. scriptId("+scriptId+")");
                    return EResultCode.SYSTEM_ERR;
                }
                this.mSelectedQuizs.add( sentence );
            }
        }

        MyLog.i("result mSelectedQuizs:"+ mSelectedQuizs.toString());

        return EResultCode.SUCCESS;
    }

    private void rollbackPlayingQuizFolder(String oldQuizFolderTitle,
                                           List<Sentence> oldSelectedQuizs){
        this.mCurrentQuizFolerTitle = oldQuizFolderTitle;
        this.mSelectedQuizs = oldSelectedQuizs;
    }
}

