package kr.jhha.engquiz.addscript;

import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import kr.jhha.engquiz.data.local.QuizGroupModel;
import kr.jhha.engquiz.data.local.ScriptRepository;
import kr.jhha.engquiz.data.local.UserModel;
import kr.jhha.engquiz.data.remote.EResultCode;
import kr.jhha.engquiz.quizgroup.QuizGroupSummary;
import kr.jhha.engquiz.quizgroup.ShowQuizGroupsFragment;
import kr.jhha.engquiz.util.FileHelper;

/**
 * Created by thyone on 2017-03-15.
 */

public class AddScriptPresenter implements AddScriptContract.ActionsListener {

    private final AddScriptContract.View mView;
    private final ScriptRepository mModel;
    private final QuizGroupModel mQuizGroupModel;

    private List<String> mItems = null;     // 리스트 뷰의 각 row에 출력될 파일명
    private List<String> mFilepath = null;  // 파일의 path + name (절대위치)

    private String mCurrentDirectoryPath;  // 선택된 파일의 폴더 위치
    private String mSelectedScriptName = null; // 선택된 파일 이름

    private boolean bNewQuizGroup = false;
    private Integer mSelectedQuizGroupId = -1;
    private String mSelectedQuizGroupName = null;

    // 카카오톡 다운로드 폴더 내 파일 리스트 가져오기
    // 수강생들은 카톡단톡방에서 영어스크립트를 다운로드하므로.
    private final String defaultDirectoryName = FileHelper.KaKaoDownloadFolder_AndroidPath;

    public AddScriptPresenter(AddScriptContract.View view, ScriptRepository model ) {
        mView = view;
        mModel = model;
        mQuizGroupModel = QuizGroupModel.getInstance();
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
        if( bOk ) {
            List<String> quizGroupList = mQuizGroupModel.getQuizGroupNames();
            mView.showQuizGroupSelectDialog( quizGroupList );
        } else {
            mView.showErrorDialog(1);
        }
    }

    @Override
    public void quizGroupSelected( String quizGroupName ) {
        if( quizGroupName.equals( ShowQuizGroupsFragment.Text_New ) ){
            mView.showNewQuizGroupTitleInputDialog();
        } else {
            bNewQuizGroup = false;
            callConfirmDialog( quizGroupName, bNewQuizGroup );
        }
    }

    @Override
    public void newQuizGroupTitleInputted(String quizGroupName ) {
        bNewQuizGroup = true;
        callConfirmDialog( quizGroupName, bNewQuizGroup );
    }

    private void callConfirmDialog( String quizGroupName, boolean bNewQuizGroup ) {
        mSelectedQuizGroupName = quizGroupName;
        if( bNewQuizGroup == false ) {
            mSelectedQuizGroupId = mQuizGroupModel.getQuizGroupIdByName( quizGroupName );
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
        Integer userId = UserModel.getInstance().getUserID();
        mModel.addScript( userId,
                mCurrentDirectoryPath,
                pdfFileName,
                onAddScriptCallback( bNewQuizGroup,
                        mSelectedQuizGroupId,
                        mSelectedQuizGroupName,
                        mSelectedScriptName,
                        msgForLog) );
        mView.closeLoadingDialog();
    }

    private ScriptRepository.ParseScriptCallback onAddScriptCallback( final boolean bNewQuizGroup,
                                                                        final Integer quizGroupId,
                                                                        final String quizGroupName,
                                                                        final String scriptName,
                                                                        final String msgForLog) {
        return new ScriptRepository.ParseScriptCallback(){

            @Override
            public void onSuccess() {
                Log.i("AppContent", "addScript onSuccess()  user: " + msgForLog);

                if( bNewQuizGroup ) {
                    addQuizGroup(quizGroupName, scriptName);
                } else {
                    addQuizGroupDetail(quizGroupId, scriptName);
                }
                mView.showAddScriptSuccessDialog( quizGroupName );
            }

            @Override
            public void onFail(EResultCode resultCode) {
                Log.e("AppContent", "addScript onFail() UnkownERROR. user: " + msgForLog);
                mView.showErrorDialog(2);
            }
        };
    }

    private void addQuizGroup( String quizGroupName, String scriptName ) {
        Log.i("AppContent", "AddScriptPresenter addQuizGroup() called");
        Integer userId = UserModel.getInstance().getUserID();
        Integer scriptId = mModel.getScriptIdAsTitle(scriptName);
        List<Integer> scriptIds = new LinkedList<>();
        scriptIds.add(scriptId);
        mQuizGroupModel.addQuizGroup( userId, quizGroupName, scriptIds, onAddQuizGroup(quizGroupName) );
    }

    private QuizGroupModel.AddQuizGroupCallback onAddQuizGroup( final String quizGroupName ) {
        return new QuizGroupModel.AddQuizGroupCallback(){
            @Override
            public void onSuccess() {
                mView.showAddScriptSuccessDialog(quizGroupName);
            }
            @Override
            public void onFail(EResultCode resultCode) {
            }
        };
    }

    private void addQuizGroupDetail( Integer quizGroupId, String scriptName ) {
        Log.i("AppContent", "AddScriptPresenter addQuizGroupDetail() called");
        Integer userId = UserModel.getInstance().getUserID();
        Integer scriptId = mModel.getScriptIdAsTitle(scriptName);
        mQuizGroupModel.addQuizGroupDetail( userId, quizGroupId, scriptId, onAddQuizGroupDetail(quizGroupId) );
    }

    private QuizGroupModel.AddQuizGroupCallback onAddQuizGroupDetail( final Integer quizGroupId ) {
        return new QuizGroupModel.AddQuizGroupCallback(){
            @Override
            public void onSuccess() {
                String quizGroupName = mQuizGroupModel.getQuizGroupNameById(quizGroupId);
                mView.showAddScriptSuccessDialog(quizGroupName);
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
