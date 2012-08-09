package com.apps.taggingthecloud;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.util.Log;

public class QuestionParser {

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
					
//					NodeList firstnameList = fstElmnt.getElementsByTagName("questionairelist");
//					Element nameElement = (Element) firstnameList.item(0);
//					firstnameList = nameElement.getChildNodes();
					Log.i("questionairelist==",node.getFirstChild().getNodeValue());
					String questionListInfo = node.getFirstChild().getNodeValue();
					String optionsArr[];
					String splitter1 = "\\^\\^";
					String splitter2 = "\\|";
					String splitter3 = "\\^";
					//05-15 21:44:43.198: INFO/optionsArr[1](250): Questionnaires^^Meal information|11~1234567^No Title|11~21~36~1234567^No Title|11~22~36~1234567^No Title|11~23~36~1234567^No Title|11~24~36~1234567^No Title|11~25~36~1234567
					String tempString;
					optionsArr = questionListInfo.split(splitter1);
					tempString = optionsArr[1];
					optionsArr = tempString.split(splitter3);


					//var parent = document.getElementById('listview');
					
					for(int i=0; i<optionsArr.length; i++)
					{
						String newArr[];
						
						String newString = optionsArr[i];
						newArr = newString.split(splitter2);
						DataAdapters.addQuestions(newArr[1].toString(), newArr[0].toString());
						
							
					}
					
					//lg.questionairelist = ((Node) firstnameList.item(0)).getNodeValue();
				} catch (Exception e) {
					System.out.println("XML Pasing Excpetion = " + e.getMessage());
					lg.validLogin = "0";
				}
			
		} catch (Exception e) {
			System.out.println("XML Pasing Excpetion = " + e.getMessage());
		}

	}
}
