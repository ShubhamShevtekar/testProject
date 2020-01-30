package com.fedex.geopolitical.constants;

public class DTOValidationConstants {
	
	private DTOValidationConstants(){}
	
	public static final long MAX_NUMBER_ID = (long) 99999999999999999999999999999999999999d;
	public static final int MIN_NUMBER_ID = 1;
	public static final int MAX_DIGIT = 38;
	public static final String MAX_LENGTH_SIZE_EXCEEDED = "Maximum Number Size Exceeded";
	public static final String NUMBER_CANNOT_BE_BLANK = "Cannot be blank!";
	public static final String DATE_FORMAT = "MM-dd-yyyy";
	public static final String COUNTRY_NUMBER_CD_NOT_NULL = "countryNumberCd is a required field";
	public static final String BLANK_COUNTRY_CD = "countryCd is a required field";
	public static final String BLANK_THREE_CHAR_COUNTRY_CD = "threeCharCountryCd is a required field";
	public static final String BLANK_FIRST_WORK_WEEK_DAY_NAME = "First Work Week Day Name cannot be blank!";
	public static final String BLANK_LAST_WORK_WEEK_DAY_NAME = "Last Work Week Day Name cannot be blank!";
	public static final String BLANK_WEEKEND_FIRST_DAY_NAME = "Weekend First Day Name cannot be blank!";
	public static final String COUNTRY_CD_LESS_THAN_2_CHAR = "countryCd cannot be greater than 2 characters!";
	public static final String THREE_CHAR_COUNTRY_CD_LESS_THAN_3_CHAR = "threeCharCountryCd cannot be greater than 3 characters!";
	public static final String INDEPENDENT_FLAG_LESS_THAN_1_CHAR = "independentFlag cannot be greater than 1 character!";
	public static final String POSTAL_FORMAT_DESC_LESS_THAN_25_CHAR = "postalFormatDescription cannot be greater than 25 characters!";
	public static final String POSTAL_FLAG_LESS_THAN_1_CHAR = "postalFlag cannot be greater than 1 character!";
	public static final String FIRST_WORK_WEEK_DAY_NAME_LESS_THAN_25_CHAR = "firstWorkWeekDayName cannot be greater than 25 characters!";
	public static final String LAST_WORK_WEEK_DAY_NAME_LESS_THAN_25_CHAR = "lastWorkWeekDayName cannot be greater than 25 characters!";
	public static final String WEEKEND_FIRST_DAY_NAME_LESS_THAN_25_CHAR = "weekendFirstDayName cannot be greater than 25 characters!";
	public static final String INTERNET_DOMAIN_NAME_LESS_THAN_5_CHAR = "internetDomainName cannot be greater than 5 characters!";
	
	
	
	public static final String BLANK_LANGUAGE_CD = "languageCd is a required field";
	public static final String LANGUAGE_CD_LESS_THAN_2_CHAR = "languageCd cannot be greater than 3 characters!";
	public static final String TRANSLATION_NAME_LESS_THAN_120_CHAR = "translationName cannot be greater than 120 characters!";
	public static final String BLANK_LOCALE_CD = "localeCd is a required field";
	public static final String LOCALE_CD_LESS_THAN_18_CHAR = "localeCd cannot be greater than 18 characters!";
	public static final String CURRENCY_NUMBER_CD_NOT_NULL = "currencyNumberCd is a required field";
	public static final String BLANK_CURRENCY_CD = "currencyCd is a required field";
	public static final String CURRENCY_CD_LESS_THAN_3_CHAR = "currencyCd cannot be greater than 3 characters!";
	public static final String MONEY_FORMAT_DESC_LESS_THAN_18_CHAR = "moneyFormatDescription cannot be greater than 18 characters!";
	public static final String MINOR_UNIT_CD_NOT_NULL = "minorUnitCd is a required field";
	public static final String BLANK_INITIAL_DIALING_PREFIX_CD = "intialDialingPrefixCd is a required field";
	public static final String INITIAL_DIALING_PREFIX_CD_LESS_THAN_7_CHAR = "intialDialingPrefixCd cannot be greater than 7 characters!";
	public static final String BLANK_INITIAL_DIALING_CD = "intialDialingCd is a required field";
	public static final String INITIAL_DIALING_CD_LESS_THAN_3_CHAR = "intialDialingCd cannot be greater than 3 characters!";
	
