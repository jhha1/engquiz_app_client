package kr.jhha.engquiz.quizfolder.scripts;

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

import java.util.List;

import kr.jhha.engquiz.MainActivity;
import kr.jhha.engquiz.R;
import kr.jhha.engquiz.data.local.QuizFolderRepository;
import kr.jhha.engquiz.data.local.ScriptRepository;
import kr.jhha.engquiz.quizfolder.scripts.QuizFolderScriptsAdapter.ScriptSummary;
import kr.jhha.engquiz.quizfolder.scripts.sentences.ShowSentenceFragment;
import kr.jhha.engquiz.util.StringHelper;

/**
 * Created by jhha on 2016-12-16.
 */

public class ShowQuizFolderScriptsFragment extends Fragment implements ShowQuizFolderScriptsContract.View
{
    private ShowQuizFolderScriptsContract.ActionsListener mActionListener;
    private QuizFolderScriptsAdapter mAdapter;

    // 다이알로그
    private AlertDialog.Builder mDialogDeleteItem = null;

    // 리스트뷰UI
    private ListView mItemListView;
    // 리스트뷰에서 클릭한 아이템. 흐름이 중간에 끊겨서 어떤 아이템 클릭했는지 알려고 클래스변수로 저장해 둠.
    private ScriptSummary mListviewSelectedItem = null;
    private Integer mQuizFolderId = -1;

    private String mTITLE = "Quiz ScriptSummary Folders";

    public ShowQuizFolderScriptsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mActionListener = new ShowQuizFolderScriptsPresenter( this, QuizFolderRepository.getInstance() );
        initDialog();
    }

    private void initDialog()
    {
        mDialogDeleteItem = new AlertDialog.Builder( getActivity() );
        mDialogDeleteItem.setTitle("스크립트 삭제");
        mDialogDeleteItem.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface d, int which) {
                d.dismiss();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

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

            // 아이템 삭제 확인 다이알로그 띄우기
            String msg = item.scriptTitle + " 을 삭제하시겠습니까?";
            mDialogDeleteItem.setMessage( msg );
            mDialogDeleteItem.setPositiveButton( "OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface d, int which) {
                    // 스크립트삭제
                    mActionListener.delQuizFolderScript( item );
                }
            });
            mDialogDeleteItem.show();
            return true;
        }
    };

    @Override
    public void onSuccessGetScrpits(List<Integer> quizFolderScriptIds) {
        mAdapter = new QuizFolderScriptsAdapter( ScriptRepository.getInstance(), mQuizFolderId, quizFolderScriptIds);
        mItemListView.setAdapter(mAdapter);
        // 데이터 변경에 대한 ui 리프레시 요청
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onFailGetScripts() {
        String msg = "스크립트 리스트를 가져오는데 실패했습니다." +
                "\n잠시 후 다시 시도해 주세요.";
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSuccessDelScript(List<Integer> updatedQuizFolderScriptIds) {
        // 데이터 변경에 대한 ui 리프레시 요청
        mAdapter.updateItems( mQuizFolderId, updatedQuizFolderScriptIds );
        mAdapter.notifyDataSetChanged();
        Toast.makeText(getActivity(), "스크립트가 삭제되었습니다", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onFailDelScript(String msg ) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onChangeFragmetNew(){
        final MainActivity context = ((MainActivity)getActivity());
        final MainActivity.EFRAGMENT fragmentID = MainActivity.EFRAGMENT.QUIZFOLDER_SCRIPT_ADD;

        // 인자값을 넘겨야함.
        AddQuizFolderScriptFragment detailFragment = (AddQuizFolderScriptFragment) context.getFragment(fragmentID);
        detailFragment.setSelectedQuizGroupId(mQuizFolderId);
        context.changeViewFragment( fragmentID );
    }

    @Override
    public void onChangeFragmetShowSentenceList(Integer scriptId, String scriptTitle ) {
        final MainActivity context = ((MainActivity)getActivity());
        final MainActivity.EFRAGMENT fragmentID = MainActivity.EFRAGMENT.QUIZFOLDER_SENTENCE_LIST_SHOW;

        // 스크립트 문장 디테일보기 프래그먼트는 인자값을 넘겨야함.
        ShowSentenceFragment fragment = (ShowSentenceFragment) context.getFragment(fragmentID);
        fragment.setScriptInfo(scriptId, scriptTitle);
        context.changeViewFragment( fragmentID );
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

        if(false == StringHelper.isNullString(quizFolderTitle))
            mTITLE = quizFolderTitle;
    }
}

