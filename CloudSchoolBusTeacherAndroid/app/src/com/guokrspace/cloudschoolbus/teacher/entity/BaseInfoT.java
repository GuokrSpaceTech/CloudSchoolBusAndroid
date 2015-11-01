package com.guokrspace.cloudschoolbus.teacher.entity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * Created by kai on 9/17/15.
 */
public class BaseInfoT implements Serializable{
    HashMap<String, School> schools;
    List<ClassinfoT> classes;
    List<TeacherT> teachers;
    List<StudentT> students;
    List<Parent>   parents;

    public HashMap<String, School> getSchools() {
        return schools;
    }

    public void setSchools(HashMap<String, School> schools) {
        this.schools = schools;
    }

    public List<ClassinfoT> getClasses() {
        return classes;
    }

    public void setClasses(List<ClassinfoT> classes) {
        this.classes = classes;
    }

    public List<TeacherT> getTeachers() {
        return teachers;
    }

    public void setTeachers(List<TeacherT> teachers) {
        this.teachers = teachers;
    }
//

    public List<StudentT> getStudents() {
        return students;
    }

    public void setStudents(List<StudentT> students) {
        this.students = students;
    }

    public List<Parent> getParents() {
        return parents;
    }

    public void setParents(List<Parent> parents) {
        this.parents = parents;
    }
}

