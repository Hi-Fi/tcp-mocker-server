package com.github.hi_fi.tcpMockeServer;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.integration.ip.tcp.serializer.ByteArrayRawSerializer;
import org.springframework.integration.ip.tcp.serializer.SoftEndOfStreamException;

public class TcpMockServerSerializer extends ByteArrayRawSerializer {

	public TcpMockServerSerializer() {
		super();
	}
	
	public TcpMockServerSerializer(int maxMessageSize) {
		super();
		this.setMaxMessageSize(maxMessageSize);
	}
	
	@Override
	protected byte[] doDeserialize(InputStream inputStream, byte[] buffer) throws IOException {
		int n = 0;
		int bite = 0;
		if (logger.isDebugEnabled()) {
			logger.debug("Available to read:" + inputStream.available());
		}
		try {
			while (bite >= 0) {
				bite = inputStream.read();
				if (bite < 0) {
					if (n == 0) {
						throw new SoftEndOfStreamException("Stream closed between payloads");
					}
					break;
				}
				if (n >= this.maxMessageSize) {
					throw new IOException("Socket was not closed before max message length: "
							+ this.maxMessageSize);
				}
				buffer[n++] = (byte) bite;
				//Stops reading if stream doesn't have anything else to read
				bite = inputStream.available() - 1; 
			}
			return copyToSizedArray(buffer, n);
		}
		catch (SoftEndOfStreamException e) {
			throw e;
		}
		catch (IOException e) {
			publishEvent(e, buffer, n);
			throw e;
		}
		catch (RuntimeException e) {
			publishEvent(e, buffer, n);
			throw e;
		}
	}
}
