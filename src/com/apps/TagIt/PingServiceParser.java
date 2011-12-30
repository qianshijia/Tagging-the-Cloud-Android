package com.apps.TagIt;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import android.util.Log;

public class PingServiceParser {

	Boolean currentElement = false;
	String currentValue = null;
	public void getRecords(String xml) {
		Document doc = null;
		try {
			Log.i("STRING==",xml);
			DataAdapters.question.clear();
			DataAdapters.questions.clear();
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputStream is = new ByteArrayInputStream(xml.getBytes("UTF-8"));
			doc = db.parse(is);
			doc.getDocumentElement().normalize();
			NodeList nodeList = doc.getElementsByTagName("ret");
			NamedNodeMap nd=nodeList.item(0).getAttributes();
			PingService.serverID= Integer.valueOf(nd.item(0).getNodeValue());
			NodeList node1 = nodeList.item(0).getChildNodes();
			Log.i("LENGTH",String.valueOf(node1.getLength()));
			Log.i("Content",doc.toString());
			try{
				NodeList nodeListM = doc.getElementsByTagName("messages");
				//NodeList nodeListMC = nodeListM.item(0).getChildNodes();
				Node nodeM = nodeListM.item(0);
				Element fstElmntM = (Element) nodeM;
				try
				{
					NodeList nodeListC = fstElmntM.getElementsByTagName("msg");
					DataAdapters.messages=new String[nodeListC.getLength()];
					for (int i = 0; i < nodeListC.getLength(); i++) {
						Element nameElement = (Element) nodeListC.item(i);
						NodeList firstnameList = nameElement.getChildNodes();
						DataAdapters.messages[i]=((Node) firstnameList.item(0)).getNodeValue();
						Log.i("Message==",DataAdapters.messages[i]);
						//DataAdapters.messages[i]=questionListInfo
					}
				}
				catch (Exception e){
					e.printStackTrace();
				}

			}catch (Exception e) {
				e.printStackTrace();
				System.out.println("XML Pasing Excpetion = " + e.getMessage());
			}

			try{

				NodeList firstnameList = doc.getElementsByTagName("question");
				Element nameElement = (Element) firstnameList.item(0);
				firstnameList = nameElement.getChildNodes();
				String questionInfo=((Node) firstnameList.item(0)).getNodeValue();
				String splitter2 = "\\|";
				String newArr[];
				newArr = questionInfo.split(splitter2);
				Log.i("answersetid",newArr[1]);
				Log.i("questionId",newArr[2]);
				Log.i("questionLabel  ",newArr[3]);
				Log.i("questionType ",newArr[4]);
				Log.i("questionData ",newArr[5]);
				Log.i("mediaLength 	",newArr[6]);
				Log.i("validationType ",newArr[7]);
				Log.i("validationMsg ",newArr[8]);
				Log.i("mediaData", newArr[9]);
				Question.answreId=newArr[1];
				Question.questionId=newArr[2];
				DataAdapters.addQuestion( newArr[1].toString(), newArr[2].toString(), newArr[3].toString(), newArr[4].toString(), newArr[5].toString(), newArr[6].toString(), newArr[7].toString(),newArr[8],newArr[9].toString());
			} catch (Exception e) {
				//e.printStackTrace();
				//System.out.println("XML Pasing Excpetion = " + e.getMessage());
			}

			try {
				if(xml.contains("questionairelist")){
					String questionListInfo=xml.substring(xml.indexOf("<questionairelist>")+18, xml.indexOf("</questionairelist>"));
					questionListInfo=questionListInfo.trim().substring(1,questionListInfo.trim().length()-3);
					Log.i("DATA",questionListInfo);
					
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
						Log.i("data1",newArr[0].toString());
						Log.i("data2",newArr[1].toString());
						DataAdapters.addQuestions(newArr[1].toString(), newArr[0].toString());
						
							
					}
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("XML Pasing Excpetion = " + e.getMessage());

			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("XML Pasing Excpetion = " + e.getMessage());
		}

	}
}
