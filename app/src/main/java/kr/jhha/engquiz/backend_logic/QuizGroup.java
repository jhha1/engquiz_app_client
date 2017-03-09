package kr.jhha.engquiz.backend_logic;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class QuizGroup {
	public Integer id = 0;
	public Integer userId = -1;
	public Integer uiOrder = -1;
	public Integer state = -1;
	public String title = "";
	public List<Integer> scriptIndexes = new LinkedList<>();

	public static final int STATE_PLAYING = 0;
	public static final int STATE_NEW = 1;
	public static final int STATE_OTHER = 2;
	public static final int STATE_NEWBUTTON = 3;

	public static boolean isNull( QuizGroup quizgroup ){
		if( quizgroup == null )
			return true;
		
		if( quizgroup.id == 0 
				&& quizgroup.userId == -1
				&& quizgroup.uiOrder == -1
				&& quizgroup.state == -1
				&& quizgroup.title.isEmpty())
			return true;
		
		return false;
	}
	
	public static boolean checkQuizGroupID( Integer quizGroupID ) {
		if( quizGroupID <= 0 )
			return false;
		return true;
	}
	
	public static boolean checkState( int newState ){
		if( newState < STATE_PLAYING || newState > STATE_NEWBUTTON ) {
			return false;
		}
		return true;
	}
	
	public String toString() {
		return "id("+id+"), "
				+ "usrID("+userId+"), "
				+ "ui_order("+uiOrder+"), "
				+ "state("+state+"), "
				+ "title("+title+"), "
				+ "scriptIndexes("+scriptIndexes.toString()+")";
	}
}
