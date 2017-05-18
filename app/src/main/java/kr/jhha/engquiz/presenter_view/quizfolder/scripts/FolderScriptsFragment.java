package kr.jhha.engquiz.presenter_view.quizfolder.scripts;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import kr.jhha.engquiz.presenter_view.FragmentHandler;
import kr.jhha.engquiz.R;
import kr.jhha.engquiz.model.local.QuizFolderRepository;
import kr.jhha.engquiz.model.local.ScriptRepository;
import kr.jhha.engquiz.presenter_view.MyToolbar;
import kr.jhha.engquiz.presenter_view.quizfolder.scripts.FolderScriptsAdapter.ScriptSummary;
import kr.jhha.engquiz.presenter_view.sentences.SentenceFragment;
import kr.jhha.engquiz.util.StringHelper;
import kr.jhha.engquiz.util.ui.MyDialog;

import static kr.jhha.engquiz.presenter_view.FragmentHandler.EFRAGMENT.ADD_SCRIPT_INTO_QUIZFOLDER;
import static kr.jhha.engquiz.presenter_view.FragmentHandler.EFRAGMENT.SHOW_SCRIPTS_IN_QUIZFOLDER;
import static kr.jhha.engquiz.presenter_view.FragmentHandler.EFRAGMENT.SHOW_SENTENCES_IN_SCRIPT;

/**
 * Created by jhha on 2016-12-16.
 */

public class FolderScriptsFragment extends Fragment implements FolderScriptsContract.View
{
    private FolderScriptsContract.ActionsListener mActionListener;
    private FolderScriptsAdapter mAdapter;

    // 리스트뷰UI
    private ListView mItemListView;
    // 리스트뷰에서 클릭한 아이템. 흐름이 중간에 끊겨서 어떤 아이템 클릭했는지 알려고 클래스변수로 저장해 둠.
    private ScriptSummary mListviewSelectedItem = null;
    private Integer mQuizFolderId = -1;

    public FolderScriptsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mActionListener = new FolderScriptsPresenter( this, QuizFolderRepository.getInstance() );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setUpToolBar();
        View view = inflater.inflate(R.layout.content_quizfolder, null);
        mItemListView = (ListView) view.findViewById(R.id.quizfolderview);
        // 클릭 이벤트 핸들러 정의: 원클릭-내 퀴즈 디테일 보기, 더블클릭-게임용으로 퀴즈폴더설정
        mItemListView.setOnItemClickListener(mListItemClickListener);
        // 롱 클릭 이벤트 핸들러 정의: 내 퀴즈 삭제
        mItemListView.setOnItemLongClickListener(mListItemLongClickListener);

        // 데이터 초기화
        mActionListener.getQuizFolderScripts(mQuizFolderId);
        view.bringToFront(); // 리스트가 길어질 경우 가장 위로 스크롤.
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void setUpToolBar(){
        MyToolbar.getInstance().setToolBar(SHOW_SCRIPTS_IN_QUIZFOLDER);

        // 툴바에 현 프래그먼트 제목 출력
        mActionListener.initToolbarTitle(mQuizFolderId);
    }

    @Override
    public void showTitle(String title) {
        if(StringHelper.isNull(title)){
            title = "Script Lists";
        }
        MyToolbar.getInstance().setToolbarTitle( title );
    }

    // 리스트뷰 클릭 이벤트 리스너
    private AdapterView.OnItemClickListener mListItemClickListener
                                        = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView parent, View v, int position, long id) {

            ScriptSummary item = (ScriptSummary) parent.getItemAtPosition(position) ;
            mListviewSelectedItem = item;
            // 한번 클릭 (리스트뷰 아이템) : 스크립트 상세보기
            mActionListener.listViewItemClicked( item.scriptId, item.scriptTitle );
        }
    };

    // 롱 클릭 (리스트뷰 아이템) : 스크립트 삭제 다이알로그 보이기
    private AdapterView.OnItemLongClickListener mListItemLongClickListener
            = new AdapterView.OnItemLongClickListener()
    {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id)
        {

            final ScriptSummary item = (ScriptSummary) parent.getItemAtPosition(position) ;

            final MyDialog dialog = new MyDialog(getActivity());
            dialog.setTitle(getString(R.string.del_script_from_folder__title));
            dialog.setMessage( item.scriptTitle + getString(R.string.del_script_from_folder__confirm) );
            dialog.setPositiveButton( new View.OnClickListener() {
                public void onClick(View arg0)
                {
                    // 스크립트삭제
                    boolean bDeletePermenantly = false;
                    mActionListener.detachScript( item );
                    dialog.dismiss();
                }});
            dialog.setNegativeButton();
            dialog.showUp();
            return true;
        }
    };

    @Override
    public void onSuccessGetScrpits(List<Integer> quizFolderScriptIds) {
        mAdapter = new FolderScriptsAdapter( ScriptRepository.getInstance(), mQuizFolderId, quizFolderScriptIds);
        mItemListView.setAdapter(mAdapter);
        // 데이터 변경에 대한 ui 리프레시 요청
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onFailGetScripts() {
        Toast.makeText(getActivity(),
                getString(R.string.del_script__fail_get_list),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSuccessDetachScript(List<Integer> updatedQuizFolderScriptIds) {
        // 데이터 변경에 대한 ui 리프레시 요청
        mAdapter.updateItems( mQuizFolderId, updatedQuizFolderScriptIds );
        mAdapter.notifyDataSetChanged();
        Toast.makeText(getActivity(),
                getString(R.string.del_script__from_folder_succ),
                Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onFailDetachScript(int msgId ) {
        Toast.makeText(getActivity(), getString(msgId), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onChangeFragmetNew(){
        final FragmentHandler fragmentHandler = FragmentHandler.getInstance();
        final FragmentHandler.EFRAGMENT fragmentID = ADD_SCRIPT_INTO_QUIZFOLDER;

        // 인자값을 넘겨야함.
        AddScriptIntoFolderFragment detailFragment = (AddScriptIntoFolderFragment) fragmentHandler.getFragment(fragmentID);
        detailFragment.setSelectedQuizGroupId(mQuizFolderId);
        fragmentHandler.changeViewFragment( fragmentID );
    }

    @Override
    public void onChangeFragmetShowSentenceList(Integer scriptId, String scriptTitle ) {
        final FragmentHandler fragmentHandler = FragmentHandler.getInstance();
        final FragmentHandler.EFRAGMENT fragmentID = SHOW_SENTENCES_IN_SCRIPT;

        // 스크립트 문장 디테일보기 프래그먼트는 인자값을 넘겨야함.
        SentenceFragment fragment = (SentenceFragment) fragmentHandler.getFragment(fragmentID);
        fragment.setScriptInfo(scriptId, scriptTitle);
        fragmentHandler.changeViewFragment( fragmentID );
    }

    /*
       quiz folder Fragment로 돌아가기.
    */
    private void returnToQuizFolderFragment(){
        // Fragment 전환.
        // 프래그먼트로 이루어진 View들의 스택에서, 최상위인 현재 view를 삭제하면, 바로 전단계 view가 보임.
        // 이게 작동하려면, 화면 전환시, transaction.addToBackStack() 해줘야 함.
        getActivity().getSupportFragmentManager().popBackStack();
    }

    public void setSelectedQuizGroupId(Integer quizFolderId, String quizFolderTitle){
        mQuizFolderId = quizFolderId;
    }
}

