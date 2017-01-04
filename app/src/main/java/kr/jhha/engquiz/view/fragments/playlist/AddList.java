package kr.jhha.engquiz.view.fragments.playlist;


import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import kr.jhha.engquiz.R;
import kr.jhha.engquiz.controller.QuizManager;
import kr.jhha.engquiz.view.MainActivity;

/**
 * Created by Junyoung on 2016-06-23.
 */

public class AddList extends Fragment {

    private final String mTITLE = "Add Play List";

    private ArrayAdapter mAdapter = null;
    private ListView mItemListView = null;
    private EditText mPlayListSubject;
    private Button mButtonSetTitle;

    // 확인버튼 클릭시, 커스텀 퀴즈 리스트 추가 재확인 다이알로그
    private AlertDialog.Builder mDialog = null;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        // 아답터 생성: 기본 안드로이드 아답터 사용
        // 스크립트 전체 제목리스트를 사용.
        int resourceID = R.layout.content_textstyle_listview_checked_multiple;
        //int resourceID = android.R.layout.simple_list_item_multiple_choice;
        Object[] quizTitleAll = QuizManager.getInstance().getQuizTitleAll();
        if(quizTitleAll == null) {
            Log.e("TAG", "quiz titles null");
            return;
        }
        mAdapter = new ArrayAdapter(getActivity(), resourceID, quizTitleAll);

        // 커스텀 플레이 리스트 추가확인 다이알로그 만들기
        initDialog();
        super.onCreate(savedInstanceState);
    }

    private void initDialog()
    {
        mDialog = new AlertDialog.Builder( getActivity() );
        mDialog.setIcon(android.R.drawable.alert_dark_frame);
        mDialog.setTitle("새 퀴즈를 추가하시겠습니까?");
        mDialog.setCancelable(false); //  Back키 눌렀을 경우 Dialog Cancle 여부 설정
        // 확인 버튼 클릭 이벤트.
        mDialog.setPositiveButton("OK", mDialogListner_ClickBtnOk);
        // 취소 버튼 클릭 이벤트.
        mDialog.setNegativeButton("Cancel", mDialogListner_ClickBtnCancel);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.content_playlist_add, container, false);

        // 1. 플레이리스트 제목 입력창
        mPlayListSubject = (EditText) view.findViewById (R.id.add_playlist_subject);
        // 포커스를 제목입력창으로 이동
        mPlayListSubject.requestFocus();

        // 2. 플레이리스트 제목입력/추가완료 버튼: 클릭 이벤트 핸들러 정의
        mButtonSetTitle = (Button) view.findViewById(R.id.add_playlist_set_title_btn);
        mButtonSetTitle.setOnClickListener(mClickListener);
        view.findViewById(R.id.add_playlist_complate_btn).setOnClickListener(mClickListener);

        // 3. 플레이 리스트뷰
        mItemListView = (ListView) view.findViewById(R.id.add_playlist_listview);
        // 플레이 리스트 아답터 연결
        mItemListView.setAdapter(mAdapter);
        view.bringToFront(); // 리스트가 길어질 경우 가장 위로 스크롤.

        return view;
    }

    @Override
    public void onResume() {
        // 툴바에 현 프래그먼트 제목 출력
        ((MainActivity)getActivity()).setActionBarTitle( mTITLE );
        super.onResume();
    }

    // 제목입력완료, 새 퀴즈 만들기 버튼
    Button.OnClickListener mClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch(v.getId())
            {
                case R.id.add_playlist_set_title_btn:
                    Boolean bOK = checkTitle();
                    if( bOK == false )
                        return;

                    //키보드 숨기기:
                    // checkTitle()에 넣으면, 완료버튼 누르면 키보드 나옴
                    // -> 완료버튼에서 checkTitle()쓰기때문에 다시 키보드 토글.
                    hideKeyboard();

                    break;

                case R.id.add_playlist_complate_btn:
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
            Boolean bOK = addPlayList();
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

    private void hideKeyboard()
    {
        InputMethodManager immhide = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
        immhide.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    private  Boolean checkTitle()
    {
        String playlistTitle = mPlayListSubject.getText().toString();
        if(playlistTitle.isEmpty()) {
            mPlayListSubject.requestFocus(); // 커서를 제목입력칸으로 이동
            Toast.makeText(getActivity(), "제목을 입력해주세요", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }



    private Boolean addPlayList()
    {
        Boolean bOK = checkTitle();
        if( bOK == false )
            return false;

        // 선택한 스크립트 개수 체크
        int selectedCount = mItemListView.getCheckedItemCount();
        if( 0 >= selectedCount ) {
            Toast.makeText(getActivity(), "선택된 퀴즈가 없습니다", Toast.LENGTH_SHORT).show();
            return false;
        }

        // 플레이 리스트뷰(PlayList)에 새 플레이리스트를 추가
        //  -> 플레이 리스트 커스텀 아답터에 새 아이템 추가.
        int resourceID = R.drawable.ic_format_align_left_grey600_48dp;
        Drawable img = ContextCompat.getDrawable( getActivity(), resourceID );
        String playlistTitle = mPlayListSubject.getText().toString();
        PlayListAdapter.getInstance().addItem( img, playlistTitle,  selectedCount + "개 리스트" );

        // TODO
        // 플레이리스트에 스크립트 넘버링
        // 넘버로 실제 스크립트 데이터 가져오기

        Toast.makeText(getActivity(), "새 퀴즈가 추가되었습니다", Toast.LENGTH_SHORT).show();
        return true;
    }

    private void addPlayListAfterProcess()
    {
        PlayListAdapter.getInstance().notifyDataSetChanged();

        // 입력한 제목과, 체크된 리스트 UI 초기화.
        // OnCreateView() 에서 초기화 시도했으나, 적용이 안되어,, 완료버튼이벤트에 삽입.
        refreshUI();

        // 이전화면(내 퀴즈 리스트)을 보여준다.
        showPreviousView();
    }

    private void refreshUI()
    {
        // 입력한 제목 초기화.
        mPlayListSubject.setText(null);
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
