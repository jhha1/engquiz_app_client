package kr.jhha.engquiz.z_legacy.quizfolder.scripts;

import java.util.List;

import kr.jhha.engquiz.R;
import kr.jhha.engquiz.model.local.QuizFolder;
import kr.jhha.engquiz.model.local.QuizFolderRepository;
import kr.jhha.engquiz.model.local.UserRepository;
import kr.jhha.engquiz.util.StringHelper;
import kr.jhha.engquiz.util.exception.EResultCode;
import kr.jhha.engquiz.util.ui.MyLog;

/**
 * Created by thyone on 2017-03-15.
 */

public class FolderScriptsPresenter implements FolderScriptsContract.ActionsListener {

    private final FolderScriptsContract.View mView;
    private final QuizFolderRepository mQuizFolderModel;

    public FolderScriptsPresenter(FolderScriptsContract.View view, QuizFolderRepository model ) {
        mQuizFolderModel = model;
        mView = view;
    }

    @Override
    public void initToolbarTitle(Integer quizFolderID) {
        String title = mQuizFolderModel.getQuizFolderNameById(quizFolderID);
        if(StringHelper.isNull(title)){
            title = "Scripts";
        }
        mView.showTitle(title);
    }

    public void getQuizFolderScripts( Integer quizFolderID ){
        mQuizFolderModel.getScriptsInFolder( quizFolderID, onGetQuizFolderScriptList() );
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
        MyLog.d("title:" + scriptTitle);

        if( QuizFolder.TEXT_ADD_SCRIPT_INTO_QUIZFOLDER.equals(scriptTitle) ) {
            // 스크립트추가 화면 전환
            mView.onChangeFragmetNew();
        } else {
            // 스크립트 디테일 보기 화면전환
            mView.onChangeFragmetShowSentenceList(scriptID, scriptTitle);
        }
    }

    @Override
    public void detachScript( FolderScriptsAdapter.ScriptSummary item )
    {
        if( item == null ) {
            mView.onFailDetachScript( R.string.del_script_from_folder__fail_no_exist_script );
            return;
        }

        Integer userId = UserRepository.getInstance().getUserID();
        Integer quizFolderId = item.quizFolderId;
        Integer scriptId = item.scriptId;
        mQuizFolderModel.detachScript( userId, quizFolderId, scriptId, onDelQuizFolderScript() );
    }

    private QuizFolderRepository.DetachScriptCallback onDelQuizFolderScript() {
        return new QuizFolderRepository.DetachScriptCallback(){

            @Override
            public void onSuccess(List<Integer> quizFolderScriptIds) {
                mView.onSuccessDetachScript(quizFolderScriptIds);
            }

            @Override
            public void onFail(EResultCode resultCode) {
                int msgId;
                switch (resultCode){
                    case NETWORK_ERR:
                        msgId = R.string.common__network_err;
                        break;
                    default:
                        msgId = R.string.del_script__fail;
                        break;
                }
                mView.onFailDetachScript(msgId);
            }
        };
    }
}
