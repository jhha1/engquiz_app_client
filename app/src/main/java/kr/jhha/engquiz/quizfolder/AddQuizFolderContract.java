package kr.jhha.engquiz.quizfolder;

import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.List;

import kr.jhha.engquiz.data.local.QuizFolder;

/**
 * Created by thyone on 2017-03-15.
 */

public class AddQuizFolderContract {

    interface View {
        void showEmptyScriptDialog();
        void showQuizFolderTitleDialog();
        void showAddQuizFolderConfirmDialog();

        void onSuccessAddQuizFolder( List<QuizFolder> updatedQuizFolders );
        void onFailAddQuizFolder( String msg );

        void clearUI();
        void returnToQuizFolderFragment();
    }

    interface ActionsListener {
        ArrayAdapter getAdapter();
        void selectStartDialog();

        Integer newQuizFolderTitleInputted( String title );
        void scriptsSelected();
        void addQuizFolder( String title, ListView listView );

        void emptyScriptDialogOkButtonClicked();
    }
}
