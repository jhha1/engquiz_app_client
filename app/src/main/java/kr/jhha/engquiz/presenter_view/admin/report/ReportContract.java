package kr.jhha.engquiz.presenter_view.admin.report;

import java.util.List;

import kr.jhha.engquiz.model.local.Report;

/**
 * Created by thyone on 2017-03-15.
 */

public class ReportContract {

    interface View {
        void onSuccessGetReportList(List<Report> reports);
        void onFailGetReportList(String msg);

        void showModifyDialog(Report report);
        void onSuccessModifyReport(String modifiedKo, String modifiedEn);
        void onFailModifyReport(String msg);
    }

    interface ActionsListener {
        void getReportList();
        void sentenceClicked(Report report);
        void modifySentence(Report modifiedSentence);
    }
}
