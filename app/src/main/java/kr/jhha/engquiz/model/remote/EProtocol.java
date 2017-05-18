package kr.jhha.engquiz.model.remote;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum EProtocol {

	// Required
	JSON ("JSON"),
	PID ("pid"),	// string
	iPID ("iPID"),  // integer
	// Response Only
	CODE("code"),
	MSG("msg"),

	// user
	UserID ("UserID"),
	UserName("UserName"),
	IsExistedUser("IsExistedUser"),
	IsAdmin("IsAdmin"),

	// Script
	ScriptId("ScriptId"),
	ScriptIds("ScriptIds"),
	ScriptTitle("title"),
	ScriptSentences("sentences"),
	ParsedSciprt("parsedScript"),
	SciprtPDF("scriptPDF"),
	SciprtDOCX("scriptDocx"),

	// Sentence
	SentenceId("SentenceId"),
	Revision("Revision"),
	SentenceKo("SenteceKo"),
	SentenceEn("SenteceEn"),

	// QuizFolder
	QuizFolderId("QuizFolderId"),
	QuizFolderState("QuizFolderState"),
	QuizFolderTitle("QuizFolderTitle"),
	QuizFolderUIOrder("QuizFolderUIOrder"),
	QuizFolder("QuizFolder"),
	QuizFolders("QuizFolders"),
	DetachScriptFromAllFolder("DetachScriptFromAllFolder"),

	// Sync
	SyncResult("SyncResult"),

	// REPORT
	ReportCountAll("ReportCountAll"),
	ReportList("ReportList"),
	ReportModifyType("ReportModifyType"),
	
	NULL("null");
	
	private static Map<String, EProtocol> lookup = null; 
	static 
	{
		lookup = new HashMap<String, EProtocol>();
		for( EProtocol e : EnumSet.allOf(EProtocol.class) ) 
		{
			String upperKey = e.value().trim().toUpperCase();
		    lookup.put( upperKey, e );
		}
	}

	private String value;
	
	private EProtocol( String value ) 
	{
		this.value = value;
	}
	
	public String value() 
	{
		return value;
	}
	
	public static EProtocol toEnum( String key )
	{
		if( key == null || key.isEmpty() )
			return EProtocol.NULL;
		
		String upperKey = key.trim().toUpperCase();
		return (lookup.containsKey(upperKey)? lookup.get(upperKey) : NULL );
	}

}

