package kr.jhha.engquiz.presenter_view.scripts;

import java.util.List;

import kr.jhha.engquiz.model.local.Script;

/**
 * Created by thyone on 2017-03-15.
 */

public class ScriptsContract {

    interface View {
        // 퀴즈폴더 스크립트 리스트 가져오기 결과
        void onSuccessGetScrpits(List<Integer> parsedScriptIds, List<Integer> userMadeScriptIds, List<String> notAddedPDFScrpitIds);
        void onFailGetScripts();

        // 스크립트 추가
        void showAddScriptConfirmDialog(String filename, Float fileSize, ScriptsAdapter.ScriptSummary item);
        void showLoadingDialog();
        void closeLoadingDialog();
        void showAddScriptSuccessDialog(ScriptsAdapter.ScriptSummary item, Script newScript);
        void showErrorDialog(int what);
        void showErrorDialog(String msg);

        // 스크립트 추가 화면으로 전환
        void onShow_ParseScrpitFragment();
        void onShow_ShowSentencesFragment(Integer scriptId, String scriptTitle);


        // 스크립트 삭제
        void onShowDeleteDialog(ScriptsAdapter.ScriptSummary item);
        void onSuccessDelScript(ScriptsAdapter.ScriptSummary item);
        void onFailDelScript(int msgId);
    }

    interface ActionsListener {
        void getScripts();

        // 스크립트 클릭에 따른 로직 타기
        void listViewItemClicked(ScriptsAdapter.ScriptSummary item);
        void listViewItemLongClicked(ScriptsAdapter.ScriptSummary item);
        void addScript( String pdfFileName, ScriptsAdapter.ScriptSummary item );

        // 스크립트 삭제
        void deleteScript(ScriptsAdapter.ScriptSummary item);
    }
}
