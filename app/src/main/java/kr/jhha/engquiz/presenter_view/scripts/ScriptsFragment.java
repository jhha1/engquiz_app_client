package kr.jhha.engquiz.presenter_view.scripts;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import kr.jhha.engquiz.R;
import kr.jhha.engquiz.model.local.Script;
import kr.jhha.engquiz.model.local.ScriptRepository;
import kr.jhha.engquiz.presenter_view.FragmentHandler;
import kr.jhha.engquiz.presenter_view.MyToolbar;
import kr.jhha.engquiz.presenter_view.scripts.ScriptsAdapter.ScriptSummary;
import kr.jhha.engquiz.presenter_view.sentences.SentenceFragment;
import kr.jhha.engquiz.util.StringHelper;
import kr.jhha.engquiz.util.ui.MyDialog;

import static kr.jhha.engquiz.presenter_view.FragmentHandler.EFRAGMENT.ADD_SCRIPT;
import static kr.jhha.engquiz.presenter_view.FragmentHandler.EFRAGMENT.SHOW_SCRIPTS;
import static kr.jhha.engquiz.presenter_view.FragmentHandler.EFRAGMENT.SHOW_SENTENCES_IN_SCRIPT;

/**
 * Created by jhha on 2016-12-16.
 */

public class ScriptsFragment extends Fragment implements ScriptsContract.View
{
    private ScriptsContract.ActionsListener mActionListener;
    private ScriptsAdapter mAdapter;

    // 리스트뷰UI
    private ListView mItemListView;
    // 리스트뷰에서 클릭한 아이템. 흐름이 중간에 끊겨서 어떤 아이템 클릭했는지 알려고 클래스변수로 저장해 둠.
    private ScriptSummary mListviewSelectedItem = null;
    private Integer mQuizFolderId = -1;

    public ScriptsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mActionListener = new ScriptsPresenter( this, ScriptRepository.getInstance() );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setUpToolBar();
        View view = inflater.inflate(R.layout.content_quizfolder, null);
        mItemListView = (ListView) view.findViewById(R.id.quizfolderview);
        // 클릭 이벤트 핸들러 정의: 원클릭- 문장들 보기
        mItemListView.setOnItemClickListener(mListItemClickListener);
        // 롱 클릭 이벤트 핸들러 정의: 스크립트 삭제
        mItemListView.setOnItemLongClickListener(mListItemLongClickListener);

        // 데이터 초기화
        mActionListener.getScripts();
        view.bringToFront(); // 리스트가 길어질 경우 가장 위로 스크롤.
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void setUpToolBar(){
        MyToolbar.getInstance().setToolBar(SHOW_SCRIPTS);
    }

