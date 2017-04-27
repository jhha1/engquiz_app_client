package kr.jhha.engquiz.presenter_view.quizfolder.scripts;

import java.util.List;

/**
 * Created by thyone on 2017-03-15.
 */

public class FolderScriptsContract {

    interface View {
        void showTitle(String title);

        // 퀴즈폴더 스크립트 리스트 가져오기 결과
        void onSuccessGetScrpits(List<Integer> quizFolderScriptIds);
        void onFailGetScripts();

        // 스크립트 추가 화면으로 전환
        void onChangeFragmetNew();
        void onChangeFragmetShowSentenceList(Integer scriptId, String scriptTitle );

        // 스크립트 삭제 결과
        void onSuccessDetachScript(List<Integer> updatedQuizFolderScriptIds);
        void onFailDetachScript(int msgId);
    }

    interface ActionsListener {
        void initToolbarTitle(Integer quizFolderID);

        void getQuizFolderScripts(Integer quizfolderId);

        // 스크립트 클릭에 따른 로직 타기
        void listViewItemClicked(Integer scriptID, String scriptTitle);

        // 스크립트 삭제
        void detachScript(FolderScriptsAdapter.ScriptSummary item, boolean deleteScriptFile);
    }
}
