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
		Message<byte[]> sendToBacked(Message<byte[]> message);
	}
	
	@Transformer(inputChannel="mockTargetTransformer", outputChannel = "mockTargetRouter")
	public Message<byte[]> fixHttpMessageHost(Message<byte[]> message) {
		GenericMessage<byte[]> transformedMessage = (GenericMessage<byte[]>) message;
		if (hhh.isHttpRequest(message)) {
			transformedMessage = (GenericMessage<byte[]>) hhh.replaceHostWithRealEndpoint(message);
		}
		return transformedMessage;
	}
	
	@Router(inputChannel = "mockTargetRouter")
	public String route(Message<byte[]> message) {
		return message.getHeaders().get("mockName") + "TargetOutgoingChannel";
	}

}
