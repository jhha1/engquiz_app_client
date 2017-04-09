package kr.jhha.engquiz.addscript;

import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import kr.jhha.engquiz.data.local.QuizFolderRepository;
import kr.jhha.engquiz.data.local.Script;
import kr.jhha.engquiz.data.local.ScriptRepository;
import kr.jhha.engquiz.data.local.UserRepository;
import kr.jhha.engquiz.data.remote.EResultCode;
import kr.jhha.engquiz.data.local.QuizFolder;
import kr.jhha.engquiz.util.FileHelper;

/**
 * Created by thyone on 2017-03-15.
 */

public class AddScriptPresenter implements AddScriptContract.ActionsListener {

    private final AddScriptContract.View mView;
    private final ScriptRepository mModel;
    private final QuizFolderRepository mQuizFolderModel;

    private List<String> mItems = null;     // 리스트 뷰의 각 row에 출력될 파일명
    private List<String> mFilepath = null;  // 파일의 path + name (절대위치)

    private String mCurrentDirectoryPath;  // 선택된 파일의 폴더 위치
    private String mSelectedScriptName = null; // 선택된 파일 이름

    private boolean bNewQuizFolder = false;
    private Integer mSelectedQuizFolderId = -1;
    private String mSelectedQuizFolderName = null;

    // 카카오톡 다운로드 폴더 내 파일 리스트 가져오기
    // 수강생들은 카톡단톡방에서 영어스크립트를 다운로드하므로.
    private final String defaultDirectoryName = FileHelper.KaKaoDownloadFolder_AndroidPath;

