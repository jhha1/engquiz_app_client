package kr.jhha.engquiz.presenter_view.sentences;

import android.os.Bundle;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import kr.jhha.engquiz.model.local.Sentence;
import kr.jhha.engquiz.R;
import kr.jhha.engquiz.model.local.ScriptRepository;
import kr.jhha.engquiz.presenter_view.FragmentHandler;
import kr.jhha.engquiz.presenter_view.MyToolbar;
import kr.jhha.engquiz.util.ui.MyDialog;
import kr.jhha.engquiz.util.StringHelper;

import static kr.jhha.engquiz.presenter_view.FragmentHandler.EFRAGMENT.ADD_SENTENCE;
import static kr.jhha.engquiz.presenter_view.FragmentHandler.EFRAGMENT.SENTENCES;
import static kr.jhha.engquiz.presenter_view.sentences.AddSentenceFragment.FIELD__HAS_PARENT_SCRIPT;
import static kr.jhha.engquiz.presenter_view.sentences.AddSentenceFragment.FIELD__SCRIPT_ID;

/**
 * Created by jhha on 2016-12-16.
 */

public class SentenceFragment extends Fragment implements SentenceContract.View
{
    private SentenceContract.ActionsListener mActionListener;

    private Integer mScriptId;
    private String mScriptTitle;

    /*
     수정 가능한 문장을 보여주는 뷰 관련
      - EditText ListView
      */
    private SentenceAdapter mAdapter;
    private ListView mItemListView;
    // 리스트뷰에서 클릭한 아이템. 흐름이 중간에 끊겨서 어떤 아이템 클릭했는지 알려고 클래스변수로 저장해 둠.
    private Sentence mListviewSelectedItem = null;
    MyDialog mEditSentenceDialog;          // 문장 수정 다이알로그
    EditText mEditSentence_EditTextKo; // 한글 문장 수정 칸
    EditText mEditSentence_EditTextEN; // 영어 문장 수정 칸
    // mEditSentence_CompleteKoBtn;  // 한글 문장 수정 완료 버튼
    Button mEditSentence_CompleteBtn;  // 영어 문장 수정 완료 버튼

    // long-click  option
    ArrayAdapter<String> mOptionAdapterForRegular;
    ArrayAdapter<String> mOptionAdapterForCustom;

    private MyToolbar mToolbar;

    public SentenceFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mActionListener = new SentencePresenter( this, ScriptRepository.getInstance() );

        // 문장 롱 클릭 액션
        initEachSentenceOptionDialog();

