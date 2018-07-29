package com.github.hi_fi.tcpMockeServer.proxy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.Router;
import org.springframework.integration.annotation.Transformer;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Service;

import com.github.hi_fi.tcpMockeServer.utils.HttpHeaderHandler;

@Service
public class Route {
	
	@Autowired
	HttpHeaderHandler hhh;
	
	@MessagingGateway(defaultRequestChannel = "mockTargetTransformer")
	public interface Gateway {
		Message sendToBacked(Message message);
	}
	
	@Transformer(inputChannel="mockTargetTransformer", outputChannel = "mockTargetRouter")
	public Message fixHttpMessageHost(Message message) {
		GenericMessage transformedMessage = (GenericMessage) message;
		if (hhh.isHttpRequest(message)) {
			transformedMessage = (GenericMessage) hhh.replaceHostWithRealEndpoint(message);
		}
		return transformedMessage;
	}
	
	@Router(inputChannel = "mockTargetRouter")
	public String route(Message message) {
		return message.getHeaders().get("mockName") + "TargetOutgoingChannel";
	}

}
