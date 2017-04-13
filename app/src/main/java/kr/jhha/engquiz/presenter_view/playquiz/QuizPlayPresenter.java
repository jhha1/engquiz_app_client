package kr.jhha.engquiz.presenter_view.playquiz;

import android.util.Log;

import kr.jhha.engquiz.model.local.QuizPlayRepository;
import kr.jhha.engquiz.model.local.ReportRepository;
import kr.jhha.engquiz.model.local.Sentence;
import kr.jhha.engquiz.util.exception.EResultCode;
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
    public void initToolbarTitle() {
        String title = mModel.getPlayQuizFolderTitle();
        if(StringHelper.isNull(title)){
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
        if( mCurrentQuiz.isMadeByUser() ) {
            // 유저가 직접 만든 문장은, 문장수정 요청을 개발자에게 보낼 수 없다.
            mView.onFailSendReport(1);
            return;
        }
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
                mView.onFailSendReport(2);
            }
        };
    }
}
