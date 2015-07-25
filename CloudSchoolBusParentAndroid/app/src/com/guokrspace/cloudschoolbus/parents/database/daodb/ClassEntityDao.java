package com.guokrspace.cloudschoolbus.parents.database.daodb;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * DAO for table CLASS_ENTITY.
*/
public class ClassEntityDao extends AbstractDao<ClassEntity, String> {

    public static final String TABLENAME = "CLASS_ENTITY";

    /**
     * Properties of entity ClassEntity.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Uid = new Property(0, String.class, "uid", false, "UID");
        public final static Property Phone = new Property(1, String.class, "phone", false, "PHONE");
        public final static Property Schoolname = new Property(2, String.class, "schoolname", false, "SCHOOLNAME");
        public final static Property Address = new Property(3, String.class, "address", false, "ADDRESS");
        public final static Property Classname = new Property(4, String.class, "classname", false, "CLASSNAME");
        public final static Property Province = new Property(5, String.class, "province", false, "PROVINCE");
        public final static Property City = new Property(6, String.class, "city", false, "CITY");
        public final static Property Classid = new Property(7, String.class, "classid", true, "CLASSID");
    };

    private DaoSession daoSession;


    public ClassEntityDao(DaoConfig config) {
        super(config);
    }
    
    public ClassEntityDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'CLASS_ENTITY' (" + //
                "'UID' TEXT," + // 0: uid
                "'PHONE' TEXT," + // 1: phone
                "'SCHOOLNAME' TEXT," + // 2: schoolname
                "'ADDRESS' TEXT," + // 3: address
                "'CLASSNAME' TEXT," + // 4: classname
                "'PROVINCE' TEXT," + // 5: province
                "'CITY' TEXT," + // 6: city
                "'CLASSID' TEXT PRIMARY KEY NOT NULL );"); // 7: classid
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'CLASS_ENTITY'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, ClassEntity entity) {
        stmt.clearBindings();
 
        String uid = entity.getUid();
        if (uid != null) {
            stmt.bindString(1, uid);
        }
 
        String phone = entity.getPhone();
        if (phone != null) {
            stmt.bindString(2, phone);
        }
 
        String schoolname = entity.getSchoolname();
        if (schoolname != null) {
            stmt.bindString(3, schoolname);
        }
 
        String address = entity.getAddress();
        if (address != null) {
            stmt.bindString(4, address);
        }
 
        String classname = entity.getClassname();
        if (classname != null) {
            stmt.bindString(5, classname);
        }
 
        String province = entity.getProvince();
        if (province != null) {
            stmt.bindString(6, province);
        }
 
        String city = entity.getCity();
        if (city != null) {
            stmt.bindString(7, city);
        }
 
        String classid = entity.getClassid();
        if (classid != null) {
            stmt.bindString(8, classid);
        }
    }

    @Override
    protected void attachEntity(ClassEntity entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    /** @inheritdoc */
    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7);
    }    

    /** @inheritdoc */
    @Override
    public ClassEntity readEntity(Cursor cursor, int offset) {
        ClassEntity entity = new ClassEntity( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // uid
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // phone
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // schoolname
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // address
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // classname
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // province
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // city
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7) // classid
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, ClassEntity entity, int offset) {
        entity.setUid(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setPhone(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setSchoolname(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setAddress(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setClassname(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setProvince(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setCity(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setClassid(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
     }
    
    /** @inheritdoc */
    @Override
    protected String updateKeyAfterInsert(ClassEntity entity, long rowId) {
        return entity.getClassid();
    }
    
    /** @inheritdoc */
    @Override
    public String getKey(ClassEntity entity) {
        if(entity != null) {
            return entity.getClassid();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
