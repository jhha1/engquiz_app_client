package kr.jhha.engquiz.quizfolder.scripts.sentences;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import kr.jhha.engquiz.MainActivity;
import kr.jhha.engquiz.R;
import kr.jhha.engquiz.data.local.QuizFolderRepository;
import kr.jhha.engquiz.data.local.ScriptRepository;
import kr.jhha.engquiz.data.local.Sentence;
import kr.jhha.engquiz.quizfolder.scripts.AddQuizFolderScriptFragment;
import kr.jhha.engquiz.quizfolder.scripts.QuizFolderScriptsAdapter;
import kr.jhha.engquiz.quizfolder.scripts.QuizFolderScriptsAdapter.ScriptSummary;
import kr.jhha.engquiz.util.StringHelper;

/**
 * Created by jhha on 2016-12-16.
 */

public class ShowSentenceFragment extends Fragment implements ShowSentenceContract.View
{
    private ShowSentenceContract.ActionsListener mActionListener;

    private String mTITLE = "Sentences";
    private Integer mScriptId;
    private String mScriptTitle;

    private TextView mTextView;

    public ShowSentenceFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mActionListener = new ShowSentencePresenter( this, ScriptRepository.getInstance() );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.content_sentence, null);
        mTextView = (TextView) view.findViewById(R.id.sentences);
        // text view scrolling
        mTextView.setMovementMethod(new ScrollingMovementMethod());

        // 데이터 초기화
        mActionListener.getSentences(mScriptId);
        view.bringToFront(); // 리스트가 길어질 경우 가장 위로 스크롤.
        return view;
    }

    @Override
    public void onResume() {
        // 툴바에 현 프래그먼트 제목 출력
        mTITLE = mScriptTitle;
        ((MainActivity)getActivity()).setActionBarTitle( mTITLE );
        super.onResume();
    }

    @Override
    public void onSuccessGetSentences(String sentences) {
        mTextView.setText(sentences);
    }

    @Override
    public void onFailGetSentences() {
        String msg = "문장 리스트를 가져오는데 실패했습니다." +
                "\n잠시 후 다시 시도해 주세요.";
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    public void setScriptInfo(Integer scriptId, String scriptTitle){
        mScriptId = scriptId;
        mScriptTitle = scriptTitle;
    }
}

