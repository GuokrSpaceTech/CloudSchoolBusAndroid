package com.guokrspace.cloudschoolbus.parents;

import android.app.Application;
import android.graphics.Bitmap;
import android.util.Log;


import com.guokrspace.cloudschoolbus.parents.entity.ClassInfo;
import com.guokrspace.cloudschoolbus.parents.entity.Student;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.util.ArrayList;
import java.util.List;

public class CloudSchoolBusParentsApplication extends Application {

	private static String TAG = "CloudSchoolBusParentsApplication";
	
	/** 教师信息 */
	//public Teacher mTeacher;
	/** 教师账号中的学生列表 */
	public List<Student> mStudentList = new ArrayList<Student>();

	public Student mCurrentStudent = new Student();
    //public Reminder mReminders;
    //public List<ReportTemplates> mReportTemplates = new ArrayList<ReportTemplates>();
	/** 班级信息 */
	public ClassInfo mClassInfo;
	/**登陆设置*/
	//public LoginSetting mLoginSetting;

	/** 带缓存的，内存缓存和磁盘缓存，设置再调用displayImage()有效,使用loadImage()无效 */
	public DisplayImageOptions mCacheDisplayImageOptions;
	/** 不带缓存，不带内存和磁盘缓存 */
	public DisplayImageOptions mNoCacheDisplayImageOptions;
	/** 带缓存的，默认图片是头像，用户学生头像显示 */
	public DisplayImageOptions mStudentCacheDisplayImageOptions;
	/** 判断应用主界面是否运行,false没有运行，true运行 */
	public boolean mFlagHome = false;
    public Integer isTrain = 0;

    @Override
	public void onCreate() {
		super.onCreate();
		Log.i(TAG, "onCreate");

		initImageLoader();
	}

	public void imageLoaderInit() {

		ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(this);
		config.threadPriority(Thread.NORM_PRIORITY - 2);
		config.denyCacheImageMultipleSizesInMemory();
		config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
		config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
		config.tasksProcessingOrder(QueueProcessingType.LIFO);
		config.writeDebugLogs(); // Remove for release app

		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config.build());
	}

	private void initImageLoader() {
		// 以下的设置再调用displayImage()有效，使用loadImage()无效
		mCacheDisplayImageOptions = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_image_default)
				.showImageForEmptyUri(R.drawable.ic_image_default) // empty
																	// URI时显示的图片
				.showImageOnFail(R.drawable.ic_image_default) // 不是图片文件 显示图片
				.resetViewBeforeLoading(true) // default
				.delayBeforeLoading(1000).cacheInMemory(true) // 缓存至内存
				.cacheOnDisc(true) // 缓存至手机SDCard
				.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)// default
				.bitmapConfig(Bitmap.Config.RGB_565) // default
				.build();

		mStudentCacheDisplayImageOptions = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_image_default)
				.showImageForEmptyUri(R.drawable.ic_image_default) // empty
				.showImageOnFail(R.drawable.ic_image_default) // 不是图片文件 显示图片
				.resetViewBeforeLoading(true) // default
				.delayBeforeLoading(1000).cacheInMemory(true) // 缓存至内存
				.cacheOnDisc(true) // 缓存至手机SDCard
				.imageScaleType(ImageScaleType.IN_SAMPLE_INT)// default
				.bitmapConfig(Bitmap.Config.RGB_565) // default
				.build();

		mNoCacheDisplayImageOptions = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_image_default)
				.showImageForEmptyUri(R.drawable.ic_image_default) // empty
				.showImageOnFail(R.drawable.ic_image_default) // 不是图片文件 显示图片
				.resetViewBeforeLoading(true) // default
				.delayBeforeLoading(1000).cacheInMemory(true) // 缓存至内存
				.cacheOnDisc(false) // 缓存至手机SDCard
				.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)// default
				.bitmapConfig(Bitmap.Config.RGB_565) // default
				.build();

		imageLoaderInit();

	}

}
