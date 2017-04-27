package kr.jhha.engquiz.presenter_view.quizfolder.scripts.sentences;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import kr.jhha.engquiz.model.local.Sentence;
import kr.jhha.engquiz.R;
import kr.jhha.engquiz.model.local.ScriptRepository;
import kr.jhha.engquiz.presenter_view.MyToolbar;
import kr.jhha.engquiz.util.ui.MyDialog;
import kr.jhha.engquiz.util.StringHelper;
import kr.jhha.engquiz.util.ui.Etc;
import kr.jhha.engquiz.util.ui.click_detector.ClickDetector;
import kr.jhha.engquiz.util.ui.click_detector.ListViewClickDetector;

import static kr.jhha.engquiz.presenter_view.FragmentHandler.EFRAGMENT.ADD_SCRIPT_INTO_QUIZFOLDER;
import static kr.jhha.engquiz.presenter_view.FragmentHandler.EFRAGMENT.SHOW_SENTENCES_IN_SCRIPT;

/**
 * Created by jhha on 2016-12-16.
 */

public class SentenceFragment extends Fragment implements SentenceContract.View, ClickDetector.Callback
{
    private SentenceContract.ActionsListener mActionListener;

    private Integer mScriptId;
    private String mScriptTitle;

    /*
     READ-ONLY 문장 보여주는 뷰 관련
      */
    private TextView mTextView;

    /*
     수정 가능한 문장을 보여주는 뷰 관련
      - EditText ListView
      */
    private SentenceAdapter mAdapter;
    private ListView mItemListView;
    // 리스트뷰에서 클릭한 아이템. 흐름이 중간에 끊겨서 어떤 아이템 클릭했는지 알려고 클래스변수로 저장해 둠.
    private Sentence mListviewSelectedItem = null;
    // 클릭 or 더블클릭 감지자. 안드로이드 더블클릭 감지해 알려주는 지원없어 직접 만듬.
    private ClickDetector mClickDetector = null;

    public SentenceFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mActionListener = new SentencePresenter( this, ScriptRepository.getInstance() );
        mClickDetector = new ListViewClickDetector( this );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.content_sentence, null);

        setUpToolBar();

        // 선생님이 올려준 스크립트 문장을 보는경우.
        // READ-ONLY. 유저는 수정할 수 없으므로 일반 텍스트 창에 뿌린다.
        mTextView = (TextView) view.findViewById(R.id.sentences);
        mTextView.setMovementMethod(new ScrollingMovementMethod()); // text view scrolling

        // 유저가 만든 스크립트 문장을 보는경우.
        // 수정을 유저가 직접해야하므로, 각 문장의 선택이벤트 등 구별위해 리스트뷰에 뿌려준다.
        mItemListView = (ListView) view.findViewById(R.id.sentence_listview);
        // 클릭 이벤트 핸들러 정의: 원클릭-내 퀴즈 디테일 보기, 더블클릭-게임용으로 퀴즈폴더설정
        mItemListView.setOnItemClickListener(mListItemClickListener);
        // 롱 클릭 이벤트 핸들러 정의: 문장 삭제
        mItemListView.setOnItemLongClickListener(mListItemLongClickListener);

        // 조건에 따라 어떤 뷰를 보여줄지 다르기 때문에,
        // 조건을 계산하는 사이에 뷰가 보이지 않도록 설정.
        mTextView.setVisibility(View.INVISIBLE);
        mItemListView.setVisibility(View.INVISIBLE);

        // 데이터 초기화
        mActionListener.getSentences(mScriptId);
        view.bringToFront(); // 리스트가 길어질 경우 가장 위로 스크롤.
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void setUpToolBar(){
        MyToolbar.getInstance().setToolBar(SHOW_SENTENCES_IN_SCRIPT);

        // 툴바에 현 프래그먼트 제목 출력
        mActionListener.initToolbarTitle(mScriptId);
    }

    @Override
    public void showTitle(String title) {
        if(StringHelper.isNull(title)){
            title = "Sentences";
        }
        MyToolbar.getInstance().setToolbarTitle( title );
    }

    /*
        FolderScriptsFragment 에서 호출함.
        선택한 스크립트 정보를 넘겨줌.
        여기서는 그 정보를 기반으로 문장들을 가져온다.
     */
    public void setScriptInfo(Integer scriptId, String scriptTitle){
        mScriptId = scriptId;
        mScriptTitle = scriptTitle;
    }

    /*
        문장 리스트 가져오기
     */
    /*
        문장리스트를 READ-ONLY 로 출력
     */
    @Override
    public void onSuccessGetSentences(String sentences) {
        mItemListView.setVisibility(View.INVISIBLE);
        mTextView.setVisibility(View.VISIBLE);

        mTextView.setText(sentences);
    }

    /*
        문장리스트를 수정가능하게 출력.

        그리고 수정 가능한 문장 관련 코드들이 아래부터.
     */
    @Override
    public void onSuccessGetSentences(List<Sentence> sentences) {
        mTextView.setVisibility(View.INVISIBLE);
        mItemListView.setVisibility(View.VISIBLE);

        mAdapter = new SentenceAdapter( sentences );
        mItemListView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onFailGetSentences() {
        Toast.makeText(getActivity(),
                getString(R.string.sentence__fail_get_list),
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
            // 클릭과 더블클릭을 구분해 그에따른 동작.
            mClickDetector.onClick( position );
        }
    };

    // 한번 클릭 (리스트뷰 아이템)
    @Override
    public void onSingleClicked() {
        // nothing
    }

    // 더블 클릭 (리스트뷰 아이템) : 문장 수정 다이알로그를 띄운다
    @Override
    public void onDoubleClicked() {
        mActionListener.sentenceDoubleClicked( mListviewSelectedItem );
    }

    // 롱 클릭 (리스트뷰 아이템) : 문장 삭제 다이알로그 띄운다
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
    public void showModifyDialog(final Sentence item){
        final EditText editTextKo = Etc.makeEditText(getActivity(), item.textKo);
        final EditText editTextEN = Etc.makeEditText(getActivity(), item.textEn);
        String neutralButtonName = getString(R.string.sentence__edit_neutral_btn);

        final MyDialog dialog = new MyDialog(getActivity());
        dialog.setTitle(getString(R.string.sentence__edit_title));
        dialog.setEditText(editTextKo, editTextEN);
        dialog.setNeutralButton(neutralButtonName, new View.OnClickListener() {
                                    public void onClick(View arg0) {
                                        String ko = editTextKo.getText().toString();
                                        String en = editTextEN.getText().toString();
                                        mActionListener.modifySentence(ko, en);
                                        dialog.dismiss();
                                    }
                                });
        dialog.setNegativeButton();
        dialog.showUp();
    }

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
    public void showDeleteDialog(){
        final MyDialog dialog = new MyDialog(getActivity());
        dialog.setTitle(getString(R.string.sentence__del_confirm));
        dialog.setPositiveButton(new View.OnClickListener() {
            public void onClick(View arg0) {
                mActionListener.deleteSentence();
                dialog.dismiss();
            }
        });
        dialog.setNegativeButton();
        dialog.showUp();
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
    public void onFailDeleteSentence() {
        Toast.makeText(getActivity(),
                getString(R.string.sentence__del_fail),
                Toast.LENGTH_SHORT).show();
    }
}

