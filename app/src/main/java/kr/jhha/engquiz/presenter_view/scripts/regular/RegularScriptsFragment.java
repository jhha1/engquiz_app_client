package kr.jhha.engquiz.presenter_view.scripts.regular;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import kr.jhha.engquiz.R;
import kr.jhha.engquiz.model.local.Script;
import kr.jhha.engquiz.model.local.ScriptRepository;
import kr.jhha.engquiz.presenter_view.FragmentHandler;
import kr.jhha.engquiz.presenter_view.MyToolbar;
import kr.jhha.engquiz.presenter_view.scripts.regular.RegularScriptsAdapter.ScriptSummary;
import kr.jhha.engquiz.presenter_view.sentences.SentenceFragment;
import kr.jhha.engquiz.presenter_view.sync.SyncDialog;
import kr.jhha.engquiz.util.StringHelper;
import kr.jhha.engquiz.util.ui.MyDialog;

import static kr.jhha.engquiz.model.local.Script.STATE_QUIZPLAYING_SCRIPT;
import static kr.jhha.engquiz.presenter_view.FragmentHandler.EFRAGMENT.ADD_SCRIPT_FROM_OTHER_LOCATION;
import static kr.jhha.engquiz.presenter_view.FragmentHandler.EFRAGMENT.SCRIPT_TAB;
import static kr.jhha.engquiz.presenter_view.FragmentHandler.EFRAGMENT.SENTENCES;
import static kr.jhha.engquiz.presenter_view.scripts.regular.RegularScriptsPresenter.DEFAUT_FILE_UPDOWN_SIZE;

/**
 * Created by jhha on 2016-12-16.
 */

public class RegularScriptsFragment extends Fragment implements RegularScriptsContract.View, SyncDialog.SyncedCallback
{
    private RegularScriptsContract.ActionsListener mActionListener;
    private RegularScriptsAdapter mAdapter;

    // 리스트뷰UI
    private ListView mItemListView;

    // long-click  option
    ArrayAdapter<String> mLoncClickOptionListAdapterType1;
    ArrayAdapter<String> mLoncClickOptionListAdapterType2;

    FloatingActionButton mSyncFabBtn;
    private MyToolbar mToolbar;

    public RegularScriptsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mActionListener = new RegularScriptsPresenter( this, ScriptRepository.getInstance() );

        // 액션 바
        initToolbar();

