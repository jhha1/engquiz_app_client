package kr.jhha.engquiz.presenter_view.quizfolder.scripts;

import java.util.List;

/**
 * Created by thyone on 2017-03-15.
 */

public class AddScriptIntoFolderContract {

    interface View {
        void showScriptList( String[] scriptTitleAll );
        void showEmptyScriptDialog();
        void onSuccessAddScriptIntoQuizFolder(List<Integer> updatedScriptIds);
        void onFailAddScriptIntoQuizFolder(String msg);

        void clearUI();
        void returnToQuizFolderFragment();
    }

    interface ActionsListener {
        void initScriptList();
        void addScriptIntoQuizFolder(Integer quizFolderId, String scriptTitle );
        void emptyScriptDialogOkButtonClicked();
    }
}
