package com.fedex.geopolitical.exception;


public class DateFormatException extends RuntimeException{
	    /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public DateFormatException() {
	        super();
	    }
	    public DateFormatException(String message, Throwable cause) {
	        super(message, cause);
	    }
	    public DateFormatException(String message) {
	        super(message);
	    }
	    public DateFormatException(Throwable cause) {
	        super(cause);
	    }
	}

