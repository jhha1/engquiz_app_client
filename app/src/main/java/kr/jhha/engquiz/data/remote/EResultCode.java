package kr.jhha.engquiz.data.remote;

import kr.jhha.engquiz.util.StringHelper;

public enum EResultCode {
	
	SUCCESS ( 0000 ),
	
	// pid
	INVALID_PID ( 1000 ),
	INVALID_ARGUMENT ( 1001 ),
	
	// account  
	INVALID_USERID ( 1010 ),
	ACCOUNT_LOGIN ( 1011 ),
	ACCOUNT_LOGOUT( 1012 ),
	ACCOUNT_BLOCK ( 1013 ),
	ACCOUNT_SIGNOUT ( 1014 ),
	ACCOUNT_NONEXIST( 1015 ),
	ACCOUNT_DUPLICATED( 1016 ),
	
	INVALID_PLATFORM( 1020 ),
	INVALID_PWD ( 1021 ),
	INVALID_NICKNAME( 1022 ),
	NICKNAME_DUPLICATED( 1023 ),
	NOEXSITED_SCRIPT( 1024 ),
	
	
	DB_SP_CONTENTS_ERR(9994),
	SYSTEM_ERR ( 9995 ),
	NETWORK_ERR ( 9996 ),
	DB_ERR ( 9997 ),
	UNKNOUN_ERR ( 9998 ),
	MAX( 9999 );
	
	
	private Integer code;
	
	private EResultCode( int value ) 
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
		if(StringHelper.isNullString(value)) {
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
