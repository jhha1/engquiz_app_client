package kr.jhha.engquiz.presenter_view.scripts.regular;

import java.util.List;

import kr.jhha.engquiz.model.local.Script;

/**
 * Created by thyone on 2017-03-15.
 */

public class RegularScriptsContract {

    interface View {
        // 퀴즈폴더 스크립트 리스트 가져오기 결과
        void onSuccessGetScrpits(List<Integer> parsedScriptIds, List<String> notAddedPDFScrpitIds);
        void onFailGetScripts();

        // sync float btn
        void showSyncFloatingBtn();
        void hideSyncFloatingBtn();

        // 스크립트 추가
        void showAddScriptConfirmDialog(String filename, Float fileSize, RegularScriptsAdapter.ScriptSummary item);
        void showLoadingDialog();
        void closeLoadingDialog();
        void showAddScriptSuccessDialog(RegularScriptsAdapter.ScriptSummary item, Script newScript);

        // 스크립트 추가 화면으로 전환
        void onShow_ParseScrpitFragment();
        // 문장 보기
        void onShow_ShowSentencesFragment(Integer scriptId, String scriptTitle);

        // 스크립트 삭제
        void onShowOptionDialog(final RegularScriptsAdapter.ScriptSummary item);
        void onSuccessDelScript(RegularScriptsAdapter.ScriptSummary item);

        // 게임용 추가 성공
        void onSuccessAddScriptIntoPlayList(RegularScriptsAdapter.ScriptSummary item, int msgId);
        // 게임용 제외 성공
        void onSuccessDelScriptIntoPlayList(RegularScriptsAdapter.ScriptSummary item, int msgId);

        void showErrorDialog(int what);
        void showErrorDialog(String msg);
    }

    interface ActionsListener {
        void init();

        // 툴바 옵션 메뉴 - 도움말클릭
        void helpBtnClicked();

        // 스크립트 클릭에 따른 로직 타기
        void listViewItemClicked(RegularScriptsAdapter.ScriptSummary item);
        void listViewItemLongClicked(RegularScriptsAdapter.ScriptSummary item);

        // 스크립트 추가 삭제
        void addScript( String pdfFileName, RegularScriptsAdapter.ScriptSummary item );
        void deleteScript(RegularScriptsAdapter.ScriptSummary item);

        // 게임용으로 추가 삭제
        void addScriptIntoPlayList(RegularScriptsAdapter.ScriptSummary item );
        void delScriptIntoPlayList(RegularScriptsAdapter.ScriptSummary item );
    }
}
