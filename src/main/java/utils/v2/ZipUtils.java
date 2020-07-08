package utils.v2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class ZipUtils {
	
	
	public static String zipFiles() {
		String zipFile = "Extend_Reports_Mail.zip";
		 String srcDir = "./Extend_Reports_Mail/";
	try {
		
	File srcFile = new File(srcDir);
	File[] files = srcFile.listFiles();
	FileOutputStream fos = new FileOutputStream(zipFile);

	ZipOutputStream zos = new ZipOutputStream(fos);

	for (int i = 0; i < files.length; i++) {

	// create byte buffer
	byte[] buffer = new byte[1024];

	FileInputStream fis = new FileInputStream(files[i]);

	zos.putNextEntry(new ZipEntry(files[i].getName()));

	int length;

	   while ((length = fis.read(buffer)) > 0) {
	  zos.write(buffer, 0, length);
	}

	 zos.closeEntry();

	// close the InputStream
	 fis.close();

	}
	zos.close();
	}
	//
	catch (Exception e)
	{
		return e.getMessage();
	}
	return zipFile;
	}
	
    public void sendEmail(Session session, String senderEmailID, String receiverEmailID, String zipFileName, String testLevel){
		try
	    {
			Message message = new MimeMessage(session);
			Multipart multiPart = new MimeMultipart();
			
			message.setFrom(new InternetAddress(senderEmailID));
			message.addRecipients(Message.RecipientType.TO,InternetAddress.parse(receiverEmailID));

			message.setSubject("Geopolitical WebService Regression Extent Report");
			
			
			MimeBodyPart extentReport = new MimeBodyPart();
			String zippedFilename = zipFileName;
			DataSource zippedFileSource = new FileDataSource(zippedFilename);
			extentReport.setDataHandler(new DataHandler(zippedFileSource));
			extentReport.setFileName(zipFileName);

			MimeBodyPart textPart = new MimeBodyPart();
			textPart.setText("<html>" + " <body>" + "  <p style='font-family:Calibri;'>Hi Team,</p>"
					+ "<p style='font-family:Calibri;'> Please find the attached Geopolitical Web Service execution report.</p>"
				
			+ "<p style='font-family:Calibri;'>Test Level: "+testLevel+ "</p>" + "<p><br></p>"

					+ "<p style='font-family:Calibri;'>Thanks and Regards,<br>Geopolitical Team</p>" + "<p><br></p>"

					+ "<p style='font-family:Calibri;'><font size='1'><center><<------------------------------------------------This is an automated email triggered from Geopilitical Automation Team-------------------------------------------->></center></font></p>"

					+ "</body>" + "</html>", "US-ASCII", "html");

			multiPart.addBodyPart(extentReport);
			multiPart.addBodyPart(textPart);

			message.setContent(multiPart);
			Transport.send(message);

	      System.out.println("---***----- EMail Sent Successfully---***----");
	    }
	    catch (Exception e) {
	      e.printStackTrace();
	    }
	}
    
    
}
