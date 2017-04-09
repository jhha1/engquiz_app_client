package kr.jhha.engquiz.quizfolder.scripts;

import android.util.Log;

import java.util.List;

import kr.jhha.engquiz.data.local.QuizFolderRepository;
import kr.jhha.engquiz.data.local.ScriptRepository;
import kr.jhha.engquiz.data.remote.EResultCode;

/**
 * Created by thyone on 2017-03-15.
 */

public class AddQuizFolderScriptPresenter implements AddQuizFolderScriptContract.ActionsListener {

    private final AddQuizFolderScriptContract.View mView;
    private final QuizFolderRepository mModel;

    public AddQuizFolderScriptPresenter(AddQuizFolderScriptContract.View view, QuizFolderRepository model ) {
        mModel = model;
        mView = view;
    }

    public void initScriptList(){
        final ScriptRepository scriptRepo = ScriptRepository.getInstance();
        String[] scriptTitleAll = scriptRepo.getScriptTitleAll();
        boolean bEmptyScripts =  (scriptTitleAll == null || scriptTitleAll.length <= 0);
        if( bEmptyScripts ) {
            Log.e("TAG", "quiz titles null");
            // 퀴즈폴더에 넣을수있는 스크립트들이 없으면,
            // 알림다이알로그를 띄우고 퀴즈폴더만들기에서 빠져나옴
            mView.showEmptyScriptDialog();
        } else {
            mView.showScriptList(scriptTitleAll);
        }
    }

    @Override
    public void addScriptIntoQuizFolder(Integer quizFolderId, String scriptTitle ) {
        Log.i("AppContent", "AddQuizFolderScriptPresenter addScriptIntoQuizFolder() called. quizFolderId:"+quizFolderId);
        final ScriptRepository scriptRepo = ScriptRepository.getInstance();
        Integer scriptId = scriptRepo.getScriptIdByTitle( scriptTitle );

        // 서버통신 퀴즈폴더 추가
        mModel.addQuizFolderDetail( quizFolderId, scriptId, onAddQuizFolder() );
    }

    private QuizFolderRepository.AddQuizFolderScriptCallback onAddQuizFolder() {
        return new QuizFolderRepository.AddQuizFolderScriptCallback(){

            @Override
            public void onSuccess(List<Integer> updatedScriptIds) {
                mView.onSuccessAddScriptInQuizFolder(updatedScriptIds);
                mView.clearUI();
                mView.returnToQuizFolderDetailFragment();
            }

            @Override
            public void onFail(EResultCode resultCode) {
                String msg;
                switch (resultCode){
                    case SCRIPT_DUPLICATED:
                        msg = "이미 추가된 스크립트입니다.";
                        break;
                    default:
                        msg = "스크립트 추가에 실패했습니다. 잠시 후 다시 시도해주세요.";
                        break;
                }
                mView.onFailAddQuizFolder( msg );
                mView.clearUI();
                mView.returnToQuizFolderDetailFragment();
            }
        };
    }

    @Override
    public void emptyScriptDialogOkButtonClicked() {
        mView.clearUI();
        mView.returnToQuizFolderDetailFragment();
    }
}
