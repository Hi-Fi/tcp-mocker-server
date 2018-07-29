package com.github.hi_fi.tcpMockeServer.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Service;

import com.github.hi_fi.tcpMockeServer.MockInit;
import com.github.hi_fi.tcpMockeServer.model.Mock;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class HttpHeaderHandler {

	@Autowired
	MockInit mi;
	
	public Message replaceHostWithRealEndpoint(Message message) {
		log.debug("Replacing HTTP header information in message payload");
		String messageContent = new String((byte[]) message.getPayload());
		log.debug(messageContent);
		String mockName = message.getHeaders().get("mockName").toString();
		Mock mock = mi.getServices().stream()
				      .filter(m -> mockName.equals(m.getName()))
				      .findFirst()
				      .orElse(null);
		messageContent = messageContent.replaceAll("Host:.*", "Host: "+mock.getTargetHost()+":"+mock.getTargetPort());
		log.debug(messageContent);
		return new GenericMessage(messageContent.getBytes(), message.getHeaders());
	}
	
	public Boolean isHttpRequest(Message message) {
		String messageContent = new String((byte[]) message.getPayload());
		return messageContent.contains("HTTP/");
	}
}
