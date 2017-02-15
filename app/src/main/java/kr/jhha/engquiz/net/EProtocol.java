package kr.jhha.engquiz.net;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum EProtocol {
	
	// required 
	JSON ("JSON"),
	
	PID ("pid"),	// string
	iPID ("iPID"),  // integer 
	AccountID("AccountID"),
	
	// required in response
	CODE("code"),
	MSG("msg"),
	
	
	// others
	Nickname ("Nickname"),
	ScriptTitle("title"),
	ScriptIndex("index"),
	ScriptRevision("revision"),
	ScriptSentences("sentences"),
	ParsedSciprt("parsedScript"),
	HasParsedScript("HasParsedScript"),
	SciprtPDF("scriptPDF"),
	SciprtDOCX("scriptDocx"),
	ScriptNames("ScriptNames"),
	MatchedScripts("MatchedScripts"),
	CheckSync_ResultCode("CheckSyncResultCode"),
	CheckSync_NewScriptList("CheckSync_NewScriptList"),
	CheckSync_NeedUpdateScriptList("CheckSync_NeedUpdateScriptList"),

	
	// for test
	TEST("test"),
	TEST_TIME("time"),
	TEST_FLOAT("-float"),
	TEST_INTMAX("intmax"),
	TEST_LIST("list"),
	TEST_COMPLEXMAP("complexMap"),
	
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
