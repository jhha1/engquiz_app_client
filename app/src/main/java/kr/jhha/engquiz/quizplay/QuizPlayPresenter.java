package kr.jhha.engquiz.quizplay;

import android.util.Log;

import kr.jhha.engquiz.data.local.QuizPlayRepository;
import kr.jhha.engquiz.data.local.ReportRepository;
import kr.jhha.engquiz.data.local.Sentence;
import kr.jhha.engquiz.data.remote.EResultCode;
import kr.jhha.engquiz.util.StringHelper;

/**
 * Created by thyone on 2017-03-15.
 */

public class QuizPlayPresenter implements QuizPlayContract.UserActionsListener {

    private final QuizPlayContract.View mView;
    private final QuizPlayRepository mModel;
    private Sentence mCurrentQuiz;

    public QuizPlayPresenter(QuizPlayContract.View view, QuizPlayRepository model ) {
        mModel = model;
        mView = view;
    }

    @Override
    public void initTitle() {
        String title = mModel.getPlayQuizFolderTitle();
        if(StringHelper.isNullString(title)){
            title = "Play Game";
        }
        mView.showTitle(title);
    }

    @Override
    public void doNextQuestion(){
        mCurrentQuiz = getNewQuiz();
        if(mCurrentQuiz == null) {
            mView.showNotAvailableQuiz();
            return;
        }
        mView.showNextQuestion(mCurrentQuiz.textKo);
    }
    private Sentence getNewQuiz() {
        final QuizPlayRepository model = QuizPlayRepository.getInstance();
        if(model != null)
            return model.getQuiz();
        return null;
    }

    @Override
    public void getAnswer(){
        mView.showAnswer(mCurrentQuiz.textEn);
    }

    @Override
    public void sendReport() {
        final ReportRepository reportRepo = ReportRepository.getInstance();
        reportRepo.sendReport(mCurrentQuiz, onSendReportCallback());
    }

    private ReportRepository.ReportCallback onSendReportCallback() {
        return new ReportRepository.ReportCallback(){

            @Override
            public void onSuccess() {
                mView.onSuccessSendReport();
            }

            @Override
            public void onFail(EResultCode resultCode) {
                Log.e("AppContent", "onFailSendReport() code: " + resultCode.stringCode());
                mView.onFailSendReport();
            }
        };
    }
}
