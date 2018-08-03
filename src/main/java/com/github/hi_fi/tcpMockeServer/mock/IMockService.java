package com.github.hi_fi.tcpMockeServer.mock;

import org.springframework.messaging.Message;

public interface IMockService {
	
	public Message<byte[]> getResponse(Message<byte[]> message);

}
