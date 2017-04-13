package kr.jhha.engquiz.presenter_view.quizfolder.scripts.sentences;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import kr.jhha.engquiz.model.local.Sentence;
import kr.jhha.engquiz.R;
import kr.jhha.engquiz.model.local.ScriptRepository;
import kr.jhha.engquiz.presenter_view.MyToolbar;
import kr.jhha.engquiz.util.StringHelper;

/**
 * Created by jhha on 2016-12-16.
 */

public class SentenceFragment extends Fragment implements SentenceContract.View
{
    private SentenceContract.ActionsListener mActionListener;

    private Integer mScriptId;
    private String mScriptTitle;

    // 수정 가능한 문장을 보여주는 뷰 관련
    private SentenceAdapter mAdapter;
    private ListView mItemListView;
    private AlertDialog.Builder mDialogModify = null;
    private EditText mEditTextKo = null;
    private EditText mEditTextEn = null;

    // READ-ONLY 문장 보여주는 뷰 관련
    private TextView mTextView;

    public SentenceFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mActionListener = new SentencePresenter( this, ScriptRepository.getInstance() );
        initDialog();
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
        final MyToolbar toolbar = MyToolbar.getInstance();
        String title = "Sentences";
        if( !StringHelper.isNull(mScriptTitle) ){
            title = mScriptTitle;
        }
        toolbar.setToolBarTitle( title );
        toolbar.switchBackground("image");
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
        String msg = "문장 리스트를 가져오는데 실패했습니다." +
                "\n잠시 후 다시 시도해 주세요.";
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    private void initDialog()
    {
        mDialogModify = new AlertDialog.Builder( getActivity() );
        mDialogModify.setTitle("Modify");
        mDialogModify.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface d, int which) {
                d.dismiss();
            }
        });

        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);
        mEditTextKo = new EditText(getActivity());
        mEditTextEn = new EditText(getActivity());
        layout.addView(mEditTextKo);
        layout.addView(mEditTextEn);
        mDialogModify.setView(layout);
    }

    // 롱 클릭시, 문장 수정 다이알로그를 띄운다
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
        mEditTextKo.setText(item.textKo);
        mEditTextEn.setText(item.textEn);
        mDialogModify.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface d, int which) {

                String ko = mEditTextKo.getText().toString();
                String en = mEditTextEn.getText().toString();
                mActionListener.modifySentence(ko, en);
            }
        });
        mDialogModify.show();
    }

    @Override
    public void onSuccessUpdateSentence() {
        String msg = "문장이 수정되었습니다.";
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();

        // re-flesh UI
        mActionListener.getSentences(mScriptId);
    }

    @Override
    public void onFailUpdateSentence() {
        String msg = "문장 수정에 실패했습니다." +
                    "\n잠시 후 다시 시도해 주세요.";
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }


}

