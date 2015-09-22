package com.guokrspace.cloudschoolbus.parents;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.avast.android.dialogs.fragment.SimpleDialogFragment;
import com.guokrspace.cloudschoolbus.parents.base.activity.BaseActivity;
import com.android.support.fastjson.FastJsonTools;
import com.guokrspace.cloudschoolbus.parents.base.include.HandlerConstant;
import com.guokrspace.cloudschoolbus.parents.base.include.Version;
import com.guokrspace.cloudschoolbus.parents.database.daodb.ClassEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.ClassEntityDao;
import com.guokrspace.cloudschoolbus.parents.database.daodb.ClassEntityT;
import com.guokrspace.cloudschoolbus.parents.database.daodb.ClassModuleEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.ConfigEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.ConfigEntityDao;
import com.guokrspace.cloudschoolbus.parents.database.daodb.MessageTypeEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.ParentEntityT;
import com.guokrspace.cloudschoolbus.parents.database.daodb.SchoolEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.SchoolEntityDao;
import com.guokrspace.cloudschoolbus.parents.database.daodb.SchoolEntityT;
import com.guokrspace.cloudschoolbus.parents.database.daodb.StudentClassRelationEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.StudentEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.StudentEntityDao;
import com.guokrspace.cloudschoolbus.parents.database.daodb.StudentEntityT;
import com.guokrspace.cloudschoolbus.parents.database.daodb.StudentEntityTDao;
import com.guokrspace.cloudschoolbus.parents.database.daodb.StudentParentRelationEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.TagsEntityT;
import com.guokrspace.cloudschoolbus.parents.database.daodb.TeacherDutyClassRelationEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.TeacherDutyEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.TeacherEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.TeacherEntityDao;
import com.guokrspace.cloudschoolbus.parents.database.daodb.TeacherEntityT;
import com.guokrspace.cloudschoolbus.parents.entity.BaseInfoT;
import com.guokrspace.cloudschoolbus.parents.entity.Baseinfo;

