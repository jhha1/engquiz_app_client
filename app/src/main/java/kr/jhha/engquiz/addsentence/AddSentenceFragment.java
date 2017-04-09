package kr.jhha.engquiz.addsentence;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import kr.jhha.engquiz.R;
import kr.jhha.engquiz.data.local.QuizFolder;
import kr.jhha.engquiz.data.local.ScriptRepository;
import kr.jhha.engquiz.util.StringHelper;

/**
 * Created by Junyoung on 2016-06-23.
 */

public class AddSentenceFragment extends Fragment implements AddSentenceContract.View {
    private AddSentenceContract.ActionsListener mActionListener;
    private ArrayAdapter mScriptListViewAdapter;

    // 현재 파일 위치 출력 뷰
    private TextView mFileLocationView = null;
    // 파일 리스트 뷰
    private ListView mItemListView = null;

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

        initDialogEditSentence();
        initDialogSelectScript();
        initDialogSelectQuizFolder();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_sentence_add, container, false);

        // presenter에 공용 클래스 변수들이 사용되므로, 뷰가 새로 그려질때마다 presenter를 새로 생성하도록 한다.
        mActionListener = new AddSentencePresenter(this, ScriptRepository.getInstance());

        mEditTextKo = (EditText) view.findViewById(R.id.sentence_add_ko);
        mEditTextEn = (EditText) view.findViewById(R.id.sentence_add_en);

        // 추가할 스크립트 선택 후, OK 버튼 클릭 이벤트.
        Button mOKButton = (Button) view.findViewById(R.id.sentence_add_complate_btn);
        mOKButton.setOnClickListener(mClickListener);

        view.bringToFront(); // 리스트가 길어질 경우 가장 위로 스크롤.
        return view;
    }

    // 추가할 스크립트 선택 리스트 뷰: 리스트의 row 선택 이벤트.
    // 상위 or 하위 디렉토리 선택시, 디렉토리 다시 그리기
    AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView parent, View v, int position, long id) {
            // String strText = (String) parent.getItemAtPosition(position);  // toInt TextView's Text.
            mActionListener.onFileListItemClick(position);
        }
    };

    // 스크립트 추가 버튼 클릭.
    //  -> 스크립트 추가 확인 다이알로그를 띄운다.
    Button.OnClickListener mClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.sentence_add_complate_btn:
                    String ko = mEditTextKo.getText().toString();
                    String en = mEditTextEn.getText().toString();
                    mActionListener.sentencesInputted( ko, en );
                    break;
            }
        }
    };

    @Override
    public void showDialogSelectScript( String[] scriptTitleAll ) {
        // 플레이 리스트 아답터 연결
        int resourceID = R.layout.content_textstyle_listview_checked_simple;
        mScriptListViewAdapter = new ArrayAdapter<String>(getActivity(), resourceID, scriptTitleAll);
        mItemListView.setAdapter(mScriptListViewAdapter);
        mItemListView.setOnItemClickListener(mListItemClickListener);
        mScriptListViewAdapter.notifyDataSetChanged();

        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(mItemListView);
        mDialogSelectScript.setView(layout);
        mDialogSelectScript.show();
    }

    // 리스트뷰 클릭 이벤트 리스너
    private AdapterView.OnItemClickListener mListItemClickListener
            = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView parent, View v, int position, long id) {
            String selectedScriptTitle = (String) mScriptListViewAdapter.getItem(position);
            mActionListener.scriptSelected(selectedScriptTitle);
        }
    };

    @Override
    public void showDialogMakeNewScript(){
        mDialogNewScriptTitleInput.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface d, int which) {
                mActionListener.makeNewScript( mInputScriptTitle.getText().toString() );
                d.dismiss();
            }
        });
        mDialogNewScriptTitleInput.show();
    }

    @Override
    public void showNeedMakeQuizFolderDialog(){
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

    @Override
    public void showAddScriptConfirmDialog(String filename, Float fileSize) {
        // 스크립트 추가 다이알로그 띄우기
        final String filenameDoubleChecked = StringHelper.isNullString(filename) ? "스크립트" : filename;
        mDialogSelectScript.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface d, int which) {
                mActionListener.addScript(filenameDoubleChecked);
            }
        });
        // 파일 사이즈가 이상할경우, UI에는 0.4MB로 표시 (업로드 + 다운로드)
        fileSize = (fileSize <= 0) ? 0.4f : fileSize;
        String dialogMsg = filename + "를 추가합니다. " +
                "\n스크립트 분석을 위한 서버로의 파일 업/다운로드 과정에서  "
                + fileSize + "MB의 데이터가 소모될 수 있으니, "
                + "WIFI 환경에서 이용하시길 권장합니다."
                + "\n\n스크립트를 추가하시겠습니까?";
        mDialogSelectScript.setMessage(dialogMsg);
        mDialogSelectScript.show();
    }

    @Override
    public void showLoadingDialog() {
        // 로딩스피너 다이알로그 보이기.
        mDialogLoadingSpinner.setMessage("서버에서 스크립트 분석중입니다..");
        mDialogLoadingSpinner.show();
    }

    @Override
    public void closeLoadingDialog() {
        mDialogLoadingSpinner.dismiss(); // 로딩스피너 다이알로그 닫기
    }

    @Override
    public void showAddScriptSuccessDialog(String quizFolderName) {
        String msg = "스크립트가 추가되었습니다. " +
                "\n QuizFolder 메뉴의 " + quizFolderName + "를 클릭하면 확인하실 수 있습니다.";
        mDialogResult.setMessage(msg);
        mDialogResult.show();
    }

    private void initDialogEditSentence() {
        mDialogSelectScript = new AlertDialog.Builder(getActivity());
        mDialogSelectScript.setIcon(android.R.drawable.alert_dark_frame);
        mDialogSelectScript.setTitle("스크립트 선택");
        mDialogSelectScript.setMessage("문장이 소속될 스크립트를 선택하세요.");
        mDialogSelectScript.setCancelable(false); //  Back키 눌렀을 경우 Dialog Cancle 여부 설정
        // 취소 버튼 클릭 이벤트.
        mDialogSelectScript.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface d, int which) {
                // 다이알로그 닫기
                d.dismiss();
            }
        });

        mDialogSelectScript.setNeutralButton("새 스크립트 만들기", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface d, int which) {
                mActionListener.makeNewScriptBtnClicked();
            }
        });
    }

    private void initDialogLoadingSpinner() {
        mDialogLoadingSpinner = new ProgressDialog(getActivity());
        mDialogLoadingSpinner.setTitle("Add Script");
        mDialogLoadingSpinner.setCancelable(false);
        mDialogLoadingSpinner.setMax(1);
        mDialogLoadingSpinner.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        // Set the progress dialog background color
        //mDialogLoadingSpinner.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFD4D9D0")));
        // 다이얼로그에 돌아가는 원이 , 무한진행상태를 표시하게 됩니다.
        mDialogLoadingSpinner.setIndeterminate(true);
    }

    private void initDialogResult() {
        mDialogResult = new AlertDialog.Builder(getActivity());
        mDialogResult.setTitle("스크립트 추가 완료");
        // ok, cancel button 클릭 이벤트.
        mDialogResult.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface d, int which) {
                d.dismiss(); // 다이알로그 닫기
            }
        });
    }

    private void initDialogSelectScript() {
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
}
