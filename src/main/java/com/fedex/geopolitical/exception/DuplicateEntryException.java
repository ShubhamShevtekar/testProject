package com.fedex.geopolitical.exception;


public class DuplicateEntryException extends RuntimeException{
	    /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public DuplicateEntryException() {
	        super();
	    }
	    public DuplicateEntryException(String message, Throwable cause) {
	        super(message, cause);
	    }
	    public DuplicateEntryException(String message) {
	        super(message);
	    }
	    public DuplicateEntryException(Throwable cause) {
	        super(cause);
	    }
	}

