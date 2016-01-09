package com.guokrspace.cloudschoolbus.teacher.base;

import com.guokrspace.cloudschoolbus.teacher.CloudSchoolBusParentsApplication;
import com.guokrspace.cloudschoolbus.teacher.database.daodb.ClassEntityT;
import com.guokrspace.cloudschoolbus.teacher.database.daodb.ClassModuleEntity;
import com.guokrspace.cloudschoolbus.teacher.database.daodb.ConfigEntity;
import com.guokrspace.cloudschoolbus.teacher.database.daodb.ConfigEntityDao;
import com.guokrspace.cloudschoolbus.teacher.database.daodb.ParentEntityT;
import com.guokrspace.cloudschoolbus.teacher.database.daodb.SchoolEntityT;
import com.guokrspace.cloudschoolbus.teacher.database.daodb.StudentClassRelationEntity;
import com.guokrspace.cloudschoolbus.teacher.database.daodb.StudentEntityT;
import com.guokrspace.cloudschoolbus.teacher.database.daodb.StudentParentRelationEntity;
import com.guokrspace.cloudschoolbus.teacher.database.daodb.TeacherDutyClassRelationEntity;
import com.guokrspace.cloudschoolbus.teacher.database.daodb.TeacherEntityT;
import com.guokrspace.cloudschoolbus.teacher.event.BusProvider;
import com.guokrspace.cloudschoolbus.teacher.event.InfoSwitchedEvent;
import com.guokrspace.cloudschoolbus.teacher.module.classes.ClassFragment;

import java.util.ArrayList;

/**
 * Created by kai on 10/8/15.
 */
public class DataWrapper {
    private static final DataWrapper DATA_WRAPPER = new DataWrapper();
    private static CloudSchoolBusParentsApplication mApplication=null;


    public static DataWrapper getInstance() {
        return DATA_WRAPPER;
    }

    public DataWrapper() {
    }

    public void init(CloudSchoolBusParentsApplication application)
    {
        mApplication = application;
    }

    public TeacherEntityT getMyself()
    {
        TeacherEntityT entity = null;
        for(TeacherEntityT teacher:mApplication.mTeachersT)
        {
            if(teacher.getTeacherid().equals(mApplication.mConfig.getUserid())) {
                entity = teacher;
                break;
            }
        }
        return entity;
    }

    public ClassEntityT findCurrentClass(int current)
    {
        ClassEntityT retEntity=null;

        if(mApplication.mClassesT.size()>current)
        {
            retEntity = mApplication.mClassesT.get(current);
        }

        return retEntity;
    }

    public ClassEntityT findCurrentClass()
    {
        ClassEntityT retEntity=null;

        int current = mApplication.mConfig.getCurrentuser();

        if(mApplication.mClassesT.size()>current)
        {
            retEntity = mApplication.mClassesT.get(current);
        }

        return retEntity;
    }

    public ArrayList<ClassEntityT> findMyClass()
    {
        ArrayList<ClassEntityT> retEntity= new ArrayList<ClassEntityT>();

        for(ClassEntityT theClass: mApplication.mClassesT)
        {
            for(TeacherDutyClassRelationEntity relation:mApplication.mTeacherClassDutys) {
                if (theClass.getClassid().equals(relation.getClassid()) && mApplication.mConfig.getUserid().equals(relation.getTeacherid())) {
                    retEntity.add(theClass);
                }
            }
        }

        return retEntity;
    }

    public ArrayList<ParentEntityT> findParentsinClass(String classid)
    {
        ArrayList<ParentEntityT> retParents = new ArrayList<>();
        ArrayList<StudentEntityT> retStudents = new ArrayList<>();
        for(StudentEntityT student:mApplication.mStudentsT)
        {
            for(StudentClassRelationEntity relation:mApplication.mStudentClasses) {
                if(relation.getClassid().equals(classid)) {
                    if (student.getStudentid().equals(relation.getStudentid()))
                    {
                        //Found the student
                        retStudents.add(student);
                        break;
                    }
                }
            }
        }

        for(StudentEntityT student:retStudents){
            for(StudentParentRelationEntity relation:mApplication.mStudentParents)
            {
                if(student.getStudentid().equals(relation.getStudentid()))
                {
                    //find the parentid, get the entity
                    for(ParentEntityT parent:mApplication.mParents)
                    {
                        if(parent.getParentid().equals(relation.getParentid()))
                        {
                            //Some parents may have multiple kids
                            if(!retParents.contains(parent))
                                retParents.add(parent);
                        }
                    }
                }
            }
        }

        return retParents;
    }


