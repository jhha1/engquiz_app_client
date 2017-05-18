package kr.jhha.engquiz.model.local;


import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import kr.jhha.engquiz.util.exception.EResultCode;
import kr.jhha.engquiz.util.StringHelper;
import kr.jhha.engquiz.util.exception.system.MyIllegalArgumentException;

/**
 * Created by jhha on 2016-12-17.
 */

public class QuizFolder
{
    private Integer quizFolderId;
    private Integer state;
    private String title;
    private Integer uiOrder;
    private Integer userId;
    private List<Integer> scriptIds = new LinkedList<>();

    public static final int STATE_PLAYING = 0;
    public static final int STATE_NEW_ADDED = 1;
    public static final int STATE_OTHER = 2;
    public static final int STATE_NEWBUTTON = 3;

    public static final String TEXT_NEW_FOLDER = "New Folder";
    public static final String TEXT_ADD_SCRIPT_INTO_QUIZFOLDER = "Add Script";

    // field for serialize/unserialize
    public static final String Field_USERID = "USERID";
    public static final String Field_QUIZFOLDER_ID = "QUIZFOLDER_ID";
    public static final String Field_UI_ORDER = "UI_ORDER";
    public static final String Field_STATE = "STATE";
    public static final String Field_TITLE = "TITLE";
    public static final String Field_SCRIPT_ID_LIST = "SCRIPT_ID_LIST";

    public static final int TITLE_MAX_LEN = 30;

    public QuizFolder()
    {
        userId = -1;
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

    // QuizFolderInfo={sentenceId=0, userId=-1, uiOrder=-1, state=-1, title=, scriptIdsJson=, createdTime_UnixTimestamp=0, scriptIndexes=[], teminatedNEWState=true}, MSG=SUCCESS, UserID=7}
    public static boolean isNull( Map<String, Object> quizfolder ){
        if( quizfolder == null || quizfolder.isEmpty() )
            return true;

        if( quizfolder.get(Field_QUIZFOLDER_ID) instanceof Integer && (Integer)quizfolder.get(Field_QUIZFOLDER_ID) != 0 ) return false;
        if( quizfolder.get(Field_USERID) instanceof Integer && (Integer)quizfolder.get(Field_USERID) != -1 ) return false;
        if( quizfolder.get(Field_UI_ORDER) instanceof Integer && (Integer)quizfolder.get(Field_UI_ORDER) != -1 ) return false;
        if( quizfolder.get(Field_STATE) instanceof Integer && (Integer)quizfolder.get(Field_STATE) != -1 ) return false;
        if( quizfolder.get(Field_TITLE) instanceof String ){
            String title = (String)quizfolder.get(Field_TITLE);
            if( title != null && !title.isEmpty() )
                return false;
        }
        if( quizfolder.get(Field_SCRIPT_ID_LIST) instanceof List ){
            List scriptIds = (List)quizfolder.get(Field_SCRIPT_ID_LIST);
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
        if( scriptIds == null ){
            throw new MyIllegalArgumentException(EResultCode.INVALID_ARGUMENT, "setScriptIds() ids is null." );
        }
        this.scriptIds = scriptIds;
    }

    public Integer checkQuizFolderID( Object quizFolderID ) {
        if( quizFolderID instanceof Integer ) {
            return checkQuizFolderID( (Integer)quizFolderID );
        } else {
            throw new MyIllegalArgumentException(EResultCode.INVALID_ARGUMENT, "QuizFolderId Type is not Integer. sentenceId:"+quizFolderID );
        }
    }

    public Integer checkState( Object newState ) {
        if( newState instanceof Integer ) {
            return checkState( (Integer)newState );
        } else {
            throw new MyIllegalArgumentException(EResultCode.INVALID_ARGUMENT, "newState Type is not Integer. state:"+newState );
        }
    }

    public String checkTitle( Object title ) {
        if( title instanceof String ) {
            return checkTitle( (String)title );
        } else {
            throw new MyIllegalArgumentException(EResultCode.INVALID_ARGUMENT, "title Type is not String. title:"+title );
        }
    }

    public Integer checkUiOrder( Object uiOrder ) {
        if( uiOrder instanceof Integer ) {
            return checkUiOrder( (Integer)uiOrder );
        } else {
            throw new MyIllegalArgumentException(EResultCode.INVALID_ARGUMENT, "uiOrder Type is not Integer. sentenceId:"+uiOrder );
        }
    }

    public Integer checkUserId( Object userId ){
        if( userId instanceof Integer ) {
            return checkUserId((Integer)userId);
        } else {
            throw new MyIllegalArgumentException(EResultCode.INVALID_ARGUMENT, "UserId Type is not Integer. sentenceId:"+userId );
        }
    }

    public List<Integer> checkScriptIds( Object scriptIds ){
        if( scriptIds instanceof List ) {
            for( Object id: (List)scriptIds){
                if( false == (id instanceof Integer) )
                    throw new MyIllegalArgumentException(EResultCode.INVALID_ARGUMENT, "scriptId Type is not Integer. sentenceId:"+id );
            }
            return checkScriptIds((List)scriptIds);
        } else {
            throw new MyIllegalArgumentException(EResultCode.INVALID_ARGUMENT, "scriptIds Type is not List. sentenceId:"+scriptIds );
        }
    }

    public static Integer checkQuizFolderID( Integer quizFolderID ) {
        if( quizFolderID <= 0 )
            throw new MyIllegalArgumentException(EResultCode.INVALID_ARGUMENT, "invalid QuizFolderId:"+quizFolderID);
        return quizFolderID;
    }

    public Integer checkState( Integer newState ){
        if( newState < STATE_PLAYING || newState > STATE_NEWBUTTON ) {
            throw new MyIllegalArgumentException(EResultCode.INVALID_ARGUMENT, "invalid newState:"+newState);
        }
        return newState;
    }

    public String checkTitle( String title ){
        if(StringHelper.isNull(title)) {
            throw new MyIllegalArgumentException(EResultCode.INVALID_ARGUMENT, "null title:"+title);
        }
        return title;
    }

    public Integer checkUiOrder( Integer uiOrder ){
        if( uiOrder <= 0 ){
            throw new MyIllegalArgumentException(EResultCode.INVALID_ARGUMENT, "invalid uiOrder:"+uiOrder);
        }
        return uiOrder;
    }

    public Integer checkUserId( Integer userId ){
        return User.checkUserID(userId);
    }

    public List<Integer> checkScriptIds( List scriptIds ){
        if( scriptIds == null  ){
            throw new MyIllegalArgumentException(EResultCode.INVALID_ARGUMENT, "invalid scriptIds:"+scriptIds);
        }
        return scriptIds;
    }

    public void deserialize( String quizFolderJsonString ){

        Map<String, Object> m = StringHelper.json2map(quizFolderJsonString);
        quizFolderId = checkQuizFolderID( m.get(Field_QUIZFOLDER_ID) );
        state = checkState( m.get(Field_STATE) );
        title = checkTitle( m.get(Field_TITLE) );
        uiOrder = checkUiOrder( m.get(Field_UI_ORDER) );
        userId = checkUserId( m.get(Field_USERID) );
        // scriptId 리스트는 별도로 받아오기 때문에, 지금은 셋팅 안함
        // 이때는 서버가 empty list로 보냄.
        //scriptIds = checkScriptIds( m.get(EProtocol.ScriptIds.toString()) );
    }

    public static boolean isNewButton(String folderName){
        return folderName.equals( TEXT_NEW_FOLDER );
    }
}