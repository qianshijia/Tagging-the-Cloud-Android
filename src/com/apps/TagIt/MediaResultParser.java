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

public class MediaResultParser {

	Boolean currentElement = false;
	String currentValue = null;
	public void getRecords(String xml) {
		Document doc = null;
		try {
			Log.i("STRING1111111==",xml);
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
					
					NodeList firstnameList = fstElmnt.getElementsByTagName("mediaid");
					Element nameElement = (Element) firstnameList.item(0);
					firstnameList = nameElement.getChildNodes();
					Log.i("mediaid==",((Node) firstnameList.item(0)).getNodeValue());
				Question.mediaId = ((Node) firstnameList.item(0))
							.getNodeValue();
				} catch (Exception e) {
					System.out.println("XML Pasing Excpetion main = " + e.getMessage());
					try {
						
						NodeList firstnameList = fstElmnt.getElementsByTagName("Error");
						Element nameElement = (Element) firstnameList.item(0);
						firstnameList = nameElement.getChildNodes();
						Log.i("Error==",((Node) firstnameList.item(0)).getNodeValue());
					DataAdapters.ErrorMsg = ((Node) firstnameList.item(0))
								.getNodeValue();
					} catch (Exception ei) {
						System.out.println("XML Pasing ExcpetionSub = " + ei.getMessage());
						
					}
					Question.mediaId = "-1";
				}
		} catch (Exception e) {
			System.out.println("XML Pasing Excpetion = " + e.getMessage());
		}

	}
}
