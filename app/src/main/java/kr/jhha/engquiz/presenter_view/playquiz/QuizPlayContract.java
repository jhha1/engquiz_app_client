package kr.jhha.engquiz.presenter_view.playquiz;

/**
 * Created by thyone on 2017-03-15.
 */

public class QuizPlayContract {

    interface View {
        void showAlarmDialog(QuizPlayPresenter.ARALM_TYPE type);

        void showNextQuestion(String question);
        void showNotAvailableQuiz();
        void showAnswer(String answer);

        void showSendReportDialog();
        void onSuccessSendReport();
        void onFailSendReport(int what);
    }

    interface UserActionsListener {

        void checkAlarm();

        void doNextQuestion();
        void getAnswer();

        int getPlayCount();
        void increaseQuizCount();

        void sendReportBtnClicked();
        void sendReport();

        void helpBtnClicked();
    }
}
