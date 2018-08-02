package com.github.hi_fi.tcpMockeServer.model;

import java.io.Serializable;

import org.springframework.messaging.Message;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MessageData implements Serializable {
    
	private static final long serialVersionUID = 7808817998132101776L;
	
	private String hash;
    private String requestContent;
    private String responseContent;
    private Message mockResponse;
}
