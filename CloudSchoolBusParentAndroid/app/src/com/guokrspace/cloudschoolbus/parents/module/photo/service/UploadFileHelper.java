package com.guokrspace.cloudschoolbus.parents.module.photo.service;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.android.support.debug.DebugLog;
import com.guokrspace.cloudschoolbus.parents.CloudSchoolBusParentsApplication;
import com.guokrspace.cloudschoolbus.parents.database.daodb.StudentEntityT;
import com.guokrspace.cloudschoolbus.parents.database.daodb.TagsEntityT;
import com.guokrspace.cloudschoolbus.parents.database.daodb.UploadArticleEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.UploadArticleEntityDao;
import com.guokrspace.cloudschoolbus.parents.database.daodb.UploadArticleFileEntity;
import com.guokrspace.cloudschoolbus.parents.event.BusProvider;
import com.guokrspace.cloudschoolbus.parents.event.FileUploadedEvent;
import com.guokrspace.cloudschoolbus.parents.protocols.CloudSchoolBusRestClient;
import com.guokrspace.cloudschoolbus.parents.protocols.ProtocolDef;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import net.soulwolf.image.picturelib.model.Picture;

import org.apache.http.Header;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件上传类
 * 
 * @author Yang Kai
 * 
 */
public class UploadFileHelper {

	private Context mContext;
	private Fragment mFragment;
	private CloudSchoolBusParentsApplication mApplication;

	private static UploadFileHelper uploadFileHelper = new UploadFileHelper();

    public static UploadFileHelper getInstance() {
		return uploadFileHelper;
	}

	/**
	 * 最先调用否则都不能使用,再每个界面开始的时候都需要调用
	 * 
	 * @param context
	 */
	public void setContext(Context context) {
		mContext = context;
		mApplication = (CloudSchoolBusParentsApplication) mContext
				.getApplicationContext();
//        mUploadArticleQ = readUploadQ();
	}

	public void setFragment(Fragment fragment) {
		mFragment = fragment;
	}


	/**
	 * 开启上传文件，每次只上传一个其余等待,在setUploadFileDB方法之后调用
	 */
	public void uploadFileService() {
		if (null == mContext) {
			throw new NullPointerException(
					"no call method setContext(Context context)");
		}
		
		uploadNextFile();
	}

    private void uploadNextFile() {
        //Check uploadable files
        List<UploadArticleEntity> uploadArticles = mApplication.mDaoSession.getUploadArticleEntityDao().queryBuilder().list();
        for (UploadArticleEntity uploadArticle : uploadArticles) {
            List<UploadArticleFileEntity> uploadFiles = uploadArticle.getUploadArticleFileEntityList();
            for (UploadArticleFileEntity uploadFile : uploadFiles) {
                if (uploadFile.getIsSuccess() == null) {
                    uploadFile(uploadFile);
                    break;
                }
            }
        }
    }

    public void retryFailedFile(UploadArticleFileEntity uploadFile)
    {
        uploadFile(uploadFile);
    }

