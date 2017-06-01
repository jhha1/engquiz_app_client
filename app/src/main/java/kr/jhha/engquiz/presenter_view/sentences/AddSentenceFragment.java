package kr.jhha.engquiz.presenter_view.sentences;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import kr.jhha.engquiz.R;
import kr.jhha.engquiz.model.local.ScriptRepository;
import kr.jhha.engquiz.presenter_view.FragmentHandler;
import kr.jhha.engquiz.presenter_view.MyToolbar;
import kr.jhha.engquiz.util.StringHelper;
import kr.jhha.engquiz.util.ui.Actions;
import kr.jhha.engquiz.util.ui.MyDialog;
import kr.jhha.engquiz.util.ui.MyLog;

import static kr.jhha.engquiz.presenter_view.FragmentHandler.EFRAGMENT.ADD_SENTENCE;
import static kr.jhha.engquiz.presenter_view.FragmentHandler.EFRAGMENT.CUSTOM_SCRIPTS;
import static kr.jhha.engquiz.presenter_view.FragmentHandler.EFRAGMENT.SENTENCES;

/**
 * Created by Junyoung on 2016-06-23.
 */

public class AddSentenceFragment extends Fragment implements AddSentenceContract.View {
    private AddSentenceContract.ActionsListener mActionListener;

    // 스크립트 내의 문장보기에서, 문장 추가 한 경우.
    // 이미 소속될 스크립트가 정해져 있다.
    private static Integer mParentScriptId = -1;
    private static boolean bHasParentScript = false;

    private EditText mEditTextKo;
    private EditText mEditTextEn;
    private LinearLayout mScrollingWorkHelpder;

    private MyToolbar mToolbar;

    public static String FIELD__HAS_PARENT_SCRIPT = "FIELD__HAS_PARENT_SCRIPT";
    public static String FIELD__SCRIPT_ID = "FIELD__SCRIPT_ID";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initToolbar(); // 액션 바
        mActionListener = new AddSentencePresenter(getActivity(), this, ScriptRepository.getInstance());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_sentence_add, container, false);

        setUpArgs( getArguments() );

        // 문장 입력 칸
        mEditTextKo = (EditText) view.findViewById(R.id.sentence_add_ko);
        mEditTextEn = (EditText) view.findViewById(R.id.sentence_add_en);
        mEditTextKo.setText(StringHelper.EMPTY_STRING);
        mEditTextEn.setText(StringHelper.EMPTY_STRING);


        // 키보드 IME로  '완료'로 다음 액션 선택.
        // IME 자판이 바뀌려면, inputType이 Text여야 한다. but,
        // inputType이 Text면 줄바꿈이 안되고 한줄로만 글씨가 입력된다.
        //mEditTextEn.setInputType(InputType.TYPE_CLASS_TEXT);
        //mEditTextEn.setImeOptions(EditorInfo.IME_ACTION_DONE);
        mEditTextEn.setOnEditorActionListener(mEditorActionListener);

        // 버튼 '완료' 으로 다음 액션 선택
        Button doneButton = (Button) view.findViewById(R.id.sentence_add_complate_btn);
        doneButton.setOnClickListener(mClickListener);

        view.bringToFront(); // 리스트가 길어질 경우 가장 위로 스크롤.
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mToolbar.updateToolBar(ADD_SENTENCE);
    }

    private void setUpArgs( Bundle bundle ){
        if( bundle != null ){
            mParentScriptId = bundle.getInt(FIELD__SCRIPT_ID);
            bHasParentScript = (bundle.getInt(FIELD__HAS_PARENT_SCRIPT) == 1) ? true : false;
        } else {
            bHasParentScript = false;
        }
    }


    /*
        1. 문장 입력 완료 단계
     */
    TextView.OnEditorActionListener mEditorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            // 키보드 '완료' 자판 클릭
            if(actionId == EditorInfo.IME_ACTION_DONE) {
                sentencesInputted();
                return true;
            }
            return false;
        }
    };

    // 문장 입력 완료 버튼 클릭
    Button.OnClickListener mClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.sentence_add_complate_btn:
                    sentencesInputted();
                    break;
            }
        }
    };

    private void sentencesInputted(){
        String ko = mEditTextKo.getText().toString();
        String en = mEditTextEn.getText().toString();
        mActionListener.sentencesInputted( bHasParentScript, mParentScriptId,  ko, en );
        Actions.hideKeyboard(getActivity());
    }

    /*
        2. 문장을 넣을 스크립트 선택 단계
     */

    @Override
    public void showDialogSelectScript( String[] scriptTitleAll ) {
        // List Adapter 생성
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.select_dialog_singlechoice) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View row = super.getView(position, convertView, parent);
                    int color = ContextCompat.getColor(getActivity(), R.color.colorPrimary);
                    row.setBackgroundColor(color);
                    return row;
                }
        };
        // adapter에 list 집어넣기
        for( String title : scriptTitleAll ){
            adapter.add(title);
        }


        final MyDialog dialog = new MyDialog(getActivity());
        dialog.setTitle("문장을 넣을 스크립트를 선택해주세요.");
        dialog.setNeutralButton("새 스크립트 만들기", new View.OnClickListener() {
            public void onClick(View arg0) {
                mActionListener.makeNewScriptBtnClicked();
                dialog.dismiss();
            }
        });
        dialog.setNegativeButton();
        dialog.setListView(getActivity(), adapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String listItemTitle = adapter.getItem(id);
                mActionListener.scriptSelected(listItemTitle);
                dialog.dismiss();
            }
        });
        adapter.notifyDataSetChanged();
        dialog.showUp();
    }

    /*
        3. 모든 단계 완료.
     */
    @Override
    public void showSentenceFragment(final Integer scriptId, final String scriptName) {
        // 문장이 들어있는 스크립트 창으로 이동
        final FragmentHandler fragmentHandler = FragmentHandler.getInstance();
        SentenceFragment fragment = (SentenceFragment) fragmentHandler.getFragment(SENTENCES);
        fragment.setScriptInfo(scriptId, scriptName);
        fragmentHandler.changeViewFragment(SENTENCES);

        Toast.makeText(getActivity(),
                getString(R.string.sentence__create_succ),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showErrorDialog(int msgId) {
        MyLog.e("addScriptFragment.showErrorDialog() msg:"+getString(msgId));
        MyDialog dialog = new MyDialog(getActivity());
        dialog.setMessage(getString(msgId));
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
        mToolbar.updateToolBarOptionMenu(ADD_SENTENCE, menu);
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
