package com.guokrspace.cloudschoolbus.parents;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.avast.android.dialogs.fragment.SimpleDialogFragment;
import com.guokrspace.cloudschoolbus.parents.base.activity.BaseActivity;
import com.guokrspace.cloudschoolbus.parents.base.fastjson.FastJsonTools;
import com.guokrspace.cloudschoolbus.parents.database.daodb.ClassEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.ClassEntityDao;
import com.guokrspace.cloudschoolbus.parents.database.daodb.ConfigEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.ConfigEntityDao;
import com.guokrspace.cloudschoolbus.parents.database.daodb.SchoolEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.SchoolEntityDao;
import com.guokrspace.cloudschoolbus.parents.database.daodb.StudentEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.StudentEntityDao;
import com.guokrspace.cloudschoolbus.parents.database.daodb.TeacherEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.TeacherEntityDao;
import com.guokrspace.cloudschoolbus.parents.entity.Baseinfo;

import com.guokrspace.cloudschoolbus.parents.entity.Classinfo;
import com.guokrspace.cloudschoolbus.parents.entity.Student;
import com.guokrspace.cloudschoolbus.parents.entity.Teacher;
import com.guokrspace.cloudschoolbus.parents.event.NetworkStatusEvent;
import com.guokrspace.cloudschoolbus.parents.protocols.CloudSchoolBusRestClient;
import com.guokrspace.cloudschoolbus.parents.protocols.ProtocolDef;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.otto.Subscribe;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivity {

    // UI references.
    private EditText mMobileNumberView;
    private EditText mVerifyCodeView;
    private Button   mVerifySignButton;
    private View     mProgressView;
    private View     mLoginFormView;

    private String mobile;
    private String verifyCode;
    private String sid;
    private String loginToken;

    private static final int LOGIN_FAILED = 3;
    private static final int MSG_NO_NETOWRK   = 4;

    private static final int MSG_REG_OK   = 6;
    private static final int MSG_REG_FAIL   = 7;

    private static final int MSG_VERIFY_OK = 8;
    private static final int MSG_VERIFY_FAIL = 9;

    private static final int MSG_BASEINFO_OK = 10;
    private static final int MSG_BASEINFO_FAIL = 11;

    private static final int MSG_RENEWSID_OK = 14;
    private static final int MSG_RENEWSID_FAIL = 15;

    private static final int RESULT_FAIL = -1;
    private static final int RESULT_OK = 0;
    private static final int REQUEST_CODE = 1;

    private static final int REQUEST_LIST_SIMPLE = 9;
    private static final int REQUEST_LIST_MULTIPLE = 10;
    private static final int REQUEST_LIST_SINGLE = 11;
    private static final int REQUEST_DATE_PICKER = 12;
    private static final int REQUEST_TIME_PICKER = 13;
    private static final int REQUEST_SIMPLE_DIALOG = 42;

    private Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_REG_OK:
                    showProgress(false);
                    mVerifyCodeView.setVisibility(View.VISIBLE);
                    mVerifyCodeView.setHint(getResources().getString(R.string.input_verify_code));
                    mVerifyCodeView.requestFocus();
                    mMobileNumberView.setEnabled(false);
                    mVerifySignButton.setText(getResources().getString(R.string.input_verify_code));
                    mVerifySignButton.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            attempVerify();
                        }
                    });
                    break;
                case MSG_REG_FAIL:
                    showProgress(false);
                    SimpleDialogFragment.createBuilder(mContext, getSupportFragmentManager())
                            .setMessage(getResources().getString(R.string.invalid_mobile))
                            .setPositiveButtonText(getResources().getString(R.string.OKAY)).show();
                    showProgress(false);
                    attemptRegister();
                    break;
                //Get Session ID
                case MSG_VERIFY_OK:
                    mApplication.mConfig = new ConfigEntity(null,sid,loginToken,mobile);
                    ConfigEntityDao configEntityDao = mApplication.mDaoSession.getConfigEntityDao();
                    configEntityDao.insert(mApplication.mConfig);
                    CloudSchoolBusRestClient.updateSessionid(sid);
                    getBaseInfoFromServer() ;
                    break;
                case MSG_VERIFY_FAIL:
                    showProgress(false);
                    SimpleDialogFragment.createBuilder(mContext, getSupportFragmentManager())
                            .setMessage(getResources().getString(R.string.invalid_verify_code))
                            .setPositiveButtonText(getResources().getString(R.string.OKAY)).show();
                    break;
                //Get the base info
                case MSG_BASEINFO_OK:
                    Intent resultIntent = new Intent();
                    setResult(RESULT_OK, resultIntent);
                    finish();
                    break;
                case MSG_BASEINFO_FAIL:
                    showProgress(false);
                    SimpleDialogFragment.createBuilder(mContext, getSupportFragmentManager())
                            .setMessage(getResources().getString(R.string.failure_baseinfo))
                            .setPositiveButtonText(getResources().getString(R.string.OKAY)).show();
                    break;
                case MSG_NO_NETOWRK:
                    showProgress(false);
                    SimpleDialogFragment.createBuilder(mContext, getSupportFragmentManager())
                            .setMessage(getResources().getString(R.string.no_network))
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set up the login form.
        mMobileNumberView = (EditText) findViewById(R.id.mobile);
        mVerifyCodeView = (EditText) findViewById(R.id.verify_code);
        mVerifyCodeView.setVisibility(View.INVISIBLE);

        mVerifySignButton = (Button) findViewById(R.id.sign_in_button);
        mVerifySignButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
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
        mVerifyCodeView.setError(null);

        // Store values at the time of the login attempt.
        mobile = mMobileNumberView.getText().toString();
        verifyCode = mVerifyCodeView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(mobile) && !isMobileNumberValid(mobile)) {
            mVerifyCodeView.setError(getString(R.string.error_invalid_password));
            focusView = mVerifyCodeView;
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
        return mobile.length() > 4;
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

        if(!networkStatusEvent.isNetworkConnected()) {
            handler.sendEmptyMessage(MSG_NO_NETOWRK);
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
                            handler.sendEmptyMessage(MSG_BASEINFO_FAIL);
                            return;
                        }

                        Baseinfo baseinfo = FastJsonTools.getObject(response.toString(), Baseinfo.class);
                        //Save School Information
                        for(int i=0; i<baseinfo.getSchools().size(); i++) {
                            SchoolEntity schoolEntity = new SchoolEntity(baseinfo.getSchools().get(i).getSchoolid(),baseinfo.getSchools().get(i).getSchoolname(), baseinfo.getSchools().get(i).getAddress());
                            schoolEntityDao.insertOrReplace(schoolEntity);
                            //Save Class Information
                            List<Classinfo> classes = baseinfo.getSchools().get(i).getClasses();
                            for(int j=0; j<classes.size(); j++)
                            {
                                Classinfo classinfo = classes.get(j);
                                ClassEntity classEntity = new ClassEntity(classes.get(j).getClassid(),classes.get(j).getClassname(), schoolEntity.getId());
                                classEntityDao.insertOrReplace(classEntity);

                                //Save teacher information
                                List<Teacher> teachers = classes.get(j).getTeacher();
                                for(int k=0; k<teachers.size(); k++)
                                {
                                    TeacherEntity teacherEntity = new TeacherEntity(teachers.get(k).getId(),teachers.get(k).getDuty(),teachers.get(k).getName(), classEntity.getClassid());
                                    teacherEntityDao.insertOrReplace(teacherEntity);
                                }

                                //Students(Student and class are many2many relationship)
                                List<Student> students = baseinfo.getStudents(); //Student list from server
                                List<String>  studentInClass = classinfo.getStudent(); //Studentid in class
                                // if found a student in class, instantiate the student entity for the DB
                                for(int m=0; m<studentInClass.size(); m++)
                                {
                                    String studentid = studentInClass.get(m);
                                    for(int n=0; n<students.size();n++)
                                    {
                                        if(studentid.equals(students.get(n).getStudentid()))
                                        {
                                            StudentEntity studentEntity = new StudentEntity(students.get(n).getCnname(),students.get(n).getBirthday(), students.get(n).getSex(),students.get(n).getAvatar(), students.get(n).getNickname(), studentid, classEntity.getClassid());
                                            studentEntityDao.insertOrReplace(studentEntity);
                                        }
                                    }
                                }
                            }
                        }

                        mApplication.initBaseinfo();

                        handler.sendEmptyMessage(MSG_BASEINFO_OK);
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
                        handler.sendEmptyMessage(MSG_BASEINFO_FAIL);
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                        handler.sendEmptyMessage(MSG_BASEINFO_FAIL);
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        handler.sendEmptyMessage(MSG_BASEINFO_FAIL);
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

        if(!networkStatusEvent.isNetworkConnected()) {
            handler.sendEmptyMessage(MSG_NO_NETOWRK);
            return;
        }

        showProgress(true);

        RequestParams params = new RequestParams();
        params.put("mobile", phonenum);
        CloudSchoolBusRestClient.get("register", params, new JsonHttpResponseHandler() {
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
                    handler.sendEmptyMessage(MSG_REG_FAIL);
                    return;
                } else
                    handler.sendEmptyMessage(MSG_REG_OK);
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
                    handler.sendEmptyMessage(MSG_REG_FAIL);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                handler.sendEmptyMessage(MSG_REG_FAIL);
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                handler.sendEmptyMessage(MSG_REG_FAIL);
                super.onFailure(statusCode, headers, responseString, throwable);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, org.json.JSONArray errorResponse) {
                handler.sendEmptyMessage(MSG_REG_FAIL);
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

        if(!networkStatusEvent.isNetworkConnected()) {
            handler.sendEmptyMessage(MSG_NO_NETOWRK);
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

                if (retCode.equals("1"))
                {
                    try {
                        sid = response.getString("sid");
                        loginToken = response.getString("token");
                    } catch (org.json.JSONException e) {
                        e.printStackTrace();
                    }

                    handler.sendEmptyMessage(MSG_VERIFY_OK);
                } else {
                    handler.sendEmptyMessage(MSG_VERIFY_FAIL);
                }

                return;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                handler.sendEmptyMessage(MSG_VERIFY_FAIL);
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                handler.sendEmptyMessage(LOGIN_FAILED);
                super.onFailure(statusCode, headers, responseString, throwable);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, org.json.JSONArray errorResponse) {
                handler.sendEmptyMessage(LOGIN_FAILED);
                System.out.println(errorResponse);
            }
        });
    }


    @Subscribe public void onNetworkStatusChange(NetworkStatusEvent event)
    {
        networkStatusEvent = event;
    }
}

