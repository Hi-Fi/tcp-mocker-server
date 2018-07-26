package com.github.hi_fi.tcpMockeServer.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import com.github.hi_fi.tcpMockeServer.parsers.SoapParser;

@Service
public class Hasher {
	
	@Autowired
	BeanFactory bf;
	
	public String getPayloadHash(Message message) {
		String messageContent = new String((byte[]) message.getPayload());
		if (messageContent.startsWith("POST") && messageContent.toLowerCase().contains("soapaction")) {
			messageContent = bf.getBean(SoapParser.class).getHashablePayload(message);
		}
		return DigestUtils.sha256Hex(messageContent);
	}
}
