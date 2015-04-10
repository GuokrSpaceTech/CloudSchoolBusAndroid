package com.cytx;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import com.baidu.frontia.FrontiaApplication;

/**
 * 自定义Application：初始化万能图片加载类，引入了jar包universal-image-loader-1.8.2-with-sources.jar
 * @author xilehang
 *
 */
public class CYTXApplication extends FrontiaApplication {

	public final int SCREEN_480 = 1;// 480 X 800;
	public final int SCREEN_720 = 2;// 720 X 1280
	private static CYTXApplication cytxApplication;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		// 初始化异步加载图片类
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.bitmapConfig(Bitmap.Config.RGB_565)
				.imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				getApplicationContext()).defaultDisplayImageOptions(options)
				.threadPoolSize(5).memoryCache(new WeakMemoryCache()).build();
		ImageLoader.getInstance().init(config);
		cytxApplication = this;
        Mint.initAndStartSession(MyActivity.this, "ed4e94f2");
	}
	
	public static CYTXApplication getInstance(){
		return cytxApplication;
	}
	
	/**
	 * 获取移动设备分辨率
	 * @param screenWidth
	 * @param screenHeight
	 * @return
	 */
	public int getScreenType(int screenWidth, int screenHeight){
		if ( screenWidth <= 480 && screenHeight <= 800) {
			return SCREEN_480;
		}
		return SCREEN_720;
	}
	

}
