package utils.v1;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class ExcelUtil {
	
	public HashMap<String, LinkedHashMap<String, String>> getTestData(String userStoryName) throws IOException {
		String FilePath = "./WS_TestData.xlsx";
		String testCaseId = null;
		HashMap<String, LinkedHashMap<String, String>> Excel = new LinkedHashMap<String, LinkedHashMap<String, String>>();
		File inputWorkbook = new File(FilePath);
		FileInputStream fis = new FileInputStream(inputWorkbook);
		if (FilePath.toString().endsWith(".xlsx")) {
			XSSFWorkbook w = new XSSFWorkbook(fis);
			XSSFSheet sheet = w.getSheet(userStoryName);
			int rowcount = sheet.getPhysicalNumberOfRows();
			for (int i = 1; i < rowcount; i++) {
				LinkedHashMap<String, String> list = new LinkedHashMap<String, String>();
				int columncount = sheet.getRow(i).getLastCellNum();
				for (int j = 0; j < columncount; j++) {
					String columnName = sheet.getRow(0).getCell(j).toString();
					String columnValue = sheet.getRow(i).getCell(j).toString();
					if (columnName.equalsIgnoreCase("TC_ID")) {
						testCaseId = columnValue;
					}
					list.put(columnName, columnValue);
				}
				Excel.put(testCaseId, list);
			}
			fis.close();
		}
		return Excel;
	}
	
	public void createResultExcel(String fileName)
	{
		File f = new File("./TestResults/"+fileName+".xlsx");
		if(f.exists()) f.delete();
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet spreadsheet = workbook.createSheet(fileName+" Results");
	    XSSFRow rowhead = spreadsheet.createRow((short)0);
	    rowhead.setHeightInPoints((2*spreadsheet.getDefaultRowHeightInPoints()));
	    CellStyle style = workbook.createCellStyle();
	    style.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
	    style.setAlignment(XSSFCellStyle.ALIGN_CENTER);
	    
	    style.setFillForegroundColor(IndexedColors.VIOLET.getIndex());
	    style.setFillPattern(CellStyle.SOLID_FOREGROUND); 
	    XSSFFont font = workbook.createFont();
	    font.setColor(IndexedColors.WHITE.getIndex());
	    font.setBold(true);
	    style.setFont(font);
	    style.setBorderBottom(XSSFCellStyle.BORDER_MEDIUM);
	    style.setBorderTop(XSSFCellStyle.BORDER_MEDIUM);
	    style.setBorderRight(XSSFCellStyle.BORDER_MEDIUM);
	    style.setBorderLeft(XSSFCellStyle.BORDER_MEDIUM);
	    rowhead.createCell(0).setCellValue("TestCase ID");
	    rowhead.createCell(1).setCellValue("TestCaseDescription");
	    rowhead.createCell(2).setCellValue("ScenarioType");
	    rowhead.createCell(3).setCellValue("Input Request");
	    rowhead.createCell(4).setCellValue("Input Data");
	    rowhead.createCell(5).setCellValue("DB Data");
	    rowhead.createCell(6).setCellValue("WS Status");
	    rowhead.createCell(7).setCellValue("WS Status Code");
	    rowhead.createCell(8).setCellValue("WS Response");
	    rowhead.createCell(9).setCellValue("TestResult");
	    rowhead.createCell(10).setCellValue("Comments");
	    
	    for(int j=0; j<11; j++)
	    {
	    	rowhead.getCell(j).setCellStyle(style);
	    	spreadsheet.autoSizeColumn(j);
	    }
	    
	    FileOutputStream out;
		try {
			out = new FileOutputStream(new File("./TestResults/"+fileName+".xlsx"));
    	    workbook.write(out);
	        out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	     
	}
	
	public void writeExcel(String fileName, String testCaseId, String testDesc, String scenarioType,
			String inputReq, String inputData, String inputDB,
			String wsStatus, String wsStatusCode, String wsResponse, String testResult, String Comments)
	{
		try {
			String excelFilePath = "./TestResults/"+fileName+".xlsx";
			File inputWorkbook = new File(excelFilePath);
			FileInputStream fis = new FileInputStream(inputWorkbook);
			XSSFWorkbook w = new XSSFWorkbook(fis);
			XSSFSheet sheet = w.getSheetAt(0);
			int rowCount = sheet.getLastRowNum();
			Object[][] cellValues = {
					{testCaseId, testDesc, scenarioType, inputReq, inputData, inputDB,  wsStatus, wsStatusCode,
						wsResponse, testResult, Comments}
			};
			XSSFRow row = sheet.createRow(++rowCount);
//			row.setHeightInPoints((15*sheet.getDefaultRowHeightInPoints()));
			for (Object[] a : cellValues) {
 
                int columnCount = 0;
                 
                XSSFCell cell = row.createCell(columnCount);
                 
                for (Object field : a) {                    
                    cell.setCellValue((String)field);
                    CellStyle cs = w.createCellStyle();
                    cs.setWrapText(true);
                    row.setHeight((short)-1);
                    cs.setAlignment(XSSFCellStyle.ALIGN_LEFT);
                    cs.setVerticalAlignment(XSSFCellStyle.VERTICAL_TOP);
                    cs.setBorderBottom(XSSFCellStyle.BORDER_THIN);
                    cs.setBorderTop(XSSFCellStyle.BORDER_THIN);
                    cs.setBorderRight(XSSFCellStyle.BORDER_THIN);
                    cs.setBorderLeft(XSSFCellStyle.BORDER_THIN);
                    cell.setCellStyle(cs);
                    XSSFFont font = w.createFont();
                    if(testResult.equalsIgnoreCase("Pass")) {
                    	font.setColor(IndexedColors.GREEN.getIndex());
                    }else {
                    	font.setColor(IndexedColors.RED.getIndex());
                    }
            	    font.setBold(false);
            	    cs.setFont(font);
                    cell = row.createCell(++columnCount);
                }
 
            }
			sheet.setColumnWidth(1, 6000);
			sheet.setColumnWidth(3, 9000);
			sheet.setColumnWidth(4, 6000);
			sheet.setColumnWidth(5, 6000);
			sheet.setColumnWidth(8, 6000);
			fis.close();
			 
            FileOutputStream outputStream = new FileOutputStream("./TestResults/"+fileName+".xlsx");
            w.write(outputStream);
            fis.close();
            outputStream.close();
		}catch (IOException e) {
			e.printStackTrace();
		}
	}

}
