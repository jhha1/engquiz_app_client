package kr.jhha.engquiz.ui.fragments.quizgroups;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import kr.jhha.engquiz.R;
import kr.jhha.engquiz.ui.MainActivity;
import kr.jhha.engquiz.ui.fragments.QuizPlayFragment;

/**
 * Created by jhha on 2016-12-16.
 */

public class ShowQuizGroups extends Fragment
{
    private final String mTITLE = "Quiz Groups";

    // 내 퀴즈 리스트
    private ListView mItemListView = null;
    // 내 퀴즈 리스트 중, 유저가 선택한 것.
    private int mSelectedItemIndex = -1;
    // 리스트 아이템 선택시, 보여지는 레이아웃
    private LinearLayout mItemOptionLayout;

    public static final String Text_New = "New..";

    // Activity를 통해, 타 fragment에 이벤트를 던지기 위한 (fragment간 직접통신 안됨. activity를 통해야함)
    // 연결자 인터페이스.
    public interface OnPlayListButtonClickListener {
        public void onPlayListBtnClicked(View v );
    }
    private OnPlayListButtonClickListener mOnPlayListBtnClickListener;
    private AlertDialog.Builder mDeleteItemDialog = null;

    public ShowQuizGroups() {}

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        initDialog();
        super.onCreate(savedInstanceState);
    }

    private void initDialog()
    {
        mDeleteItemDialog = new AlertDialog.Builder( getActivity() );
        mDeleteItemDialog.setTitle("내 퀴즈 삭제");
        mDeleteItemDialog.setPositiveButton( "OK", mDeleteItemDialogListner_ClickBtnOk);
        mDeleteItemDialog.setNegativeButton( "CanCel", mDeleteItemDialogListner_ClickBtnCancel);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.content_playlist, null);

        // 1. 리스트 옵션 설정
        // 옵션 레이아웃. 아이템 선택 여부에 따라 보여졌다 안보여졌다 해야함.
        mItemOptionLayout = (LinearLayout) view.findViewById(R.id.playlist_selected_item_option);
        // 옵션 레이아웃 내 버튼 클릭 이벤트 핸들러 정의
        view.findViewById(R.id.playlist_show_detail_btn).setOnClickListener(mClickListener);
        view.findViewById(R.id.playlist_set_for_play_btn).setOnClickListener(mClickListener);

        // 2. 퀴즈그룹 리스트뷰
        ListView itemListView = (ListView) view.findViewById(R.id.playlistview);
        // 아답터 연결
        itemListView.setAdapter( QuizGroupAdapter.getInstance() );
        // 클릭 이벤트 핸들러 정의: 내 퀴즈 디테일 보기
        itemListView.setOnItemClickListener(mListItemClickListener);
        // 롱 클릭 이벤트 핸들러 정의: 내 퀴즈 삭제
        itemListView.setOnItemLongClickListener(mListItemLongClickListener);
        // 퀴즈그룹 리스트 ui 소팅
        QuizGroupAdapter.getInstance().sort();
        // 데이터 변경에 대한 ui 리프레시 요청
        QuizGroupAdapter.getInstance().notifyDataSetChanged();

        // 3. 아이템 선택시 나오는 옵션 창이 열려있으면 닫기
        hideOptions();

        view.bringToFront(); // 리스트가 길어질 경우 가장 위로 스크롤.
        return view;
    }

    @Override
    public void onResume() {
        // 툴바에 현 프래그먼트 제목 출력
        ((MainActivity)getActivity()).setActionBarTitle( mTITLE );
        super.onResume();
    }


    // 플레이 리스트 클릭 이벤트 리스너
    private AdapterView.OnItemClickListener mListItemClickListener
                                        = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView parent, View v, int position, long id) {

            mSelectedItemIndex = position;

            // toInt item
            QuizGroupItem item = (QuizGroupItem) parent.getItemAtPosition(position) ;
            String titleStr = item.getTitle();
            String descStr = item.getDesc();

            // quiz group detail에서 보여줘야할.. 퀴즈그룹정보 전달.
            QuizGroupDetailAdapter.getInstance().setCurrentQuizGroup( item );
            Log.d("%%%%%%%%%%%%%%%", "ShowQuizGroups.item clicked. item titleStr:" + titleStr);

            // 새 커스텀 퀴즈 리스트 만들기
            if( Text_New.equals(titleStr) )
                ((MainActivity)getActivity()).changeViewFragment( MainActivity.FRAGMENT.NEW_CUSTOM_QUIZ );  // activity에게 리스트추가 화면으로 전환요청.
            else // 옵션 선택 버튼 띄우기
                showOptions();
        }
    };

    // 플레이 리스트 롱클릭 이벤트 리스너 : 퀴즈 삭제 다이알로그 보이기
    private AdapterView.OnItemLongClickListener mListItemLongClickListener
            = new AdapterView.OnItemLongClickListener()
    {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id)
        {
            mSelectedItemIndex = position;
            QuizGroupItem item = (QuizGroupItem) parent.getItemAtPosition(position) ;
            // 아이템 삭제 확인 다이알로그 띄우기
            String msg = item.getTitle() + " 을 삭제하시겠습니까?";
            mDeleteItemDialog.setMessage( msg );
            mDeleteItemDialog.show();
            return true;
        }
    };

    // 퀴즈 삭제 다이알로그 OK 클릭 이벤트
    DialogInterface.OnClickListener mDeleteItemDialogListner_ClickBtnOk = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface d, int which) {
            // 내 퀴즈 삭제
            Boolean bOK = deletePlayList();
            if( bOK  )
                addPlayListAfterProcess();
        }
    };

    // 퀴즈 삭제 다이알로그 버튼 클릭 이벤트 CANCEL
    DialogInterface.OnClickListener mDeleteItemDialogListner_ClickBtnCancel = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface d, int which) {
            // 다이알로그 닫기
            d.dismiss();
        }
    };


    // 리스트 아이템 옵션 버튼 클릭 리스너
    private Button.OnClickListener mClickListener = new View.OnClickListener() {
        public void onClick(View v) {
        switch(v.getId()){
            case R.id.playlist_set_for_play_btn:
                // 플레이용 퀴즈로 설정
                boolean bOK = QuizPlayFragment.setQuizList( mSelectedItemIndex );
                String msg = (bOK)? "게임플레이용 퀴즈가 변경되었습니다.":"게임플레이용 퀴즈 변경에 실패했습니다.";
                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                break;

            case R.id.playlist_show_detail_btn:
                // 디테일 보기로 화면 전환
                ((MainActivity)getActivity()).changeViewFragment( MainActivity.FRAGMENT.QUIZ_DETAIL_LIST);
                // 옵션 창 닫기
                hideOptions();
                break;
        }
        }
    };

    private void showOptions( )
    {
        mItemOptionLayout.setVisibility(View.VISIBLE);
    }

    private void hideOptions()
    {
        mItemOptionLayout.setVisibility(View.INVISIBLE);
    }

    private Boolean deletePlayList()
    {
        if( true == isEmptyPlayList() ) {
            return false;
        }

        boolean bDeleted = QuizGroupAdapter.getInstance().deleteQuizGroup( getActivity(), mSelectedItemIndex );
        if( false == bDeleted ) {
            Log.e("Tag", "Failed Deleting customPlaylist. Idx:" + mSelectedItemIndex);
            return false;
        }

        Toast.makeText(getActivity(), "내 퀴즈가 삭제되었습니다", Toast.LENGTH_SHORT).show();
        return true;
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
    }

    // Activity와 통신을 위해, ShowList를 activity에 연결.
    // .. 하나의 앱은, 하나의 activity를 가진다. 하나의 activity에서 여러 화면전환을 컨트롤.
    // .. 전환되는 각 화면들은 프래그먼트로 만들어야 함.
    // .. Activity <-> fragment간 통신을 위해 아래와 같이, activity에게 showlist 프래그먼트를 연결.
    @Override
    public void onAttach( Context context ) {
        super.onAttach( context );
        this.mOnPlayListBtnClickListener = (OnPlayListButtonClickListener)context;
    }
}

