package kr.jhha.engquiz.presenter_view.quizfolder.scripts;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import kr.jhha.engquiz.R;
import kr.jhha.engquiz.model.local.QuizFolderRepository;
import kr.jhha.engquiz.model.local.QuizPlayRepository;
import kr.jhha.engquiz.model.local.ScriptRepository;
import kr.jhha.engquiz.util.exception.EResultCode;
import kr.jhha.engquiz.util.ui.MyLog;

/**
 * Created by thyone on 2017-03-15.
 */

public class AddScriptIntoFolderPresenter implements AddScriptIntoFolderContract.ActionsListener {

    private final AddScriptIntoFolderContract.View mView;
    private final QuizFolderRepository mModel;
    private final Context mContext;
    private ArrayAdapter<String> mScriptListViewAdapter = null;

    public AddScriptIntoFolderPresenter(Context context, AddScriptIntoFolderContract.View view, QuizFolderRepository model ) {
        mModel = model;
        mView = view;
        mContext = context;
    }

    public void initScriptList()
    {
        final ScriptRepository scriptRepo = ScriptRepository.getInstance();
        String[] scriptTitleAll = scriptRepo.getScriptTitleAll();
        boolean bEmptyScripts =  (scriptTitleAll == null || scriptTitleAll.length <= 0);
        if( bEmptyScripts ) {
            MyLog.e("quiz titles null");
            // 퀴즈폴더에 넣을수있는 스크립트들이 없으면,
            // 알림다이알로그를 띄우고 퀴즈폴더만들기에서 빠져나옴
            mView.showEmptyScriptDialog();
        } else {
            // 플레이 리스트 아답터 연결
            int resourceID = R.layout.common_listview_textstyle_checked_multiple;
            mScriptListViewAdapter = new ArrayAdapter<String>(mContext, resourceID, scriptTitleAll);
            mView.setAdapter(mScriptListViewAdapter);
        }
    }

    @Override
    public void scriptsSelected( Integer quizfolderId, ListView itemListView )
    {
        if( check(quizfolderId, itemListView) ) {
            addScript(quizfolderId, itemListView);
        }
    }

    private boolean check (Integer quizfolderId, ListView itemListView ){
        if( ! mModel.isExistQuizFolder(quizfolderId) ){
            mView.onFailAddScriptIntoQuizFolder( R.string.add_script_into_folder__fail);
            return false;
        }

        // 선택한 스크립트 개수 체크
        int selectedCount = itemListView.getCheckedItemCount();
        if( 0 >= selectedCount ) {
            mView.onFailAddScriptIntoQuizFolder( R.string.add_folder__fail_no_scripts_choice);
            return false;
        }

        // 이미 추가한 스크립트인지는 체크 안함
        // 서버에서는 이미 추가된거는 제외하고 추가함.
        return true;
    }

    private void addScript(Integer quizfolderId, ListView itemListView)
    {
        // 선택한 스크립트들의 scriptId 가져오기
        SparseBooleanArray checked = itemListView.getCheckedItemPositions();
        List<Integer> selectedItems = new ArrayList<>();
        for( int i = 0; i < checked.size(); i++) {
            final ScriptRepository scriptRepo = ScriptRepository.getInstance();
            if (checked.valueAt(i)) {
                int position = checked.keyAt(i);
                String scriptTitle = mScriptListViewAdapter.getItem(position);
                Integer scriptId = scriptRepo.getScriptIdByTitle( scriptTitle );
                if( scriptId < 0 ) {
                    MyLog.e("Invalid ScrpitId. " +
                            "but ignore it and continue Add other scripts into QuizFolder. " +
                            "scriptID:"+scriptId+",scriptTitle:"+scriptTitle);
                    continue;
                }
                selectedItems.add( scriptId );
            }
        }

        if( selectedItems.isEmpty() ){
            mView.onFailAddScriptIntoQuizFolder( R.string.add_script_into_folder__fail );
            return;
        }

        // 서버통신 스크립트를 퀴즈폴더에 추가
        mModel.attachScript( quizfolderId, selectedItems, onAddScriptIntoQuizFolder(quizfolderId) );
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
                int msgId;
                switch (resultCode){
                    case SCRIPT_DUPLICATED:
                        msgId = R.string.add_script_into_folder__fail_already_added;
                        break;
                    case NETWORK_ERR:
                        msgId = R.string.common__network_err;
                        break;
                    default:
                        msgId = R.string.add_script_into_folder__fail;
                        break;
                }
                mView.onFailAddScriptIntoQuizFolder( msgId );
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
        //playRepo.reset(quizFolderId, quizFolderTitle, updatedScriptIds);
    }

    @Override
    public void emptyScriptDialogOkButtonClicked() {
        mView.clearUI();
        mView.returnToQuizFolderFragment();
    }
}
