package com.guokrspace.cloudschoolbus.teacher.module.photo.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.Fragment;

import com.android.support.debug.DebugLog;
import com.guokrspace.cloudschoolbus.teacher.CloudSchoolBusParentsApplication;
import com.guokrspace.cloudschoolbus.teacher.database.daodb.UploadArticleEntity;
import com.guokrspace.cloudschoolbus.teacher.database.daodb.UploadArticleEntityDao;
import com.guokrspace.cloudschoolbus.teacher.database.daodb.UploadArticleFileEntity;
import com.guokrspace.cloudschoolbus.teacher.event.BusProvider;
import com.guokrspace.cloudschoolbus.teacher.event.FileUploadedEvent;
import com.guokrspace.cloudschoolbus.teacher.protocols.CloudSchoolBusRestClient;
import com.guokrspace.cloudschoolbus.teacher.protocols.ProtocolDef;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * 文件上传类
 * 
 * @author Yang Kai
 * 
 */
public class UploadFileHelper extends Service {
    private Context mContext;
    private Fragment mFragment;
    private Thread thread;
    private int IDLE_INTERVAL = 5000;
    private int BUSY_INTERVAL = 1000;
	private CloudSchoolBusParentsApplication mApplication;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            if(message.what == MSG_UPLOAD)
            {
                if( uploadNextFile() == 0 ) //no file to upload
                {
                    handler.sendEmptyMessageDelayed(MSG_UPLOAD,IDLE_INTERVAL);
                }
            }
            return false;
        }
    });
    private static int MSG_UPLOAD = 1;

    private static UploadFileHelper uploadFileHelper = new UploadFileHelper();

    public static UploadFileHelper getInstance() {
		return uploadFileHelper;
	}

    public class LocalBinder extends Binder {
        public UploadFileHelper getService() {
            return UploadFileHelper.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    // This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.
    private final IBinder mBinder = new LocalBinder();

	/**
	 * 最先调用否则都不能使用,再每个界面开始的时候都需要调用
	 * 
	 * @param context
	 */
	public void setContext(Context context) {
		mContext = context;
		mApplication = (CloudSchoolBusParentsApplication) mContext
				.getApplicationContext();
	}


    @Override
    public void onCreate() {
        super.onCreate();

//        handler.sendEmptyMessage(MSG_KICKOFF);
    }



    public void setFragment(Fragment fragment) {
        mFragment = fragment;
    }

	/**
	 * 开启上传文件，每次只上传一个其余等待,在setUploadFileDB方法之后调用
	 */
	public void startUploadFileService() {
		if (null == mContext) {
            throw new NullPointerException(
                    "no call method setContext(Context context)");
        }

        handler.sendEmptyMessageDelayed(MSG_UPLOAD, IDLE_INTERVAL);
	}

    private int uploadNextFile() {
        //Check uploadable files
        List<UploadArticleEntity> uploadArticles = mApplication.mDaoSession.getUploadArticleEntityDao().queryBuilder().list();
        for (UploadArticleEntity uploadArticle : uploadArticles) {
            List<UploadArticleFileEntity> uploadFiles = uploadArticle.getUploadArticleFileEntityList();
            for (UploadArticleFileEntity uploadFile : uploadFiles) {
                if (uploadFile.getIsSuccess() == null) {
                    uploadFile(uploadFile);
                    return 1;
                }
            }
        }

        //no file found
        return 0;
    }

    public void retryFailedFile(UploadArticleFileEntity uploadFile)
    {
        markUploadNull(uploadFile);
    }

    public void removeFailedFile(UploadArticleFileEntity uploadFile)
    {
        mApplication.mDaoSession.getUploadArticleFileEntityDao().delete(uploadFile);
        mApplication.mDaoSession.clear();
    }

	private synchronized void uploadFile(final UploadArticleFileEntity uploadFile) {

		RequestParams params = new RequestParams();
        params.put("fname", uploadFile.getFname());
        try {
            params.put("fbody", new File(compressUploadSource(uploadFile)));
//            params.put("fbody", new File(uploadFile.getFbody()));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        params.put("pickey", uploadFile.getPickey());
        params.put("pictype", uploadFile.getPictype());
        params.put("ftime", uploadFile.getFtime());

//		DebugLog.logI("mMethod : " + ProtocolDef.METHOD_upload + " RequestParams : " + params.toString());
		CloudSchoolBusRestClient.upload(ProtocolDef.METHOD_upload, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                //Remove the upload Q in DB
                MarkUplodSuccess(uploadFile);

//                //Notify the fragment or activity to update list view
                FileUploadedEvent event = new FileUploadedEvent(uploadFile);
                event.setIsSuccess(true);
                BusProvider.getInstance().post(event);

                //Kick off next load
                handler.sendEmptyMessageDelayed(MSG_UPLOAD, BUSY_INTERVAL);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                //Todo: needs to handle the failure scenario
                //Remove the upload Q in DB
                markUploadFailure(uploadFile);

//                //Notify the fragment or activity to update list view
                FileUploadedEvent event = new FileUploadedEvent(uploadFile);
                mApplication.mDaoSession.getUploadArticleFileEntityDao().update(uploadFile);

                event.setIsSuccess(false);
                BusProvider.getInstance().post(event);

                //Kick off next load
                handler.sendEmptyMessageDelayed(MSG_UPLOAD, BUSY_INTERVAL);
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                super.onProgress(bytesWritten, totalSize);
                int progress = (int) (((double) bytesWritten / (double) totalSize) * 100);
//                SendingRecordFragment fragment = (SendingRecordFragment) mFragment;
//                View view = fragment.mUploadFileAdapter.getFirstView();
//                if (null != view) {
//                    TextView progressTextView = (TextView) view
//                            .findViewById(R.id.progressTextView);
//                    progressTextView
//                            .setText(progress + "%");
//                }
            }
        });
	}


    private synchronized void setArticleParameters(final UploadArticleEntity uploadArticle) {

        RequestParams params = new RequestParams();

        params.put("pickey", uploadArticle.getPickey());
        params.put("pictype", uploadArticle.getPictype());
        params.put("classid", uploadArticle.getClassid());
        params.put("teacherid", uploadArticle.getTeacherid());
        params.put("studentids", uploadArticle.getStudentids());
        params.put("tagids", uploadArticle.getTagids());
        params.put("content", uploadArticle.getContent());

//        DebugLog.logI("mMethod : " + ProtocolDef.METHOD_over + " RequestParams : " + params.toString());
        CloudSchoolBusRestClient.upload(ProtocolDef.METHOD_over, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                // 上传成功，更新上传列表
                markUploadAriticleSuccess(uploadArticle);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                //Todo: handle failure scenario
                markUploadAriticleSuccess(uploadArticle);
            }
        });
    }


    public List<UploadArticleEntity> readUploadQ ()
    {
        return mApplication.mDaoSession.getUploadArticleEntityDao().queryBuilder().list();

    }

    public List<UploadArticleFileEntity> readUploadFileQ ()
    {
        return mApplication.mDaoSession.getUploadArticleFileEntityDao().queryBuilder().list();
    }

    //Get the articles sent including being sent
    public List<UploadArticleEntity> readUploadArticleQ ()
    {
        return mApplication.mDaoSession.getUploadArticleEntityDao().queryBuilder()
                .orderDesc(UploadArticleEntityDao.Properties.Sendtime)
                .list();
    }

    public void MarkUplodSuccess(UploadArticleFileEntity uploadfile)
    {
        // the uploadfile;
        uploadfile.setIsSuccess(true);
        mApplication.mDaoSession.getUploadArticleFileEntityDao().update(uploadfile);

        //Find its parental article
        String pickey = uploadfile.getPickey();

        List<UploadArticleEntity> articles = mApplication.mDaoSession
                .getUploadArticleEntityDao().queryBuilder()
                .where(UploadArticleEntityDao.Properties.Pickey.eq(pickey))
                .list();

        if(articles.size()==1)
        {
            //If all files in that article have been uploaded
            boolean isAllUploaded = true;
            for(UploadArticleFileEntity file : articles.get(0).getUploadArticleFileEntityList() )
            {
                //Check if all files in the article have been uploaded
                if(file.getIsSuccess() == null || !file.getIsSuccess()) {
                    isAllUploaded = false;
                }
            }

            if(isAllUploaded)
            {
                //Send the "Over" request to Server, when it success, remove the aritice from the queue
                setArticleParameters(articles.get(0));
            }
        } else {
            DebugLog.logI("UploadQ out of sync");
        }

        try {
            File file = new File(mApplication.mCacheDir, uploadfile.getFname() + ".small.jpg");
            file.delete();
        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public void markUploadFailure(UploadArticleFileEntity uploadfile)
    {
        uploadfile.setIsSuccess(false);
        mApplication.mDaoSession.getUploadArticleFileEntityDao().update(uploadfile);
    }

    //Add back to upload Q
    public void markUploadNull(UploadArticleFileEntity uploadfile)
    {
        uploadfile.setIsSuccess(null);
        mApplication.mDaoSession.getUploadArticleFileEntityDao().update(uploadfile);
    }


    private void markUploadAriticleSuccess(UploadArticleEntity uploadArticle)
    {
        mApplication.mDaoSession.getUploadArticleEntityDao().delete(uploadArticle);
        mApplication.mDaoSession.clear();
    }

    public String compressUploadSource(UploadArticleFileEntity fileEntity)
    {

        File compressFile = new File(mApplication.mCacheDir, fileEntity.getFname() + ".small.jpg");

        if(!compressFile.exists()) {
            try {
                compressFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return fileEntity.getFbody();
            }
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inDither = false;                     //Disable Dithering mode
            opts.inPurgeable = true;                   //Tell to gc that whether it needs free memory, the Bitmap can be cleared
            opts.inInputShareable = true;              //Which kind of reference will be used to recover the Bitmap data after being clear, when it will be used in the future
            opts.inTempStorage = new byte[32 * 1024];
            Bitmap bmp = BitmapFactory.decodeFile(fileEntity.getFbody(), opts);
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(compressFile);
            } catch (java.io.IOException e) {
                e.printStackTrace();
                return fileEntity.getFbody();
            }
            bmp.compress(Bitmap.CompressFormat.JPEG, 70, fos);
        }
        return compressFile.getAbsolutePath();
    }
}