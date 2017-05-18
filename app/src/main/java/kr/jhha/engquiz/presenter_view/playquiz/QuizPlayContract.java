package kr.jhha.engquiz.presenter_view.playquiz;

/**
 * Created by thyone on 2017-03-15.
 */

public class QuizPlayContract {

    interface View {
        void showTitle(String title);
        void showNextQuestion(String question);
        void showNotAvailableQuiz();
        void showAnswer(String answer);

        void showSendReportDialog();
        void onSuccessSendReport();
        void onFailSendReport(int what);
    }

    interface UserActionsListener {
        void initToolbarTitle();
        void resetQuizData();

        void doNextQuestion();
        void getAnswer();

        void sendReportBtnClicked();
        void sendReport();

        void helpBtnClicked();
    }
}
