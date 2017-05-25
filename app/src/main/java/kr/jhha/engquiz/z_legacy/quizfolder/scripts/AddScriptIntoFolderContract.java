package kr.jhha.engquiz.z_legacy.quizfolder.scripts;

import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

/**
 * Created by thyone on 2017-03-15.
 */

public class AddScriptIntoFolderContract {

    interface View {
        void setAdapter(ArrayAdapter<String> adapter);
        void showEmptyScriptDialog();
        void onSuccessAddScriptIntoQuizFolder(List<Integer> updatedScriptIds);
        void onFailAddScriptIntoQuizFolder(int msgId);

        void clearUI();
        void returnToQuizFolderFragment();
    }

    interface ActionsListener {
        void initScriptList();
        void scriptsSelected(Integer quizFolderId, ListView listView);
        void emptyScriptDialogOkButtonClicked();
    }
}
