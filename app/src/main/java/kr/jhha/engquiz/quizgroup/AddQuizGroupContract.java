package kr.jhha.engquiz.quizgroup;

import java.util.List;

/**
 * Created by thyone on 2017-03-15.
 */

public class AddQuizGroupContract {

    interface View {
        void onAddQuizGroupSuccess();
        void onAddQuizGroupFail();
    }

    interface UserActionsListener {
        void addQuizGroup( String title, List<Integer> scriptIds );
    }
}
