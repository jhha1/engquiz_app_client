package kr.jhha.engquiz.presenter_view.quizfolder.scripts;

import android.util.Log;

import java.util.List;

import kr.jhha.engquiz.model.local.QuizFolderRepository;
import kr.jhha.engquiz.model.local.QuizPlayRepository;
import kr.jhha.engquiz.model.local.ScriptRepository;
import kr.jhha.engquiz.util.exception.EResultCode;

/**
 * Created by thyone on 2017-03-15.
 */

public class AddScriptIntoFolderPresenter implements AddScriptIntoFolderContract.ActionsListener {

    private final AddScriptIntoFolderContract.View mView;
    private final QuizFolderRepository mModel;

    public AddScriptIntoFolderPresenter(AddScriptIntoFolderContract.View view, QuizFolderRepository model ) {
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
        Log.i("AppContent", "AddScriptIntoFolderPresenter addScriptIntoQuizFolder() called. quizFolderId:"+quizFolderId);
        final ScriptRepository scriptRepo = ScriptRepository.getInstance();
        Integer scriptId = scriptRepo.getScriptIdByTitle( scriptTitle );

        // 서버통신 스크립트를 퀴즈폴더에 추가
        mModel.addScriptIntoQuizFolder( quizFolderId, scriptId, onAddScriptIntoQuizFolder(quizFolderId) );
    }

    private QuizFolderRepository.AddScriptIntoQuizFolderCallback onAddScriptIntoQuizFolder( final Integer quizFolderId ) {
        return new QuizFolderRepository.AddScriptIntoQuizFolderCallback(){

            @Override
            public void onSuccess(List<Integer> sortedScriptIdAll) {
                // 이 퀴즈폴더로 게임플레이 중이었다면,
                // 추가된 스크립트가 게임에 적용되도록 함
                updatePlayingRepository(quizFolderId, sortedScriptIdAll);

                mView.onSuccessAddScriptIntoQuizFolder(sortedScriptIdAll);
                mView.clearUI();
                mView.returnToQuizFolderFragment();
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
                mView.onFailAddScriptIntoQuizFolder( msg );
                mView.clearUI();
                mView.returnToQuizFolderFragment();
            }
        };
    }

    private void updatePlayingRepository( Integer quizFolderId, List<Integer> updatedScriptIds ){
        final QuizPlayRepository playRepo = QuizPlayRepository.getInstance();
        Integer playFolderId = playRepo.getPlayQuizFolderId();
        if( quizFolderId != playFolderId )
            return;

        String quizFolderTitle = playRepo.getPlayQuizFolderTitle();
        playRepo.changePlayingQuizFolder(quizFolderId, quizFolderTitle, updatedScriptIds);
    }

    @Override
    public void emptyScriptDialogOkButtonClicked() {
        mView.clearUI();
        mView.returnToQuizFolderFragment();
    }
}
