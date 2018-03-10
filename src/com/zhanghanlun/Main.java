package com.zhanghanlun;

//import com.elasticcloudservice.predict.Predict;
//import com.filetool.util.FileUtil;
//import com.filetool.util.LogUtil;

/**
 * 
 * �������
 * 
 * @version [�汾��, 2017-12-8]
 * @see [�����/����]
 * @since [��Ʒ/ģ��汾]
 */
public class Main {
	public static void main(String[] args) {

		if (args.length != 3) {
			System.err
					.println("please input args: ecsDataPath, inputFilePath, resultFilePath");
			return;
		}
		/**
		 * ecsData ��ѵ������
		 * input �������ѵ��Ҫ��
		 * output ����Ľ��
		 */

		String ecsDataPath = args[0];
		String inputFilePath = args[1];
		String resultFilePath = args[2];

		LogUtil.printLog("Begin");

		// ��ȡ�����ļ�
		String[] ecsContent = FileUtil.read(ecsDataPath, null);
		String[] inputContent = FileUtil.read(inputFilePath, null);
		// ����ʵ�����
		String[] resultContents = Predict.predictVm(ecsContent, inputContent);
//		for(int i=0;i<resultContents.length;i++) {
//		System.out.println(resultContents[i]);
//	    }
		
		// д������ļ�
//		if (hasResults(resultContents)) {
//			FileUtil.write(resultFilePath, resultContents, false);
//		} else {
//			FileUtil.write(resultFilePath, new String[] { "NA" }, false);
//		}
//		LogUtil.printLog("End");
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
