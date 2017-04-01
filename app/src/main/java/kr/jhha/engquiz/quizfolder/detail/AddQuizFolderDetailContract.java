package kr.jhha.engquiz.quizfolder.detail;

import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

import kr.jhha.engquiz.data.local.QuizFolder;

/**
 * Created by thyone on 2017-03-15.
 */

public class AddQuizFolderDetailContract {

    interface View {
        void showEmptyScriptDialog();
        void onSuccessAddScriptInQuizFolder(List<Integer> updatedScriptIds);
        void onFailAddQuizFolder(String msg);

        void clearUI();
        void returnToQuizFolderDetailFragment();
    }

    interface ActionsListener {
        ArrayAdapter getAdapter();

        void addScriptInQuizFolder(Integer quizFolderId, ListView listView );

        void emptyScriptDialogOkButtonClicked();
    }
}
