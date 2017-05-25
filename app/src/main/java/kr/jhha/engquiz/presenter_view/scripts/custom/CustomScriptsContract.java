package kr.jhha.engquiz.presenter_view.scripts.custom;

import java.util.List;

import kr.jhha.engquiz.model.local.Script;
import kr.jhha.engquiz.presenter_view.scripts.regular.RegularScriptsAdapter;

/**
 * Created by thyone on 2017-03-15.
 */

public class CustomScriptsContract {

    interface View {
        // 스크립트 리스트 가져오기 결과
        void onSuccessGetScrpits(List<Integer> userMadeScriptIds);

        // 클릭: 문장 보기
        void onShowSentences(Integer scriptId, String scriptTitle);

        // 더블클릭: 옵션 보기
        void onShowOptionDialog(CustomScriptsAdapter.ScriptSummary item);

        // 스크립트 생성
        void onSuccessCreateScript(Script script);
        // 스크립트 삭제
        void onSuccessDelScript(CustomScriptsAdapter.ScriptSummary item);

        // 게임용 추가 성공
        void onSuccessAddScriptIntoPlayList(CustomScriptsAdapter.ScriptSummary item, int msgId);
        // 게임용 제외 성공
        void onSuccessDelScriptIntoPlayList(CustomScriptsAdapter.ScriptSummary item, int msgId);

        void showErrorDialog(int what);
        void showErrorDialog(String msg);
    }

    interface ActionsListener {
        void getScripts();

        // 툴바 옵션 메뉴 - 도움말클릭
        void helpBtnClicked();

        // 스크립트 클릭에 따른 로직 타기
        void listViewItemClicked(CustomScriptsAdapter.ScriptSummary item);
        void listViewItemLongClicked(CustomScriptsAdapter.ScriptSummary item);

        // 스크립트 삭제
        void deleteScript(CustomScriptsAdapter.ScriptSummary item);

        // 게임용으로 추가 삭제
        void addScriptIntoPlayList(CustomScriptsAdapter.ScriptSummary item );
        void delScriptIntoPlayList(CustomScriptsAdapter.ScriptSummary item );
    }
}
