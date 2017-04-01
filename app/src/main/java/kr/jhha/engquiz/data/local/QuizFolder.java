package kr.jhha.engquiz.data.local;


import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import kr.jhha.engquiz.data.remote.EProtocol;
import kr.jhha.engquiz.data.remote.EResultCode;
import kr.jhha.engquiz.util.StringHelper;
import kr.jhha.engquiz.util.exception.system.IllegalArgumentException;

/**
 * Created by jhha on 2016-12-17.
 */

public class QuizFolder
{
    private Integer quizFolderId;
    private Integer state;
    private String title;
    private Integer uiOrder;
    private Integer userId = -1;
    private List<Integer> scriptIds = new LinkedList<>();

    public static final int STATE_PLAYING = 0;
    public static final int STATE_NEW = 1;
    public static final int STATE_OTHER = 2;
    public static final int STATE_NEWBUTTON = 3;

    public static final String TEXT_NEW = "New..";

    public QuizFolder()
    {
        quizFolderId = 0;
        state = -1;
        title = "";
        uiOrder = -1;
    }

    public static boolean isNull( QuizFolder quizfolder ){
        if( quizfolder == null )
            return true;

        if( quizfolder.quizFolderId == 0
                && quizfolder.uiOrder == -1
                && quizfolder.state == -1
                && quizfolder.title.isEmpty()
                && quizfolder.userId == -1
                && quizfolder.scriptIds.isEmpty())
            return true;

        return false;
    }

    // QuizFolderInfo={id=0, userId=-1, uiOrder=-1, state=-1, title=, scriptIdsJson=, createdTime_UnixTimestamp=0, scriptIndexes=[], teminatedNEWState=true}, MSG=SUCCESS, UserID=7}
    public static boolean isNull( Map<String, Object> quizfolder ){
        if( quizfolder == null || quizfolder.isEmpty() )
            return true;

        if( quizfolder.get("quizFolderId") instanceof Integer && (Integer)quizfolder.get("quizFolderId") != 0 ) return false;
        if( quizfolder.get("userId") instanceof Integer && (Integer)quizfolder.get("userId") != -1 ) return false;
        if( quizfolder.get("uiOrder") instanceof Integer && (Integer)quizfolder.get("uiOrder") != -1 ) return false;
        if( quizfolder.get("state") instanceof Integer && (Integer)quizfolder.get("state") != -1 ) return false;
        if( quizfolder.get("title") instanceof String ){
            String title = (String)quizfolder.get("title");
            if( title != null && !title.isEmpty() )
                return false;
        }
        if( quizfolder.get("scriptIdsJson") instanceof String ){
            String scriptIdsJson = (String)quizfolder.get("scriptIdsJson");
            if( scriptIdsJson != null && !scriptIdsJson.isEmpty() )
                return false;
        }
        if( quizfolder.get("scriptIndexes") instanceof List ){
            List scriptIds = (List)quizfolder.get("scriptIndexes");
            if( scriptIds != null && !scriptIds.isEmpty() )
                return false;
        }
        return true;
    }

    public String toString() {
        return "quizFolderId("+ quizFolderId +"), "
                + "ui_order("+uiOrder+"), "
                + "state("+state+"), "
                + "title("+title+")"
                + "usrID("+userId+"), "
                + "scriptIds("+ scriptIds.toString()+")";
    }

    public Integer getId() {
        return quizFolderId;
    }

    public Integer getState() {
        return state;
    }

    public String getTitle() {
        return title;
    }

    public Integer getUiOrder() {
        return uiOrder;
    }

    public void setQuizFolderId(Integer quizFolderId) {
        this.quizFolderId = checkQuizFolderID(quizFolderId);
    }

    public Integer getUserId() {
        return userId;
    }

    public List<Integer> getScriptIds() {
        return scriptIds;
    }

    public void setState(Integer state) {
        this.state = checkState(state);
    }

    public void setTitle(String title) {
        this.title = checkTitle(title);
    }

