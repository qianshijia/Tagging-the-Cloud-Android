package com.apps.taggingthecloud;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.util.Log;

public class LoginParser {

	Boolean currentElement = false;
	String currentValue = null;
	public void getRecords(String xml) {
		Document doc = null;
		try {
			Log.i("STRING==",xml);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputStream is = new ByteArrayInputStream(xml.getBytes("UTF-8"));
			doc = db.parse(is);
			doc.getDocumentElement().normalize();
			NodeList nodeList = doc.getElementsByTagName("ret");
			NodeList node1 = nodeList.item(0).getChildNodes();
				Log.i("LENGTH",String.valueOf(node1.getLength()));
				LoginCls lg = new LoginCls();
				Node node = node1.item(0);
				Log.i("NODE===",node1.item(0).getNodeName());
				try {
					lg.validLogin = node.getFirstChild().getNodeValue();
				} catch (Exception e) {
					e.printStackTrace();
					lg.validLogin = "0";
				}
				 node = node1.item(1);
				Log.i("NODE===",node.getNodeName());
				try {
					lg.retMessage = node.getFirstChild().getNodeValue();
				} catch (Exception e) {
					e.printStackTrace();
					lg.retMessage = "0";
				}
				try {
				 node = node1.item(2);
				Log.i("NODE===",node.getNodeName());
				lg.token = node.getFirstChild().getNodeValue();
				} catch (Exception e) {
					e.printStackTrace();
					lg.token = "0";
				}
				DataAdapters.addloginInfo(lg.validLogin, lg.retMessage,lg.token);
		} catch (Exception e) {
			System.out.println("XML Pasing Excpetion = " + e.getMessage());
		}

	}
}
