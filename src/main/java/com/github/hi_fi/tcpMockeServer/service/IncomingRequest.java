package com.github.hi_fi.tcpMockeServer.service;

import java.util.Arrays;
import java.util.zip.GZIPInputStream;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;

import com.github.hi_fi.tcpMockeServer.MockInit;
import com.github.hi_fi.tcpMockeServer.data.RequestCache;
import com.github.hi_fi.tcpMockeServer.mock.IMockService;
import com.github.hi_fi.tcpMockeServer.model.Mock;
import com.github.hi_fi.tcpMockeServer.parsers.IPayloadParser;
import com.github.hi_fi.tcpMockeServer.proxy.Route.Gateway;
import com.github.hi_fi.tcpMockeServer.utils.Hasher;
import com.github.hi_fi.tcpMockeServer.utils.HttpHeaderHandler;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class IncomingRequest {
	
	@Autowired
	Gateway gw;
	
	@Autowired
	Hasher hasher;
	
	@Autowired
	RequestCache cache;
	
	@Autowired
	BeanFactory bf;
	
	@Autowired
	MockInit mi;
	
	@ServiceActivator(inputChannel = "ServiceChannel")
	public Message<byte[]> handleRequestMessage(Message<byte[]> message) {
		String requestHash = hasher.getPayloadHash(message);
		Message<byte[]> responseMessage;
		if (cache.isRequestInCache(requestHash)) {
			log.debug("Returning cached message for "+requestHash);
			byte[] responsePayload = cache.getCachedResponsePayload(requestHash).clone();
			Mock mock = mi.getServices().stream()
                    .filter(m -> message.getHeaders().get("mockName").equals(m.getName()))
                    .findFirst().orElse(null);
            if (mock != null && !mock.getBytesToCopy().isEmpty()) {
                for (String copy: mock.getBytesToCopy().split(",")) {
                    String[] replacements = copy.split(">");
                    //Direct editing of response's payload can cause interesting issues, so cloning it first
                    responsePayload[Integer.parseInt(replacements[0])] = (message.getPayload())[Integer.parseInt(replacements[1])];
                }
            }
            responseMessage = new GenericMessage<byte[]>(responsePayload, message.getHeaders());
		} else if (message.getHeaders().get("mockBeanName") != null) {
			responseMessage = ((IMockService)bf.getBean(message.getHeaders().get("mockBeanName").toString())).getResponse(message);
			cache.addResponseToCache(requestHash, responseMessage);
		} else {
			log.debug("Requesting response from backend for "+requestHash);
			responseMessage = gw.sendToBacked(message);
			cache.addResponseToCache(requestHash, responseMessage);
		}
		return responseMessage;
	}
}
