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
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONException;
import com.android.support.authcode.ooo;
import com.avast.android.dialogs.fragment.ListDialogFragment;
import com.avast.android.dialogs.fragment.SimpleDialogFragment;
import com.avast.android.dialogs.iface.IListDialogListener;
import com.guokrspace.cloudschoolbus.parents.base.activity.BaseActivity;
import com.guokrspace.cloudschoolbus.parents.base.fastjson.FastJsonTools;
import com.guokrspace.cloudschoolbus.parents.database.daodb.ClassEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.ClassEntityDao;
import com.guokrspace.cloudschoolbus.parents.database.daodb.ConfigEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.ConfigEntityDao;
import com.guokrspace.cloudschoolbus.parents.database.daodb.TeacherEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.TeacherEntityDao;
import com.guokrspace.cloudschoolbus.parents.entity.Baseinfo;
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
public class LoginActivity extends BaseActivity implements IListDialogListener {

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    private short current_student;
    private List<Student> students = null;
    private String username;
    private String password;
    private String sid;

    private static final int CURRENT_STUDENT = 0;
    private static final int GET_SESIONID = 1;
    private static final int GET_BASEINFO = 2;
    private static final int LOGIN_FAILED = 3;
    private static final int NO_NETOWRK   = 4;
    private static final int SERVER_ERROR   = 5;

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
                //Login success, select the current kid
                case CURRENT_STUDENT:
                    unit(); //Ask for sessionid
                    break;
                //Get Session ID
                case GET_SESIONID:
                    //Save key information into Global var and database
                    mApplication.mConfig = new ConfigEntity(null,sid,current_student,username,password);
                    ConfigEntityDao configEntityDao = mApplication.mDaoSession.getConfigEntityDao();
                    configEntityDao.insert(mApplication.mConfig);
                    CloudSchoolBusRestClient.updateSessionid(sid);
                    getClassInfoFromServer();
                    break;
                //Get the base info
                case GET_BASEINFO:
                    Intent resultIntent = new Intent();
                    setResult(RESULT_OK, resultIntent);
                    finish();
                    break;
                //Login Process Failed
                case LOGIN_FAILED:
                    SimpleDialogFragment.createBuilder(mContext, getSupportFragmentManager()).setMessage(getResources().getString(R.string.auth_failed))
                            .setPositiveButtonText(getResources().getString(R.string.OKAY)).show();
                    showProgress(false);
                    break;
                case NO_NETOWRK:
                    SimpleDialogFragment.createBuilder(mContext, getSupportFragmentManager()).setMessage(getResources().getString(R.string.no_network))
                            .setPositiveButtonText(getResources().getString(R.string.OKAY)).show();
                    showProgress(false);
                    break;
                case SERVER_ERROR:
                    SimpleDialogFragment.createBuilder(mContext, getSupportFragmentManager()).setMessage(getResources().getString(R.string.server_error))
                            .setPositiveButtonText(getResources().getString(R.string.OKAY)).show();
                    showProgress(false);
                    break;
                default:
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
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
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
    public void attemptLogin() {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        username = mEmailView.getText().toString();
        password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

//        // Check for a valid email address.
//        if (TextUtils.isEmpty(email)) {
//            mEmailView.setError(getString(R.string.error_field_required));
//            focusView = mEmailView;
//            cancel = true;
//        } else if (!isEmailValid(email)) {
//            mEmailView.setError(getString(R.string.error_invalid_email));
//            focusView = mEmailView;
//            cancel = true;
//        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            login(username, password);
        }
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
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