        // 툴 바
        initToolbar();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.content_sentence, null);

        // 유저가 만든 스크립트 문장을 보는경우.
        // 수정을 유저가 직접해야하므로, 각 문장의 선택이벤트 등 구별위해 리스트뷰에 뿌려준다.
        mItemListView = (ListView) view.findViewById(R.id.sentence_listview);
        // 클릭 이벤트 핸들러 정의: 원클릭-내 퀴즈 디테일 보기, 더블클릭-게임용으로 퀴즈폴더설정
        mItemListView.setOnItemClickListener(mListItemClickListener);
        // 롱 클릭 이벤트 핸들러 정의: 문장 삭제
        mItemListView.setOnItemLongClickListener(mListItemLongClickListener);

        // 데이터 초기화
        mActionListener.getSentences(mScriptId);
        view.bringToFront(); // 리스트가 길어질 경우 가장 위로 스크롤.
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateToolbar();
    }

    /*
        Script Fragment 에서 호출함.
        선택한 스크립트 정보를 넘겨줌.
        여기서는 그 정보를 기반으로 문장들을 가져온다.
     */
    public void setScriptInfo(Integer scriptId, String scriptTitle){
        mScriptId = scriptId;
        mScriptTitle = scriptTitle;
    }

    private void initEachSentenceOptionDialog(){
        String[] optionsType1 = new String[] {"문장 수정 요청하기"};
        String[] optionsType2 = new String[] {"문장 수정 하기", "스크립트에서 삭제"};
        mOptionAdapterForRegular =
                new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, optionsType1);
        mOptionAdapterForCustom =
                new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, optionsType2);
    }

    /*
        문장 리스트 가져오기
     */
    @Override
    public void onSuccessGetSentences(boolean bCustomSentences, List<Sentence> sentences) {
        mAdapter = new SentenceAdapter( bCustomSentences, sentences );
        mItemListView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onFailGetSentences(int msgID) {
        Toast.makeText(getActivity(),
                getString(msgID),
                Toast.LENGTH_SHORT).show();
    }

    // 리스트뷰 클릭 이벤트 리스너
    private AdapterView.OnItemClickListener mListItemClickListener
            = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView parent, View v, int position, long id) {

            Sentence item = (Sentence) parent.getItemAtPosition(position) ;
            mListviewSelectedItem = item;
            // 한번 클릭 (리스트뷰 아이템): 문장 추가 버튼이 아니면 아무동작 안함
            mActionListener.sentenceSingleClicked( mListviewSelectedItem );
        }
    };

    // 롱 클릭 (리스트뷰 아이템) : 문장 옵션 다이알로그 띄운다
    private AdapterView.OnItemLongClickListener mListItemLongClickListener
            = new AdapterView.OnItemLongClickListener()
    {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id)
        {
            Sentence item = (Sentence) parent.getItemAtPosition(position) ;
            mActionListener.sentenceLongClicked( item );
            return true;
        }
    };

    @Override
    public void onShowOptionDialog(final Sentence item) {
        final MyDialog dialog = new MyDialog(getActivity());
        dialog.setTitle(getString(R.string.sentence__option_title));
        ListView optionListView = createOptionListView(item, dialog);
        dialog.setCustomView(optionListView, getActivity());
        dialog.setCancelable(true);
        dialog.showUp();
    }

    private ListView createOptionListView(final Sentence item, final MyDialog dialog)
    {
        ListView listView = new ListView(getActivity());

        // 문장 타입에 따라 옵션 어댑터를 다르게.
        if( item.isMadeByUser() ){
            listView.setAdapter(mOptionAdapterForCustom);
        } else {
            listView.setAdapter(mOptionAdapterForRegular);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                int option = position;
                switch (option){
                    case 0:
                         // 문장 수정 옵션
                        if( item.isMadeByUser() ) {
                            // 문장 직접수정
                            showModifyDialog(item);
                        } else {
                            // 개발자에게 수정요청
                            showSendReportDialog(item);
                        }
                        dialog.dismiss();
                        break;
                    case 1:
                        // 문장 삭제 옵션.
                        if( item.isMadeByUser() ) {
                            // 커스텀 문장만 삭제가능
                            mActionListener.deleteSentence(item);
                        }
                        dialog.dismiss();
                        break;
                }
            }
        });
        return listView;
    }


    // 문장 추가
    @Override
    public void showAddSentenceFragment(Sentence item) {
        // 문장 추가 창으로 이동
        Bundle bundle = new Bundle();
        bundle.putInt(FIELD__HAS_PARENT_SCRIPT, 1);
        bundle.putInt(FIELD__SCRIPT_ID, mScriptId);

        final FragmentHandler fragmentHandler = FragmentHandler.getInstance();
        fragmentHandler.changeViewFragment(ADD_SENTENCE, bundle);

        Toast.makeText(getActivity(),
                getString(R.string.sentence__create_succ),
                Toast.LENGTH_SHORT).show();
    }

    // 정규 스클립트 -  개발자에게 수정요청 보내기 다이알로그
    @Override
    public void showSendReportDialog(final Sentence item){
        final MyDialog dialog = new MyDialog(getActivity());
        dialog.setTitle(getString(R.string.report__send_title));
        dialog.setMessage(getString(R.string.report__send_guide));
        dialog.setNeutralButton( "수정 요청", new View.OnClickListener() {
            public void onClick(View arg0)
            {
                mActionListener.sendReport(item);
                dialog.dismiss();
            }});
        dialog.setNegativeButton();
        dialog.showUp();
    }

    @Override
    public void onSuccessSendReport() {
        Toast.makeText(getActivity(),
                getString(R.string.report__send_succ),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFailSendReport(int msgId) {
        Toast.makeText(getActivity(),
                getString(msgId),
                Toast.LENGTH_SHORT).show();
    }


    // 유저가 직접 만든 문장 - 수정 다이알로그
    @Override
    public void showModifyDialog(final Sentence item)
    {
        // custom view 를 새로 생성한다.
        // 안하면 이 에러 남..  You must call removeView() on the child's parent first.
        View mEditSentence_View = createView();
        mEditSentence_EditTextKo.setText(item.textKo);
        mEditSentence_EditTextEN.setText(item.textEn);

        mEditSentenceDialog = new MyDialog(getActivity());
        mEditSentenceDialog.setTitle(getString(R.string.sentence__edit_title));
        mEditSentenceDialog.setCustomView(mEditSentence_View, getActivity());
        mEditSentenceDialog.setCancelable(true);
        mEditSentenceDialog.showUp();
    }

    private View createView(){
        // 문장 수정 관련
        View mEditSentence_View = View.inflate(getActivity(), R.layout.content_sentence_edit, null);
        mEditSentence_EditTextKo = ((EditText) mEditSentence_View.findViewById(R.id.sentence_edit_ko));
        mEditSentence_EditTextEN = ((EditText) mEditSentence_View.findViewById(R.id.sentence_edit_en));
        mEditSentence_CompleteBtn = ((Button) mEditSentence_View.findViewById(R.id.sentence_edit_complate_btn));
        mEditSentence_CompleteBtn.setText(getString(R.string.sentence__edit_btn));
        mEditSentence_CompleteBtn.setOnClickListener(mClickListener);

        return mEditSentence_View;
    }

    // 문장 입력 완료 버튼 클릭
    Button.OnClickListener mClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
              //  case R.id.sentence_edit_next_btn:
              //      mEditSentence_EditTextEN.requestFocus();
                    // 스크롤 되도록 함
              //      mScrollingWorkHelpder.setVisibility(View.VISIBLE);
               //     break;
                case R.id.sentence_edit_complate_btn:
                    String ko = mEditSentence_EditTextKo.getText().toString();
                    String en = mEditSentence_EditTextEN.getText().toString();
                    mActionListener.modifySentence(ko, en);

                    mEditSentenceDialog.dismiss();
                    break;
            }
        }
    };

    @Override
    public void onSuccessUpdateSentence() {
        Toast.makeText(getActivity(),
                getString(R.string.sentence__edit_succ),
                Toast.LENGTH_SHORT).show();

        // re-flesh UI
        mActionListener.getSentences(mScriptId);
    }

    @Override
    public void onFailUpdateSentence() {
        Toast.makeText(getActivity(),
                getString(R.string.sentence__edit_fail),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSuccessDeleteSentence() {
        Toast.makeText(getActivity(),
                getString(R.string.sentence__del_succ),
                Toast.LENGTH_SHORT).show();

        // re-flesh UI
        mActionListener.getSentences(mScriptId);
    }

    @Override
    public void onFailDeleteSentence(int msgId) {
        Toast.makeText(getActivity(),
                getString(msgId),
                Toast.LENGTH_SHORT).show();
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
        mToolbar.updateToolBarOptionMenu(SENTENCES, menu);
        super.onPrepareOptionsMenu(menu);
    }

    private void updateToolbar(){
        // 툴바에 현 프래그먼트 제목 출력
        MyToolbar.getInstance().updateToolBar(SENTENCES);
        mActionListener.updateToolbarTitle(mScriptId);
    }

    @Override
    public void showToolbarTitle(String title) {
        if(StringHelper.isNull(title)){
            title = "Sentences";
        }
        MyToolbar.getInstance().updateToolbarTitle( title );
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

