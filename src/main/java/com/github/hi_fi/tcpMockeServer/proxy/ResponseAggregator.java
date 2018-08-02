package com.github.hi_fi.tcpMockeServer.proxy;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.Aggregator;
import org.springframework.integration.annotation.CorrelationStrategy;
import org.springframework.integration.annotation.Header;
import org.springframework.integration.annotation.ReleaseStrategy;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ResponseAggregator {

	@Bean
	public DirectChannel fromCustomReceiver() {
		return new DirectChannel();
	}
	
	@Aggregator(inputChannel="fromCustomerReceiver")
	public Message aggregateMessages(List<Message> messageList) {
		return null;
	}
	
	@CorrelationStrategy
	public String correlateBy(@Header("ip_connectionId") String ip_connectionId){
	    return ip_connectionId;
	}
	
	@ReleaseStrategy
	public boolean isReadytoRelease(List<Message<?>> messages) {
		log.info(Arrays.toString(messages.toArray()));
	    return messages.size() == 2;
	}
}
