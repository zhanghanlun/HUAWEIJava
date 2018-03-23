package com.zhanghanlun;

//import com.elasticcloudservice.predict.Predict;
//import com.filetool.util.FileUtil;
//import com.filetool.util.LogUtil;


public class Main {
	public static void main(String[] args) {

		if (args.length != 3) {
			System.err
					.println("please input args: ecsDataPath, inputFilePath, resultFilePath");
			return;
		}
		/**
		 * ecsData 
		 * input 
		 * output 
		 */

		String ecsDataPath = args[0];
		String inputFilePath = args[1];
		String resultFilePath = args[2];
		LogUtil.printLog("Begin");
		String[] ecsContent = FileUtil.read(ecsDataPath, null);
		String[] inputContent = FileUtil.read(inputFilePath, null);
		String[] resultContents = Predict.predictVm(ecsContent, inputContent);
		if (hasResults(resultContents)) {
			FileUtil.write(resultFilePath, resultContents, false);
		} else {
			FileUtil.write(resultFilePath, new String[] { "NA" }, false);
		}
		LogUtil.printLog("End");
	}

	private static boolean hasResults(String[] resultContents) {
		if (resultContents == null) {
			return false;
		}
		for (String contents : resultContents) {
			if (contents != null && !contents.trim().isEmpty()) {
				return true;
			}
		}
		return false;
	}

}
