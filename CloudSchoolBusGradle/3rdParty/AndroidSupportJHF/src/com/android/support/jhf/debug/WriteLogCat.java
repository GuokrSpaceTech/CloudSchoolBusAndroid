package com.android.support.jhf.debug;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.ClosedByInterruptException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Environment;

/**
 * debug工具类
 * 
 * @author hongfeijia
 * 
 */
public class WriteLogCat {

	public static final String DEBUG_SHARED_NAME = "Debug";
	public static final String TEST_DOMAIN = "test_domain";
	public static final String LOG_TXT = Environment
			.getExternalStorageDirectory()
			+ "/"
			+ "debuglogcat"
			+ ".log";

	private static final WriteLogCat DEBUG_UTIL = new WriteLogCat();

	private ExecutorService mExecutorService = Executors.newCachedThreadPool();
	private Process mLogcatProc;

	private WriteLogCat() {
	}

	/**
	 * 生成单例的DebugUtil
	 * @return
	 */
	public static WriteLogCat factory() {
		return DEBUG_UTIL;
	}
	
	/**
	 * 存储TestDomain标志，true测试环境，false正式环境
	 * 
	 * @param application
	 * @param value
	 */
	public void putTestDomainBoolean(Application application, boolean value) {
		SharedPreferences sharedPreferences = application.getSharedPreferences(
				DEBUG_SHARED_NAME, Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putBoolean(TEST_DOMAIN, value);
		editor.commit();
	}

	/**
	 * 获取boolean true测试环境， false正式环境
	 * 
	 * @param application
	 * @param keyString
	 * @return
	 */
	public boolean getTestDomainBoolean(Application application) {
		SharedPreferences sharedPreferences = application.getSharedPreferences(
				DEBUG_SHARED_NAME, Context.MODE_PRIVATE);
		return sharedPreferences.getBoolean(TEST_DOMAIN, true);
	}

	public String getDebugLogInfo(File file) {
		StringBuilder stringBuilder = new StringBuilder();
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(
					file));
			try {
				String lineString = null;
				while (null != (lineString = bufferedReader.readLine())) {
					stringBuilder.append(lineString).append("\n");
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return stringBuilder.toString();
	}

	/**
	 * 停止获取log信息
	 */
	public void stopLogCarInfo() {
		if (null != mExecutorService && null != mLogcatProc) {
			mLogcatProc.destroy();
		}

	}

	/**
	 * 开始获取log信息
	 */
	public void startLogCatInfo() {
		mExecutorService.execute(new Runnable() {

			@Override
			public void run() {

				BufferedReader reader = null;
				PrintWriter printWriter = null;

				File outputFile = new File(LOG_TXT);
				if (outputFile.exists()) {
					outputFile.delete();
				} else {

				}

				// 获取logcat日志信息
				try {
					// 优先级有底到高
					// V — Verbose (lowest priority)
					// D — Debug
					// I — Info
					// W — Warning
					// E — Error
					// F — Fatal
					// S — Silent (highest priority, on which nothing is ever
					// printed)
					mLogcatProc = Runtime.getRuntime().exec(
							new String[] { "logcat", "*:I" });// 可以打印I上的优先级
					printWriter = new PrintWriter(new BufferedWriter(
							new OutputStreamWriter(new FileOutputStream(
									outputFile), "utf-8")));

					reader = new BufferedReader(new InputStreamReader(
							mLogcatProc.getInputStream()));

					String line;

					while ((line = reader.readLine()) != null) {
						printWriter.write(line);
						printWriter.write("\n\r");
					}

				} catch (SecurityException e) {
					System.out.println("SecurityException ");
					e.printStackTrace();

				} catch (ClosedByInterruptException e) {
					System.out.println("ClosedByInterruptException");
					e.printStackTrace();

				} catch (AsynchronousCloseException e) {
					System.out.println("AsynchronousCloseException");
					e.printStackTrace();

				} catch (IOException e) {
					System.out.println("IOException");
					e.printStackTrace();

				} finally {
					System.out.println("log writer close");
					printWriter.close();

				}

			}

		});

	}

}
