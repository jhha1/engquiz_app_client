package kr.jhha.engquiz.presenter_view.addsentence;

import android.util.Log;

import java.util.LinkedList;
import java.util.List;

import kr.jhha.engquiz.model.local.QuizFolder;
import kr.jhha.engquiz.model.local.QuizFolderRepository;
import kr.jhha.engquiz.model.local.Script;
import kr.jhha.engquiz.model.local.ScriptRepository;
import kr.jhha.engquiz.model.local.Sentence;
import kr.jhha.engquiz.util.exception.EResultCode;
import kr.jhha.engquiz.util.StringHelper;

/**
 * Created by thyone on 2017-03-15.
 */

public class AddSentencePresenter implements AddSentenceContract.ActionsListener {

    private final AddSentenceContract.View mView;
    private final QuizFolderRepository mQuizFolderModel;
    private final ScriptRepository mScriptModel;

    private String mSentenceKo;
    private String mSentenceEn;

    private boolean mIsNewScript;
    private String mScriptName = null; // 선택된 파일 이름

    private boolean mIsNewQuizFolder = false;
    private String mSelectedQuizFolderName = null;


    public AddSentencePresenter(AddSentenceContract.View view, ScriptRepository model ) {
        mView = view;
        mScriptModel = model;
        mQuizFolderModel = QuizFolderRepository.getInstance();
    }

    @Override
    public void sentencesInputted(String ko, String en)
    {
        if( checkSentences(ko, en) ){
            mSentenceKo = ko;
            mSentenceEn = en;

            List<String> scriptTitleAll = mScriptModel.getUserMadeScriptTitleAll();
            if( scriptTitleAll == null || scriptTitleAll.isEmpty() ){
                mView.showNeedMakeScriptDialog();
            } else {
                String[] titleArray = scriptTitleAll.toArray(new String[scriptTitleAll.size()]);
                mView.showDialogSelectScript(titleArray);
            }

        } else {
            //mView.
            mView.showErrorDialog(7);
        }
    }

    private boolean checkSentences(String ko, String en){
        if(StringHelper.isNull(ko)) {
            return false;
        }
        if(StringHelper.isNull(en)) {
            return false;
        }
        return true;
    }


    @Override
    public void makeNewScriptBtnClicked(){
        mView.showDialogMakeNewScript();
    }

    @Override
    public void makeNewScript(String scriptName) {
        Integer scriptId = mScriptModel.getScriptIdByTitle(scriptName);
        boolean bExistScript = (scriptId > 0);
        if (bExistScript) {
            mView.showDialogMakeNewScript_ReInput();
            return;
        }

        mIsNewScript = true;
        mScriptName = scriptName;
        getQuizFolderList();
    }

    private void getQuizFolderList(){
        // 퀴즈폴더 리스트를 로드하기 전에
        // Add Script에서 퀴즈폴더리스트를 참조하는 경우(1)에 대비해
        // 아래 함수(2)를 사용해 리스트를 받아온다.
        //
        // (1) 유저가 퀴즈폴더메뉴를 클릭하기 전에 Add script를 하는 경우.
        //      퀴즈폴더메뉴를 클릭하는 시점에 퀴즈폴더데이터로드.
        // (2) 아래함수: 메모리에 리스트가 있으면 반환 or 없으면 서버로부터 리스트를 받음
        mQuizFolderModel.getQuizFolders( new QuizFolderRepository.GetQuizFolderCallback(){

            @Override
            public void onSuccess(List<QuizFolder> quizFolders) {
                // 모든 로직이 성공하여 mQuizFolderModel에 퀴즈폴더리스트가 저장되어있다.
                // mQuizFolderModel에서 필요한 데이터를 가져다 쓴다.
                List<String> quizFolderList = mQuizFolderModel.getQuizFolderNames();
                if( quizFolderList == null || quizFolderList.isEmpty() ){
                    mView.showMakeNewQuizFolderDialog();
                } else {
                    mView.showQuizFolderSelectDialog( quizFolderList );
                }
            }

            @Override
            public void onFail(EResultCode resultCode) {
                mView.showErrorDialog(4);
            }
        });
    }

    @Override
    public void newQuizFolderTitleInputted( String quizFolderName )
    {
        if( mQuizFolderModel.isExistQuizFolder(quizFolderName) ){
            //mView.showDialogReInputQuizFolderName();
            mView.showErrorDialog(5);
            return;
        }
        mIsNewQuizFolder = true;
        mSelectedQuizFolderName = quizFolderName;
        postProcess();
    }

