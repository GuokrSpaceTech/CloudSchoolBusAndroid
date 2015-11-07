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
 * DAO for table TAGS_ENTITY_T.
*/
public class TagsEntityTDao extends AbstractDao<TagsEntityT, String> {

    public static final String TABLENAME = "TAGS_ENTITY_T";

    /**
     * Properties of entity TagsEntityT.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Tagid = new Property(0, String.class, "tagid", true, "TAGID");
        public final static Property Tagname = new Property(1, String.class, "tagname", false, "TAGNAME");
        public final static Property Tagname_en = new Property(2, String.class, "tagname_en", false, "TAGNAME_EN");
        public final static Property Tagnamedesc = new Property(3, String.class, "tagnamedesc", false, "TAGNAMEDESC");
        public final static Property Tagnamedesc_en = new Property(4, String.class, "tagnamedesc_en", false, "TAGNAMEDESC_EN");
        public final static Property Schoolid = new Property(5, String.class, "schoolid", false, "SCHOOLID");
        public final static Property Pickey = new Property(6, String.class, "pickey", false, "PICKEY");
    };

    private Query<TagsEntityT> schoolEntityT_TagsEntityTListQuery;

    public TagsEntityTDao(DaoConfig config) {
        super(config);
    }
    
    public TagsEntityTDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'TAGS_ENTITY_T' (" + //
                "'TAGID' TEXT PRIMARY KEY NOT NULL ," + // 0: tagid
                "'TAGNAME' TEXT," + // 1: tagname
                "'TAGNAME_EN' TEXT," + // 2: tagname_en
                "'TAGNAMEDESC' TEXT," + // 3: tagnamedesc
                "'TAGNAMEDESC_EN' TEXT," + // 4: tagnamedesc_en
                "'SCHOOLID' TEXT NOT NULL ," + // 5: schoolid
                "'PICKEY' TEXT);"); // 6: pickey
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'TAGS_ENTITY_T'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, TagsEntityT entity) {
        stmt.clearBindings();
 
        String tagid = entity.getTagid();
        if (tagid != null) {
            stmt.bindString(1, tagid);
        }
 
        String tagname = entity.getTagname();
        if (tagname != null) {
            stmt.bindString(2, tagname);
        }
 
        String tagname_en = entity.getTagname_en();
        if (tagname_en != null) {
            stmt.bindString(3, tagname_en);
        }
 
        String tagnamedesc = entity.getTagnamedesc();
        if (tagnamedesc != null) {
            stmt.bindString(4, tagnamedesc);
        }
 
        String tagnamedesc_en = entity.getTagnamedesc_en();
        if (tagnamedesc_en != null) {
            stmt.bindString(5, tagnamedesc_en);
        }
        stmt.bindString(6, entity.getSchoolid());
 
        String pickey = entity.getPickey();
        if (pickey != null) {
            stmt.bindString(7, pickey);
        }
    }

    /** @inheritdoc */
    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public TagsEntityT readEntity(Cursor cursor, int offset) {
        TagsEntityT entity = new TagsEntityT( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // tagid
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // tagname
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // tagname_en
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // tagnamedesc
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // tagnamedesc_en
            cursor.getString(offset + 5), // schoolid
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6) // pickey
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, TagsEntityT entity, int offset) {
        entity.setTagid(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setTagname(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setTagname_en(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setTagnamedesc(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setTagnamedesc_en(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setSchoolid(cursor.getString(offset + 5));
        entity.setPickey(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
     }
    
    /** @inheritdoc */
    @Override
    protected String updateKeyAfterInsert(TagsEntityT entity, long rowId) {
        return entity.getTagid();
    }
    
    /** @inheritdoc */
    @Override
    public String getKey(TagsEntityT entity) {
        if(entity != null) {
            return entity.getTagid();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
    /** Internal query to resolve the "tagsEntityTList" to-many relationship of SchoolEntityT. */
    public List<TagsEntityT> _querySchoolEntityT_TagsEntityTList(String schoolid) {
        synchronized (this) {
            if (schoolEntityT_TagsEntityTListQuery == null) {
                QueryBuilder<TagsEntityT> queryBuilder = queryBuilder();
                queryBuilder.where(Properties.Schoolid.eq(null));
                schoolEntityT_TagsEntityTListQuery = queryBuilder.build();
            }
        }
        Query<TagsEntityT> query = schoolEntityT_TagsEntityTListQuery.forCurrentThread();
        query.setParameter(0, schoolid);
        return query.list();
    }

}
