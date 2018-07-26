package com.github.hi_fi.tcpMockeServer.parsers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import com.github.hi_fi.tcpMockeServer.data.FieldsToClear;
import com.github.hi_fi.tcpMockeServer.data.RegexpFilters;
import com.github.hi_fi.tcpMockeServer.utils.MessageContentParser;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SoapParser implements IPayloadParser {
	
	@Autowired
	FieldsToClear ftc;
	
	@Autowired
	RegexpFilters rf;
	
	@Autowired
	MessageContentParser mcp;

	@Override
	public String getHashablePayload(Message message) {
		String messageContent = mcp.getMessageContent(message);
		//SOAP message, making some modifications to get valid hash.
		//Clearing out the elements that are mentioned on the configuration
		log.debug(messageContent);
		for (String fieldName : ftc.getFieldsToClear(message.getHeaders().get("mockName").toString())) {
			messageContent = messageContent.replaceAll("<([a-z0-9]+?:)"+fieldName+"(\\s.*?)?>.*?<?[a-z0-9]+?:"+fieldName+">", "<$1"+fieldName+" $2>XXX</$1"+fieldName+">");
		}
		for (String regexReplace : rf.getRegexpFilters(message.getHeaders().get("mockName").toString())) {
			String[] filters = regexReplace.split("->");
            		messageContent = messageContent.replaceAll(filters[0], filters[1]);
		}
		messageContent = messageContent.substring(messageContent.indexOf("<"));
		return messageContent;
	}

}
