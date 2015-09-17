package com.guokrspace.cloudschoolbus.parents;

import android.app.Application;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.util.Log;


import com.guokrspace.cloudschoolbus.parents.base.include.Version;
import com.guokrspace.cloudschoolbus.parents.database.daodb.ClassEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.ClassEntityDao;
import com.guokrspace.cloudschoolbus.parents.database.daodb.ClassEntityT;
import com.guokrspace.cloudschoolbus.parents.database.daodb.ClassEntityTDao;
import com.guokrspace.cloudschoolbus.parents.database.daodb.ClassModuleEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.ConfigEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.ConfigEntityDao;
import com.guokrspace.cloudschoolbus.parents.database.daodb.DaoMaster;
import com.guokrspace.cloudschoolbus.parents.database.daodb.DaoSession;
import com.guokrspace.cloudschoolbus.parents.database.daodb.MessageTypeEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.MessageTypeEntityDao;
import com.guokrspace.cloudschoolbus.parents.database.daodb.ParentEntityT;
import com.guokrspace.cloudschoolbus.parents.database.daodb.ParentEntityTDao;
import com.guokrspace.cloudschoolbus.parents.database.daodb.SchoolEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.SchoolEntityDao;
import com.guokrspace.cloudschoolbus.parents.database.daodb.SchoolEntityT;
import com.guokrspace.cloudschoolbus.parents.database.daodb.SchoolEntityTDao;
import com.guokrspace.cloudschoolbus.parents.database.daodb.StudentClassRelationEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.StudentEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.StudentEntityDao;
import com.guokrspace.cloudschoolbus.parents.database.daodb.StudentEntityT;
import com.guokrspace.cloudschoolbus.parents.database.daodb.StudentEntityTDao;
import com.guokrspace.cloudschoolbus.parents.database.daodb.StudentParentRelationEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.TagEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.TagsEntityT;
import com.guokrspace.cloudschoolbus.parents.database.daodb.TeacherDutyClassRelationEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.TeacherEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.TeacherEntityDao;
import com.guokrspace.cloudschoolbus.parents.database.daodb.TeacherEntityT;
import com.guokrspace.cloudschoolbus.parents.database.daodb.TeacherEntityTDao;
import com.guokrspace.cloudschoolbus.parents.entity.Baseinfo;
import com.guokrspace.cloudschoolbus.parents.entity.ClassInfo;
import com.guokrspace.cloudschoolbus.parents.entity.Student;
import com.guokrspace.cloudschoolbus.parents.entity.Teacher;
import com.guokrspace.cloudschoolbus.parents.event.NetworkStatusEvent;
import com.guokrspace.cloudschoolbus.parents.protocols.CloudSchoolBusRestClient;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

public class CloudSchoolBusParentsApplication extends Application {

    private static String TAG = "CloudSchoolBusParentsApplication";

    public NetworkStatusEvent networkStatusEvent;

    /**
     * 带缓存的，内存缓存和磁盘缓存，设置再调用displayImage()有效,使用loadImage()无效
     */
    public DisplayImageOptions mCacheDisplayImageOptions;
    /**
     * 不带缓存，不带内存和磁盘缓存
     */
    public DisplayImageOptions mNoCacheDisplayImageOptions;
    /**
     * 带缓存的，默认图片是头像，用户学生头像显示
     */
    public DisplayImageOptions mStudentCacheDisplayImageOptions;

    public DaoMaster.DevOpenHelper mDBhelper;
    public DaoMaster mDaoMaster;
    public DaoSession mDaoSession;

    public ConfigEntity mConfig;
    public List<SchoolEntity> mSchools;
    public List<SchoolEntityT> mSchoolsT;
    public List<ClassEntity> mClasses;
    public List<ClassEntityT> mClassesT;
    public List<TeacherEntity> mTeachers;
    public List<TeacherEntityT> mTeachersT;
    public List<StudentEntity> mStudents;
    public List<StudentEntityT> mStudentsT;
    public List<TagsEntityT> mTagsT;
    public List<MessageTypeEntity> mMessageTypes;
    public List<ClassModuleEntity> mClassModules;
    public List<ParentEntityT> mParents;
    public List<TeacherDutyClassRelationEntity> mTeacherClassDutys;
    public List<StudentClassRelationEntity> mStudentClasses;
    public List<StudentParentRelationEntity> mStudentParents;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");

        initDB();

        initConfig();

        initBaseinfo();

        initImageLoader();

        RongIM.init(this);
    }

    private void initDB() {
        SQLiteDatabase db;
        mDBhelper = new DaoMaster.DevOpenHelper(this, "cloudschoolbusparents-db", null);
        db = mDBhelper.getWritableDatabase();

        mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();
    }

