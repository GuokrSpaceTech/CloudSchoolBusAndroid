package com.guokrspace.cloudschoolbus.parents.base;

import com.guokrspace.cloudschoolbus.parents.CloudSchoolBusParentsApplication;
import com.guokrspace.cloudschoolbus.parents.database.daodb.ConfigEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.ConfigEntityDao;
import com.guokrspace.cloudschoolbus.parents.event.BusProvider;
import com.guokrspace.cloudschoolbus.parents.event.InfoSwitchedEvent;

import java.util.ArrayList;

/**
 * Created by kai on 10/8/15.
 */
public class DataWrapper {
    private static final DataWrapper DATA_WRAPPER = new DataWrapper();
    private static CloudSchoolBusParentsApplication mApplication=null;


    public static DataWrapper getInstance() {
        return DATA_WRAPPER;
    }

    public DataWrapper() {
    }

    public void init(CloudSchoolBusParentsApplication application)
    {
        mApplication = application;
    }

    public void switchChildren(int currentChild)
    {
        ConfigEntityDao configEntityDao = mApplication.mDaoSession.getConfigEntityDao();
        ConfigEntity oldConfigEntity = configEntityDao.queryBuilder().limit(1).list().get(0);
        oldConfigEntity.setCurrentuser(currentChild);
        ConfigEntity newConfigEntity = oldConfigEntity;
        configEntityDao.update(newConfigEntity);
        mApplication.mConfig = newConfigEntity;

        BusProvider.getInstance().post(new InfoSwitchedEvent(currentChild));
    }

    //This function only works in parent app
    public String findCurrentStudentid()
    {
        int current =  mApplication.mConfig.getCurrentuser();
        String studentId = mApplication.mStudents.get(current).getStudentid();
        return studentId;
    }
}
