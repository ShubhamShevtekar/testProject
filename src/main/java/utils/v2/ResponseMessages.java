package utils.v2;

public class ResponseMessages {
	
	// Version validation message
	public String expectedApiVersionNumber = "1.0.0";
	
	//***success messages
	public String geoTypePostSuccessMsg = "Geopolitical Type Details successfully saved with GeopoliticalTypeId :";
	public String geoTypePutSuccessMsg = "Geopolitical Type Details successfully updated with GeopoliticalTypeId :";
	public String geoRsTypePostSuccessMsg = "GeopoliticalRelationshipType Details successfully saved with GeopoliticalRelationshipTypeCd :";
	public String geoRsTypePutSuccessMsg = "GeopoliticalRelationshipType Details successfully updated with GeopoliticalRelationshipTypeCd :";
	public String geoOrgStdPostSuccessMsg = "GEOPLORGSTD Details successfully saved with OrgStdCd :";
	public String geoOrgStdPutSuccessMsg = "GEOPLORGSTD Details successfully updated with OrgStdCd :";
	public String depnCntryRltspPostSuccessMsg = "Dependent Country Relationship Details successfully saved with DependentRelationshipId :";
	public String depnCntryRltspPutSuccessMsg = "Dependent Country Relationship Details successfully updated with DependentRelationshipId :";
	public String holidayPostSuccessMsg = "Holiday Details successfully saved with HolidayId :";
	public String holidayPutSuccessMsg = "Holiday Details successfully updated with HolidayId :";
	public String uomTypePostSuccessMsg = "REFUOMTYPE Details successfully saved with uomTypeCd :";
	public String uomTypePutSuccessMsg = "REFUOMTYPE Details successfully updated with uomTypeCd :";
	public String scriptPostSuccessMsg = "REFSCRIPT Details successfully saved with scrptCd :";
	public String scriptPutSuccessMsg = "REFSCRIPT Details successfully updated with scrptCd :";
	public String dayOfWeekPostSuccessMsg = "Day Of Week Details successfully saved with DayOfWeekNumber :";
	public String dayOfWeekPutSuccessMsg = "Day of week Details successfully updated with DayOfWeekNumber :";
	public String monthOfYearPostSuccessMsg = "Month of Year Details successfully saved with MonthOfYearNumber :";
	public String monthOfYearPutSuccessMsg = "Month of Year Details successfully updated with MonthOfYearNumber :";
	public String countryOrgStdPostSuccessMsg = "CntryOrgStd Details successfully saved with OrgStdCd :";
	public String countryOrgStdPutSuccessMsg = "CntryOrgStd Details successfully updated with OrgStdCd :";
	public String geoRltspPostSuccessMsg = "GeopoliticalRelationship Details successfully saved with GeoplRltspCmptId :";
	public String geoRltspPutSuccessMsg = "GeopoliticalRelationship Details successfully updated with GeoplRltspCmptId :";
	public String stProvStdPostSuccessMsg = " State Province Details successfully saved with stProvCd :";
	public String stProvStdPutSuccessMsg = "State Province Details successfully updated with stProvCd :";
	public String affilTypePostSuccessMsg = "GeoplAffilType Details successfully saved with affilTypeId :";
	public String affilTypePutSuccessMsg = "GeoplAffilType Details successfully updated with affilTypeId :";
	public String langPostSuccessMsg = "Language Details successfully saved with langCd :";
	public String langPutSuccessMsg = "Language Details successfully updated with langCd :";
	public String cntryPostSuccessMsg = "Country Details successfully saved with GeoplId :";
	public String cntryPutSuccessMsg = "Country Details successfully updated with GeoplId:";
	public String addressLabelSuccessMsg = "Address Details successfully saved with GeopoliticalId : ";
	
	//***Get general success message
	public String getSuccessMsg = "Records Fetched Successfully";
	
	//***Get general error message
	public String getErrorMsg = "There are no records for the specified criteria";
	
	//***Get format error message
	public String getFormatErrorMsg = "Not correct format for Id!";
	
	//***Get exception error message
	public String getAddressInvalidCd = " getAddressByCountryCd.countryCode: must match \"^[a-zA-Z]$\"";
	
