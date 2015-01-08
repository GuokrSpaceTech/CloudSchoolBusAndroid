package com.Manga.Activity.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.w3c.dom.*;

import com.cytx.utility.FileTools;

import android.util.Log;

public class xmlStringDocParsor {
	
	//<LinkReturn>SUCCESS</LinkReturn><DVRType>HIKDVRDVS</DVRType><Width>1024</Width><Height>768</Height><Interval>0</Interval><AudioCodeID>-1</AudioCodeID><HZ>44100</HZ><SampleWidth>16</SampleWidth><AudioChns>2</AudioChns><BitRate>8000</BitRate>
	//<?xml version="1.0" encoding="GB2312" standalone="yes"?><ReturnInfo>CountError</ReturnInfo>
	/*
	 * <?xml version="1.0" encoding="GB2312" standalone="yes"?>
			<Root>
			<device>
			<svrid>10042184</svrid>
			<depart>3</depart>
			<svrname Status="1" NetE="1097194593">bt6670890_10042184</svrname>
			<svrip>1.190.2.79</svrip>
			<svruser>admin</svruser>
			<svrpwd>888888</svrpwd>
			<svrchns>1</svrchns>
			<svrtype>23</svrtype>
			<svrport>8101</svrport>
			<domain>009df3b.hanbang.org.cn</domain>
			<udpip/>
			<mediaport>6050</mediaport>
			<streamip/>
			<streamport>600</streamport>
			<fvport>8101</fvport>
			<dvcidstr>5a09df3b</dvcidstr>
			</device>
    */
	public Document getDomElement(String xml){
        Document doc = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            
        	DocumentBuilder db = dbf.newDocumentBuilder();                        
            InputSource is = new InputSource(new ByteArrayInputStream(xml.getBytes("GBK")));
            
           // FileTools.save2SDCard(FileTools.getSDcardPath() + "/", "devicelist", ".xml",xml);
            is.setEncoding("GBK");
            doc  = db.parse(is); 
            } catch (ParserConfigurationException e) {
                Log.e("Error: ", e.getMessage());
                return null;
            } catch (SAXException e) {
                Log.e("Error: ", e.getMessage());
                return null;
            } catch (IOException e) {
                Log.e("Error: ", e.getMessage());
                return null;
            }
                // return DOM
            return doc;
    }
	
	public String getValue(Element item, String str) {      
	    NodeList n = item.getElementsByTagName(str);        
	    return this.getElementValue(n.item(0));
	}
	
	public String getAttribute(Element item, String attr) {      
	    return this.getAttributeValue(item);
	}
	 
	public final String getElementValue( Node elem ) {
	         Node child;
	         if( elem != null){
	             if (elem.hasChildNodes()){
	                 for( child = elem.getFirstChild(); child != null; child = child.getNextSibling() ){
	                     if( child.getNodeType() == Node.TEXT_NODE  ){
	                         return child.getNodeValue();
	                     }
	                 }
	             }
	         }
	         return "";
	  }
	
	public final String getAttributeValue( Node elem ) {
        Node child;
        if( elem != null){
            if (elem.hasChildNodes()){
                for( child = elem.getFirstChild(); child != null; child = child.getNextSibling() ){
                    if( child.getNodeType() == Node.TEXT_NODE  ){
                        return child.getNodeValue();
                    }
                }
            }
        }
        return "";
 }
	
    private String Stream2String(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is), 16*1024); //强制缓存大小为16KB，一般Java类默认为8KB
        StringBuilder sb = new StringBuilder();
 
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {  //处理换行符
                sb.append(line + "\n");  
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
 
        return sb.toString();
    }
}
