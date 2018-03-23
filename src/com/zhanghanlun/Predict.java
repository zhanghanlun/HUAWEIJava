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
	/**
	 * [0] CPU
	 * [1] MEM
	 * [2] HARDWARE
	 */
	private static int[] physical=new int[3];//Physical machine flavor
	/**
	 * read the input and predict the VMNumbers
	 * [0] 是否预测该实例 1表示要预测该实例
	 * [1] CPU
	 * [2] MEM
	 * [3] Predict Numbers
	 */
	private static int[][] PredictVM = new int[17][5];
	private static double[] PredictVMNum= new double[18];
	private static String resourceDimension;// Resource Dimension CPU or MEM
	private static String startTime;//预测开始时间
	private static String endTime;//预测结束时间
	private static int vmNums = 0;

	private static ArrayList<int[]> PhyMachineAlloc = new ArrayList<>();//物理机的分配
	private static String[] result;
	static Date date=new Date();
	static double[][] TrainData=new double[1000][20];//存储训练算法
	static int BasePoint=0;
	static SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
	/**
	 * 主要预测函数
	 * @param ecsContent
	 * @param inputContent
	 * @return
	 */
	public static String[] predictVm(String[] ecsContent, String[] inputContent) {

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

		parseInputContent(inputContent);
		PredictResult(startTime,endTime);
		//PredictOneModel(startTime,endTime);
		allocation();
		String[] results = generateResult();
		return results;
	}
	/**
	 * read the input.txt
	 * @param inputContent
	 */
	private static void parseInputContent(String[] inputContent) {
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
	/**
	 * 分配算法
	 */
	private static void allocation() {
		/**
		 * [16]:Physical CPU Flavor
		 * [17]:Physical MEM Flavor
		 */
		int[] temp = new int[18];
		temp[16] = physical[0];
		temp[17] = physical[1];
		PhyMachineAlloc.add(temp);

		for(int i = PredictVM.length -1 ; i >= 0; i--) {
			if (PredictVM[i][0] != 1) {
				continue;
			}

			for(int j = 0; j < PredictVM[i][3]; j++) {
				int cpuflavor = PredictVM[i][1];
				int memflavor = PredictVM[i][2];
				vmNums++;
				boolean flag = true;
				for(int a = 0; a < PhyMachineAlloc.size(); a++) {
					if (cpuflavor <= PhyMachineAlloc.get(a)[16] && memflavor <= PhyMachineAlloc.get(a)[17]) {
						PhyMachineAlloc.get(a)[i]++;
						PhyMachineAlloc.get(a)[16] -= cpuflavor;
						PhyMachineAlloc.get(a)[17] -=  memflavor;
						flag = false;
						break;
					}
				}
				if (flag) {
					int[] newArray = new int[18];
					newArray[i]++;
					newArray[16] = physical[0] - cpuflavor;
					newArray[17] = physical[1] - memflavor;
					PhyMachineAlloc.add(newArray);
				}
			}
		}
	}
	/**
	 * 输出result.txt 函数
	 * @return
	 */
	private static String[] generateResult() {
		int totalPhysicalMachineNum =  PhyMachineAlloc.size();
		result = new String[3 + vmNums + totalPhysicalMachineNum];
		result[0] = String.valueOf(vmNums);

		int index = 0;
		for(int i = 0 ; i < PredictVM.length - 1; i++) {
			if (PredictVM[i][0] != 1) {
				continue;
			}
			result[++index] = "flavor" + i + " " + PredictVM[i][3];
		}

		result[++index] = " ";

		result[++index] = String.valueOf(totalPhysicalMachineNum);

		for (int i = 0; i < PhyMachineAlloc.size(); i++) {
			result[(index+1) + i] = (i+1)+" ";
			for (int j = 0; j < PhyMachineAlloc.get(i).length - 2; j++) {
				if (PhyMachineAlloc.get(i)[j] != 0) {
					result[(index+1) + i] += "flavor"+j + " " +PhyMachineAlloc.get(i)[j] + " ";
				}
			}
		}
		return result;
	}
	/**
	 * 格式化日期为天
	 * @param i
	 * @param CreatTime
	 * @return
	 */
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
		return day-BasePoint+1;
	}
	/**
	 * AR Predict Model
	 * AR 预测模型指导文给出的模型
	 * @param day
	 * @param flavor
	 * @return
	 */
	public static double LinearModel(int day,int flavor) {
		double tamp=0.0;
		for(int i=1; i<=30; i++) {
			if(i<=3) {
				tamp=tamp+TrainData[day-i][flavor]*1/6;
			} else if(i<=10) {
				tamp=tamp+TrainData[day-i][flavor]*1/21;
			} else {
				tamp=tamp+TrainData[day-i][flavor]*1/144;
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
	public static void PredictResult(String start,String end) {
		int begin = changeDate(3,start);
		int finish= changeDate(3,end);
		for(int i=begin; i<=finish; i++) {
			for(int j=1; j<=15; j++) {
				if(PredictVM[j][0]==1) {
					PredictVMNum[j]+=LinearModel(i,j);
				}
			}
		}
		for(int i=1; i<=15; i++) {
			if(PredictVM[i][0]==1) {
				PredictVM[i][3]=(int) Math.round(PredictVMNum[i]);
			}
		}
	}
	/**
	 * 一次指数平滑预测函数
	 * @param start
	 * @param end
	 * @param a
	 * @param flovor
	 */
	private static void OneExponModel(String start,String end,double a,int flovor) {
		int begin = changeDate(3,start);
		int finish= changeDate(3,end);
		double pre=(TrainData[begin-30][flovor]+TrainData[begin-29][flovor]+TrainData[begin-28][flovor])/3;
		double next;
		for(int i=begin-30; i<begin; i++) {
			if(i==begin-30) {
				pre=a*TrainData[i][flovor]+(1-a)*pre;
			}
		}
		for(int i=begin; i<=finish; i++) {
			pre=a*TrainData[i][flovor]+(1-a)*pre;
			PredictVMNum[flovor]+=pre;
		}
	}
	/**
	 * 一次指数平滑预测结果生成方法
	 * @param start
	 * @param end
	 */
	public static void PredictOneModel(String start, String end) {
		for (int i = 1; i <= 15; i++) {
			if (PredictVM[i][0] == 1) {
				
				OneExponModel(start, end, 0.0008, i);
				PredictVM[i][3] = (int) Math.round(PredictVMNum[i]);
				System.out.println(i + "    " + PredictVMNum[i]);
			}
		}
	}
}
