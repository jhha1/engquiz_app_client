package kr.jhha.engquiz.z_legacy.quizfolder.scripts;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import kr.jhha.engquiz.R;
import kr.jhha.engquiz.model.local.QuizFolderRepository;
import kr.jhha.engquiz.model.local.ScriptRepository;
import kr.jhha.engquiz.presenter_view.MyToolbar;
import kr.jhha.engquiz.util.ui.MyDialog;

import static kr.jhha.engquiz.presenter_view.FragmentHandler.EFRAGMENT.ADD_SCRIPT_INTO_QUIZFOLDER;

/**
 * Created by Junyoung on 2016-06-23.
 */

public class AddScriptIntoFolderFragment extends Fragment implements  AddScriptIntoFolderContract.View {

    private AddScriptIntoFolderContract.ActionsListener mActionListener;

    private ListView mItemListView = null;

    private Integer mQuizFolderId = -1;
    private Button mButtonConfirmTitle;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mActionListener = new AddScriptIntoFolderPresenter( getActivity(), this, QuizFolderRepository.getInstance() );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.content_quizfolder_script_add, container, false);

        setUpToolBar();

        // 플레이리스트 제목입력/추가완료 버튼: 클릭 이벤트 핸들러 정의
        mButtonConfirmTitle = (Button) view.findViewById(R.id.add_quizfolder_script_complate_btn);
        mButtonConfirmTitle.setOnClickListener(mClickListener);

        // 플레이 리스트뷰
        mItemListView = (ListView) view.findViewById(R.id.add_quizfolder_script_listview);
        mItemListView.setOnItemClickListener(mListItemClickListener);

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
        MyToolbar.getInstance().setToolBar(ADD_SCRIPT_INTO_QUIZFOLDER);
    }

    @Override
    public void setAdapter(ArrayAdapter<String> adapter) {
        mItemListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void showEmptyScriptDialog() {
        final MyDialog dialog = new MyDialog(getActivity());
        dialog.setMessage(getString(R.string.add_script_into_folder__fail_noexist_script));
        dialog.setPositiveButton( new View.OnClickListener() {
            public void onClick(View arg0)
            {
                // 다이알로그와 이 프레그먼트를 닫고, quiz folder fragment로 돌아간다.
                dialog.dismiss();
                mActionListener.emptyScriptDialogOkButtonClicked();
            }});
        dialog.showUp();
    }

    // 리스트뷰 클릭 이벤트 리스너
    private AdapterView.OnItemClickListener mListItemClickListener
            = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView parent, View v, int position, long id) {
        }
    };

    // 스크립트 추가 버튼이벤트 리스너
    Button.OnClickListener mClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch(v.getId())
            {
                case R.id.add_quizfolder_script_complate_btn:
                    mActionListener.scriptsSelected(mQuizFolderId, mItemListView);
                    break;
            }
        }
    };

    @Override
    public void onSuccessAddScriptIntoQuizFolder(List<Integer> updatedScriptIds ) {
        //  퀴즈폴더 리스트 리프레쉬.
        // TODO fragment전환시에 onResume()에서라던가 리프레쉬가 되면, 여기서 할 필요 없음
        FolderScriptsAdapter quizFolderAdapter
                = new FolderScriptsAdapter( ScriptRepository.getInstance(), mQuizFolderId, updatedScriptIds );
        quizFolderAdapter.notifyDataSetChanged();

        Toast.makeText(getActivity(),
                getString(R.string.add_script_into_folder__succ),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFailAddScriptIntoQuizFolder(int msgId ) {
        Toast.makeText(getActivity(), getString(msgId), Toast.LENGTH_SHORT).show();
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
    public void returnToQuizFolderFragment(){
        // Fragment 전환.
        // 프래그먼트로 이루어진 View들의 스택에서, 최상위인 현재 view를 삭제하면, 바로 전단계 view가 보임.
        // 이게 작동하려면, 화면 전환시, transaction.addToBackStack() 해줘야 함.
        getActivity().getSupportFragmentManager().popBackStack();
    }

    public void setSelectedQuizGroupId(Integer quizFolderId){
        mQuizFolderId = quizFolderId;
    }
}
