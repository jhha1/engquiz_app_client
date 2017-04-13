package kr.jhha.engquiz.presenter_view.quizfolder;

import android.content.Context;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import kr.jhha.engquiz.R;
import kr.jhha.engquiz.model.local.QuizFolder;
import kr.jhha.engquiz.model.local.QuizFolderRepository;
import kr.jhha.engquiz.model.local.ScriptRepository;
import kr.jhha.engquiz.util.Actions;
import kr.jhha.engquiz.util.exception.EResultCode;

/**
 * Created by thyone on 2017-03-15.
 */

public class AddQuizFolderPresenter implements AddQuizFolderContract.ActionsListener {

    private final AddQuizFolderContract.View mView;
    private final QuizFolderRepository mModel;
    private final Context mContext;
    private ArrayAdapter<String> mScriptListViewAdapter = null;

    public AddQuizFolderPresenter( Context context, AddQuizFolderContract.View view, QuizFolderRepository model ) {
        mModel = model;
        mView = view;
        mContext = context;
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
            // 아답터 생성
            // UI에서 쓸일이 없어 여기서 생성
            int resourceID = R.layout.content_textstyle_listview_checked_multiple;
            //int resourceID = android.R.layout.simple_list_item_multiple_choice;
            mScriptListViewAdapter = new ArrayAdapter<String>(mContext, resourceID, scriptTitleAll);
            mView.setAdapter(mScriptListViewAdapter);

            // 퀴즈폴더에 넣을수있는 스크립트가 있으면,
            // 퀴즈폴더제목 입력받는 다이알로그 띄움.
            mView.showQuizFolderTitleDialog();
        }
    }

    @Override
    public void emptyScriptDialogOkButtonClicked() {
        mView.clearUI();
        mView.returnToQuizFolderFragment();
    }

    @Override
    public Integer checkInputtedTitle(String title ) {
        Integer result = checkTitle( title );
        if( result != 0 ) {
            int nextAction = 1; // title re-input
            return nextAction;
        }

        //키보드 숨기기:
        // checkTitle()에 넣으면, 완료버튼 누르면 키보드 나옴
        // -> 완료버튼에서 checkTitle()쓰기때문에 다시 키보드 토글.
        Actions.hideKeyboard(mContext);
        return 0;
    }

    @Override
    public void scriptsSelected() {
        mView.showAddQuizFolderConfirmDialog();
    }

    @Override
    public void addQuizFolder(String title, ListView mItemListView)
    {
        Log.i("AppContent", "AddQuizFolderPresenter addScriptIntoQuizFolder() called. title:"+title);

        // 선택한 스크립트 개수 체크
        int selectedCount = mItemListView.getCheckedItemCount();
        if( 0 >= selectedCount ) {
            mView.onFailAddQuizFolder( "선택된 퀴즈가 없습니다" );
            return;
        }

        if( mScriptListViewAdapter == null ){
            mView.onFailAddQuizFolder( "일시적으로 퀴즈폴더를 생성할 수 없습니다. 잠시후 다시 시도해주세요." );
            return;
        }

        // 선택한 스크립트들의 scriptId 가져오기
        SparseBooleanArray checked = mItemListView.getCheckedItemPositions();
        List<Integer> selectedItems = new ArrayList<>();
        for( int i = 0; i < checked.size(); i++) {
            final ScriptRepository scriptRepo = ScriptRepository.getInstance();
            if (checked.valueAt(i)) {
                int position = checked.keyAt(i);
                String scriptTitle = mScriptListViewAdapter.getItem(position);
                Integer scriptId = scriptRepo.getScriptIdByTitle( scriptTitle );
                if( scriptId < 0 ) {
                    Log.e("#########","Invalid ScrpitId. " +
                            "but ignore it and continue Add other scripts into QuizFolder. " +
                            "scriptID:"+scriptId+",scriptTitle:"+scriptTitle);
                    continue;
                }
                selectedItems.add( scriptId );
            }
        }

        if( selectedItems.isEmpty() ){
            mView.onFailAddQuizFolder( "일시적으로 퀴즈폴더를 생성할 수 없습니다. 잠시후 다시 시도해주세요." );
            return;
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
                mView.onFailAddQuizFolder(  "새 퀴즈폴더 생성에 실패했습니다. 잠시 후 다시 시도해주세요." );
                mView.clearUI();
                mView.returnToQuizFolderFragment();
            }
        };
    }

    private Integer checkTitle( String title )
    {
        String titleStr = title;
        if(titleStr.isEmpty()) {
            int emptyTitle = 1;
            return emptyTitle;
        }

        int success = 0;
        return success;
    }
}
