package kr.jhha.engquiz.quizfolder.scripts.sentences;

import java.util.List;

import kr.jhha.engquiz.data.local.Sentence;
import kr.jhha.engquiz.quizfolder.scripts.QuizFolderScriptsAdapter;

/**
 * Created by thyone on 2017-03-15.
 */

public class ShowSentenceContract {

    interface View {
        void onSuccessGetSentences(String sentences);
        void onFailGetSentences();
    }

    interface ActionsListener {
        void getSentences(Integer scriptId);
    }
}
