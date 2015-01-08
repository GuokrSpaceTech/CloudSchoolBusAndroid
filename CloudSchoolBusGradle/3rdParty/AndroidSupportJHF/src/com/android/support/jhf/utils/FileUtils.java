package com.android.support.jhf.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import android.content.Context;
import android.os.Environment;

/**
 * 文件管理类
 * 
 * @author hongfeijia
 * 
 */
public class FileUtils {

	/**
	 * 拷贝assets文件夹下的文件到指定的路径
	 * 
	 * @param context
	 * @param assets
	 * @param targetPath
	 * @throws java.io.IOException
	 */
	public static void copyAssetsFile(Context context, String assets,
			String targetPath) throws IOException {
		File targetFile = new File(targetPath);
		FileOutputStream fileOutputStream = new FileOutputStream(targetFile);
		InputStream inputStream = context.getAssets().open(assets);
		int len = -1;
		byte[] b = new byte[4096];
		while (-1 != (len = inputStream.read(b))) {
			fileOutputStream.write(b, 0, len);
		}
		fileOutputStream.flush();
		inputStream.close();
		fileOutputStream.close();
	}

	/**
	 * 用在android上 NIO —— MappedByteBuffer 分割拷贝单个大文件
	 * 
	 * @param sourcePath
	 * @param targetPath
	 * @throws java.io.IOException
	 */
	public static void copySingleFile(String sourcePath, String targetPath)
			throws IOException {
		copySingleFile(sourcePath, targetPath, 1);
	}

	/**
	 * NIO —— MappedByteBuffer 分割拷贝单个大文件
	 * 
	 * @param sourcePath
	 * @param targetPath
	 * @param device
	 *            device = 0 在pc上用,device = 1 在android上用
	 * @throws java.io.IOException
	 */
	public static void copySingleFile(String sourcePath, String targetPath,
			int device) throws IOException {
		// long before = System.currentTimeMillis();

		File files = new File(sourcePath); // 源文件
		File filet = new File(targetPath); // 目标文件

		long size = files.length(); // 文件总大小
		long countSize = 0;
		long lontemp = 0;

		if (0 == device) {
			// device = 0 在pc上用
			// 获取读、写之和所占用虚拟内存 倍数
			long copycount = size * 2 / Integer.MAX_VALUE;
			// 根据倍数确认分割份数
			int copynum = copycount >= 1 ? (int) copycount + 2
					: (int) copycount + 1;
			// 每块分割大小<每次读写的大小>
			countSize = Integer.MAX_VALUE / copynum;
			lontemp = countSize; // 初始读、写大小
		} else if (1 == device) {
			// device = 1 在android上用
			countSize = 1024 * 1024; // 每块分割大小<每次读写的大小>
			lontemp = countSize; // 初始读、写大小
		}

		FileChannel channels = null;
		FileChannel channelt = null;
		try {
			channels = new RandomAccessFile(files, "r").getChannel();// 得到映射读文件的通道
			channelt = new RandomAccessFile(filet, "rw").getChannel(); // 得到映射写文件的通道

			long j = 0; // 每次循环累加字节的起始点
			MappedByteBuffer mbbs = null; // 声明读源文件对象
			MappedByteBuffer mbbt = null; // 声明写目标文件对象
			try {
				while (j < size) {
					// 每次读源文件都重新构造对象
					mbbs = channels.map(FileChannel.MapMode.READ_ONLY, j,
							lontemp);
					// 每次写目标文件都重新构造对象
					mbbt = channelt.map(FileChannel.MapMode.READ_WRITE, j,
							lontemp);
					for (int i = 0; i < lontemp; i++) {
						byte b = mbbs.get(i); // 从源文件读取字节
						mbbt.put(i, b); // 把字节写到目标文件中
					}
					System.gc(); // 手动调用 GC <必须的，否则出现异常>
					System.runFinalization(); // 运行处于挂起终止状态的所有对象的终止方法。<必须的，否则出现异常>
					j += lontemp; // 累加每次读写的字节
					lontemp = size - j; // 获取剩余字节
					// 如果剩余字节 大于 每次分割字节 则 读取 每次分割字节 ，否则读取剩余字节
					lontemp = lontemp > countSize ? countSize : lontemp;
				}
			} catch (IOException e) {
				e.printStackTrace();
				IOException ioException = new IOException();
				ioException.initCause(e);
				throw ioException;
			} finally {
				try {
					channels.close();
					channelt.close();
				} catch (IOException e) {
					e.printStackTrace();
					IOException ioException = new IOException();
					ioException.initCause(e);
					throw ioException;
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			FileNotFoundException fileNotFoundException = new FileNotFoundException();
			fileNotFoundException.initCause(e);
			throw fileNotFoundException;
		}

		// System.out.println("MillTime : "
		// + (double) (System.currentTimeMillis() - before) / 1000 + "s");
	}

	public static void saveFile(String soure, File file) {
		FileWriter fw = null;
		try {
			fw = new FileWriter(file);
			fw.write(soure);
			fw.flush();
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
	
	public static String readFile(File file){
		String resultString = null;
		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			int len = -1;
			byte[] buffer = new byte[4096];
			try {
				while (-1 != (len = fileInputStream.read(buffer))) {
					byteArrayOutputStream.write(buffer, 0, len);
				}
				byteArrayOutputStream.flush();
				resultString = byteArrayOutputStream.toString();
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				try {
					byteArrayOutputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return resultString;
	}

}
