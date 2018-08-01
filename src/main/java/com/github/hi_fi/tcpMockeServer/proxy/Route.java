package com.github.hi_fi.tcpMockeServer.proxy;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.Router;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.integration.handler.BridgeHandler;
import org.springframework.integration.ip.tcp.connection.MessageConvertingTcpMessageMapper;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.integration.support.converter.MapMessageConverter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessageHeaders;
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
			transformedMessage = (GenericMessage) MessageBuilder.fromMessage(transformedMessage).setHeader("correlationId", UUID.randomUUID()).build();
		}
		return transformedMessage;
	}
	
	@Bean
	public PublishSubscribeChannel mockTargetRouter() {
	    return new PublishSubscribeChannel();
	}
	
	@Order(1)
	@Bean
	@ServiceActivator(inputChannel="mockTargetRouter")
	public MessageHandler bridge() {
	    BridgeHandler handler = new BridgeHandler();
	    handler.setOutputChannelName("fromCustomReceiver");
	    return handler;
	}
	
	@Order(2)
	@Router(inputChannel = "mockTargetRouter")
	public String route(Message message) {
		return message.getHeaders().get("mockName") + "TargetOutgoingChannel";
	}
	
	@Bean
	public MessageConvertingTcpMessageMapper mapper() {
	    MapMessageConverter mapMessageConverter = new MapMessageConverter();
	    mapMessageConverter.setHeaderNames("correlationId", "mockName");
	    return new MessageConvertingTcpMessageMapper(mapMessageConverter);
	}
}
