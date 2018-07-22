package com.github.hi_fi.tcpMockeServer.data;

import java.util.HashMap;
import java.util.Map;

import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class RequestCache {
	
	private Map<String, Message> cachedRequests = new HashMap<String, Message>();
	
	public Boolean isRequestInCache(String requestHash) {
		return this.cachedRequests.containsKey(requestHash);
	}
	
	public void addRequestToCache(String requestHash, Message responseMessage) {
		this.cachedRequests.put(requestHash, responseMessage);
	}
	
	public Message getCachedResponse(String requestHash) {
		return this.cachedRequests.get(requestHash);
	}
	
	public Map<String, Message> getCachedMessages() {
	    return this.cachedRequests;
	}
	
}
