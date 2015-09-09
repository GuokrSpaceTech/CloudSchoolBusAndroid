package com.guokrspace.cloudschoolbus.parents.database.daodb;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * DAO for table UPLOADING_PHOTOS.
*/
public class UploadingPhotoEntityDao extends AbstractDao<UploadingPhotoEntity, Void> {

    public static final String TABLENAME = "UPLOADING_PHOTOS";

    /**
     * Properties of entity UploadingPhotoEntity.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property PicPathString = new Property(0, String.class, "picPathString", false, "PIC_PATH_STRING");
        public final static Property PicFileString = new Property(1, String.class, "picFileString", false, "PIC_FILE_STRING");
        public final static Property PicSizeString = new Property(2, String.class, "picSizeString", false, "PIC_SIZE_STRING");
        public final static Property StudentId = new Property(3, String.class, "studentId", false, "STUDENT_ID");
        public final static Property Classuid = new Property(4, String.class, "classuid", false, "CLASSUID");
        public final static Property Intro = new Property(5, String.class, "intro", false, "INTRO");
        public final static Property PhotoTag = new Property(6, String.class, "photoTag", false, "PHOTO_TAG");
        public final static Property Teacherid = new Property(7, String.class, "teacherid", false, "TEACHERID");
    };


    public UploadingPhotoEntityDao(DaoConfig config) {
        super(config);
    }
    
    public UploadingPhotoEntityDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'UPLOADING_PHOTOS' (" + //
                "'PIC_PATH_STRING' TEXT," + // 0: picPathString
                "'PIC_FILE_STRING' TEXT," + // 1: picFileString
                "'PIC_SIZE_STRING' TEXT," + // 2: picSizeString
                "'STUDENT_ID' TEXT," + // 3: studentId
                "'CLASSUID' TEXT," + // 4: classuid
                "'INTRO' TEXT," + // 5: intro
                "'PHOTO_TAG' TEXT," + // 6: photoTag
                "'TEACHERID' TEXT);"); // 7: teacherid
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'UPLOADING_PHOTOS'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, UploadingPhotoEntity entity) {
        stmt.clearBindings();
 
        String picPathString = entity.getPicPathString();
        if (picPathString != null) {
            stmt.bindString(1, picPathString);
        }
 
        String picFileString = entity.getPicFileString();
        if (picFileString != null) {
            stmt.bindString(2, picFileString);
        }
 
        String picSizeString = entity.getPicSizeString();
        if (picSizeString != null) {
            stmt.bindString(3, picSizeString);
        }
 
        String studentId = entity.getStudentId();
        if (studentId != null) {
            stmt.bindString(4, studentId);
        }
 
        String classuid = entity.getClassuid();
        if (classuid != null) {
            stmt.bindString(5, classuid);
        }
 
        String intro = entity.getIntro();
        if (intro != null) {
            stmt.bindString(6, intro);
        }
 
        String photoTag = entity.getPhotoTag();
        if (photoTag != null) {
            stmt.bindString(7, photoTag);
        }
 
        String teacherid = entity.getTeacherid();
        if (teacherid != null) {
            stmt.bindString(8, teacherid);
        }
    }

    /** @inheritdoc */
    @Override
    public Void readKey(Cursor cursor, int offset) {
        return null;
    }    

    /** @inheritdoc */
    @Override
    public UploadingPhotoEntity readEntity(Cursor cursor, int offset) {
        UploadingPhotoEntity entity = new UploadingPhotoEntity( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // picPathString
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // picFileString
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // picSizeString
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // studentId
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // classuid
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // intro
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // photoTag
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7) // teacherid
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, UploadingPhotoEntity entity, int offset) {
        entity.setPicPathString(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setPicFileString(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setPicSizeString(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setStudentId(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setClassuid(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setIntro(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setPhotoTag(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setTeacherid(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
     }
    
    /** @inheritdoc */
    @Override
    protected Void updateKeyAfterInsert(UploadingPhotoEntity entity, long rowId) {
        // Unsupported or missing PK type
        return null;
    }
    
    /** @inheritdoc */
    @Override
    public Void getKey(UploadingPhotoEntity entity) {
        return null;
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
