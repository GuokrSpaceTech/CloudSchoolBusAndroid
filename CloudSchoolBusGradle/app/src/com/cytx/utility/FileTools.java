package com.cytx.utility;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import android.os.Environment;
import android.util.Log;

public class FileTools {
	
	
	/**
	 * 得到SDcard路径
	 * 
	 * @return
	 */
	public static String getSDcardPath() {
		String SDcardPath = "";
		if (existSDcard()) {
			SDcardPath = Environment.getExternalStorageDirectory()
					.getAbsolutePath();
		}
		return SDcardPath;
	}

	/**
	 * 判断存储卡是否存在
	 * 
	 * @return
	 */
	public static boolean existSDcard() {
		if (android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment
				.getExternalStorageState())) {
			return true;
		} else
			return false;
	}

	/**
	 * 讲字符串写到sdcard
	 * @param fileDir 文件目录
	 * @param fileName 文件名
	 * @param suffix 文件后缀
	 * @param content 内容
	 * @return
	 */
	public static boolean save2SDCard(String fileDir, String fileName,String suffix, String content) {
		try {
			File file = new File(fileDir);
			if (!file.exists()) {
				file.mkdirs();
			}

			File[] fileListsFiles = file.listFiles();

			int index = -1;
			if (fileListsFiles == null) {
				index = 0;
			} else {
				index = fileListsFiles.length;
			}

			File fileAbsolute = new File(fileDir, fileName + index + suffix);
			if (fileAbsolute.exists()) {
				fileAbsolute.delete();
			}
			fileAbsolute.createNewFile();

			// 得到文件输出流
			FileOutputStream outStream = new FileOutputStream(fileAbsolute);
			outStream.write(content.getBytes());
			outStream.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	
    
    /**
	 * 将数据保存的文件中
	 * @param path
	 */
	public static void writeFileByLines(String jsonData, String fileName){
		Log.d("writeByLines", jsonData + "=====" + fileName);
		File file = new File(fileName);
		if(file.exists()){
			file.delete();
		}
		BufferedWriter writer = null;
		try {
			file.createNewFile();
			Log.d("writeByLines", file.getName());
			writer = new BufferedWriter(new FileWriter(file));
			writer.write(jsonData);
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(writer != null){
					writer.close();
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}