package com.github.hi_fi.tcpMockeServer.service;

import java.util.Arrays;
import java.util.zip.GZIPInputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Component;

import com.github.hi_fi.tcpMockeServer.data.RequestCache;
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
	
	@ServiceActivator(inputChannel = "ServiceChannel")
	public Message handleRequestMessage(Message message) {
		String requestHash = hasher.getPayloadHash(message);
		Message responseMessage;
		if (cache.isRequestInCache(requestHash)) {
			log.debug("Returning cached message for "+requestHash);
			responseMessage = cache.getCachedResponse(requestHash);
		} else {
			log.debug("Requesting response from backend for "+requestHash);
			responseMessage = gw.sendToBacked(message);
			cache.addRequestToCache(requestHash, responseMessage);
		}
		return responseMessage;
	}
}
