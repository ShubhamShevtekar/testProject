package com.fedex.geopolitical.dto;

import java.io.Serializable;
import java.math.BigInteger;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fedex.geopolitical.constants.DTOValidationConstants;

import lombok.Data;

@Data
public class RefMonthOfYearDTO implements Serializable{
 	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Digits(integer=38, fraction = 0, groups = {Update.class, Create.class})
	@NotNull(message = DTOValidationConstants.MOY_ID_NOT_NULL, groups = {Update.class, Create.class})
	private BigInteger monthOfYearNumber;
	
	@Size(max=18, message=DTOValidationConstants.MONTH_SHORT_NAME_SIZE, groups = {Update.class, Create.class})
	@NotBlank(message = DTOValidationConstants.MOY_NAME_NOT_BLANK, groups = {Update.class, Create.class})
	private String monthOfYearShortName;
	
	public interface Create{}
	
	@JsonIgnore
	private User user;
	
	public interface Update{}
	
	@Override
	public String toString()
	{
	  return ToStringBuilder.reflectionToString(this,ToStringStyle.JSON_STYLE);
	}

}
