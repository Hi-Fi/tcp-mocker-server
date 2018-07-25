package com.github.hi_fi.tcpMockeServer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.ip.tcp.serializer.ByteArrayRawSerializer;
import org.springframework.integration.ip.tcp.serializer.SoftEndOfStreamException;

@Slf4j
public class TcpMockServerSerializer extends ByteArrayRawSerializer {

	private byte[] messageStartingBytes = new byte[0];

	public TcpMockServerSerializer() {
		super();
	}
	
	public TcpMockServerSerializer(int maxMessageSize) {
		super();
		this.setMaxMessageSize(maxMessageSize);
	}

	public TcpMockServerSerializer(int maxMessageSize, String messageStarter) {
		super();
		this.setMaxMessageSize(maxMessageSize);
		if (messageStarter.trim().length() > 0) {
			String[] starters = messageStarter.split(",");
			messageStartingBytes = new byte[starters.length];
			log.debug("Message starter: " + messageStarter + ". Number of starting bytes: " + starters.length);
			int n = 0;
			for (String starter : starters) {
				messageStartingBytes[n++] = Byte.parseByte(starter);
			}
		}
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
				//Adding a message start in case multiple messages were in same stream.
				if (n == 0 && this.messageStartingBytes.length > 0 && ((byte) bite) != this.messageStartingBytes[0]) {
					for (byte startingByte : this.messageStartingBytes) {
						buffer[n++] = startingByte;
					}
				}
				buffer[n++] = (byte) bite;
				//Stops reading if stream doesn't have anything else to read
				bite = inputStream.available() - 1;
				if (this.messageStartingBytes.length > 0 && n > this.messageStartingBytes.length+1 && buffer[n-1] == this.messageStartingBytes[this.messageStartingBytes.length-1]) {
					int i = 0;
					boolean messageEndedAlready = true;
					for (byte startingByte: this.messageStartingBytes) {
						messageEndedAlready = messageEndedAlready && startingByte == buffer[n-this.messageStartingBytes.length+(i++)];
					}
					if (messageEndedAlready) {
						log.debug("Terminating read, new message started already");
						n = n - this.messageStartingBytes.length;
						bite = -1;
					}
				}
				if (bite < 0) {
					log.debug("Stream ended, current buffer size: "+n);
				}
			}
			log.debug(Arrays.toString(copyToSizedArray(buffer, n)));
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
