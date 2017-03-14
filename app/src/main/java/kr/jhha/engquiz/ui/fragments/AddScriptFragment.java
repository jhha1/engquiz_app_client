package kr.jhha.engquiz.ui.fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import kr.jhha.engquiz.R;
import kr.jhha.engquiz.backend_logic.FileManager;
import kr.jhha.engquiz.backend_logic.ScriptManager;
import kr.jhha.engquiz.backend_logic.Script;
import kr.jhha.engquiz.ui.fragments.quizgroups.QuizGroupAdapter;

/**
 * Created by Junyoung on 2016-06-23.
 */

//public class AddScriptFragment extends Fragment implements ParseScriptPacket.AsyncHandler
public class AddScriptFragment extends Fragment
{
    // 현재 파일 위치 출력 뷰
    private TextView mFileLocationView = null;

    // 파일 리스트 뷰
    private ListView mItemListView = null;
    private List<String> mItems = null;     // 리스트 뷰의 각 row에 출력될 파일명
    private List<String> mFilepath = null;  // 파일의 path + name (절대위치)
    private int mSelectedScriptPosition = -1; // 선택된 파일 index.
    private String mCurrentDirectoryPath;  // 선택된 파일의 폴더 위치

    // 다이알로그
    private AlertDialog.Builder mDialogConfirm = null;  // 스크립트 추가 confirm
    private ProgressDialog mDialogLoadingSpinner = null; // 동글뱅이 로딩 중(스크립트 추가중.. ) 다이알로그. 서버통신때씀
    private AlertDialog.Builder mDialogResult = null; // 스크립트 추가(파싱) 결과 다이알로그
    private AlertDialog.Builder mDialogModifyResult = null; // 개발자가 스크립트 결과 수정하는 다이알로그
    private AlertDialog.Builder mDialogReportError = null; // 정상적으로 스크립트 추가가 안됬을때 유저가 버그리포트보내는 다이알로그

