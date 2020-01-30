package com.fedex.geopolitical.exception;


public class InvalidEffectiveDateException extends RuntimeException{
	    /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public InvalidEffectiveDateException() {
	        super();
	    }
	    public InvalidEffectiveDateException(String message, Throwable cause) {
	        super(message, cause);
	    }
	    public InvalidEffectiveDateException(String message) {
	        super(message);
	    }
	    public InvalidEffectiveDateException(Throwable cause) {
	        super(cause);
	    }
	}

