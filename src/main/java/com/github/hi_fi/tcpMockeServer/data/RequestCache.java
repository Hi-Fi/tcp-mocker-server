package com.github.hi_fi.tcpMockeServer.data;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import com.github.hi_fi.tcpMockeServer.model.MessageData;
import com.github.hi_fi.tcpMockeServer.utils.MessageContentParser;

@Component
public class RequestCache {
    
    @Autowired
    MessageContentParser mcp;
	
	private Map<String, MessageData> messageDatas = new HashMap<String, MessageData>();
	
	public Boolean isRequestInCache(String requestHash) {
		return this.messageDatas.containsKey(requestHash) && this.messageDatas.get(requestHash).getMockResponse() != null;
	}
	
	public void addRequestContentToCache(String requestHash, String messageContent) {
	    MessageData messageData;
	    if (messageDatas.containsKey(requestHash)) {
	        messageData = messageDatas.get(requestHash);
	        messageData.setRequestContent(messageContent);
	    } else {
	        messageData = MessageData.builder().hash(requestHash).requestContent(messageContent).build();
	    }
		this.messageDatas.put(requestHash, messageData);	
	}
	
	public void addResponseToCache(String requestHash, Message responseMessage) {
        MessageData messageData;
        if (messageDatas.containsKey(requestHash)) {
            messageData = messageDatas.get(requestHash);
            messageData.setResponseContent(mcp.getMessageContent(responseMessage));
            messageData.setMockResponse(responseMessage);
        } else {
            messageData = MessageData.builder().hash(requestHash).responseContent(mcp.getMessageContent(responseMessage)).mockResponse(responseMessage).build();
        }
        this.messageDatas.put(requestHash, messageData);    
    }
	
	public Message getCachedResponse(String requestHash) {
		return this.messageDatas.get(requestHash).getMockResponse();
	}
	
	public Map<String, MessageData> getMessageDatas() {
	    return this.messageDatas;
	}
	
	public void appendToMessageDatas(Map<String, MessageData> messageDatas) {
		this.messageDatas.putAll(messageDatas);
	}
	
	public void removeCachedInformation(String requestHash) {
	    this.messageDatas.remove(requestHash);
	}
	
}
