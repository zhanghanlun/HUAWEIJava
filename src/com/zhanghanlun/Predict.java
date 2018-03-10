package com.zhanghanlun;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Predict {
	static int[] Physical=new int[3];
	static String[][] predictVM;
	static Date date=new Date();
	static double[][] TrainData=new double[1000][20];
	static int BasePoint=0;
	static SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");

	public static String[] predictVm(String[] ecsContent, String[] inputContent) {

		/** =========do your work here========== **/
		

		String[] results = new String[ecsContent.length];

		List<String> history = new ArrayList<String>();

		for (int i = 0; i < ecsContent.length; i++) {
			ecsContent[i]=ecsContent[i].replaceAll("\t+", " ");
			if (ecsContent[i].contains(" ")
					&& ecsContent[i].split(" ").length == 4) {
				String[] array = ecsContent[i].split(" ");
				String uuid = array[0];
				String flavorName = array[1];
				String createTime = array[2];
				int flavor=Integer.parseInt(flavorName.substring(6));
				int DayIndex=changeDate(i,createTime);
				if(flavor<19)
					TrainData[DayIndex][flavor]++;			
//				history.add(uuid + " " + flavorName + " " + createTime);
			}
		}
		int testday=changeDate(3,"2015-02-20");
		System.out.println(LinearModel(testday,3));
//		System.out.println(TrainData[0][15]);
		
		for (int i = 0; i < inputContent.length; i++) {
			if(i==0) {
				System.out.println();
				inputContent[i]=inputContent[i].replaceAll("\t+", " ");
				String[] array = inputContent[i].split(" ");
				Physical[0]=Integer.parseInt(array[0]);
				Physical[1]=Integer.parseInt(array[1]);
				Physical[2]=Integer.parseInt(array[2]);
			}	
			if(i==2) {
				
			}

		}

		for (int i = 0; i < history.size(); i++) {
			
		}

		return results;
	}
	//将日期yyyy-MM-dd格式转换成天
	public static int changeDate(int i,String CreatTime) {
		try {
			date = sdf.parse(CreatTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		long DayNum=date.getTime()/86400000;
		if(i==0) {
			BasePoint=(int) DayNum;
		}
		int day=(int)DayNum;		
		return day-BasePoint;		
	}
	public static double LinearModel(int day,int flavor) {
		int result = 0;
		double tamp=0.0; 
		for(int i=1;i<=30;i++) {
			if(i<=5) {
				tamp=tamp+TrainData[day-i][flavor]*1/10;
			}
			else if(i<=10) {
				tamp=tamp+TrainData[day-i][flavor]*1/15;
			}
			else {
				tamp=tamp+TrainData[day-i][flavor]*1/120;
			}
		}
		TrainData[day][flavor]=tamp;
		result=(int)tamp;
		return tamp;
	}
}
