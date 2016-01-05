package com.guokrspace.cloudschoolbus.parents;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;


import com.guokrspace.cloudschoolbus.parents.base.RongCloudEvent;
import com.guokrspace.cloudschoolbus.parents.database.daodb.ClassEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.ClassEntityDao;
import com.guokrspace.cloudschoolbus.parents.database.daodb.ConfigEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.ConfigEntityDao;
import com.guokrspace.cloudschoolbus.parents.database.daodb.DaoMaster;
import com.guokrspace.cloudschoolbus.parents.database.daodb.DaoSession;
import com.guokrspace.cloudschoolbus.parents.database.daodb.SchoolEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.SchoolEntityDao;
import com.guokrspace.cloudschoolbus.parents.database.daodb.StudentEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.StudentEntityDao;
import com.guokrspace.cloudschoolbus.parents.database.daodb.TeacherEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.TeacherEntityDao;
import com.guokrspace.cloudschoolbus.parents.event.BaseinfoErrorEvent;
import com.guokrspace.cloudschoolbus.parents.event.BusProvider;
import com.guokrspace.cloudschoolbus.parents.event.NetworkStatusEvent;
import com.guokrspace.cloudschoolbus.parents.protocols.CloudSchoolBusRestClient;

import java.util.List;

import io.rong.imkit.RongIM;

public class CloudSchoolBusParentsApplication extends Application {

    private static String TAG = "CloudSchoolBusParentsApplication";

    public NetworkStatusEvent networkStatusEvent;

    public DaoMaster.DevOpenHelper mDBhelper;
    public DaoMaster mDaoMaster;
    public DaoSession mDaoSession;

    public ConfigEntity mConfig;
    public List<SchoolEntity> mSchools;
    public List<ClassEntity> mClasses;
    public List<TeacherEntity> mTeachers;
    public List<StudentEntity> mStudents;

    public String mCacheDir;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");

        initDB();

        initConfig();

        initBaseinfo();

//        initCacheFile();

        initRongIM();
    }

//    public void initCacheFile() {
//        if (android.os.Environment.getExternalStorageState().equals(
//                android.os.Environment.MEDIA_MOUNTED)) {
//            if(getExternalFilesDir(Environment.DIRECTORY_PICTURES)!=null)
//                this.mCacheDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath();
//        } else {
//            if(getFilesDir()!=null)
//                this.mCacheDir = getFilesDir().getAbsolutePath();
//        }
//    }

    public void initDB() {
        SQLiteDatabase db;
        if (mDBhelper != null) mDBhelper.close();

        mDBhelper = new DaoMaster.DevOpenHelper(this, "cloudschoolbusparents-db", null);

        db = mDBhelper.getWritableDatabase();

        mDaoMaster = new DaoMaster(db);

        mDaoSession = mDaoMaster.newSession();
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

    public void initBaseinfo() {
        SchoolEntityDao schoolEntityDao = mDaoSession.getSchoolEntityDao();
        ClassEntityDao classEntityDao = mDaoSession.getClassEntityDao();
        TeacherEntityDao teacherEntityDao = mDaoSession.getTeacherEntityDao();
        StudentEntityDao studentEntityDao = mDaoSession.getStudentEntityDao();

        mSchools = schoolEntityDao.queryBuilder().list();
        mClasses = classEntityDao.queryBuilder().list();
        mTeachers = teacherEntityDao.queryBuilder().list();
        mStudents = studentEntityDao.queryBuilder().list();
    }

    public void clearBaseinfo() {
        SchoolEntityDao schoolEntityDao = mDaoSession.getSchoolEntityDao();
        ClassEntityDao classEntityDao = mDaoSession.getClassEntityDao();
        TeacherEntityDao teacherEntityDao = mDaoSession.getTeacherEntityDao();
        StudentEntityDao studentEntityDao = mDaoSession.getStudentEntityDao();

        schoolEntityDao.deleteAll();
        classEntityDao.deleteAll();
        teacherEntityDao.deleteAll();
        studentEntityDao.deleteAll();
    }

    public void clearConfig() {
        ConfigEntityDao configEntityDao = mDaoSession.getConfigEntityDao();
        configEntityDao.deleteAll();
        mConfig = null;
    }

    public void clearDb() {
        mDaoSession.clear();

        mClasses = null;
        mSchools = null;
        mStudents = null;
        mTeachers = null;
        mDBhelper.close();

    }

    public void clearData() {
        //Clear DB
        mDaoSession.getMessageEntityDao().deleteAll();
        mDaoSession.clear();
    }

    private void initRongIM() {
        /**
         * OnCreate 会被多个进程重入，这段保护代码，确保只有您需要使用 RongIM 的进程和 Push 进程执行了 init。
         * io.rong.push 为融云 push 进程名称，不可修改。
         */
        if (getApplicationInfo().packageName.equals(getCurProcessName(getApplicationContext())) ||
                "io.rong.push".equals(getCurProcessName(getApplicationContext()))) {

            /**
             * IMKit SDK调用第一步 初始化
             */
            RongIM.init(this);
            RongCloudEvent.init(this);

        }
    }


    /**
     * 获得当前进程的名字
     *
     * @param context
     * @return 进程号
     */
    public static String getCurProcessName(Context context) {

        int pid = android.os.Process.myPid();

        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager
                .getRunningAppProcesses()) {

            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }
}