	//*** GraphQL Error message
	//MOY
	public String moyNumberErrorMsg = "Validation error of type FieldUndefined: Field 'monthOfYearNumbe' in type 'RefMonthOfYear' is undefined @ 'monthsOfYear/monthOfYearNumbe'";
	public String moyShortNameErrorMsg = "Validation error of type FieldUndefined: Field 'monthOfYearShortNam' in type 'RefMonthOfYear' is undefined @ 'monthsOfYear/monthOfYearShortNam'";
		
	//DOW	
	public String dowNumberErrorMsg = "Validation error of type FieldUndefined: Field 'dayOfWeekNumbe' in type 'RefDayOfWeek' is undefined @ 'daysOfWeek/dayOfWeekNumbe'";
	public String dowFullNamErrorMsg = "Validation error of type FieldUndefined: Field 'dayOfWeekFullNam' in type 'RefDayOfWeek' is undefined @ 'daysOfWeek/dayOfWeekFullNam'";
	public String dowShortNamErrorMsg = "Validation error of type FieldUndefined: Field 'dayOfWeekShortNam' in type 'RefDayOfWeek' is undefined @ 'daysOfWeek/dayOfWeekShortNam'";
	
	//Language
	public String moyLangErrorMsg = "Validation error of type FieldUndefined: Field 'translatedMOY' in type 'Language' is undefined @ 'languages/translatedMOY'";
	public String dowLangErrorMsg = "Validation error of type FieldUndefined: Field 'translatedDOW' in type 'Language' is undefined @ 'languages/translatedDOW'";
	public String langCodeErrorMsg = "Validation error of type FieldUndefined: Field 'languageCod' in type 'Language' is undefined @ 'languages/languageCod'";
	public String langNameErrorMsg = "Validation error of type FieldUndefined: Field 'engLanguageNam' in type 'Language' is undefined @ 'languages/engLanguageNam'";
	public String natScriptLangNameErrorMsg = "Validation error of type FieldUndefined: Field 'nativeScriptLanguageNam' in type 'Language' is undefined @ 'languages/nativeScriptLanguageNam'";

	public String affilTypeErrorMsg1 = "Validation error of type FieldUndefined: Field 'affilTypeId1' in type 'GeoPoliticalAffiliatedType' is undefined @ 'affiliationTypes/affilTypeId1'";
	public String affilTypeErrorMsg2 = "Validation error of type FieldUndefined: Field 'affilTypeCode1' in type 'GeoPoliticalAffiliatedType' is undefined @ 'affiliationTypes/affilTypeCode1'";
	public String scriptInvalidAttribute1 = "Validation error of type FieldUndefined: Field 'scriptDescription1' in type 'RefScript' is undefined @ 'scripts/scriptDescription1'";
	public String scriptInvalidAttribute2 = "Validation error of type FieldUndefined: Field 'scriptName2' in type 'RefScript' is undefined @ 'scripts/scriptName2'";
	public String scriptInvalidAttribute3 = "Validation error of type FieldUndefined: Field 'scriptDescription2' in type 'RefScript' is undefined @ 'scripts/scriptDescription2'";
	
	

	public String ErrorMsg="Validation error of type FieldUndefined: Field 'holidayDateParameterTexts' in type 'Holiday' is undefined @ 'holidays/holidayDateParameterTexts'";
	public String ErrorMsg1="Validation error of type FieldUndefined: Field 'holidayNames' in type 'Holiday' is undefined @ 'holidays/holidayNames'";
	public String GeoOrgStdErrorMsg2="Validation error of type FieldUndefined: Field 'organisationStandardNames' in type 'GeoPoliticalOrganizationStandard' is undefined @ 'geopoliticalOrganizationStandards/organisationStandardNames'";
	public String GeoOrgStdErrorMsg1="Validation error of type FieldUndefined: Field 'organisationStandardCodes' in type 'GeoPoliticalOrganizationStandard' is undefined @ 'geopoliticalOrganizationStandards/organisationStandardCodes'";
	public String DepCntryOrgTypeErrorMsg1="Validation error of type FieldUndefined: Field 'dependentRelationshipIds' in type 'DependentCountryRelationship' is undefined @ 'dependentCountryRelationshipTypes/dependentRelationshipIds'";
	public String DepCntryOrgTypeErrorMsg2="Validation error of type FieldUndefined: Field 'dependentRelationshipDescriptions' in type 'DependentCountryRelationship' is undefined @ 'dependentCountryRelationshipTypes/dependentRelationshipDescriptions'";
	public String HolidayErrorMsg1="Validation error of type FieldUndefined: Field 'holidayNames' in type 'Holiday' is undefined @ 'holidays/holidayNames'";
	public String HolidayErrorMsg2="Validation error of type FieldUndefined: Field 'holidayDateParameterTexts' in type 'Holiday' is undefined @ 'holidays/holidayDateParameterTexts'";
	public String DepCntryOrgeErrorMsg1="Validation error of type WrongType: argument 'dependentCountryCd' with value 'StringValue{value=''}' is not a valid 'Long' @ 'countries'";
	public String DepCntryOrgeErrorMsg2="Validation error of type FieldUndefined: Field 'geopoliticalIds' in type 'Country' is undefined @ 'countries/geopoliticalIds'";
	public String DepCntryOrgeErrorMsg3="Validation error of type FieldUndefined: Field 'dependentCountryRelationships' in type 'Country' is undefined @ 'countries/dependentCountryRelationships'";
	