    @Override
    public void quizFolderSelected( String quizFolderName )
    {
        if( QuizFolder.isNewButton(quizFolderName) ){
            mView.showNewQuizFolderTitleInputDialog();
            return;
        }

        if( false == mQuizFolderModel.isExistQuizFolder(quizFolderName) ){
            mView.showErrorDialog(4);
            return;
        }

        mIsNewQuizFolder = false;
        mSelectedQuizFolderName = quizFolderName;
        postProcess();
    }

    @Override
    public void scriptSelected(String scriptName){
        mIsNewScript = false;
        mScriptName = scriptName;
        postProcess();
    }

    private void postProcess()
    {
        // Make Sentence
        Sentence newSentence = new Sentence();
        newSentence.textEn = mSentenceEn;
        newSentence.textKo = mSentenceKo;
        newSentence.src = Sentence.SRC_USER;

        // Add Sentence Into Script
        Script script = null;
        if(mIsNewScript) {
            // make new user's script Id
            Integer largestScriptID = 10000;
            Integer[] scriptIds = mScriptModel.getScriptIdAll();
            for(Integer id : scriptIds){
                largestScriptID = (largestScriptID < id)?id:largestScriptID;
            }
            Integer newScriptID = largestScriptID + 1;
            ///////////////////////////////////////////

            script = new Script();
            script.title = mScriptName;
            script.scriptId = newScriptID;
        } else {
            Integer scriptId = mScriptModel.getScriptIdByTitle(mScriptName);
            script = mScriptModel.getScript(scriptId);
            Log.i("############### ", "call by ref or value TEST script:" +script.toString());
        }
        newSentence.scriptId = script.scriptId;
        newSentence.sentenceId = 0;
        script.sentences.add( newSentence );
        mScriptModel.addUserCustomScript(script);
        Log.i("############### ", "call by ref or value TEST. after sentencec ADD..  :" +mScriptModel.getScript(script.scriptId).toString());
        mView.showAddSentenceSuccessDialog("스크립트가 소속된", mScriptName);

        if( mIsNewScript ) {
            // Add Script Into Quiz Folder
            if (mIsNewQuizFolder) {
                addQuizFolder(mSelectedQuizFolderName, script.scriptId);
            } else {
                addQuizFolderDetail(mSelectedQuizFolderName, script.scriptId);
            }
        }
    }

    private void addQuizFolder( String quizFolderName, Integer scriptId ) {
        Log.i("AppContent", "AddSentencePresenter addScriptIntoQuizFolder() called");
        List<Integer> scriptIds = new LinkedList<>();
        scriptIds.add(scriptId);
        mQuizFolderModel.addQuizFolder( quizFolderName, scriptIds, onAddQuizFolder(quizFolderName) );
    }

    private QuizFolderRepository.AddQuizFolderCallback onAddQuizFolder(final String quizFolderName ) {
        return new QuizFolderRepository.AddQuizFolderCallback( ){
            @Override
            public void onSuccess(List<QuizFolder> updatedQuizFolders) {
                mView.showAddSentenceSuccessDialog(quizFolderName, mScriptName);
            }
            @Override
            public void onFail(EResultCode resultCode) {
                mView.showErrorDialog(3);
            }
        };
    }

    private void addQuizFolderDetail( String quizFolderName, Integer scriptId ) {
        Log.i("AppContent", "AddSentencePresenter addScriptIntoQuizFolder() called");
        Integer quizFolderId = mQuizFolderModel.getQuizFolderIdByName(quizFolderName);
        mQuizFolderModel.addScriptIntoQuizFolder( quizFolderId, scriptId, onAddQuizFolderDetail(quizFolderId) );
    }

    private QuizFolderRepository.AddScriptIntoQuizFolderCallback onAddQuizFolderDetail(final Integer quizFolderId ) {
        return new QuizFolderRepository.AddScriptIntoQuizFolderCallback(){
            @Override
            public void onSuccess(List<Integer> updatedScriptIds) {
                String quizFolderName = mQuizFolderModel.getQuizFolderNameById(quizFolderId);
                mView.showAddSentenceSuccessDialog(quizFolderName, mScriptName);
            }
            @Override
            public void onFail(EResultCode resultCode) {
                if( resultCode == EResultCode.SCRIPT_DUPLICATED )
                    mView.showErrorDialog(1);
                else
                    mView.showErrorDialog(2);
            }
        };
    }
}
