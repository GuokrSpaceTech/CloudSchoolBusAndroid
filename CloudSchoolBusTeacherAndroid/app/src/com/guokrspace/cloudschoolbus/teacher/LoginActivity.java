package com.guokrspace.cloudschoolbus.teacher;

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

import com.android.support.handlerui.HandlerToastUI;
import com.avast.android.dialogs.fragment.SimpleDialogFragment;
import com.guokrspace.cloudschoolbus.teacher.base.RongCloudEvent;
import com.guokrspace.cloudschoolbus.teacher.base.activity.BaseActivity;
import com.android.support.fastjson.FastJsonTools;
import com.guokrspace.cloudschoolbus.teacher.base.include.HandlerConstant;
import com.guokrspace.cloudschoolbus.teacher.base.include.Version;
import com.guokrspace.cloudschoolbus.teacher.database.daodb.ClassEntityT;
import com.guokrspace.cloudschoolbus.teacher.database.daodb.ClassModuleEntity;
import com.guokrspace.cloudschoolbus.teacher.database.daodb.ConfigEntity;
import com.guokrspace.cloudschoolbus.teacher.database.daodb.ConfigEntityDao;
import com.guokrspace.cloudschoolbus.teacher.database.daodb.MessageTypeEntity;
import com.guokrspace.cloudschoolbus.teacher.database.daodb.ParentEntityT;
import com.guokrspace.cloudschoolbus.teacher.database.daodb.SchoolEntityT;
import com.guokrspace.cloudschoolbus.teacher.database.daodb.StudentClassRelationEntity;
import com.guokrspace.cloudschoolbus.teacher.database.daodb.StudentEntityT;
import com.guokrspace.cloudschoolbus.teacher.database.daodb.StudentParentRelationEntity;
import com.guokrspace.cloudschoolbus.teacher.database.daodb.TagsEntityT;
import com.guokrspace.cloudschoolbus.teacher.database.daodb.TeacherDutyClassRelationEntity;
import com.guokrspace.cloudschoolbus.teacher.database.daodb.TeacherDutyEntity;
import com.guokrspace.cloudschoolbus.teacher.database.daodb.TeacherEntityT;
import com.guokrspace.cloudschoolbus.teacher.entity.BaseInfoT;
import com.guokrspace.cloudschoolbus.teacher.entity.Baseinfo;

import com.guokrspace.cloudschoolbus.teacher.entity.ClassInfo;
import com.guokrspace.cloudschoolbus.teacher.entity.ClassinfoT;
import com.guokrspace.cloudschoolbus.teacher.entity.Module;
import com.guokrspace.cloudschoolbus.teacher.entity.Parent;
import com.guokrspace.cloudschoolbus.teacher.entity.School;
import com.guokrspace.cloudschoolbus.teacher.entity.SchoolInfo;
import com.guokrspace.cloudschoolbus.teacher.entity.Setting;
import com.guokrspace.cloudschoolbus.teacher.entity.Student;
import com.guokrspace.cloudschoolbus.teacher.entity.StudentT;
import com.guokrspace.cloudschoolbus.teacher.entity.TagT;
import com.guokrspace.cloudschoolbus.teacher.entity.Teacher;
import com.guokrspace.cloudschoolbus.teacher.entity.TeacherClassInfo;
import com.guokrspace.cloudschoolbus.teacher.entity.TeacherT;
import com.guokrspace.cloudschoolbus.teacher.event.BusProvider;
import com.guokrspace.cloudschoolbus.teacher.event.LoginResultEvent;
import com.guokrspace.cloudschoolbus.teacher.protocols.CloudSchoolBusRestClient;
import com.guokrspace.cloudschoolbus.teacher.protocols.ProtocolDef;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.widget.provider.CameraInputProvider;
import io.rong.imkit.widget.provider.ImageInputProvider;
import io.rong.imkit.widget.provider.InputProvider;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;

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
                        HandlerToastUI.getHandlerToastUI(mContext, getResources().getString(R.string.invalid_mobile));
//                        SimpleDialogFragment.createBuilder(mContext, getSupportFragmentManager())
//                                .setMessage(getResources().getString(R.string.invalid_mobile))
//                                .setPositiveButtonText(getResources().getString(R.string.OKAY)).show();
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

                        HandlerToastUI.getHandlerToastUI(mContext,getResources().getString(R.string.invalid_verify_code));
//                        SimpleDialogFragment.createBuilder(mContext, getSupportFragmentManager())
//                                .setMessage(getResources().getString(R.string.invalid_verify_code))
//                                .setPositiveButtonText(getResources().getString(R.string.OKAY)).show();
                        break;
                    case HandlerConstant.MSG_REG_SMS:
                        showProgress(false);
                        threadStopFlag = true;
                        HandlerToastUI.getHandlerToastUI(mContext,getResources().getString(R.string.SMS_oversend));
//                        SimpleDialogFragment.createBuilder(mContext, getSupportFragmentManager())
//                                .setMessage(getResources().getString(R.string.SMS_oversend))
//                                .setPositiveButtonText(getResources().getString(R.string.OKAY)).show();
                        break;
                    //Get the base info
                    case HandlerConstant.MSG_BASEINFO_OK:
                        BusProvider.getInstance().post(new LoginResultEvent(true));
                        finish();
                        break;
                    case HandlerConstant.MSG_BASEINFO_FAIL:
                        showProgress(false);
                        HandlerToastUI.getHandlerToastUI(mContext, getResources().getString(R.string.failure_baseinfo));
//                        SimpleDialogFragment.createBuilder(mContext, getSupportFragmentManager())
//                                .setMessage(getResources().getString(R.string.failure_baseinfo))
//                                .setPositiveButtonText(getResources().getString(R.string.OKAY)).show();
                        break;
                    case HandlerConstant.MSG_NO_NETOWRK:
                        showProgress(false);
                        HandlerToastUI.getHandlerToastUI(mContext, getResources().getString(R.string.no_network));
//                        SimpleDialogFragment.createBuilder(mContext, getSupportFragmentManager())
//                                .setMessage(getResources().getString(R.string.no_network))
//                                .setPositiveButtonText(getResources().getString(R.string.OKAY)).show();
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
                        HandlerToastUI.getHandlerToastUI(mContext, getResources().getString(R.string.failure_unknown));
