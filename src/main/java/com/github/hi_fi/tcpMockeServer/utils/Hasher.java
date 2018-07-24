package com.github.hi_fi.tcpMockeServer.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import com.github.hi_fi.tcpMockeServer.data.FieldsToClear;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class Hasher {
	
	@Autowired
	private FieldsToClear ftc; 
	
	public String getPayloadHash(Message message) {
		String messageContent = new String((byte[]) message.getPayload());
		if (messageContent.startsWith("POST") && messageContent.toLowerCase().contains("soapaction")) {
			//SOAP message, making some modifications to get valid hash.
			//Clearing out the elements that are mentioned on the configuration
			log.debug(messageContent);
			for (String fieldName : ftc.getFieldsToClear(message.getHeaders().get("mockName").toString())) {
				messageContent = messageContent.replaceAll("<([a-z0-9]+?:)"+fieldName+"(\\s.*?)?>.*?<?[a-z0-9]+?:"+fieldName+">", "<$1"+fieldName+" $2>XXX</$1"+fieldName+">");
			}
			messageContent = messageContent.substring(messageContent.indexOf("<"));
			log.debug(messageContent);
		}
		return DigestUtils.sha256Hex(messageContent);
	}
}
