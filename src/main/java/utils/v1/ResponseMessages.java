package utils.v1;

public class ResponseMessages {

	// Version validation message
	public String expectedApiVersionNumber = "1.0.0";

	//***success messages
	public String geoTypePostSuccessMsg = "Geopolitical Type Details successfully saved with GeopoliticalTypeId :";
	public String geoTypePutSuccessMsg = "Geopolitical Type Details successfully updated with GeopoliticalTypeId :";
	public String geoRsTypePostSuccessMsg = "GeopoliticalRelationshipType Details successfully saved with geopoliticalRelationshipTypeCode :";
	public String geoRsTypePutSuccessMsg = "GeopoliticalRelationshipType Details successfully updated with geopoliticalRelationshipTypeCode :";
	public String geoOrgStdPostSuccessMsg = "GEOPLORGSTD Details successfully saved with OrgStdCd :";
	public String geoOrgStdPutSuccessMsg = "GEOPLORGSTD Details successfully updated with OrgStdCd :";
	public String depnCntryRltspPostSuccessMsg = "Dependent Country Relationship Details successfully saved with DependentRelationshipId :";
	public String depnCntryRltspPutSuccessMsg = "Dependent Country Relationship Details successfully updated with DependentRelationshipId :";
	public String holidayPostSuccessMsg = "Holiday Details successfully saved with HolidayId :";
	public String holidayPutSuccessMsg = "Holiday Details successfully updated with HolidayId :";
	public String uomTypePostSuccessMsg = "REFUOMTYPE Details successfully saved with uomTypeCd :";
	public String uomTypePutSuccessMsg = "REFUOMTYPE Details successfully updated with uomTypeCd :";
	public String uomTypePostSuccessNewMsg = "REFUOMTYPE Details successfully saved with uomTypeCode :";
	public String uomTypePutSuccessNewMsg = "REFUOMTYPE Details successfully updated with uomTypeCode :";
	public String scriptPostSuccessMsg = "REFSCRIPT Details successfully saved with scrptCd :";
	public String scriptPutSuccessMsg = "REFSCRIPT Details successfully updated with scrptCd :";
	public String dayOfWeekPostSuccessMsg = "Day Of Week Details successfully saved with dayOfWeekNumber :";
	public String dayOfWeekPutSuccessMsg = "Day of week Details successfully updated with dayOfWeekNumber :";

	public String monthOfYearPostSuccessMsg = "Month of Year Details successfully saved with MonthOfYearNumber :";
	public String monthOfYearPutSuccessMsg = "Month of Year Details successfully updated with MonthOfYearNumber :";
	public String countryOrgStdPostSuccessMsg = "CountryOrganizationStandard Details successfully saved with organizationStandardCode :";

	public String countryOrgStdPutSuccessMsg = "CountryOrganizationStandard Details successfully updated with organizationStandardCode :";
	public String geoRltspPostSuccessMsg = "GeopoliticalRelationship Details successfully saved with GeoplRltspCmptId :";
	public String geoRltspPutSuccessMsg = "GeopoliticalRelationship Details successfully updated with GeoplRltspCmptId :";
	public String stProvStdPostSuccessMsg = " State Province Details successfully saved with stateProvinceCode :";
	public String stProvStdPutSuccessMsg = "State Province Details successfully updated with stateProvinceCode :";


