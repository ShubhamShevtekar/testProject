package com.fedex.geopolitical.exception;


public class LongFormatException extends RuntimeException{
	    /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public LongFormatException() {
	        super();
	    }
	    public LongFormatException(String message, Throwable cause) {
	        super(message, cause);
	    }
	    public LongFormatException(String message) {
	        super(message);
	    }
	    public LongFormatException(Throwable cause) {
	        super(cause);
	    }
	}

