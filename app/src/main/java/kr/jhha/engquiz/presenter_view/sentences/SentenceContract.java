package kr.jhha.engquiz.presenter_view.sentences;

import java.util.List;

import kr.jhha.engquiz.model.local.Sentence;

/**
 * Created by thyone on 2017-03-15.
 */

public class SentenceContract {

    interface View {
        void showTitle(String title);

        void onSuccessGetSentences(List<Sentence> sentences);
        void onFailGetSentences();

        void showSendReportDialog();
        void onSuccessSendReport();
        void onFailSendReport(int msgId);

        void showModifyDialog(Sentence item);
        void onSuccessUpdateSentence();
        void onFailUpdateSentence();

        void showDeleteDialog();
        void onSuccessDeleteSentence();
        void onFailDeleteSentence(int msgId);
    }

    interface ActionsListener {
        void initToolbarTitle(Integer scriptId);
        void getSentences(Integer scriptId);

        void sentenceDoubleClicked(Sentence item);
        void sentenceLongClicked(Sentence item);

        void sendReport();
        void modifySentence(String ko, String en);
        void deleteSentence();
    }
}
