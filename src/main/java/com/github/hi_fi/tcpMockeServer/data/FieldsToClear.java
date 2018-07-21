package com.github.hi_fi.tcpMockeServer.data;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class FieldsToClear {
	
	private Map<String, String[]> fieldsToClear = new HashMap<String, String[]>();
	
	public void setFields(String mockName, String fields) {
		fieldsToClear.put(mockName, fields.split(","));
	}
	
	public String[] getFieldsToClear(String mockName) {
		return fieldsToClear.get(mockName);
	}

}