	public String affilTypePostSuccessMsg = "GeoplAffilType Details successfully saved with affilTypeId :";
	public String affilTypePostSuccessNewMsg = "GeopoliticalAffiliatedType Details successfully saved with affiliationTypeId :";
	public String AffillengthExceeds10CharNew = "affilTypeCode cannot be greater than 10 characters";
	public String affilTypePutSuccessMsg = "GeoplAffilType Details successfully updated with affilTypeId :";
	public String langPostSuccessMsg = "Language Details successfully saved with languageCode :";
	public String langPutSuccessMsg = "Language Details successfully updated with languageCode :";
	public String cntryPostSuccessMsg = "Country Details successfully saved with GeoplId :";
	public String cntryPutSuccessMsg = "Country Details successfully updated with GeoplId:";
	public String addressLabelSuccessMsg = "Address Label Details successfully saved with GeopoliticalId :";
	public String addressLabelPutSuccessMsg = "Address Label Details successfully updated with GeopoliticalId :";
	public String affilTypePutSuccessNewMsg = "GeopoliticalAffiliatedType Details successfully updated with affiliationTypeId :";
	public String geoRltspPostSuccessNewMsg = "GeopoliticalRelationship Details successfully saved with GeopoliticalRelationshipComponentId :";
	public String geoRltspPutSuccessNewMsg = "GeopoliticalRelationship Details successfully updated with GeopoliticalRelationshipComponentId :";
	public String holidayPutSuccessNewMsg = "Holiday Details successfully updated with holidayId :";
	//***Get general success message
	public String getSuccessMsg = "Records Fetched Successfully";

	public String lengthExceeds80Char1 = " cannot be greater than 80 character!";

	//*** Address label Error message
	public String addressLineNumberNullMegNew = " should be in between 1 and 99 inclusive";
	public String lengthExceeds3CharLanguageCd = " cannot be greater than 3 characters!";

	//*** new Error Message

	public String invalidUrlMsgAddresslabelPOST = "Could not find the POST method for URL /v1/addressLa";
	public String invalidUrlMsgAddresslabelPUT = "Could not find the PUT method for URL /v1/addressLa";
	public String invalidUrlMsgAffilPOST = "Could not find the POST method for URL /v1/affiliati";
	public String invalidUrlMsgAffilPUT = "Could not find the PUT method for URL /v1/affiliati";
	public String invalidUrlMsgHolidayPOST = "Could not find the POST method for URL /v1/h";
	public String invalidUrlMsgHolidayPUT = "Could not find the PUT method for URL /v1/h";
	public String invalidUrlMsgUOMTypePOST = "Could not find the POST method for URL /v1/uo";
	public String invalidUrlMsgUOMTypePUT = "Could not find the PUT method for URL /v1/uo";
	public String invalidUrlMsgGeoRltspPOST = "Could not find the POST method for URL /v1/relat";
	public String invalidUrlMsgGeoRltspPUT = "Could not find the PUT method for URL /v1/relat";
	public String missingCommaInRequestMsg = "JSON parse error: Unexpected character ('\"' (code 34)): was expecting comma to separate Object entries";
	public String usedGETinCommanderrorMsg = "Request method 'GET' not supported";
	public String usedPOSTinGetErrorMsg = "Request method 'POST' not supported";
	public String missingHTTPHeaderInRequestMsg ="Missing HTTP header X-CSR-SECURITY_TOKEN";
	public String invalidUrlMsgMOYPost = "Could not find the POST method for URL /v1/month";
	public String invalidUrlMsgMOYPut = "Could not find the PUT method for URL /v1/month";
	public String invalidUrlMsgLangPost = "Could not find the POST method for URL /v1/la";
	public String invalidUrlMsgLangPut = "Could not find the PUT method for URL /v1/la";
	public String invalidUrlMsgGeoRlshipTypePost = "Could not find the POST method for URL /v1/relations";
	public String invalidUrlMsgGeoRlshipTypePut = "Could not find the PUT method for URL /v1/relations";
	public String invalidUrldowpostMsg1 = "[Could not find the POST method for URL /v1/d]";
	public String invalidUrldowputMsg1 = "[Could not find the PUT method for URL /v1/d]";
	public String invalidUrlMsgstProvPut1 = "[Could not find the PUT method for URL /v1/stP]";
	public String invalidUrlMsgstProvPOST1 = "[Could not find the POST method for URL /v1/stP]";
	public String invalidUrldentcountryMsgstPut1 = "[Could not find the PUT method for URL /v1/dependentCountryRelati]";
	public String invalidUrldentcountryMsgstPost1 = "[Could not find the POST method for URL /v1/dependentCountryRelati]";
	public String invalidUrlcountryOrgStdMsgstPut1 = "[Could not find the PUT method for URL /v1/cntry]";
	public String invalidUrlcountryOrgStdMsgstPost1 = "[Could not find the POST method for URL /v1/cntry]";

