package kr.jhha.engquiz.presenter_view.quizfolder.scripts.sentences;

import java.util.List;

import kr.jhha.engquiz.model.local.Report;
import kr.jhha.engquiz.model.local.Sentence;

/**
 * Created by thyone on 2017-03-15.
 */

public class SentenceContract {

    interface View {
        void onSuccessGetSentences(String sentences);
        void onSuccessGetSentences(List<Sentence> sentences);
        void onFailGetSentences();

        void showModifyDialog(Sentence item);
        void onSuccessUpdateSentence();
        void onFailUpdateSentence();
    }

    interface ActionsListener {
        void getSentences(Integer scriptId);

        void sentenceLongClicked(Sentence item);
        void modifySentence(String ko, String en);
    }
}
