package com.guokrspace.cloudschoolbus.teacher.database.daodb;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;
import de.greenrobot.dao.internal.SqlUtils;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * DAO for table MESSAGE_ENTITY.
*/
public class MessageEntityDao extends AbstractDao<MessageEntity, String> {

    public static final String TABLENAME = "MESSAGE_ENTITY";

    /**
     * Properties of entity MessageEntity.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Messageid = new Property(0, String.class, "messageid", true, "MESSAGEID");
        public final static Property Title = new Property(1, String.class, "title", false, "TITLE");
        public final static Property Description = new Property(2, String.class, "description", false, "DESCRIPTION");
        public final static Property Isconfirm = new Property(3, String.class, "isconfirm", false, "ISCONFIRM");
        public final static Property Sendtime = new Property(4, String.class, "sendtime", false, "SENDTIME");
        public final static Property Apptype = new Property(5, String.class, "apptype", false, "APPTYPE");
        public final static Property Studentid = new Property(6, String.class, "studentid", false, "STUDENTID");
        public final static Property Ismass = new Property(7, String.class, "ismass", false, "ISMASS");
        public final static Property Isreaded = new Property(8, String.class, "isreaded", false, "ISREADED");
        public final static Property Body = new Property(9, String.class, "body", false, "BODY");
        public final static Property Tagids = new Property(10, String.class, "tagids", false, "TAGIDS");
        public final static Property Senderid = new Property(11, String.class, "senderid", false, "SENDERID");
    };

    private DaoSession daoSession;


    public MessageEntityDao(DaoConfig config) {
        super(config);
    }
    
    public MessageEntityDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'MESSAGE_ENTITY' (" + //
                "'MESSAGEID' TEXT PRIMARY KEY NOT NULL ," + // 0: messageid
                "'TITLE' TEXT," + // 1: title
                "'DESCRIPTION' TEXT," + // 2: description
                "'ISCONFIRM' TEXT," + // 3: isconfirm
                "'SENDTIME' TEXT," + // 4: sendtime
                "'APPTYPE' TEXT," + // 5: apptype
                "'STUDENTID' TEXT," + // 6: studentid
                "'ISMASS' TEXT," + // 7: ismass
                "'ISREADED' TEXT," + // 8: isreaded
                "'BODY' TEXT," + // 9: body
                "'TAGIDS' TEXT," + // 10: tagids
                "'SENDERID' TEXT);"); // 11: senderid
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'MESSAGE_ENTITY'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, MessageEntity entity) {
        stmt.clearBindings();
        stmt.bindString(1, entity.getMessageid());
 
        String title = entity.getTitle();
        if (title != null) {
            stmt.bindString(2, title);
        }
 
        String description = entity.getDescription();
        if (description != null) {
            stmt.bindString(3, description);
        }
 
        String isconfirm = entity.getIsconfirm();
        if (isconfirm != null) {
            stmt.bindString(4, isconfirm);
        }
 
        String sendtime = entity.getSendtime();
        if (sendtime != null) {
            stmt.bindString(5, sendtime);
        }
 
        String apptype = entity.getApptype();
        if (apptype != null) {
            stmt.bindString(6, apptype);
        }
 
        String studentid = entity.getStudentid();
        if (studentid != null) {
            stmt.bindString(7, studentid);
        }
 
        String ismass = entity.getIsmass();
        if (ismass != null) {
            stmt.bindString(8, ismass);
        }
 
        String isreaded = entity.getIsreaded();
        if (isreaded != null) {
            stmt.bindString(9, isreaded);
        }
 
        String body = entity.getBody();
        if (body != null) {
            stmt.bindString(10, body);
        }
 
        String tagids = entity.getTagids();
        if (tagids != null) {
            stmt.bindString(11, tagids);
        }
 
        String senderid = entity.getSenderid();
        if (senderid != null) {
            stmt.bindString(12, senderid);
        }
    }

    @Override
    protected void attachEntity(MessageEntity entity) {
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
    public MessageEntity readEntity(Cursor cursor, int offset) {
        MessageEntity entity = new MessageEntity( //
            cursor.getString(offset + 0), // messageid
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // title
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // description
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // isconfirm
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // sendtime
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // apptype
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // studentid
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // ismass
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // isreaded
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // body
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // tagids
            cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11) // senderid
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, MessageEntity entity, int offset) {
        entity.setMessageid(cursor.getString(offset + 0));
        entity.setTitle(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setDescription(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setIsconfirm(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setSendtime(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setApptype(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setStudentid(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setIsmass(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setIsreaded(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setBody(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setTagids(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setSenderid(cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11));
     }
    
    /** @inheritdoc */
    @Override
    protected String updateKeyAfterInsert(MessageEntity entity, long rowId) {
        return entity.getMessageid();
    }
    
    /** @inheritdoc */
    @Override
    public String getKey(MessageEntity entity) {
        if(entity != null) {
            return entity.getMessageid();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
    private String selectDeep;

    protected String getSelectDeep() {
        if (selectDeep == null) {
            StringBuilder builder = new StringBuilder("SELECT ");
            SqlUtils.appendColumns(builder, "T", getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T0", daoSession.getSenderEntityDao().getAllColumns());
            builder.append(" FROM MESSAGE_ENTITY T");
            builder.append(" LEFT JOIN SENDER_ENTITY T0 ON T.'SENDERID'=T0.'ID'");
            builder.append(' ');
            selectDeep = builder.toString();
        }
        return selectDeep;
    }
    
    protected MessageEntity loadCurrentDeep(Cursor cursor, boolean lock) {
        MessageEntity entity = loadCurrent(cursor, 0, lock);
        int offset = getAllColumns().length;

        SenderEntity senderEntity = loadCurrentOther(daoSession.getSenderEntityDao(), cursor, offset);
        entity.setSenderEntity(senderEntity);

        return entity;    
    }

    public MessageEntity loadDeep(Long key) {
        assertSinglePk();
        if (key == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder(getSelectDeep());
        builder.append("WHERE ");
        SqlUtils.appendColumnsEqValue(builder, "T", getPkColumns());
        String sql = builder.toString();
        
        String[] keyArray = new String[] { key.toString() };
        Cursor cursor = db.rawQuery(sql, keyArray);
        
        try {
            boolean available = cursor.moveToFirst();
            if (!available) {
                return null;
            } else if (!cursor.isLast()) {
                throw new IllegalStateException("Expected unique result, but count was " + cursor.getCount());
            }
            return loadCurrentDeep(cursor, true);
        } finally {
            cursor.close();
        }
    }
    
    /** Reads all available rows from the given cursor and returns a list of new ImageTO objects. */
    public List<MessageEntity> loadAllDeepFromCursor(Cursor cursor) {
        int count = cursor.getCount();
        List<MessageEntity> list = new ArrayList<MessageEntity>(count);
        
        if (cursor.moveToFirst()) {
            if (identityScope != null) {
                identityScope.lock();
                identityScope.reserveRoom(count);
            }
            try {
                do {
                    list.add(loadCurrentDeep(cursor, false));
                } while (cursor.moveToNext());
            } finally {
                if (identityScope != null) {
                    identityScope.unlock();
                }
            }
        }
        return list;
    }
    
    protected List<MessageEntity> loadDeepAllAndCloseCursor(Cursor cursor) {
        try {
            return loadAllDeepFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }
    

    /** A raw-style query where you can pass any WHERE clause and arguments. */
    public List<MessageEntity> queryDeep(String where, String... selectionArg) {
        Cursor cursor = db.rawQuery(getSelectDeep() + where, selectionArg);
        return loadDeepAllAndCloseCursor(cursor);
    }
 
}
