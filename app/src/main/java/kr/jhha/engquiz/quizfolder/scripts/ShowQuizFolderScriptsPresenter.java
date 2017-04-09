package kr.jhha.engquiz.quizfolder.scripts;

import android.util.Log;

import java.util.List;

import kr.jhha.engquiz.data.local.QuizFolder;
import kr.jhha.engquiz.data.local.QuizFolderRepository;
import kr.jhha.engquiz.data.local.UserRepository;
import kr.jhha.engquiz.data.remote.EResultCode;

/**
 * Created by thyone on 2017-03-15.
 */

public class ShowQuizFolderScriptsPresenter implements ShowQuizFolderScriptsContract.ActionsListener {

    private final ShowQuizFolderScriptsContract.View mView;
    private final QuizFolderRepository mQuizFolderModel;

    public ShowQuizFolderScriptsPresenter(ShowQuizFolderScriptsContract.View view, QuizFolderRepository model ) {
        mQuizFolderModel = model;
        mView = view;
    }

    public void getQuizFolderScripts( Integer quizFolderID ){
        Log.i("AppContent", "ShowSentencePresenter initQuizFolderScripts() called");
        final Integer userId = UserRepository.getInstance().getUserID();
        mQuizFolderModel.getQuizFolderScriptList( userId, quizFolderID, onGetQuizFolderScriptList() );
    }

    private QuizFolderRepository.GetQuizFolderScriptListCallback onGetQuizFolderScriptList() {
        return new QuizFolderRepository.GetQuizFolderScriptListCallback(){

            @Override
            public void onSuccess(List<Integer> quizFolderScripts) {
                mView.onSuccessGetScrpits(quizFolderScripts);
            }

            @Override
            public void onFail(EResultCode resultCode) {
                mView.onFailGetScripts();
            }
        };
    }

    @Override
    public void listViewItemClicked(Integer scriptID, String scriptTitle) {
        Log.d("%%%%%%%%%%%%%%%", "ShowQuizFolderScriptsPresenter.listViewItemClicked. title:" + scriptTitle);

        if( QuizFolder.TEXT_ADD_SCRIPT_INTO_QUIZFOLDER.equals(scriptTitle) ) {
            // 스크립트추가 화면 전환
            mView.onChangeFragmetNew();
        } else {
            // 스크립트 디테일 보기 화면전환
            mView.onChangeFragmetShowSentenceList(scriptID, scriptTitle);
        }
    }

    @Override
    public void delQuizFolderScript(QuizFolderScriptsAdapter.ScriptSummary item ){
        Log.i("AppContent", "ShowSentencePresenter delQuizFolderScript() called");

        if( item == null ) {
            String msg = "삭제할 스크립트가 없습니다";
            mView.onFailDelScript( msg );
            return;
        }

        Integer userId = UserRepository.getInstance().getUserID();
        Integer quizFolderId = item.quizFolderId;
        Integer scriptId = item.scriptId;
        mQuizFolderModel.delQuizFolderScript( userId, quizFolderId, scriptId, onDelQuizFolderScript() );
    }

    private QuizFolderRepository.DelQuizFolderScriptCallback onDelQuizFolderScript() {
        return new QuizFolderRepository.DelQuizFolderScriptCallback(){

            @Override
            public void onSuccess(List<Integer> quizFolderScriptIds) {
                mView.onSuccessDelScript(quizFolderScriptIds);
            }

            @Override
            public void onFail(EResultCode resultCode) {
                String msg = "스크립트 삭제에 실패했습니다";
                mView.onFailDelScript(msg);
            }
        };
    }
}
