package com.apps.TagIt;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

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
				Log.i("NODE===",node.getNodeName());
				Element fstElmnt = (Element) node;
				try {
					
					NodeList firstnameList = fstElmnt.getElementsByTagName("validLogin");
					Element nameElement = (Element) firstnameList.item(0);
					firstnameList = nameElement.getChildNodes();
					Log.i("validlogin==",((Node) firstnameList.item(0)).getNodeValue());
					lg.validLogin = ((Node) firstnameList.item(0))
							.getNodeValue();
				} catch (Exception e) {
					System.out.println("XML Pasing Excpetion = " + e.getMessage());
					lg.validLogin = "0";
				}
				 node = node1.item(1);
				Log.i("NODE===",node.getNodeName());
				 fstElmnt = (Element) node;
				try {
					NodeList firstnameList = fstElmnt.getElementsByTagName("retMessage");
					Element nameElement = (Element) firstnameList.item(0);
					firstnameList = nameElement.getChildNodes();
					Log.i("retMessage==",((Node) firstnameList.item(0)).getNodeValue());
					lg.retMessage = ((Node) firstnameList.item(0))
							.getNodeValue();
				} catch (Exception e) {
					System.out.println("XML Pasing Excpetion = " + e.getMessage());
					lg.retMessage = "0";
				}
				try {
				 node = node1.item(2);
				Log.i("NODE===",node.getNodeName());
				 fstElmnt = (Element) node;
					NodeList firstnameList = fstElmnt.getElementsByTagName("token");
					Element nameElement = (Element) firstnameList.item(0);
					firstnameList = nameElement.getChildNodes();
					Log.i("token==",((Node) firstnameList.item(0)).getNodeValue());
					lg.token = ((Node) firstnameList.item(0)).getNodeValue();
				} catch (Exception e) {
					System.out.println("XML Pasing Excpetion sub = " + e.getMessage());
					lg.token = "0";
				}
				DataAdapters.addloginInfo(lg.validLogin, lg.retMessage,lg.token);
		} catch (Exception e) {
			System.out.println("XML Pasing Excpetion = " + e.getMessage());
		}

	}
}
