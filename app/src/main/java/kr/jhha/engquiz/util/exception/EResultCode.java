package kr.jhha.engquiz.util.exception;

import kr.jhha.engquiz.util.StringHelper;

public enum EResultCode {
	
	SUCCESS ( 0000 ),

	// common
	INVALID_ARGUMENT ( 1001 ),
	NOEXSIT (1002),
	NULL_VALUE(1003),
	INVALID_DATATYPE(1004),

	// pid
	INVALID_PID ( 1010 ),

	// user
	INVALID_USERID ( 1100 ),
	INVALID_USER_ID_RANGE ( 1101 ),
	INVALID_USERNAME( 1102 ),
	INVALID_USERNAME_NONE_ALPHANUMERIC( 1103 ),
	INVALID_USERNAME_LENGTH( 1104 ),
	USERNAME_DUPLICATED( 1105 ),
	USER_CREATED_BUT_FAILED_GET_USERINFO(1106),
	NONEXIST_USER( 1107 ),

	// quiz folder
	INVALID_QUIZFOLDER_ID(1211),
	FAIL_ADD_SCRIPT_INTO_QUIZFOLDER(1212),
	QUIZFOLDER__NOEXIST_QUIZFOLDERID(1213),
	QUIZFOLDER__NOALLOWED_DELETE_NEWBUTTON(1214),
	quizFolder__NOALLOWED_DELETE_PLAYING(1215),
	QUIZFOLDER__NOEXIST_STATE_NEW(1216),
	QUIZFOLDER__NOEXIST_SCRIPTS(1217),

	// add script into quiz folder
	INVALID_SCRIPT_ID(1301),
	SCRIPT_DUPLICATED(1302),
	SCRIPT__NO_HAS_KR_OR_EN(1320),

	// sentence
	INVALID_SENTENCE ( 1401 ),
	INVALID_SENTENCE_ID ( 1402 ),
	INVALID_SENTENCE_TEXT( 1403 ),
	USER_SENTENCE_REVISION_IS_OLD_THEN_SYSTEMS(1404),

	ENCODING_ERR(9994),
	SYSTEM_ERR ( 9995 ),
	NETWORK_ERR ( 9996 ),
	DB_ERR ( 9997 ),
	UNKNOUN_ERR ( 9998 ),
	MAX( 9999 );
	
	
	private Integer code;
	
	EResultCode( int value )
	{
		this.code = value;
	}
	
	public Integer intCode() 
	{
		return code;
	}
	
	public String stringCode()
	{
		return code.toString();
	}

	public static EResultCode findBy(String value ) {
		if(StringHelper.isNull(value)) {
			throw new IllegalArgumentException("Null arg. value:"+value);
		}

		Integer intVaule = Integer.parseInt( value );
		EResultCode[] codes = EResultCode.values();
		for( EResultCode e: codes ) {
			if( e.intCode().equals(intVaule) )
				return e;
		}
		throw new IllegalArgumentException("No Existed EResultCode. value:"+value);
	}
}
