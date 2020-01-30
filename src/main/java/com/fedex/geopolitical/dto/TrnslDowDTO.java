package com.fedex.geopolitical.dto;

import java.io.Serializable;
import java.math.BigInteger;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fedex.geopolitical.constants.DTOValidationConstants;
import com.fedex.geopolitical.dto.RefLanguageDTO.Create;
import com.fedex.geopolitical.dto.RefLanguageDTO.Update;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrnslDowDTO implements Serializable{
 	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Digits(integer=38, fraction = 0, groups = {Update.class, Create.class})
	@NotNull(message = DTOValidationConstants.TRNSL_DOW_NOT_NUL, groups = {Update.class, Create.class})
	private BigInteger dowNbr;
	
	@Size(max=256, message=DTOValidationConstants.TRNS_DOW_NAME, groups = {Update.class, Create.class})
	@NotBlank(message = DTOValidationConstants.TRNS_DOW_NAME_NOT_BLANK, groups = {Update.class, Create.class})
	private String transDowName;
	
	@Override
	public String toString()
	{
	  return ToStringBuilder.reflectionToString(this,ToStringStyle.JSON_STYLE);
	}
}
