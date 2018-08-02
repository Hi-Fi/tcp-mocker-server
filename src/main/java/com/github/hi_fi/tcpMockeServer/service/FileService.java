package com.github.hi_fi.tcpMockeServer.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.github.hi_fi.tcpMockeServer.data.RequestCache;
import com.github.hi_fi.tcpMockeServer.model.MessageData;

@Service
public class FileService {
	
	@Autowired
	RequestCache cache;
	
	public File exportCacheToFile() {
		File outputFile = new File("cacheExport.dat");
		try {
			FileOutputStream f = new FileOutputStream(outputFile);
			ObjectOutputStream o = new ObjectOutputStream(f);
			o.writeObject(cache.getMessageDatas());
			o.close();
			f.close();
		} catch (FileNotFoundException fnfe) {
			// TODO Auto-generated catch block
			fnfe.printStackTrace();
		} catch (IOException ioe) {
			// TODO Auto-generated catch block
			ioe.printStackTrace();
		}
		return outputFile;
	}
	
	public void importCacheFromFile(MultipartFile file) {
		try {
			InputStream is = file.getInputStream();
			ObjectInputStream oi = new ObjectInputStream(is);
			cache.appendToMessageDatas((Map<String, MessageData>) oi.readObject());
			oi.close();
			is.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
