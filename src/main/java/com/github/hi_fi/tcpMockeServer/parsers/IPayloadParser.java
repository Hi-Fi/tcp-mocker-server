package com.github.hi_fi.tcpMockeServer.parsers;

import org.springframework.messaging.Message;

public interface IPayloadParser {

	String getHashablePayload(Message message);
}
