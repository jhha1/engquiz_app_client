package kr.jhha.engquiz.quizfolder.scripts;

import java.util.List;

/**
 * Created by thyone on 2017-03-15.
 */

public class AddQuizFolderScriptContract {

    interface View {
        void showScriptList( String[] scriptTitleAll );
        void showEmptyScriptDialog();
        void onSuccessAddScriptInQuizFolder(List<Integer> updatedScriptIds);
        void onFailAddQuizFolder(String msg);

        void clearUI();
        void returnToQuizFolderDetailFragment();
    }

    interface ActionsListener {
        void initScriptList();
        void addScriptIntoQuizFolder(Integer quizFolderId, String scriptTitle );
        void emptyScriptDialogOkButtonClicked();
    }
}