	public static final String DATE_FULL_FORMAT_DESC_LESS_THAN_65_CHAR = "dateFullFormatDescription cannot be greater than 65 characters!";
	public static final String DATE_LONG_FORMAT_DESC_LESS_THAN_65_CHAR = "dateLongFormatDescription cannot be greater than 65 characters!";
	public static final String DATE_MEDIUM_FORMAT_DESC_LESS_THAN_65_CHAR = "dateMediumFormatDescription cannot be greater than 65 characters!";
	public static final String DATE_SHORT_FORMAT_DESC_LESS_THAN_65_CHAR = "dateShortFormatDescription cannot be greater than 65 characters!";
	
	public static final String CLDR_VERSION_NBR_LESS_THAN_18_CHAR = "cldrVersionNumber cannot be greater than 18 characters!";
	public static final String VERSION_NBR_LESS_THAN_18_CHAR = "versionNumber cannot be greater than 18 characters!";
	
	public static final String GEOPOLITICAL_TYPE_NOT_NULL = " is a required field";
	
	public static final String BLANK_NAME = "Name cannot be blank!";
	public static final String BLANK_USERNAME = "userName is a required field";
	public static final String NAME_LESS_THAN_25_CHAR = "Name cannot be greater than 25 characters!";
	public static final String USER_NOT_NULL = "User cannot be a null field!";
	public static final String GEOPL_TYPE_ID_NOT_NULL = "GeopoliticalTypeId cannot be a null field!";
	public static final String BLANK_GEOPL_RLTSP_TYPE_CD = "geopoliticalRelationshipTypeCd is a required field";
	public static final String BLANK_AREA_RLTSP_TYPE_DESC = "AreaRelationshipTypeDescription cannot be blank!";
	public static final String GEOPL_RLTSP_TYPE_CD_LESS_THAN_20_CHAR = "geopoliticalRelationshipTypeCd cannot be greater than 20 characters!";
	public static final String AREA_RLTSP_TYPE_DESC_LESS_THAN_100_CHAR = "areaRelationshipTypeDescription cannot be greater than 100 characters!";
	public static final String BLANK_GEOPL_TYPE_NAME = "geopoliticalTypeName is a required field";
	public static final String GEOPL_TYPE_NAME_LESS_THAN_50_CHAR = "geopoliticalTypeName cannot be greater than 50 characters!";
	public static final String DEPENDENT_RELATIONSHIP_ID_NOT_NULL = "DependentRelationshipId cannot be a null field!";
	public static final String BLANK_DEPENDENT_RELATIONSHIP_DESCRIPTION = "dependentRelationshipDescription is a required field";
	public static final String DEPENDENT_RELATIONSHIP_DESCRIPTION_LESS_THAN_65_CHAR = "dependentRelationshipDescription cannot be greater than 65 characters!";
	
	
	public static final String REF_SCRIPT_TYPE_CD_NOT_NULL = "scrptCd is a required field";
	public static final String BLANK_SCRIPT_NAME = "scrptNm is a required field";
	public static final String BLANK_SCRIPT_DESC = "Script Desc cannot be blank!";
	public static final String SCRPT_CD_NOT_MORE_THAN_EIGHTEEN = "scrptCd cannot be greater than 18 characters!";
	public static final String SCRPT_CD_NAME_SIZE="scrptNm cannot be greater than 256 characters";
	public static final String SCRPT_CD_DESC_SIZE="scrptDesc cannot be greater than 4000 characters";
	
	public static final String BLANK_UOM_TYPE_CD = "UOM Type Cd cannot be a blank or null field!";
	public static final String UOM_TYPE_CD_LESS_THAN_10_CHAR = "UOM Type Cd cannot be greater than 50 characters!";
	public static final String REF_UOM_TYPE_CD_NOT_NULL = "uomTypeCd is a required field";
	public static final String REF_UOM_TYPE_DESC_NOT_BLANK = "uomTypeDesc cannot be null";
	public static final String BLANK_UOM_TYPE_NAME = "uomTypeNm is a required field";
	public static final String REF_UOM_TYPE_NAME_SIZE="Uom Type name cannot be greater 256 characters";
	public static final String REF_UOM_TYPE_DESC_SIZE="Uom Type name cannot be greater than 1000 characters";
	public static final String DATATYPE_SIZE_EXCEEDED_UOM_TYPE_CD="uomTypeCd cannot be greater 10 characters!";
	public static final String DATATYPE_SIZE_EXCEEDED_UOM_TYPE_NM="uomTypeNm cannot be greater 256 characters!";
	public static final String DATATYPE_SIZE_EXCEEDED_UOM_TYPE_DESC="uomTypeDesc cannot be greater 1000 characters!";
	public static final String DATATYPE_SIZE_EXCEEDED="Datatype size exceeded";
	
