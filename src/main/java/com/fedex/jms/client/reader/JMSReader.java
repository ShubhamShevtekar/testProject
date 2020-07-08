//package scenarios.GEO;
package com.fedex.jms.client.reader;

import javax.jms.BytesMessage;
import javax.jms.MessageConsumer;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSession;

import org.json.JSONObject;
import org.testng.annotations.Test;

import utils.v1.Reporting;

public class JMSReader extends Reporting {

	public QueueConnectionFactory factory;
    public QueueSession session;
    public javax.jms.Queue queue;
    public QueueConnection connection;
    public QueueReceiver queueReceiver;
	@Test
	public JSONObject messageGetsPublished(String servicName)  {
		JSONObject resultJSON = null ;

		try {
			Thread.sleep(5000);
			factory = new com.tibco.tibjms.TibjmsQueueConnectionFactory("tcp://mitms018.ute.fedex.com:52214");
			connection = factory.createQueueConnection("GEOPOLITIC-3534861", "frLqO5gaj9kr2ghcGR946QWWw");
			session = connection.createQueueSession(false, javax.jms.Session.AUTO_ACKNOWLEDGE);
//			session = connection.createQueueSession(false, javax.jms.Session.CLIENT_ACKNOWLEDGE);
			queue = session.createQueue("FDXGEOPOLITICAL.GEOPEFS.GEOPOLITICAL.TESTQ");
			queueReceiver = session.createReceiver(queue);
			MessageConsumer queueConsumer = session.createConsumer(queue);
			connection.start();
			BytesMessage byteMessage = null;

			while (true) {
				Object message = queueConsumer.receive(2000);
				if (message == null) {
					break;
				}
				if (message instanceof BytesMessage) {
					byteMessage = (BytesMessage) message;
				}
			}

			if (byteMessage != null) {
				byte[] byteData = null;
				byteData = new byte[(int) byteMessage.getBodyLength()];
				byteMessage.readBytes(byteData);
				byteMessage.reset();
				String ans = new String(byteData);

//				JsonPath js = new JsonPath(ans);
				//***validation of service source and name, needs to be done initial validations
				if(byteMessage.getStringProperty("MsgSource").equalsIgnoreCase("GEOPCORE") && byteMessage.getStringProperty("DataSegment").equalsIgnoreCase(servicName)){
					resultJSON = new JSONObject(ans);
					test.pass("msgSource  validation passed: "+byteMessage.getStringProperty("MsgSource"));
		        	test.pass("DataSegement  validation passed: "+byteMessage.getStringProperty("DataSegment"));
				}
				else
				{
					test.fail("msgSource  validation failed: "+byteMessage.getStringProperty("MsgSource"));
		        	test.fail("DataSegement  validation failed: "+byteMessage.getStringProperty("DataSegment"));
				}

				//***complete json
				//***get particular attribute value

			}
			session.close();
		    connection.close();
		} catch (Exception e) {
			e.printStackTrace();
		}


		return resultJSON;

	}

}