package com.guokrspace.cloudschoolbus.teacher.database.daodb;

import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 

/**
 * Entity mapped to table MESSAGE_ENTITY.
 */
public class MessageEntity {

    /** Not-null value. */
    private String messageid;
    private String title;
    private String description;
    private String isconfirm;
    private String sendtime;
    private String apptype;
    private String studentid;
    private String ismass;
    private String isreaded;
    private String body;
    private String tagids;
    private String senderid;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient MessageEntityDao myDao;

    private SenderEntity senderEntity;
    private String senderEntity__resolvedKey;


    public MessageEntity() {
    }

    public MessageEntity(String messageid) {
        this.messageid = messageid;
    }

    public MessageEntity(String messageid, String title, String description, String isconfirm, String sendtime, String apptype, String studentid, String ismass, String isreaded, String body, String tagids, String senderid) {
        this.messageid = messageid;
        this.title = title;
        this.description = description;
        this.isconfirm = isconfirm;
        this.sendtime = sendtime;
        this.apptype = apptype;
        this.studentid = studentid;
        this.ismass = ismass;
        this.isreaded = isreaded;
        this.body = body;
        this.tagids = tagids;
        this.senderid = senderid;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getMessageEntityDao() : null;
    }

    /** Not-null value. */
    public String getMessageid() {
        return messageid;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setMessageid(String messageid) {
        this.messageid = messageid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIsconfirm() {
        return isconfirm;
    }

    public void setIsconfirm(String isconfirm) {
        this.isconfirm = isconfirm;
    }

    public String getSendtime() {
        return sendtime;
    }

    public void setSendtime(String sendtime) {
        this.sendtime = sendtime;
    }

    public String getApptype() {
        return apptype;
    }

    public void setApptype(String apptype) {
        this.apptype = apptype;
    }

    public String getStudentid() {
        return studentid;
    }

    public void setStudentid(String studentid) {
        this.studentid = studentid;
    }

    public String getIsmass() {
        return ismass;
    }

    public void setIsmass(String ismass) {
        this.ismass = ismass;
    }

    public String getIsreaded() {
        return isreaded;
    }

    public void setIsreaded(String isreaded) {
        this.isreaded = isreaded;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTagids() {
        return tagids;
    }

    public void setTagids(String tagids) {
        this.tagids = tagids;
    }

    public String getSenderid() {
        return senderid;
    }

    public void setSenderid(String senderid) {
        this.senderid = senderid;
    }

    /** To-one relationship, resolved on first access. */
    public SenderEntity getSenderEntity() {
        String __key = this.senderid;
        if (senderEntity__resolvedKey == null || senderEntity__resolvedKey != __key) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            SenderEntityDao targetDao = daoSession.getSenderEntityDao();
            SenderEntity senderEntityNew = targetDao.load(__key);
            synchronized (this) {
                senderEntity = senderEntityNew;
            	senderEntity__resolvedKey = __key;
            }
        }
        return senderEntity;
    }

    public void setSenderEntity(SenderEntity senderEntity) {
        synchronized (this) {
            this.senderEntity = senderEntity;
            senderid = senderEntity == null ? null : senderEntity.getId();
            senderEntity__resolvedKey = senderid;
        }
    }

    /** Convenient call for {@link AbstractDao#delete(Object)}. Entity must attached to an entity context. */
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.delete(this);
    }

    /** Convenient call for {@link AbstractDao#update(Object)}. Entity must attached to an entity context. */
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.update(this);
    }

    /** Convenient call for {@link AbstractDao#refresh(Object)}. Entity must attached to an entity context. */
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.refresh(this);
    }

}