        // 롱클릭 액션
        String[] optionsType1 = new String[] {"퀴즈 게임에 추가", "앱에서 삭제"};
        String[] optionsType2 = new String[] {"퀴즈 게임에서 빼기", "앱에서 삭제"};
        mLoncClickOptionListAdapterType1 =
                new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, optionsType1);
        mLoncClickOptionListAdapterType2 =
                new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, optionsType2);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_scripts, null);
        mItemListView = (ListView) view.findViewById(R.id.script_list_view);
        // 클릭 이벤트 핸들러 정의: 원클릭- 문장 보기, 겜용 설정
        mItemListView.setOnItemClickListener(mListItemClickListener);
        // 롱 클릭 이벤트 핸들러 정의: 스크립트 삭제
        mItemListView.setOnItemLongClickListener(mListItemLongClickListener);

        // Sync Fab
        mSyncFabBtn = (FloatingActionButton) view.findViewById(R.id.script_sync_fab);
        mSyncFabBtn.setOnClickListener(mClickListener);

        // 데이터 초기화
        mActionListener.init();
        view.bringToFront(); // 리스트가 길어질 경우 가장 위로 스크롤.
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mToolbar.updateToolBar(SCRIPT_TAB);
    }


    /*
        script 가져오기
     */
    @Override
    public void onSuccessGetScrpits(List<Integer> parsedScriptIds, List<String> notAddedPDFScrpitIds) {
        mAdapter = new RegularScriptsAdapter( getActivity(), ScriptRepository.getInstance(),
                parsedScriptIds,  notAddedPDFScrpitIds);
        mItemListView.setAdapter(mAdapter);
        // 데이터 변경에 대한 ui 리프레시 요청
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onFailGetScripts() {
        Toast.makeText(getActivity(),
                getString(R.string.script__fail_get_scripts),
                Toast.LENGTH_SHORT).show();
    }

    // 리스트뷰 클릭 이벤트 리스너: 문장들 보기
    private AdapterView.OnItemClickListener mListItemClickListener
                                        = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView parent, View v, int position, long id) {

            ScriptSummary item = (ScriptSummary) parent.getItemAtPosition(position) ;
            // 한번 클릭 (리스트뷰 아이템) : 스크립트 상세보기
            mActionListener.listViewItemClicked( item);
        }
    };

    // 롱 클릭 (리스트뷰 아이템) : 스크립트 옵션 다이알로그 보이기
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
                                            final RegularScriptsAdapter.ScriptSummary item)
    {
        // 스크립트 추가 다이알로그 띄우기
        // 파일 사이즈가 이상할경우, UI에는 0.4MB로 표시 (업로드 + 다운로드)
        fileSize = (fileSize <= 0) ? DEFAUT_FILE_UPDOWN_SIZE : fileSize;
        String dialogMsg = (StringHelper.isNull(filename) ? "스크립트" : filename)
                + "\n\n스크립트 분석을 위한 서버로의 파일 업/다운로드 과정에서  "
                + fileSize + "MB의 데이터가 소모될 수 있으니, "
                + "WIFI 환경에서 이용하시길 권장드려요."
                + "\n\n스크립트를 추가하시겠어요?";

        final MyDialog dialog = new MyDialog(getActivity());
        dialog.setTitle("스크립트를 앱에 추가");
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
    public void showAddScriptSuccessDialog(RegularScriptsAdapter.ScriptSummary item, Script newScript )
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
    public void onShowOptionDialog(final RegularScriptsAdapter.ScriptSummary item) {
        final MyDialog dialog = new MyDialog(getActivity());
        dialog.setTitle(getString(R.string.script_option__title));
        ListView optionListView = createOptionListView(item, dialog);
        dialog.setCustomView(optionListView, getActivity());
        dialog.setCancelable(true);
        dialog.showUp();
    }

    private ListView createOptionListView(final RegularScriptsAdapter.ScriptSummary item, final MyDialog dialog){
        ListView listView = new ListView(getActivity());
        final boolean bQuizPlayingScript = item.state.equals(STATE_QUIZPLAYING_SCRIPT);
        if( bQuizPlayingScript )
            listView.setAdapter(mLoncClickOptionListAdapterType2); // for none added script
        else
            listView.setAdapter(mLoncClickOptionListAdapterType1); // for added script

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                int option = position;
                switch (option){
                    case 0:
                        // 스크립트를 퀴즈게임에 추가 or 삭제
                        if( bQuizPlayingScript )
                            mActionListener.delScriptIntoPlayList(item);
                        else
                            mActionListener.addScriptIntoPlayList(item);

                        dialog.dismiss();
                        break;
                    case 1:
                        // 스크립트를 앱에서 삭제
                        mActionListener.deleteScript(item);
                        dialog.dismiss();
                        break;
                }
            }
        });
        return listView;
    }

    @Override
    public void onSuccessAddScriptIntoPlayList(RegularScriptsAdapter.ScriptSummary item, int msgId) {
        // 데이터 변경에 대한 ui 리프레시 요청
        mAdapter.addItemIntoQuizPlaying( item );
        mAdapter.notifyDataSetChanged();

        Toast.makeText(getActivity(),
                getString(msgId),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSuccessDelScriptIntoPlayList(RegularScriptsAdapter.ScriptSummary item, int msgId) {
        // 데이터 변경에 대한 ui 리프레시 요청
        mAdapter.removeItemFromQuizPlaying( item );
        mAdapter.notifyDataSetChanged();

        Toast.makeText(getActivity(),
                getString(msgId),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSuccessDelScript(RegularScriptsAdapter.ScriptSummary item) {
        // 데이터 변경에 대한 ui 리프레시 요청
        mAdapter.delItem( item );
        mAdapter.notifyDataSetChanged();
        Toast.makeText(getActivity(),
                getString(R.string.del_script__succ),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onShow_ParseScrpitFragment(){
        final FragmentHandler fragmentHandler = FragmentHandler.getInstance();
        final FragmentHandler.EFRAGMENT fragmentID = ADD_SCRIPT_FROM_OTHER_LOCATION;

        fragmentHandler.changeViewFragment( fragmentID );
    }

    @Override
    public void onShow_ShowSentencesFragment(Integer scriptId, String scriptTitle ) {
        final FragmentHandler fragmentHandler = FragmentHandler.getInstance();
        final FragmentHandler.EFRAGMENT fragmentID = SENTENCES;

        // 스크립트 문장 디테일보기 프래그먼트는 인자값을 넘겨야함.
        SentenceFragment fragment = (SentenceFragment) fragmentHandler.getFragment(fragmentID);
        fragment.setScriptInfo(scriptId, scriptTitle);
        fragmentHandler.changeViewFragment( fragmentID );
    }

    @Override
    public void showErrorDialog(int msgId) {
        showErrorDialog(getString(msgId));
    }

    @Override
    public void showErrorDialog(String msg){
        MyDialog dialog = new MyDialog(getActivity());
        dialog.setTitle(getString(R.string.common__warning));
        dialog.setMessage( msg );
        dialog.setPositiveButton();
        dialog.showUp();
    }

    /*
        Floating Button
     */
    @Override
    public void showSyncFloatingBtn() {
        mSyncFabBtn.setVisibility(View.VISIBLE);
    }
    @Override
    public void hideSyncFloatingBtn() {
        mSyncFabBtn.setVisibility(View.GONE);
    }

    /*
        Show Sync Dialog
     */
    Button.OnClickListener mClickListener = new View.OnClickListener()
    {
        public void onClick(View v) {
            switch (v.getId())
            {
                case R.id.script_sync_fab:
                    SyncDialog dialog = new SyncDialog(getActivity());
                    dialog.show( new SyncDialog.SyncedCallback(){
                            @Override
                            public void onSynced() {
                                mSyncFabBtn.setVisibility(View.GONE);
                            }
                        });
                    break;
            }
        }
    };

    @Override
    public void onSynced() {
        hideSyncFloatingBtn();
    }


    /*
        Action Bar
       */
    private void initToolbar() {
        // 액션 바 보이기
        mToolbar = MyToolbar.getInstance();
        setHasOptionsMenu(true);
    }

    // 메뉴버튼이 처음 눌러졌을 때 실행되는 콜백메서드
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    // 화면에 보여질때 마다 호출됨
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        mToolbar.updateToolBarOptionMenu(SCRIPT_TAB, menu);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_bar__help_webview:
                mActionListener.helpBtnClicked();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

