package com.github.hi_fi.tcpMockeServer.mock;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Service;

/*
 * Example of mock that handles responses to request (no need to proxy)
 */
@Service
public class SimpleCalculatorMock implements IMockService {

	@Override
	public Message getResponse(Message message) {
		MessageFactory factory;
		try {
			String resultingMessage = "HTTP/1.1 200 OK\r\n";
			factory = MessageFactory.newInstance();
			SOAPMessage soapMsg = factory.createMessage();
			SOAPPart part = soapMsg.getSOAPPart();

			SOAPEnvelope envelope = part.getEnvelope();
			SOAPHeader header = envelope.getHeader();
			SOAPBody body = envelope.getBody();

			SOAPBodyElement element = body
					.addBodyElement(envelope.createName("AddResponse", "tem", "http://tempuri.org/"));
			element.addChildElement("AddResult").addTextNode("Response coming from mock");
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			soapMsg.writeTo(out);
			resultingMessage = resultingMessage + "Content-length: "+out.toByteArray().length+"\r\n\r\n"+ out.toString();
			return new GenericMessage(resultingMessage.getBytes(), message.getHeaders());
		} catch (SOAPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

}
