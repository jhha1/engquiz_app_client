package kr.jhha.engquiz.quizgroup;


import java.util.Map;

import kr.jhha.engquiz.data.remote.EProtocol;
import kr.jhha.engquiz.data.remote.EResultCode;
import kr.jhha.engquiz.util.StringHelper;
import kr.jhha.engquiz.util.exception.system.IllegalArgumentException;

/**
 * Created by jhha on 2016-12-17.
 */

public class QuizGroupSummary
{
    private Integer quizGroupId;
    private Integer state;
    private String title;
    private Integer uiOrder;

    public static final int STATE_PLAYING = 0;
    public static final int STATE_NEW = 1;
    public static final int STATE_OTHER = 2;
    public static final int STATE_NEWBUTTON = 3;

    public QuizGroupSummary()
    {
        quizGroupId = 0;
        state = -1;
        title = "";
        uiOrder = -1;
    }

    public static boolean isNull( QuizGroupSummary quizGroupSummary ){
        if( quizGroupSummary == null )
            return true;

        if( quizGroupSummary.quizGroupId == 0
                && quizGroupSummary.uiOrder == -1
                && quizGroupSummary.state == -1
                && quizGroupSummary.title.isEmpty())
            return true;

        return false;
    }

    public String toString() {
        return "quizGroupId("+ quizGroupId +"), "
                + "ui_order("+uiOrder+"), "
                + "state("+state+"), "
                + "title("+title+")";
    }

    public Integer getQuizGroupId() {
        return quizGroupId;
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

    public void setQuizGroupId(Integer quizGroupId) {
        this.quizGroupId = checkQuizGroupID(quizGroupId);
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

    public Integer checkQuizGroupID( Object quizGroupID ) {
        if( quizGroupID instanceof Integer ) {
            return checkQuizGroupID( (Integer)quizGroupID );
        } else {
            throw new IllegalArgumentException(EResultCode.INVALID_ARGUMENT, "QuizGroupId Type is not Integer. id:"+quizGroupID );
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

    public Integer checkQuizGroupID( Integer quizGroupID ) {
        if( quizGroupID <= 0 )
            throw new IllegalArgumentException(EResultCode.INVALID_ARGUMENT, "invalid QuizGroupId:"+quizGroupID);
        return quizGroupID;
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

    public void deserialize( Map<String, Object> summary ){
        quizGroupId = checkQuizGroupID( summary.get(EProtocol.QuizGroupId.toString()) );
        state = checkState( summary.get(EProtocol.QuizGroupState.toString()) );
        title = checkTitle( summary.get(EProtocol.QuizGroupTitle.toString()) );
        uiOrder = checkUiOrder( summary.get(EProtocol.QuizGroupUIOrder.toString()) );
    }



}