	public static final String GEOPL_ORG_STD_CD_NOT_NULL = "orgStdCd is a required field";
	public static final String ORG_STD_CD_NOT_MORE_THAN_TEN = "orgStdCd cannot be greater than 10 characters!";
	public static final String ORG_STD_NM_NOT_MORE_THAN_SIXTY_FIVE = "orgStdNm cannot be greater than 65 characters!";
	public static final String BLANK_GEOPL_ORG_STD_NAME = "Organization Standard Name cannot be blank!";
	
	public static final String REF_LANGUAGE_CD_NOT_NULL = "langCd is a required field";
	public static final String LANG_NAME_NOT_NULL = "englLanguageNm is a required field";
	public static final String SCRIPT_CODE_NOT_FOUND_ERROR_MESSAGE="scrptCd is not a valid value";
	public static final String DAY_OF_WEEK_NOT_FOUND_ERROR_MESSAGE="dowNbr is not a valid value";
	public static final String MTH_OF_YEAR_NOT_FOUND_ERROR_MESSAGE="mthOfYrNbr is not a valid value";
	public static final String THREE_CHAR_LANG_CD_CANNOT_EXCEED_THREE_CHARS="Three character language code cannot be greater than 3 characters";
	public static final String CANNOT_EXCEED_TWO_CHARS="langCd cannot be greater than 3 characters";
	public static final String CANNOT_EXCEED_TWELVE_CHARS="cannot have more than twelve elements";
	public static final String CANNOT_EXCEED_SEVEN_CHARS="cannot have more than seven elements";
	public static final String REF_LANGUAGE_CD_NOT_BLANK= "Language Code cannot be blank!";
	public static final String REF_LANGUAGE_NAME_SIZE="englLanguageNm cannot be greater than 256 characters";
	public static final String REF_LANGUAGE_NATIVE_SCRPT_SIZE="nativeScriptLanguageNm cannot be greater than 256 characters";
	public static final String SCRIPT_CODE_SIZE="scrptCd cannot be greater than 18 characters";
	public static final String LANG_DESC_SIZE="Language Description cannot be greater than 4000 characters";
	
	public static final String HOLIDAY_ID_NOT_NULL = "Holiday Id cannot be a null field!";
	public static final String HOLIDAY_DATE_TEXT_NOT_NULL = "Holiday Date Param Text cannot be a null field!";
	public static final String HOLIDAY_NAME_NOT_NULL = "Holiday Name cannot be a null field!";
	public static final String BLANK_HOLIDAY_NAME = "holidayName is a required field";
	public static final String MOY_ID_NOT_NULL = " monthOfYearNumber is a required field";
	public static final String MOY_NAME_NOT_BLANK = "monthOfYearShortName is a required field";
	public static final String DOW_ID_NOT_NULL = "dayOfweekNumber is a required field";
	public static final String DOW_NAME_NOT_NULL = "dayOfweekShortName is a required field";
	public static final String DUPLICATE_ERROR_MESSAGE="Record already exists.";
	public static final String RESOURCE_NOT_FOUND_ERROR_MESSAGE="Record not found";
	public static final String NOT_FOUND_GEOPOLITICAL_TYPE_NAME = "geopoliticalTypeName is not a valid value";
	public static final String NOT_FOUND_DEPENDENT_COUNTRY_RELATIONSHIP_ID = "dependentRelationshipId is not a valid value";
	public static final String NOT_FOUND_DEPENDENT_COUNTRY_CD = "dependentCountryCd is not a valid value";
	public static final String NOT_FOUND_UOM_TYPE_NAME = "UOMTypeName not found";
	public static final String NOT_FOUND_HOLIDAY_NAME = "holidayName is not a valid value";
	public static final String NOT_FOUND_AFFIL_TYPE_NAME = "AffilTypeName not found";
	public static final String NOT_FOUND_LANGUAGE_CD = "languageCd is not a valid value";
	public static final String NOT_FOUND_COUNTRY_CD = "Country Code not found";
	public static final String NOT_FOUND_GEOPOLITICAL_TYPE = "GeopoliticalType not found";
	public static final String DOW_NBR_VALUES = "Date of week number must be equals to or between [1,7]";
	
	public static final String GEOPL_AFFIL_ID_NOT_NULL = "affilTypeId cannot be a null field!";
	public static final String GEOPL_AFFIL_CODE_NOT_NULL = "affilTypeCd is a required field";
	public static final String GEOPL_AFFIL_TYPE_CODE_NOT_NULL = "affilTypeCode is a required field";
	public static final String GEOPL_AFFIL_NAME_SIZE = "affilTypeName cannot be greater than 65 characters";
	public static final String BLANK_GEOPL_AFFIL_NAME = "affilTypeName cannot be blank!";
	
