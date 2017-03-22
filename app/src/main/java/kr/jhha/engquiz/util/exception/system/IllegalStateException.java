package kr.jhha.engquiz.util.exception.system;

import kr.jhha.engquiz.data.remote.EResultCode;

@SuppressWarnings("serial")
public class IllegalStateException extends SystemException
{
	EResultCode code = EResultCode.SYSTEM_ERR;
	
	public IllegalStateException () 
	{}
	
	public IllegalStateException (String msg) {
		super(msg);
	}
	
	public IllegalStateException (Throwable cause) {
		super(cause);
	}
	
	public IllegalStateException (EResultCode code) {
		this.code = code;
	}
	
	public IllegalStateException (EResultCode code, String msg) {
		super(msg);
		this.code = code;
	}
	
	public IllegalStateException (EResultCode code, Throwable cause) {
		super(cause);
		this.code = code;
	}
	
	public IllegalStateException (String msg, Throwable cause) {
		super(msg, cause);
	}
	
	public IllegalStateException (EResultCode code, String msg, Throwable cause) {
		super(msg, cause);
		this.code = code;
	}

	@Override
	public EResultCode getErrorCode() {
		return this.code;
	}
}
