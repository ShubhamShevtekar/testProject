package com.fedex.geopolitical.dto;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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
public class RefDayOfWeekDTO implements Serializable{
 	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Digits(integer=38, fraction = 0,groups = {Update.class, Create.class})
	@NotNull(message = DTOValidationConstants.DOW_ID_NOT_NULL, groups = {Create.class,Update.class})
	private BigInteger dayOfweekNumber;
	
	@Size(max=256,message = DTOValidationConstants.DOW_FULL_NAME_SIZE, groups = {Update.class, Create.class})
	@JsonDeserialize(using = StringDeserializer.class)
	private String dayOfweekFullName;
	
	@Size(max=9,message = DTOValidationConstants.DOW_SHORT_NAME_SIZE, groups = {Update.class, Create.class})
	@NotBlank(message = DTOValidationConstants.DOW_NAME_NOT_NULL, groups = {Update.class, Create.class})
	private String dayOfweekShortName;
	
	public interface Create{}
	public interface Update{}
	
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
