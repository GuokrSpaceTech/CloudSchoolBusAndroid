package com.guokrspace.cloudschoolbus.teacher.entity;

/**
 * Created by kai on 12/27/14.
 */
public class LetterDto {
    private String letterid;
    private String letter_type;
    private String from_role;
    private String from_id;
    private String to_role;
    private String to_id;
    private String addtime;
    private String content;
    public boolean isShowDate;

    public String getLetterid() {
        return letterid;
    }

    public void setLetterid(String letterid) {
        this.letterid = letterid;
    }

    public String getLetter_type() {
        return letter_type;
    }

    public void setLetter_type(String letter_type) {
        this.letter_type = letter_type;
    }

    public String getFrom_role() {
        return from_role;
    }

    public void setFrom_role(String from_role) {
        this.from_role = from_role;
    }

    public String getFrom_id() {
        return from_id;
    }

    public void setFrom_id(String from_id) {
        this.from_id = from_id;
    }

    public String getTo_role() {
        return to_role;
    }

    public void setTo_role(String to_role) {
        this.to_role = to_role;
    }

    public String getTo_id() {
        return to_id;
    }

    public void setTo_id(String to_id) {
        this.to_id = to_id;
    }

    public String getAddtime() {
        return addtime;
    }

    public void setAddtime(String addtime) {
        this.addtime = addtime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
