package com.apps.taggingthecloud;


import java.util.ArrayList;
import java.util.HashMap;

import android.util.Log;
public class DataAdapters {
public static String messages[];
public static String ErrorMsg="";
private static  HashMap<String,String> list;
public static String repeatTag = "";
public static String repeatTagType = "";
public static ArrayList<HashMap<String,String>> login =new ArrayList<HashMap<String,String>>();
public static ArrayList<HashMap<String,String>> questions =new ArrayList<HashMap<String,String>>();
public static ArrayList<HashMap<String,String>> question =new ArrayList<HashMap<String,String>>();
public static String processName;

		public static void  addloginInfo(String validLogin, String retMessage,String token)
		{			
			
			if(validLogin.length()>0 )
			{
			list = new HashMap<String,String>();
			list.put("validLogin",validLogin);
			list.put("retMessage",retMessage);
			list.put("token",token);
			login.add(list);				
			}
		}
		
		public static ArrayList<HashMap<String,String>> getLoginResult()
		{						
					return 	login;		
		}
		public static void  addQuestions(String Qcode, String Question)
		{			
			
			if(Question.length()>0 )
			{
			list = new HashMap<String,String>();
			
			list.put("Qcode",Qcode);
			list.put("Question",Question);
			questions.add(list);				
			}
		}
		
		public static ArrayList<HashMap<String,String>> getQuestionsList()
		{						
					return 	questions;		
		}
		public static void  addQuestion(String answersetid, String questionId, String questionLabel,String questionType , String questionData,String mediaLength, String validationType,String validationMsg, String mediaData)
		{			
			
			
			list = new HashMap<String,String>();
			Log.i("answersetid",answersetid);
			Log.i("questionId ",questionId);
			Log.i("questionLabel ",questionLabel);
			Log.i("questionType",questionType);
			Log.i("questionData",questionData);
			Log.i("mediaLength",mediaLength);
			Log.i("validationType",validationType);
			Log.i("validationMsg",validationMsg);
			Log.i("mediaData", mediaData);
			
			
			list.put("answersetid",answersetid);
			list.put("questionId",questionId);
			list.put("questionLabel",questionLabel);
			list.put("questionType",questionType);
			list.put("questionData",questionData);
			list.put("mediaLength",mediaLength);
			list.put("validationType",validationType);
			list.put("validationMsg",validationMsg);
			list.put("mediaData", mediaData);
			
			question.add(list);				
			
		}
		
		public static ArrayList<HashMap<String,String>> getQuestionList()
		{						
					return 	question;		
		}
		
		public static void setProcessName(String name)
		{
			processName = name;
		}
		
		public static String getProcessName()
		{
			return processName;
		}
		
		public static void clearProcessName()
		{
			processName = "";
		}
}