	//New query Message:
	public String invalidUrlAffilTypeGet = "Could not find the GET method for URL /v2/affiliationType";
	public String invalidUrlHolidayGet = "Could not find the GET method for URL /v2/holiday";
	public String invalidUrlUOMTypeGet = "Could not find the GET method for URL /v2/uomType";
	public String invalidUrlGeoRltspGet = "Could not find the GET method for URL /v2/relationship";
	public String invalidUrlDoWGet = "Could not find the GET method for URL /v2/daysOfWee";
	public String invalidUrlMoyGet = "Could not find the GET method for URL /v2/monthsOfYea";
	public String invalidUrlGeoOrgStdGet = "Could not find the GET method for URL /v2/geopoliticalOrganizationStandard";
	public String invalidUrlDepnCntryRltspTypeGet = "Could not find the GET method for URL /v2/dependentCountryRelationshipTyp";
	public String invalidUrlScriptGet = "Could not find the GET method for URL /v2/script";
	public String usePOST_Instead_GET = "Request method 'POST' not supported";
	public String missingHeaderTokenGET = "Missing HTTP header X-CSR-SECURITY_TOKEN";

	public String invalidUrlLanguageGet = "Could not find the GET method for URL /v2/language";
	public String invalidUrlAddreLblGet = "Could not find the GET method for URL /v2/addressLabel";
	public String geoplIdNullAddressLabelErrorMsg = " is a required field";

	//***Get general error message
	public String getErrorMsg = "There are no records for the specified criteria";

	public String addrLblGeoplIdErrorMsg = "Validation error of type FieldUndefined: Field 'geopoliticalI' in type 'AddressLabel' is undefined @ 'addressLabels/geopoliticalI'";
	public String addrLblLocaleCdErrorMsg = "Validation error of type FieldUndefined: Field 'localeCod' in type 'AddressLabel' is undefined @ 'addressLabels/localeCod'";
	public String addrLblAddrLineNumberErrorMsg = "Validation error of type FieldUndefined: Field 'addressLineNumbe' in type 'AddressLabel' is undefined @ 'addressLabels/addressLineNumbe'";
	public String addrLblBrandAddrErrorMsg = "Validation error of type FieldUndefined: Field 'brandAddressLineDescriptio' in type 'AddressLabel' is undefined @ 'addressLabels/brandAddressLineDescriptio'";
	public String addrLblapplicableErrorMsg = "Validation error of type FieldUndefined: Field 'applicabl' in type 'AddressLabel' is undefined @ 'addressLabels/applicabl'";


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
	public String moyLangLocaleErrorMsg = "Validation error of type FieldUndefined: Field 'translatedMOY' in type 'Locale' is undefined @ 'languages/locales/translatedMOY'";
	public String dowLangLocaleErrorMsg = "Validation error of type FieldUndefined: Field 'translatedDOW' in type 'Locale' is undefined @ 'languages/locales/translatedDOW'";
	public String langCodeErrorMsg = "Validation error of type FieldUndefined: Field 'languageCod' in type 'Language' is undefined @ 'languages/languageCod'";
	public String langNameErrorMsg = "Validation error of type FieldUndefined: Field 'engLanguageNam' in type 'Language' is undefined @ 'languages/engLanguageNam'";
	public String natScriptLangNameErrorMsg = "Validation error of type FieldUndefined: Field 'nativeScriptLanguageNam' in type 'Language' is undefined @ 'languages/nativeScriptLanguageNam'";
	public String langLocaleErrorMsg = "Validation error of type FieldUndefined: Field 'locale' in type 'Language' is undefined @ 'languages/locale'";

