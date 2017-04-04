package kr.jhha.engquiz.quizfolder;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import kr.jhha.engquiz.R;
import kr.jhha.engquiz.data.local.QuizFolder;
import kr.jhha.engquiz.data.local.QuizFolderRepository;
import kr.jhha.engquiz.MainActivity;

/**
 * Created by Junyoung on 2016-06-23.
 */

public class AddQuizFolderFragment extends Fragment implements  AddQuizFolderContract.View {

    private AddQuizFolderContract.ActionsListener mActionListener;
    private ListView mItemListView = null;

    private EditText mInputTitleQuizFolder;
    private Button mButtonConfirmTitle;

    // 퀴즈폴더 타이틀입력 다이알로그
    private AlertDialog.Builder mDialogQuizFolderTitleInput = null;
    // 커스텀 퀴즈 리스트 추가 재확인 다이알로그
    private AlertDialog.Builder mDialogAddQuizFolder = null;

    private final String mTITLE = "Add Quiz Folder";

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mActionListener = new AddQuizFolderPresenter( getActivity(), this, QuizFolderRepository.getInstance() );

        // 커스텀 플레이 리스트 추가확인 다이알로그 만들기
        initDialog();
    }

    private void initDialog()
    {
        mDialogQuizFolderTitleInput = new AlertDialog.Builder( getActivity() );
        mDialogQuizFolderTitleInput.setIcon(android.R.drawable.alert_dark_frame);
        mDialogQuizFolderTitleInput.setTitle("새 퀴즈폴더의 제목을 입력해주세요");
        mInputTitleQuizFolder = new EditText(getActivity());
        mInputTitleQuizFolder.setInputType(InputType.TYPE_CLASS_TEXT);
        mDialogQuizFolderTitleInput.setView(mInputTitleQuizFolder);
        mDialogQuizFolderTitleInput.setCancelable(false); //  Back키 눌렀을 경우 Dialog Cancle 여부 설정
        // 취소 버튼 클릭 이벤트.
        mDialogQuizFolderTitleInput.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface d, int which) {
                d.dismiss();
            }
        });

        mDialogAddQuizFolder = new AlertDialog.Builder( getActivity() );
        mDialogAddQuizFolder.setIcon(android.R.drawable.alert_dark_frame);
        mDialogAddQuizFolder.setTitle("새 퀴즈를 추가하시겠습니까?");
        mDialogAddQuizFolder.setCancelable(false); //  Back키 눌렀을 경우 Dialog Cancle 여부 설정
        // 취소 버튼 클릭 이벤트.
        mDialogAddQuizFolder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface d, int which) {
                d.dismiss();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.content_quizfolder_add, container, false);

        // 1. 플레이리스트 제목 입력창
        //mInputTitleQuizFolder = (EditText) view.findViewById (R.id.add_quizfolder_subject);
        // 포커스를 제목입력창으로 이동
       // mInputTitleQuizFolder.requestFocus();

        // 2. 플레이리스트 추가완료 버튼: 클릭 이벤트 핸들러 정의
       //mButtonConfirmTitle = (Button) view.findViewById(R.id.add_quizfolder_set_title_btn);
       // mButtonConfirmTitle.setOnClickListener(mClickListener);
        view.findViewById(R.id.add_quizfolder_complate_btn).setOnClickListener(mClickListener);

        // 3. 플레이 리스트뷰
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
    public void setAdapter(ArrayAdapter<String> adapter) {
        mItemListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void showEmptyScriptDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder( getActivity() );
        dialog.setIcon(android.R.drawable.alert_dark_frame);
        dialog.setTitle("퀴즈폴더를 만들기 실패");
        dialog.setMessage("스크립트가 없어서 퀴즈폴더를 만들 수 없습니다." +
                "\n'스크립트 추가' 메뉴에서 스크립트를 추가하세요.");
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

    // 퀴즈폴더 제목 입력 다이알로그
    @Override
    public void showQuizFolderTitleDialog(){
        mDialogQuizFolderTitleInput.setPositiveButton( "OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface d, int which) {
                // 제목입력완료 버튼
                String title = mInputTitleQuizFolder.getText().toString();
                Integer nextAction = mActionListener.checkInputtedTitle( title );
                switch ( nextAction ){
                    case 0:
                        d.dismiss();
                        break;
                    case 1:
                        mInputTitleQuizFolder.requestFocus(); // 커서를 제목입력칸으로 이동
                        Toast.makeText(getActivity(), "제목을 다시 입력해주세요", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        mInputTitleQuizFolder.requestFocus(); // 커서를 제목입력칸으로 이동
                        Toast.makeText(getActivity(), "제목은 30자 이하여야 합니다. ", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
        mDialogQuizFolderTitleInput.show();
    }

    // 리스트뷰 클릭 이벤트 리스너
    private AdapterView.OnItemClickListener mListItemClickListener
            = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView parent, View v, int position, long id) {
           //
        }
    };

    // 새 퀴즈폴더 만들기 버튼이벤트 리스너
    Button.OnClickListener mClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch(v.getId())
            {
                case R.id.add_quizfolder_complate_btn:
                    mActionListener.scriptsSelected();
                    break;
            }
        }
    };

    @Override
    // 새 퀴즈 만들지 마지막확인 다이알로그 띄우기
    public void showAddQuizFolderConfirmDialog() {
        // 컨펌 다이알로그 띄우기
        mDialogAddQuizFolder.setPositiveButton( "OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface d, int which) {
                // 새 리스트 추가
                String title = mInputTitleQuizFolder.getText().toString();
                mActionListener.addQuizFolder( title, mItemListView );
            }
        });
        mDialogAddQuizFolder.show();
    }

    @Override
    public void onSuccessAddQuizFolder( List<QuizFolder> updatedQuizFolders ) {
        // 퀴즈폴더 리스트 리프레쉬는 안해도됨.
        //      :프레그먼트 popBackStack()으로 ShowQuizFolder로 되돌아갈때,
        //      OnCreateView()가 호출됨. 이때, QuizFolder Model로부터 데이터를 받아 셋팅하므로.
        //      (Model에는 현재 Add된 데이터가 저장되어있다)

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
        // 입력한 제목 초기화.
        mInputTitleQuizFolder.setText(null);
        // 모든 선택 상태 초기화.
        mItemListView.clearChoices() ;
    }

    /*
        quiz folder Fragment로 돌아가기.
     */
    public void returnToQuizFolderFragment(){
        // Fragment 전환.
        // 프래그먼트로 이루어진 View들의 스택에서, 최상위인 현재 view를 삭제하면, 바로 전단계 view가 보임.
        // 이게 작동하려면, 화면 전환시, transaction.addToBackStack() 해줘야 함.
        getActivity().getSupportFragmentManager().popBackStack();
    }
}