	public static final String HOLIDAY_NAME_MAX_SIZE="holidayName cannot be greater than 65 characters";
	public static final String HOLIDAY_DATE_PARAM_TEXT_MAX_SIZE="holidayDateParamText cannot be greater than 400 characters";
	
	public static final String MONTH_SHORT_NAME_SIZE="monthOfYearShortName cannot be greater than 18 characters";
	public static final String DOW_FULL_NAME_SIZE="dayOfweekFullName cannot be greater than 256 characters";
	public static final String DOW_SHORT_NAME_SIZE="dayOfweekShortName cannot be greater than 9 characters";
	public static final String GEOPL_AFFIL_CODE_SIZE = "affilTypeCode cannot be greater than 10 characters";
	public static final String GEOPL_AFFILIATION_CODE_SIZE = "affilTypeCd cannot be greater than 10 characters";
	
	public static final String FROM_NOT_NULL = "fromGeopoliticalId is a required field";
	public static final String TO_NOT_NULL = "toGeopoliticalId is a required field";
	public static final String RELATIONSHIP_CODE_NOT_NULL = "relationshipTypeCode is a required field";
	
	public static final String COUNTRY_NOT_FOUND_MESSAGE_CNTRYORGSTD = "countryCode is not a valid value";
	public static final String COUNTRY_NOT_FOUND_MESSAGE = "fromGeopoliticalId is not a valid value";
	public static final String STATE_NOT_FOUND_MESSAGE = "toGeopoliticalId is not a valid value";
	
	public static final String ST_PROV_CD_NOT_BLANK = "stProvCd is a required field";
	public static final String ST_PROV_NM_NOT_BLANK = "State Province Name cannot be blank!";
	public static final String GEOPL_ID_NOT_NULL = "Geopolitical Id cannot be a null field!";
	public static final String ORG_STD_CD_NOT_BLANK ="orgStdCd is a required field";
	public static final String ST_PROV_NM_SIZE="stProvNm cannot be greater than 120 characters";
	public static final String GEOPL_ID_SIZE="Geopolitical ID cannot be greater than 50 characters";
	public static final String ST_PROV_CD_SIZE="stProvCd cannot be greater than 10 characters";
	public static final String ORG_STD_CD_SIZE="orgStdCd cannot be greater than 10 characters";
	public static final String ORG_STD_CD_NOT_FOUND_ERROR_MESSAGE="orgStdCd is not a valid value";
	public static final String ST_CD_NOT_FOUND_ERROR_MESSAGE="State Code does not found";
	
	public static final String GEOPOL_RELATIONSHIP_MAX_SIZE="relationshipTypeCode  cannot be greater than 20 characters ";
	public static final String COUNTRY_FULL_NAME_MAX_SIZE = "countryFullName cannot be greater than 120 characters";
	public static final String COUNTRY_SHORT_NAME_MAX_SIZE = "countryShortName cannot be greater than 65 characters";
	public static final String ORG_STD_CD_NAME_MAX_SIZE = "orgStandardCode cannot be greater than 10 characters";
	public static final String GEOPL_ID_NAME_MAX_SIZE = "Geopolitical ID code cannot be greater than 38 characters";
	public static final String ORG_STD_NOT_NULL = "orgStandardCode is a required field";
	public static final String CNTRY_FULL_NM_NOT_NULL = "Country Full Name cannot be a null field!";
	public static final String CNTRY_SHRT_NM_NOT_BLANK = "countryShortName is a required field";
	public static final String GEOPL_ORG_CODE_NOT_EXISTING = "orgStandardCode is not a valid value";
	public static final String GEOPOLITICAL_TYPE_CODE_NOT_FOUND = "relationshipTypeCode is not a valid value";
	public static final String GEOPOLITICAL_RLTSP_NOT_FOUND = "Geopolitical Relationship not found";
	public static final String STATE_ALREADY_EXISTS = "State already exists with GeopoliticalId:";
	public static final String INVALID_RELATIONSHIP_TYPE = "Invalid Relationship Type Code";
	
	public static final String ID_NOT_NULL = "Id cannot be null for update!";
	public static final String ID_WRONG_FORMAT = "Not correct format for Id!";
	public static final String UNABLE_PARSE_DATE = "Unable to parse Date";
	
