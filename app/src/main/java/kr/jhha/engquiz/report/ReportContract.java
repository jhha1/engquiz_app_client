package kr.jhha.engquiz.report;

import java.util.List;

import kr.jhha.engquiz.data.local.QuizFolder;
import kr.jhha.engquiz.data.local.Report;

/**
 * Created by thyone on 2017-03-15.
 */

public class ReportContract {

    interface View {
        void onSuccessGetReportList(List<Report> reports);
        void onFailGetReportList(String msg);

        void showModifyDialog(Report report);
        void onSuccessModifyReport();
        void onFailModifyReport(String msg);
    }

    interface ActionsListener {
        void getReportList();
        void sentenceClicked(Report report);
        void modifySentence(Report modifiedSentence);
    }
}
