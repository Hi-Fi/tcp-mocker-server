package com.github.hi_fi.tcpMockeServer.data;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class RegexpFilters {

	private Map<String, String[]> regexpFilters = new HashMap<String, String[]>();
	
	public void setRegexpFilters(String mockName, String filters) {
		String[] filterArray = filters.isEmpty() ? new String[0] : filters.split(",");
		regexpFilters.put(mockName, filterArray);
	}
	
	public String[] getRegexpFilters(String mockName) {
		return regexpFilters.get(mockName);
	}
}
