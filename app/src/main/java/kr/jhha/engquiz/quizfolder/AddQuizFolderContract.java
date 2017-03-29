package kr.jhha.engquiz.quizfolder;

import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.List;

/**
 * Created by thyone on 2017-03-15.
 */

public class AddQuizFolderContract {

    interface View {
        void showEmptyScriptDialog();
        void showAddQuizFolderConfirmDialog();

        void onSuccessAddQuizFolder();
        void onFailAddQuizFolder( int nextAction, String msg );
    }

    interface ActionsListener {
        ArrayAdapter getAdapter();
        Integer newQuizFolderTitleInputted( String title );
        void scriptsSelected();
        void addQuizFolder( String title, ListView listView );
    }
}
