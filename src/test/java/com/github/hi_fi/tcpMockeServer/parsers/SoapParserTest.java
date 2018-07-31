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
    
        @Test
    public void checkMultipleFieldRemovals() {
        SoapParser sp = new SoapParser();
        String testString = "<SOAP-ENV:Header>\n" +
                "\t\t<Security SOAP-ENV:mustUnderstand=\"1\" xmlns=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\" xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\"><UsernameToken wsu:Id=\"UsernameToken-A0F177421F32FF7D80153303395088732\"><Username>notValidOne</Username><Password Type=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText\">notValidOne</Password><Nonce EncodingType=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-soap-message-security-1.0#Base64Binary\">someInvalidHash</Nonce><wsu:Created>2018-07-31T10:45:50.887Z</wsu:Created></UsernameToken>\n" +
                "\t\t\t<UsernameToken ns0:Id=\"-1703106754\" xmlns:ns0=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\">\n" +
                "\t\t\t\t<Username>notValidOne</Username>\n" +
                "\t\t\t\t<Password Type=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText\">notValidOne</Password>\n" +
                "\t\t\t</UsernameToken>\n" +
                "\t\t</Security>\n" +
                "\t</SOAP-ENV:Header>";

        String replacedString = sp.replaceField(testString, "Username");
        replacedString = sp.replaceField(replacedString, "Password");
        Assert.assertEquals(testString.replaceAll("notValidOne", "XXX"), replacedString);
    }
    
}