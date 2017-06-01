package kr.jhha.engquiz.presenter_view.playquiz;

import kr.jhha.engquiz.R;
import kr.jhha.engquiz.model.local.QuizPlayRepository;
import kr.jhha.engquiz.model.local.ReportRepository;
import kr.jhha.engquiz.model.local.ScriptRepository;
import kr.jhha.engquiz.model.local.Sentence;
import kr.jhha.engquiz.model.local.SyncRepository;
import kr.jhha.engquiz.presenter_view.FragmentHandler;
import kr.jhha.engquiz.presenter_view.help.WebViewFragment;
import kr.jhha.engquiz.util.exception.EResultCode;
import kr.jhha.engquiz.util.ui.MyLog;

import static kr.jhha.engquiz.presenter_view.FragmentHandler.EFRAGMENT.WEB_VIEW;

/**
 * Created by thyone on 2017-03-15.
 */

public class QuizPlayPresenter implements QuizPlayContract.UserActionsListener {

    private final QuizPlayContract.View mView;
    private final QuizPlayRepository mModel;
    private Sentence mCurrentQuiz;

    private static int playCount = 1;

    // 싱크 받으라는 다이알로그는 접속시에 한번만 보여준다. 구분위해 플래그사용
    private static boolean bNotShowSyncAlarmDialog = true;

    public enum ARALM_TYPE { SYNC }

    public QuizPlayPresenter(QuizPlayContract.View view, QuizPlayRepository model ) {
        mModel = model;
        mView = view;
        mCurrentQuiz = null;
    }

    @Override
    public void checkAlarm() {
        boolean bNeedSync = (SyncRepository.getInstance().getSyncNeededCount() > 0);
        if( bNeedSync && bNotShowSyncAlarmDialog ){
            mView.showAlarmDialog(ARALM_TYPE.SYNC);
            bNotShowSyncAlarmDialog = false;
        }
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
    public int getPlayCount() {
        return playCount;
    }

    @Override
    public void increaseQuizCount() {
        // 퀴즈 맞추는 개수
        playCount++;
    }

    @Override
    public void helpBtnClicked() {
        FragmentHandler handler = FragmentHandler.getInstance();
        WebViewFragment fragment = (WebViewFragment)handler.getFragment(WEB_VIEW);
        fragment.setHelpWhat(FragmentHandler.EFRAGMENT.PLAYQUIZ);
        handler.changeViewFragment(WEB_VIEW);
    }

    @Override
    public void sendReportBtnClicked(){
        // 문장이 null이면, 문장이 없는것..
        // 수정요청 할수없다.
        if( mCurrentQuiz == null )
            return;

        if( mCurrentQuiz.isMadeByUser() ) {
            // 유저가 직접 만든 문장은, 문장수정 요청을 개발자에게 보낼 수 없다.
            mView.onFailSendReport(R.string.report__send_fail_custom_sentence);
            return;
        } else {
            mView.showSendReportDialog();
        }
    }

    @Override
    public void sendReport() {
        if( mCurrentQuiz.isMadeByUser() ) {
            // 유저가 직접 만든 문장은, 문장수정 요청을 개발자에게 보낼 수 없다.
            mView.onFailSendReport(R.string.report__send_fail_custom_sentence);
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
                int msgId = EResultCode.commonMsgHandler(resultCode, R.string.report__send_fali);
                mView.onFailSendReport(msgId);

                MyLog.e("onFailSendReport() code: " + resultCode.stringCode() +",msgId:"+msgId);
            }
        };
    }
}
