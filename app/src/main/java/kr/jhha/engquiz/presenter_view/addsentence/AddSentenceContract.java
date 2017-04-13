package kr.jhha.engquiz.presenter_view.addsentence;

import java.util.List;

/**
 * Created by thyone on 2017-03-15.
 */

public class AddSentenceContract {

    interface View {
        void showDialogSelectScript( String[] scriptTitleAll );
        void showNeedMakeScriptDialog();
        void showDialogMakeNewScript();
        void showDialogMakeNewScript_ReInput();

        void showQuizFolderSelectDialog(List<String> quizFolderList);
        void showMakeNewQuizFolderDialog();
        void showNewQuizFolderTitleInputDialog();

        void showAddSentenceSuccessDialog(String quizFolderName, String scriptName);

        void showErrorDialog(int what);
    }

    interface ActionsListener {
        void sentencesInputted(String ko, String en);
        void makeNewScriptBtnClicked();
        void scriptSelected(String selectedScriptTitle);
        void makeNewScript(String scriptName);

        void quizFolderSelected(String quizFolderName);
        void newQuizFolderTitleInputted(String quizFolderName);
    }
}
