package kr.jhha.engquiz.quizfolder.detail;

import java.util.List;

import kr.jhha.engquiz.MainActivity;
import kr.jhha.engquiz.data.local.QuizFolder;

/**
 * Created by thyone on 2017-03-15.
 */

public class ShowQuizFolderDetailContract {

    interface View {
        // 퀴즈폴더 스크립트 리스트 가져오기 결과
        void onSuccessGetScrpits(List<Integer> quizFolderScriptIds);
        void onFailGetScripts();

        // 스크립트 추가 화면으로 전환
        void onChangeFragmetNew();
        void onChangeFragmetFolderDetail( Integer scriptId, String scriptTitle );

        // 스크립트 삭제 결과
        void onSuccessDelScript(List<Integer> updatedQuizFolderScriptIds);
        void onFailDelScript(String msg);
    }

    interface ActionsListener {
        void getQuizFolderScripts(Integer quizfolderId);

        // 스크립트 클릭에 따른 로직 타기
        void listViewItemClicked(Integer scriptID, String scriptTitle);

        // 스크립트 삭제
        void delQuizFolderScript(QuizFolderDetailAdapter.ScriptSummary item);
    }
}
