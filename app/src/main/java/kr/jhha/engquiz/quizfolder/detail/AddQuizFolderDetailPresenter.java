package kr.jhha.engquiz.quizfolder.detail;

import android.content.Context;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import kr.jhha.engquiz.R;
import kr.jhha.engquiz.data.local.QuizFolderRepository;
import kr.jhha.engquiz.data.local.ScriptRepository;
import kr.jhha.engquiz.data.remote.EResultCode;

/**
 * Created by thyone on 2017-03-15.
 */

public class AddQuizFolderDetailPresenter implements AddQuizFolderDetailContract.ActionsListener {

    private final AddQuizFolderDetailContract.View mView;
    private final QuizFolderRepository mModel;
    private final Context mContext;
    private ArrayAdapter<String> mScriptListViewAdapter = null;

    public AddQuizFolderDetailPresenter(Context context, AddQuizFolderDetailContract.View view, QuizFolderRepository model ) {
        mModel = model;
        mView = view;
        mContext = context;
    }

    /*
        아답터 생성
        아답터를 리프레쉬할 일이 없어, UI에 둘 필요 없다 판단,,여기로 옮김.
        기본 안드로이드 아답터 사용. 스크립트 전체 제목리스트를 보여줌.
    */
    @Override
    public ArrayAdapter getAdapter() {
        if( mScriptListViewAdapter == null )
        {
            final ScriptRepository scriptRepo = ScriptRepository.getInstance();
            String[] scriptTitleAll = ScriptRepository.getInstance().getScriptTitleAll();
            boolean bEmptyScripts =  (scriptTitleAll == null || scriptTitleAll.length <= 0);
            if( bEmptyScripts ) {
                Log.e("TAG", "quiz titles null");
                // 퀴즈폴더에 넣을수있는 스크립트들이 없으면,
                // 알림다이알로그를 띄우고 퀴즈폴더만들기에서 빠져나옴
                mView.showEmptyScriptDialog();
                return null;
            }

            int resourceID = R.layout.content_textstyle_listview_checked_multiple;
            //int resourceID = android.R.layout.simple_list_item_multiple_choice;
            return new ArrayAdapter<String>(mContext, resourceID, scriptTitleAll);
        }
        return mScriptListViewAdapter;
    }

    @Override
    public void emptyScriptDialogOkButtonClicked() {
        mView.clearUI();
        mView.returnToQuizFolderDetailFragment();
    }

    @Override
    public void addScriptInQuizFolder(Integer quizFolderId, ListView listView ) {
        Log.i("AppContent", "AddQuizFolderDetailPresenter addScriptInQuizFolder() called. quizFolderId:"+quizFolderId);

        // 선택한 스크립트 개수 체크
        int selectedCount = listView.getCheckedItemCount();
        if( 0 >= selectedCount ) {
            mView.onFailAddQuizFolder( "선택된 스크립트가 없습니다" );
            return;
        }

        // 선택한 스크립트들의 scriptId 가져오기
        SparseBooleanArray checked = listView.getCheckedItemPositions();
        List<Integer> selectedItems = new ArrayList<>();
        for( int i = 0; i < checked.size(); i++) {
            int position = checked.keyAt(i);
            if (checked.valueAt(i)) {
                String scriptTitle = mScriptListViewAdapter.getItem(position);
                Integer scriptIndex = ScriptRepository.getInstance().getParsedScriptIdAsTitle( scriptTitle );
                if( scriptIndex < 0 ) {
                    continue;
                }
                selectedItems.add( scriptIndex );
            }
        }
        // TODO single choice.
        Integer scriptId = selectedItems.get(0);

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
                mView.onFailAddQuizFolder(  "새 퀴즈폴더 생성에 실패했습니다. 잠시 후 다시 시도해주세요." );
                mView.clearUI();
                mView.returnToQuizFolderDetailFragment();
            }
        };
    }
}
