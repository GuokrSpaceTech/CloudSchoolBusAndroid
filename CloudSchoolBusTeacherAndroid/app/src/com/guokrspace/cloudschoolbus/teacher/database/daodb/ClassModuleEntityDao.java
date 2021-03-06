package com.guokrspace.cloudschoolbus.teacher.database.daodb;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.util.List;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;
import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * DAO for table CLASS_MODULE_ENTITY.
*/
public class ClassModuleEntityDao extends AbstractDao<ClassModuleEntity, String> {

    public static final String TABLENAME = "CLASS_MODULE_ENTITY";

    /**
     * Properties of entity ClassModuleEntity.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, String.class, "id", true, "ID");
        public final static Property Icon = new Property(1, String.class, "icon", false, "ICON");
        public final static Property Url = new Property(2, String.class, "url", false, "URL");
        public final static Property Title = new Property(3, String.class, "title", false, "TITLE");
        public final static Property Schoolid = new Property(4, String.class, "schoolid", false, "SCHOOLID");
    };

    private Query<ClassModuleEntity> schoolEntityT_ClassModuleEntityListQuery;

    public ClassModuleEntityDao(DaoConfig config) {
        super(config);
    }
    
    public ClassModuleEntityDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'CLASS_MODULE_ENTITY' (" + //
                "'ID' TEXT PRIMARY KEY NOT NULL ," + // 0: id
                "'ICON' TEXT," + // 1: icon
                "'URL' TEXT," + // 2: url
                "'TITLE' TEXT," + // 3: title
                "'SCHOOLID' TEXT NOT NULL );"); // 4: schoolid
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'CLASS_MODULE_ENTITY'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, ClassModuleEntity entity) {
        stmt.clearBindings();
        stmt.bindString(1, entity.getId());
 
        String icon = entity.getIcon();
        if (icon != null) {
            stmt.bindString(2, icon);
        }
 
        String url = entity.getUrl();
        if (url != null) {
            stmt.bindString(3, url);
        }
 
        String title = entity.getTitle();
        if (title != null) {
            stmt.bindString(4, title);
        }
        stmt.bindString(5, entity.getSchoolid());
    }

    /** @inheritdoc */
    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.getString(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public ClassModuleEntity readEntity(Cursor cursor, int offset) {
        ClassModuleEntity entity = new ClassModuleEntity( //
            cursor.getString(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // icon
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // url
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // title
            cursor.getString(offset + 4) // schoolid
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, ClassModuleEntity entity, int offset) {
        entity.setId(cursor.getString(offset + 0));
        entity.setIcon(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setUrl(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setTitle(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setSchoolid(cursor.getString(offset + 4));
     }
    
    /** @inheritdoc */
    @Override
    protected String updateKeyAfterInsert(ClassModuleEntity entity, long rowId) {
        return entity.getId();
    }
    
    /** @inheritdoc */
    @Override
    public String getKey(ClassModuleEntity entity) {
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
    
    /** Internal query to resolve the "classModuleEntityList" to-many relationship of SchoolEntityT. */
    public List<ClassModuleEntity> _querySchoolEntityT_ClassModuleEntityList(String schoolid) {
        synchronized (this) {
            if (schoolEntityT_ClassModuleEntityListQuery == null) {
                QueryBuilder<ClassModuleEntity> queryBuilder = queryBuilder();
                queryBuilder.where(Properties.Schoolid.eq(null));
                schoolEntityT_ClassModuleEntityListQuery = queryBuilder.build();
            }
        }
        Query<ClassModuleEntity> query = schoolEntityT_ClassModuleEntityListQuery.forCurrentThread();
        query.setParameter(0, schoolid);
        return query.list();
    }

}
