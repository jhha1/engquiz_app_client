package kr.jhha.engquiz.util.exception.system;

import kr.jhha.engquiz.data.remote.EResultCode;

@SuppressWarnings("serial")
public class IllegalArgumentException extends SystemException
{
	EResultCode code = EResultCode.INVALID_ARGUMENT;
	
	public IllegalArgumentException () 
	{}
	
	public IllegalArgumentException (String log) {
		super(log);
	}
	
	public IllegalArgumentException (Throwable cause) {
		super(cause);
	}
	
	public IllegalArgumentException (EResultCode code) {
		this.code = code;
	}
	
	public IllegalArgumentException (EResultCode code, String log) {
		super(log);
		this.code = code;
	}
	
	public IllegalArgumentException (EResultCode code, Throwable cause) {
		super(cause);
		this.code = code;
	}
	
	public IllegalArgumentException (String log, Throwable cause) {
		super(log, cause);
	}
	
	public IllegalArgumentException (EResultCode code, String log, Throwable cause) {
		super(log, cause);
		this.code = code;
	}
	

	@Override
	public EResultCode getErrorCode() {
		return this.code;
	}
}