	//Constants related to Effective and expiry date
	public static final String EFFECTIVE_DATE_NOT_NULL = "effectiveDate is a required field";
	public static final String EFFECTIVE_DATE_CANNOT_LESS_THAN_EXISTING = "effectiveDate must be less than existing effective date";
	public static final String EFFECTIVE_EXPIRY_DATE_MESSAGE="effectiveDate must be less than Expiration Date";
	public static final String EFFECTIVE_CREATED_DATE_MESSAGE=" effectiveDate must be greater than or equal to current date";
	public static final String EFFECTIVE_EXISTING_MAX_DATE="effectiveDate must be greater than: ";
	public static final String EFFECTIVE_DATE_GIVEN_LESS_THAN = "effectiveDate given less than";
	public static final String EFFECTIVE_DATE_VOILATION_ERROR_MESSAGE="effectiveDate Voilation occured.";
	public static final String BLANK_EFFECTIVE_DATE = "Effective Date cannot be null!";
	public static final String BLANK_EXPIRATIO_DATE = "Expiration Date cannot be null!";
	public static final String EFFECTIVE_DT_MUST_BE_LESS_THAN_EXPIRY_DT = " effectiveDate must be less than Expiration Date";
	public static final String EFFECTIVE_DT_MUST_BE_GREATER_THAN_EQUAL_TO_CURRENCT_DATE = " effectiveDate must be greater than or equal to current date";
	public static final String EFFECTIVE_DATE_CANNOT_BE_NULL = "effectiveDate is a required field";
	public static final String TRNSLDOW_SEVEN_ELEMENTS = "Translated Day Of Week must have 0 or 7 elements";
	public static final String TRNSLMOY_TWELVE_ELEMENTS = "Translated Month Of Year must have 0 or 12 elements";
	public static final String GEOPL_ENTITY_NOT_FOUND = "Geopolitical Entity not found";	
	
	
	public static final String DEPENDENT_COUNTRY_RELATIONSHIP_DTO_NOT_NULL = " is a required field";
	public static final String COUNTRY_ORG_STD_DTO_NOT_NULL = " is a required field";
	public static final String COUNTRY_DTO_NOT_NULL = " is a required field";
	public static final String GEOPOLITICAL_AFFILIATION_TYPE_DTO_NOT_NULL = " is a required field";
	public static final String GEOPOLITICAL_ORG_STD_DTO_NOT_NULL = " is a required field";
	public static final String GEOPOLITICAL_RELATIONSHIP_DTO_NOT_NULL = " is a required field";
	public static final String GEOPOLITICAL_RELATIONSHIP_TYPE_DTO_NOT_NULL = " is a required field";
	public static final String GEOPOLITICAL_TYPE_DTO_NOT_NULL = " cannot be a null field!";
	public static final String HOLIDAY_DTO_NOT_NULL = " is a required field";
	public static final String REF_DAY_OF_WEEK_DTO_NOT_NULL = " is a required field";
	public static final String REF_LANGUAGE_DTO_NOT_NULL = " is a required field";
	public static final String MONTH_OF_YEAR_DTO_NOT_NULL = " is a required field";
	public static final String REF_SCRIPT_NOT_NULL = " is a required field";
	public static final String UOM_TYPE_DTO_NOT_NULL = " is a required field";
	public static final String ST_PROV_STD_DTO_NOT_NULL = " is a required field";
	public static final String META_NOT_NULL = " is a required field";
	public static final String NOT_FOUND_UOM_TYPE_CODE = "uomTypeCd is not a valid value";
	public static final String NOT_FOUND_AFFIL_TYPE_CODE = "affilTypeCd is not a valid value";
	public static final String TRNS_DOW_NAME = "transDowName cannot be greater than 256 characters";
	public static final String TRNS_DOW_NAME_NOT_BLANK = "transDowName is a required field";
	public static final String TRNSL_MOYNBR_NOT_NULL = "mthOfYrNbr is a required field";
	public static final String TRNSL_MOY_SIZE = "transMoyName cannot be greater than 65 characters";
	public static final String TRNSL_MOY_NOT_BLANK = "transMoyName is a required field";
	public static final String TRNSL_DOW_NOT_NUL = "dowNbr is a required field";
	public static final String CNTRY_CO_NOT_NULL = "countryCode is a required field";
	public static final String CNTRY_CD_MAX_SIZE = "countryCode cannot be greater than 10 characters";
	public static final String USERNAME_SIZE = "userName cannot be greater than 25 characters";
	public static final String NOT_FOUND_GEOPOLITICAL_TYPE_NAME_STATE = "Geopolitical Type of State not found";
	
	
}