	public String geoTypeInvalid1AttributeGraphQLMsg = "Validation error of type FieldUndefined: Field 'geopoliticalTypeId1' in type 'GeoPoliticalType' is undefined @ 'geopoliticalTypes/geopoliticalTypeId1'";
	public String geoTypeInvalid2AttributeGraphQLMsg = "Validation error of type FieldUndefined: Field 'geopoliticalTypeName2' in type 'GeoPoliticalType' is undefined @ 'geopoliticalTypes/geopoliticalTypeName2'";
	public String countryInvalidAttributeGraphQLMsg1 = "Validation error of type FieldUndefined: Field 'geopliticalId' in type 'Country' is undefined @ 'countries/geopliticalId'";
	public String countryInvalidAttributeGraphQLMsg2 = "Validation error of type FieldUndefined: Field 'geopliticalId' in type 'Country' is undefined @ 'countries/geopliticalId'";
	public String countryInvalidAttributeGraphQLMsg3 = "Validation error of type FieldUndefined: Field 'countryNumercCode' in type 'Country' is undefined @ 'countries/countryNumercCode'";
	public String countryExpiredTokenGraphQLMsg = "The token has reached its time to live";
	public String countryInvalidTokenGraphQLMsg = "Input byte array has wrong 4-byte ending unit";
	public String geoRltspInvalid1AttributeGraphQLMsg = "Validation error of type FieldUndefined: Field 'geopoliticalComponentId1' in type 'GeoPoliticalRelationship' is undefined @ 'relationships/geopoliticalComponentId1'";
    public String geoRltspInvalid2AttributeGraphQLMsg = "Validation error of type FieldUndefined: Field 'relateGeopoliticalComponentId2' in type 'GeoPoliticalRelationship' is undefined @ 'relationships/relateGeopoliticalComponentId2'";
	
    public String addressLabelsErrorMsg1 = "Validation error of type FieldUndefined: Field 'countryCodee' in type 'AddressLabels' is undefined @ 'AddressLabels/countryCodee'";
	public String addressLabelsErrorMsg2 = "Validation error of type FieldUndefined: Field 'languageCodee' in type 'AddressLabels' is undefined @ 'AddressLabels/languageCodee'";
	public String addressLabelsErrorMsg3 = "Validation error of type FieldUndefined: Field 'addressLineNumberr' in type 'AddressLabels' is undefined @ 'AddressLabels/addressLineNumberr'";
	//***error messages
	
