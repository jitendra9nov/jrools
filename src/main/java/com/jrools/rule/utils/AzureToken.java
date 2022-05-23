package com.jrools.rule.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class AzureToken {

	public static void main(String[] args) {
		GetFileSAS();

	}
	
	public static void GetFileSAS(){
		

	    String accountName = "ocrpocst";
	    String key = "8vuqleensqy0ySL9aIcpzdMmcbSWPO7PgHqLkVyQiMHRttFpQ/UVyOmiWEQm4NBcxybcr7+Q9RlC9LvejdMByA==";
	    String resourceUrl = "https://ocrpocst.blob.core.windows.net/brdemo-invoice/InProgress/17384%20Ogier%20llp.pdf";

	    String start = "startTime";
	    String expiry = "expiry";
	    String azureApiVersion = "2020-08-04";

	    String stringToSign = accountName + "\n" +
	                "r\n" +
	                "f\n" +
	                "o\n" +
	                start + "\n" +
	                expiry + "\n" +
	                "\n" +
	                "https\n" +
	                azureApiVersion+"\n";

	    String signature = getHMAC256(key, stringToSign);

	    try{

	        String sasToken = "sv=" + azureApiVersion +
	            "&ss=f" +
	            "&srt=o" +
	            "&sp=r" +
	            "&se=" +URLEncoder.encode(expiry, "UTF-8") +
	            "&st=" + URLEncoder.encode(start, "UTF-8") +
	            "&spr=https" +
	            "&sig=" + URLEncoder.encode(signature, "UTF-8");

	    System.out.println(resourceUrl+"?"+sasToken);

	    } catch (UnsupportedEncodingException e) {
	        e.printStackTrace();
	    }
	}

	private static String getHMAC256(String accountKey, String signStr) {
	    String signature = null;
	    try {
	        SecretKeySpec secretKey = new SecretKeySpec(Base64.getDecoder().decode(accountKey), "HmacSHA256");
	        Mac sha256HMAC = Mac.getInstance("HmacSHA256");
	        sha256HMAC.init(secretKey);
	        signature = Base64.getEncoder().encodeToString(sha256HMAC.doFinal(signStr.getBytes("UTF-8")));
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return signature;
	}

}
