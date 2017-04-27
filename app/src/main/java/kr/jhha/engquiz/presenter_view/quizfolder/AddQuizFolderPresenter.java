package kr.jhha.engquiz.presenter_view.quizfolder;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import kr.jhha.engquiz.R;
import kr.jhha.engquiz.model.local.QuizFolder;
import kr.jhha.engquiz.model.local.QuizFolderRepository;
import kr.jhha.engquiz.model.local.ScriptRepository;
import kr.jhha.engquiz.util.ui.Actions;
import kr.jhha.engquiz.util.exception.EResultCode;
import kr.jhha.engquiz.util.ui.MyLog;

/**
 * Created by thyone on 2017-03-15.
 */

public class AddQuizFolderPresenter implements AddQuizFolderContract.ActionsListener {

    private final AddQuizFolderContract.View mView;
    private final QuizFolderRepository mModel;
    private final Context mContext;
    private ArrayAdapter<String> mScriptListViewAdapter = null;

    public final static int ERR_DEAULT = 1;
    public final static int ERR_EMPTY_TITLE = 2;
    public final static int ERR_TITLE_MAX_LEN_OVER = 3;

    private boolean mIsEmptyScripts = false;

    public AddQuizFolderPresenter( Context context, AddQuizFolderContract.View view, QuizFolderRepository model ) {
        mModel = model;
        mView = view;
        mContext = context;
    }

    public void initScriptList()
    {
        final ScriptRepository scriptRepo = ScriptRepository.getInstance();
        String[] scriptTitleAll = scriptRepo.getScriptTitleAll();
        mIsEmptyScripts =  (scriptTitleAll == null || scriptTitleAll.length <= 0);
       if( mIsEmptyScripts ) {
             MyLog.e("quiz titles null");
            // 퀴즈폴더에 넣을수있는 스크립트들이 없으면,
            // 알림다이알로그를 띄우고 퀴즈폴더만들기에서 빠져나옴
      //      mView.showEmptyScriptDialog();
       } else {
            // 아답터 생성
            // UI에서 쓸일이 없어 여기서 생성
            int resourceID = R.layout.common_listview_textstyle_checked_multiple;
            //int resourceID = android.R.layout.simple_list_item_multiple_choice;
            mScriptListViewAdapter = new ArrayAdapter<String>(mContext, resourceID, scriptTitleAll);
            mView.setAdapter(mScriptListViewAdapter);
       }
        // 퀴즈폴더에 넣을수있는 스크립트가 있으면,
        // 퀴즈폴더제목 입력받는 다이알로그 띄움.
        mView.showQuizFolderTitleDialog();
    }

    @Override
    public void emptyScriptDialogOkButtonClicked() {
        mView.clearUI();
        mView.returnToQuizFolderFragment();
    }

    @Override
    public Integer checkInputtedTitle(String title )
    {
        if(title.isEmpty()) {
            return ERR_EMPTY_TITLE;
        } else if( title.length() > QuizFolder.TITLE_MAX_LEN ){
            return ERR_TITLE_MAX_LEN_OVER;
        }

        //키보드 숨기기:
        // checkTitle()에 넣으면, 완료버튼 누르면 키보드 나옴
        // -> 완료버튼에서 checkTitle()쓰기때문에 다시 키보드 토글.
        Actions.hideKeyboard(mContext);

        if( mIsEmptyScripts )
            mView.showEmptyScriptDialog();

        return 0;
    }

    @Override
    public void scriptsSelected(String title, ListView mItemListView) {

        if( !mIsEmptyScripts ) {
            // 선택한 스크립트 개수 체크
            int selectedCount = mItemListView.getCheckedItemCount();
            if( 0 >= selectedCount ) {
                mView.onFailAddQuizFolder( R.string.add_folder__fail_no_scripts_choice);
                return;
            }
        }
        addQuizFolder( title, mItemListView );
    }

    private void addQuizFolder(String title, ListView mItemListView)
    {
        MyLog.d("addQuizFolder title:"+title);

        List<Integer> selectedItems = new ArrayList<>();
        boolean bHasSelectedScipts = ! mIsEmptyScripts;
        if( bHasSelectedScipts )
        {
            if (mScriptListViewAdapter == null) {
                mView.onFailAddQuizFolder(R.string.add_folder__fail);
                return;
            }

            // 선택한 스크립트들의 scriptId 가져오기
            SparseBooleanArray checked = mItemListView.getCheckedItemPositions();
            for (int i = 0; i < checked.size(); i++) {
                final ScriptRepository scriptRepo = ScriptRepository.getInstance();
                if (checked.valueAt(i)) {
                    int position = checked.keyAt(i);
                    String scriptTitle = mScriptListViewAdapter.getItem(position);
                    Integer scriptId = scriptRepo.getScriptIdByTitle(scriptTitle);
                    if (scriptId < 0) {
                        MyLog.e("Invalid ScrpitId. " +
                                "but ignore it and continue Add other scripts into QuizFolder. " +
                                "scriptID:" + scriptId + ",scriptTitle:" + scriptTitle);
                        continue;
                    }
                    selectedItems.add(scriptId);
                }
            }

           if( selectedItems.isEmpty() ){
                mView.onFailAddQuizFolder( R.string.add_folder__fail );
                return;
            }
        }

        // 서버통신 퀴즈폴더 추가
        mModel.addQuizFolder( title, selectedItems, onAddQuizFolder() );
    }

    private QuizFolderRepository.AddQuizFolderCallback onAddQuizFolder() {
        return new QuizFolderRepository.AddQuizFolderCallback(){

            @Override
            public void onSuccess(List<QuizFolder> updatedQuizFolders) {
                mView.onSuccessAddQuizFolder(updatedQuizFolders);
                mView.clearUI();
                mView.returnToQuizFolderFragment();
            }

            @Override
            public void onFail(EResultCode resultCode) {
                int msgId;
                switch (resultCode){
                    case NETWORK_ERR:
                        msgId = R.string.common__network_err;
                        break;
                    default:
                        msgId = R.string.add_folder__fail;
                        break;
                }
                mView.onFailAddQuizFolder(msgId);
                mView.clearUI();
                mView.returnToQuizFolderFragment();
            }
        };
    }
}