//                        SimpleDialogFragment.createBuilder(mContext, getSupportFragmentManager())
//                                .setMessage(getResources().getString(R.string.failure_unknown))
//                                .setPositiveButtonText(getResources().getString(R.string.OKAY)).show();
                        break;
                    default:
                        showProgress(false);
                        HandlerToastUI.getHandlerToastUI(mContext, getResources().getString(R.string.failure_unknown));
//                        SimpleDialogFragment.createBuilder(mContext, getSupportFragmentManager())
//                                .setMessage(getResources().getString(R.string.failure_unknown))
//                                .setPositiveButtonText(getResources().getString(R.string.OKAY)).show();
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

                        String responseString = "{\"classes\":[{\"schoolid\":\"27\",\"remark\":null,\"dutyid\":\"0\",\"classname\":\"火箭班\",\"classid\":\"37\"},{\"schoolid\":\"27\",\"remark\":null,\"dutyid\":\"0\",\"classname\":\"宇宙班\",\"classid\":\"38\"},{\"schoolid\":\"28\",\"remark\":null,\"dutyid\":\"0\",\"classname\":\"星蓝湾社区托管托班\",\"classid\":\"39\"}],\"parents\":[{\"relationship\":\"妈妈\",\"parentid\":\"531\",\"avatar\":\"\",\"pictureid\":null,\"nickname\":\"妈妈\",\"studentids\":[\"530\"],\"mobile\":\"15874124102\"},{\"relationship\":\"妈妈\",\"parentid\":\"772\",\"avatar\":\"\",\"pictureid\":null,\"nickname\":\"妈妈\",\"studentids\":[\"771\"],\"mobile\":\"15084741116\"},{\"relationship\":\"妈妈\",\"parentid\":\"774\",\"avatar\":\"\",\"pictureid\":null,\"nickname\":\"妈妈\",\"studentids\":[\"773\"],\"mobile\":\"13548944222\"},{\"relationship\":\"妈妈\",\"parentid\":\"776\",\"avatar\":\"\",\"pictureid\":null,\"nickname\":\"妈妈\",\"studentids\":[\"775\"],\"mobile\":\"13755066690\"},{\"relationship\":\"妈妈\",\"parentid\":\"780\",\"avatar\":\"\",\"pictureid\":null,\"nickname\":\"妈妈\",\"studentids\":[\"779\"],\"mobile\":\"18974913392\"},{\"relationship\":\"妈妈\",\"parentid\":\"782\",\"avatar\":\"\",\"pictureid\":null,\"nickname\":\"妈妈\",\"studentids\":[\"781\"],\"mobile\":\"15874160606\"},{\"relationship\":\"妈妈\",\"parentid\":\"796\",\"avatar\":\"\",\"pictureid\":null,\"nickname\":\"妈妈\",\"studentids\":[\"795\"],\"mobile\":\"18684812822\"},{\"relationship\":\"妈妈\",\"parentid\":\"808\",\"avatar\":\"\",\"pictureid\":null,\"nickname\":\"妈妈\",\"studentids\":[\"807\"],\"mobile\":\"15874118927\"},{\"relationship\":\"妈妈\",\"parentid\":\"810\",\"avatar\":\"\",\"pictureid\":null,\"nickname\":\"妈妈\",\"studentids\":[\"809\"],\"mobile\":\"15575460505\"},{\"relationship\":\"妈妈\",\"parentid\":\"812\",\"avatar\":\"\",\"pictureid\":null,\"nickname\":\"妈妈\",\"studentids\":[\"811\"],\"mobile\":\"15084310205\"},{\"relationship\":\"妈妈\",\"parentid\":\"814\",\"avatar\":\"\",\"pictureid\":null,\"nickname\":\"妈妈\",\"studentids\":[\"813\"],\"mobile\":\"13973136999\"},{\"relationship\":\"妈妈\",\"parentid\":\"823\",\"avatar\":\"\",\"pictureid\":null,\"nickname\":\"妈妈\",\"studentids\":[\"822\"],\"mobile\":\"13786110841\"},{\"relationship\":\"爸爸\",\"parentid\":\"825\",\"avatar\":\"\",\"pictureid\":null,\"nickname\":\"爸爸\",\"studentids\":[\"824\"],\"mobile\":\"18673281815\"},{\"relationship\":\"妈妈\",\"parentid\":\"827\",\"avatar\":\"\",\"pictureid\":null,\"nickname\":\"妈妈\",\"studentids\":[\"826\"],\"mobile\":\"13874970467\"},{\"relationship\":\"妈妈\",\"parentid\":\"829\",\"avatar\":\"\",\"pictureid\":null,\"nickname\":\"妈妈\",\"studentids\":[\"828\"],\"mobile\":\"13874806868\"},{\"relationship\":\"妈妈\",\"parentid\":\"831\",\"avatar\":\"\",\"pictureid\":null,\"nickname\":\"妈妈\",\"studentids\":[\"830\"],\"mobile\":\"13657444666\"},{\"relationship\":\"爸爸\",\"parentid\":\"833\",\"avatar\":\"\",\"pictureid\":null,\"nickname\":\"爸爸\",\"studentids\":[\"832\"],\"mobile\":\"18674827341\"},{\"relationship\":\"妈妈\",\"parentid\":\"835\",\"avatar\":\"\",\"pictureid\":null,\"nickname\":\"妈妈\",\"studentids\":[\"834\"],\"mobile\":\"13974964177\"},{\"relationship\":\"妈妈\",\"parentid\":\"837\",\"avatar\":\"\",\"pictureid\":null,\"nickname\":\"妈妈\",\"studentids\":[\"836\"],\"mobile\":\"15308487176\"},{\"relationship\":\"\",\"parentid\":\"839\",\"avatar\":\"\",\"pictureid\":null,\"nickname\":\"妈妈\",\"studentids\":[\"838\",\"2810\"],\"mobile\":\"18900790043\"},{\"relationship\":\"妈妈\",\"parentid\":\"2751\",\"avatar\":\"\",\"pictureid\":null,\"nickname\":\"云中校车测试学生\",\"studentids\":[\"2752\"],\"mobile\":\"15243639900\"},{\"relationship\":\"妈妈\",\"parentid\":\"2761\",\"avatar\":\"\",\"pictureid\":null,\"nickname\":\"欧阳洋\",\"studentids\":[\"2762\"],\"mobile\":\"15974210026\"},{\"relationship\":\"妈妈\",\"parentid\":\"2775\",\"avatar\":\"\",\"pictureid\":null,\"nickname\":\"李溢辉\",\"studentids\":[\"2776\"],\"mobile\":\"15084910205\"},{\"relationship\":\"妈妈\",\"parentid\":\"2782\",\"avatar\":\"\",\"pictureid\":null,\"nickname\":\"曹琼\",\"studentids\":[\"2783\"],\"mobile\":\"13807482960\"},{\"relationship\":\"爸爸\",\"parentid\":\"2784\",\"avatar\":\"\",\"pictureid\":null,\"nickname\":\"郑彬\",\"studentids\":[\"2785\"],\"mobile\":\"18673178447\"},{\"relationship\":\"妈妈\",\"parentid\":\"2786\",\"avatar\":\"\",\"pictureid\":null,\"nickname\":\"汤漾\",\"studentids\":[\"2787\"],\"mobile\":\"13574156315\"},{\"relationship\":\"妈妈\",\"parentid\":\"2788\",\"avatar\":\"\",\"pictureid\":null,\"nickname\":\"尹恒\",\"studentids\":[\"2789\"],\"mobile\":\"18973110540\"},{\"relationship\":\"妈妈\",\"parentid\":\"2790\",\"avatar\":\"\",\"pictureid\":null,\"nickname\":\"徐可\",\"studentids\":[\"2791\"],\"mobile\":\"13786197171\"},{\"relationship\":\"妈妈\",\"parentid\":\"2792\",\"avatar\":\"\",\"pictureid\":null,\"nickname\":\"陈鑫华\",\"studentids\":[\"2793\"],\"mobile\":\"13508476676\"},{\"relationship\":\"妈妈\",\"parentid\":\"2800\",\"avatar\":\"\",\"pictureid\":null,\"nickname\":\"梁娇娇\",\"studentids\":[\"2801\"],\"mobile\":\"13787251945\"},{\"relationship\":\"妈妈\",\"parentid\":\"2802\",\"avatar\":\"\",\"pictureid\":null,\"nickname\":\"贺小燕\",\"studentids\":[\"2803\"],\"mobile\":\"13367315858\"},{\"relationship\":\"妈妈\",\"parentid\":\"2804\",\"avatar\":\"\",\"pictureid\":null,\"nickname\":\"鲁寅瑛\",\"studentids\":[\"2805\"],\"mobile\":\"18684852677\"},{\"relationship\":\"妈妈\",\"parentid\":\"2806\",\"avatar\":\"\",\"pictureid\":null,\"nickname\":\"高雅\",\"studentids\":[\"2807\"],\"mobile\":\"18817108033\"},{\"relationship\":\"妈妈\",\"parentid\":\"2808\",\"avatar\":\"\",\"pictureid\":null,\"nickname\":\"钟莎磊\",\"studentids\":[\"2809\"],\"mobile\":\"13707319092\"},{\"relationship\":\"妈妈\",\"parentid\":\"2811\",\"avatar\":\"\",\"pictureid\":null,\"nickname\":\"刘海艳\",\"studentids\":[\"2812\"],\"mobile\":\"13973119121\"},{\"relationship\":\"妈妈\",\"parentid\":\"2813\",\"avatar\":\"\",\"pictureid\":null,\"nickname\":\"朱美丽\",\"studentids\":[\"2814\"],\"mobile\":\"15920326127\"},{\"relationship\":\"妈妈\",\"parentid\":\"2815\",\"avatar\":\"\",\"pictureid\":null,\"nickname\":\"翁春艳\",\"studentids\":[\"2816\"],\"mobile\":\"13975840422\"},{\"relationship\":\"妈妈\",\"parentid\":\"2817\",\"avatar\":\"\",\"pictureid\":null,\"nickname\":\"袁冬琴\",\"studentids\":[\"2818\"],\"mobile\":\"13757115171\"},{\"relationship\":\"妈妈\",\"parentid\":\"2819\",\"avatar\":\"\",\"pictureid\":null,\"nickname\":\"崔媛\",\"studentids\":[\"2820\"],\"mobile\":\"15367998233\"},{\"relationship\":\"妈妈\",\"parentid\":\"2821\",\"avatar\":\"\",\"pictureid\":null,\"nickname\":\"杨圭玉\",\"studentids\":[\"2822\"],\"mobile\":\"15973189233\"},{\"relationship\":\"妈妈\",\"parentid\":\"2823\",\"avatar\":\"\",\"pictureid\":null,\"nickname\":\"谭婷\",\"studentids\":[\"2824\"],\"mobile\":\"13974957136\"}],\"students\":[{\"birthday\":\"2013-06-28\",\"sex\":\"2\",\"pictureid\":\"24999\",\"nickname\":\"谢逅\",\"classids\":[\"30\",\"37\"],\"cnname\":\"谢逅\",\"avatar\":\"http:\\/\\/cloud.yunxiaoche.com\\/images\\/student.jpg\",\"classid\":\"37\",\"studentid\":\"530\"},{\"birthday\":\"2013-08-05\",\"sex\":\"1\",\"pictureid\":\"24999\",\"nickname\":\"腾腾\",\"classids\":[\"38\"],\"cnname\":\"孔思腾\",\"avatar\":\"http:\\/\\/cloud.yunxiaoche.com\\/images\\/student.jpg\",\"classid\":\"38\",\"studentid\":\"771\"},{\"birthday\":\"0000-00-00\",\"sex\":\"2\",\"pictureid\":\"24999\",\"nickname\":\"君君\",\"classids\":[\"38\"],\"cnname\":\"林致君\",\"avatar\":\"http:\\/\\/cloud.yunxiaoche.com\\/images\\/student.jpg\",\"classid\":\"38\",\"studentid\":\"773\"},{\"birthday\":\"2012-12-25\",\"sex\":\"1\",\"pictureid\":\"24999\",\"nickname\":\"牛牛\",\"classids\":[\"38\"],\"cnname\":\"陈祉宇\",\"avatar\":\"http:\\/\\/cloud.yunxiaoche.com\\/images\\/student.jpg\",\"classid\":\"38\",\"studentid\":\"775\"},{\"birthday\":\"2013-04-19\",\"sex\":\"1\",\"pictureid\":\"24999\",\"nickname\":\"壮壮\",\"classids\":[\"38\"],\"cnname\":\"张景文\",\"avatar\":\"http:\\/\\/cloud.yunxiaoche.com\\/images\\/student.jpg\",\"classid\":\"38\",\"studentid\":\"779\"},{\"birthday\":\"2013-01-08\",\"sex\":\"1\",\"pictureid\":\"24999\",\"nickname\":\"琦琦\",\"classids\":[\"38\"],\"cnname\":\"尹泽涵\",\"avatar\":\"http:\\/\\/cloud.yunxiaoche.com\\/images\\/student.jpg\",\"classid\":\"38\",\"studentid\":\"781\"},{\"birthday\":\"2012-12-01\",\"sex\":\"2\",\"pictureid\":\"24999\",\"nickname\":\"cc\",\"classids\":[\"37\"],\"cnname\":\"毛梓溪\",\"avatar\":\"http:\\/\\/cloud.yunxiaoche.com\\/images\\/student.jpg\",\"classid\":\"37\",\"studentid\":\"795\"},{\"birthday\":\"2013-01-16\",\"sex\":\"1\",\"pictureid\":\"24999\",\"nickname\":\"乐乐\",\"classids\":[\"37\"],\"cnname\":\"冯予森\",\"avatar\":\"http:\\/\\/cloud.yunxiaoche.com\\/images\\/student.jpg\",\"classid\":\"37\",\"studentid\":\"807\"},{\"birthday\":\"2013-05-17\",\"sex\":\"1\",\"pictureid\":\"24999\",\"nickname\":\"Benny\",\"classids\":[\"37\"],\"cnname\":\"闫本泥\",\"avatar\":\"http:\\/\\/cloud.yunxiaoche.com\\/images\\/student.jpg\",\"classid\":\"37\",\"studentid\":\"809\"},{\"birthday\":\"2013-01-02\",\"sex\":\"1\",\"pictureid\":\"24999\",\"nickname\":\"憨憨\",\"classids\":[\"37\"],\"cnname\":\"李赫煊\",\"avatar\":\"http:\\/\\/cloud.yunxiaoche.com\\/images\\/student.jpg\",\"classid\":\"37\",\"studentid\":\"811\"},{\"birthday\":\"2013-04-10\",\"sex\":\"1\",\"pictureid\":\"24999\",\"nickname\":\"阳阳\",\"classids\":[\"37\"],\"cnname\":\"廖旭阳\",\"avatar\":\"http:\\/\\/cloud.yunxiaoche.com\\/images\\/student.jpg\",\"classid\":\"37\",\"studentid\":\"813\"},{\"birthday\":\"2012-03-10\",\"sex\":\"2\",\"pictureid\":\"24999\",\"nickname\":\"璇璇\",\"classids\":[\"39\"],\"cnname\":\"陈音璇\",\"avatar\":\"http:\\/\\/cloud.yunxiaoche.com\\/images\\/student.jpg\",\"classid\":\"39\",\"studentid\":\"822\"},{\"birthday\":\"2012-05-09\",\"sex\":\"1\",\"pictureid\":\"24999\",\"nickname\":\"哲哲\",\"classids\":[\"39\"],\"cnname\":\"龚浩哲\",\"avatar\":\"http:\\/\\/cloud.yunxiaoche.com\\/images\\/student.jpg\",\"classid\":\"39\",\"studentid\":\"824\"},{\"birthday\":\"2012-03-31\",\"sex\":\"2\",\"pictureid\":\"24999\",\"nickname\":\"乖乖\",\"classids\":[\"39\"],\"cnname\":\"李玥凝\",\"avatar\":\"http:\\/\\/cloud.yunxiaoche.com\\/images\\/student.jpg\",\"classid\":\"39\",\"studentid\":\"826\"},{\"birthday\":\"2012-09-18\",\"sex\":\"2\",\"pictureid\":\"24999\",\"nickname\":\"坦坦\",\"classids\":[\"39\"],\"cnname\":\"任语荞\",\"avatar\":\"http:\\/\\/cloud.yunxiaoche.com\\/images\\/student.jpg\",\"classid\":\"39\",\"studentid\":\"828\"},{\"birthday\":\"2012-05-30\",\"sex\":\"2\",\"pictureid\":\"24999\",\"nickname\":\"小苹果\",\"classids\":[\"39\"],\"cnname\":\"许秦溱\",\"avatar\":\"http:\\/\\/cloud.yunxiaoche.com\\/images\\/student.jpg\",\"classid\":\"39\",\"studentid\":\"830\"},{\"birthday\":\"2013-09-29\",\"sex\":\"2\",\"pictureid\":\"24999\",\"nickname\":\"然然\",\"classids\":[\"38\",\"39\"],\"cnname\":\"曾昕然\",\"avatar\":\"http:\\/\\/cloud.yunxiaoche.com\\/images\\/student.jpg\",\"classid\":\"38\",\"studentid\":\"832\"},{\"birthday\":\"2013-01-17\",\"sex\":\"2\",\"pictureid\":\"24999\",\"nickname\":\"欣欣\",\"classids\":[\"39\"],\"cnname\":\"李欣晔\",\"avatar\":\"http:\\/\\/cloud.yunxiaoche.com\\/images\\/student.jpg\",\"classid\":\"39\",\"studentid\":\"834\"},{\"birthday\":\"2012-06-01\",\"sex\":\"2\",\"pictureid\":\"24999\",\"nickname\":\"六六\",\"classids\":[\"39\"],\"cnname\":\"吴嘉雯\",\"avatar\":\"http:\\/\\/cloud.yunxiaoche.com\\/images\\/student.jpg\",\"classid\":\"39\",\"studentid\":\"836\"},{\"birthday\":\"2013-02-21\",\"sex\":\"1\",\"pictureid\":\"24999\",\"nickname\":\"可可\",\"classids\":[\"39\"],\"cnname\":\"许宇\",\"avatar\":\"http:\\/\\/cloud.yunxiaoche.com\\/images\\/student.jpg\",\"classid\":\"39\",\"studentid\":\"838\"},{\"birthday\":\"2015-12-01\",\"sex\":\"2\",\"pictureid\":\"0\",\"nickname\":null,\"classids\":[\"21\",\"22\",\"23\",\"24\",\"25\",\"26\",\"27\",\"38\",\"78\",\"83\",\"84\",\"85\",\"86\",\"87\",\"88\",\"89\",\"90\",\"91\",\"92\",\"93\",\"94\",\"95\",\"96\",\"97\",\"98\",\"99\",\"100\",\"101\",\"102\",\"103\",\"104\",\"105\",\"106\",\"107\",\"108\",\"109\",\"110\",\"111\",\"112\",\"113\",\"114\",\"115\",\"116\",\"117\"],\"cnname\":\"测试家长小朋友\",\"avatar\":\"\",\"classid\":\"38\",\"studentid\":\"2752\"},{\"birthday\":\"2012-12-01\",\"sex\":\"2\",\"pictureid\":\"0\",\"nickname\":null,\"classids\":[\"37\"],\"cnname\":\"欧阳洋\",\"avatar\":\"\",\"classid\":\"37\",\"studentid\":\"2762\"},{\"birthday\":\"2013-01-01\",\"sex\":\"1\",\"pictureid\":\"0\",\"nickname\":null,\"classids\":[\"37\"],\"cnname\":\"李赫煊\",\"avatar\":\"\",\"classid\":\"37\",\"studentid\":\"2776\"},{\"birthday\":\"2012-11-22\",\"sex\":\"1\",\"pictureid\":\"0\",\"nickname\":null,\"classids\":[\"37\"],\"cnname\":\"杨承熹\",\"avatar\":\"\",\"classid\":\"37\",\"studentid\":\"2783\"},{\"birthday\":\"2013-05-04\",\"sex\":\"2\",\"pictureid\":\"0\",\"nickname\":null,\"classids\":[\"38\"],\"cnname\":\"郑楚为\",\"avatar\":\"\",\"classid\":\"38\",\"studentid\":\"2785\"},{\"birthday\":\"2012-06-14\",\"sex\":\"1\",\"pictureid\":\"0\",\"nickname\":null,\"classids\":[\"37\"],\"cnname\":\"廖瀚驰\",\"avatar\":\"\",\"classid\":\"37\",\"studentid\":\"2787\"},{\"birthday\":\"2013-02-15\",\"sex\":\"1\",\"pictureid\":\"0\",\"nickname\":null,\"classids\":[\"38\"],\"cnname\":\"刘宸宇\",\"avatar\":\"\",\"classid\":\"38\",\"studentid\":\"2789\"},{\"birthday\":\"2013-06-10\",\"sex\":\"1\",\"pictureid\":\"0\",\"nickname\":null,\"classids\":[\"38\"],\"cnname\":\"朱泓瑞\",\"avatar\":\"\",\"classid\":\"38\",\"studentid\":\"2791\"},{\"birthday\":\"2013-03-31\",\"sex\":\"2\",\"pictureid\":\"0\",\"nickname\":null,\"classids\":[\"37\"],\"cnname\":\"吴群洲\",\"avatar\":\"\",\"classid\":\"37\",\"studentid\":\"2793\"},{\"birthday\":\"2013-07-01\",\"sex\":\"2\",\"pictureid\":\"0\",\"nickname\":null,\"classids\":[\"38\"],\"cnname\":\"林诗晨\",\"avatar\":\"\",\"classid\":\"38\",\"studentid\":\"2801\"},{\"birthday\":\"2012-11-18\",\"sex\":\"2\",\"pictureid\":\"0\",\"nickname\":null,\"classids\":[\"37\"],\"cnname\":\"谭焱曦\",\"avatar\":\"\",\"classid\":\"37\",\"studentid\":\"2803\"},{\"birthday\":\"2013-04-06\",\"sex\":\"1\",\"pictureid\":\"0\",\"nickname\":null,\"classids\":[\"37\"],\"cnname\":\"沈振宇\",\"avatar\":\"\",\"classid\":\"37\",\"studentid\":\"2805\"},{\"birthday\":\"2012-11-07\",\"sex\":\"2\",\"pictureid\":\"0\",\"nickname\":null,\"classids\":[\"38\"],\"cnname\":\"刘子祎\",\"avatar\":\"\",\"classid\":\"38\",\"studentid\":\"2807\"},{\"birthday\":\"2011-08-01\",\"sex\":\"2\",\"pictureid\":\"0\",\"nickname\":null,\"classids\":[\"38\"],\"cnname\":\"李怡辰\",\"avatar\":\"\",\"classid\":\"38\",\"studentid\":\"2809\"},{\"birthday\":\"2013-02-21\",\"sex\":\"1\",\"pictureid\":\"0\",\"nickname\":null,\"classids\":[\"37\"],\"cnname\":\"许宇\",\"avatar\":\"\",\"classid\":\"37\",\"studentid\":\"2810\"},{\"birthday\":\"2013-04-06\",\"sex\":\"1\",\"pictureid\":\"0\",\"nickname\":null,\"classids\":[\"38\"],\"cnname\":\"曹恩一\",\"avatar\":\"\",\"classid\":\"38\",\"studentid\":\"2812\"},{\"birthday\":\"2012-10-24\",\"sex\":\"1\",\"pictureid\":\"0\",\"nickname\":null,\"classids\":[\"37\"],\"cnname\":\"陈宏睿\",\"avatar\":\"\",\"classid\":\"37\",\"studentid\":\"2814\"},{\"birthday\":\"2013-03-20\",\"sex\":\"2\",\"pictureid\":\"0\",\"nickname\":null,\"classids\":[\"38\"],\"cnname\":\"吴沁倪\",\"avatar\":\"\",\"classid\":\"38\",\"studentid\":\"2816\"},{\"birthday\":\"2013-05-25\",\"sex\":\"1\",\"pictureid\":\"0\",\"nickname\":null,\"classids\":[\"38\"],\"cnname\":\"曾赫渊\",\"avatar\":\"\",\"classid\":\"38\",\"studentid\":\"2818\"},{\"birthday\":\"2012-12-18\",\"sex\":\"2\",\"pictureid\":\"0\",\"nickname\":null,\"classids\":[\"37\"],\"cnname\":\"岳宛霓\",\"avatar\":\"\",\"classid\":\"37\",\"studentid\":\"2820\"},{\"birthday\":\"2013-01-19\",\"sex\":\"1\",\"pictureid\":\"0\",\"nickname\":null,\"classids\":[\"38\"],\"cnname\":\"周耿嘉\",\"avatar\":\"\",\"classid\":\"38\",\"studentid\":\"2822\"},{\"birthday\":\"2013-01-27\",\"sex\":\"1\",\"pictureid\":\"0\",\"nickname\":null,\"classids\":[\"37\"],\"cnname\":\"杨沛儒\",\"avatar\":\"\",\"classid\":\"37\",\"studentid\":\"2824\"}],\"schools\":{\"27\":{\"tags\":[{\"tagname_en\":\"Sensorial\",\"isdelete\":\"0\",\"schoolid\":\"27\",\"tagnamedesc\":\"培养幼儿视、听、嗅、味、触五种感官能力的敏锐程度，使幼儿的每一项感觉都是灵敏全面发展。为学习数学及其他领域做准备，增强对于社会及知识的感知，培养初步的序列、分列概念和抽象思维能力。\",\"tagid\":\"369\",\"tagnamedesc_en\":\"Cultivate kids sharp degree of the five senses ability--see, hear, smell, and touch, make every sense to all-round development.In preparation for the study mathematics and other fields, enhancement the awareness of social and intellectual, and cultivate t\",\"tagname\":\"蒙氏感官区\"},{\"tagname_en\":\"Mathematics\",\"isdelete\":\"0\",\"schoolid\":\"27\",\"tagnamedesc\":\"培养数的概念，理解加减乘除运算的关系；会做10000以内的加减乘除，会做简单的测量工作；培养逻辑思维能力和创造能力。\",\"tagid\":\"370\",\"tagnamedesc_en\":\"Understand the concepts of number and the relationship between addition, subtraction, multiplication, and division. Have the ability to do the addition, subtraction, multiplication, and division within 1000, and simple measurements. Cultivate the ability \",\"tagname\":\"蒙氏数学区\"},{\"tagname_en\":\"Practical Life\",\"isdelete\":\"0\",\"schoolid\":\"27\",\"tagnamedesc\":\"培养幼儿的大肌肉和小肌肉的组织能力，增强对自己身体的控制能力，从而把孩子从自然人向社会人过渡，脱离对父母的过度依赖。学习生活自理能力，学会帮助别人，照顾环境，培养对家庭、社会的责任感。形成良好的社会交往行为，为将来进入正式的社会生活做准备。\",\"tagid\":\"371\",\"tagnamedesc_en\":\"Cultivate kids\\\\' ability of muscle tissue, enhance the capacity of control body, thus can help your kid transition from natural person to social person, out of excessive dependence on parents. Study self-care ability, to take care of environment, the resp\",\"tagname\":\"蒙氏日常生活区\"},{\"tagname_en\":\"Culture Subjects\",\"isdelete\":\"0\",\"schoolid\":\"27\",\"tagnamedesc\":\"增长幼儿见识，开阔眼界，培养幼儿热爱科学的兴趣及探索求知的精神，使幼儿从小就具有创造性思维和实践能力，了解基本的动植物学、地理学、天文学、历史学知识。\",\"tagid\":\"372\",\"tagnamedesc_en\":\"Increase kids' knowledge, widen horizon, and cultivate kids\\\\' interest in science and exploration spirit, to make kids own the ability of creative thinking and practical. Understand the basic knowledge of animal, plant science, geography, astronomy, and h\",\"tagname\":\"蒙氏科学文化区\"},{\"tagname_en\":\"Language\",\"isdelete\":\"0\",\"schoolid\":\"27\",\"tagnamedesc\":\"培养幼儿听说读写的能力，帮助幼儿更好的书写和阅读，使幼儿在童年时期就形成良好的阅读与书写习惯，培养喜爱阅读的兴趣、增强口语表达能力、强化听力与阅读能力，提高书写能力。\",\"tagid\":\"373\",\"tagnamedesc_en\":\"Cultivate kids\\\\' ability of listen, speak, read and write, to help kids writing and reading better, and form good habits. Cultivate reading interest, strengthen the ability of oral English, listening , reading, and writing.\",\"tagname\":\"蒙氏语言区\"},{\"tagname_en\":\"Outdoor Activities\",\"isdelete\":\"0\",\"schoolid\":\"27\",\"tagnamedesc\":\"课堂间隙户外锻炼，校园外户外活动等，可以使孩子拥有健康强壮的体质，更有阳光、开朗活泼的个性。\",\"tagid\":\"374\",\"tagnamedesc_en\":\"Outdoor exercise and activities, etc. To make kids have a strong and healthy physique, and own optimistic, cheerful and lively personality.\",\"tagname\":\"户外活动\"},{\"tagname_en\":\"Art\",\"isdelete\":\"0\",\"schoolid\":\"27\",\"tagnamedesc\":\"孩子进行绘画、手工制作等艺术学习，挖掘孩子的潜力，培养其自发学习的能力。\",\"tagid\":\"375\",\"tagnamedesc_en\":\"Learning painting, handmade art, etc. To dig kids' potential, and cultivate their ability of learning spontaneously.\",\"tagname\":\"美工区\"},{\"tagname_en\":\"Meal Time\",\"isdelete\":\"0\",\"schoolid\":\"27\",\"tagnamedesc\":\"早餐、早点、午餐、午点、晚餐等学校餐点的进行，健康的饮食，给孩子健康的营养。\",\"tagid\":\"376\",\"tagnamedesc_en\":\"Breakfast, snake AM, lunch, snake PM, dinner etc. The healthy diet, give the kids a healthy nutrition.\",\"tagname\":\"就餐时间\"}],\"id\":\"27\",\"logo\":\"\",\"cover\":\"\",\"address\":\"岳麓区金星中路茉莉花酒店旁\",\"remark\":null,\"settings\":{\"message_type\":[\"Article\",\"Active\",\"Attendance\",\"Bus\",\"Food\",\"Report\",\"Openclass\"],\"teacher_duty\":{\"3\":\"英文老师\",\"2\":\"中文老师\",\"1\":\"主讲老师\",\"4\":\"班级助理\"},\"class_module\":[{\"icon\":\"http:\\/\\/api36.yunxiaoche.com\\/Public\\/images\\/punch.png\",\"url\":\"http:\\/\\/api36.yunxiaoche.com\\/api\\/TeacherMobile\\/punch\",\"title\":\"晨检考勤\"},{\"icon\":\"http:\\/\\/api36.yunxiaoche.com\\/Public\\/images\\/report.png\",\"url\":\"http:\\/\\/api36.yunxiaoche.com\\/api\\/TeacherMobile\\/classReport\",\"title\":\"班级报告\"},{\"icon\":\"http:\\/\\/api36.yunxiaoche.com\\/Public\\/images\\/notice.png\",\"url\":\"http:\\/\\/api36.yunxiaoche.com\\/api\\/TeacherMobile\\/classNotice\",\"title\":\"班级通知\"}]},\"name\":\"萌达_岳麓中心\",\"groupid\":\"21\"},\"28\":{\"tags\":[{\"tagname_en\":\"Sensorial\",\"isdelete\":\"0\",\"schoolid\":\"28\",\"tagnamedesc\":\"培养幼儿视、听、嗅、味、触五种感官能力的敏锐程度，使幼儿的每一项感觉都是灵敏全面发展。为学习数学及其他领域做准备，增强对于社会及知识的感知，培养初步的序列、分列概念和抽象思维能力。\",\"tagid\":\"377\",\"tagnamedesc_en\":\"Cultivate kids sharp degree of the five senses ability--see, hear, smell, and touch, make every sense to all-round development.In preparation for the study mathematics and other fields, enhancement the awareness of social and intellectual, and cultivate t\",\"tagname\":\"蒙氏感官区\"},{\"tagname_en\":\"Mathematics\",\"isdelete\":\"0\",\"schoolid\":\"28\",\"tagnamedesc\":\"培养数的概念，理解加减乘除运算的关系；会做10000以内的加减乘除，会做简单的测量工作；培养逻辑思维能力和创造能力。\",\"tagid\":\"378\",\"tagnamedesc_en\":\"Understand the concepts of number and the relationship between addition, subtraction, multiplication, and division. Have the ability to do the addition, subtraction, multiplication, and division within 1000, and simple measurements. Cultivate the ability \",\"tagname\":\"蒙氏数学区\"},{\"tagname_en\":\"Practical Life\",\"isdelete\":\"0\",\"schoolid\":\"28\",\"tagnamedesc\":\"培养幼儿的大肌肉和小肌肉的组织能力，增强对自己身体的控制能力，从而把孩子从自然人向社会人过渡，脱离对父母的过度依赖。学习生活自理能力，学会帮助别人，照顾环境，培养对家庭、社会的责任感。形成良好的社会交往行为，为将来进入正式的社会生活做准备。\",\"tagid\":\"379\",\"tagnamedesc_en\":\"Cultivate kids\\\\' ability of muscle tissue, enhance the capacity of control body, thus can help your kid transition from natural person to social person, out of excessive dependence on parents. Study self-care ability, to take care of environment, the resp\",\"tagname\":\"蒙氏日常生活区\"},{\"tagname_en\":\"Culture Subjects\",\"isdelete\":\"0\",\"schoolid\":\"28\",\"tagnamedesc\":\"增长幼儿见识，开阔眼界，培养幼儿热爱科学的兴趣及探索求知的精神，使幼儿从小就具有创造性思维和实践能力，了解基本的动植物学、地理学、天文学、历史学知识。\",\"tagid\":\"380\",\"tagnamedesc_en\":\"Increase kids' knowledge, widen horizon, and cultivate kids\\\\' interest in science and exploration spirit, to make kids own the ability of creative thinking and practical. Understand the basic knowledge of animal, plant science, geography, astronomy, and h\",\"tagname\":\"蒙氏科学文化区\"},{\"tagname_en\":\"Language\",\"isdelete\":\"0\",\"schoolid\":\"28\",\"tagnamedesc\":\"培养幼儿听说读写的能力，帮助幼儿更好的书写和阅读，使幼儿在童年时期就形成良好的阅读与书写习惯，培养喜爱阅读的兴趣、增强口语表达能力、强化听力与阅读能力，提高书写能力。\",\"tagid\":\"381\",\"tagnamedesc_en\":\"Cultivate kids\\\\' ability of listen, speak, read and write, to help kids writing and reading better, and form good habits. Cultivate reading interest, strengthen the ability of oral English, listening , reading, and writing.\",\"tagname\":\"蒙氏语言区\"},{\"tagname_en\":\"Outdoor Activities\",\"isdelete\":\"0\",\"schoolid\":\"28\",\"tagnamedesc\":\"课堂间隙户外锻炼，校园外户外活动等，可以使孩子拥有健康强壮的体质，更有阳光、开朗活泼的个性。\",\"tagid\":\"382\",\"tagnamedesc_en\":\"Outdoor exercise and activities, etc. To make kids have a strong and healthy physique, and own optimistic, cheerful and lively personality.\",\"tagname\":\"户外活动\"},{\"tagname_en\":\"Art\",\"isdelete\":\"0\",\"schoolid\":\"28\",\"tagnamedesc\":\"孩子进行绘画、手工制作等艺术学习，挖掘孩子的潜力，培养其自发学习的能力。\",\"tagid\":\"383\",\"tagnamedesc_en\":\"Learning painting, handmade art, etc. To dig kids' potential, and cultivate their ability of learning spontaneously.\",\"tagname\":\"美工区\"},{\"tagname_en\":\"Meal Time\",\"isdelete\":\"0\",\"schoolid\":\"28\",\"tagnamedesc\":\"早餐、早点、午餐、午点、晚餐等学校餐点的进行，健康的饮食，给孩子健康的营养。\",\"tagid\":\"384\",\"tagnamedesc_en\":\"Breakfast, snake AM, lunch, snake PM, dinner etc. The healthy diet, give the kids a healthy nutrition.\",\"tagname\":\"就餐时间\"}],\"id\":\"28\",\"logo\":\"\",\"cover\":\"\",\"address\":\"岳麓区银杉路星蓝湾小区\",\"remark\":null,\"settings\":{\"message_type\":[\"Article\",\"Active\",\"Attendance\",\"Bus\",\"Food\",\"Report\",\"Openclass\"],\"teacher_duty\":{\"3\":\"英文老师\",\"2\":\"中文老师\",\"1\":\"主讲老师\",\"4\":\"班级助理\"},\"class_module\":[{\"icon\":\"http:\\/\\/api36.yunxiaoche.com\\/Public\\/images\\/punch.png\",\"url\":\"http:\\/\\/api36.yunxiaoche.com\\/api\\/TeacherMobile\\/punch\",\"title\":\"晨检考勤\"},{\"icon\":\"http:\\/\\/api36.yunxiaoche.com\\/Public\\/images\\/report.png\",\"url\":\"http:\\/\\/api36.yunxiaoche.com\\/api\\/TeacherMobile\\/classReport\",\"title\":\"班级报告\"},{\"icon\":\"http:\\/\\/api36.yunxiaoche.com\\/Public\\/images\\/notice.png\",\"url\":\"http:\\/\\/api36.yunxiaoche.com\\/api\\/TeacherMobile\\/classNotice\",\"title\":\"班级通知\"}]},\"name\":\"萌达_星蓝湾托管\",\"groupid\":\"21\"}},\"groups\":[{\"createtime\":\"1427853121\",\"id\":\"21\",\"logo\":\"\",\"cover\":\"\",\"remark\":null,\"status\":\"0\",\"email\":\"\",\"name_en\":\"Montastar\",\"is_operation\":\"0\",\"name\":\"萌达蒙特梭利教育机构\",\"aboutus\":null,\"type\":\"0\",\"mobile\":\"\"}],\"teachers\":[{\"classes\":[{\"duty\":\"班主任\",\"dutyid\":\"4\",\"classid\":\"38\"}],\"schoolid\":\"27\",\"sex\":\"2\",\"pictureid\":\"25747\",\"duty\":\"班主任\",\"nickname\":\"梅子\",\"teacherid\":\"818\",\"realname\":\"梅子\",\"avatar\":\"http:\\/\\/s3.cn-north-1.amazonaws.com.cn\\/client\\/thumbs\\/d40fcaff188d790cab5094df74a4f319.jpg\",\"dutyid\":\"4\",\"mobile\":\"13047214309\"},{\"classes\":[{\"duty\":\"班主任\",\"dutyid\":\"1\",\"classid\":\"37\"}],\"schoolid\":\"27\",\"sex\":\"2\",\"pictureid\":\"25085\",\"duty\":\"班主任\",\"nickname\":\"YOYO\",\"teacherid\":\"820\",\"realname\":\"YOYO\",\"avatar\":\"http:\\/\\/cloud.yunxiaoche.com\\/images\\/teacher.jpg\",\"dutyid\":\"1\",\"mobile\":\"13875991836\"},{\"classes\":[{\"duty\":\"班主任\",\"dutyid\":\"4\",\"classid\":\"37\"}],\"schoolid\":\"27\",\"sex\":\"2\",\"pictureid\":\"25085\",\"duty\":\"班主任\",\"nickname\":\"高文倩\",\"teacherid\":\"821\",\"realname\":\"高文倩\",\"avatar\":\"http:\\/\\/cloud.yunxiaoche.com\\/images\\/teacher.jpg\",\"dutyid\":\"4\",\"mobile\":\"18390918600\"},{\"classes\":[{\"duty\":\"班主任\",\"dutyid\":\"0\",\"classid\":\"37\"},{\"duty\":\"班主任\",\"dutyid\":\"0\",\"classid\":\"38\"},{\"duty\":\"班主任\",\"dutyid\":\"0\",\"classid\":\"39\"}],\"schoolid\":\"27\",\"sex\":\"2\",\"pictureid\":\"25085\",\"duty\":\"班主任\",\"nickname\":\"欧阳洋\",\"teacherid\":\"840\",\"realname\":\"欧阳洋\",\"avatar\":\"http:\\/\\/cloud.yunxiaoche.com\\/images\\/teacher.jpg\",\"dutyid\":\"0\",\"mobile\":\"17752870517\"},{\"classes\":[{\"duty\":\"班主任\",\"dutyid\":\"4\",\"classid\":\"39\"}],\"schoolid\":\"28\",\"sex\":\"2\",\"pictureid\":\"25085\",\"duty\":\"班主任\",\"nickname\":\"曹佳佳\",\"teacherid\":\"841\",\"realname\":\"曹佳佳\",\"avatar\":\"http:\\/\\/cloud.yunxiaoche.com\\/images\\/teacher.jpg\",\"dutyid\":\"4\",\"mobile\":\"18570602230\"}]}";
//                        baseinfo = FastJsonTools.getObject(response.toString(), BaseInfoT.class);
                        baseinfo = FastJsonTools.getObject(responseString, BaseInfoT.class);

                        //Save School Information
                        if (baseinfo instanceof BaseInfoT) {
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
                                schoolDb.setCover(school.getCover());
                                schoolDb.setLogo(school.getLogo());
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
                                    classModuleDb.setId(Integer.toString(i) + school.getId());
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
                InputProvider.ExtendProvider[] provider = {
                        new ImageInputProvider(RongContext.getInstance()),//图片
                        new CameraInputProvider(RongContext.getInstance()),//相机
//                        new LocationInputProvider(RongContext.getInstance()),//地理位置
//                        new VoIPInputProvider(RongContext.getInstance()),// 语音通话
                };
                RongIM.getInstance().resetInputExtensionProvider(Conversation.ConversationType.PRIVATE, provider);
                RongCloudEvent.getInstance().setOtherListener();
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

