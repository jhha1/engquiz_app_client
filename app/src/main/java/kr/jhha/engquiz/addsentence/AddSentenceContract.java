package kr.jhha.engquiz.addsentence;

import java.util.List;

/**
 * Created by thyone on 2017-03-15.
 */

public class AddSentenceContract {

    interface View {
        void showDialogSelectScript( String[] scriptTitleAll );
        void showDialogMakeNewScript();
        void showReInputNewScriptName();




        void showMsg(int what, String arg);
        void showErrorDialog(int what);

        void showQuizFolderSelectDialog(List<String> quizFolderList);
        void showNeedMakeQuizFolderDialog();
        void showNewQuizFolderTitleInputDialog();

        void showAddScriptConfirmDialog(String filename, Float fileSize);
        void showAddScriptSuccessDialog(String quizFolderName);

        void showLoadingDialog();
        void closeLoadingDialog();

        void showCurrentDirectoryPath(String path);
        void showFileListInDirectory(List<String> fileList);
    }

    interface ActionsListener {
        void sentencesInputted(String ko, String en);
        void makeNewScriptBtnClicked();
        void scriptSelected(String selectedScriptTitle);
        void makeNewScript(String scriptName);

        void initDirectoryLocationAndAvailableFiles();
        void onFileListItemClick(int position);


        void quizFolderSelected(String quizFolderName);
        void newQuizFolderTitleInputted(String quizFolderName);

        void addScript(String pdfFilename);
    }
}
