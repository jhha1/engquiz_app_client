package kr.jhha.engquiz.presenter_view.scripts.regular;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import kr.jhha.engquiz.model.local.Script;
import kr.jhha.engquiz.model.local.ScriptRepository;
import kr.jhha.engquiz.model.local.UserRepository;
import kr.jhha.engquiz.util.Parsor;
import kr.jhha.engquiz.util.exception.EResultCode;
import kr.jhha.engquiz.util.FileHelper;
import kr.jhha.engquiz.util.ui.MyLog;

/**
 * Created by thyone on 2017-03-15.
 */

public class AddScriptOtherDirectoryPresenter implements AddScriptOtherDirectoryContract.ActionsListener {

    private final AddScriptOtherDirectoryContract.View mView;
    private final ScriptRepository mModel;

    private List<String> mItems = null;     // 리스트 뷰의 각 row에 출력될 파일명
    private List<String> mFilepath = null;  // 파일의 path + name (절대위치)

    private String mCurrentDirectoryPath;  // 선택된 파일의 폴더 위치
    private String mSelectedScriptName = null; // 선택된 파일 이름


    // 카카오톡 다운로드 폴더 내 파일 리스트 가져오기
    // 수강생들은 카톡단톡방에서 영어스크립트를 다운로드하므로.
    private final String defaultDirectoryName = FileHelper.KaKaoDownloadFolder_AndroidPath;

    public static String TEXT_ALREADY_ADDED = "[추가됨] ";

    public AddScriptOtherDirectoryPresenter(AddScriptOtherDirectoryContract.View view, ScriptRepository model ) {
        mView = view;
        mModel = model;
    }

    @Override
    public void initDirectoryLocationAndAvailableFiles() {
        mCurrentDirectoryPath = mModel.getAbsoluteFilePath(defaultDirectoryName);
        updataDirectory(mCurrentDirectoryPath, defaultDirectoryName);
    }

    @Override
    public void onFileListItemClick(int position) {
        String fileFullPath = mFilepath.get(position);
        File file = new File( fileFullPath );
        if ( false == file.canRead() ) {
            mView.showMsg( 1, file.getName() );
            return;
        }

        if ( file.isDirectory() ) {
            // it's dir.
            mCurrentDirectoryPath = fileFullPath;
            updataDirectory(mCurrentDirectoryPath, fileFullPath);
            return;
        } else {
            // it's file.
            setSelectedScriptName( position );
        }
    }

    private void updataDirectory( String dirPath, String dirName ){
        // 리스트 뷰 상위에 현재 폴더 위치 출력.
        mView.showCurrentDirectoryPath( dirPath );
        // 현재 디렉토리 하이락키 로드 & 리스트뷰에 출력
        loadFileListInDirectory(dirName);
    }

    // 현재 디렉토리 하이락키 로드 & 리스트뷰에 출력
    private void loadFileListInDirectory( String dirName ) {
        File files[] = FileHelper.getInstance().listFiles(dirName);
        if (files == null) {
            MyLog.e("Directory is null. mFilepath:" + dirName);
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
            // 디렉토리와 pdf만 표시
            boolean bPDFFile = file.getName().contains(".pdf");
            boolean bHideFile = ( false == file.isDirectory() && false == bPDFFile );
            if( bHideFile )
                continue;

            mFilepath.add(file.getPath());  // file path = file dir + file name
            String fileName = (file.isDirectory()) ? file.getName() + "/" : file.getName();
            if(file.isDirectory()){
                fileName = file.getName() + "/";
            } else {
                fileName = hasParsedScript(file.getName()) ? TEXT_ALREADY_ADDED+file.getName() : file.getName();
            }
            mItems.add(fileName);
        }
        mView.showFileListInDirectory( mItems );
    }

    private boolean hasParsedScript( String filenmae ){
        String scriptName = Parsor.removeExtensionFromScriptTitle(filenmae, ".pdf");
        return mModel.hasParsedScript(scriptName);
    }

    @Override
    public void scriptSelected()
    {
        checkFile();
    }

    private void checkFile(){
        final String filename = mSelectedScriptName;
        if( false == mModel.checkFileFormat(filename) ) { // 파일 형식 체크
            mView.showErrorDialog(1);
            return;
        }

        if( mModel.checkDoubleAdd(filename) ){
            String addedTitle = mModel.getFileNameRemovedDoubleDownloadTagAndPDFExtension(filename);
            String errmsg =  "이미 추가된 스크립트에요.\n\n\n"
                    + "# 추가 되어있는 스크립트 : \n"
                    + addedTitle +"\n\n"
                    + "# 현재 추가하려는 스크립트 :\n"
                    + filename;
            mView.showErrorDialog(errmsg);
        } else {
            showFileSizeConfirmDialog();
        }
    }

    private void showFileSizeConfirmDialog(){
        String fileName = mSelectedScriptName;
        final FileHelper file = FileHelper.getInstance();
        float filesize = file.getFileMegaSize(mCurrentDirectoryPath, fileName );
        mView.showAddScriptConfirmDialog(fileName, filesize);
    }

    @Override
    public void addScript( String pdfFileName) {
        String msgForLog = "pdfFilePath:"+ mCurrentDirectoryPath + ", pdfFileName:"+pdfFileName;
        mView.showLoadingDialog();
        Integer userId = UserRepository.getInstance().getUserID();
        mModel.addPDFScript( userId,
                mCurrentDirectoryPath,
                pdfFileName,
                onAddScriptCallback( msgForLog ) );
        mView.closeLoadingDialog();
    }

    private ScriptRepository.ParseScriptCallback onAddScriptCallback(final String msgForLog) {
        return new ScriptRepository.ParseScriptCallback(){

            @Override
            public void onSuccess( Script script ) {
                MyLog.d("addPDFScript onSuccess()  user: " + msgForLog);

                mView.showAddScriptSuccessDialog();
            }

            @Override
            public void onFail(EResultCode resultCode) {
                MyLog.d("addPDFScript onFail() " +
                        "resultCode:"+resultCode.toString()+", user: " + msgForLog);
                switch (resultCode) {
                    case SCRIPT__NO_HAS_KR_OR_EN:
                        mView.showErrorDialog(4);
                        break;
                    default:
                        mView.showErrorDialog(2);
                        break;
                }
            }
        };
    }

    private void setSelectedScriptName( int position ) {
        mSelectedScriptName = mItems.get( position );
    }
}