    public void setUiOrder(Integer uiOrder) {
        this.uiOrder = checkUiOrder(uiOrder);
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void setScriptIds(List<Integer> scriptIds) {
        if( scriptIds == null || scriptIds.isEmpty() ){
            throw new IllegalArgumentException(EResultCode.INVALID_ARGUMENT, "setScriptIds() ids is null." );
        }
        this.scriptIds = scriptIds;
    }

    public Integer checkQuizFolderID( Object quizFolderID ) {
        if( quizFolderID instanceof Integer ) {
            return checkQuizFolderID( (Integer)quizFolderID );
        } else {
            throw new IllegalArgumentException(EResultCode.INVALID_ARGUMENT, "QuizFolderId Type is not Integer. id:"+quizFolderID );
        }
    }

    public Integer checkState( Object newState ) {
        if( newState instanceof Integer ) {
            return checkState( (Integer)newState );
        } else {
            throw new IllegalArgumentException(EResultCode.INVALID_ARGUMENT, "newState Type is not Integer. state:"+newState );
        }
    }

    public String checkTitle( Object title ) {
        if( title instanceof String ) {
            return checkTitle( (String)title );
        } else {
            throw new IllegalArgumentException(EResultCode.INVALID_ARGUMENT, "title Type is not String. title:"+title );
        }
    }

    public Integer checkUiOrder( Object uiOrder ) {
        if( uiOrder instanceof Integer ) {
            return checkUiOrder( (Integer)uiOrder );
        } else {
            throw new IllegalArgumentException(EResultCode.INVALID_ARGUMENT, "uiOrder Type is not Integer. id:"+uiOrder );
        }
    }

    public Integer checkUserId( Object userId ){
        if( userId instanceof Integer ) {
            return checkUserId((Integer)userId);
        } else {
            throw new IllegalArgumentException(EResultCode.INVALID_ARGUMENT, "UserId Type is not Integer. id:"+userId );
        }
    }

    public List<Integer> checkScriptIds( Object scriptIds ){
        if( scriptIds instanceof List ) {
            for( Object id: (List)scriptIds){
                if( false == (id instanceof Integer) )
                    throw new IllegalArgumentException(EResultCode.INVALID_ARGUMENT, "scriptId Type is not Integer. id:"+id );
            }
            return checkScriptIds((List)scriptIds);
        } else {
            throw new IllegalArgumentException(EResultCode.INVALID_ARGUMENT, "scriptIds Type is not List. id:"+scriptIds );
        }
    }

    public static Integer checkQuizFolderID( Integer quizFolderID ) {
        if( quizFolderID <= 0 )
            throw new IllegalArgumentException(EResultCode.INVALID_ARGUMENT, "invalid QuizFolderId:"+quizFolderID);
        return quizFolderID;
    }

    public Integer checkState( Integer newState ){
        if( newState < STATE_PLAYING || newState > STATE_NEWBUTTON ) {
            throw new IllegalArgumentException(EResultCode.INVALID_ARGUMENT, "invalid newState:"+newState);
        }
        return newState;
    }

    public String checkTitle( String title ){
        if(StringHelper.isNullString(title)) {
            throw new IllegalArgumentException(EResultCode.INVALID_ARGUMENT, "null title:"+title);
        }
        return title;
    }

    public Integer checkUiOrder( Integer uiOrder ){
        if( uiOrder <= 0 ){
            throw new IllegalArgumentException(EResultCode.INVALID_ARGUMENT, "invalid uiOrder:"+uiOrder);
        }
        return uiOrder;
    }

    public Integer checkUserId( Integer userId ){
        return User.checkUserID(userId);
    }

    public List<Integer> checkScriptIds( List scriptIds ){
        if( scriptIds == null  ){
            throw new IllegalArgumentException(EResultCode.INVALID_ARGUMENT, "invalid scriptIds:"+scriptIds);
        }
        return scriptIds;
    }

    public void deserialize( Map<String, Object> m ){
        quizFolderId = checkQuizFolderID( m.get(EProtocol.QuizFolderId.toString()) );
        state = checkState( m.get(EProtocol.QuizFolderState.toString()) );
        title = checkTitle( m.get(EProtocol.QuizFolderTitle.toString()) );
        uiOrder = checkUiOrder( m.get(EProtocol.QuizFolderUIOrder.toString()) );
        userId = checkUserId( m.get(EProtocol.UserID.toString()) );
        // scriptId 리스트는 별도로 받아오기 때문에, 지금은 셋팅 안함
        // 이때는 서버가 empty list로 보냄.
        //scriptIds = checkScriptIds( m.get(EProtocol.ScriptIds.toString()) );
    }


}