package com.guokrspace.cloudschoolbus.parents.entity;

import java.io.Serializable;
import java.util.List;


//{"schools":
//          [{"classes":
//                      [{"teacher":[{"avatar":"http:\/\/cloud.yunxiaoche.com\/images\/teacher.jpg",
//                                    "id":"1234",
//                                    "duty":"班主任",
//                                    "name":"小美"}],
//                      "student":["39747","39738"],
//                      "classname":"海豚班",
//                      "classid":"123"}],
//            "address":"长沙市岳麓区麓山南路201号，湖大毛主席像后方",
//            "schoolid":"108",
//            "schoolname":"长沙卓越实验幼儿园"}],
// "students":[{"cnname":"王弘毅",
//              "birthday":"2010-10-10",
//              "sex":"1",
//              "avatar":"http:\/\/cloud.yunxiaoche.com\/avatar-tea-stu\/39724_39741.jpg",
//              "nickname":"大顺",
//              "studentid":"39738"}]}


/**
 * Created by kai on 12/25/14.
 */
public class Baseinfo implements Serializable {

    private List<SchoolInfo> schools;
    private List<Group>      groups;
    private List<Student>    students;

    public List<SchoolInfo> getSchools() {
        return schools;
    }

    public void setSchools(List<SchoolInfo> schools) {
        this.schools = schools;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }
}

