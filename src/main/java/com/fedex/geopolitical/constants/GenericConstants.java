package com.fedex.geopolitical.constants;

public class GenericConstants {
	
	private GenericConstants(){}

	public static final String VERSION = "1.0.0";
	public static final String REGEX_DATE_VALIDATION = "^\\d{4}\\-(0?[1-9]|1[012])\\-(0?[1-9]|[12][0-9]|3[01])$";
	public static final String DATE_FORMAT = "yyyy-MM-dd";
	public static final String NOT_PARSEABLE = "Unable to parse date";
	public static final String INVALID_DATE_FORMAT = "Invalid Date Format";
	public static final String DEFAULT_SCRIPT_CD = "Zzzz";
	
	public static final String MSG_SOURCE = "MsgSource";
	public static final String MSG_VERSION = "MsgVersion";
	public static final String MSG_SOURCE_VALUE = "GEOPCORE";
	public static final String MSG_VERSION_VALUE = "1.0.0";
	public static final String TRIGGEREVENTTYPE_POST = "ADD";
	public static final String TRIGGEREVENTTYPE_PUT = "CHANGE";
	public static final String SCRIPT_DEFAULT_VALUE = "Zzzz";
	public static final String JMS_EXCEPTION="Exception while processing JMS message";
	public static final String MSG_CREATE_TMSTMP = "MsgCreateTmstp";
	public static final String TRIGGER_EVENT_TYPE = "TriggerEventType";
	public static final String MESSAGE_SEGMENT = "DataSegment";
	
}
