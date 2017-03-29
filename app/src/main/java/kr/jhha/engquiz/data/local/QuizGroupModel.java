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
        void onSuccess( );
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
    private List<QuizGroupDetail> mQuizGroupDetailList = new ArrayList<>();
    private Integer MAX_QUIZ_GROUP_LIST_COUNT = 30;

    // 퀴즈그룹리스트 서버로부터 받아오는 시점.
    // 1. login 성공시.
    // 2. 유저가 퀴즈그룹 메뉴를 클릭해 퀴즈그룹 리스트를 볼려고 할때, 메모리에 리스트가 없으면.
    //
    // 원래 2번만으로 데이터를 받아오는걸 했었음. but,
    // 2번 시행전 퀴즈그룹데이터가 필요한경우를 대비해  (add script같은)
    // 퀴즈그룹데이터 얻기 관련 모든 함수에 서버로부터 데이터 참조 코드를 넣어야 함 (=getQuizGroupSummaryList(callback))
    // 함수가 다 비대해짐. so, 1번을 먼저 시행하면 2번 경우 제외한
    // 모든 퀴즈그룹데이터는 메모리의 데이터만 참조해도 왠만히 커버가능.
    public void initQuizGroupList( Integer userId ){
        getQuizGroupSummaryList( userId, new QuizGroupModel.GetQuizGroupCallback(){
            @Override
            public void onSuccess(List<QuizGroupSummary> quizGroupSummaryList) {
            }
            @Override
            public void onFail(EResultCode resultCode) {
                // TODO ?
                return;
            }
        });
    }

    public Integer getMAX_QUIZ_GROUP_LIST_COUNT() {
        return MAX_QUIZ_GROUP_LIST_COUNT;
    }

    public Integer getQuizGroupSummaryCount(){
        return this.mQuizGroupSummaryList.size();
    }

    public Integer getQuizGroupIdByName( String quizGroupName ){
        for( QuizGroupSummary summary: mQuizGroupSummaryList){
            if( summary.getTitle().equals(quizGroupName)){
                return summary.getQuizGroupId();
            }
        }
        return -1;
    }

    public String getQuizGroupNameById( Integer quizGroupId ){
        for( QuizGroupSummary summary: mQuizGroupSummaryList){
            if( summary.getQuizGroupId() == quizGroupId ){
                return summary.getTitle();
            }
        }
        return null;
    }

    public List<String> getQuizGroupNames(){
        final List<String> names = new LinkedList<>();
        for(QuizGroupSummary summary : mQuizGroupSummaryList){
            names.add( summary.getTitle() );
        }
        return names;
    }

    public QuizGroupSummary getQuizGroupSummary( final int arrayIndex ){
        if( 0 > arrayIndex || mQuizGroupSummaryList.size() < arrayIndex )
            throw new IllegalArgumentException(EResultCode.INVALID_ARGUMENT,
                    "QuizGroup ListView Position is invalid(pos:"+arrayIndex+", listSize:"+ mQuizGroupSummaryList.size());
        return mQuizGroupSummaryList.get( arrayIndex );
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
                    overwriteQuizGroupSummarysAll( quizGroupSummaryList );
                    callback.onSuccess( mQuizGroupSummaryList );
                } else {
                    // 서버 응답 에러
                    Log.e("AppContent", "onGetQuizGroupSummaryList() UnkownERROR : "+ response.getResultCodeString());
                    callback.onFail( response.getResultCode() );
                }
            }
        };
    }

    // 퀴즈그룹 메뉴에서 퀴즈그룹 생성한 경우.
    // 서버에 저장 -> 클라에 저장.
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
                    overwriteQuizGroupSummarysAll( quizGroupSummaryList );
                    callback.onSuccess( );
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
                    overwriteQuizGroupSummarysAll( quizGroupSummaryList );
                    callback.onSuccess( mQuizGroupSummaryList );
                } else {
                    // 서버 응답 에러
                    Log.e("AppContent", "onGetQuizGroupSummaryList() UnkownERROR : "+ response.getResultCodeString());
                    callback.onFail( response.getResultCode() );
                }
            }
        };
    }

    private void overwriteQuizGroupSummarysAll(List<Map<String, Object>> quizGroupSummaryList ){
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


    public void addQuizGroupDetail( Integer userId, Integer quizGroupId, Integer scriptId, final AddQuizGroupCallback callback ){
        Request request = new Request( EProtocol2.PID.AddUserQuizGroupDetail );
        request.set(EProtocol.UserID, userId);
        request.set(EProtocol.QuizGroupId, quizGroupId);
        request.set(EProtocol.ScriptId, scriptId);
        AsyncNet net = new AsyncNet( request, onAddQuizGroupDetail(quizGroupId, callback) );
        net.execute();
    }

    private AsyncNet.Callback onAddQuizGroupDetail( final Integer quizGroupId, final AddQuizGroupCallback callback ) {
        return new AsyncNet.Callback() {
            @Override
            public void onResponse(Response response) {
                if (response.isSuccess()) {
                    List<Integer> quizGroupScriptIdList = (List)response.get(EProtocol.ScriptIds);
                    Log.e("AppContent", "onAddQuizGroupDetail(): "+ quizGroupScriptIdList.toString());
                    // save script list
                    overwriteQuizGroupScriptIdList( quizGroupId, quizGroupScriptIdList );
                    callback.onSuccess();
                } else {
                    // 서버 응답 에러
                    Log.e("AppContent", "onGetQuizGroupSummaryList() UnkownERROR : "+ response.getResultCodeString());
                    callback.onFail( response.getResultCode() );
                }
            }
        };
    }

    // Add Script메뉴에서 기존퀴즈그룹에 스크립트를 추가한경우.
    // 클라에 저장. 서버는 이미 저장됨.
    public void overwriteQuizGroupScriptIdList(Integer quizGroupId, List<Integer> scriptIds )
    {
        // 기존퀴즈그룹에 스크립트를 추가한경우
        // 새로 추가된 스크립트를 포함해 UI에 보일순서 재 소팅된..
        // 퀴즈 그룹 내의 스크립트 리스트를 저장.
        for( QuizGroupDetail detail : mQuizGroupDetailList ){
            if( detail.getQuizGroupId() == quizGroupId ){
                detail.setScriptIds(scriptIds);
            }
        }
    }
}
