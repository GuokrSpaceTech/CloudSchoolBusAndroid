package com.guokrspace.cloudschoolbus.parents.database.daodb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import de.greenrobot.dao.AbstractDaoMaster;
import de.greenrobot.dao.identityscope.IdentityScopeType;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * Master of DAO (schema version 1000): knows all DAOs.
*/
public class DaoMaster extends AbstractDaoMaster {
    public static final int SCHEMA_VERSION = 1000;

    /** Creates underlying database table using DAOs. */
    public static void createAllTables(SQLiteDatabase db, boolean ifNotExists) {
        ConfigEntityDao.createTable(db, ifNotExists);
        StudentEntityDao.createTable(db, ifNotExists);
        TeacherEntityDao.createTable(db, ifNotExists);
        ClassEntityDao.createTable(db, ifNotExists);
        ArticleEntityDao.createTable(db, ifNotExists);
        ImageEntityDao.createTable(db, ifNotExists);
        TagEntityDao.createTable(db, ifNotExists);
        NoticeEntityDao.createTable(db, ifNotExists);
        NoticeImageEntityDao.createTable(db, ifNotExists);
        AttendanceEntityDao.createTable(db, ifNotExists);
        FestivalEntityDao.createTable(db, ifNotExists);
        ScheduleEntityDao.createTable(db, ifNotExists);
        LetterEntityDao.createTable(db, ifNotExists);
    }
    
    /** Drops underlying database table using DAOs. */
    public static void dropAllTables(SQLiteDatabase db, boolean ifExists) {
        ConfigEntityDao.dropTable(db, ifExists);
        StudentEntityDao.dropTable(db, ifExists);
        TeacherEntityDao.dropTable(db, ifExists);
        ClassEntityDao.dropTable(db, ifExists);
        ArticleEntityDao.dropTable(db, ifExists);
        ImageEntityDao.dropTable(db, ifExists);
        TagEntityDao.dropTable(db, ifExists);
        NoticeEntityDao.dropTable(db, ifExists);
        NoticeImageEntityDao.dropTable(db, ifExists);
        AttendanceEntityDao.dropTable(db, ifExists);
        FestivalEntityDao.dropTable(db, ifExists);
        ScheduleEntityDao.dropTable(db, ifExists);
        LetterEntityDao.dropTable(db, ifExists);
    }
    
    public static abstract class OpenHelper extends SQLiteOpenHelper {

        public OpenHelper(Context context, String name, CursorFactory factory) {
            super(context, name, factory, SCHEMA_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.i("greenDAO", "Creating tables for schema version " + SCHEMA_VERSION);
            createAllTables(db, false);
        }
    }
    
    /** WARNING: Drops all table on Upgrade! Use only during development. */
    public static class DevOpenHelper extends OpenHelper {
        public DevOpenHelper(Context context, String name, CursorFactory factory) {
            super(context, name, factory);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.i("greenDAO", "Upgrading schema from version " + oldVersion + " to " + newVersion + " by dropping all tables");
            dropAllTables(db, true);
            onCreate(db);
        }
    }

    public DaoMaster(SQLiteDatabase db) {
        super(db, SCHEMA_VERSION);
        registerDaoClass(ConfigEntityDao.class);
        registerDaoClass(StudentEntityDao.class);
        registerDaoClass(TeacherEntityDao.class);
        registerDaoClass(ClassEntityDao.class);
        registerDaoClass(ArticleEntityDao.class);
        registerDaoClass(ImageEntityDao.class);
        registerDaoClass(TagEntityDao.class);
        registerDaoClass(NoticeEntityDao.class);
        registerDaoClass(NoticeImageEntityDao.class);
        registerDaoClass(AttendanceEntityDao.class);
        registerDaoClass(FestivalEntityDao.class);
        registerDaoClass(ScheduleEntityDao.class);
        registerDaoClass(LetterEntityDao.class);
    }
    
    public DaoSession newSession() {
        return new DaoSession(db, IdentityScopeType.Session, daoConfigMap);
    }
    
    public DaoSession newSession(IdentityScopeType type) {
        return new DaoSession(db, type, daoConfigMap);
    }
    
}
