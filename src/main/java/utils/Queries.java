package utils;

public class Queries {
	
	public String geoTypePostQuery(String whereField) {
		String geoPostQuery = "select * from geopl_type where GEOPL_TYPE_NM='" + whereField + "'";		
		return geoPostQuery;
	}
	
	public String geoTypePutQuery(String whereField) {
		String geoPutQuery = "select * from geopl_type where GEOPL_TYPE_ID='" + whereField + "'";		
		return geoPutQuery;
	}
	
	public String geoRsTypePostQuery(String whereField) {
		String geoPostQuery = "select * from geopl_rltsp_type where geopl_rltsp_type_cd='" + whereField + "'";		
		return geoPostQuery;
	}
	
	public String geoOrgStdPostQuery(String whereField) {
		String geoPostQuery = "select * from geopl_org_std where ORG_STD_CD='" + whereField + "'";		
		return geoPostQuery;
	}
	
	public String depnCntryRltspPostQuery(String whereField) {
		String geoPostQuery = "select * from depn_cntry_rltsp where DEPN_RLTSP_DESC='" + whereField + "'";		
		return geoPostQuery;
	}
	
	public String depnCntryRltspPutQuery(String whereField) {
		String geoPostQuery = "select * from depn_cntry_rltsp where DEPN_RLTSP_ID='" + whereField + "'";		
		return geoPostQuery;
	}
	
	public String holidayPostQuery(String whereField) {
		String geoPostQuery = "select * from holiday where HDAY_NM='" + whereField + "'";		
		return geoPostQuery;
	}
	
	public String holidayPutQuery(String whereField) {
		String geoPostQuery = "select * from holiday where HDAY_ID='" + whereField + "'";		
		return geoPostQuery;
	}
	
	public String uomTypePostQuery(String whereField) {
		String geoPostQuery = "select * from ref_uom_type where UOM_TYPE_CD='" + whereField + "'";		
		return geoPostQuery;
	}
	
	public String scriptPostQuery(String whereField) {
		String geoPostQuery = "select * from ref_script where SCRIPT_CD='" + whereField + "'";		
		return geoPostQuery;
	}
	
	public String dayOfWeekPostQuery(String whereField) {
		String geoPostQuery = "select * from ref_day_of_week where DAY_OF_WEEK_NBR='" + whereField + "'";		
		return geoPostQuery;
	}
	
	public String monthOfYearPostQuery(String whereField) {
		String geoPostQuery = "select * from ref_month_of_year where MONTH_OF_YEAR_NBR='" + whereField + "'";		
		return geoPostQuery;
	}
	
	public String countryOrgStdPostQuery(String cntryCode, String cntryShName, String cntryFullName, String orgStdCode)
	{
		String CNTRY_FULL_NM;
		if(cntryFullName.isEmpty()){
			CNTRY_FULL_NM = "and CNTRY_FULL_NM is null";
		}else{
			CNTRY_FULL_NM = "and CNTRY_FULL_NM ='"+cntryFullName+"'";
		}
		String geoPostQuery = "select GEOPL_ID, CNTRY_SHT_NM, CNTRY_FULL_NM, ORG_STD_CD, to_char(EFFECTIVE_DT,'YYYY-MM-DD') \"EFFECTIVE_DT\", to_char(EXPIRATION_DT,'YYYY-MM-DD') \"EXPIRATION_DT\", " + 
				"CREATED_BY_USER_ID, LAST_UPDATED_BY_USER_ID from cntry_org_std where geopl_id in (select geopl_id from country where cntry_cd='"+cntryCode+"') and "
				+ "CNTRY_SHT_NM = '"+cntryShName+"'"+CNTRY_FULL_NM+" and ORG_STD_CD = '"+orgStdCode+"'";		
		return geoPostQuery;
	}
	
	public String geopRltspPostQuery(String whereField1, String whereField2 , String whereField3) {
		String geoPostQuery = "select GEOPL_COMPT_ID, RELTD_GEOPL_COMPT_ID, GEOPL_RLTSP_TYPE_CD,  "
				+ "to_char(EFFECTIVE_DT,'YYYY-MM-DD') \"EFFECTIVE_DT\", to_char(EXPIRATION_DT,'YYYY-MM-DD') \"EXPIRATION_DT\", " 
				+"CREATED_BY_USER_ID, LAST_UPDATED_BY_USER_ID from geopl_rltsp where GEOPL_COMPT_ID='" + whereField1 + "' and RELTD_GEOPL_COMPT_ID = '" + whereField2 + "' "
				+"and EFFECTIVE_DT ='"+whereField3 +"'";		
		return geoPostQuery;
	}
		
	public String stProvStdPostQuery(String whereField1,String whereField2) {
		String geoPostQuery = "select GEOPL_ID, ORG_STD_CD, ST_PROV_CD, ST_PROV_NM, to_char(EFFECTIVE_DT,'YYYY-MM-DD') \"EFFECTIVE_DT\", to_char(EXPIRATION_DT,'YYYY-MM-DD') \"EXPIRATION_DT\","
				+ " CREATED_BY_USER_ID, LAST_UPDATED_BY_USER_ID from st_prov_std where ST_PROV_CD='" + whereField1 + "'"
				+" and EFFECTIVE_DT='"+ whereField2 +"'";		
		return geoPostQuery;
	}
	
	public String affilTypePostQuery(String whereField) {
		String geoPostQuery = "select * from geopl_affil_type where AFFIL_TYPE_CD='" + whereField + "'";		
		return geoPostQuery;
	}
	
	public String affilTypePutQuery(String whereField) {
		String geoPostQuery = "select * from geopl_affil_type where AFFIL_TYPE_ID='" + whereField + "'";		
		return geoPostQuery;
	}
	
	public String langPostQuery(String whereField) {
		String geoPostQuery = "select * from ref_language  where LANGUAGE_CD='" + whereField + "'";		
		return geoPostQuery;
	}
	
	public String langTrnslDowPostQuery(String whereField1, String whereField2) {
		String geoPostQuery = "select * from trnsl_dow where LANGUAGE_CD='" + whereField1 + "' and DAY_OF_WEEK_NBR='" + whereField2 + "'";		
		return geoPostQuery;
	}
	
	public String langTrnslMonthOfYearPostQuery(String whereField1, String whereField2) {
		String geoPostQuery = "select * from TRNSL_MTH_OF_YR  where LANGUAGE_CD='" + whereField1 + "' and MONTH_OF_YEAR_NBR='" + whereField2 + "'";		
		return geoPostQuery;
	}

}