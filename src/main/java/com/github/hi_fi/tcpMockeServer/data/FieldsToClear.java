package com.github.hi_fi.tcpMockeServer.data;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class FieldsToClear {
	
	private Map<String, String[]> fieldsToClear = new HashMap<String, String[]>();
	
	public void setFields(String mockName, String fields) {
		String[] fieldsArray = fields.isEmpty() ? new String[0] : fields.split(",");
		fieldsToClear.put(mockName, fieldsArray);
	}
	
	public String[] getFieldsToClear(String mockName) {
		return fieldsToClear.get(mockName);
	}

}
