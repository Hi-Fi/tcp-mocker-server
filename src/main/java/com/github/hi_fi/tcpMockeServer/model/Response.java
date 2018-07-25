package com.github.hi_fi.tcpMockeServer.model;

import lombok.Builder;
import lombok.Getter;

import lombok.Setter;

@Getter
@Builder
public class Response {
    
    private String messageContent;
    private String messageHeaders;

}
