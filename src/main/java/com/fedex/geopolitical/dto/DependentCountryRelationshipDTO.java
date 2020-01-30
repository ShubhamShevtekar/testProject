package com.fedex.geopolitical.dto;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fedex.geopolitical.constants.DTOValidationConstants;

import lombok.Data;

@Data
public class DependentCountryRelationshipDTO implements Serializable{
 	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long dependentRelationshipId;
	
	@NotBlank(message = DTOValidationConstants.BLANK_DEPENDENT_RELATIONSHIP_DESCRIPTION, groups = {Update.class, Create.class})
	@Size(max = 65, message = DTOValidationConstants.DEPENDENT_RELATIONSHIP_DESCRIPTION_LESS_THAN_65_CHAR, groups = {Update.class, Create.class})
	private String dependentRelationshipDescription;
	
	@JsonIgnore
	private User user;

	public interface Create{}
	public interface Update{}
	
	@Override
	public String toString()
	{
	  return ToStringBuilder.reflectionToString(this,ToStringStyle.JSON_STYLE);
	}
	
}