	public String langLocalelocaleCodeErrorMsg = "Validation error of type FieldUndefined: Field 'localeCod' in type 'Locale' is undefined @ 'languages/locales/localeCod'";
	public String langLocalecountryCodErrorMsg = "Validation error of type FieldUndefined: Field 'countryCod' in type 'Locale' is undefined @ 'languages/locales/countryCod'";
	public String langLocalellanguageCodErrorMsg = "Validation error of type FieldUndefined: Field 'languageCod' in type 'Locale' is undefined @ 'languages/locales/languageCod'";
	public String langLocalescriptCodErrorMsg = "Validation error of type FieldUndefined: Field 'scriptCod' in type 'Locale' is undefined @ 'languages/locales/scriptCod'";
	public String langLocaledateFullFormatDescriptioErrorMsg = "Validation error of type FieldUndefined: Field 'dateFullFormatDescriptio' in type 'Locale' is undefined @ 'languages/locales/dateFullFormatDescriptio'";
	public String langLocaledateLongFormatDescriptioErrorMsg = "Validation error of type FieldUndefined: Field 'dateLongFormatDescriptio' in type 'Locale' is undefined @ 'languages/locales/dateLongFormatDescriptio'";
	public String langLocaledateMediumFormatDescriptioErrorMsg = "Validation error of type FieldUndefined: Field 'dateMediumFormatDescriptio' in type 'Locale' is undefined @ 'languages/locales/dateMediumFormatDescriptio'";
	public String langLocaledateShortFormatDescriptioErrorMsg = "Validation error of type FieldUndefined: Field 'dateShortFormatDescriptio' in type 'Locale' is undefined @ 'languages/locales/dateShortFormatDescriptio'";
	public String langLocalecldrVersionNumbeErrorMsg = "Validation error of type FieldUndefined: Field 'cldrVersionNumbe' in type 'Locale' is undefined @ 'languages/locales/cldrVersionNumbe'";
	public String langLocalecldrVersionDatErrorMsg = "Validation error of type FieldUndefined: Field 'cldrVersionDat' in type 'Locale' is undefined @ 'languages/locales/cldrVersionDat'";
	public String langLocaleeffectiveDatErrorMsg = "Validation error of type FieldUndefined: Field 'effectiveDat' in type 'Locale' is undefined @ 'languages/locales/effectiveDat'";
	public String langLocaleexpirationDatErrorMsg = "Validation error of type FieldUndefined: Field 'expirationDat' in type 'Locale' is undefined @ 'languages/locales/expirationDat'";

	public String affilTypeErrorMsg1 = "Validation error of type FieldUndefined: Field 'affilTypeId1' in type 'GeoPoliticalAffiliatedType' is undefined @ 'affiliationTypes/affilTypeId1'";
	public String affilTypeErrorMsg2 = "Validation error of type FieldUndefined: Field 'affilTypeCode1' in type 'GeoPoliticalAffiliatedType' is undefined @ 'affiliationTypes/affilTypeCode1'";
	public String scriptInvalidAttribute1 = "Validation error of type FieldUndefined: Field 'scriptDescription1' in type 'Script' is undefined @ 'scripts/scriptDescription1'";
	public String scriptInvalidAttribute2 = "Validation error of type FieldUndefined: Field 'scriptName2' in type 'Script' is undefined @ 'scripts/scriptName2'";
	public String scriptInvalidAttribute3 = "Validation error of type FieldUndefined: Field 'scriptDescription2' in type 'Script' is undefined @ 'scripts/scriptDescription2'";



