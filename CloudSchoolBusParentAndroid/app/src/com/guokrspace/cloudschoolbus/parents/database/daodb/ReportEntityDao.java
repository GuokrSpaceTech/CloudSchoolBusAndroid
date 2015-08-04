package com.guokrspace.cloudschoolbus.parents.database.daodb;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * DAO for table REPORT_ENTITY.
*/
public class ReportEntityDao extends AbstractDao<ReportEntity, String> {

    public static final String TABLENAME = "REPORT_ENTITY";

    /**
     * Properties of entity ReportEntity.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, String.class, "id", true, "ID");
        public final static Property Title = new Property(1, String.class, "title", false, "TITLE");
        public final static Property Cnname = new Property(2, String.class, "cnname", false, "CNNAME");
        public final static Property Reportname = new Property(3, String.class, "reportname", false, "REPORTNAME");
        public final static Property Studentlist = new Property(4, String.class, "studentlist", false, "STUDENTLIST");
        public final static Property Reporttime = new Property(5, String.class, "reporttime", false, "REPORTTIME");
        public final static Property Createtime = new Property(6, String.class, "createtime", false, "CREATETIME");
        public final static Property Type = new Property(7, String.class, "type", false, "TYPE");
        public final static Property Adduserid = new Property(8, String.class, "adduserid", false, "ADDUSERID");
        public final static Property Teachername = new Property(9, String.class, "teachername", false, "TEACHERNAME");
        public final static Property Studentlistid = new Property(10, String.class, "studentlistid", false, "STUDENTLISTID");
        public final static Property Studentname = new Property(11, String.class, "studentname", false, "STUDENTNAME");
    };

    private DaoSession daoSession;


    public ReportEntityDao(DaoConfig config) {
        super(config);
    }
    
    public ReportEntityDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'REPORT_ENTITY' (" + //
                "'ID' TEXT PRIMARY KEY NOT NULL ," + // 0: id
                "'TITLE' TEXT," + // 1: title
                "'CNNAME' TEXT," + // 2: cnname
                "'REPORTNAME' TEXT," + // 3: reportname
                "'STUDENTLIST' TEXT," + // 4: studentlist
                "'REPORTTIME' TEXT," + // 5: reporttime
                "'CREATETIME' TEXT," + // 6: createtime
                "'TYPE' TEXT," + // 7: type
                "'ADDUSERID' TEXT," + // 8: adduserid
                "'TEACHERNAME' TEXT," + // 9: teachername
                "'STUDENTLISTID' TEXT," + // 10: studentlistid
                "'STUDENTNAME' TEXT);"); // 11: studentname
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'REPORT_ENTITY'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, ReportEntity entity) {
        stmt.clearBindings();
        stmt.bindString(1, entity.getId());
 
        String title = entity.getTitle();
        if (title != null) {
            stmt.bindString(2, title);
        }
 
        String cnname = entity.getCnname();
        if (cnname != null) {
            stmt.bindString(3, cnname);
        }
 
        String reportname = entity.getReportname();
        if (reportname != null) {
            stmt.bindString(4, reportname);
        }
 
        String studentlist = entity.getStudentlist();
        if (studentlist != null) {
            stmt.bindString(5, studentlist);
        }
 
        String reporttime = entity.getReporttime();
        if (reporttime != null) {
            stmt.bindString(6, reporttime);
        }
 
        String createtime = entity.getCreatetime();
        if (createtime != null) {
            stmt.bindString(7, createtime);
        }
 
        String type = entity.getType();
        if (type != null) {
            stmt.bindString(8, type);
        }
 
        String adduserid = entity.getAdduserid();
        if (adduserid != null) {
            stmt.bindString(9, adduserid);
        }
 
        String teachername = entity.getTeachername();
        if (teachername != null) {
            stmt.bindString(10, teachername);
        }
 
        String studentlistid = entity.getStudentlistid();
        if (studentlistid != null) {
            stmt.bindString(11, studentlistid);
        }
 
        String studentname = entity.getStudentname();
        if (studentname != null) {
            stmt.bindString(12, studentname);
        }
    }

    @Override
    protected void attachEntity(ReportEntity entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    /** @inheritdoc */
    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.getString(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public ReportEntity readEntity(Cursor cursor, int offset) {
        ReportEntity entity = new ReportEntity( //
            cursor.getString(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // title
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // cnname
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // reportname
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // studentlist
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // reporttime
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // createtime
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // type
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // adduserid
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // teachername
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // studentlistid
            cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11) // studentname
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, ReportEntity entity, int offset) {
        entity.setId(cursor.getString(offset + 0));
        entity.setTitle(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setCnname(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setReportname(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setStudentlist(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setReporttime(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setCreatetime(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setType(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setAdduserid(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setTeachername(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setStudentlistid(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setStudentname(cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11));
     }
    
    /** @inheritdoc */
    @Override
    protected String updateKeyAfterInsert(ReportEntity entity, long rowId) {
        return entity.getId();
    }
    
    /** @inheritdoc */
    @Override
    public String getKey(ReportEntity entity) {
        if(entity != null) {
            return entity.getId();
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