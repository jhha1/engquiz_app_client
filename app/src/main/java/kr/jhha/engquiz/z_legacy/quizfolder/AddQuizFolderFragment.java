package kr.jhha.engquiz.z_legacy.quizfolder;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import kr.jhha.engquiz.R;
import kr.jhha.engquiz.model.local.QuizFolder;
import kr.jhha.engquiz.model.local.QuizFolderRepository;
import kr.jhha.engquiz.presenter_view.MyToolbar;
import kr.jhha.engquiz.util.ui.Etc;
import kr.jhha.engquiz.util.ui.MyDialog;

import static kr.jhha.engquiz.presenter_view.FragmentHandler.EFRAGMENT.NEW_QUIZFOLDER;
import static kr.jhha.engquiz.z_legacy.quizfolder.AddQuizFolderPresenter.ERR_EMPTY_TITLE;
import static kr.jhha.engquiz.z_legacy.quizfolder.AddQuizFolderPresenter.ERR_TITLE_MAX_LEN_OVER;

/**
 * Created by Junyoung on 2016-06-23.
 */

public class AddQuizFolderFragment extends Fragment implements  AddQuizFolderContract.View {

    private AddQuizFolderContract.ActionsListener mActionListener;
    private ListView mItemListView = null;
    private EditText mInputTitleQuizFolder;
    private TextView mGuideText;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mActionListener = new AddQuizFolderPresenter( getActivity(), this, QuizFolderRepository.getInstance() );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.content_quizfolder_add, container, false);

        setUpToolBar();

        // 1. 설명창
        mGuideText = (TextView) view.findViewById (R.id.add_quizfolder_howtouse);

        // 2. 플레이리스트 추가완료 버튼: 클릭 이벤트 핸들러 정의
       //mButtonConfirmTitle = (Button) view.findViewById(R.sentenceId.add_quizfolder_set_title_btn);
       // mButtonConfirmTitle.setOnClickListener(mClickListener);
        view.findViewById(R.id.add_quizfolder_complate_btn).setOnClickListener(mClickListener);

        // 3. 플레이 리스트뷰
        mItemListView = (ListView) view.findViewById(R.id.add_quizfolder_listview);
        mItemListView.setOnItemClickListener(mListItemClickListener);
        // 기존에 선택된 것들 삭제
        mItemListView.clearChoices();

        // 퀴즈폴더에 추가할 파싱된스크립트 리스트 가져오기
        mActionListener.initScriptList();

        view.bringToFront(); // 리스트가 길어질 경우 가장 위로 스크롤.
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void setUpToolBar(){
        MyToolbar.getInstance().setToolBar(NEW_QUIZFOLDER);
    }

    @Override
    public void setAdapter(ArrayAdapter<String> adapter) {
        mItemListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void showEmptyScriptDialog() {
        /*
        final MyDialog dialog = new MyDialog(getActivity());
        dialog.setTitle(getString(R.string.add_folder__fail_no_scripts_title));
        dialog.setMessage(getString(R.string.add_folder__fail_no_has_scripts));
        dialog.setPositiveButton( new View.OnClickListener() {
            public void onClick(View arg0)
            {
                // 다이알로그와 이 프레그먼트를 닫고, quiz folder fragment로 돌아간다.
                dialog.dismiss();
                mActionListener.emptyScriptDialogOkButtonClicked();
            }});
        dialog.showUp();
        */
        String emptyScriptgGuide = mGuideText.getText() + getString(R.string.add_folder__fail_no_has_scripts);
        mGuideText.setText(emptyScriptgGuide);
    }

    // 퀴즈폴더 제목 입력 다이알로그
    @Override
    public void showQuizFolderTitleDialog()
    {
        final MyDialog dialog = new MyDialog(getActivity());
        dialog.setTitle(getString(R.string.add_folder__title));
        dialog.setMessage(getString(R.string.add_folder__title_guide));
        mInputTitleQuizFolder = Etc.makeEditText(getActivity());
        dialog.setEditText(mInputTitleQuizFolder);
        dialog.setPositiveButton( new View.OnClickListener() {
            public void onClick(View arg0)
            {
                // 제목입력완료 버튼
                String title = mInputTitleQuizFolder.getText().toString();
                Integer nextAction = mActionListener.checkInputtedTitle( title );
                switch ( nextAction ){
                    case 0: // success
                        dialog.dismiss();
                        break;
                    case ERR_EMPTY_TITLE:
                    case ERR_TITLE_MAX_LEN_OVER:
                    default:
                        Toast.makeText(getActivity(),
                                getString(R.string.add_folder__fail_title_re_input),
                                Toast.LENGTH_SHORT).show();
                        mInputTitleQuizFolder.requestFocus(); // 커서를 제목입력칸으로 이동
                        break;
                }
            }});
        dialog.setNegativeButton();
        dialog.showUp();
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
                    String title = mInputTitleQuizFolder.getText().toString();
                    mActionListener.scriptsSelected(title, mItemListView);
                    break;
            }
        }
    };

    @Override
    public void onSuccessAddQuizFolder( List<QuizFolder> updatedQuizFolders ) {
        // 퀴즈폴더 리스트 리프레쉬는 안해도됨.
        //      :프레그먼트 popBackStack()으로 ShowQuizFolder로 되돌아갈때,
        //      OnCreateView()가 호출됨. 이때, QuizFolder Model로부터 데이터를 받아 셋팅하므로.
        //      (Model에는 현재 Add된 데이터가 저장되어있다)

        Toast.makeText(getActivity(), getString(R.string.add_folder__success), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFailAddQuizFolder( int msgID ) {
        Toast.makeText(getActivity(), getString(msgID), Toast.LENGTH_SHORT).show();
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
