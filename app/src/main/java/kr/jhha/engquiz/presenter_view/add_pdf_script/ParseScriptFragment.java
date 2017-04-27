package kr.jhha.engquiz.presenter_view.add_pdf_script;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import kr.jhha.engquiz.R;
import kr.jhha.engquiz.model.local.QuizFolder;
import kr.jhha.engquiz.model.local.ScriptRepository;
import kr.jhha.engquiz.presenter_view.MyToolbar;
import kr.jhha.engquiz.util.StringHelper;
import kr.jhha.engquiz.util.ui.Etc;
import kr.jhha.engquiz.util.ui.MyDialog;

import static kr.jhha.engquiz.presenter_view.FragmentHandler.EFRAGMENT.ADD_SCRIPT;


/**
 * Created by Junyoung on 2016-06-23.
 */

public class ParseScriptFragment extends Fragment implements ParseScriptContract.View {
    private ParseScriptContract.ActionsListener mActionListener;

    // 현재 파일 위치 출력 뷰
    private TextView mFileLocationView = null;
    // 파일 리스트 뷰
    private ListView mItemListView = null;
    // 다이알로그
    private ProgressDialog mDialogLoadingSpinner = null; // 동글뱅이 로딩 중(스크립트 추가중.. ) 다이알로그. 서버통신때씀

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_add_script, container, false);

        setUpToolBar();

        // presenter에 공용 클래스 변수들이 사용되므로, 뷰가 새로 그려질때마다 presenter를 새로 생성하도록 한다.
        mActionListener = new ParseScriptPresenter(this, ScriptRepository.getInstance());


        // 현재 폴더 위치 출력할 폴더 위치 칸
        mFileLocationView = (TextView) view.findViewById(R.id.add_script_file_location);
        // 리스트 뷰: 디렉토리와 파일 하이락키를 출력
        mItemListView = (ListView) view.findViewById(R.id.add_script_filebrower);
        mItemListView.clearChoices(); // 기존에 선택했던 것 초기화

        // 파일 리스트 클릭 이벤트 연결
        mItemListView.setOnItemClickListener(mOnItemClickListener);
        // 추가할 스크립트 선택 후, OK 버튼 클릭 이벤트.
        Button mOKButton = (Button) view.findViewById(R.id.add_script_btn_ok);
        mOKButton.setOnClickListener(mClickListener);

        // 리스트에 출력할 데이터 초기화
        mActionListener.initDirectoryLocationAndAvailableFiles();

        view.bringToFront(); // 리스트가 길어질 경우 가장 위로 스크롤.
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void setUpToolBar(){
        MyToolbar.getInstance().setToolBar(ADD_SCRIPT);
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
                case R.id.add_script_btn_ok:
                    mActionListener.scriptSelected();
                    break;
            }
        }
    };

    @Override
    public void showCurrentDirectoryPath(String path) {
        mFileLocationView.setText(
                getString(R.string.add_pdf_script__cur_folder_path)
                + path);
    }

    @Override
    public void showFileListInDirectory(List<String> fileList) {
        // 디렉토리 위치변경에 의한 UI 리프레시
        //  ->  mAdapter.notifyDataSetChanged(); 가 안먹혀서 이렇게 함.
        //      adapter내부 데이터가 변경 될때 mAdapter.notifyDataSetChanged()가 먹힌다고 함.
        //      ArrayAdapter는 내부데이터 변경이 인지 않된다는건데 잘 이해는 안감.
        int drawable = R.layout.common_listview_textstyle_checked_singlechoice;
        ArrayAdapter mAdapter = new ArrayAdapter<String>(getActivity(), drawable, fileList);
        // 파일 리스트 아답터 연결
        mItemListView.setAdapter(mAdapter);
    }

    @Override
    public void showMsg(int what, String arg) {
        String msg = null;
        switch (what) {
            case 1:
                msg = "[" + arg + "] 폴더는 열 수 없습니다.";
                break;
        }
        Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showErrorDialog(int what) {
        int msgId = 2;
        switch (what) {
            case 1:
                msgId = R.string.add_pdf_script__fail_only_allow_pdf_format;
                break;
            case 2:
                msgId = R.string.add_pdf_script__fail;
                break;
            case 3:
                msgId = R.string.show_folders__get_folders__fail_defaut;
                break;
            case 4:
                msgId = R.string.add_pdf_script__fail_only_has_en_or_kn;
                break;
        }

        MyDialog dialog = new MyDialog(getActivity());
        dialog.setTitle(getString(R.string.common__warning));
        dialog.setMessage( getString(msgId) );
        dialog.setPositiveButton();
        dialog.showUp();
    }

    @Override
    public void showNeedMakeQuizFolderDialog(){
        final MyDialog dialog = new MyDialog(getActivity());
        dialog.setTitle(getString(R.string.add_pdf_script__choice_folder));
        dialog.setMessage(getString(R.string.add_pdf_script__no_exist_folder));
        dialog.setNeutralButton(getString(R.string.add_pdf_script__make_new_folder_btn),
                new View.OnClickListener() {
            public void onClick(View arg0)
            {
                 mActionListener.quizFolderSelected( QuizFolder.TEXT_NEW_FOLDER );
                 dialog.dismiss();
            }});
        dialog.setNegativeButton();
        dialog.showUp();
    }

    @Override
    public void showQuizFolderSelectDialog( List<String> quizFolderList )
    {
        final MyDialog dialog = new MyDialog(getActivity());
        dialog.setTitle(getString(R.string.add_pdf_script__choice_folder));
        dialog.setNeutralButton(getString(R.string.add_pdf_script__make_new_folder_btn)
                , new View.OnClickListener() {
            public void onClick(View arg0)
            {
                    mActionListener.quizFolderSelected( QuizFolder.TEXT_NEW_FOLDER );
                    dialog.dismiss();
            }});
        dialog.setNegativeButton();

        // List Adapter 생성
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.common_listview_textstyle_checked_singlechoice);
        // adapter에 quiz folder list 집어넣기
        for( String quizFolderTitle : quizFolderList ){
            adapter.add(quizFolderTitle);
        }
        adapter.notifyDataSetChanged();

        // Dialog와 Adapter 연결
        dialog.setListView( getActivity(), adapter,  new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // 유저가 quiz folder 선택.
                String listItemTitle = adapter.getItem(id);
                mActionListener.quizFolderSelected( listItemTitle );
                dialog.dismiss();
            }
        } );
        dialog.showUp();
    }

    @Override
    public void showNewQuizFolderTitleInputDialog() {
        // AlertDialog 안에 있는 AlertDialog
        final MyDialog dialog = new MyDialog(getActivity());
        dialog.setTitle(getString(R.string.add_pdf_script__input_folder_name));
        dialog.setMessage(getString(R.string.add_pdf_script__folder_name_guide));
        final EditText inputQuizFolderTitle = Etc.makeEditText(getActivity());
        dialog.setEditText(inputQuizFolderTitle);
        dialog.setPositiveButton( new View.OnClickListener() {
                                      public void onClick(View arg0) {
                                          String inputText = inputQuizFolderTitle.getText().toString();
                                          mActionListener.newQuizFolderTitleInputted( inputText );
                                          dialog.dismiss();
                                      }
                                  });
        dialog.showUp();
    }

    @Override
    public void showAddScriptConfirmDialog(String filename, Float fileSize) {
        // 스크립트 추가 다이알로그 띄우기
        // 파일 사이즈가 이상할경우, UI에는 0.4MB로 표시 (업로드 + 다운로드)
        fileSize = (fileSize <= 0) ? 0.4f : fileSize;
        String dialogMsg = filename + "를 추가합니다. " +
                "\n스크립트 분석을 위한 서버로의 파일 업/다운로드 과정에서  "
                + fileSize + "MB의 데이터가 소모될 수 있으니, "
                + "WIFI 환경에서 이용하시길 권장합니다."
                + "\n\n스크립트를 추가하시겠습니까?";
        final String filenameDoubleChecked = StringHelper.isNull(filename) ? "스크립트" : filename;

        final MyDialog dialog = new MyDialog(getActivity());
        dialog.setTitle("스크립트 추가");
        dialog.setMessage( dialogMsg );
        dialog.setPositiveButton( new View.OnClickListener() {
            public void onClick(View arg0) {
                mActionListener.addScript(filenameDoubleChecked);
                dialog.dismiss();
            }});
        dialog.setNegativeButton();
        dialog.showUp();
    }

    @Override
    public void showLoadingDialog() {
        mDialogLoadingSpinner = new ProgressDialog(getActivity());
        mDialogLoadingSpinner.setTitle(getString(R.string.add_pdf_script__loading_dialog_title));
        mDialogLoadingSpinner.setCancelable(false);
        mDialogLoadingSpinner.setMax(1);
        mDialogLoadingSpinner.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        // Set the progress dialog background color
        //mDialogLoadingSpinner.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFD4D9D0")));
        // 다이얼로그에 돌아가는 원이 , 무한진행상태를 표시하게 됩니다.
        mDialogLoadingSpinner.setIndeterminate(true);
        // 로딩스피너 다이알로그 보이기.
        mDialogLoadingSpinner.setMessage(getString(R.string.add_pdf_script__loading_dialog_text));
        mDialogLoadingSpinner.show();
    }

    @Override
    public void closeLoadingDialog() {
        mDialogLoadingSpinner.dismiss(); // 로딩스피너 다이알로그 닫기
    }

    @Override
    public void showAddScriptSuccessDialog(String quizFolderName) {
        MyDialog dialog = new MyDialog(getActivity());
        dialog.setTitle(getString(R.string.add_pdf_script__succ_dialog_title));
        dialog.setMessage("스크립트가 추가되었습니다. " +
                        "\nFolders 메뉴의 [" + quizFolderName + "]을(를) " +
                        "클릭하면 확인하실 수 있습니다.");
        dialog.setPositiveButton();
        dialog.showUp();
    }

}
