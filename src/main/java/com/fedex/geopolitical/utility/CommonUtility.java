package com.fedex.geopolitical.utility;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

import org.joda.time.DateTime;

public class CommonUtility {
	private static long value;
	private static Random random;

	public static final String DATEFORMAT = "yyyy-MM-dd HH:mm:ss";

	private CommonUtility(){}


	public static long generateId(){
		if(random==null){
			random = new Random();
		}
		value = -1;
		while (value <= 0) {
			value = random.nextLong();
		}
		return value;
	} 

	public static Timestamp getCurrentTimeStamp(){
		return new Timestamp(Instant.now().toEpochMilli());
	}

	public static String getCurrenctDate(){
		DateTimeFormatter dtf = DateTimeFormatter.ISO_DATE;
		return dtf.format(LocalDate.now());
	}

	public static Date getDefaultExpirationDate(){
		DateTime dateTime = new DateTime(9999, 12, 31, 0, 0);
		return dateTime.toDate();
	}


	public static String getCurrentMethodName() {

		return Thread.currentThread().getStackTrace()[2].getClassName() + "."
				+ Thread.currentThread().getStackTrace()[2].getMethodName();
	}

	public static String getCurrentTimestampAsString() {
		DateFormat dateFormat = new SimpleDateFormat(DATEFORMAT);
		return dateFormat.format(new Timestamp(System.currentTimeMillis()));
	}
	public static String getIsoTimeStamp() {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		df.setTimeZone(TimeZone.getTimeZone("UTC"));
		return df.format(new Date());
	}

}