    public AddScriptPresenter(AddScriptContract.View view, ScriptRepository model ) {
        mView = view;
        mModel = model;
        mQuizFolderModel = QuizFolderRepository.getInstance();
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
            // 디렉토리와 pdf만 표시
            boolean bPDFFile = file.getName().contains(".pdf");
            boolean bHideFile = ( false == file.isDirectory() && false == bPDFFile );
            if( bHideFile )
                continue;

            mFilepath.add(file.getPath());  // file path = file dir + file name
            String fileName = (file.isDirectory()) ? file.getName() + "/" : file.getName();
            mItems.add(fileName);
        }
        mView.showFileListInDirectory( mItems );
    }



    @Override
    public void scriptSelected()
    {
        final String filename = mSelectedScriptName;
        boolean bOk = mModel.checkFileFormat(filename); // 파일 형식 체크
        if( !bOk ) {
            mView.showErrorDialog(1);
            return;
        }
        getQuizFolderList();
    }

    private void getQuizFolderList(){
        // 퀴즈폴더 리스트를 로드하기 전에
        // Add Script에서 퀴즈폴더리스트를 참조하는 경우(1)에 대비해
        // 아래 함수(2)를 사용해 리스트를 받아온다.
        //
        // (1) 유저가 퀴즈폴더메뉴를 클릭하기 전에 Add script를 하는 경우.
        //      퀴즈폴더메뉴를 클릭하는 시점에 퀴즈폴더데이터로드.
        // (2) 아래함수: 메모리에 리스트가 있으면 반환 or 없으면 서버로부터 리스트를 받음
        mQuizFolderModel.getQuizFolders( new QuizFolderRepository.GetQuizFolderCallback(){

            @Override
            public void onSuccess(List<QuizFolder> quizFolders) {
                // 모든 로직이 성공하여 mQuizFolderModel에 퀴즈폴더리스트가 저장되어있다.
                // mQuizFolderModel에서 필요한 데이터를 가져다 쓴다.
                List<String> quizFolderList = mQuizFolderModel.getQuizFolderNames();
                if( quizFolderList == null || quizFolderList.isEmpty() ){
                    mView.showNeedMakeQuizFolderDialog();
                } else {
                    mView.showQuizFolderSelectDialog( quizFolderList );
                }
            }

            @Override
            public void onFail(EResultCode resultCode) {
                mView.showErrorDialog(3);
            }
        });
    }

    @Override
    public void quizFolderSelected( String quizFolderName ) {
        if( quizFolderName.equals( QuizFolder.TEXT_NEW_FOLDER) ){
            mView.showNewQuizFolderTitleInputDialog();
        } else {
            bNewQuizFolder = false;
            callConfirmDialog( quizFolderName, bNewQuizFolder );
        }
    }

    @Override
    public void newQuizFolderTitleInputted(String quizFolderName ) {
        bNewQuizFolder = true;
        callConfirmDialog( quizFolderName, bNewQuizFolder );
    }

    private void callConfirmDialog( String quizFolderName, boolean bNewQuizFolder ) {
        mSelectedQuizFolderName = quizFolderName;
        if( bNewQuizFolder == false ) {
            mSelectedQuizFolderId = mQuizFolderModel.getQuizFolderIdByName( quizFolderName );
        }
        String fileName = mSelectedScriptName;
        final FileHelper file = FileHelper.getInstance();
        float filesize = file.getFileMegaSize(mCurrentDirectoryPath, fileName, 1);
        mView.showAddScriptConfirmDialog(fileName, filesize);
    }

    @Override
    public void addScript( String pdfFileName) {
        String msgForLog = "pdfFilePath:"+ mCurrentDirectoryPath + ", pdfFileName:"+pdfFileName;
        mView.showLoadingDialog();
        Integer userId = UserRepository.getInstance().getUserID();
        mModel.addScript( userId,
                mCurrentDirectoryPath,
                pdfFileName,
                onAddScriptCallback( bNewQuizFolder,
                        mSelectedQuizFolderId,
                        mSelectedQuizFolderName,
                        mSelectedScriptName,
                        msgForLog) );
        mView.closeLoadingDialog();
    }

    private ScriptRepository.ParseScriptCallback onAddScriptCallback( final boolean bNewQuizFolder,
                                                                        final Integer quizFolderId,
                                                                        final String quizFolderName,
                                                                        final String scriptName,
                                                                        final String msgForLog) {
        return new ScriptRepository.ParseScriptCallback(){

            @Override
            public void onSuccess( Script script ) {
                Log.i("AppContent", "addScript onSuccess()  user: " + msgForLog);

                if( bNewQuizFolder ) {
                    addQuizFolder(quizFolderName, scriptName);
                } else {
                    addQuizFolderDetail(quizFolderId, scriptName);
                }
                mView.showAddScriptSuccessDialog( quizFolderName );
            }

            @Override
            public void onFail(EResultCode resultCode) {
                Log.e("AppContent", "addScript onFail() UnkownERROR. " +
                        "resultCode:"+resultCode.toString()+", user: " + msgForLog);
                switch (resultCode) {
                    default:
                        mView.showErrorDialog(2);
                        break;
                }
            }
        };
    }

    private void addQuizFolder( String quizFolderName, String scriptName ) {
        Log.i("AppContent", "AddScriptPresenter addScriptIntoQuizFolder() called");
        Integer userId = UserRepository.getInstance().getUserID();
        Integer scriptId = mModel.getScriptIdByTitle(scriptName);
        List<Integer> scriptIds = new LinkedList<>();
        scriptIds.add(scriptId);
        mQuizFolderModel.addQuizFolder( userId, quizFolderName, scriptIds, onAddQuizFolder(quizFolderName) );
    }

    private QuizFolderRepository.AddQuizFolderCallback onAddQuizFolder(final String quizFolderName ) {
        return new QuizFolderRepository.AddQuizFolderCallback( ){
            @Override
            public void onSuccess(List<QuizFolder> updatedQuizFolders) {
                mView.showAddScriptSuccessDialog(quizFolderName);
            }
            @Override
            public void onFail(EResultCode resultCode) {
            }
        };
    }

    private void addQuizFolderDetail( Integer quizFolderId, String scriptName ) {
        Log.i("AppContent", "AddScriptPresenter addQuizFolderDetail() called");
        Integer userId = UserRepository.getInstance().getUserID();
        Integer scriptId = mModel.getScriptIdByTitle(scriptName);
        mQuizFolderModel.addQuizFolderDetail( quizFolderId, scriptId, onAddQuizFolderDetail(quizFolderId) );
    }

    private QuizFolderRepository.AddQuizFolderScriptCallback onAddQuizFolderDetail(final Integer quizFolderId ) {
        return new QuizFolderRepository.AddQuizFolderScriptCallback(){
            @Override
            public void onSuccess(List<Integer> updatedScriptIds) {
                String quizFolderName = mQuizFolderModel.getQuizFolderNameById(quizFolderId);
                mView.showAddScriptSuccessDialog(quizFolderName);
            }
            @Override
            public void onFail(EResultCode resultCode) {
            }
        };
    }

    private void setSelectedScriptName( int position ) {
        mSelectedScriptName = mItems.get( position );
    }
}
