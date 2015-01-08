package com.Manga.Activity.Entity;

import java.io.Serializable;

/**
 * Created by kai on 12/25/14.
 */
public class Teacher implements Serializable {
    private String teacherid;
    private String teachername;

    public String getTeacherid() {
        return teacherid;
    }

    public void setTeacherid(String teacherid) {
        this.teacherid = teacherid;
    }

    public String getTeachername() {
        return teachername;
    }

    public void setTeachername(String teachername) {
        this.teachername = teachername;
    }
}
