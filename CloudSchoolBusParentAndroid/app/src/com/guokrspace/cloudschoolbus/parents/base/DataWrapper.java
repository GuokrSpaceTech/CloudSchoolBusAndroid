package com.guokrspace.cloudschoolbus.parents.base;

import com.guokrspace.cloudschoolbus.parents.CloudSchoolBusParentsApplication;
import com.guokrspace.cloudschoolbus.parents.database.daodb.ClassEntityT;
import com.guokrspace.cloudschoolbus.parents.database.daodb.ConfigEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.ConfigEntityDao;
import com.guokrspace.cloudschoolbus.parents.database.daodb.ParentEntityT;
import com.guokrspace.cloudschoolbus.parents.database.daodb.StudentClassRelationEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.StudentEntityT;
import com.guokrspace.cloudschoolbus.parents.database.daodb.StudentParentRelationEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.TeacherDutyClassRelationEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.TeacherEntityT;
import com.guokrspace.cloudschoolbus.parents.event.BusProvider;
import com.guokrspace.cloudschoolbus.parents.event.InfoSwitchedEvent;

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

    public void switchChildren(int currentChild)
    {
        ConfigEntityDao configEntityDao = mApplication.mDaoSession.getConfigEntityDao();
        ConfigEntity oldConfigEntity = configEntityDao.queryBuilder().limit(1).list().get(0);
        oldConfigEntity.setCurrentChild(currentChild);
        ConfigEntity newConfigEntity = oldConfigEntity;
        configEntityDao.update(newConfigEntity);
        mApplication.mConfig = newConfigEntity;

        BusProvider.getInstance().post(new InfoSwitchedEvent(currentChild));
    }

    //This function only works in parent app
    public String findCurrentStudentid()
    {
        int current =  mApplication.mConfig.getCurrentChild();
        String studentId = mApplication.mStudents.get(current).getStudentid();
        return studentId;
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

        String classid = mApplication.mTeacherClassDutys.get(current).getClassid();

        for(ClassEntityT theClass: mApplication.mClassesT)
        {
            if(theClass.getClassid().equals(classid))
            {
                retEntity = theClass; break;
            }
        }

        return retEntity;
    }

    public ClassEntityT findCurrentClass()
    {
        ClassEntityT retEntity=null;

        int current = mApplication.mConfig.getCurrentChild();

        String classid = mApplication.mTeacherClassDutys.get(current).getClassid();

        for(ClassEntityT theClass: mApplication.mClassesT)
        {
            if(theClass.getClassid().equals(classid))
            {
                retEntity = theClass; break;
            }
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
                        //Found the student, then find the arents
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
}