    @Override
    public void onSuccessGetScrpits(List<Integer> parsedScriptIds, List<Integer> userMadeScriptIds, List<String> notAddedPDFScrpitIds) {
        mAdapter = new ScriptsAdapter( getActivity(), ScriptRepository.getInstance(),
                parsedScriptIds, userMadeScriptIds, notAddedPDFScrpitIds);
        mItemListView.setAdapter(mAdapter);
        // 데이터 변경에 대한 ui 리프레시 요청
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onFailGetScripts() {
        Toast.makeText(getActivity(),
                getString(R.string.show_scripts__fail_get_scripts),
                Toast.LENGTH_SHORT).show();
    }

    // 리스트뷰 클릭 이벤트 리스너: 문장들 보기
    private AdapterView.OnItemClickListener mListItemClickListener
                                        = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView parent, View v, int position, long id) {

            ScriptSummary item = (ScriptSummary) parent.getItemAtPosition(position) ;
            mListviewSelectedItem = item;
            // 한번 클릭 (리스트뷰 아이템) : 스크립트 상세보기
            mActionListener.listViewItemClicked( item);
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
            mActionListener.listViewItemLongClicked( item );
            return true;
        }
    };

    @Override
    public void showAddScriptConfirmDialog(final String filename,
                                            Float fileSize,
                                            final ScriptsAdapter.ScriptSummary item)
    {
        // 스크립트 추가 다이알로그 띄우기
        // 파일 사이즈가 이상할경우, UI에는 0.4MB로 표시 (업로드 + 다운로드)
        fileSize = (fileSize <= 0) ? 0.4f : fileSize;
        String dialogMsg = (StringHelper.isNull(filename) ? "스크립트" : filename)
                + "\n\n를 추가합니다. "
                + "\n\n스크립트 분석을 위한 서버로의 파일 업/다운로드 과정에서  "
                + fileSize + "MB의 데이터가 소모될 수 있으니, "
                + "WIFI 환경에서 이용하시길 권장드려요."
                + "\n\n스크립트를 추가하시겠어요?";

        final MyDialog dialog = new MyDialog(getActivity());
        dialog.setTitle("스크립트 추가");
        dialog.setMessage( dialogMsg );
        dialog.setPositiveButton( new View.OnClickListener() {
            public void onClick(View arg0) {
                mActionListener.addScript(filename, item);
                dialog.dismiss();
            }});
        dialog.setNegativeButton();
        dialog.showUp();
    }

    // 다이알로그
    // 동글뱅이 로딩 중(스크립트 추가중.. ) 다이알로그. 서버통신때씀
    private ProgressDialog mDialogLoadingSpinner = null;
    @Override
    public void showLoadingDialog() {
        mDialogLoadingSpinner = MyDialog.createLoadingDialog(getActivity());
        mDialogLoadingSpinner.show();
    }

    @Override
    public void closeLoadingDialog() {
        mDialogLoadingSpinner.dismiss(); // 로딩스피너 다이알로그 닫기
    }

    @Override
    public void showAddScriptSuccessDialog( ScriptsAdapter.ScriptSummary item, Script newScript )
    {
        // 데이터 변경에 대한 ui 리프레시 요청
        mAdapter.addItem( item, newScript );
        mAdapter.notifyDataSetChanged();

        MyDialog dialog = new MyDialog(getActivity());
        dialog.setTitle(getString(R.string.add_pdf_script__succ_dialog_title));
        dialog.setMessage("스크립트가 추가되었습니다. ");
        dialog.setPositiveButton();
        dialog.showUp();
    }

    @Override
    public void onShowDeleteDialog(final ScriptsAdapter.ScriptSummary item) {
        final MyDialog dialog = new MyDialog(getActivity());
        dialog.setTitle(getString(R.string.del_script_from_folder__title));
        dialog.setMessage( item.scriptTitle + getString(R.string.common__del_comfirm) );
        dialog.setPositiveButton( new View.OnClickListener() {
            public void onClick(View arg0)
            {
                // 스크립트삭제
                mActionListener.deleteScript(item);
                dialog.dismiss();
            }});
        dialog.setNegativeButton();
        dialog.showUp();
    }

    @Override
    public void onSuccessDelScript(ScriptsAdapter.ScriptSummary item) {
        // 데이터 변경에 대한 ui 리프레시 요청
        mAdapter.delItem( item );
        mAdapter.notifyDataSetChanged();
        Toast.makeText(getActivity(),
                getString(R.string.del_script__succ),
                Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onFailDelScript(int msgId ) {
        Toast.makeText(getActivity(), getString(msgId), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onShow_ParseScrpitFragment(){
        final FragmentHandler fragmentHandler = FragmentHandler.getInstance();
        final FragmentHandler.EFRAGMENT fragmentID = ADD_SCRIPT;

        fragmentHandler.changeViewFragment( fragmentID );
    }

    @Override
    public void onShow_ShowSentencesFragment(Integer scriptId, String scriptTitle ) {
        final FragmentHandler fragmentHandler = FragmentHandler.getInstance();
        final FragmentHandler.EFRAGMENT fragmentID = SHOW_SENTENCES_IN_SCRIPT;

        // 스크립트 문장 디테일보기 프래그먼트는 인자값을 넘겨야함.
        SentenceFragment fragment = (SentenceFragment) fragmentHandler.getFragment(fragmentID);
        fragment.setScriptInfo(scriptId, scriptTitle);
        fragmentHandler.changeViewFragment( fragmentID );
    }

    @Override
    public void showErrorDialog(int what) {
        int msgId =  R.string.add_pdf_script__fail;
        switch (what) {
            case 1:
                msgId = R.string.add_pdf_script__fail_only_allow_pdf_format;
                break;
            case 2:
                msgId = R.string.add_pdf_script__fail;
                break;
            case 3:
                msgId = R.string.show_folders__get_folders__fail_defaut;
                break;
            case 4:
                msgId = R.string.add_pdf_script__fail_only_has_en_or_kn;
                break;
            case 5:
                msgId = R.string.add_pdf_script__fail_add_script_into_folder;
            case 6:
                msgId = R.string.add_pdf_script__fail_add_folder;
                break;
        }

        showErrorDialog(getString(msgId));
    }

    public void showErrorDialog(String msg){
        MyDialog dialog = new MyDialog(getActivity());
        dialog.setTitle(getString(R.string.common__warning));
        dialog.setMessage( msg );
        dialog.setPositiveButton();
        dialog.showUp();
    }
}

