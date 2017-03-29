package kr.jhha.engquiz.quizfolder;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import kr.jhha.engquiz.R;
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

    private final String mTITLE = "Add Quiz Group";

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

        // 2. 플레이리스트 제목입력/추가완료 버튼: 클릭 이벤트 핸들러 정의
        mButtonConfirmTitle = (Button) view.findViewById(R.id.add_quizfolder_set_title_btn);
        mButtonConfirmTitle.setOnClickListener(mClickListener);
        view.findViewById(R.id.add_quizfolder_complate_btn).setOnClickListener(mClickListener);

        // 3. 플레이 리스트뷰
        mItemListView = (ListView) view.findViewById(R.id.add_quizfolder_listview);
        // 플레이 리스트 아답터 연결
        ArrayAdapter adapter = mActionListener.getAdapter();
        mItemListView.setAdapter(adapter);

        showQuizFolderTitleDialog();

        view.bringToFront(); // 리스트가 길어질 경우 가장 위로 스크롤.
        return view;
    }

    @Override
    public void onResume() {
        // 툴바에 현 프래그먼트 제목 출력
        ((MainActivity)getActivity()).setActionBarTitle( mTITLE );
        super.onResume();
    }

    // 퀴즈폴더 제목 입력 다이알로그
    private void showQuizFolderTitleDialog(){
        mDialogAddQuizFolder.setPositiveButton( "OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface d, int which) {
                // 제목입력완료 버튼
                String title = mInputTitleQuizFolder.getText().toString();
                Integer nextAction = mActionListener.newQuizFolderTitleInputted( title );
                switch ( nextAction ){
                    case 0:
                        d.dismiss();
                    case 1:
                        mInputTitleQuizFolder.requestFocus(); // 커서를 제목입력칸으로 이동
                        Toast.makeText(getActivity(), "제목을 다시 입력해주세요", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
        mDialogQuizFolderTitleInput.show();
    }

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
    public void showEmptyScriptDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder( getActivity() );
        dialog.setIcon(android.R.drawable.alert_dark_frame);
        dialog.setTitle("퀴즈폴더를 만들기 실패");
        dialog.setMessage("스크립트가 없어서 퀴즈폴더를 만들 수 없습니다." +
                "\n'스크립트 추가' 메뉴에서 스크립트를 추가하세요.");
        dialog.setCancelable(false); //  Back키 눌렀을 경우 Dialog Cancle 여부 설정
        // 취소 버튼 클릭 이벤트.
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface d, int which) {
                d.dismiss();
            }
        });
    }


    @Override
    public void onSuccessAddQuizFolder() {
        Toast.makeText(getActivity(), "새 퀴즈가 추가되었습니다", Toast.LENGTH_SHORT).show();

        clearUI();
        returnToQuizFolderFragment();
    }

    @Override
    public void onFailAddQuizFolder( int nextAction, String msg ) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();

        switch ( nextAction ){
            case 0:
                // nothing
                break;
            case 1:
                clearUI();
                returnToQuizFolderFragment();
                break;
        }
    }

    /*
        UI 초기화
        OnCreateView() 에서 초기화 시도했으나, 적용이 안되어,, 완료버튼이벤트에 삽입.
     */
    private void clearUI(){
        // 입력한 제목 초기화.
        mInputTitleQuizFolder.setText(null);
        // 모든 선택 상태 초기화.
        mItemListView.clearChoices() ;
    }

    private void returnToQuizFolderFragment(){
        //  퀴즈폴더 리스트 리프레쉬.
        // TODO fragment전환시에 onResume()에서라던가 리프레쉬가 되면, 여기서 할 필요 없음
        QuizFolderAdapter quizFolderAdapter = new QuizFolderAdapter( QuizFolderRepository.getInstance() );
        quizFolderAdapter.notifyDataSetChanged();

        // Fragment 전환.
        // 프래그먼트로 이루어진 View들의 스택에서, 최상위인 현재 view를 삭제하면, 바로 전단계 view가 보임.
        // 이게 작동하려면, 화면 전환시, transaction.addToBackStack() 해줘야 함.
        getActivity().getSupportFragmentManager().popBackStack();
    }
}
