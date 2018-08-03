package com.github.hi_fi.tcpMockeServer.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.hi_fi.tcpMockeServer.data.RequestCache;
import com.github.hi_fi.tcpMockeServer.model.MessageData;

@Service
@Slf4j
public class FileService {
	
	@Autowired
	RequestCache cache;
	
	public File exportCacheToFile() {
		File outputFile = new File("cacheExport.dat");
		try {
		    ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            objectMapper.writeValue(outputFile, cache.getMessageDatas());
		} catch (IOException ioe) {
			log.error("Generation of export YAML failed", ioe);
		}
		return outputFile;
	}
	
	public void importCacheFromFile(MultipartFile file) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
            TypeReference<Map<String, MessageData>> typeRef
                = new TypeReference<Map<String, MessageData>>() {};
            Map<String, MessageData> importedData = objectMapper.readValue(file.getInputStream(), typeRef);
            for (Map.Entry<String, MessageData> dataItem : importedData.entrySet()) {
            	if (dataItem.getValue().getHash().isEmpty()) {
            		dataItem.getValue().setHash(DigestUtils.sha256Hex(dataItem.getValue().getRequestContent()));
				}
				cache.appendToMessageDatas(dataItem.getValue());
			}
		} catch (IOException e) {
			log.error("Reading of import YAML failed", e);
		}
	}
}
