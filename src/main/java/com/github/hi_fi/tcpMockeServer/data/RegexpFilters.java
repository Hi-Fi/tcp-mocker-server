package com.github.hi_fi.tcpMockeServer.data;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class RegexpFilters {

	private Map<String, String[]> regexpFilters = new HashMap<String, String[]>();
	
	public void setRegexpFilters(String mockName, String filters) {
		regexpFilters.put(mockName, filters.split(","));
	}
	
	public String[] getRegexpFilters(String mockName) {
		return regexpFilters.get(mockName);
	}
}
