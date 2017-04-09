package kr.jhha.engquiz.data.local;

import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import kr.jhha.engquiz.data.remote.AsyncNet;
import kr.jhha.engquiz.data.remote.EProtocol;
import kr.jhha.engquiz.data.remote.EProtocol2;
import kr.jhha.engquiz.data.remote.EResultCode;
import kr.jhha.engquiz.data.remote.ObjectBundle;
import kr.jhha.engquiz.data.remote.Request;
import kr.jhha.engquiz.data.remote.Response;
import kr.jhha.engquiz.util.JsonHelper;


/**
 * Created by jhha on 2016-10-14.
 */

public class ReportRepository {

    public interface GetReportListCallback{
        void onSuccess( List<Report> reports );
        void onFail(EResultCode resultCode);
    }

    public interface ReportCallback {
        void onSuccess();
        void onFail(EResultCode resultCode);
    }

    private List<Report> mReports = new LinkedList<>();

    private static final ReportRepository instance = new ReportRepository();
    private ReportRepository() {}
    public static ReportRepository getInstance() {
        return instance;
    }

    public void getReportList( final ReportRepository.GetReportListCallback callback )
    {
        Request request = new Request( EProtocol2.PID.Report_GetList );
        AsyncNet net = new AsyncNet( request, onGetReportList(callback) );
        net.execute();
    }

    private AsyncNet.Callback onGetReportList(final ReportRepository.GetReportListCallback callback ) {
        return new AsyncNet.Callback() {
            @Override
            public void onResponse(Response response) {
                if (response.isSuccess())
                {
                    List<String> reportBundles = (List) response.get(EProtocol.ReportList);
                    if(reportBundles != null) {
                        for (String bundle : reportBundles) {
                            Report report = new Report(bundle);
                            mReports.add(report);
                        }
                    }
                    callback.onSuccess( new ArrayList<>(mReports) );
                } else {
                    Log.e("AppContent", "onSendReport() UnkownERROR : "+ response.getResultCodeString());
                    callback.onFail( response.getResultCode() );
                }
            }
        };
    }

    public void sendReport( Sentence sentence,
                           final ReportCallback callback )
    {
        Integer userId = UserRepository.getInstance().getUserID();

        Request request = new Request( EProtocol2.PID.Report_Send );
        request.set( EProtocol.UserID, userId );
        request.set( EProtocol.ScriptId, sentence.scriptId);
        request.set( EProtocol.SentenceId, sentence.sentenceId);
        request.set( EProtocol.SentenceKo, sentence.textKo);
        request.set( EProtocol.SentenceEn, sentence.textEn);
        AsyncNet net = new AsyncNet( request, onSendReport(callback) );
        net.execute();
    }

    private AsyncNet.Callback onSendReport(final ReportCallback callback ) {
        return new AsyncNet.Callback() {
            @Override
            public void onResponse(Response response) {
                if (response.isSuccess()) {
                    callback.onSuccess();
                } else {
                    Log.e("AppContent", "onSendReport() UnkownERROR : "+ response.getResultCodeString());
                    callback.onFail( response.getResultCode() );
                }
            }
        };
    }

    public void sendModifiedSentence( Report report,
                                      final ReportCallback callback )
    {
        Request request = new Request( EProtocol2.PID.Report_Modify );
        request.set( EProtocol.SentenceId, report.getSentenceId());
        request.set( EProtocol.SentenceKo, report.getTextKo());
        request.set( EProtocol.SentenceEn, report.getTextEn());
        AsyncNet net = new AsyncNet( request, onModifiedSentence(callback) );
        net.execute();
    }

    private AsyncNet.Callback onModifiedSentence(final ReportCallback callback ) {
        return new AsyncNet.Callback() {
            @Override
            public void onResponse(Response response) {
                if (response.isSuccess()) {
                    callback.onSuccess();
                } else {
                    Log.e("AppContent", "onSendReport() UnkownERROR : "+ response.getResultCodeString());
                    callback.onFail( response.getResultCode() );
                }
            }
        };
    }
}
