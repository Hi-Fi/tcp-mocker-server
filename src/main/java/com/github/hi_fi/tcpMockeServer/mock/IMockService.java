package com.github.hi_fi.tcpMockeServer.mock;

import org.springframework.messaging.Message;

public interface IMockService {
	
	public Message getResponse(Message message);

}
