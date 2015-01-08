package com.cytx.utility;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Environment;
import android.util.Log;

/**
 * 图片处理
 * @author xilehang
 *
 */
public class BitmapTools {
	
	/**     
     * 缩放图片     
     * @param bmp     
     * @param width     
     * @param height     
     * @return     
     */      
    public static Bitmap PicZoom(Bitmap bmp, int width, int height) {      
        int bmpWidth = bmp.getWidth();      
        int bmpHeght = bmp.getHeight();      
        Matrix matrix = new Matrix();      
        matrix.postScale((float) width / bmpWidth, (float) height / bmpHeght);      
      
        return Bitmap.createBitmap(bmp, 0, 0, bmpWidth, bmpHeght, matrix, true);      
    }  
    
    /**     
     * 计算缩放比     
     * @param oldWidth     
     * @param oldHeight     
     * @param newWidth     
     * @param newHeight     
     * @return     
     */      
    public static int reckonThumbnail(int oldWidth, int oldHeight, int newWidth, int newHeight) {      
        if ((oldHeight > newHeight && oldWidth > newWidth)      
                || (oldHeight <= newHeight && oldWidth > newWidth)) {      
            int be = (int) (oldWidth / (float) newWidth);      
            if (be <= 1)      
                be = 1;      
            return be;      
        } else if (oldHeight > newHeight && oldWidth <= newWidth) {      
            int be = (int) (oldHeight / (float) newHeight);      
            if (be <= 1)      
                be = 1;      
            return be;      
        }      
      
        return 1;      
    }  
    
    /**
	 * 保存图片到指定路径
	 * 
	 * @param bm
	 *            bm
	 */
	public static String saveImage(Bitmap bm) {
		HashMap<Integer, Object> map = judgeSdAndPath();
		String strTempFile = (String) map.get(1);
		File myRecAudioDir = (File) map.get(2);
		File mediaFile = null;
		try {
			mediaFile = File.createTempFile(strTempFile, ".jpg", myRecAudioDir);

			BufferedOutputStream bos = new BufferedOutputStream(
					new FileOutputStream(mediaFile));

			bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
			bos.flush();
			bos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return "";
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
		if (mediaFile == null) {
			return "";
		}
		return mediaFile.getPath();
	}

	/**
	 * 判断SDcard以及目录是否存在
	 * 
	 * @return map
	 */
	private static HashMap<Integer, Object> judgeSdAndPath() {
		HashMap<Integer, Object> map = new HashMap<Integer, Object>();
		// 判断SDcard是否存在
		boolean isSdCardExit = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
		File myRecAudioDir = null;

		String strTempFile = String.valueOf(System.currentTimeMillis());

		if (isSdCardExit) {
			myRecAudioDir = new File(Environment.getExternalStorageDirectory()
					+ "/upload");
			if (!myRecAudioDir.exists()) {
				myRecAudioDir.mkdir();
			}
		} else {
			Log.d("debug", "myRecAudioDir exists");
		}

		map.put(1, strTempFile);
		map.put(2, myRecAudioDir);
		return map;
	}

}