	public String ErrorMsg="Validation error of type FieldUndefined: Field 'holidayDateParameterTexts' in type 'Holiday' is undefined @ 'holidays/holidayDateParameterTexts'";
	public String ErrorMsg1="Validation error of type FieldUndefined: Field 'holidayNames' in type 'Holiday' is undefined @ 'holidays/holidayNames'";
	public String GeoOrgStdErrorMsg2="Validation error of type FieldUndefined: Field 'organizationStandardNames1' in type 'GeoPoliticalOrganizationStandard' is undefined @ 'geopoliticalOrganizationStandards/organizationStandardNames1'";
	public String GeoOrgStdErrorMsg1="Validation error of type FieldUndefined: Field 'organizationStandardCodes1' in type 'GeoPoliticalOrganizationStandard' is undefined @ 'geopoliticalOrganizationStandards/organizationStandardCodes1'";
	public String DepCntryOrgTypeErrorMsg1="Validation error of type FieldUndefined: Field 'dependentRelationshipIds' in type 'DependentCountryRelationship' is undefined @ 'dependentCountryRelationshipTypes/dependentRelationshipIds'";
	public String DepCntryOrgTypeErrorMsg2="Validation error of type FieldUndefined: Field 'dependentRelationshipDescriptions' in type 'DependentCountryRelationship' is undefined @ 'dependentCountryRelationshipTypes/dependentRelationshipDescriptions'";
	public String HolidayErrorMsg1="Validation error of type FieldUndefined: Field 'holidayNames' in type 'Holiday' is undefined @ 'holidays/holidayNames'";
	public String HolidayErrorMsg2="Validation error of type FieldUndefined: Field 'holidayDateParameterTexts' in type 'Holiday' is undefined @ 'holidays/holidayDateParameterTexts'";
	public String DepCntryOrgeErrorMsg1="Validation error of type WrongType: argument 'dependentCountryCd' with value 'StringValue{value=''}' is not a valid 'Long' @ 'countries'";
	public String DepCntryOrgeErrorMsg2="Validation error of type FieldUndefined: Field 'geopoliticalIds' in type 'Country' is undefined @ 'countries/geopoliticalIds'";
	public String DepCntryOrgeErrorMsg3="Validation error of type FieldUndefined: Field 'dependentCountryRelationships' in type 'Country' is undefined @ 'countries/dependentCountryRelationships'";

	public String geoTypeInvalid1AttributeGraphQLMsg = "Validation error of type FieldUndefined: Field 'geopoliticalTypeId1' in type 'GeoPoliticalType' is undefined @ 'geopoliticalTypes/geopoliticalTypeId1'";
	public String geoTypeInvalid2AttributeGraphQLMsg = "Validation error of type FieldUndefined: Field 'geopoliticalTypeName2' in type 'GeoPoliticalType' is undefined @ 'geopoliticalTypes/geopoliticalTypeName2'";
	public String countryInvalidAttributeGraphQLMsg1 = "Validation error of type FieldUndefined: Field 'geopoliticalId1' in type 'Country' is undefined @ 'countries/geopoliticalId1'";
	public String countryInvalidAttributeGraphQLMsg2 = "Validation error of type FieldUndefined: Field 'geopliticalId' in type 'Country' is undefined @ 'countries/geopliticalId'";
	public String countryInvalidAttributeGraphQLMsg3 = "Validation error of type FieldUndefined: Field 'countryNumericCode1' in type 'Country' is undefined @ 'countries/countryNumericCode1'";
	public String countryExpiredTokenGraphQLMsg = "Unable to find a certificate that can decode the token";
	public String countryInvalidTokenGraphQLMsg = "Input byte array has wrong 4-byte ending unit";
	public String geoRltspInvalid1AttributeGraphQLMsg = "Validation error of type FieldUndefined: Field 'geopoliticalComponentId1' in type 'GeoPoliticalRelationship' is undefined @ 'relationships/geopoliticalComponentId1'";
    public String geoRltspInvalid2AttributeGraphQLMsg = "Validation error of type FieldUndefined: Field 'relateGeopoliticalComponentId2' in type 'GeoPoliticalRelationship' is undefined @ 'relationships/relateGeopoliticalComponentId2'";

	//***error messages  " is a required field"

