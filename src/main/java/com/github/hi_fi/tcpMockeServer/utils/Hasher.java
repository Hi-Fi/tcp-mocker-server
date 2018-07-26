package com.github.hi_fi.tcpMockeServer.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import com.github.hi_fi.tcpMockeServer.data.RequestCache;
import com.github.hi_fi.tcpMockeServer.parsers.SoapParser;

import lombok.extern.slf4j.Slf4j;

@Service
public class Hasher {
	
	@Autowired
	BeanFactory bf;
	
	@Autowired
	RequestCache rc;
	
	public String getPayloadHash(Message message) {
		String messageContent = new String((byte[]) message.getPayload());
		if (messageContent.startsWith("POST") && messageContent.toLowerCase().contains("soapaction")) {
			messageContent = bf.getBean(SoapParser.class).getHashablePayload(message);
		}
		String hash = DigestUtils.sha256Hex(messageContent);
		rc.addRequestToCache(hash, message);
		return hash;
	}
}
