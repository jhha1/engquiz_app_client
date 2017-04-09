package kr.jhha.engquiz.quizplay;

/**
 * Created by thyone on 2017-03-15.
 */

public class QuizPlayContract {

    interface View {
        void showTitle(String title);
        void showNextQuestion(String question);
        void showNotAvailableQuiz();
        void showAnswer(String answer);

        void onSuccessSendReport();
        void onFailSendReport();
    }

    interface UserActionsListener {
        void initTitle();
        void doNextQuestion();
        void getAnswer();

        void sendReport();
    }
}