	public String requiredFieldMsg = " is a required field";
	public String invalidUrlMsg = "Not Found";
	public String metaBlankMsg = " cannot be a null field!";	
	public String userNullMsg = "Username cannot be blank!, ";
	public String recordExistsMsg = "Record already exists.";
	public String invalidPutRequestMsg = "Request method 'PUT' not supported";
	public String exceedLimitMsg = "Not correct format for Id!";
	public String dateBlankMsg = " cannot be a null field!";
	public String userLenghtMsg = "User Name cannot be greater than 25 characters";
	public String recordNotFoundMsg = "Record not found";
	public String wrongFormatIdMsg = "Not correct format for Id!";
	public String lengthExceeds25Char = " cannot be greater than 25 characters";
	public String lengthExceeds10Char = " cannot be greater 10 characters!"; 
	public String lengthExceeds10Char2 = " cannot be greater than 10 characters!";
	public String lengthExceeds65Char2 = " cannot be greater than 65 characters!";
	public String lengthExceeds10CharNew = " cannot be greater than 10 characters";
	public String lengthExceeds256Char = " cannot be greater 256 characters!";
	public String lengthExceeds1000Char = " cannot be greater 1000 characters!";
	public String lengthExceeds3Char = " cannot be greater than 3 characters";
	public String lengthExceeds256Char1 = " cannot be greater than 256 characters";
	public String lengthExceeds65Char = " cannot be greater than 65 characters";
	public String lengthExceeds10Char1 = " cannot be greater than 10 characters";
	public String lengthExceeds120Char = " cannot be greater than 120 characters";
	public String lengthExceeds38Char = "cannot be greater than 38 digits";
	public String lengthExceeds18Char = " cannot be greater than 18 characters";
	public String jsonParserErrorMsg = "JSON parse error: InvalidFormatException:  Cannot deserialize value of type `java.lang.Long` from String ";
	public String jsonParserErrorMsg1 ="JSON parse error: InvalidFormatException:  Cannot deserialize value of type `java.math.BigInteger` from String ";
	public String lengthExceeds9Char1=" cannot be greater than 9 characters";
	public String lengthExceeds50Char = " cannot be greater than 50 characters!";
	public String lengthExceeds50Char1 = "cannot be greater than 50 digits";
	public String lengthExceeds256CharMsg1 =" cannot be greater than 256 characters";
	public String lengthExceeds20Char1="  cannot be greater than 20 characters ";
	public String lengthExceeds20Char2=" cannot be greater than 20 characters!";
	public String lengthExceeds100Char1=" cannot be greater than 100 characters!";	
	public String countryNotFoundMsg = "Country not found";
	public String invalidDateMsg="Invalid Date Format";
	public String geoRltspeffectiveDateBlankMsg="Effective Date cannot be a null field!";
	public String requiredField = " is a required field";
	public String lengthExceeds18Char1 = " cannot be greater than 18 characters!";
	public String lengthExceeds4000Char1 = " cannot be greater than 4000 characters";
	public String metaValidationMsg = " cannot be a null field!";   
	public String EffectiveDateBlankMsg = "Effective Date cannot be a null field!";
	public String lengthExceeds65CharMsg = " cannot be greater than 65 characters";
	public String lengthExceeds400CharMsg = " cannot be greater than 400 characters";
	public String inValidFieldMsg = " is not a valid value";
	public String geoTypeMsg = "Geopolitical Type of State not found";
	public String lengthExceeds2Char = " cannot be greater than 2 characters!";
	public String lengthExceeds35Char = " cannot be greater than 35 characters!";
	public String lengthExceeds80Char = " cannot be greater than 80 characters!";
	public String lengthExceeds3Char1 = " cannot be greater than 3 characters!";
		
		///geoType
	public String geoTypeNameBlankMsg = "Geopolitical Type Name not found null";		
	public String geoTypeInvalidUrl = "The request was rejected because the URL contained a potentially malicious String \"%25\"";
	
	
	//geoRltsp
	public String invalidToGeopoliticalIdMsg="State not found";
	public String invalidRelationshipTypeCodedMsg= "Relationship Type code not found";
	
	//geoRsType
	public String geoRsTypeCodeBlankMsg = "geopoliticalRelationshipTypeCd cannot be null, ";
	//GraphQL
	public String geoRsTypeCdFieldNotPresent = "Validation error of type FieldUndefined: Field 'geopoliticalRelationshipTypeCode1' in type 'GeoPoliticalRelationshipType' is undefined @ 'relationshipTypes/geopoliticalRelationshipTypeCode1'";
	public String geoRsTypeDescFieldNotPresent = "Validation error of type FieldUndefined: Field 'areaRelationshipTypeDescription1' in type 'GeoPoliticalRelationshipType' is undefined @ 'relationshipTypes/areaRelationshipTypeDescription1'";

