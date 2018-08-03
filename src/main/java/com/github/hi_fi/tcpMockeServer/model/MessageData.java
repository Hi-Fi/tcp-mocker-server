package com.github.hi_fi.tcpMockeServer.model;

import java.io.Serializable;

import org.springframework.messaging.Message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageData implements Serializable {
    
	private static final long serialVersionUID = 7808817998132101776L;
	
	private String hash;
    private String requestContent;
    private String responseContent;
    private byte[] mockResponsePayload;
}