    public ArrayList<StudentEntityT> findStudentsinClass(String classid)
    {
        ArrayList<StudentEntityT> retStudents = new ArrayList<>();
        for(StudentEntityT student:mApplication.mStudentsT)
        {
            for(StudentClassRelationEntity relation:mApplication.mStudentClasses) {
                if(relation.getClassid().equals(classid)) {
                    if (student.getStudentid().equals(relation.getStudentid())) {
                        //Found the student, then find the parents
                        retStudents.add(student);
                        break;
                    }
                }
            }
        }

        return retStudents;
    }


    public ArrayList<TeacherEntityT> findTeachersinClass(String classid)
    {
        ArrayList<TeacherEntityT> retTeachers = new ArrayList<>();
        for(TeacherEntityT teacher:mApplication.mTeachersT) {
            for (TeacherDutyClassRelationEntity relation : mApplication.mTeacherClassDutys) {
                if (relation.getClassid().equals(classid)) {
                    if (teacher.getTeacherid().equals(relation.getTeacherid())) {
                        //Found the student, then find the parents
                        retTeachers.add(teacher);
                        break;
                    }
                }
            }
        }

        return retTeachers;
    }

    public ArrayList<StudentEntityT> findStudentsOfParents(ParentEntityT parent)
    {
        ArrayList<StudentEntityT> retStudents = new ArrayList<>();
        //Loop over students list
        for(StudentEntityT student:mApplication.mStudentsT)
        {
            //Loop Over the relationship table
            for(StudentParentRelationEntity relation:mApplication.mStudentParents)
            {
                //If found the student in the relation table and its parent is eqaul to the designated parent
                if(student.getStudentid().equals(relation.getStudentid()) && parent.getParentid().equals(relation.getParentid()))
                {
                    retStudents.add(student);
                }
            }
        }
        return retStudents;
    }

    public ArrayList<StudentEntityT> findWhichStudentsInClass(ArrayList<StudentEntityT> students, String classid)
    {
        ArrayList<StudentEntityT> retStudents = new ArrayList<>();
        ArrayList<StudentEntityT> class_students = findStudentsinClass(classid);
        for(StudentEntityT student:students) {
            if (class_students.contains(student))
            {
                retStudents.add(student);
            }
        }

        return retStudents;
    }

    public ArrayList<StudentEntityT> findStudentInCurrentClassForParent(ParentEntityT parent, String classid)
    {
        ArrayList<StudentEntityT> students = DataWrapper.getInstance().findStudentsOfParents(parent);

        return findWhichStudentsInClass(students, classid);
    }

    public SchoolEntityT findSchool(String schoolid)
    {
        SchoolEntityT school = null;
        for(SchoolEntityT schoolEntityT:mApplication.mSchoolsT)
        {
            if(schoolEntityT.getId().equals(schoolid)) {
                school = schoolEntityT;
            }
        }
        return school;
    }

    public SchoolEntityT findCurrentSchool()
    {
        ClassEntityT currentClass = DataWrapper.getInstance().findCurrentClass();

        if(currentClass!=null) {
            SchoolEntityT school = DataWrapper.getInstance().findSchool(currentClass.getSchoolid());
            if (school != null)
                return school;
        }

        return null;
    }

    public ArrayList<ClassModuleEntity> findClassModulesofCurrentSchool()
    {

        ArrayList<ClassModuleEntity> classModuleEntityArrayList = new ArrayList<>();

        SchoolEntityT currentSchool = findCurrentSchool();

        for(ClassModuleEntity module : mApplication.mClassModules)
        {
            if(module.getSchoolid().equals(currentSchool.getId()))
            {
                classModuleEntityArrayList.add(module);
            }
        }

        return classModuleEntityArrayList;
    }
}
