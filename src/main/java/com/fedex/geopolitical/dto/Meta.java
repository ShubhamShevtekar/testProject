package com.fedex.geopolitical.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.fedex.geopolitical.constants.DTOValidationConstants;

import lombok.Data;

@Data
public class Meta {

	@NotBlank(message = DTOValidationConstants.BLANK_USERNAME, groups = {DTOTags.class})
	@Size(max=25, message=DTOValidationConstants.USERNAME_SIZE, groups = {DTOTags.class})
	private String userName;
	
	public interface DTOTags{}
	
}
