package kr.jhha.engquiz.report;

import android.util.Log;

import java.util.List;

import kr.jhha.engquiz.data.local.Report;
import kr.jhha.engquiz.data.local.ReportRepository;
import kr.jhha.engquiz.data.remote.EResultCode;

/**
 * Created by thyone on 2017-03-15.
 */

public class ReportPresenter implements ReportContract.ActionsListener {

    private final ReportContract.View mView;
    private final ReportRepository mReportModel;

    public ReportPresenter(ReportContract.View view, ReportRepository model ) {
        mReportModel = model;
        mView = view;
    }

    public void getReportList() {
        Log.i("AppContent", "ReportPresenter onGetReportList() called");
        mReportModel.getReportList( onGetReportList() );
    }

    private ReportRepository.GetReportListCallback onGetReportList() {
        return new ReportRepository.GetReportListCallback(){

            @Override
            public void onSuccess(List<Report> reports) {
                mView.onSuccessGetReportList(reports);
            }

            @Override
            public void onFail(EResultCode resultCode) {
                mView.onFailGetReportList(resultCode.stringCode());
            }
        };
    }

    @Override
    public void sentenceClicked(Report report) {
        mView.showModifyDialog(report);
    }

    @Override
    public void modifySentence(Report modifiedSentence) {
        mReportModel.sendModifiedSentence( modifiedSentence, onModifySentence() );
    }

    private ReportRepository.ReportCallback onModifySentence() {
        return new ReportRepository.ReportCallback(){

            @Override
            public void onSuccess() {
                mView.onSuccessModifyReport();
            }

            @Override
            public void onFail(EResultCode resultCode) {
                mView.onFailModifyReport(resultCode.stringCode());
            }
        };
    }
}
