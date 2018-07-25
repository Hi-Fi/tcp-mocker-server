package com.github.hi_fi.tcpMockeServer.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.github.hi_fi.tcpMockeServer.MockInit;
import com.github.hi_fi.tcpMockeServer.data.RequestCache;
import com.github.hi_fi.tcpMockeServer.model.Mock;

@Controller
public class MockController {
    
    @Autowired
    private MockInit mi;
    
    @Autowired
    private RequestCache cache;

    @RequestMapping("/")
    public String listMocks(Map<String, Object> model) {
        model.put("mockList", mi.getServices());
        return "listMocks";
    }
    
    @RequestMapping("/cache")
    public String listCache(Map<String, Object> model) {
        model.put("cachedResponses", cache.getCachedMessages());
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
        return "redirect:/";
    }
}