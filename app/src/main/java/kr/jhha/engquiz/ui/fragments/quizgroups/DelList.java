package kr.jhha.engquiz.ui.fragments.quizgroups;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import kr.jhha.engquiz.R;
import kr.jhha.engquiz.ui.MainActivity;


/**
 * Created by Junyoung on 2016-06-23.
 */

public class DelList extends Fragment {

    private final String mTITLE = "Delete Play List";

    private ArrayAdapter mAdapter = null;
    private ListView mItemListView = null;

    // 확인버튼 클릭시, 커스텀 퀴즈 리스트 삭제 재확인 다이알로그
    private AlertDialog.Builder mDialog = null;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        // 아답터 생성: 기본 안드로이드 아답터 사용
        // Custom PlayList에서 받아온 제목리스트를 사용.
        int resourceID = android.R.layout.simple_list_item_multiple_choice;
        Object[] customQuizTitleAll = QuizGroupAdapter.getInstance().getPlayListTitles();
        if(customQuizTitleAll == null) {
            Log.e("TAG", "quiz titles null");
            return;
        }
        mAdapter = new ArrayAdapter(getActivity(), resourceID, customQuizTitleAll);

        // 커스텀 플레이 리스트 삭제확인 다이알로그 만들기
        initDialog();
        super.onCreate(savedInstanceState);
    }

    private void initDialog()
    {
        mDialog = new AlertDialog.Builder( getActivity() );
        mDialog.setIcon(android.R.drawable.alert_dark_frame);
        mDialog.setTitle("퀴즈를 삭제하시겠습니까?");
        mDialog.setCancelable(false); //  Back키 눌렀을 경우 Dialog Cancle 여부 설정
        // 확인 버튼 클릭 이벤트.
        mDialog.setPositiveButton("OK", mDialogListner_ClickBtnOk);
        // 취소 버튼 클릭 이벤트.
        mDialog.setNegativeButton("Cancel", mDialogListner_ClickBtnCancel);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View  view = inflater.inflate(R.layout.content_playlist_del, container, false);

        // 1. 플레이리스트 삭제완료 버튼: 클릭 이벤트 핸들러 정의
        view.findViewById(R.id.del_playlist_complate_btn).setOnClickListener(mClickListener);

        // 2. 플레이 리스트뷰
        mItemListView = (ListView) view.findViewById(R.id.del_playlist_listview);
        // 플레이 리스트 아답터 연결
        mItemListView.setAdapter(mAdapter);

        // 3. 삭제할 리스트가 없는경우 처리
        handlingEmptyPlayList(view);

        view.bringToFront(); // 리스트가 길어질 경우 가장 위로 스크롤.
        return view;
    }

    @Override
    public void onResume() {
        // 툴바에 현 프래그먼트 제목 출력
        ((MainActivity)getActivity()).setActionBarTitle( mTITLE );
        super.onResume();
    }

    Button.OnClickListener mClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch(v.getId())
            {
                case R.id.del_playlist_complate_btn:
                    // 체크 다이알로그 띄우기
                    mDialog.show();
                    break;
            }
        }
    };

    // 다이알로그 버튼 클릭 이벤트 OK
    DialogInterface.OnClickListener mDialogListner_ClickBtnOk = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface d, int which) {
            // 새 리스트 추가
            Boolean bOK = deletePlayList();
            if( bOK  )
                addPlayListAfterProcess();
        }
    };

    // 다이알로그 버튼 클릭 이벤트 CANCEL
    DialogInterface.OnClickListener mDialogListner_ClickBtnCancel = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface d, int which) {
            // 다이알로그 닫기
            d.dismiss();
        }
    };

    private void handlingEmptyPlayList(View view)
    {
        if ( true == isEmptyPlayList() ) {
            TextView textview = (TextView) view.findViewById(R.id.del_playlist_howtouse);
            textview.setText("삭제할 리스트가 없습니다.");
            Button btn = (Button) view.findViewById(R.id.del_playlist_complate_btn);
            btn.setText("OK");
        }
    }

    private Boolean deletePlayList()
    {
        if( true == isEmptyPlayList() || true == isNoSelectedList() ) {
            return false;
        }

        SparseBooleanArray checkedItems = mItemListView.getCheckedItemPositions();
        int playListCount = QuizGroupAdapter.getInstance().getCount();
        int startIndex = playListCount - 1;
        for (int i=startIndex; i >= 0; --i)
        {
            boolean bSelected = checkedItems.get(i);
            if( false == bSelected )
                continue;

            boolean bDeleted = QuizGroupAdapter.getInstance().deleteQuizGroup( getActivity(), i );
            if( false == bDeleted ) {
                 Log.e("Tag", "Failed Deleting customPlaylist. Idx:" + i);
                return false;
            }
        }
        Toast.makeText(getActivity(), "새 퀴즈가 삭제되었습니다", Toast.LENGTH_SHORT).show();

        return true;
    }

    private boolean isNoSelectedList ()
    {
        int selectedListCount = mItemListView.getCheckedItemCount();
        return (selectedListCount <= 0);
    }

    private boolean isEmptyPlayList()
    {
        int playListCount = QuizGroupAdapter.getInstance().getCount();
        return (playListCount <= 0);
    }

    private void addPlayListAfterProcess()
    {
        // mAdapter.notifyDataSetChanged();
        QuizGroupAdapter.getInstance().notifyDataSetChanged(); // playlist

        // 체크된 리스트 UI 초기화.
        refreshUI();

        // 이전화면(내 퀴즈 리스트)을 보여준다.
        showPreviousView();
    }

    private void refreshUI()
    {
        // 모든 선택 상태 초기화.
        mItemListView.clearChoices() ;
    }

    // 이전 화면을 보여준다.
    private void showPreviousView()
    {
        // 프래그먼트로 이루어진 View들의 스택에서, 최상위인 현재 view를 삭제하면, 바로 전단계 view가 보임.
        // 이게 작동하려면, 화면 전환시, transaction.addToBackStack() 해줘야 함.
        getActivity().getSupportFragmentManager().popBackStack();
    }

}
