package com.github.hi_fi.tcpMockeServer.model;

import org.springframework.messaging.Message;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MessageData {
    
    private String hash;
    private String requestContent;
    private String responseContent;
    private Message mockResponse;
}
