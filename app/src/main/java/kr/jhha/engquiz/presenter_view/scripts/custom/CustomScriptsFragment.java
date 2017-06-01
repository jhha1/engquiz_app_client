package kr.jhha.engquiz.presenter_view.scripts.custom;

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
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import kr.jhha.engquiz.R;
import kr.jhha.engquiz.model.local.Script;
import kr.jhha.engquiz.model.local.ScriptRepository;
import kr.jhha.engquiz.presenter_view.FragmentHandler;
import kr.jhha.engquiz.presenter_view.MyToolbar;
import kr.jhha.engquiz.presenter_view.scripts.custom.CustomScriptsAdapter.ScriptSummary;
import kr.jhha.engquiz.presenter_view.sentences.SentenceFragment;
import kr.jhha.engquiz.util.ui.Actions;
import kr.jhha.engquiz.util.ui.MyDialog;

import static kr.jhha.engquiz.model.local.Script.STATE_ADDED_SCRIPT;
import static kr.jhha.engquiz.presenter_view.FragmentHandler.EFRAGMENT.ADD_SCRIPT_FROM_OTHER_LOCATION;
import static kr.jhha.engquiz.presenter_view.FragmentHandler.EFRAGMENT.SCRIPT_TAB;
import static kr.jhha.engquiz.presenter_view.FragmentHandler.EFRAGMENT.SENTENCES;

/**
 * Created by jhha on 2016-12-16.
 */

public class CustomScriptsFragment extends Fragment implements CustomScriptsContract.View
{
    private CustomScriptsContract.ActionsListener mActionListener;
    private CustomScriptsAdapter mAdapter;

    // 리스트뷰UI
    private ListView mItemListView;

    // long-click  option
    ArrayAdapter<String> mLoncClickOptionListAdapterType1;
    ArrayAdapter<String> mLoncClickOptionListAdapterType2;

    private MyToolbar mToolbar;

    public CustomScriptsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mActionListener = new CustomScriptsPresenter( getActivity(), this, ScriptRepository.getInstance() );

        // 액션 바
        initToolbar();

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
        // 클릭 이벤트 핸들러 정의: 원클릭- 문장들 보기
        mItemListView.setOnItemClickListener(mListItemClickListener);
        // 롱 클릭 이벤트 핸들러 정의: 스크립트 삭제
        mItemListView.setOnItemLongClickListener(mListItemLongClickListener);

        // sync 플로팅 버튼은 여기서는 필요없다. 안보이게.
        // 레이아웃을 regular script와 공유하므로 이런 작업 필요
        FloatingActionButton syncFab = (FloatingActionButton) view.findViewById(R.id.script_sync_fab);
        syncFab.setVisibility(View.GONE);

        // 데이터 초기화
        mActionListener.getScripts();
        view.bringToFront(); // 리스트가 길어질 경우 가장 위로 스크롤.
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mToolbar.updateToolBar(SCRIPT_TAB);
    }

    @Override
    public void onSuccessGetScrpits(List<Integer> userMadeScriptIds) {
        mAdapter = new CustomScriptsAdapter( getActivity(), ScriptRepository.getInstance(), userMadeScriptIds);
        mItemListView.setAdapter(mAdapter);
        // 데이터 변경에 대한 ui 리프레시 요청
        mAdapter.notifyDataSetChanged();
    }


    // 리스트뷰 클릭 이벤트 리스너: 문장들 보기
    private AdapterView.OnItemClickListener mListItemClickListener
                                        = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView parent, View v, int position, long id) {

            ScriptSummary item = (ScriptSummary) parent.getItemAtPosition(position) ;
            // 한번 클릭 (리스트뷰 아이템) : 스크립트 상세보기
            mActionListener.listViewItemClicked(item);
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
    public void onShowOptionDialog(final ScriptSummary item) {
        final MyDialog dialog = new MyDialog(getActivity());
        dialog.setTitle(getString(R.string.script_option__title));
        ListView optionListView = createOptionListView(item, dialog);
        dialog.setCustomView(optionListView, getActivity());
        dialog.setCancelable(true);
        dialog.showUp();
    }

    private ListView createOptionListView(final ScriptSummary item, final MyDialog dialog){
        ListView listView = new ListView(getActivity());
        final boolean bAddedScript = item.state.equals(STATE_ADDED_SCRIPT);
        if( bAddedScript )
            listView.setAdapter(mLoncClickOptionListAdapterType1); // for added script
        else
            listView.setAdapter(mLoncClickOptionListAdapterType2); // for none added script

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                int option = position;
                switch (option){
                    case 0:
                        // 스크립트를 퀴즈게임에 추가 or 삭제
                        if( bAddedScript )
                            mActionListener.addScriptIntoPlayList(item);
                        else
                            mActionListener.delScriptIntoPlayList(item);

                        dialog.dismiss();
                        break;
                    case 1:
                        // 스크립트삭제
                        mActionListener.deleteScript(item);
                        dialog.dismiss();
                        break;
                }
            }
        });
        return listView;
    }

    @Override
    public void onSuccessAddScriptIntoPlayList(CustomScriptsAdapter.ScriptSummary item, int msgId) {
        // 데이터 변경에 대한 ui 리프레시 요청
        mAdapter.addItemIntoQuizPlaying( item );
        mAdapter.notifyDataSetChanged();

        Toast.makeText(getActivity(),
                getString(msgId),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSuccessDelScriptIntoPlayList(CustomScriptsAdapter.ScriptSummary item, int msgId) {
        // 데이터 변경에 대한 ui 리프레시 요청
        mAdapter.removeItemFromQuizPlaying( item );
        mAdapter.notifyDataSetChanged();

        Toast.makeText(getActivity(),
                getString(msgId),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSuccessCreateScript(Script script) {
        // 데이터 변경에 대한 ui 리프레시 요청
        mAdapter.addItem( script );
        mAdapter.notifyDataSetChanged();
        Toast.makeText(getActivity(),
                getString(R.string.create_script__succ),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSuccessDelScript(ScriptSummary item) {
        // 데이터 변경에 대한 ui 리프레시 요청
        mAdapter.delItem( item );
        mAdapter.notifyDataSetChanged();
        Toast.makeText(getActivity(),
                getString(R.string.del_script__succ),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onShowSentences(Integer scriptId, String scriptTitle ) {
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

    public void showErrorDialog(String msg){
        MyDialog dialog = new MyDialog(getActivity());
        dialog.setTitle(getString(R.string.common__warning));
        dialog.setMessage( msg );
        dialog.setPositiveButton();
        dialog.showUp();
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
        mToolbar.updateToolBarOptionMenu(ADD_SCRIPT_FROM_OTHER_LOCATION, menu);
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

