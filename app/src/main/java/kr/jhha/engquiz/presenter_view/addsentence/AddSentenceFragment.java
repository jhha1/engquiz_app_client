package kr.jhha.engquiz.presenter_view.addsentence;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import kr.jhha.engquiz.R;
import kr.jhha.engquiz.model.local.QuizFolder;
import kr.jhha.engquiz.model.local.ScriptRepository;
import kr.jhha.engquiz.model.local.UserRepository;
import kr.jhha.engquiz.presenter_view.FragmentHandler;
import kr.jhha.engquiz.presenter_view.MyToolbar;
import kr.jhha.engquiz.util.ui.Actions;
import kr.jhha.engquiz.util.ui.MyDialog;
import kr.jhha.engquiz.util.ui.Etc;
import kr.jhha.engquiz.util.ui.MyLog;

import static kr.jhha.engquiz.presenter_view.FragmentHandler.EFRAGMENT.ADD_SENTENCE;

/**
 * Created by Junyoung on 2016-06-23.
 */

public class AddSentenceFragment extends Fragment implements AddSentenceContract.View {
    private AddSentenceContract.ActionsListener mActionListener;

    private EditText mEditTextKo;
    private EditText mEditTextEn;
    private LinearLayout mScrollingWorkHelpder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActionListener = new AddSentencePresenter(this, ScriptRepository.getInstance());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_sentence_add, container, false);;

        setUpToolBar();

        // 문장 입력 칸
        mEditTextKo = (EditText) view.findViewById(R.id.sentence_add_ko);
        mEditTextEn = (EditText) view.findViewById(R.id.sentence_add_en);

        // 키보드 IME로  '완료'로 다음 액션 선택.
        // IME 자판이 바뀌려면, inputType이 Text여야 한다. but,
        // inputType이 Text면 줄바꿈이 안되고 한줄로만 글씨가 입력된다.
        //mEditTextEn.setInputType(InputType.TYPE_CLASS_TEXT);
        //mEditTextEn.setImeOptions(EditorInfo.IME_ACTION_DONE);
        mEditTextEn.setOnEditorActionListener(mEditorActionListener);

        // 버튼 '완료' 으로 다음 액션 선택
        //Button nextButton = (Button) view.findViewById(R.id.sentence_add_next_btn);
        Button doneButton = (Button) view.findViewById(R.id.sentence_add_complate_btn);
       // nextButton.setOnClickListener(mClickListener);
        doneButton.setOnClickListener(mClickListener);

        view.bringToFront(); // 리스트가 길어질 경우 가장 위로 스크롤.
        return view;
    }

    private void setUpToolBar(){
        final MyToolbar toolbar = MyToolbar.getInstance();
        toolbar.setToolBar(ADD_SENTENCE);
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
        mActionListener.sentencesInputted( ko, en );
        clearEditTexts();
    }

    private void clearEditTexts(){
        mEditTextEn.setText("");
        mEditTextKo.setText("");
        mEditTextKo.requestFocus();
        Actions.hideKeyboard(getActivity());
    }

    /*
        2. 문장을 넣을 스크립트 선택 단계
     */
    @Override
    public void showNeedMakeScriptDialog() {
        String username = UserRepository.getInstance().getUserName();
        final MyDialog dialog = new MyDialog(getActivity());
        dialog.setTitle("문장을 넣을 스크립트를 선택해주세요.");
        dialog.setMessage("직접 만든 문장을 넣을 스크립트가 아직 없네요~ \n"
                        + "정규 스크립트에는 직접 만든 문장을 넣을 수 없어요." +
                        "\n\n'새 스크립트 만들기' 버튼을 클릭하세요 :)");
        dialog.setNeutralButton("새 스크립트 만들기", new View.OnClickListener() {
            public void onClick(View arg0) {
                mActionListener.makeNewScriptBtnClicked();
                dialog.dismiss();
            }
        });
        dialog.setNegativeButton();
        dialog.showUp();
    }

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
        dialog.setTitle("문장을 넣을 스크립트를 선택해주세요 :)");
        dialog.setNeutralButton("새 스크립트 만들기", new View.OnClickListener() {
            public void onClick(View arg0) {
                mActionListener.makeNewScriptBtnClicked();
                dialog.dismiss();
            }
        });
        dialog.setNegativeButton();
        dialog.setAdapter(adapter,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String listItemTitle = adapter.getItem(id);
                        mActionListener.scriptSelected(listItemTitle);
                        dialog.dismiss();
                    }
                });
        adapter.notifyDataSetChanged();
        dialog.showUp();
    }

    @Override
    public void showDialogMakeNewScript(){
        final EditText input = Etc.makeEditText(getActivity());
        final MyDialog dialog = new MyDialog(getActivity());
        dialog.setTitle("새 스크립트의 제목을 입력해주세요.");
        dialog.setEditText(input);
        dialog.setPositiveButton(new View.OnClickListener() {
            public void onClick(View arg0) {
                mActionListener.makeNewScript( input.getText().toString() );
                dialog.dismiss();
            }
        });
        dialog.showUp();
    }

    @Override
    public void showDialogMakeNewScript_ReInput(){
        final EditText input = Etc.makeEditText(getActivity());
        final MyDialog dialog = new MyDialog(getActivity());
        dialog.setTitle("이미 똑같은 이름의 스크립트가 있어요.  다른 이름을 지어주세요~");
        dialog.setEditText(input);
        dialog.setPositiveButton(new View.OnClickListener() {
            public void onClick(View arg0) {
                mActionListener.makeNewScript( input.getText().toString() );
                dialog.dismiss();
            }
        });
        dialog.showUp();
    }

    /*
        3. 모든 단계 완료.
     */
    @Override
    public void showAddSentenceSuccessDialog(String scriptName) {
        final MyDialog dialog = new MyDialog(getActivity());
        dialog.setTitle("문장 추가 완료");
        dialog.setMessage( "문장이 추가되었어요. "+scriptName+" 에서 확인하실 수 있어요 :D" );
        dialog.setPositiveButton( new View.OnClickListener() {
            public void onClick(View arg0)
            {
                dialog.dismiss(); // 다이알로그 닫기
                clearEditTexts();
            }});
        dialog.showUp();
    }

    @Override
    public void showErrorDialog(int what) {
        MyLog.e("addScriptFragment.showErrorDialog() what:"+what);
        String msg = null;
        switch (what) {
            case 1:
                msg = "이미 똑같은 이름의 스크립트가 있어요. \n 다른 이름을 지어주세요. ";
                break;
            case 7:
                msg = "잘못된 문장이에요.  문장을 다시 입력해 주세요.";
                break;
            default:
                msg = "오류가 발생했어요. 잠시후에 다시 시도해 주세요 ㅜㅜ";
                break;
        }

        MyDialog dialog = new MyDialog(getActivity());
        dialog.setTitle(getString(R.string.common__warning));
        dialog.setMessage(msg);
        dialog.setPositiveButton();
        dialog.showUp();
    }
}
