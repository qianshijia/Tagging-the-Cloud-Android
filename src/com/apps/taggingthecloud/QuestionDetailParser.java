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

public class QuestionDetailParser {

	Boolean currentElement = false;
	String currentValue = null;
	public void getRecords(String xml) {
		DataAdapters.question.clear();
		Document doc = null;
		try {
			Log.i("STRING==",xml);
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputStream is = new ByteArrayInputStream(xml.getBytes("UTF-8"));
			doc = db.parse(is);
			doc.getDocumentElement().normalize();
	} catch (Exception e) {
		System.out.println("XML Pasing Excpetion = " + e.getMessage());
	}
			NodeList nodeList = doc.getElementsByTagName("question");
			
				Node node = nodeList.item(0);
				Log.i("NODE===",node.getNodeName());
					Log.i("question==eeeeee",node.getFirstChild().getNodeValue());
					String questionInfo=node.getFirstChild().getNodeValue();
					String splitter2 = "\\|";
								
						String newArr[];
						
						
						newArr = questionInfo.split(splitter2);
						
						Question.answreId=newArr[1];
						Question.questionId=newArr[2];
						DataAdapters.addQuestion( newArr[1].toString(), newArr[2].toString(), newArr[3].toString(), newArr[4].toString(), newArr[5].toString(), newArr[6].toString(), newArr[7].toString(),newArr[8],newArr[9].toString());
						
				
					
					//lg.questionairelist = ((Node) firstnameList.item(0)).getNodeValue();
				
		
		
		//}
	}
}
