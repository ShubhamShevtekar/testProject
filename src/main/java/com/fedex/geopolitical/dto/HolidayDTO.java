package com.fedex.geopolitical.dto;

import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fedex.geopolitical.constants.DTOValidationConstants;
import com.fedex.geopolitical.custom.annotation.DateHandlerDeserializer;
import com.fedex.geopolitical.custom.annotation.DateHandlerSerializer;
import com.fedex.geopolitical.custom.annotation.StringDeserializer;

import lombok.Data;
 
@Data
public class HolidayDTO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Size(max=400, message=DTOValidationConstants.HOLIDAY_DATE_PARAM_TEXT_MAX_SIZE, groups = {Update.class, Create.class})
	@JsonDeserialize(using = StringDeserializer.class)
	private String holidayDateParamText;
	
	private Long holidayId;
	
	public interface Create{}
	public interface Update{}
	
	@NotBlank(message = DTOValidationConstants.BLANK_HOLIDAY_NAME, groups = {Update.class, Create.class})
	@Size(max=65, message=DTOValidationConstants.HOLIDAY_NAME_MAX_SIZE, groups = {Update.class, Create.class})
	private String holidayName;
	
	@JsonIgnore
	private User user;
	
	@JsonDeserialize(using = DateHandlerDeserializer.class)
	@JsonSerialize(using = DateHandlerSerializer.class)
	@JsonInclude(Include.NON_NULL)
	private Date effectiveDate;
	
	@JsonDeserialize(using = DateHandlerDeserializer.class)
	@JsonSerialize(using = DateHandlerSerializer.class)
	@JsonInclude(Include.NON_NULL)
	private Date expirationDate;
	
	@Override
	public String toString()
	{
	  return ToStringBuilder.reflectionToString(this,ToStringStyle.JSON_STYLE);
	}

}
