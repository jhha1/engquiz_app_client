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
import kr.jhha.engquiz.data.remote.Request;
import kr.jhha.engquiz.data.remote.Response;
import kr.jhha.engquiz.quizgroup.QuizGroupAdapter;
import kr.jhha.engquiz.quizgroup.QuizGroupSummary;
import kr.jhha.engquiz.util.exception.system.IllegalArgumentException;

/**
 * Created by thyone on 2017-03-19.
 */

public class QuizGroupModel {

    public interface GetQuizGroupCallback {
        void onSuccess( List<QuizGroupSummary> quizGroupSummaryList );
        void onFail(EResultCode resultCode);
    }

    public interface AddQuizGroupCallback {
        void onSuccess( List<QuizGroupSummary> quizGroupSummaryList );
        void onFail(EResultCode resultCode);
    }

    public interface DelQuizGroupCallback {
        void onSuccess( List<QuizGroupSummary> quizGroupSummaryList );
        void onFail(EResultCode resultCode);
    }

    private static QuizGroupModel instance = new QuizGroupModel();
    private QuizGroupModel() {}
    public static QuizGroupModel getInstance() {
        return instance;
    }


    private List<QuizGroupSummary> mQuizGroupSummaryList = new ArrayList<>();

    private Integer MAX_QUIZ_GROUP_LIST_COUNT = 30;

    public Integer getMAX_QUIZ_GROUP_LIST_COUNT() {
        return MAX_QUIZ_GROUP_LIST_COUNT;
    }

    public Integer getQuizGroupSummaryCount(){
        return this.mQuizGroupSummaryList.size();
    }

    public QuizGroupSummary getQuizGroupSummary( int position ){
        if( 0 > position || mQuizGroupSummaryList.size() < position )
            throw new IllegalArgumentException(EResultCode.INVALID_ARGUMENT,
                    "QuizGroup ListView Position is invalid(pos:"+position+", listSize:"+ mQuizGroupSummaryList.size());
        return mQuizGroupSummaryList.get( position );
    }

    public void getQuizGroupSummaryList( Integer userId, final QuizGroupModel.GetQuizGroupCallback callback ) {
        if( null != mQuizGroupSummaryList && false == mQuizGroupSummaryList.isEmpty() ){
            callback.onSuccess( mQuizGroupSummaryList );
            return;
        }

        Request request = new Request( EProtocol2.PID.GetUserQuizGroupSummaryList );
        request.set(EProtocol.UserID, userId);
        AsyncNet net = new AsyncNet( request, onGetQuizGroupSummaryList(callback) );
        net.execute();
    }

    private AsyncNet.Callback onGetQuizGroupSummaryList( final QuizGroupModel.GetQuizGroupCallback callback ) {
        return new AsyncNet.Callback() {
            @Override
            public void onResponse(Response response) {
                if (response.isSuccess()) {
                    List<Map<String, Object>> quizGroupSummaryList = (List)response.get(EProtocol.QuizGroupInfo);
                    Log.e("AppContent", "onGetQuizGroupSummaryList(): "+ quizGroupSummaryList.toString());
                    // save summaries.
                    saveQuizGroupSummaryList( quizGroupSummaryList );
                    callback.onSuccess( mQuizGroupSummaryList );
                } else {
                    // 서버 응답 에러
                    Log.e("AppContent", "onGetQuizGroupSummaryList() UnkownERROR : "+ response.getResultCodeString());
                    callback.onFail( response.getResultCode() );
                }
            }
        };
    }

    public void addQuizGroup( Integer userId, String quizGroupTitle, List<Integer> scriptIds, final AddQuizGroupCallback callback ){
        Request request = new Request( EProtocol2.PID.AddUserQuizGroup );
        request.set(EProtocol.UserID, userId);
        request.set(EProtocol.QuizGroupTitle, quizGroupTitle);
        request.set(EProtocol.ScriptIds, scriptIds);
        AsyncNet net = new AsyncNet( request, onAddQuizGroup(callback) );
        net.execute();
    }

    private AsyncNet.Callback onAddQuizGroup( final AddQuizGroupCallback callback ) {
        return new AsyncNet.Callback() {
            @Override
            public void onResponse(Response response) {
                if (response.isSuccess()) {
                    List<Map<String, Object>> quizGroupSummaryList = (List)response.get(EProtocol.QuizGroupInfo);
                    Log.e("AppContent", "onAddQuizGroup(): "+ quizGroupSummaryList.toString());
                    // save summaries.
                    saveQuizGroupSummaryList( quizGroupSummaryList );
                    callback.onSuccess( mQuizGroupSummaryList );
                } else {
                    // 서버 응답 에러
                    Log.e("AppContent", "onGetQuizGroupSummaryList() UnkownERROR : "+ response.getResultCodeString());
                    callback.onFail( response.getResultCode() );
                }
            }
        };
    }

    public void delQuizGroup( Integer userId, Integer quizgroupId, final DelQuizGroupCallback callback ){
        Request request = new Request( EProtocol2.PID.DelUserQuizGroup );
        request.set(EProtocol.UserID, userId);
        request.set(EProtocol.QuizGroupId, quizgroupId);
        AsyncNet net = new AsyncNet( request, onDelQuizGroup(callback) );
        net.execute();
    }

    private AsyncNet.Callback onDelQuizGroup( final DelQuizGroupCallback callback ) {
        return new AsyncNet.Callback() {
            @Override
            public void onResponse(Response response) {
                if (response.isSuccess()) {
                    List<Map<String, Object>> quizGroupSummaryList = (List)response.get(EProtocol.QuizGroupInfo);
                    Log.e("AppContent", "onDelQuizGroup(): "+ quizGroupSummaryList.toString());
                    // save summaries.
                    saveQuizGroupSummaryList( quizGroupSummaryList );
                    callback.onSuccess( mQuizGroupSummaryList );
                } else {
                    // 서버 응답 에러
                    Log.e("AppContent", "onGetQuizGroupSummaryList() UnkownERROR : "+ response.getResultCodeString());
                    callback.onFail( response.getResultCode() );
                }
            }
        };
    }

    private void saveQuizGroupSummaryList( List<Map<String, Object>> quizGroupSummaryList ){
        // 1. save into memory.
        if( mQuizGroupSummaryList == null ) {
            mQuizGroupSummaryList = new ArrayList<>();
        }
        mQuizGroupSummaryList.clear();
        for( Map<String, Object> summary : quizGroupSummaryList ){
            QuizGroupSummary summaryObj = new QuizGroupSummary();
            summaryObj.deserialize( summary );
            mQuizGroupSummaryList.add( summaryObj );
        }
        // 2. No save into file.
        //     -> download summaries from a server when a quizgroup menu first clicked.
    }
}
