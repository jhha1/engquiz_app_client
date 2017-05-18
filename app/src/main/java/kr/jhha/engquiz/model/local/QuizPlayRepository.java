package kr.jhha.engquiz.model.local;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import kr.jhha.engquiz.util.exception.EResultCode;
import kr.jhha.engquiz.util.ui.MyLog;

/**
 * Created by jhha on 2016-10-14.
 *
 * 문장 업데이트
 * 문장 추가/삭제
 * 스크립트 추가/삭제
 *     List<Sentence> mSelectedQuizs - reset
 *
 * 퀴즈 폴더 변경
 *     List<Sentence> mSelectedQuizs - initialize
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

    public EResultCode initialize(Integer quizFolderId, String quizFolderTitle, List<Integer> quizFolderScriptIds )
    {
        this.mCurrentQuizFolerId = quizFolderId;
        this.mCurrentQuizFolerTitle = quizFolderTitle;
        setSentences( quizFolderId, quizFolderScriptIds );

        return EResultCode.SUCCESS;
    }

    // 업데이트 되었을 수 있는 문장, 스크립트 추가/삭제 등.. 반영
    public void reset(){
        setSentences( mCurrentQuizFolerId, null );
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

    private void setSentences( Integer quizFolderId, List<Integer> scriptIds )
    {
        if(scriptIds != null)
            setSentencesImpl( quizFolderId, scriptIds );
        else
            loadScriptIdsAndSetSentences( quizFolderId, scriptIds );
    }

    private void loadScriptIdsAndSetSentences( final Integer quizFolderId, List<Integer> scriptIds )
    {
        final QuizFolderRepository quizFolderRepo = QuizFolderRepository.getInstance();
        scriptIds = quizFolderRepo.getScriptIDsInFolder(quizFolderId);
        if (scriptIds != null && ! scriptIds.isEmpty())
            setSentencesImpl( quizFolderId, scriptIds );

        quizFolderRepo.getScriptsInFolder(
                quizFolderId,
                new QuizFolderRepository.GetQuizFolderScriptListCallback(){
                    @Override
                    public void onSuccess(List<Integer> quizFolderScripts) {
                        setSentencesImpl( quizFolderId, quizFolderScripts );
                    }
                    @Override
                    public void onFail(EResultCode resultCode) {
                        MyLog.e("loadScriptIds is fail. resultCode("+resultCode.toString()+")");
                    }
                }
        );
    }

    private void setSentencesImpl( Integer quizFolderId, List<Integer> scriptIds )
    {
        List<Sentence> sentences = new ArrayList<>();
        final ScriptRepository scriptRepo = ScriptRepository.getInstance();
        for(Integer scriptId : scriptIds)
        {
            Script script = scriptRepo.getScript( scriptId );
            if( Script.isNull(script) ) {
                MyLog.e("Script is null. scriptId("+scriptId+")");
                return;
            }

            MyLog.d("script title:"+script.title);
            for( Sentence sentence : script.sentences ) {
                if( Sentence.isNull(sentence) ){
                    MyLog.e("Sentence is null. scriptId("+scriptId+")");
                    return;
                }
                sentences.add( sentence );
            }
        }
        this.mSelectedQuizs.clear();
        this.mSelectedQuizs.addAll(sentences);
    }


    /*
    public void addSentences( List<Sentence> sentences ){
        if( null == sentences || sentences.isEmpty() ){
            return;
        }
        mSelectedQuizs.addAll(sentences);
    }

    public void delSentences( Integer scriptId ){
        if(false == Script.checkScriptID(scriptId)){
            return;
        }

        for( Sentence sentence : mSelectedQuizs ){
            if( sentence == null )
                continue;

            if( sentence.scriptId.equals(scriptId) ){
                delSentence(sentence);
            }
        }
    }

    public void addSentence( Sentence sentence ){
        if(Sentence.isNull(sentence))
            return;

        mSelectedQuizs.add(sentence);
    }

    public void delSentence( Sentence sentence ){
        if(Sentence.isNull(sentence))
            return;

        mSelectedQuizs.remove(sentence);
    }
    public void updateSentence( Sentence newSentence ){
        if( Sentence.isNull(newSentence))
            return;

        for( Sentence oldSentence: mSelectedQuizs){
            int oldID = oldSentence.sentenceId;
            int newID = newSentence.sentenceId;
            if(oldID == newID){
                delSentence(oldSentence);
                addSentence(newSentence);
            }
        }
    }
    */
}