import com.guokrspace.cloudschoolbus.parents.entity.ClassInfo;
import com.guokrspace.cloudschoolbus.parents.entity.ClassinfoT;
import com.guokrspace.cloudschoolbus.parents.entity.Module;
import com.guokrspace.cloudschoolbus.parents.entity.Parent;
import com.guokrspace.cloudschoolbus.parents.entity.School;
import com.guokrspace.cloudschoolbus.parents.entity.Setting;
import com.guokrspace.cloudschoolbus.parents.entity.Student;
import com.guokrspace.cloudschoolbus.parents.entity.StudentT;
import com.guokrspace.cloudschoolbus.parents.entity.TagT;
import com.guokrspace.cloudschoolbus.parents.entity.Teacher;
import com.guokrspace.cloudschoolbus.parents.entity.TeacherClassInfo;
import com.guokrspace.cloudschoolbus.parents.entity.TeacherT;
import com.guokrspace.cloudschoolbus.parents.event.BusProvider;
import com.guokrspace.cloudschoolbus.parents.event.LoginResultEvent;
import com.guokrspace.cloudschoolbus.parents.protocols.CloudSchoolBusRestClient;
import com.guokrspace.cloudschoolbus.parents.protocols.ProtocolDef;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivity {

    // UI references.
    private EditText mMobileNumberView;
    private EditText mVerifyCodeEditText;
    private Button mSigninButton;
    private View     mProgressView;
    private View     mLoginFormView;
    private Button mClicktoGetVerifyCodeButton;
    private TextView mTextViewBrand;
    private TextView mTextViewProduct;

    private String mobile;
    private String verifyCode;
    private String sid;
    private String loginToken;
    private String imToken = "IWb9/EypgQlMEo/W/o3qSLI6ZiT8q7s0UEaMPWY0lMyB3UonaGf0gmlCJbN+zU7OvAaDYa9d8U6xzmBRkFjv+Q==";
    private String userid;

//    private LoginResultEvent loginResultEvent = new LoginResultEvent();

    private Thread  thread;
    private boolean threadStopFlag = false;

    private static final int REQUEST_LIST_SIMPLE = 9;
    private static final int REQUEST_LIST_MULTIPLE = 10;
    private static final int REQUEST_LIST_SINGLE = 11;
    private static final int REQUEST_DATE_PICKER = 12;
    private static final int REQUEST_TIME_PICKER = 13;
    private static final int REQUEST_SIMPLE_DIALOG = 42;

    private Handler handler;
    {
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case HandlerConstant.MSG_REG_OK:
                        showProgress(false);
                        mVerifyCodeEditText.setVisibility(View.VISIBLE);
                        mVerifyCodeEditText.setHint(getResources().getString(R.string.input_verify_code));
                        mVerifyCodeEditText.requestFocus();
                        mMobileNumberView.setEnabled(false);
                        TimerTick(60);
                        mSigninButton.setVisibility(View.VISIBLE);
                        break;
                    case HandlerConstant.MSG_REG_FAIL:
                        showProgress(false);
                        SimpleDialogFragment.createBuilder(mContext, getSupportFragmentManager())
                                .setMessage(getResources().getString(R.string.invalid_mobile))
                                .setPositiveButtonText(getResources().getString(R.string.OKAY)).show();
                        showProgress(false);
                        break;
                    //Get Session ID
                    case HandlerConstant.MSG_VERIFY_OK:
                        mApplication.mConfig = new ConfigEntity(null, sid, loginToken, mobile, userid, imToken, 0);
                        ConfigEntityDao configEntityDao = mApplication.mDaoSession.getConfigEntityDao();
                        configEntityDao.insert(mApplication.mConfig);
                        CloudSchoolBusRestClient.updateSessionid(sid);
                        httpGetTokenSuccess(imToken);
                        getBaseInfoFromServer();
                        break;
                    case HandlerConstant.MSG_VERIFY_FAIL:
                        showProgress(false);
                        threadStopFlag = true;
                        SimpleDialogFragment.createBuilder(mContext, getSupportFragmentManager())
                                .setMessage(getResources().getString(R.string.invalid_verify_code))
                                .setPositiveButtonText(getResources().getString(R.string.OKAY)).show();
                        break;
                    case HandlerConstant.MSG_REG_SMS:
                        showProgress(false);
                        threadStopFlag = true;
                        SimpleDialogFragment.createBuilder(mContext, getSupportFragmentManager())
                                .setMessage(getResources().getString(R.string.SMS_oversend))
                                .setPositiveButtonText(getResources().getString(R.string.OKAY)).show();
                        break;
                    //Get the base info
                    case HandlerConstant.MSG_BASEINFO_OK:
                        BusProvider.getInstance().post(new LoginResultEvent(true));
                        finish();
                        break;
                    case HandlerConstant.MSG_BASEINFO_FAIL:
                        showProgress(false);
                        SimpleDialogFragment.createBuilder(mContext, getSupportFragmentManager())
                                .setMessage(getResources().getString(R.string.failure_baseinfo))
                                .setPositiveButtonText(getResources().getString(R.string.OKAY)).show();
                        break;
                    case HandlerConstant.MSG_NO_NETOWRK:
                        showProgress(false);
                        SimpleDialogFragment.createBuilder(mContext, getSupportFragmentManager())
                                .setMessage(getResources().getString(R.string.no_network))
                                .setPositiveButtonText(getResources().getString(R.string.OKAY)).show();
                        break;
                    case HandlerConstant.MSG_TIMER_TICK:
                        mClicktoGetVerifyCodeButton.setText((String) msg.obj);
                        mClicktoGetVerifyCodeButton.setEnabled(false);
                        break;
                    case HandlerConstant.MSG_TIMER_TIMEOUT:
                        mClicktoGetVerifyCodeButton.setText(getResources().getString(R.string.click_get_verify_code));
                        mClicktoGetVerifyCodeButton.setEnabled(true);
                        break;
                    case HandlerConstant.LOGIN_FAILED:
                        showProgress(false);
                        SimpleDialogFragment.createBuilder(mContext, getSupportFragmentManager())
                                .setMessage(getResources().getString(R.string.failure_unknown))
                                .setPositiveButtonText(getResources().getString(R.string.OKAY)).show();
                        break;
                    default:
                        showProgress(false);
                        SimpleDialogFragment.createBuilder(mContext, getSupportFragmentManager())
                                .setMessage(getResources().getString(R.string.failure_unknown))
                                .setPositiveButtonText(getResources().getString(R.string.OKAY)).show();
                        break;
                }
                return false;
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set up the login form.
        mMobileNumberView = (EditText) findViewById(R.id.mobile);
        mVerifyCodeEditText = (EditText) findViewById(R.id.verify_code);

        mSigninButton = (Button) findViewById(R.id.sign_in_button);

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        mClicktoGetVerifyCodeButton = (Button) findViewById(R.id.sms_verifycode_button);
        mClicktoGetVerifyCodeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });

        mSigninButton = (Button) findViewById(R.id.sign_in_button);
        mSigninButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attempVerify();
            }
        });

        mTextViewProduct = (TextView) findViewById(R.id.textView_product);
        if (Version.PARENT == true)
            mTextViewProduct.setText(Version.productNameParent);
        else
            mTextViewProduct.setText(Version.productNameTeacher);

        mTextViewBrand = (TextView) findViewById(R.id.textView_brand);
        mTextViewBrand.setText(Version.desc);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.login_title_layout);
        View view = getSupportActionBar().getCustomView();
        TextView textView = (TextView) view.findViewById(R.id.abs_layout_titleTextView);
        textView.setText(getResources().getString(R.string.logintitle));
    }

    @Override
    public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptRegister() {

        // Reset errors.
        mMobileNumberView.setError(null);

        // Store values at the time of the login attempt.
        mobile = mMobileNumberView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(mobile) && !isMobileNumberValid(mobile)) {
            mMobileNumberView.setError(getString(R.string.error_incorrect_mobilenumber));
            focusView = mMobileNumberView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            register(mobile);
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attempVerify() {

        // Reset errors.
        mMobileNumberView.setError(null);
        mVerifyCodeEditText.setError(null);

        // Store values at the time of the login attempt.
        mobile = mMobileNumberView.getText().toString();
        verifyCode = mVerifyCodeEditText.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(mobile) && !isMobileNumberValid(mobile)) {
            mVerifyCodeEditText.setError(getString(R.string.error_invalid_password));
            focusView = mVerifyCodeEditText;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            verify(verifyCode, mobile);
        }
    }

    private boolean isVerifyCodeValid(String password) {
        return password.length() > 4;
    }

    private boolean isMobileNumberValid(String mobile) {
        return mobile.length() > 9;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void getBaseInfoFromServer() {

        if(!mApplication.networkStatusEvent.isNetworkConnected()) {
            handler.sendEmptyMessage(HandlerConstant.MSG_NO_NETOWRK);
            return;
        }

        showProgress(true);

        RequestParams params = new RequestParams();

        CloudSchoolBusRestClient.get(ProtocolDef.METHOD_baseinfo, params, new JsonHttpResponseHandler() {

                    SchoolEntityDao schoolEntityDao = mApplication.mDaoSession.getSchoolEntityDao();
                    ClassEntityDao classEntityDao = mApplication.mDaoSession.getClassEntityDao();
                    TeacherEntityDao teacherEntityDao = mApplication.mDaoSession.getTeacherEntityDao();
                    StudentEntityDao studentEntityDao = mApplication.mDaoSession.getStudentEntityDao();

                    public void onSuccess(int statusCode, Header[] headers, org.json.JSONObject response) {
                        String retCode = "";

                        for (int i = 0; i < headers.length; i++) {
                            Header header = headers[i];
                            if ("code".equalsIgnoreCase(header.getName())) {
                                retCode = header.getValue();
                                break;
                            }
                        }

                        if (!retCode.equals("1")) {
                            handler.sendEmptyMessage(HandlerConstant.MSG_BASEINFO_FAIL);
                            return;
                        }

                        Object baseinfo;
                        if(Version.PARENT) {
                             baseinfo = FastJsonTools.getObject(response.toString(), Baseinfo.class);
                        }else{
                             baseinfo = FastJsonTools.getObject(response.toString(), BaseInfoT.class);
                        }

                        //Save School Information
                        if(baseinfo instanceof Baseinfo ) {

                            for (int i = 0; i < ((Baseinfo)baseinfo).getSchools().size(); i++) {
                                SchoolEntity schoolEntity = new SchoolEntity(((Baseinfo)baseinfo).getSchools().get(i).getSchoolid(), ((Baseinfo)baseinfo).getSchools().get(i).getSchoolname(), ((Baseinfo)baseinfo).getSchools().get(i).getAddress());
                                schoolEntityDao.insertOrReplace(schoolEntity);
                                //Save Class Information
                                List<ClassInfo> classes = ((Baseinfo)baseinfo).getSchools().get(i).getClasses();
                                for (int j = 0; j < classes.size(); j++) {
                                    ClassInfo classinfo = classes.get(j);
                                    ClassEntity classEntity = new ClassEntity(classes.get(j).getClassid(), classes.get(j).getClassname(), schoolEntity.getId());
                                    classEntityDao.insertOrReplace(classEntity);

                                    //Save teacher information
                                    List<Teacher> teachers = classes.get(j).getTeacher();
                                    for (int k = 0; k < teachers.size(); k++) {
                                        TeacherEntity teacherEntity = new TeacherEntity(teachers.get(k).getId(), teachers.get(k).getDuty(), "https://assets-cdn.github.com/images/modules/logos_page/GitHub-Mark.png", teachers.get(k).getName(), classEntity.getClassid());
                                        teacherEntityDao.insertOrReplace(teacherEntity);
                                    }

                                    //Students(Student and class are many2many relationship)
                                    List<Student> students = ((Baseinfo)baseinfo).getStudents(); //Student list from server
                                    List<String> studentInClass = classinfo.getStudent(); //Studentid in class
                                    // if found a student in class, instantiate the student entity for the DB
                                    for (int m = 0; m < studentInClass.size(); m++) {
                                        String studentid = studentInClass.get(m);
                                        for (int n = 0; n < students.size(); n++) {
                                            if (studentid.equals(students.get(n).getStudentid())) {
                                                StudentEntity studentEntity = new StudentEntity(students.get(n).getCnname(), students.get(n).getBirthday(), students.get(n).getSex(), students.get(n).getAvatar(), students.get(n).getNickname(), studentid, classEntity.getClassid());
                                                studentEntityDao.insertOrReplace(studentEntity);
                                            }
                                        }
                                    }
                                }
                            }
                        } else if (baseinfo instanceof BaseInfoT) {
                            HashMap<String,School> schoolsmap = ((BaseInfoT)baseinfo).getSchools();
                            Iterator<Map.Entry<String, School>> iterator = schoolsmap.entrySet().iterator();
                            while (iterator.hasNext()) {
                                Map.Entry<String, School> entry = iterator.next();
                                School school = entry.getValue();
                                SchoolEntityT schoolDb = new SchoolEntityT();
                                schoolDb.setId(school.getId());
                                schoolDb.setAddress(school.getAddress());
                                schoolDb.setGroupid(school.getGroupid());
                                schoolDb.setName(school.getName());
                                schoolDb.setRemark(school.getRemark());
                                mApplication.mDaoSession.getSchoolEntityTDao().insertOrReplace(schoolDb);

                                for(TagT tag:school.getTags())
                                {
                                    TagsEntityT tagDb = new TagsEntityT();
                                    tagDb.setTagid(tag.getTagid());
                                    tagDb.setTagname(tag.getTagname());
                                    tagDb.setTagname_en(tag.getTagname_en());
                                    tagDb.setTagnamedesc(tag.getTagnamedesc());
                                    tagDb.setTagnamedesc_en(tag.getTagnamedesc_en());
                                    tagDb.setSchoolid(school.getId());
                                    mApplication.mDaoSession.getTagsEntityTDao().insertOrReplace(tagDb);
                                }

                                Setting setting = school.getSettings();
                                int i=0;
                                for(String messagetype : setting.getMessage_type())
                                {
                                    MessageTypeEntity messageTypeDb = new MessageTypeEntity();
                                    messageTypeDb.setSchoolid(school.getId());
                                    messageTypeDb.setId(Integer.toString(i));
                                    messageTypeDb.setType(messagetype);
                                    i++;
                                    mApplication.mDaoSession.getMessageTypeEntityDao().insertOrReplace(messageTypeDb);
                                }

                                i=0;
                                for(Module classmodule: setting.getClass_module()) {

                                    ClassModuleEntity classModuleDb = new ClassModuleEntity();
                                    classModuleDb.setSchoolid(school.getId());
                                    classModuleDb.setIcon(classmodule.getIcon());
                                    classModuleDb.setTitle(classmodule.getTitle());
                                    classModuleDb.setUrl(classmodule.getUrl());
                                    classModuleDb.setId(Integer.toString(i));
                                    mApplication.mDaoSession.getClassModuleEntityDao().insert(classModuleDb);
                                    i++;
                                }

                                HashMap<String, String> dutymap = setting.getTeacher_duty();
                                Iterator<Map.Entry<String,String>> entryIterator = dutymap.entrySet().iterator();
                                while(entryIterator.hasNext())
                                {
                                    Map.Entry<String,String> entry1 = entryIterator.next();
                                    TeacherDutyEntity teacherDutyEntity = new TeacherDutyEntity();
                                    teacherDutyEntity.setId(entry1.getKey());
                                    teacherDutyEntity.setSchoolid(school.getId());
                                    teacherDutyEntity.setDuty(entry1.getValue());
                                    mApplication.mDaoSession.getTeacherDutyEntityDao().insert(teacherDutyEntity);
                                }
                            } //Finish of Schools

                            List<ClassinfoT> classes = ((BaseInfoT)baseinfo).getClasses();
                            for(ClassinfoT classinfo: classes)
                            {
                                ClassEntityT classDb = new ClassEntityT();
                                classDb.setSchoolid(classinfo.getSchoolid());
                                classDb.setClassid(classinfo.getClassid());
                                classDb.setClassname(classinfo.getClassname());
                                classDb.setRemark(classinfo.getRemark());
                                classDb.setDutyid(classinfo.getDutyid());
                                mApplication.mDaoSession.getClassEntityTDao().insert(classDb);
                            }

                            List<TeacherT> teachers = ((BaseInfoT)baseinfo).getTeachers();
                            for(TeacherT teacher:teachers)
                            {
                                TeacherEntityT teacherDb = new TeacherEntityT();
                                teacherDb.setTeacherid(teacher.getTeacherid());
                                teacherDb.setAvatar(teacher.getAvatar());
                                teacherDb.setDuty(teacher.getDuty());
                                teacherDb.setRealname(teacher.getRealname());
                                teacherDb.setNickname(teacher.getNickname());
                                teacherDb.setSex(teacher.getSex());
                                teacherDb.setMobile(teacher.getMobile());
                                teacherDb.setSchoolid(teacher.getSchoolid());
                                mApplication.mDaoSession.getTeacherEntityTDao().insert(teacherDb);

                                for( TeacherClassInfo teacherClass : teacher.getClasses() )
                                {
                                    TeacherDutyClassRelationEntity relation = new TeacherDutyClassRelationEntity();
                                    relation.setTeacherid(teacher.getTeacherid());
                                    relation.setDutyid(teacherClass.getDutyid());
                                    relation.setClassid(teacherClass.getClassid());
                                    mApplication.mDaoSession.getTeacherDutyClassRelationEntityDao().insert(relation);
                                }
                            }

                            List<StudentT> students = ((BaseInfoT)baseinfo).getStudents();
                            for(StudentT student:students)
                            {
                                StudentEntityT studentDb = new StudentEntityT();
                                studentDb.setStudentid(student.getStudentid());
                                studentDb.setSex(student.getSex());
                                studentDb.setAvatar(student.getAvatar());
                                studentDb.setBirthday(student.getBirthday());
                                studentDb.setCnname(student.getCnname());
                                studentDb.setNikename(student.getNickname());
                                mApplication.mDaoSession.getStudentEntityTDao().insert(studentDb);

                                for(String classid:student.getClassids())
                                {
                                    StudentClassRelationEntity relation = new StudentClassRelationEntity();
                                    relation.setClassid(classid);
                                    relation.setStudentid(student.getStudentid());
                                    mApplication.mDaoSession.getStudentClassRelationEntityDao().insert(relation);
                                }
                            }

                            List<Parent> parents = ((BaseInfoT)baseinfo).getParents();
                            for(Parent parent:parents)
                            {
                                ParentEntityT parentDb = new ParentEntityT();
                                parentDb.setNikename(parent.getNickname());
                                parentDb.setAvatar(parent.getAvatar());
                                parentDb.setMobile(parent.getMobile());
                                parentDb.setParentid(parent.getParentid());
                                parentDb.setRelationship(parent.getRelationship());
                                mApplication.mDaoSession.getParentEntityTDao().insert(parentDb);

                                for(String studentid:parent.getStudentids()) {
                                    StudentParentRelationEntity relation = new StudentParentRelationEntity();
                                    relation.setStudentid(studentid);
                                    relation.setParentid(parent.getParentid());
                                    mApplication.mDaoSession.getStudentParentRelationEntityDao().insert(relation);
                                }
                            }
                        }

                        mApplication.initBaseinfo();

                        handler.sendEmptyMessage(HandlerConstant.MSG_BASEINFO_OK);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        super.onSuccess(statusCode, headers, response);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        super.onSuccess(statusCode, headers, responseString);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        handler.sendEmptyMessage(HandlerConstant.MSG_BASEINFO_FAIL);
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                        handler.sendEmptyMessage(HandlerConstant.MSG_BASEINFO_FAIL);
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        handler.sendEmptyMessage(HandlerConstant.MSG_BASEINFO_FAIL);
                        super.onFailure(statusCode, headers, responseString, throwable);
                    }
                }
        );
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public void register(String phonenum) {

        if(!mApplication.networkStatusEvent.isNetworkConnected()) {
            handler.sendEmptyMessage(HandlerConstant.MSG_NO_NETOWRK);
            return;
        }

        showProgress(true);

        RequestParams params = new RequestParams();
        params.put("mobile", phonenum);
        CloudSchoolBusRestClient.get(ProtocolDef.METHOD_register, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, org.json.JSONArray response) {

                String retCode = "";

                for (int i = 0; i < headers.length; i++) {
                    Header header = headers[i];
                    if ("code".equalsIgnoreCase(header.getName())) {
                        retCode = header.getValue();
                        break;
                    }
                }

                if (!retCode.equals("1")) {
                    handler.sendEmptyMessage(HandlerConstant.MSG_REG_FAIL);
                    return;
                } else
                    handler.sendEmptyMessage(HandlerConstant.MSG_REG_OK);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                String retCode = "";

                for (int i = 0; i < headers.length; i++) {
                    Header header = headers[i];
                    if ("code".equalsIgnoreCase(header.getName())) {
                        retCode = header.getValue();
                        break;
                    }
                }

                if(retCode.equals("-1117"))
                {
                    handler.sendEmptyMessage(HandlerConstant.MSG_REG_FAIL);
                } else if(retCode.equals("-1120")) {
                    handler.sendEmptyMessage(HandlerConstant.MSG_REG_SMS);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                handler.sendEmptyMessage(HandlerConstant.MSG_REG_FAIL);
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                handler.sendEmptyMessage(HandlerConstant.MSG_REG_FAIL);
                super.onFailure(statusCode, headers, responseString, throwable);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, org.json.JSONArray errorResponse) {
                handler.sendEmptyMessage(HandlerConstant.MSG_REG_FAIL);
                System.out.println(errorResponse);
            }
        });
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public void verify(String verify_code, String mobile) {


        ConfigEntityDao configEntityDao = mApplication.mDaoSession.getConfigEntityDao();

        if(!mApplication.networkStatusEvent.isNetworkConnected()) {
            handler.sendEmptyMessage(HandlerConstant.MSG_NO_NETOWRK);
            return;
        }

        showProgress(true);

        RequestParams params = new RequestParams();
        params.put("verifycode", verify_code);
        params.put("mobile",mobile);
        CloudSchoolBusRestClient.post("verify", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, org.json.JSONArray response) {
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                String retCode = "";

                for (int i = 0; i < headers.length; i++) {
                    Header header = headers[i];
                    if ("code".equalsIgnoreCase(header.getName())) {
                        retCode = header.getValue();
                        break;
                    }
                }

                if (retCode.equals("1")) {
                    try {
                        sid = response.getString("sid");
                        loginToken = response.getString("token");
                        userid = response.getString("userid");
                        if (response.getString("rongtoken") != null || !response.getString("rongtoken").isEmpty())
                            imToken = response.getString("rongtoken");
                    } catch (org.json.JSONException e) {
                        e.printStackTrace();
                    }

                    handler.sendEmptyMessage(HandlerConstant.MSG_VERIFY_OK);
                } else {
                    handler.sendEmptyMessage(HandlerConstant.MSG_VERIFY_FAIL);
                }

                return;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                handler.sendEmptyMessage(HandlerConstant.MSG_VERIFY_FAIL);
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                handler.sendEmptyMessage(HandlerConstant.LOGIN_FAILED);
                super.onFailure(statusCode, headers, responseString, throwable);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, org.json.JSONArray errorResponse) {
                handler.sendEmptyMessage(HandlerConstant.LOGIN_FAILED);
                System.out.println(errorResponse);
            }
        });
    }

    private void TimerTick(final int max_seconds) {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                int seconds_left = max_seconds;
                while (seconds_left > 0 && !threadStopFlag) {
                    seconds_left--;
                    handler.sendMessage(handler.obtainMessage(HandlerConstant.MSG_TIMER_TICK, seconds_left + getResources().getString(R.string.seconds)));
                    try {
                        thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                handler.sendEmptyMessage(HandlerConstant.MSG_TIMER_TIMEOUT);
            }
        });
        if (!thread.isAlive()) {
            thread.start();
        }
    }

    private void httpGetTokenSuccess(String token) {

    /* IMKit SDK调用第二步 建立与服务器的连接 */

    /* 集成和测试阶段，您可以直接使用从您开发者后台获取到的 token，比如 String token = “d6bCQsXiupB......”; */
        RongIM.connect(token, new RongIMClient.ConnectCallback() {
            @Override
            public void onSuccess(String userId) {
            /* 连接成功 */
            }

            @Override
            public void onError(RongIMClient.ErrorCode e) {
            /* 连接失败，注意并不需要您做重连 */
            }

            @Override
            public void onTokenIncorrect() {
            /* Token 错误，在线上环境下主要是因为 Token 已经过期，您需要向 App Server 重新请求一个新的 Token */
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                return true;
            default:
                return super.onKeyDown(keyCode, event);
        }
    }
}

