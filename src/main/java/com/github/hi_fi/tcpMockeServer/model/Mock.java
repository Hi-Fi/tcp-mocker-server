package com.github.hi_fi.tcpMockeServer.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Mock {
	private String name;
	private Integer mockPort;
	private String targetHost;
	private Integer targetPort;
	//Comma separated list of fields that needs to be emptied (valid for SOAP messages only)
	private String fieldsToClear = "";
	private String regexpFilters = "";
	private String messageStarter = "";
	private String bytesToClear = "";
	private Type endpointType = Type.TCP;
	private String mockBeanName = null;
	
	@Override
	public String toString() {
		return String.format("Mock %s from port %d to %s:%d", this.name, this.mockPort, this.targetHost, this.targetPort);
	}

	public enum Type {
		TCP, HTTP
	}
}
