package kr.jhha.engquiz.presenter_view.admin.report;

import java.util.List;

import kr.jhha.engquiz.model.local.Report;

/**
 * Created by thyone on 2017-03-15.
 */

public class ReportContract {

    interface View {
        void onSuccessGetReportList(int reportCountAll, List<Report> reports);
        void onFailGetReportList(String msg);

        void showModifyDialog(Report report);
        void onSuccessModifyReportUpdate(String modifiedKo, String modifiedEn);
        void onSuccessModifyReportAdd(Integer newSentenceID, String modifiedKo, String modifiedEn);
        void onSuccessModifyReportDel();
        void onFailModifyReport(String msg);
    }

    interface ActionsListener {
        void getReportList();
        void sentenceClicked(Report report);
        void modifySentenceUpdate(Integer scriptID, Integer sentenceID, String ko, String en);
        void modifySentenceDel(Integer scriptID, Integer sentenceID);
        void modifySentenceAdd(Integer scriptID, String ko, String en);
    }
}
