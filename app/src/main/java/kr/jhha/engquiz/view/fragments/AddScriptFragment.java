package kr.jhha.engquiz.view.fragments;

import android.content.DialogInterface;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import kr.jhha.engquiz.R;
import kr.jhha.engquiz.controller.AddScript;
import kr.jhha.engquiz.controller.FileManager;
import kr.jhha.engquiz.model.Const;
import kr.jhha.engquiz.view.MainActivity;

/**
 * Created by Junyoung on 2016-06-23.
 */

public class AddScriptFragment extends Fragment
{
    private final String mTITLE = "Add Script";
    private AddScript mController = new AddScript();

    // 현재 파일 위치 출력 뷰
    private TextView mFileLocationView = null;

    // 파일 리스트 뷰
    private ListView mItemListView = null;
    private List<String> mItems = null;     // 리스트 뷰의 각 row에 출력될 파일명
    private List<String> mFilepath = null;  // 파일의 full path (절대위치)
    private int mSelectedScriptPosition = -1; // 선택된 파일 index.

    // 확인버튼
    private Button mOKButton = null;

    // 확인버튼 클릭시, 스크립트 추가 재확인 다이알로그
    private AlertDialog.Builder mDialog = null;
    private String mDialogMessage;

    // 카카오톡 다운로드 폴더 내 파일 리스트 가져오기
    // 수강생들은 카톡단톡방에서 영어스크립트를 다운로드하므로.
    private final String currentDirectoryName = Const.KaKaoDownloadFolder_AndroidPath;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        initDialog();
    }

    private void initDialog()
    {
        mDialog = new AlertDialog.Builder( getActivity() );
        mDialog.setIcon(android.R.drawable.alert_dark_frame);
        mDialog.setTitle("스크립트 추가 확인");
        mDialog.setCancelable(false); //  Back키 눌렀을 경우 Dialog Cancle 여부 설정
        // 확인 버튼 클릭 이벤트.
        mDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface d, int which) {
                // 스크립트 추가
                mController.addScript( getFileLocation() );
            }
        });
        // 취소 버튼 클릭 이벤트.
        mDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface d, int which) {
                // 다이알로그 닫기
                d.dismiss();
            }
        });

        // 다이알로그 메세지
        //mDialogMessage = getActivity().getResources().getString(R.string.add_script_dialog_msg);
        mDialogMessage ="\n\n" + "스크립트를 공유하시겠습니까?" +
                "\n" + "반드시! \"Sonya\"의 \"pdf(answer 포함)\"만 추가하셔야 합니다." +
                "\n\n" + "(스크립트는 클라우드 서버를 통해 분석 및 추가됩니다."+
                "클라우드 서버에 없는 스크립트면, 앱을 사용하는 모든 수강생에게 공유됩니다)";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.content_add_script, container, false);

        mFileLocationView = (TextView) view.findViewById(R.id.add_script_file_location);
        showCurrentPath( mController.getAbsoluteFilePath(currentDirectoryName) );

        // 2. 파일 리스트뷰
        mItemListView = (ListView) view.findViewById(R.id.add_script_filebrower);
        mItemListView.clearChoices(); // 기존에 선택했던 것 초기화
        // 파일 리스트 클릭 이벤트 연결
        mItemListView.setOnItemClickListener( mOnItemClickListener );
        // 파일 리스트에 데이터 채우기
        showCurrentDirectory( currentDirectoryName ); // 함수 내부에서 아답터 연결

        // 3. 확인 버튼 클릭 이벤트 셋팅
        mOKButton = (Button) view.findViewById(R.id.add_script_btn_ok);
        mOKButton.setOnClickListener(mClickListener);

        view.bringToFront(); // 리스트가 길어질 경우 가장 위로 스크롤.
        return view;
    }

    @Override
    public void onResume() {
        // 툴바에 현 프래그먼트 제목 출력
        ((MainActivity)getActivity()).setActionBarTitle( mTITLE );
        super.onResume();
    }

    // 리스트의 row 선택 이벤트.
    // 상위 or 하위 디렉토리 선택시, 디렉토리 다시 그리기
    AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView parent, View v, int position, long id) {

            // get TextView's Text.
            String strText = (String) parent.getItemAtPosition(position) ;

            String filename = mFilepath.get(position);
            File file = new File( filename );
            if ( false == file.isDirectory() ) {
                mSelectedScriptPosition = position;
                return;
            }
            if ( false == file.canRead() ) {
                String msg =  "[" + file.getName() + "] 폴더는 열 수 없습니다.";
                Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
                return;
            }

            showCurrentPath( filename );
            showCurrentDirectory( filename );
        }
    };

    // 확인, 취소 버튼 이벤트
    Button.OnClickListener mClickListener = new View.OnClickListener()
    {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.add_script_btn_ok:
                    String filename = mItems.get( mSelectedScriptPosition );
                    /*if( false == checkFileFormat(filename) ) {
                        showDialog("pdf 파일만 가능합니다");
                        break;
                    }*/

                    mDialog.setMessage( filename + mDialogMessage );
                    mDialog.show();
                    break;
            }
        }
    };

    private String getFileLocation() {
        return mFilepath.get( mSelectedScriptPosition );
    }

    // 현재 파일 위치 출력
    private void showCurrentPath( String path ) {
        mFileLocationView.setText( "현재 폴더 위치: " + path );
    }

    private void showCurrentDirectory(String dirName ) {
        File files[] = FileManager.getInstance().getFileList(dirName);
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
            mFilepath.add(file.getPath());
            String filename = (file.isDirectory()) ? file.getName() + "/" : file.getName();
            mItems.add(filename);
        }

        // 디렉토리 위치변경에 의한 UI 리프레시
        //  ->  mAdapter.notifyDataSetChanged(); 가 안먹혀서 이렇게 함.
        //      adapter내부 데이터가 변경 될때 mAdapter.notifyDataSetChanged()가 먹힌다고 함.
        //      ArrayAdapter는 내부데이터 변경이 인지 않된다는건데 잘 이해는 안감.
        int drawable = android.R.layout.simple_list_item_single_choice;
        ArrayAdapter mAdapter = new ArrayAdapter<String>(getActivity(), drawable, mItems);
        // 파일 리스트 아답터 연결
        mItemListView.setAdapter(mAdapter);
    }

}
