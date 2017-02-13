package kr.jhha.engquiz.ui.fragments.playlist;


import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import kr.jhha.engquiz.R;
import kr.jhha.engquiz.ui.MainActivity;


/**
 * Created by Junyoung on 2016-06-23.
 */

public class PlayListDetail extends Fragment {

    private final String mTITLE = "My Quiz Detail";

    private ArrayAdapter mAdapter = null;
    private ListView mItemListView = null;
    private String mSelectedItem;
    private int mSelectedItemIndex = -1;

    // 확인버튼 클릭시, 커스텀 퀴즈 리스트 삭제 재확인 다이알로그
    private AlertDialog.Builder mDeleteItemDialog = null;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        // 아답터 생성: 기본 안드로이드 아답터 사용
        //int resourceID = android.R.layout.simple_list_item_1;
        int resourceID = R.layout.content_textstyle_listview;
        String[] items = {"A", "B", "C", "D", "E", PlayList.Text_New};
        mAdapter = new ArrayAdapter(getActivity(), resourceID, items);

        // 리스트 아이템 삭제확인 다이알로그 만들기
        initDialog();
        super.onCreate(savedInstanceState);
    }

    private void initDialog()
    {
        mDeleteItemDialog = new AlertDialog.Builder( getActivity() );
        mDeleteItemDialog.setIcon(android.R.drawable.alert_dark_frame);
        mDeleteItemDialog.setTitle("스크립트를 삭제하시겠습니까?");
        mDeleteItemDialog.setCancelable(false); //  Back키 눌렀을 경우 Dialog Cancle 여부 설정
        // 확인 버튼 클릭 이벤트.
        mDeleteItemDialog.setPositiveButton("OK", mDeleteItemDialogListner_ClickBtnOk);
        // 취소 버튼 클릭 이벤트.
        mDeleteItemDialog.setNegativeButton("Cancel", mDeleteItemDialogListner_ClickBtnCancel);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View  view = inflater.inflate(R.layout.content_playlist_detail, container, false);

        // 1. 플레이 리스트뷰
        mItemListView = (ListView) view.findViewById(R.id.playlist_detail_listview);
        // 플레이 리스트 아답터 연결
        mItemListView.setAdapter(mAdapter);
        // 롱 클릭 이벤트 핸들러 정의: 내 퀴즈 삭제
        mItemListView.setOnItemLongClickListener(mListItemLongClickListener);

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

            // get item
            PlayListItem item = (PlayListItem) parent.getItemAtPosition(position) ;
            String titleStr = item.getTitle() ;
            String descStr = item.getDesc() ;
            Drawable iconDrawable = item.getIcon() ;

            // 내 커스텀 퀴즈에 스크립트 추가.
            if( PlayList.Text_New.equals(titleStr) )
                ; // activity에게 화면전환요청.
            else
                ; // nothing
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
            mSelectedItem = (String) parent.getItemAtPosition(position) ;
            // 아이템 삭제 확인 다이알로그 띄우기
            String msg = mSelectedItem + " 을 삭제하시겠습니까?";
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
                delQuizAfterProcess();
        }
    };

    // 퀴즈 삭제 다이알로그 버튼 클릭 이벤트 CANCEL
    DialogInterface.OnClickListener mDeleteItemDialogListner_ClickBtnCancel = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface d, int which) {
            // 다이알로그 닫기
            d.dismiss();
        }
    };

    private Boolean deletePlayList()
    {
        if( true == isEmptyPlayList() ) {
            return false;
        }

        Toast.makeText(getActivity(), "..가 삭제되었습니다", Toast.LENGTH_SHORT).show();
        return true;
    }

    private boolean isEmptyPlayList()
    {
        int playListCount = PlayListAdapter.getInstance().getCount();
        return (playListCount <= 0);
    }

    private void delQuizAfterProcess()
    {
        mAdapter.notifyDataSetChanged();
    }
}
