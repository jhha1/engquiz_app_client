package kr.jhha.engquiz.quizfolder.scripts;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import kr.jhha.engquiz.MainActivity;
import kr.jhha.engquiz.R;
import kr.jhha.engquiz.data.local.QuizFolderRepository;
import kr.jhha.engquiz.data.local.ScriptRepository;

/**
 * Created by Junyoung on 2016-06-23.
 */

public class AddQuizFolderScriptFragment extends Fragment implements  AddQuizFolderScriptContract.View {

    private AddQuizFolderScriptContract.ActionsListener mActionListener;
    private ArrayAdapter mScriptListViewAdapter;
    private ListView mItemListView = null;

    private Integer mQuizFolderId = -1;
    private String mSelectedScriptTitle;
    private Button mButtonConfirmTitle;

    private final String mTITLE = "Add a Script in a Quiz Folder";

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mActionListener = new AddQuizFolderScriptPresenter( this, QuizFolderRepository.getInstance() );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.content_quizfolder_add, container, false);

        TextView textView = (TextView) view.findViewById(R.id.add_quizfolder_howtouse);
        textView.setText("추가할 스크립트를 선택하세요.");

        // 플레이리스트 제목입력/추가완료 버튼: 클릭 이벤트 핸들러 정의
        mButtonConfirmTitle = (Button) view.findViewById(R.id.add_quizfolder_complate_btn);
        mButtonConfirmTitle.setOnClickListener(mClickListener);
        view.findViewById(R.id.add_quizfolder_complate_btn).setOnClickListener(mClickListener);

        // 플레이 리스트뷰
        mItemListView = (ListView) view.findViewById(R.id.add_quizfolder_listview);
        mItemListView.setOnItemClickListener(mListItemClickListener);

        // 퀴즈폴더에 추가할 파싱된스크립트 리스트 가져오기
        mActionListener.initScriptList();

        view.bringToFront(); // 리스트가 길어질 경우 가장 위로 스크롤.
        return view;
    }

    @Override
    public void onResume() {
        // 툴바에 현 프래그먼트 제목 출력
        ((MainActivity)getActivity()).setActionBarTitle( mTITLE );
        super.onResume();
    }

    @Override
    public void showScriptList(String[] scriptTitleAll) {
        // 플레이 리스트 아답터 연결
        int resourceID = R.layout.content_textstyle_listview_checked_multiple;
        //int resourceID = android.R.layout.simple_list_item_multiple_choice;
        mScriptListViewAdapter = new ArrayAdapter<String>(getActivity(), resourceID, scriptTitleAll);
        mItemListView.setAdapter(mScriptListViewAdapter);
        mScriptListViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void showEmptyScriptDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder( getActivity() );
        dialog.setIcon(android.R.drawable.alert_dark_frame);
        dialog.setTitle("추가할 스크립트가 없습니다.");
        dialog.setCancelable(false); //  Back키 눌렀을 경우 Dialog Cancle 여부 설정
        // 버튼 클릭 이벤트.
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface d, int which) {
                // 다이알로그와 이 프레그먼트를 닫고, quiz folder fragment로 돌아간다.
                d.dismiss();
                mActionListener.emptyScriptDialogOkButtonClicked();
            }
        });
        dialog.show();
    }

    // 리스트뷰 클릭 이벤트 리스너
    private AdapterView.OnItemClickListener mListItemClickListener
            = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView parent, View v, int position, long id) {
            mSelectedScriptTitle = (String) mScriptListViewAdapter.getItem(position);
        }
    };

    // 스크립트 추가 버튼이벤트 리스너
    Button.OnClickListener mClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch(v.getId())
            {
                case R.id.add_quizfolder_complate_btn:
                    mActionListener.addScriptIntoQuizFolder( mQuizFolderId, mSelectedScriptTitle );
                    break;
            }
        }
    };

    @Override
    public void onSuccessAddScriptInQuizFolder(List<Integer> updatedScriptIds ) {
        //  퀴즈폴더 리스트 리프레쉬.
        // TODO fragment전환시에 onResume()에서라던가 리프레쉬가 되면, 여기서 할 필요 없음
        QuizFolderScriptsAdapter quizFolderAdapter
                = new QuizFolderScriptsAdapter( ScriptRepository.getInstance(), mQuizFolderId, updatedScriptIds );
        quizFolderAdapter.notifyDataSetChanged();

        Toast.makeText(getActivity(), "새 퀴즈가 추가되었습니다", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFailAddQuizFolder( String msg ) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    /*
        UI 초기화
        OnCreateView() 에서 초기화 시도했으나, 적용이 안되어,, 완료버튼이벤트에 삽입.
     */
    public void clearUI(){
        // 모든 선택 상태 초기화.
        mItemListView.clearChoices() ;
    }

    /*
        quiz folder Fragment로 돌아가기.
     */
    public void returnToQuizFolderDetailFragment(){
        // Fragment 전환.
        // 프래그먼트로 이루어진 View들의 스택에서, 최상위인 현재 view를 삭제하면, 바로 전단계 view가 보임.
        // 이게 작동하려면, 화면 전환시, transaction.addToBackStack() 해줘야 함.
        getActivity().getSupportFragmentManager().popBackStack();
    }

    public void setSelectedQuizGroupId(Integer quizFolderId){
        mQuizFolderId = quizFolderId;
    }
}