    // 카카오톡 다운로드 폴더 내 파일 리스트 가져오기
    // 수강생들은 카톡단톡방에서 영어스크립트를 다운로드하므로.
    private final String defaultDirectoryName = FileManager.KaKaoDownloadFolder_AndroidPath;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        initDialog();
    }

    private void initDialog()
    {
        mDialogConfirm = new AlertDialog.Builder( getActivity() );
        mDialogConfirm.setIcon(android.R.drawable.alert_dark_frame);
        mDialogConfirm.setTitle("Add Script");
        mDialogConfirm.setCancelable(false); //  Back키 눌렀을 경우 Dialog Cancle 여부 설정
        // 취소 버튼 클릭 이벤트.
        mDialogConfirm.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface d, int which) {
                // 다이알로그 닫기
                d.dismiss();
            }
        });
        // 다이알로그 메세지
        //mDialogMessageConform = getActivity().getResources().getString(R.string.add_script_dialog_msg);
        // "스크립트 형태 변환중.. PDF -> 게임용"
        // "변환 결과. (결과 값들 .. )   변환 결과가 이상합니까? 예 클릭 -> 개발자에게 전송 버튼을 클릭해 주세요. 개발자가 확인 후 수정합니다. 수정된 결과는 앱 재접속시 씽크 메뉴의 업데이트 알람으로 확인하실 수 있습니다.
        // 업데이트 알람을 통해 수정된 스크립트를 받아 게임을 즐길 수 있습니다.


        mDialogLoadingSpinner = new ProgressDialog( getActivity() );
        mDialogLoadingSpinner.setTitle("Add Script");
        mDialogLoadingSpinner.setCancelable(false);
        mDialogLoadingSpinner.setMax(1);
        mDialogLoadingSpinner.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        // Set the progress dialog background color
        //mDialogLoadingSpinner.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFD4D9D0")));
        // 다이얼로그에 돌아가는 원이 , 무한진행상태를 표시하게 됩니다.
        mDialogLoadingSpinner.setIndeterminate(true);

        mDialogResult = new AlertDialog.Builder( getActivity() );
        mDialogResult.setTitle("Add Script");
        // ok, cancel button 클릭 이벤트.
        mDialogResult.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface d, int which) {
                d.dismiss(); // 다이알로그 닫기
            }
        });

        mDialogModifyResult = new AlertDialog.Builder( getActivity() );
        mDialogModifyResult.setTitle("Modify Script");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.content_add_script, container, false);

        // 1. 폴더 위치 칸에, 현재 폴더 위치 출력
        mFileLocationView = (TextView) view.findViewById(R.id.add_script_file_location);
        mCurrentDirectoryPath = ScriptManager.getInstance().getAbsoluteFilePath(defaultDirectoryName);
        mFileLocationView.setText( "현재 폴더 위치: " + mCurrentDirectoryPath );

        // 2. 리스트 뷰: 디렉토리와 파일 하이락키를 출력
        mItemListView = (ListView) view.findViewById(R.id.add_script_filebrower);
        mItemListView.clearChoices(); // 기존에 선택했던 것 초기화
        // 파일 리스트 클릭 이벤트 연결
        mItemListView.setOnItemClickListener( mOnItemClickListener );
        // 파일 리스트에 데이터 채우기
        showCurrentFilesInDirectory(defaultDirectoryName); // 함수 내부에서 아답터 연결

        // 3. 추가할 스크립트 선택 후, OK 버튼 클릭 이벤트.
        Button mOKButton = (Button) view.findViewById(R.id.add_script_btn_ok);
        mOKButton.setOnClickListener(mClickListener);

        view.bringToFront(); // 리스트가 길어질 경우 가장 위로 스크롤.
        return view;
    }

    // 추가할 스크립트 선택 리스트 뷰: 리스트의 row 선택 이벤트.
    // 상위 or 하위 디렉토리 선택시, 디렉토리 다시 그리기
    AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView parent, View v, int position, long id) {

            // get TextView's Text.
            String strText = (String) parent.getItemAtPosition(position) ;

            String fileFullPath = mFilepath.get(position);
            File file = new File( fileFullPath );
            if ( false == file.isDirectory() ) {
                mSelectedScriptPosition = position;
                return;
            }
            if ( false == file.canRead() ) {
                String msg =  "[" + file.getName() + "] 폴더는 열 수 없습니다.";
                Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
                return;
            }

            // 리스트 뷰 상위에 현재 폴더 위치 출력.
            mCurrentDirectoryPath = fileFullPath;
            mFileLocationView.setText( "현재 폴더 위치: " + fileFullPath );
            // 리스트 뷰에 현재 디렉토리 하이락키 그리기
            showCurrentFilesInDirectory( fileFullPath );
        }
    };

    // 리스트 뷰에 현재 디렉토리 하이락키 그리기
    private void showCurrentFilesInDirectory(String dirName ) {
        File files[] = FileManager.getInstance().listFiles(dirName);
        if (files == null) {
            Log.e("Tag", "Directory is null. mFilepath:" + dirName);
            return;
        }

        mItems = new ArrayList<String>();    // 리스트에 보여질 파일이름 리스트
        mFilepath = new ArrayList<String>(); // 파일이름에 해당하는 실제 파일로케이션

        // 현재 디렉토리가 루트가 아니면, 현재폴더 상위 디렉토리를 뷰 리스트에 삽입
        if (false == FileManager.getInstance().isRootDirectory(dirName)) {
            mItems.add("../"); // 상위 디렉토리로 이동 텍스트
            String parentDir = FileManager.getInstance().getParentDirectoryName(dirName);
            mFilepath.add(parentDir); // 상위 디렉토리 경로 삽입
        }

        // 폴더 내 파일들을 뷰 리스트에 삽입
        for (File file : files) {
            // 디렉토리와 pdf만 표시
            boolean bPDFFile = file.getName().contains(".pdf");
            boolean bHideFile = ( false == file.isDirectory() && false == bPDFFile );
            if( bHideFile )
                continue;

            mFilepath.add(file.getPath());  // file path = file dir + file name
            String fileName = (file.isDirectory()) ? file.getName() + "/" : file.getName();
            mItems.add(fileName);
        }

        // 디렉토리 위치변경에 의한 UI 리프레시
        //  ->  mAdapter.notifyDataSetChanged(); 가 안먹혀서 이렇게 함.
        //      adapter내부 데이터가 변경 될때 mAdapter.notifyDataSetChanged()가 먹힌다고 함.
        //      ArrayAdapter는 내부데이터 변경이 인지 않된다는건데 잘 이해는 안감.
        int drawable = R.layout.content_textstyle_listview_checked_simple;
        ArrayAdapter mAdapter = new ArrayAdapter<String>(getActivity(), drawable, mItems);
        // 파일 리스트 아답터 연결
        mItemListView.setAdapter(mAdapter);
    }


    // 추가할 스크립트 선택 후, OK 버튼 이벤트.
    //  -> 스크립트 추가 확인 다이알로그를 띄운다.
    Button.OnClickListener mClickListener = new View.OnClickListener()
    {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.add_script_btn_ok:
                    final String filename = mItems.get( mSelectedScriptPosition );
                    // 파일 형식 체크
                    boolean bOk = ScriptManager.getInstance().checkFileFormat(filename);
                    if( false == bOk ) {
                        // 오류 다이알로그 띄우기
                        String msg = "'pdf' 형식의 파일만 가능합니다";
                        AlertDialog.Builder d = new AlertDialog.Builder( getActivity() );
                        d.setTitle("Warning").setMessage( msg );
                        d.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int which) {
                                d.dismiss();
                            }
                        });
                        d.show();
                        break;
                    }
                    // 스크립트 추가 다이알로그 띄우기
                    mDialogConfirm.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface d, int which) {
                            // 스크립트 추가. async 별도 쓰레드로 서버에 요청.
                            new AddScriptTask(mCurrentDirectoryPath, filename);
                        }
                    });
                    float fileMegaSize = FileManager.getInstance().getFileMegaSize(mCurrentDirectoryPath, filename, 1);
                    String dialogMsg = filename + "를 추가합니다. " +
                            "\n스크립트 분석을 위한 서버로의 파일 업/다운로드 과정에서  "
                            + fileMegaSize +"~"+ fileMegaSize*2 +"MB의 데이터가 소모될 수 있으니, "
                            +"WIFI 환경에서 이용하시길 권장합니다."
                            +"\n\n스크립트를 추가하시겠습니까?";
                    mDialogConfirm.setMessage( dialogMsg );
                    mDialogConfirm.show();
                    break;
            }
        }
    };

    class AddScriptTask
    {
        /*
         http 통신은 main thread 에서 못하기 때문에(안드로이드 기조)
        별도 thread를 만들어 통신.
         구글링 결과 보통 http통신에 AsyncTask를 쓰길래 나도;) (but, AsyncTask는 작은 일단위에 사용한다는데, 단위기준은 안찾아봤다)
         첨엔 이 AsyncTask 부분을 통신패키지 위치쪽으로 뺐다가, doInBackground()를 제외한 함수들은 UI그리는데 써서
         다시 UI코드에 넣었다. backend(통신 등)와 ui 코드를 분리 시키려 했는데,, 뭔가 쉽지않다.

        # AsyncTask<Object,Integer,Object> parameters :
         1. Object: doInBackground의 파라메터. doInBackground의 return 파라메터는  onPostExecute의 인자값 파라메터
         2. Integer: onProgressUpdate 파라메터
         3. Object: onPostExecute 파라메터.
        */
        private AsyncTask<Object, Integer, Object> task = null;
        private boolean mIsParseOK = false;
        private Integer mParsedScriptIndex = 0;

        public AddScriptTask( String filepath, String filename )
        {
            // AsyncTask는 재사용이 불가하기 때문에, 매번 새로 생성해야 한다
            task = newTask();
            // 별도 thread 시작. Parameter는 doInBackground()의 인자로.
            task.execute( filepath, filename );
        }

        private AsyncTask<Object, Integer, Object> newTask()
        {
            return new AsyncTask<Object, Integer, Object>()
            {
                @Override
                protected void onPreExecute() {
                    //mDialogConfirm.dismiss();
                }

                @Override
                // 별도 쓰레드로 돌아가는 함수.
                protected Object doInBackground( Object... params ) {
                    publishProgress(0); // onProgressUpdate()를 호출한다.
                    if (checkParams(params) == false) {
                        System.out.println("ERR invalid param");
                        return null;
                    }
                    String pdfFilepath = (String) params[0];
                    String pdfFilename = (String) params[1];
                    // 서버를 통해 스크립트 파싱받고, 결과 스크립트를 파일과 맵에 저장.
                    return ScriptManager.getInstance().addScript( pdfFilepath, pdfFilename );
                }

                // doInBackground()가 동작하는 동안 Ui작업
                // Main thread 에서 돌아가는 함수
                @Override
                protected void onProgressUpdate(Integer... progressState) {
                    // 로딩스피너 다이알로그 보이기.
                    mDialogLoadingSpinner.setMessage("서버에서 스크립트 분석중입니다..");
                    mDialogLoadingSpinner.show();
                }

                // 모두 작업을 마치고 실행할 일 (메소드 등등)
                // Main thread 에서 돌아가는 함수
                @Override
                protected void onPostExecute(final Object result) // TODO exception과 script 둘다 넘어오게.
                {
                    mDialogLoadingSpinner.dismiss(); // 로딩스피너 다이알로그 닫기

                    // 에러 처리
                    String dialogMsg = "";
                    boolean bAdmin = true;
                    if( result == null || result instanceof Exception ) {
                        // 개발자에겐 스크립트 파싱 결과 텍스트를 보여줌.
                        if( bAdmin ) {
                            dialogMsg = (result == null)? null : ((Exception) result).getMessage();
                        } // 유저에겐 안내메세지
                        else {
                            dialogMsg = "오류가 발생했습니다. " +
                                    "\nReport 버튼을 클릭하시면 개발자에게 에러내용이 보고됩니다." +
                                    "\n개발자가 확인 및 수정한 내역은 알람메뉴에서 보실 수 있습니다.";
                            mDialogResult.setNegativeButton("Report", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface d, int which) {

                                    d.dismiss(); // 다이알로그 닫기
                                }
                            });
                        }
                        mDialogResult.setMessage( dialogMsg );
                        mDialogResult.show();
                        return;
                    }

                    // Default Quiz Group에 넣어주기
                    final QuizGroupAdapter adapter = QuizGroupAdapter.getInstance();
                    if( result instanceof Script ) {
                        mParsedScriptIndex = ((Script) result).index;
                        adapter.addScriptIntoDefaultQuizGroup( mParsedScriptIndex );
                        mIsParseOK = true;
                    }

                    // 결과 다이알로그
                    if ( bAdmin )
                    {
                        // 개발자에겐 스크립트 파싱 결과 텍스트를 보여줌.
                        if( result instanceof Script )
                            dialogMsg = ((Script) result).toString();

                        mDialogResult.setNegativeButton("Modify", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int which) {
                                // 파싱결과가 이상하면 수정
                                EditText scriptText = new EditText(getActivity());
                                scriptText.setText(result.toString());
                                mDialogModifyResult.setView(scriptText);
                                mDialogModifyResult.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface d, int which) {
                                        // 수정결과를 메모리맵에 반영.
                                        boolean bOK = ScriptManager.getInstance().replaceScript( (Script)result );
                                        if( bOK == false ) {
                                            Toast.makeText(getActivity(), "수정결과 적용에러", Toast.LENGTH_SHORT).show();
                                        }
                                        d.dismiss(); // 다이알로그 닫기
                                    }
                                });
                                mDialogModifyResult.show();
                                d.dismiss(); // 다이알로그 닫기
                            }
                        });
                    }
                    else
                    {
                        // 유저에겐 스크립트 추가완료 메세지만.
                        if( result instanceof Script )
                            dialogMsg = "스크립트가 추가되었습니다. " +
                                    "\n튜토리얼에서 추가된 스크립트를 게임에 적용하는 법을 확인 할 수 있습니다.";
                    }
                    mDialogResult.setMessage( dialogMsg );
                    mDialogResult.show();
                }

                // 외부에서 강제로 취소할때 호출되는 메소드
                @Override
                protected void onCancelled() {
                    task.cancel(true);
                    Toast.makeText(getActivity(), "취소되었습니다", Toast.LENGTH_SHORT).show();
                }

                private boolean checkParams(Object... params) {
                    if (params == null) {
                        System.out.println("ERR no param.");
                        return false;
                    }
                    if (params.length < 2) {
                        System.out.println("ERR params.length is false");
                        return false;
                    }
                    return true;
                }
            };
        }
    }

}
