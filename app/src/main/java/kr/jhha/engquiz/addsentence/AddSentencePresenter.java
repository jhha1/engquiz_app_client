package kr.jhha.engquiz.addsentence;

import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import kr.jhha.engquiz.addscript.AddScriptContract;
import kr.jhha.engquiz.data.local.QuizFolder;
import kr.jhha.engquiz.data.local.QuizFolderRepository;
import kr.jhha.engquiz.data.local.Script;
import kr.jhha.engquiz.data.local.ScriptRepository;
import kr.jhha.engquiz.data.local.Sentence;
import kr.jhha.engquiz.data.local.UserRepository;
import kr.jhha.engquiz.data.remote.EResultCode;
import kr.jhha.engquiz.util.FileHelper;
import kr.jhha.engquiz.util.StringHelper;

/**
 * Created by thyone on 2017-03-15.
 */

public class AddSentencePresenter implements AddSentenceContract.ActionsListener {

    private final AddSentenceContract.View mView;
    private final ScriptRepository mModel;
    private final ScriptRepository mScriptModel;

    private String mSentenceKo;
    private String mSentenceEn;
    private Script mSelectedScript;

    private List<String> mItems = null;     // 리스트 뷰의 각 row에 출력될 파일명
    private List<String> mFilepath = null;  // 파일의 path + name (절대위치)

    private String mCurrentDirectoryPath;  // 선택된 파일의 폴더 위치
    private String mSelectedScriptName = null; // 선택된 파일 이름

    private boolean bNewQuizFolder = false;
    private Integer mSelectedQuizFolderId = -1;
    private String mSelectedQuizFolderName = null;



    public AddSentencePresenter(AddSentenceContract.View view, ScriptRepository model ) {
        mView = view;
        mModel = model;
        mScriptModel = model;
    }

    @Override
    public void sentencesInputted(String ko, String en)
    {
        if( checkSentences(ko, en) ){
            mSentenceKo = ko;
            mSentenceEn = en;

            String[] scriptTitleAll = mScriptModel.getScriptTitleAll();
            mView.showDialogSelectScript(scriptTitleAll);
        }
    }

    private boolean checkSentences(String ko, String en){
        if(StringHelper.isNullString(ko)) {

        }
        if(StringHelper.isNullString(en)) {

        }
        return true;
    }

    @Override
    public void makeNewScriptBtnClicked(){
        mView.showDialogMakeNewScript();
    }

    @Override
    public void makeNewScript(String scriptName){
        if( checkIfExistScript(scriptName)){
            mView.showReInputNewScriptName();
            return;
        }

        // make new user's script Id
        Integer largestScriptID = 10000;
        Integer[] scriptIds = mScriptModel.getScriptIdAll();
        for(Integer id : scriptIds){
            largestScriptID = (largestScriptID < id)?id:largestScriptID;
        }
        Integer newScriptID = largestScriptID + 1;
        ///////////////////////////////////////////


        Script newScript = new Script();
        newScript.title = scriptName;
        newScript.scriptId = newScriptID;
        mScriptModel.addScript(newScript);
        scriptSelected(scriptName);
    }

    private boolean checkIfExistScript(String scriptName){
        Integer scriptId = mScriptModel.getScriptIdByTitle( scriptName );
        if( scriptId > 0 ) {
            return true;
        }
        return false;
    }

    @Override
    public void scriptSelected(String selectedScriptTitle){
        Integer scriptId = mScriptModel.getScriptIdByTitle( selectedScriptTitle );
        Script script = mScriptModel.getScript(scriptId);

        Sentence newSentence = new Sentence();
        newSentence.textEn = mSentenceEn;
        newSentence.textKo = mSentenceKo;
        newSentence.scriptId = script.scriptId;
        newSentence.sentenceId = 0;
        newSentence.src = Sentence.SRC_USER;

        List<Sentence> sentences = script.sentences;
        sentences.add(newSentence);
        script.sentences = sentences;

        mSelectedScript = script;

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
        final QuizFolderRepository quizFolderRepo = QuizFolderRepository.getInstance();
        quizFolderRepo.getQuizFolders( new QuizFolderRepository.GetQuizFolderCallback(){

            @Override
            public void onSuccess(List<QuizFolder> quizFolders) {
                // 모든 로직이 성공하여 mQuizFolderModel에 퀴즈폴더리스트가 저장되어있다.
                // mQuizFolderModel에서 필요한 데이터를 가져다 쓴다.
                List<String> quizFolderList = quizFolderRepo.getQuizFolderNames();
                if( quizFolderList == null || quizFolderList.isEmpty() ){
                    mView.showNeedMakeQuizFolderDialog();
                } else {
                    mView.showQuizFolderSelectDialog( quizFolderList );
                }
            }

            @Override
            public void onFail(EResultCode resultCode) {
                mView.showErrorDialog(3);
            }
        });
    }

    @Override
    public void quizFolderSelected( String quizFolderName ) {
        if( quizFolderName.equals( QuizFolder.TEXT_NEW_FOLDER) ){
            mView.showNewQuizFolderTitleInputDialog();
        } else {
            bNewQuizFolder = false;
            callConfirmDialog( quizFolderName, bNewQuizFolder );
        }
    }

    @Override
    public void newQuizFolderTitleInputted(String quizFolderName ) {
        bNewQuizFolder = true;
        callConfirmDialog( quizFolderName, bNewQuizFolder );
    }

    private void callConfirmDialog( String quizFolderName, boolean bNewQuizFolder ) {
        mSelectedQuizFolderName = quizFolderName;
        if( bNewQuizFolder == false ) {
            mSelectedQuizFolderId = mQuizFolderModel.getQuizFolderIdByName( quizFolderName );
        }
        String fileName = mSelectedScriptName;
        final FileHelper file = FileHelper.getInstance();
        float filesize = file.getFileMegaSize(mCurrentDirectoryPath, fileName, 1);
        mView.showAddScriptConfirmDialog(fileName, filesize);
    }

}
