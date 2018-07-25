package com.github.hi_fi.tcpMockeServer.data;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import com.github.hi_fi.tcpMockeServer.model.Response;
import com.github.hi_fi.tcpMockeServer.utils.MessageContentParser;

@Component
public class RequestCache {
    
    @Autowired
    MessageContentParser mcp;
	
	private Map<String, Message> cachedRequests = new HashMap<String, Message>();
	private Map<String, Response> parsedCachedRequests = new HashMap<String, Response>();
	
	public Boolean isRequestInCache(String requestHash) {
		return this.cachedRequests.containsKey(requestHash);
	}
	
	public void addRequestToCache(String requestHash, Message responseMessage) {
		this.cachedRequests.put(requestHash, responseMessage);
		this.parsedCachedRequests.put(requestHash, Response.builder().messageContent(mcp.getMessageContent(responseMessage)).messageHeaders(responseMessage.getHeaders().toString()).build());
		System.out.println(this.parsedCachedRequests.get(requestHash).getMessageContent());
	}
	
	public Message getCachedResponse(String requestHash) {
		return this.cachedRequests.get(requestHash);
	}
	
	public Map<String, Message> getCachedMessages() {
	    return this.cachedRequests;
	}
	
	public Map<String, Response> getParsedCachedResponses() {
	    return this.parsedCachedRequests;
	}
	
}
