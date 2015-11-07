package com.guokrspace.cloudschoolbus.teacher;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.util.Log;

import com.guokrspace.cloudschoolbus.teacher.base.RongCloudEvent;
import com.guokrspace.cloudschoolbus.teacher.base.include.Version;
import com.guokrspace.cloudschoolbus.teacher.database.daodb.ClassEntityT;
import com.guokrspace.cloudschoolbus.teacher.database.daodb.ClassModuleEntity;
import com.guokrspace.cloudschoolbus.teacher.database.daodb.ConfigEntity;
import com.guokrspace.cloudschoolbus.teacher.database.daodb.ConfigEntityDao;
import com.guokrspace.cloudschoolbus.teacher.database.daodb.DaoMaster;
import com.guokrspace.cloudschoolbus.teacher.database.daodb.DaoSession;
import com.guokrspace.cloudschoolbus.teacher.database.daodb.MessageTypeEntity;
import com.guokrspace.cloudschoolbus.teacher.database.daodb.ParentEntityT;
import com.guokrspace.cloudschoolbus.teacher.database.daodb.SchoolEntityT;
import com.guokrspace.cloudschoolbus.teacher.database.daodb.StudentClassRelationEntity;
import com.guokrspace.cloudschoolbus.teacher.database.daodb.StudentEntityT;
import com.guokrspace.cloudschoolbus.teacher.database.daodb.StudentParentRelationEntity;
import com.guokrspace.cloudschoolbus.teacher.database.daodb.TagsEntityT;
import com.guokrspace.cloudschoolbus.teacher.database.daodb.TeacherDutyClassRelationEntity;
import com.guokrspace.cloudschoolbus.teacher.database.daodb.TeacherEntityT;
import com.guokrspace.cloudschoolbus.teacher.event.NetworkStatusEvent;
import com.guokrspace.cloudschoolbus.teacher.protocols.CloudSchoolBusRestClient;

import java.util.List;

import io.rong.imkit.RongIM;

public class CloudSchoolBusParentsApplication extends Application {

    private static String TAG = "CloudSchoolBusParentsApplication";

    public NetworkStatusEvent networkStatusEvent;

    public DaoMaster.DevOpenHelper mDBhelper;
    public DaoMaster mDaoMaster;
    public DaoSession mDaoSession;

    public ConfigEntity mConfig;
    public List<SchoolEntityT> mSchoolsT;
    public List<ClassEntityT> mClassesT;
    public List<TeacherEntityT> mTeachersT;
    public List<StudentEntityT> mStudentsT;
    public List<TagsEntityT> mTagsT;
    public List<MessageTypeEntity> mMessageTypes;
    public List<ClassModuleEntity> mClassModules;
    public List<ParentEntityT> mParents;
    public List<TeacherDutyClassRelationEntity> mTeacherClassDutys;
    public List<StudentClassRelationEntity> mStudentClasses;
    public List<StudentParentRelationEntity> mStudentParents;

    public String mCacheDir;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");

        initDB();

        initConfig();

        initBaseinfo();

        initCacheFile();

        initRongIM();
    }

    public void initCacheFile() {
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            this.mCacheDir = getExternalCacheDir().getAbsolutePath();
        } else {
            this.mCacheDir = getCacheDir().getAbsolutePath();
        }
    }

    public void initDB() {
        SQLiteDatabase db;
        if (mDBhelper != null) mDBhelper.close();
        if (Version.PARENT) {
            mDBhelper = new DaoMaster.DevOpenHelper(this, "cloudschoolbusparents-db", null);
        } else {
            mDBhelper = new DaoMaster.DevOpenHelper(this, "cloudschoolbusteacher-db", null);
        }
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

    public void clearBaseinfo() {
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
    }

    public void clearConfig() {
        ConfigEntityDao configEntityDao = mDaoSession.getConfigEntityDao();
        configEntityDao.deleteAll();
        mConfig = null;
    }

    public void clearDb() {
        mDaoSession.clear();

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

        mDBhelper.close();
}

    public void clearData()
    {
        mDaoSession.getMessageEntityDao().deleteAll();
        mDaoSession.clear();
    }

    private void initRongIM()
    {
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
