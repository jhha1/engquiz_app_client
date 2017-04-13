package kr.jhha.engquiz.presenter_view.addsentence;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import kr.jhha.engquiz.R;
import kr.jhha.engquiz.model.local.QuizFolder;
import kr.jhha.engquiz.model.local.ScriptRepository;
import kr.jhha.engquiz.presenter_view.MyToolbar;
import kr.jhha.engquiz.util.Actions;
import kr.jhha.engquiz.util.Dialogs;

/**
 * Created by Junyoung on 2016-06-23.
 */

public class AddSentenceFragment extends Fragment implements AddSentenceContract.View {
    private AddSentenceContract.ActionsListener mActionListener;

    // 다이알로그
    private AlertDialog.Builder mDialogSelectQuizFolder = null; // 퀴즈폴더선택
    private AlertDialog.Builder mDialogSelectScript = null;  // 스크립트 선택
    private AlertDialog.Builder mDialogNewScriptTitleInput = null; // 새 스크립트 이름 입력 다이알로그


    private ProgressDialog mDialogLoadingSpinner = null; // 동글뱅이 로딩 중(스크립트 추가중.. ) 다이알로그. 서버통신때씀
    private AlertDialog.Builder mDialogResult = null; // 스크립트 추가(파싱) 결과 다이알로그


    private EditText mEditTextKo;
    private EditText mEditTextEn;
    private EditText mInputScriptTitle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActionListener = new AddSentencePresenter(this, ScriptRepository.getInstance());

        initDialogSelectScript();
        //initDialogInputScriptTitle();
        initDialogSelectQuizFolder();
        initDialogResult();
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
        Button nextButton = (Button) view.findViewById(R.id.sentence_add_next_btn);
        Button doneButton = (Button) view.findViewById(R.id.sentence_add_complate_btn);
        nextButton.setOnClickListener(mClickListener);
        doneButton.setOnClickListener(mClickListener);

