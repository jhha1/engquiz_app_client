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
import kr.jhha.engquiz.util.exception.system.IllegalArgumentException;

/**
 * Created by thyone on 2017-03-19.
 */

public class QuizFolderRepository {

    public interface GetQuizFolderCallback {
        void onSuccess( List<QuizFolder> quizFolders );
        void onFail(EResultCode resultCode);
    }

    public interface AddQuizFolderCallback {
        void onSuccess( );
        void onFail(EResultCode resultCode);
    }

    public interface DelQuizFolderCallback {
        void onSuccess( List<QuizFolder> quizFolders );
        void onFail(EResultCode resultCode);
    }

    private static QuizFolderRepository instance = new QuizFolderRepository();
    private QuizFolderRepository() {}
    public static QuizFolderRepository getInstance() {
        return instance;
    }


    private List<QuizFolder> mQuizFolders = new ArrayList<>();
    private Integer MAX_QUIZ_GROUP_LIST_COUNT = 30;

    /*
    // 퀴즈폴더리스트 서버로부터 받아오는 시점.
    // 1. login 성공시.
    // 2. 유저가 퀴즈폴더 메뉴를 클릭해 퀴즈폴더 리스트를 볼려고 할때, 메모리에 리스트가 없으면.
    //
    // 원래 2번만으로 데이터를 받아오는걸 했었음. but,
    // 2번 시행전 퀴즈폴더데이터가 필요한경우를 대비해  (add script같은)
    // 퀴즈폴더데이터 얻기 관련 모든 함수에 서버로부터 데이터 참조 코드를 넣어야 함 (=getQuizFolders(callback))
    // 함수가 다 비대해짐. so, 1번을 먼저 시행하면 2번 경우 제외한
    // 모든 퀴즈폴더데이터는 메모리의 데이터만 참조해도 왠만히 커버가능.
    */
    public void initQuizFolderList( Integer userId ){
        getQuizFolders( userId, new QuizFolderRepository.GetQuizFolderCallback(){
            @Override
            public void onSuccess(List<QuizFolder> quizFolders) {
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

    public Integer getQuizFolderCount(){
        return this.mQuizFolders.size();
    }

    public Integer getQuizFolderIdByName( String quizFolderName ){
        for( QuizFolder quizFolder: mQuizFolders){
            if( quizFolder.getTitle().equals(quizFolderName)){
                return quizFolder.getId();
            }
        }
        return -1;
    }

    public String getQuizFolderNameById( Integer quizFolderId ){
        for( QuizFolder quizFolder: mQuizFolders){
            if( quizFolder.getId() == quizFolderId ){
                return quizFolder.getTitle();
            }
        }
        return null;
    }

    public List<String> getQuizFolderNames(){
        final List<String> names = new LinkedList<>();
        for(QuizFolder quizFolder : mQuizFolders){
            names.add( quizFolder.getTitle() );
        }
        return names;
    }

    public QuizFolder getQuizFolder(final int arrayIndex ){
        if( 0 > arrayIndex || mQuizFolders.size() < arrayIndex )
            throw new IllegalArgumentException(EResultCode.INVALID_ARGUMENT,
                    "QuizFolder ListView Position is invalid(pos:"+arrayIndex+", listSize:"+ mQuizFolders.size());
        return mQuizFolders.get( arrayIndex );
    }

    public void getQuizFolders(Integer userId, final QuizFolderRepository.GetQuizFolderCallback callback ) {
        if( null != mQuizFolders && false == mQuizFolders.isEmpty() ){
            callback.onSuccess(mQuizFolders);
            return;
        }

        Request request = new Request( EProtocol2.PID.GetUserQuizFolders);
        request.set(EProtocol.UserID, userId);
        AsyncNet net = new AsyncNet( request, onGetQuizFolders(callback) );
        net.execute();
    }

    private AsyncNet.Callback onGetQuizFolders(final QuizFolderRepository.GetQuizFolderCallback callback ) {
        return new AsyncNet.Callback() {
            @Override
            public void onResponse(Response response) {
                if (response.isSuccess()) {
                    List<Map<String, Object>> quizFolders = (List)response.get(EProtocol.QuizFolders);
                    Log.e("AppContent", "onGetQuizFolders(): "+ quizFolders.toString());
                    // save quiz folders
                    overwriteQuizFoldersAll( quizFolders );
                    callback.onSuccess(mQuizFolders);
                } else {
                    // 서버 응답 에러
                    Log.e("AppContent", "onGetQuizFolders() UnkownERROR : "+ response.getResultCodeString());
                    callback.onFail( response.getResultCode() );
                }
            }
        };
    }

    // 퀴즈폴더 메뉴에서 퀴즈폴더 생성한 경우.
    // 서버에 저장 -> 클라에 저장.
    public void addQuizFolder( Integer userId, String quizFolderTitle, List<Integer> scriptIds, final AddQuizFolderCallback callback ){
        Request request = new Request( EProtocol2.PID.AddUserQuizFolder );
        request.set(EProtocol.UserID, userId);
        request.set(EProtocol.QuizFolderTitle, quizFolderTitle);
        request.set(EProtocol.ScriptIds, scriptIds);
        AsyncNet net = new AsyncNet( request, onAddQuizFolder(callback) );
        net.execute();
    }

    private AsyncNet.Callback onAddQuizFolder( final AddQuizFolderCallback callback ) {
        return new AsyncNet.Callback() {
            @Override
            public void onResponse(Response response) {
                if (response.isSuccess()) {
                    List<Map<String, Object>> quizFolders = (List)response.get(EProtocol.QuizFolders);
                    Log.e("AppContent", "onAddQuizFolder(): "+ quizFolders.toString());
                    // save quiz folders
                    overwriteQuizFoldersAll( quizFolders );
                    callback.onSuccess( );
                } else {
                    // 서버 응답 에러
                    Log.e("AppContent", "onGetQuizFolders() UnkownERROR : "+ response.getResultCodeString());
                    callback.onFail( response.getResultCode() );
                }
            }
        };
    }

    public void delQuizFolder( Integer userId, Integer quizfolderId, final DelQuizFolderCallback callback ){
        Request request = new Request( EProtocol2.PID.DelUserQuizFolder );
        request.set(EProtocol.UserID, userId);
        request.set(EProtocol.QuizFolderId, quizfolderId);
        AsyncNet net = new AsyncNet( request, onDelQuizFolder(callback) );
        net.execute();
    }

    private AsyncNet.Callback onDelQuizFolder( final DelQuizFolderCallback callback ) {
        return new AsyncNet.Callback() {
            @Override
            public void onResponse(Response response) {
                if (response.isSuccess()) {
                    List<Map<String, Object>> quizFolders = (List)response.get(EProtocol.QuizFolders);
                    Log.e("AppContent", "onDelQuizFolder(): "+ quizFolders.toString());
                    // save summaries.
                    overwriteQuizFoldersAll( quizFolders );
                    callback.onSuccess(mQuizFolders);
                } else {
                    // 서버 응답 에러
                    Log.e("AppContent", "onGetQuizFolders() UnkownERROR : "+ response.getResultCodeString());
                    callback.onFail( response.getResultCode() );
                }
            }
        };
    }

    private void overwriteQuizFoldersAll(List<Map<String, Object>> quizFolders ){
        // 1. save into memory.
        if( mQuizFolders == null ) {
            mQuizFolders = new ArrayList<>();
        }
        mQuizFolders.clear();
        for( Map<String, Object> quizFolder : quizFolders ){
            QuizFolder obj = new QuizFolder();
            obj.deserialize( quizFolder );
            mQuizFolders.add( obj );
        }
        // 2. No save into file.
        //     -> download summaries from a server when a quizfolder menu first clicked.
    }


    public void addQuizFolderDetail( Integer userId, Integer quizFolderId, Integer scriptId, final AddQuizFolderCallback callback ){
        Request request = new Request( EProtocol2.PID.AddUserQuizFolderDetail );
        request.set(EProtocol.UserID, userId);
        request.set(EProtocol.QuizFolderId, quizFolderId);
        request.set(EProtocol.ScriptId, scriptId);
        AsyncNet net = new AsyncNet( request, onAddQuizFolderDetail(quizFolderId, callback) );
        net.execute();
    }

    private AsyncNet.Callback onAddQuizFolderDetail( final Integer quizFolderId, final AddQuizFolderCallback callback ) {
        return new AsyncNet.Callback() {
            @Override
            public void onResponse(Response response) {
                if (response.isSuccess()) {
                    List<Integer> quizFolderScriptIdList = (List)response.get(EProtocol.ScriptIds);
                    Log.e("AppContent", "onAddQuizFolderDetail(): "+ quizFolderScriptIdList.toString());
                    // save script list
                    overwriteQuizFolderScriptIdList( quizFolderId, quizFolderScriptIdList );
                    callback.onSuccess();
                } else {
                    // 서버 응답 에러
                    Log.e("AppContent", "onGetQuizFolders() UnkownERROR : "+ response.getResultCodeString());
                    callback.onFail( response.getResultCode() );
                }
            }
        };
    }

    // Add Script메뉴에서 기존퀴즈폴더에 스크립트를 추가한경우.
    // 클라에 저장. 서버는 이미 저장됨.
    public void overwriteQuizFolderScriptIdList(Integer quizFolderId, List<Integer> scriptIds )
    {
        // 기존퀴즈폴더에 스크립트를 추가한경우
        // 새로 추가된 스크립트를 포함해 UI에 보일순서 재 소팅된..
        // 퀴즈 폴더 내의 스크립트 리스트를 저장.
        for( QuizFolder quizFolder : mQuizFolders ){
            if( quizFolder.getId() == quizFolderId ){
                quizFolder.setScriptIds(scriptIds);
            }
        }
    }
}
