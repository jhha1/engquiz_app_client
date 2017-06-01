package kr.jhha.engquiz.presenter_view.sentences;

import java.util.List;

import kr.jhha.engquiz.model.local.Sentence;

/**
 * Created by thyone on 2017-03-15.
 */

public class SentenceContract {

    interface View {
        void showToolbarTitle(String title);

        // 문장 리스트 보기
        void onSuccessGetSentences(boolean bCustomSentences, List<Sentence> sentences);
        void onFailGetSentences(int msgId);

        // 문장 옵션보기
        void onShowOptionDialog(Sentence item);

        // 문장 추가
        void showAddSentenceFragment(Sentence item);

        // 문장 수정 요청
        void showSendReportDialog(Sentence item);
        void onSuccessSendReport();
        void onFailSendReport(int msgId);

        // 문장 직접 수정
        void showModifyDialog(Sentence item);
        void onSuccessUpdateSentence();
        void onFailUpdateSentence();

        // 문장 삭제
        void onSuccessDeleteSentence();
        void onFailDeleteSentence(int msgId);
    }

    interface ActionsListener {
        // 툴바 옵션 메뉴 - 도움말클릭
        void helpBtnClicked();

        void updateToolbarTitle(Integer scriptId);
        void getSentences(Integer scriptId);

        void sentenceSingleClicked(Sentence item);
        void sentenceLongClicked(Sentence item);

        void sendReport(Sentence item);
        void modifySentence(String ko, String en);
        void deleteSentence(Sentence item);
    }
}
