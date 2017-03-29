package kr.jhha.engquiz.quizfolder;

import android.content.Context;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import kr.jhha.engquiz.R;
import kr.jhha.engquiz.data.local.QuizFolderRepository;
import kr.jhha.engquiz.data.local.ScriptRepository;
import kr.jhha.engquiz.data.local.UserModel;
import kr.jhha.engquiz.data.remote.EResultCode;

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
        mScriptListViewAdapter = makeAdapter();
    }

    /*
         아답터 생성
         아답터를 리프레쉬할 일이 없어, UI에 둘 필요 없다 판단,,여기로 옮김.
         기본 안드로이드 아답터 사용. 스크립트 전체 제목리스트를 보여줌.
     */
    private ArrayAdapter makeAdapter(){
        int resourceID = R.layout.content_textstyle_listview_checked_multiple;
        //int resourceID = android.R.layout.simple_list_item_multiple_choice;
        String[] scriptTitleAll = ScriptRepository.getInstance().getScriptTitleAll();
        if(scriptTitleAll == null || scriptTitleAll.length < 0) {
            Log.e("TAG", "quiz titles null");
            mView.showEmptyScriptDialog();
            return null;
        }

        return new ArrayAdapter<String>(mContext, resourceID, scriptTitleAll);
    }

    @Override
    public ArrayAdapter getAdapter(){
        return mScriptListViewAdapter;
    }

    @Override
    public Integer newQuizFolderTitleInputted( String title ) {
        Integer result = checkTitle( title );
        if( result != 0 ) {
            int nextAction = 1; // title re-input
            //mView.onFailQuizFolderTitle( nextAction );
            return nextAction;
        }

        //키보드 숨기기:
        // checkTitle()에 넣으면, 완료버튼 누르면 키보드 나옴
        // -> 완료버튼에서 checkTitle()쓰기때문에 다시 키보드 토글.
        hideKeyboard();
        return 0;
    }

    @Override
    public void scriptsSelected() {
        mView.showAddQuizFolderConfirmDialog();
    }

    @Override
    public void addQuizFolder( String title, ListView mItemListView )
    {
        Log.i("AppContent", "AddQuizFolderPresenter addQuizFolder() called. title:"+title);

        // 선택한 스크립트 개수 체크
        int selectedCount = mItemListView.getCheckedItemCount();
        if( 0 >= selectedCount ) {
            mView.onFailAddQuizFolder( 0, "선택된 퀴즈가 없습니다" );
            return;
        }

        // 선택한 스크립트들의 scriptId 가져오기
        SparseBooleanArray checked = mItemListView.getCheckedItemPositions();
        List<Integer> selectedItems = new ArrayList<>();
        for( int i = 0; i < checked.size(); i++) {
            int position = checked.keyAt(i);
            if (checked.valueAt(i)) {
                String scriptTitle = mScriptListViewAdapter.getItem(position);
                Integer scriptIndex = ScriptRepository.getInstance().getScriptIdAsTitle( scriptTitle );
                if( scriptIndex < 0 ) {
                    continue;
                }
                selectedItems.add( scriptIndex );
            }
        }

        // 서버통신 퀴즈폴더 추가
        Integer userId = UserModel.getInstance().getUserID();
        mModel.addQuizFolder( userId, title, selectedItems, onAddQuizFolder() );
    }

    private QuizFolderRepository.AddQuizFolderCallback onAddQuizFolder() {
        return new QuizFolderRepository.AddQuizFolderCallback(){

            @Override
            public void onSuccess() {
                mView.onSuccessAddQuizFolder();
            }

            @Override
            public void onFail(EResultCode resultCode) {
                mView.onFailAddQuizFolder( 1, "새 퀴즈폴더 생성에 실패했습니다. 잠시 후 다시 시도해주세요." );
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

    private void hideKeyboard()
    {
        InputMethodManager immhide =
                (InputMethodManager) mContext.getSystemService(mContext.INPUT_METHOD_SERVICE);
        immhide.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }
}
