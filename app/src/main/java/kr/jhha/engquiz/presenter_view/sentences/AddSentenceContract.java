package kr.jhha.engquiz.presenter_view.sentences;

import java.util.List;

/**
 * Created by thyone on 2017-03-15.
 */

public class AddSentenceContract {

    interface View {
        void showDialogSelectScript( String[] scriptTitleAll );

        void showAddSentenceSuccessDialog(String scriptName);
        void showErrorDialog(int what);
    }

    interface ActionsListener {
        // 툴바 옵션 메뉴 - 도움말클릭
        void helpBtnClicked();

        void sentencesInputted( boolean bHasParentScript, Integer parentScriptId, String ko, String en);
        void makeNewScriptBtnClicked();
        void scriptSelected(String selectedScriptTitle);
    }
}
