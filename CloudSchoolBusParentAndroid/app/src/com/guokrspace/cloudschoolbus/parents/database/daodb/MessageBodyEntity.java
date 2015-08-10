package com.guokrspace.cloudschoolbus.parents.database.daodb;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table MESSAGE_BODY_ENTITY.
 */
public class MessageBodyEntity {

    private Long id;
    private String content;
    private String messageid;

    public MessageBodyEntity() {
    }

    public MessageBodyEntity(Long id) {
        this.id = id;
    }

    public MessageBodyEntity(Long id, String content, String messageid) {
        this.id = id;
        this.content = content;
        this.messageid = messageid;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMessageid() {
        return messageid;
    }

    public void setMessageid(String messageid) {
        this.messageid = messageid;
    }

}
