package kr.jhha.engquiz.quizfolder;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import kr.jhha.engquiz.R;
import kr.jhha.engquiz.MainActivity;
import kr.jhha.engquiz.data.local.QuizFolder;
import kr.jhha.engquiz.data.local.QuizFolderRepository;
import kr.jhha.engquiz.util.click.ClickDetector;
import kr.jhha.engquiz.util.click.ListViewClickDetector;

/**
 * Created by jhha on 2016-12-16.
 */

public class ShowQuizFoldersFragment extends Fragment implements  ShowQuizFoldersContract.View, ClickDetector.Callback
{
    private ShowQuizFoldersContract.ActionsListener mActionListener;
    private QuizFolderAdapter mAdapter;

    // 다이알로그
    private AlertDialog.Builder mDialogChangePlayingQuizFolder = null;
    private AlertDialog.Builder mDialogDeleteItem = null;

    // 클릭 or 더블클릭 감지자. 안드로이드 더블클릭 감지해 알려주는 지원없어 직접 만듬.
    private ClickDetector mClickDetector = null;
    // 리스트뷰에서 클릭한 아이템. 흐름이 중간에 끊겨서 어떤 아이템 클릭했는지 알려고 클래스변수로 저장해 둠.
    private QuizFolder mListviewSelectedItem = null;

    private final String mTITLE = "Quiz Groups";

    public ShowQuizFoldersFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mActionListener = new ShowQuizFolderPresenter( this, QuizFolderRepository.getInstance() );
        mActionListener.getQuizFolderList();
        mAdapter = new QuizFolderAdapter( QuizFolderRepository.getInstance() );
        mClickDetector = new ListViewClickDetector( this );
        initDialog();
    }

    private void initDialog()
    {
        mDialogDeleteItem = new AlertDialog.Builder( getActivity() );
        mDialogDeleteItem.setTitle("내 퀴즈 삭제");
        mDialogDeleteItem.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface d, int which) {
                d.dismiss();
            }
        });

        mDialogChangePlayingQuizFolder = new AlertDialog.Builder( getActivity() );
        mDialogChangePlayingQuizFolder.setTitle("플레이 그룹 변경");
        mDialogChangePlayingQuizFolder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface d, int which) {
                d.dismiss();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.content_quizfolder, null);
        ListView itemListView = (ListView) view.findViewById(R.id.quizfolderview);
        itemListView.setAdapter( mAdapter );
        // 클릭 이벤트 핸들러 정의: 원클릭-내 퀴즈 디테일 보기, 더블클릭-게임용으로 퀴즈폴더설정
        itemListView.setOnItemClickListener(mListItemClickListener);
        // 롱 클릭 이벤트 핸들러 정의: 내 퀴즈 삭제
        itemListView.setOnItemLongClickListener(mListItemLongClickListener);

        // 데이터 변경에 대한 ui 리프레시 요청
        mAdapter.notifyDataSetChanged();

        view.bringToFront(); // 리스트가 길어질 경우 가장 위로 스크롤.
        return view;
    }

    @Override
    public void onResume() {
        // 툴바에 현 프래그먼트 제목 출력
        ((MainActivity)getActivity()).setActionBarTitle( mTITLE );
        super.onResume();
    }

    // 리스트뷰 클릭 이벤트 리스너
    private AdapterView.OnItemClickListener mListItemClickListener
                                        = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView parent, View v, int position, long id) {

            QuizFolder item = (QuizFolder) parent.getItemAtPosition(position) ;
            mListviewSelectedItem = item;
            // 클릭과 더블클릭을 구분해 그에따른 동작.
            mClickDetector.onClick( position );
        }
    };

    // 한번 클릭 (리스트뷰 아이템) : 퀴즈폴더 상세보기
    @Override
    public void onSingleClicked() {
        mActionListener.listViewItemClicked( mListviewSelectedItem );
    }

    // 더블 클릭 (리스트뷰 아이템) : 이 퀴즈폴더를 게임플레이용으로 설정
    @Override
    public void onDoubleClicked() {
        mActionListener.listViewItemDoubleClicked( mListviewSelectedItem );
    }

    // 롱 클릭 (리스트뷰 아이템) : 퀴즈폴더 삭제 다이알로그 보이기
    private AdapterView.OnItemLongClickListener mListItemLongClickListener
            = new AdapterView.OnItemLongClickListener()
    {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id)
        {

            final QuizFolder item = (QuizFolder) parent.getItemAtPosition(position) ;

            // 아이템 삭제 확인 다이알로그 띄우기
            String msg = item.getTitle() + " 을 삭제하시겠습니까?";
            mDialogDeleteItem.setMessage( msg );
            mDialogDeleteItem.setPositiveButton( "OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface d, int which) {
                    // 퀴즈폴더삭제
                    mActionListener.delQuizFolder( item );
                }
            });
            mDialogDeleteItem.show();
            return true;
        }
    };

    @Override
    public void onSuccessGetQuizFolderList() {
        mAdapter.notifyDataSetChanged();
    }
    @Override
    public void onFailGetQuizFolderList() {
        String msg = "퀴즈 폴더 리스트를 가져오는데 실패했습니다." +
                "\n잠시 후 다시 시도해 주세요.";
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSuccessDelQuizFolder() {
        mAdapter.notifyDataSetChanged();
        Toast.makeText(getActivity(), "퀴즈 폴더가 삭제되었습니다", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onFailDelQuizFolder(String msg ) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    // 플레이용 퀴즈 변경 확인 다이알로그
    public void showDialogChangePlayingQuizFolder(final QuizFolder listviewSelectedItem ) {
        String msg = listviewSelectedItem.getTitle() + " 을 게임용으로 선택하시겠습니까?";
        mDialogChangePlayingQuizFolder.setMessage( msg );
        // 플레이용 퀴즈 변경 OK 클릭 이벤트
        mDialogChangePlayingQuizFolder.setPositiveButton( "OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface d, int which) {
                mActionListener.changePlayingQuizFolder( listviewSelectedItem );
            }
        });
        mDialogChangePlayingQuizFolder.show();
    }

    @Override
    public void onSucessChangePlayingQuizFolder() {
        mAdapter.notifyDataSetChanged();
        Toast.makeText(getActivity(), "게임 플레이용 퀴즈 폴더 변경에 성공했습니다", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFailChangePlayingQuizFolder() {
        String msg = "이 퀴즈폴더를 게임플레이용으로 변경하는데 실패했습니다." +
                "\n잠시 후 다시 시도해주세요.";
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onChangeViewFragmet( MainActivity.EFRAGMENT fragment ) {
        ((MainActivity)getActivity()).changeViewFragment( fragment );
    }
}

