package com.github.hi_fi.tcpMockeServer.proxy;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.Aggregator;
import org.springframework.integration.annotation.CorrelationStrategy;
import org.springframework.integration.annotation.Header;
import org.springframework.integration.annotation.ReleaseStrategy;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EndpointResponseAggregator {
    
    @Bean
    public MessageChannel fromCustomReceiver() {
        return new DirectChannel();
    }
    
    @Aggregator(inputChannel="fromCustomReceiver", outputChannel="toReplyTransformer") 
    public List<Message> logMessages(List<Message> messages){
        log.info("Messages in list in aggregator: {}", messages.size());
        return messages;
    }
    
    @CorrelationStrategy 
    public String correlateBy(){
        return "1";
    }
    
    @ReleaseStrategy
    public boolean isReadytoRelease(List<Message> messages) {
//        log.info("Header: {}", (messages.get(messages.size()-1).getHeaders()));
//        log.info("Size of last message is: {}", ((byte[])messages.get(messages.size()-1).getPayload()).length);
        log.info("Messages in list in release Strategy: {}", messages.size());
        return messages.size() == 2;
    }
    
    @Transformer(inputChannel="toReplyTransformer")
    public Message replyWithMessage(List<Message> messages) {
        Message returnMessage = messages.stream().filter(m -> m.getHeaders().getReplyChannel() != null).findFirst().orElse(null);
        log.info("Returning message with headers: {}", returnMessage.getHeaders());
        Message responseMessage = new GenericMessage(messages.get(0).getPayload(), messages.get(0).getHeaders());
        return responseMessage;
    }
}
