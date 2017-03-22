package kr.jhha.engquiz.quizgroup;

import java.util.List;

import kr.jhha.engquiz.MainActivity;
import kr.jhha.engquiz.data.local.QuizGroupDetail;

/**
 * Created by thyone on 2017-03-15.
 */

public class ShowQuizGroupsContract {

    interface View {
        void onGetQuizGroupSummaryListSuccess();
        void onGetQuizGroupSummaryListFail();

        void onDelQuizGroupSuccess();
        void onDelQuizGroupFail( String msg );
        void onChangeViewFragmet(MainActivity.EFRAGMENT fragment );
        void onShowOptions();
    }

    interface UserActionsListener {
        void quizGroupItemClicked( QuizGroupSummary item );
        void getQuizGroupSummaryList();

        void delQuizGroup( Integer position );
    }
}