	//***geoOrgStd
	public String geoOrgStdCodeBlankMsg = "orgStdCd cannot be null, ";
	public String geoOrgStdCodeNotInJson = "Organization Standard Code cannot be a null or blank field!, Organization Standard Code cannot be a null or blank field!, ";
	public String geoOrgStdCodeLengthMsg = "Organization Standard Code cannot be greater than 10 characters!, ";
	public String geoorgStdNmLengthMsg = "Organization Standard Name cannot be greater than 65 characters!, ";
	
	//depnCntryRltsp
	public String depnCntryRltspDescBlankMsg = "dependentRelationshipDescription cannot be null, ";
	public String depnCntryRltspDescLimitMsg = "DependentRelationshipDescription cannot be greater than 65 characters!, ";
	public String depnCntryRltspIdNotInDbMsg = "Not correct format for Id!";
	
	//holiday
	public String holidayNameBlankMsg = "holidayName cannot be null, ";
	
	//UOM type
	public String UomTypeNmNullMsg = "uomTypeNm cannot be null, ";
	public String UomTypeCdNullMsg = "uomTypeCd cannot be null, ";
	public String uomTypeNmFieldNotPresent = "Validation error of type FieldUndefined: Field 'uomTypeName1' in type 'RefUOMType' is undefined @ 'uomTypes/uomTypeName1'";
	public String uomTypeCdFieldNotPresent = "Validation error of type FieldUndefined: Field 'uomTypeCode1' in type 'RefUOMType' is undefined @ 'uomTypes/uomTypeCode1'";
	public String uomTypeDescFieldNotPresent = "Validation error of type FieldUndefined: Field 'uomTypeDesc1' in type 'RefUOMType' is undefined @ 'uomTypes/uomTypeDesc1'";
	
	//script
	public String scriptNameBlankMsg = "scrptNm cannot be null, ";
	public String dayOfWeekShNmBlankMsg = "dayOfweekShortName cannot be null, ";
	
	
	//DOW
	public String dayOfWeekNbrLengthMsg = "Numeric value required for field \"dayOfweekNumber\"";
	
	public String countryOrgStdContryCodeBlankMsg = "Country Code cannot be a null field!, ";
	public String countryOrgStdCntryShNmBlankMsg = "Country Short Name cannot be blank!, ";
	public String countryOrgStdOrgStdCdBlankMsg = "Organisation Standard code cannot be a null field!, ";	
	public String OrgStdCdNotFoundMsg = "Organisation standard Code not existing";

	//***GeoRltsp
	public String geoRltspFromGeoIdBlankMsg = "geopoliticalTypeName cannot be null, ";
	
	//***StProvStd
	public String stProvStdOrgStdCdBlankMsg = "Organization Standard Code cannot be a null or blank field!, ";
	public String stProvStdstProvCdBlankMsg = "State Province Code cannot be blank!, ";
	public String stProvStdOrgStdCdNotFoundMsg = "Organization Standard Code not found";
	
	public String stProvStdOrdStdCdFieldNotPresent = "Validation error of type FieldUndefined: Field 'organisationStandardCode1' in type 'StateProvinceStandard' is undefined @ 'stProvStds/organisationStandardCode1'";
	public String stProvStdGeoplIdFieldNotPresent = "Validation error of type FieldUndefined: Field 'geopoliticalId1' in type 'StateProvinceStandard' is undefined @ 'stProvStds/geopoliticalId1'";
	public String stProvStdStProvCdFieldNotPresent = "Validation error of type FieldUndefined: Field 'stateProvinceCode1' in type 'StateProvinceStandard' is undefined @ 'stProvStds/stateProvinceCode1'";
	public String stProvStdStProvNmFieldNotPresent = "Validation error of type FieldUndefined: Field 'stateProvinceName1' in type 'StateProvinceStandard' is undefined @ 'stProvStds/stateProvinceName1'";
	public String stProvStdEffeDtFieldNotPresent = "Validation error of type FieldUndefined: Field 'effectiveDate1' in type 'StateProvinceStandard' is undefined @ 'stProvStds/effectiveDate1'";
	public String stProvStdExpirDtFieldNotPresent = "Validation error of type FieldUndefined: Field 'expirationDate1' in type 'StateProvinceStandard' is undefined @ 'stProvStds/expirationDate1'";


	//***Country
	public String geopoliticalTypeNameBlanckMsg ="Geopolitical Type Name not found ";
	
}
