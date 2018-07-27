package com.github.hi_fi.tcpMockeServer.parsers;

import junit.framework.Assert;

import org.junit.Test;

import static org.junit.Assert.*;

public class SoapParserTest {
    
    @Test
    public void checkFieldRemoval() {
        SoapParser sp = new SoapParser();
        String testString = "<wsu:Timestamp wsu:Id='timestamp'>4431431</wsu:Timestamp>";
        Assert.assertEquals(sp.replaceField(testString, "Timestamp"), "<wsu:Timestamp wsu:Id='timestamp'>XXX</wsu:Timestamp>");
    }
    
    @Test
    public void checkFieldRemovalFromEmptyField() {
        SoapParser sp = new SoapParser();
        String testString = "<wsu:Timestamp wsu:Id='timestamp'></wsu:Timestamp>";
        Assert.assertEquals(sp.replaceField(testString, "Timestamp"), "<wsu:Timestamp wsu:Id='timestamp'>XXX</wsu:Timestamp>");
    }
    
}