    @Override
    public void onListItemSelected(CharSequence value, int number, int requestCode) {
        if (requestCode == REQUEST_LIST_SIMPLE || requestCode == REQUEST_LIST_SINGLE) {
            Toast.makeText(getApplicationContext(), "Selected: " + value, Toast.LENGTH_SHORT).show();
            mApplication.mCurrentStudent = students.get(number);
            current_student = (short) number;
            handler.sendEmptyMessage(CURRENT_STUDENT);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public void login(String username, String password) {

        if(!networkStatusEvent.isNetworkConnected()) {
            handler.sendEmptyMessage(NO_NETOWRK);
            return;
        }

        showProgress(true);

        RequestParams params = new RequestParams();
        params.put("username", username);
        params.put("password", ooo.h(password, "mactop", 0));
        CloudSchoolBusRestClient.post("signin", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, org.json.JSONArray response) {

                String retCode = "";

                int num_kids = 0;

                for (int i = 0; i < headers.length; i++) {
                    Header header = headers[i];
                    if ("code".equalsIgnoreCase(header.getName())) {
                        retCode = header.getValue();
                        break;
                    }
                }

                if (!retCode.equals("1"))
                {
                    handler.sendEmptyMessage(SERVER_ERROR);
                    return;
                } else {
                    students = FastJsonTools.getListObject(response.toString(), Student.class);
                    mApplication.mStudentList = students;
                }

                num_kids = students.size();

                if (num_kids == 1) {
                    String kids[] = new String[num_kids];

                    for (int i = 0; i < num_kids; i++)
                        kids[i] = students.get(i).getNikename();

                    ListDialogFragment
                            .createBuilder(getApplicationContext(), getSupportFragmentManager())
                            .setTitle("请选择您的孩子：")
                            .setItems(kids)
                            .setRequestCode(REQUEST_LIST_SINGLE)
                            .setChoiceMode(AbsListView.CHOICE_MODE_SINGLE)
                            .show();

                } else if (num_kids == 1) {
                    mApplication.mCurrentStudent = students.get(0);
                    current_student = 0;
                    handler.sendEmptyMessage(CURRENT_STUDENT);
                } else {
                    //Error handling
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                handler.sendEmptyMessage(LOGIN_FAILED);
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

    private void getClassInfoFromServer() {

        if(!networkStatusEvent.isNetworkConnected()) {
            handler.sendEmptyMessage(NO_NETOWRK);
            return;
        }

        showProgress(true);

        final ClassEntityDao classEntityDao = mApplication.mDaoSession.getClassEntityDao();
        final TeacherEntityDao teacherEntityDao = mApplication.mDaoSession.getTeacherEntityDao();

        RequestParams params = new RequestParams();

        CloudSchoolBusRestClient.get(ProtocolDef.METHOD_Classinfo, params, new JsonHttpResponseHandler() {
                    @Override
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
                            handler.sendEmptyMessage(SERVER_ERROR);
                            return;
                        }

                        mApplication.mBaseInfo = (Baseinfo) FastJsonTools.getObject(response.toString(), Baseinfo.class);
                        ClassEntity classEntity = new ClassEntity(
                                mApplication.mBaseInfo.getClassinfo().getUid(),
                                mApplication.mBaseInfo.getClassinfo().getPhone(),
                                mApplication.mBaseInfo.getClassinfo().getSchoolname(),
                                mApplication.mBaseInfo.getClassinfo().getAddress(),
                                mApplication.mBaseInfo.getClassinfo().getClassname(),
                                mApplication.mBaseInfo.getClassinfo().getProvince(),
                                mApplication.mBaseInfo.getClassinfo().getCity(),
                                mApplication.mBaseInfo.getClassinfo().getClassid()
                        );
                        classEntityDao.insertOrReplace(classEntity);

                        for (int i = 0; i < mApplication.mBaseInfo.getTeacherlist().size(); i++) {
                            Teacher teacher = mApplication.mBaseInfo.getTeacherlist().get(i);
                            TeacherEntity teacherEntity = new TeacherEntity(teacher.getTeacherid(), teacher.getTeachername(), classEntity.getClassid());
                            teacherEntityDao.insertOrReplace(teacherEntity);
                        }
                        handler.sendEmptyMessage(GET_BASEINFO);
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
                        handler.sendEmptyMessage(LOGIN_FAILED);
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                        handler.sendEmptyMessage(LOGIN_FAILED);
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        handler.sendEmptyMessage(LOGIN_FAILED);
                        super.onFailure(statusCode, headers, responseString, throwable);
                    }
                }
        );
    }


    public void unit() throws JSONException {

        if(!networkStatusEvent.isNetworkConnected()) {
            handler.sendEmptyMessage(NO_NETOWRK);
            return;
        }

        showProgress(true);

        RequestParams params = new RequestParams();
        params.put("uid_student", mApplication.mCurrentStudent.getUid_student());
        params.put("uid_class", mApplication.mCurrentStudent.getUid_class());

        CloudSchoolBusRestClient.post("unit", params, new JsonHttpResponseHandler() {

            @Override
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
                    handler.sendEmptyMessage(SERVER_ERROR);
                    return;
                } else {
                    try {
                        sid = response.getString("sid");
                        handler.sendEmptyMessage(GET_SESIONID);
                    } catch (org.json.JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, org.json.JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    @Subscribe public void onNetworkStatusChange(NetworkStatusEvent event)
    {
        networkStatusEvent = event;
    }
}

