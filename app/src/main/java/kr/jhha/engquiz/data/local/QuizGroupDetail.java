package kr.jhha.engquiz.data.local;

import android.util.Log;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import kr.jhha.engquiz.data.remote.EProtocol;
import kr.jhha.engquiz.data.remote.EResultCode;
import kr.jhha.engquiz.quizgroup.QuizGroupSummary;
import kr.jhha.engquiz.util.exception.system.IllegalArgumentException;

public class QuizGroupDetail {
	private QuizGroupSummary summary = null;
	private Integer userId = -1;
	private List<Integer> scriptIds = new LinkedList<>();

	public static boolean isNull( QuizGroupDetail quizgroup ){
		if( quizgroup == null )
			return true;
		if( QuizGroupSummary.isNull( quizgroup.summary )
				&& quizgroup.userId == -1
				&& quizgroup.scriptIds.isEmpty())
			return true;
		
		return false;
	}

	// QuizGroupInfo={id=0, userId=-1, uiOrder=-1, state=-1, title=, scriptIdsJson=, createdTime_UnixTimestamp=0, scriptIndexes=[], teminatedNEWState=true}, MSG=SUCCESS, UserID=7}
	public static boolean isNull( Map<String, Object> quizgroup ){
		if( quizgroup == null || quizgroup.isEmpty() )
			return true;

		if( quizgroup.get("quizGroupId") instanceof Integer && (Integer)quizgroup.get("quizGroupId") != 0 ) return false;
		if( quizgroup.get("userId") instanceof Integer && (Integer)quizgroup.get("userId") != -1 ) return false;
		if( quizgroup.get("uiOrder") instanceof Integer && (Integer)quizgroup.get("uiOrder") != -1 ) return false;
		if( quizgroup.get("state") instanceof Integer && (Integer)quizgroup.get("state") != -1 ) return false;
		if( quizgroup.get("title") instanceof String ){
			String title = (String)quizgroup.get("title");
			if( title != null && !title.isEmpty() )
				return false;
		}
		if( quizgroup.get("scriptIdsJson") instanceof String ){
			String scriptIdsJson = (String)quizgroup.get("scriptIdsJson");
			if( scriptIdsJson != null && !scriptIdsJson.isEmpty() )
				return false;
		}
		if( quizgroup.get("scriptIndexes") instanceof List ){
			List scriptIds = (List)quizgroup.get("scriptIndexes");
			if( scriptIds != null && !scriptIds.isEmpty() )
				return false;
		}
		return true;
	}

	public String toString() {
		return summary.toString() + ", "
				+ "usrID("+userId+"), "
				+ "scriptIds("+ scriptIds.toString()+")";
	}

	public QuizGroupSummary getSummary(){
		return summary;
	}

	public Integer getQuizGroupId() {
		return summary.getQuizGroupId();
	}

	public Integer getState() {
		return summary.getState();
	}

	public String getTitle() {
		return summary.getTitle();
	}

	public Integer getUiOrder() {
		return summary.getUiOrder();
	}

	public Integer getUserId() {
		return userId;
	}

	public List<Integer> getScriptIds() {
		return scriptIds;
	}

	public void setSummary(QuizGroupSummary summary) {
		this.summary = summary;
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

	public void setQuizGroupId(Integer quizGroupId) {
		summary.setQuizGroupId(quizGroupId);
	}

	public void setState(Integer state) {
		summary.setState(state);
	}

	public void setTitle(String title) {
		summary.setTitle(title);
	}

	public void setUiOrder(Integer uiOrder) {
		summary.setUiOrder(uiOrder);
	}

	public void deserialize( Map<String, Object> quizGroupDetail ){
		if( quizGroupDetail == null ){
			throw new IllegalArgumentException( "deserialize() quizGroupDetail is null");
		}
		summary.deserialize( quizGroupDetail );
		setUserId( (Integer) quizGroupDetail.get(EProtocol.UserID.toString()) );
		setScriptIds( (List) quizGroupDetail.get(EProtocol.ScriptIds.toString()) );
	}
}
