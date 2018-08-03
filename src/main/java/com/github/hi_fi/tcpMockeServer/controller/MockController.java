package com.github.hi_fi.tcpMockeServer.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.integration.http.management.IntegrationGraphController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.github.hi_fi.tcpMockeServer.MockInit;
import com.github.hi_fi.tcpMockeServer.data.RequestCache;
import com.github.hi_fi.tcpMockeServer.model.Mock;
import com.github.hi_fi.tcpMockeServer.service.FileService;

@Controller
public class MockController {
    
    @Autowired
    private MockInit mi;
    
    @Autowired
    private RequestCache cache;
    
    @Autowired
    private IntegrationGraphController igc;
    
    @Autowired
    FileService fileService;

    @RequestMapping("/")
    public String listMocks(Map<String, Object> model) {
        model.put("mockList", mi.getServices());
        return "listMocks";
    }
    
    @RequestMapping("/cache")
    public String listCache(Map<String, Object> model) {
        model.put("messageDatas", cache.getMessageDatas());
        return "listCachedResponses";
    }
    
    @GetMapping("/add")
    public String mockForm(Model model) {
        model.addAttribute("mock", new Mock());
        return "addMock";
    }

    @PostMapping("/add")
    public String mockSubmit(@ModelAttribute Mock mock) {
        mi.addMock(mock);
        mi.startMock(mock);
        mi.startMockBackendConnection(mock);
        igc.refreshGraph();
        return "redirect:/";
    }
    
    @DeleteMapping("/cache/{messageDataId}")
    public String removeItem(@PathVariable String messageDataId) {
        cache.removeCachedInformation(messageDataId);
        return "redirect:/cache";
    }
    
    @GetMapping("/cache/download")
    public ResponseEntity<InputStreamResource> getCachedItems() throws FileNotFoundException {
    	File cacheFile = fileService.exportCacheToFile();
    	HttpHeaders respHeaders = new HttpHeaders();
    	respHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
    	respHeaders.set(HttpHeaders.CONTENT_DISPOSITION,
                       "attachment; filename=cacheExport.dat");
    	respHeaders.setContentLength(cacheFile.length());

        InputStreamResource isr = new InputStreamResource(new FileInputStream(cacheFile));
        return new ResponseEntity<InputStreamResource>(isr, respHeaders, HttpStatus.OK);
    }
    
    @PostMapping("/cache/upload")
    public String uploadCacheFile(@RequestParam("file") MultipartFile file) {
    	fileService.importCacheFromFile(file);
        return "redirect:/cache";
    }
}
