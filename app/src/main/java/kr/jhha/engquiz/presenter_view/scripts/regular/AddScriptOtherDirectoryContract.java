package kr.jhha.engquiz.presenter_view.scripts.regular;

import java.util.List;

/**
 * Created by thyone on 2017-03-15.
 */

public class AddScriptOtherDirectoryContract {

    interface View {
        void showMsg( int what, String arg );
        void showErrorDialog(int what );
        void showErrorDialog(String msg);

        void showAddScriptConfirmDialog( String filename, Float fileSize );
        void showAddScriptSuccessDialog();

        void showLoadingDialog();
        void closeLoadingDialog();

        void showCurrentDirectoryPath( String path );
        void showFileListInDirectory( List<String> fileList );
    }

    interface ActionsListener {
        void initDirectoryLocationAndAvailableFiles();
        void onFileListItemClick( int position );
        void scriptSelected();
        void addScript( String pdfFilename);
    }
}
