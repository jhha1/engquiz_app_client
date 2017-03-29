package kr.jhha.engquiz.addscript;

import java.util.List;

import kr.jhha.engquiz.data.local.Script;

/**
 * Created by thyone on 2017-03-15.
 */

public class AddScriptContract {

    interface View {
        void showMsg( int what, String arg );
        void showErrorDialog(int what );

        void showQuizGroupSelectDialog( List<String> quizGroupList  );
        void showNewQuizGroupTitleInputDialog();

        void showAddScriptConfirmDialog( String filename, Float fileSize);
        void showAddScriptSuccessDialog( String quizGroupName );

        void showLoadingDialog();
        void closeLoadingDialog();

        void showCurrentDirectoryPath( String path );
        void showFileListInDirectory( List<String> fileList );
    }

    interface ActionsListener {
        void initDirectoryLocationAndAvailableFiles();
        void onFileListItemClick( int position );

        void scriptSelected();
        void quizGroupSelected( String quizGroupName );
        void newQuizGroupTitleInputted(String quizGroupName);

        void addScript( String pdfFilename);
    }
}
