package com.github.hi_fi.tcpMockeServer.utils;

import java.io.IOException;

import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MessageContentParser {
    
    public String getMessageContent(Message message) {
        //HTTP message
        byte[] body = this.getBodyBytes((byte[]) message.getPayload());
        if (Gzip.isCompressed(body)) {
            try {
                return Gzip.decompressGzip(body);
            } catch (IOException e) {
                return new String((byte[]) message.getPayload());
            }
        } else {
            return new String((byte[]) message.getPayload());
        }
        
        
        
    }
    
    private byte[] getBodyBytes(byte[] payload) {
        //In HTTP calls body is separated from headers with bytes 13, 10, 13, 10. First occurance of there marks the start of the body
        
        int n=0;
        for (byte payloadByte : payload) {
            if (n>4) {
                if (payload[n-3] == 13 && payload[n-2] == 10 && payload[n-1] == 13 && payload[n] == 10) {
                    break;
                }
            }
            n++;
        }
        //if no header present, just presuming that whole message is content (header is missing for some reason)
        n = n < payload.length ? n : 0;
        byte[] assembledData = new byte[payload.length-n];
        System.arraycopy(payload, n+1, assembledData, 0, payload.length-n-1);
        return assembledData;
    }

}
