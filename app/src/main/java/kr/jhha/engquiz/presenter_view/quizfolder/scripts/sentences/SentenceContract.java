package kr.jhha.engquiz.presenter_view.quizfolder.scripts.sentences;

import java.util.List;

import kr.jhha.engquiz.model.local.Report;
import kr.jhha.engquiz.model.local.Sentence;

/**
 * Created by thyone on 2017-03-15.
 */

public class SentenceContract {

    interface View {
        void showTitle(String title);

        void onSuccessGetSentences(String sentences);
        void onSuccessGetSentences(List<Sentence> sentences);
        void onFailGetSentences();

        void showModifyDialog(Sentence item);
        void onSuccessUpdateSentence();
        void onFailUpdateSentence();

        void showDeleteDialog();
        void onSuccessDeleteSentence();
        void onFailDeleteSentence();
    }

    interface ActionsListener {
        void initToolbarTitle(Integer scriptId);
        void getSentences(Integer scriptId);

        void sentenceDoubleClicked(Sentence item);
        void sentenceLongClicked(Sentence item);
        void modifySentence(String ko, String en);
        void deleteSentence();
    }
}