//    private void initSharedPreference()
//    {
//        SharedPreferences messageCount = this.getSharedPreferences("cloudschoolbuspref", MODE_WORLD_WRITEABLE);
//        SharedPreferences.Editor editor = messageCount.edit();
//        editor.putInt("unreadmessages",0);
//        editor.commit();
//    }

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

    public boolean initConfig() {
        boolean retCode = false;
        ConfigEntityDao configEntityDao = mDaoSession.getConfigEntityDao();
        List configs = configEntityDao.queryBuilder().list();
        if (configs.size() != 0) {
            mConfig = (ConfigEntity) configs.get(0);
            CloudSchoolBusRestClient.updateSessionid(mConfig.getSid());
            retCode = true;
        }

        return retCode;
    }

    public void initBaseinfo()
    {
        if(Version.PARENT) {
            SchoolEntityDao schoolEntityDao = mDaoSession.getSchoolEntityDao();
            ClassEntityDao classEntityDao = mDaoSession.getClassEntityDao();
            TeacherEntityDao teacherEntityDao = mDaoSession.getTeacherEntityDao();
            StudentEntityDao studentEntityDao = mDaoSession.getStudentEntityDao();

            mSchools = schoolEntityDao.queryBuilder().list();
            mClasses = classEntityDao.queryBuilder().list();
            mTeachers = teacherEntityDao.queryBuilder().list();
            mStudents = studentEntityDao.queryBuilder().list();
        } else {
            mSchoolsT = mDaoSession.getSchoolEntityTDao().queryBuilder().list();
            mClassesT = mDaoSession.getClassEntityTDao().queryBuilder().list();
            mTeachersT = mDaoSession.getTeacherEntityTDao().queryBuilder().list();
            mStudentsT = mDaoSession.getStudentEntityTDao().queryBuilder().list();
            mParents = mDaoSession.getParentEntityTDao().queryBuilder().list();
            mMessageTypes = mDaoSession.getMessageTypeEntityDao().queryBuilder().list();
            mTagsT = mDaoSession.getTagsEntityTDao().queryBuilder().list();
            mClassModules = mDaoSession.getClassModuleEntityDao().queryBuilder().list();
            mTeacherClassDutys = mDaoSession.getTeacherDutyClassRelationEntityDao().queryBuilder().list();
            mStudentParents = mDaoSession.getStudentParentRelationEntityDao().queryBuilder().list();
            mStudentClasses = mDaoSession.getStudentClassRelationEntityDao().queryBuilder().list();
        }
    }

    public void clearBaseinfo()
    {
        if(Version.PARENT) {
            SchoolEntityDao schoolEntityDao = mDaoSession.getSchoolEntityDao();
            ClassEntityDao classEntityDao = mDaoSession.getClassEntityDao();
            TeacherEntityDao teacherEntityDao = mDaoSession.getTeacherEntityDao();
            StudentEntityDao studentEntityDao = mDaoSession.getStudentEntityDao();

            schoolEntityDao.deleteAll();
            classEntityDao.deleteAll();
            teacherEntityDao.deleteAll();
            studentEntityDao.deleteAll();

            mClasses = null;
            mSchools = null;
            mStudents = null;
            mTeachers = null;
        } else {
            mDaoSession.getSchoolEntityTDao().deleteAll();
            mDaoSession.getClassEntityTDao().deleteAll();
            mDaoSession.getTeacherEntityTDao().deleteAll();
            mDaoSession.getStudentEntityTDao().deleteAll();
            mDaoSession.getParentEntityTDao().deleteAll();
            mDaoSession.getMessageTypeEntityDao().deleteAll();
            mDaoSession.getTagsEntityTDao().deleteAll();
            mDaoSession.getClassModuleEntityDao().deleteAll();
            mDaoSession.getStudentParentRelationEntityDao().deleteAll();
            mDaoSession.getTeacherDutyClassRelationEntityDao().deleteAll();
            mDaoSession.getStudentParentRelationEntityDao().deleteAll();


            mSchoolsT = null;
            mClassesT = null;
            mTeachersT = null;
            mStudentsT = null;
            mParents = null;
            mMessageTypes = null;
            mTagsT = null;
            mClassModules = null;
            mTeacherClassDutys = null;
            mStudentClasses = null;
            mStudentParents = null;
        }
    }

    public void clearConfig()
    {
        ConfigEntityDao configEntityDao = mDaoSession.getConfigEntityDao();
        configEntityDao.deleteAll();
        mConfig = null;
    }

    public void clearDb() {
        mDaoSession.clear();
    }
}
