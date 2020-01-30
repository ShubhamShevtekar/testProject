package com.fedex.geopolitical.custom.annotation;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fedex.geopolitical.constants.GenericConstants;
import com.fedex.geopolitical.exception.DateFormatException;

public class DateHandlerDeserializer extends StdDeserializer<Date>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DateHandlerDeserializer.class);
	 

	public DateHandlerDeserializer() {
		this(null);
	}

	public DateHandlerDeserializer(Class<?> vc) {
		super(vc);
	}

	@Override
	public Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		String date = p.getText();
		if(StringUtils.isEmpty(date)){
			return null;
		}
			Pattern pattern = Pattern.compile(GenericConstants.REGEX_DATE_VALIDATION);
			if(pattern.matcher(date).matches()){
				SimpleDateFormat sdf = new SimpleDateFormat(GenericConstants.DATE_FORMAT);
				try {
					return sdf.parse(date);
				} catch (ParseException e) {
					LOGGER.info(GenericConstants.NOT_PARSEABLE);
				}
			}else{
				throw new DateFormatException(GenericConstants.INVALID_DATE_FORMAT);
			}
		return null;
	}

}