	public String requiredFieldMsg = " is a required field";
	public String invalidUrlMsg = "Not Found";
	public String invalidPostUrlMsg = "Could not find the POST method for URL";
	public String invalidPutUrlMsg = "Could not find the PUT method for URL";
	public String invalidGetUrlMsg = "Could not find the GET method for URL";
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
	public String lengthExceeds1to7digit = " should be between 1 and 7";
	public String MoyNumInBetween1and12 = " should be between 1 and 12";
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
	public String lengthExceeds4000Char1 = "scriptDesc cannot be greater than 4000 characters";
	public String geoRltspTypeCdLength = "geopoliticalRelationshipTypeCd cannot be greater than 20 characters!";

	public String metaValidationMsg = " cannot be a null field!";
	public String EffectiveDateBlankMsg = "Effective Date cannot be a null field!";
	public String lengthExceeds65CharMsg = " cannot be greater than 65 characters";
	public String lengthExceeds400CharMsg = " cannot be greater than 400 characters";
	public String holidaylengthExceeds400CharMsg = "holidayDateParamText cannot be greater than 400 characters";
	public String inValidFieldMsg = " is not a valid value";
	public String geoTypeMsg = "Geopolitical Type of State not found";
	public String lengthExceeds2Char = " cannot be greater than 2 characters!";
	public String lengthExceeds35Char = " cannot be greater than 35 characters!";
	public String lengthExceeds80Char = " cannot be greater than 80 characters!";
	public String lengthExceeds3Char1 = " cannot be greater than 3 characters!";
	public String lengthExceeds50CharNew =" cannot be greater than 50 characters";
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
	public String currencyNumberCodeNullMsg = "currencyNumberCode is a required field";
	public String affiltypecdnullmsg = "JSON parse error: InvalidFormatException: Cannot deserialize value of type `java.lang.Long` from String ";

	public String geoOrgStdPostNewSuccessMsg = "GeopoliticalOrganizationStandard Details successfully saved with organizationStandardCode :";
	public String geoOrgStdPutNewSuccessMsg = "GeopoliticalOrganizationStandard Details successfully updated with organizationStandardCode :";
	public String scriptPostNewSuccessMsg = "REFSCRIPT Details successfully saved with scriptCode :";
	public String scriptPutNewSuccessMsg = "REFSCRIPT Details successfully updated with scriptCode :";
	public String scriptlengthExceeds4000CharNewMsg = "scriptDesc cannot be greater than 4000 characters";
	public String cntryNewPostSuccessMsg = "Country Details successfully saved with geopoliticalId :";
	public String cntryNewPutSuccessMsg = "Country Details successfully updated with geopoliticalId:";

	//Address Labels
	public String addressLabelLocalCode = "locale code is a required field";
	public String applicableFlagRequiredMeg = " code is a required field";
	public String lengthExceeds35Char1 = " cannot be greater than 35 character!";
	public String lengthExceeds18CharLocalCd = " cannot be greater than 18 characters!";
		public String NotValidValueMsg = " is not a valid value";
		public String addressLineNumberNullMeg = "addressLineNumber is a required field";
		public String lengthExceeds80CharAddressLabel = " cannot be greater than 80 character!";
		public String lengthExceeds35CharAddressLabel = " cannot be greater than 35 character!";

		public String Invalidgeopolitical = "There are no records for the specified criteria";
		public String InvalidgeopoliticalIDAndLocale = "There are no records for the specified criteria";
		public String InvalidgeopoliticalIDAndLocaleAndAddressLine = "There are no records for the specified criteria";


		 public String addressLabelsErrorMsg1 = "Validation error of type FieldUndefined: Field 'countryCodee' in type 'AddressLabels' is undefined @ 'AddressLabels/countryCodee'";
			public String addressLabelsErrorMsg2 = "Validation error of type FieldUndefined: Field 'languageCodee' in type 'AddressLabels' is undefined @ 'AddressLabels/languageCodee'";
			public String addressLabelsErrorMsg3 = "Validation error of type FieldUndefined: Field 'addressLineNumberr' in type 'AddressLabels' is undefined @ 'AddressLabels/addressLineNumberr'";
}
