package com.github.hi_fi.tcpMockeServer.backend;

import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.Router;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

@Service
public class Route {
	
	@MessagingGateway(defaultRequestChannel = "mockTargetRouter")
	public interface Gateway {

		Message sendToBacked(Message message);

	}
	
	@Router(inputChannel = "mockTargetRouter")
	public String route(Message message) {
		return message.getHeaders().get("mockName") + "TargetOutgoingChannel";
	}

}