        view.bringToFront(); // 리스트가 길어질 경우 가장 위로 스크롤.
        return view;
    }

    private void setUpToolBar(){
        final MyToolbar toolbar = MyToolbar.getInstance();
        toolbar.setToolBarTitle( "Add Sentence" );
        toolbar.switchBackground("image");
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
                case R.id.sentence_add_next_btn:
                    mEditTextEn.requestFocus();
                    break;
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
        String msg = "문장을 넣을 커스텀 스크립트가 없습니다." +
                "\n'새 스크립트 만들기' 버튼을 눌러 새 스크립트를 만드세요.";
        mDialogSelectScript.setMessage(msg);
        mDialogSelectScript.setNeutralButton("새 스크립트 만들기", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface d, int which) {
                mActionListener.makeNewScriptBtnClicked();
            }
        });

        mDialogSelectScript.show();
    }

    @Override
    public void showDialogSelectScript( String[] scriptTitleAll ) {
        // List Adapter 생성
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.select_dialog_singlechoice);
        // adapter에 quiz folder list 집어넣기
        for( String title : scriptTitleAll ){
            adapter.add(title);
        }

        // Dialog와 Adapter 연결
        mDialogSelectScript.setAdapter(adapter,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // 유저가 quiz folder 선택.
                        String listItemTitle = adapter.getItem(id);
                        mActionListener.scriptSelected(listItemTitle);
                        dialog.dismiss();
                    }
                });
        adapter.notifyDataSetChanged();

        mDialogSelectScript.setNeutralButton("새 스크립트 만들기", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface d, int which) {
                mActionListener.makeNewScriptBtnClicked();
            }
        });
        mDialogSelectScript.show();
    }

    @Override
    public void showDialogMakeNewScript(){
        final EditText input = Dialogs.makeEditText(getActivity());
        Dialogs.showEditDialog(getActivity(),
                "새 스크립트의 제목을 입력해주세요",
                    input,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface d, int which) {
                            mActionListener.makeNewScript( input.getText().toString() );
                            d.dismiss();
                        }
                    });
    }

    @Override
    public void showDialogMakeNewScript_ReInput(){
        final EditText input = new EditText(getActivity());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        Dialogs.showEditDialog(getActivity(),
                "이미 존재하는 스크립트 이름입니다. 다른 이름을 기입해주세요.",
                input,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface d, int which) {
                        mActionListener.makeNewScript( input.getText().toString() );
                        d.dismiss();
                    }
                });
    }

    /*
        2 - 1.  새 스크립트 경우,
                스크립트를 넣을 퀴즈폴더 선택 단계
     */
    @Override
    public void showMakeNewQuizFolderDialog(){
        String msg = "스크립트를 넣을 퀴즈폴더가 없습니다." +
                    "\n'새 폴더 만들기' 버튼을 눌러 새폴더를 만드세요.";
        mDialogSelectQuizFolder.setMessage(msg);
        mDialogSelectQuizFolder.setNeutralButton("새 폴더 만들기", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface d, int which) {
                mActionListener.quizFolderSelected( QuizFolder.TEXT_NEW_FOLDER );
            }
        });

        mDialogSelectQuizFolder.show();
    }

    @Override
    public void showQuizFolderSelectDialog( List<String> quizFolderList ) {
        // List Adapter 생성
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.select_dialog_singlechoice);
        // adapter에 quiz folder list 집어넣기
        for( String quizFolderTitle : quizFolderList ){
            adapter.add(quizFolderTitle);
        }
        adapter.notifyDataSetChanged();

        // Dialog와 Adapter 연결
        mDialogSelectQuizFolder.setAdapter(adapter,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // 유저가 quiz folder 선택.
                        String listItemTitle = adapter.getItem(id);
                        mActionListener.quizFolderSelected( listItemTitle );
                        dialog.dismiss();
                    }
                });

        mDialogSelectQuizFolder.setNeutralButton("새 폴더 만들기", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface d, int which) {
                mActionListener.quizFolderSelected( QuizFolder.TEXT_NEW_FOLDER );
            }
        });

        mDialogSelectQuizFolder.show();
    }

    @Override
    public void showNewQuizFolderTitleInputDialog() {
        // AlertDialog 안에 있는 AlertDialog
        final AlertDialog.Builder innBuilder = new AlertDialog.Builder(getActivity());
        innBuilder.setTitle("새 퀴즈폴더의 이름을 기입해주세요.");
        final EditText inputQuizFolderTitle = new EditText(getActivity());
        innBuilder.setView( inputQuizFolderTitle );
        //innBuilder.setMessage("영어,한글,숫자만 가능합니다. \n최소 1글자 ~ 최대 30글자까지 가능합니다.");

        innBuilder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String inputText = inputQuizFolderTitle.getText().toString();
                        mActionListener.newQuizFolderTitleInputted( inputText );
                        dialog.dismiss();
                    }
                });
        innBuilder.show();
    }

    /*
        3. 모든 단계 완료.
     */
    @Override
    public void showAddSentenceSuccessDialog(String quizFolderName, String scriptName) {
        AlertDialog.Builder d = new AlertDialog.Builder(getActivity());
        d.setTitle("문장 추가 완료");
        String msg = "문장이 추가되었습니다. " +
                "\n QuizFolder 메뉴 -> " + quizFolderName + " -> "+scriptName+" 에서 확인하실 수 있습니다.";
        d.setMessage(msg);
        d.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface d, int which) {
                d.dismiss(); // 다이알로그 닫기
                clearEditTexts();
            }
        });
        d.show();
    }

    private void initDialogSelectScript() {
        mDialogSelectScript = new AlertDialog.Builder(getActivity());
        mDialogSelectScript.setIcon(android.R.drawable.alert_dark_frame);
        mDialogSelectScript.setTitle("문장을 넣을 스크립트를 선택해주세요.");
        mDialogSelectScript.setCancelable(false); //  Back키 눌렀을 경우 Dialog Cancle 여부 설정
        // 취소 버튼 클릭 이벤트.
        mDialogSelectScript.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface d, int which) {
                // 다이알로그 닫기
                d.dismiss();
            }
        });
    }

    private void initDialogSelectQuizFolder() {
        mDialogSelectQuizFolder = new AlertDialog.Builder(getActivity());
        mDialogSelectQuizFolder.setIcon(android.R.drawable.alert_dark_frame);
        mDialogSelectQuizFolder.setTitle("스크립트를 넣을 퀴즈폴더를 선택해주세요.");
        mDialogSelectQuizFolder.setCancelable(false); //  Back키 눌렀을 경우 Dialog Cancle 여부 설정
        // 취소 버튼 클릭 이벤트.
        mDialogSelectQuizFolder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface d, int which) {
                // 다이알로그 닫기
                d.dismiss();
            }
        });
    }

    private void initDialogResult() {
        mDialogResult = new AlertDialog.Builder(getActivity());
        mDialogResult.setTitle("문장 추가 완료");
        // ok, cancel button 클릭 이벤트.
        mDialogResult.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface d, int which) {
                d.dismiss(); // 다이알로그 닫기
            }
        });
    }

    private void initDialogInputScriptTitle() {
        mDialogNewScriptTitleInput = new AlertDialog.Builder( getActivity() );
        mDialogNewScriptTitleInput.setIcon(android.R.drawable.alert_dark_frame);
        mDialogNewScriptTitleInput.setTitle("새 스크립트의 제목을 입력해주세요");
        mInputScriptTitle = new EditText(getActivity());
        mInputScriptTitle.setInputType(InputType.TYPE_CLASS_TEXT);
        mDialogNewScriptTitleInput.setView(mInputScriptTitle);
        mDialogNewScriptTitleInput.setCancelable(false); //  Back키 눌렀을 경우 Dialog Cancle 여부 설정
        // 취소 버튼 클릭 이벤트.
        mDialogNewScriptTitleInput.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface d, int which) {
                d.dismiss();
            }
        });
    }



    @Override
    public void showErrorDialog(int what) {
        Log.e("AppContent", "addScriptFragment.showErrorDialog() what:"+what);
        String msg = null;
        switch (what) {
            case 1:
                msg = "이미 존재하는 스크립트 이름입니다. 다른 이름으로 스크립트를 생성해주세요.";
                break;
            case 2:
                msg = "스크립트를 퀴즈폴더에 추가하는데 오류가 발생했습니다. 잠시후에 다시 시도해 주세요";
                break;
            case 3:
                msg = "퀴즈폴더를 생성하는데 실패했습니다. 잠시후에 다시 시도해주세요.";
                break;
            case 4:
                msg = "퀴즈폴더를 가져오는데 실패했습니다. 잠시후에 다시 시도해주세요.";
                break;
            case 5:
                msg = "이미 존재하는 퀴즈폴더 이름입니다. 다른 이름으로 퀴즈폴더를 생성해주세요";
                break;
            case 7:
                msg = "잘못된 문장입니다. 문장을 다시 입력해 주세요.";
                break;
            default:
                msg = "오류가 발생했습니다. 잠시후에 다시 시도해 주세요";
                break;
        }

        AlertDialog.Builder d = new AlertDialog.Builder(getActivity());
        d.setTitle("Warning").setMessage(msg);
        d.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface d, int which) {
                d.dismiss();
            }
        });
        d.show();
    }
}
