package kr.jhha.engquiz;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import kr.jhha.engquiz.util.FileHelper;
import kr.jhha.engquiz.model.local.ScriptRepository;

/**
 * Created by thyone on 2016-12-29.
 */

public class AddScriptDialog extends Dialog
{

    private TextView mTitleView = null;
    private TextView mFileLocationView = null;
    private ListView mItemListView = null;
    private Button mOKButton = null;
    private Button mCancelButton = null;

    private List<String> mItems = null;
    private List<String> mFilepath = null;

    private Context mContext = null; // == MainActivity

    private int mSelectedScriptPosition = -1;

    // 카카오톡 다운로드 폴더 내 파일 리스트 가져오기
    // 수강생들은 카톡단톡방에서 영어스크립트를 다운로드하므로.
    private final String currentDirectoryName = FileHelper.KaKaoDownloadFolder_AndroidPath;

    public AddScriptDialog (Context context) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.mContext = context;

        boolean test = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_add_script);

        // 다이얼로그 외부 화면 블럭킹
        blockWindow();


        mFileLocationView = (TextView) findViewById(R.id.add_script_file_location);
        showCurrentPath( ScriptRepository.getInstance().getAbsoluteFilePath(currentDirectoryName) );

        // 2. 파일 리스트뷰
        mItemListView = (ListView) findViewById(R.id.add_script_filebrower);
        // 파일 리스트 클릭 이벤트 연결
        mItemListView.setOnItemClickListener( mOnItemClickListener );
        // 파일 리스트에 데이터 채우기
        showCurrentDirectory( currentDirectoryName ); // 함수 내부에서 아답터 연결

        // 클릭 이벤트 셋팅
        mOKButton = (Button) findViewById(R.id.add_script_btn_ok);
       // mCancelButton = (Button) findViewById(R.quizFolderId.add_script_btn_cancel);
        if (mClickListener != null && mClickListener != null) {
            mOKButton.setOnClickListener(mClickListener);
            mCancelButton.setOnClickListener(mClickListener);
        }
    }

    // 다이얼로그 외부 화면 블럭킹.
    private void blockWindow()
    {
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f; // 블럭킹 색 투명도
        getWindow().setAttributes(lpWindow);
    }

    AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView parent, View v, int position, long id) {

            // toInt TextView's Text.
            String strText = (String) parent.getItemAtPosition(position) ;

            String filename = mFilepath.get(position);
            File file = new File( filename );
            if ( false == file.isDirectory() ) {
                mSelectedScriptPosition = position;
                return;
            }
            if ( false == file.canRead() ) {
                String msg =  "[" + file.getName() + "] 폴더는 열 수 없습니다.";
                Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
                return;
            }

            showCurrentPath( filename );
            showCurrentDirectory( filename );
        }
    };

    Button.OnClickListener mClickListener = new View.OnClickListener()
    {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.add_script_btn_ok:
                    String filename = mItems.get( mSelectedScriptPosition );
                    /*if( false == checkFileFormat(filename) ) {
                        showDoubleCheckDialog("pdf 파일만 가능합니다");
                        break;
                    }*/

                    showDoubleCheckDialog( filename, v );
                    //showDialog(DIALOG_YES_NO_LONG_MESSAGE);
                    break;
               // case R.quizFolderId.add_script_btn_cancel:
                    //dismiss();
                //    break;
            }
        }
    };

    // 스크립트 추가 재확인 다이알로그
    private void showDoubleCheckDialog(String filename, View v )
    {
        String msg = filename + " 스크립트를 추가하시겠습니까? " +
                     "\\n반드시 Sonya 선생님의 pdf만 추가하셔야 합니다. ";

        AlertDialog.Builder ab = new AlertDialog.Builder( mContext );
        ab.setIcon(android.R.drawable.alert_dark_frame);
        ab.setTitle("스크립트 추가");
        ab.setMessage(msg);
        ab.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            // 확인 버튼 클릭.
            public void onClick(DialogInterface dialog, int which) {
                // 스크립트 추가
                addScript( );
            }
        });
        ab.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            // 취소 버튼 클릭.
            public void onClick(DialogInterface dialog, int which) {
                // 다이알로그 닫기
                dialog.dismiss();
            }
        });
         ab.show();
    }

    // 스크립트 추가. 서버로 스크립트 전송
    private void addScript() {
        String filename = mItems.get( mSelectedScriptPosition );
        String filepath = mFilepath.get( mSelectedScriptPosition );
        //ScriptRepository.getInstance().parseScript( filename, filepath );
    }

    // 다이알로그 닫기
    private void closeDialog() {
        this.dismiss();
    }

    private void showCurrentPath( String path ) {
        mFileLocationView.setText( "Current Location: " + path );
    }

    private void showCurrentDirectory(String dirName ) {
        File files[] = FileHelper.getInstance().listFiles(dirName);
        if (files == null) {
            Log.e("Tag", "Directory is null. mFilepath:" + dirName);
            return;
        }

        mItems = new ArrayList<String>();    // 리스트에 보여질 파일이름 리스트
        mFilepath = new ArrayList<String>(); // 파일이름에 해당하는 실제 파일로케이션

        // 현재 디렉토리가 루트가 아니면, 현재폴더 상위 디렉토리를 뷰 리스트에 삽입
        if (false == FileHelper.getInstance().isRootDirectory(dirName)) {
            mItems.add("../"); // 상위 디렉토리로 이동 텍스트
            String parentDir = FileHelper.getInstance().getParentDirectoryName(dirName);
            mFilepath.add(parentDir); // 상위 디렉토리 경로 삽입
        }

        // 폴더 내 파일들을 뷰 리스트에 삽입
        for (File file : files) {
            mFilepath.add(file.getPath());
            String filename = (file.isDirectory()) ? file.getName() + "/" : file.getName();
            mItems.add(filename);
        }

        // 디렉토리 위치변경에 의한 UI 리프레시
        //  ->  mAdapter.notifyDataSetChanged(); 가 안먹혀서 이렇게 함.
        //      adapter내부 데이터가 변경 될때 mAdapter.notifyDataSetChanged()가 먹힌다고 함.
        //      ArrayAdapter는 내부데이터 변경이 인지 않된다는건데 잘 이해는 안감.
        int drawable = android.R.layout.simple_list_item_single_choice;
        ArrayAdapter mAdapter = new ArrayAdapter<String>(mContext, drawable, mItems);
        // 파일 리스트 아답터 연결
        mItemListView.setAdapter(mAdapter);
    }

}