	private synchronized void uploadFile(final UploadArticleFileEntity uploadFile) {

		RequestParams params = new RequestParams();
        params.put("fname", uploadFile.getFname());
        try {
            params.put("fbody", new File(uploadFile.getFbody()));
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

                //Notify the fragment or activity to update list view
                FileUploadedEvent event = new FileUploadedEvent(uploadFile);
                event.setIsSuccess(true);
                BusProvider.getInstance().post(event);

                //Kick off next load
                uploadNextFile();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                //Todo: needs to handle the failure scenario
                //Remove the upload Q in DB
                markUploadFailure(uploadFile);

                //Notify the fragment or activity to update list view
                FileUploadedEvent event = new FileUploadedEvent(uploadFile);
                mApplication.mDaoSession.getUploadArticleFileEntityDao().update(uploadFile);

                event.setIsSuccess(false);
                BusProvider.getInstance().post(event);

                //Kick off next load
                uploadNextFile();
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                super.onProgress(bytesWritten, totalSize);
                int progress = (int) (((double) bytesWritten / (double) totalSize) * 100);
//                SentRecordFragment fragment = (SentRecordFragment) mFragment;
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

        DebugLog.logI("uploadFile11111111111111111111111111111111111");
        RequestParams params = new RequestParams();

        params.put("pickey", uploadArticle.getPickey());
        params.put("pictype", uploadArticle.getPictype());
        params.put("classid", uploadArticle.getClassid());
        params.put("teacherid", uploadArticle.getTeacherid());
        params.put("studentids", generateStudentidstring(uploadArticle));
        params.put("tagids", generateTagidString(uploadArticle));
        params.put("content", uploadArticle.getContent());

        DebugLog.logI("mMethod : " + ProtocolDef.METHOD_over + " RequestParams : " + params.toString());
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


    public void addUploadQueue( ArrayList<Picture> pictureList, String content, String pickey)
    {
        int currentclass = mApplication.mConfig.getCurrentChild();
        String classid = mApplication.mTeacherClassDutys.get(currentclass).getClassid();

        UploadArticleEntity uploadArticle = new UploadArticleEntity();
        uploadArticle.setPickey(pickey);
        uploadArticle.setTeacherid(mApplication.mConfig.getUserid());
        uploadArticle.setPictype("article");
        uploadArticle.setClassid(classid);
        uploadArticle.setContent(content);
        uploadArticle.setSendtime(System.currentTimeMillis()/1000+"");

        mApplication.mDaoSession.getUploadArticleEntityDao().insert(uploadArticle);

        for(Picture picture:pictureList)
        {
            UploadArticleFileEntity uploadFile = new UploadArticleFileEntity();

            String picPathString = picture.getPicturePath().replace("file:///", "/");
            String fname = "";
            String ftime = "";
            if (!TextUtils.isEmpty(picPathString)) {
                File file= new File(picPathString);
                ftime = (file.lastModified()/1000) + "";
                fname = picPathString.substring(picPathString.lastIndexOf("/") + 1);
            }

            uploadFile.setFname(fname);
            uploadFile.setFtime(ftime);
            uploadFile.setFbody(picPathString);
            uploadFile.setPictype("article");
            uploadFile.setPickey(pickey);

            mApplication.mDaoSession.getUploadArticleFileEntityDao().insertOrReplace(uploadFile);
        }
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
    }

    public void markUploadFailure(UploadArticleFileEntity uploadfile)
    {
        uploadfile.setIsSuccess(false);
        mApplication.mDaoSession.getUploadArticleFileEntityDao().update(uploadfile);
    }


    private void markUploadAriticleSuccess(UploadArticleEntity uploadArticle)
    {
//        mApplication.mDaoSession.getUploadArticleEntityDao().delete(uploadArticle);
//        mApplication.mDaoSession.clear();
    }

    public String generateStudentidstring(UploadArticleEntity article)
    {
        String retStr = "";
        for(StudentEntityT student : article.getStudentEntityTList())
        {
            retStr += student.getStudentid() + ",";
        }

        if(!retStr.equals("")) {
            int end = retStr.lastIndexOf(',');
            retStr = retStr.substring(0, end);
        }

        return retStr;
    }

    public String generateTagidString(UploadArticleEntity article)
    {
        String retStr = "";
        for(TagsEntityT tag : article.getTagsEntityTList())
        {
            retStr += tag.getTagid() + ",";
        }

        if(!retStr.equals("")) {
            int end = retStr.lastIndexOf(',');
            retStr = retStr.substring(0, end);
        }

        return retStr;
    }

    public ByteArrayOutputStream generateUplodinputString(UploadArticleFileEntity fileEntity)
    {

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inDither = false;                     //Disable Dithering mode
        opts.inPurgeable = true;                   //Tell to gc that whether it needs free memory, the Bitmap can be cleared
        opts.inInputShareable = true;              //Which kind of reference will be used to recover the Bitmap data after being clear, when it will be used in the future
        opts.inTempStorage = new byte[32 * 1024];
        Bitmap bmp = BitmapFactory.decodeFile(fileEntity.getFbody(),opts);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 70, bos);
        return bos;
    }
}