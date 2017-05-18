package kr.jhha.engquiz.presenter_view.admin.report;

import java.util.List;

import kr.jhha.engquiz.model.local.Report;
import kr.jhha.engquiz.model.local.ReportRepository;
import kr.jhha.engquiz.util.exception.EResultCode;
import kr.jhha.engquiz.util.ui.MyLog;

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
        MyLog.i("ReportPresenter onGetReportList() called");
        mReportModel.getReportList( onGetReportList() );
    }

    private ReportRepository.GetReportListCallback onGetReportList() {
        return new ReportRepository.GetReportListCallback(){

            @Override
            public void onSuccess(int reportCountAll, List<Report> reports) {
                mView.onSuccessGetReportList(reportCountAll, reports);
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
    public void modifySentenceUpdate(Integer scriptID, Integer sentenceID, String ko, String en) {
        Report modifiedSentence = new Report();
        modifiedSentence.setModifyType(Report.MODIFY_TYPE.UPDATE);
        modifiedSentence.setScriptId(scriptID);
        modifiedSentence.setSentenceId(sentenceID);
        modifiedSentence.setTextKo(ko);
        modifiedSentence.setTextEn(en);
        mReportModel.ModifyUpdate( modifiedSentence, onModifySentence() );
    }

    @Override
    public void modifySentenceDel(Integer scriptID, Integer sentenceID) {
        Report modifiedSentence = new Report();
        modifiedSentence.setModifyType(Report.MODIFY_TYPE.DEL);
        modifiedSentence.setScriptId(scriptID);
        modifiedSentence.setSentenceId(sentenceID);
        mReportModel.ModifyDel( modifiedSentence, onModifySentence() );
    }

    @Override
    public void modifySentenceAdd(Integer scriptID, String ko, String en) {
        Report modifiedSentence = new Report();
        modifiedSentence.setModifyType(Report.MODIFY_TYPE.ADD);
        modifiedSentence.setScriptId(scriptID);
        modifiedSentence.setTextKo(ko);
        modifiedSentence.setTextEn(en);
        mReportModel.ModifyAdd( modifiedSentence, onModifySentence() );
    }

    private ReportRepository.ReportModifyCallback onModifySentence() {
        return new ReportRepository.ReportModifyCallback(){

            @Override
            public void onSuccessUpdate(String modifiedKo, String modifiedEn) {
                mView.onSuccessModifyReportUpdate(modifiedKo, modifiedEn);
            }

            @Override
            public void onSuccessAdd(Integer newSentenceID, String modifiedKo, String modifiedEn) {
                mView.onSuccessModifyReportAdd(newSentenceID, modifiedKo, modifiedEn);
            }

            @Override
            public void onSuccessDel() {
                mView.onSuccessModifyReportDel();
            }

            @Override
            public void onFail(EResultCode resultCode) {
                mView.onFailModifyReport(resultCode.stringCode());
            }
        };
    }
}
