package com.zhanghanlun;

import java.util.ArrayList;
import java.util.List;

public class Predict {
	static int[] Physical=new int[3];
	static String[][] predictVM;

	public static String[] predictVm(String[] ecsContent, String[] inputContent) {

		/** =========do your work here========== **/
		
		

		String[] results = new String[ecsContent.length];

		List<String> history = new ArrayList<String>();

		for (int i = 0; i < ecsContent.length; i++) {
			ecsContent[i]=ecsContent[i].replaceAll("\t+", " ");
			if (ecsContent[i].contains(" ")
					&& ecsContent[i].split(" ").length == 4) {
				System.out.print("zhang");
				String[] array = ecsContent[i].split(" ");
				System.out.println(array.length);
				String uuid = array[0];
				String flavorName = array[1];
				String createTime = array[2];
//				System.out.println( flavorName + " " );
				history.add(uuid + " " + flavorName + " " + createTime);
			}
		}
		
		for (int i = 0; i < inputContent.length; i++) {
			if(i==0) {
				System.out.println();
				inputContent[i]=inputContent[i].replaceAll("\t+", " ");
				String[] array = inputContent[i].split(" ");
//				System.out.println(array[0]);
				Physical[0]=Integer.parseInt(array[0]);
				Physical[1]=Integer.parseInt(array[1]);
				Physical[2]=Integer.parseInt(array[2]);
			}
//			
			if(i==2) {
				System.out.println(inputContent[i]);
			}

		}

		for (int i = 0; i < history.size(); i++) {
//			System.out.println(history.get(i));
//			results[i] = history.get(i);

		}

		return results;
	}

}
