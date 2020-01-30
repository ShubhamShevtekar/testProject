package com.fedex.geopolitical.custom.annotation;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class DateHandlerSerializer extends StdSerializer<Date>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final String DATE_FORMAT = "yyyy-MM-dd";
	 

	public DateHandlerSerializer() {
		this(null);
	}

	public DateHandlerSerializer(Class<Date> vc) {
		super(vc);
	}

	@Override
	public void serialize(Date value, JsonGenerator gen, SerializerProvider provider) throws IOException {
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		gen.writeString(sdf.format(value));
	}
	
}
