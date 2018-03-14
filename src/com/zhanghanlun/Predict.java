package com.zhanghanlun;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Predict {
	
	static int[] physical=new int[3];
	private static int[][] PredictVM = new int[17][5];
	private static double[] PredictVMNum= new double[18];
	private static String resourceDimension;// Resource Dimension
	private static String startTime;
	private static String endTime;
	private static int totalPhysicalMachineNum = 5;
	private static String[] result;
	private static String[] physicalAllocationResult;
	
	static Date date=new Date();
	static double[][] TrainData=new double[1000][20];
	static int BasePoint=0;
	static SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");

	public static String[] predictVm(String[] ecsContent, String[] inputContent) {
		
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
				history.add(flavorName + "\t" + createTime);
				int flavor=Integer.parseInt(flavorName.substring(6));
				int DayIndex=changeDate(i,createTime);
				if(flavor<19)
					TrainData[DayIndex][flavor]++;			
			}
		}
//		int testday=changeDate(3,"2015-02-20");
//		System.out.println(LinearModel(testday,3));
		
//		for (int i = 0; i < inputContent.length; i++) {
//			if(i==0) {
//				System.out.println();
//				inputContent[i]=inputContent[i].replaceAll("\t+", " ");
//				String[] array = inputContent[i].split(" ");
//				physical[0]=Integer.parseInt(array[0]);
//				physical[1]=Integer.parseInt(array[1]);
//				physical[2]=Integer.parseInt(array[2]);
//			}	
//			if(i==2) {
//				
//			}
//
//		}
		parseInputContent(inputContent);
		PredictResult(startTime,endTime);
//		String[] results = generateResult();
//		System.out.println(results[0]);
//		for(int i=0;i<history.size();i++) {
//			results[i]=history.get(i);
//		}
		return results;
	}
	/**
	 * 解析Input Data数据到相应的引用
	 */
	private static void parseInputContent(String[] inputContent){
		int predictVmNum = 0;
		for (int i = 0; i < inputContent.length; i++) {
			if (i == 0) {
				String[] phyArray = inputContent[i].split(" ");
				physical[0] = Integer.parseInt(phyArray[0]);//CPU
				physical[1] = Integer.parseInt(phyArray[1]);//Memory
				physical[2] = Integer.parseInt(phyArray[2]);//Hardware
			}
			
			if (i == 2) {
				predictVmNum = Integer.parseInt(inputContent[i]);
			}
			
			if (i > 2 && i <= 2 + predictVmNum) {
				String[] flavourItems = inputContent[i].split(" ");
				int flavorNumber=Integer.parseInt(flavourItems[0].substring(6));
				PredictVM[flavorNumber][0]=1;
				PredictVM[flavorNumber][1]=Integer.parseInt(flavourItems[1]);
				PredictVM[flavorNumber][2]=Integer.parseInt(flavourItems[2]) / 1024;
				PredictVM[flavorNumber][3]=0;
			}
			
			if (i == 4 + predictVmNum) { //Resource Dimension
				resourceDimension = inputContent[i];
			}
			
			if (i == 6 + predictVmNum) {
				startTime = inputContent[i];
				endTime = inputContent[i+1];
				break;
			}
		}
	}
	
	//将日期yyyy-MM-dd换成天
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
	//线性预测模型
	public static double LinearModel(int day,int flavor) {
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
		return tamp;
	}
	/**
	 * PredictResult Function
	 * @param start
	 * @param end
	 */
	
	
	//zhang
	public static void PredictResult(String start,String end) {
		int begin = changeDate(3,start);
		int finish= changeDate(3,end);
		for(int i=begin;i<=finish;i++) {
			for(int j=1;j<=15;j++) {
				if(PredictVM[j][0]==1) {
					PredictVMNum[j]+=LinearModel(i,j);
				}
			}
		}
		for(int i=1;i<=15;i++) {
			if(PredictVM[i][0]==1) {
				System.out.println(i+"    "+PredictVMNum[i]);
				PredictVM[i][3]=(int) PredictVMNum[i];
			}
		}
	}
//	private static String[] generateResult(){
//		totalPhysicalMachineNum =  predictVmNums.size();
//		result = new String[3 + predictVmNums.size() + totalPhysicalMachineNum];
//		
//		result[0] = String.valueOf(predictVmNums.size());
//		
//		Iterator<Entry<Integer, int[]>> iter = predictVmNums.entrySet().iterator();
//		int i=1;
//		while (iter.hasNext()) {
//			Map.Entry<Integer, int[]> entry = (Map.Entry<Integer, int[]>) iter.next();
//			int flavorName = entry.getKey();
//			int perFlavorNum = entry.getValue()[2];
//			result[i++] = "flavor" + flavorName + " " + perFlavorNum;
//		}
//		
//		result[i++] = "\r";// in this place must use the "\r" or " "
//		
//		test();
//		
//		result[i++] = String.valueOf(totalPhysicalMachineNum);
//		
//		for (int j = 0; j < totalPhysicalMachineNum; j++) {
//			result[i + j] = physicalAllocationResult[j];
//		}
//		return result;
//	}
	
	private static void test(){
		physicalAllocationResult = new String[totalPhysicalMachineNum];
		for (int i = 0; i < totalPhysicalMachineNum; i++) {
			physicalAllocationResult[i] = String.valueOf(i + 1)+" " + result[i+1];			
		}
	}
}
