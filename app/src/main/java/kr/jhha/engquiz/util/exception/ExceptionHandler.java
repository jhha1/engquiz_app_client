package kr.jhha.engquiz.util.exception;

import kr.jhha.engquiz.util.exception.contents.ContentsRoleException;
import kr.jhha.engquiz.util.exception.system.SystemException;
import kr.jhha.engquiz.data.remote.EResultCode;

public class ExceptionHandler {

	public static boolean isCustomException( Exception e ) {
		if( e instanceof SystemException
				|| e instanceof ContentsRoleException)
			return true;
		else 
			return false;
	}
	
	public static EResultCode getCodeForClient(Exception e )
	{
		if( e == null ) 
			return EResultCode.UNKNOUN_ERR;
		
		if( e instanceof ErrorCode ) 
		{
			EResultCode code = ((ErrorCode)e).getErrorCode();
			if( code == null )
				return EResultCode.UNKNOUN_ERR;
			return code;
		}
		
		if( e instanceof IllegalStateException ) return EResultCode.SYSTEM_ERR;
		else if( e instanceof IllegalArgumentException ) return EResultCode.INVALID_ARGUMENT;
		else return EResultCode.UNKNOUN_ERR;
	}
	
	public static String getMsgForClient( Exception e ) 
	{
		String none = "NONE";
		if( e instanceof ContentsRoleException ) return e.getMessage();
		else return none;
	}
}
