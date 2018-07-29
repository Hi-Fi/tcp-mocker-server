package com.github.hi_fi.tcpMockeServer.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

public class Gzip {
	
	public static String decompressGzip(byte[] compressedData) throws IOException {
		  byte[] buffer=new byte[compressedData.length];
		  ByteArrayOutputStream out=new ByteArrayOutputStream();
		  GZIPInputStream in=new GZIPInputStream(new ByteArrayInputStream(compressedData));
		  for (int bytesRead=0; bytesRead != -1; bytesRead=in.read(buffer)) {
		    out.write(buffer,0,bytesRead);
		  }
		  return new String(out.toByteArray(),"UTF-8");
		}
	
	public static boolean isCompressed(final byte[] compressed) {
	    return (compressed[0] == (byte) (GZIPInputStream.GZIP_MAGIC)) && (compressed[1] == (byte) (GZIPInputStream.GZIP_MAGIC >> 8));
	  }

}
