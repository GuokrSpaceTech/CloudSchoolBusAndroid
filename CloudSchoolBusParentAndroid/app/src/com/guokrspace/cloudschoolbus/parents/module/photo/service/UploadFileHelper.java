package com.guokrspace.cloudschoolbus.parents.module.photo.service;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.android.support.debug.DebugLog;
import com.guokrspace.cloudschoolbus.parents.CloudSchoolBusParentsApplication;
import com.guokrspace.cloudschoolbus.parents.R;
import com.guokrspace.cloudschoolbus.parents.database.daodb.UploadingPhotoEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.UploadingPhotoEntityDao;
import com.guokrspace.cloudschoolbus.parents.entity.UploadFile;
import com.guokrspace.cloudschoolbus.parents.event.BusProvider;
import com.guokrspace.cloudschoolbus.parents.event.FileUploadedEvent;
import com.guokrspace.cloudschoolbus.parents.module.photo.UploadListFragment;
import com.guokrspace.cloudschoolbus.parents.protocols.CloudSchoolBusRestClient;
import com.guokrspace.cloudschoolbus.parents.protocols.ProtocolDef;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
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
	private UploadingPhotoEntityDao mUploadDB;

	private List<UploadFile> mUploadFileList = new ArrayList<UploadFile>();

	private static UploadFileHelper sUploadUtils = new UploadFileHelper();

    public static UploadFileHelper getUploadUtils() {
		return sUploadUtils;
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
		mUploadDB = mApplication.mDaoSession.getUploadingPhotoEntityDao();
	}

	public void setFragment(Fragment fragment) {
		mFragment = fragment;
	}

	/**
	 * 将上传文件保存到数据库中
	 * 
	 * @param uploadFileList
	 */
	public void setUploadFileDB(List<UploadFile> uploadFileList) {
		if (null == mContext) {
			throw new NullPointerException(
					"no call method setContext(Context context)");
		}

		// 保存数据
		for (int j = 0; j < uploadFileList.size(); j++) {
			mApplication.mDaoSession.getUploadingPhotoEntityDao().insert(objToDbEntity(uploadFileList.get(j)));
		}

		mUploadFileList.addAll(uploadFileList);
	}

	/**
	 * 在数据库中读取数据，主要用在软件关闭打开得时候加载数据，继续上传
	 */
	public void readUploadFileDB() {

		if (null == mContext) {
			throw new NullPointerException(
					"no call method setContext(Context context)");
		}

		// 读取数据
		mUploadFileList.clear();
		List<UploadingPhotoEntity> uploadingPhotoEntities = mApplication.mDaoSession.getUploadingPhotoEntityDao().queryBuilder().list();
		for( UploadingPhotoEntity entity : uploadingPhotoEntities )
		{
			mUploadFileList.add(dBEntityToObj(entity));
		}
	}

	/**
	 * 开启线程上传文件，每次只上传一个其余等待,在setUploadFileDB方法之后调用
	 */
	public void uploadFileService() {
		if (null == mContext) {
			throw new NullPointerException(
					"no call method setContext(Context context)");
		}
		
		uploadFirstFile();
	}

	private void uploadFirstFile() {
		if (mUploadFileList.size() > 0) {
			UploadFile uploadFile = mUploadFileList.get(0);
			if (null == uploadFile.requestHandle) {
				uploadFileNew(mUploadFileList.get(0));
			}
		}
	}

	public List<UploadFile> getUploadFiles() {
		return mUploadFileList;
	}

	public void remove(UploadFile uploadFile) {
		int removeIndex = 0;
        boolean found = false;
		for (int i = 0; i < mUploadFileList.size(); i++) {
			if (mUploadFileList.get(i).picPathString
					.equals(uploadFile.picPathString)) {
                found = true;
				removeIndex = i;
				break;
			}
		}

        if(found == true)
		   mUploadFileList.remove(removeIndex);
	}

	private synchronized void uploadFile(final UploadFile uploadFile) {

		DebugLog.logI("uploadFile11111111111111111111111111111111111");

		String picPathString = uploadFile.picPathString
				.replace("file:///", "/");
		String fname = null;
		String ftime = null;
		if (!TextUtils.isEmpty(picPathString)) {
			File file= new File(picPathString);
			ftime = (file.lastModified()/1000) + "";
			fname = picPathString.substring(picPathString.lastIndexOf("/") + 1);
		}

		RequestParams params = new RequestParams();
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inDither = false;                     //Disable Dithering mode
		opts.inPurgeable = true;                   //Tell to gc that whether it needs free memory, the Bitmap can be cleared
		opts.inInputShareable = true;              //Which kind of reference will be used to recover the Bitmap data after being clear, when it will be used in the future
		opts.inTempStorage = new byte[32 * 1024];
		Bitmap bmp = BitmapFactory.decodeFile(picPathString);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		bmp.compress(Bitmap.CompressFormat.JPEG, 70, bos);
		InputStream in = new ByteArrayInputStream(bos.toByteArray());
		params.put("fbody", in);
		params.put("pictype", "article");
		if (!TextUtils.isEmpty(fname)) {
			params.put("fname", fname);
		}
		if (!TextUtils.isEmpty(uploadFile.studentIdList)) {
			params.put("memberlist", uploadFile.studentIdList);
		}
		if (!TextUtils.isEmpty(ftime)) {
			params.put("ftime", ftime);
		}
		if (!TextUtils.isEmpty(uploadFile.teacherid)) {
			params.put("uid", uploadFile.teacherid);
		}
		if (!TextUtils.isEmpty(uploadFile.intro) && !"null".equals(uploadFile.intro)) {
			params.put("intro", uploadFile.intro);
		}
		if (!TextUtils.isEmpty(uploadFile.photoTag) && !"null".equals(uploadFile.photoTag)) {
			params.put("tag", uploadFile.photoTag);
		}
		if (!TextUtils.isEmpty(uploadFile.teacherid)) {
			params.put("teacherid", uploadFile.teacherid);
		}
		params.put("register", 0); //Normal
		params.put("pickey", uploadFile.generateKey());

		DebugLog.logI("mMethod : " + ProtocolDef.METHOD_Source + " RequestParams : " + params.toString());
		CloudSchoolBusRestClient.upload(ProtocolDef.METHOD_Source, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				// 上传成功，更新上传列表
				mUploadDB.delete(objToDbEntity(uploadFile));
				mUploadFileList.remove(uploadFile);
				FileUploadedEvent event = new FileUploadedEvent(uploadFile);
				event.setIsSuccess(true);
				BusProvider.getInstance().post(event);
				uploadFirstFile();
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                mUploadDB.delete(objToDbEntity(uploadFile));
                mUploadFileList.remove(uploadFile);
                FileUploadedEvent event = new FileUploadedEvent(uploadFile);
                event.setIsSuccess(true);
                BusProvider.getInstance().post(event);
                uploadFirstFile();
			}

			@Override
			public void onProgress(long bytesWritten, long totalSize) {
				super.onProgress(bytesWritten, totalSize);
				uploadFile.progress = (int) (((double) bytesWritten / (double) totalSize) * 100);
				UploadListFragment fragment = (UploadListFragment) mFragment;
				View view = fragment.mUploadFileAdapter
						.getFirstView();
				if (null != view) {
					TextView progressTextView = (TextView) view
							.findViewById(R.id.progressTextView);
					progressTextView
							.setText(uploadFile.progress
									+ "%");
				}
			}
		});
	}


    private synchronized void uploadFileNew(final UploadFile uploadFile) {

        DebugLog.logI("uploadFile11111111111111111111111111111111111");

        String picPathString = uploadFile.picPathString
                .replace("file:///", "/");
        String fname = null;
        String ftime = null;
        if (!TextUtils.isEmpty(picPathString)) {
            File file= new File(picPathString);
            ftime = (file.lastModified()/1000) + "";
            fname = picPathString.substring(picPathString.lastIndexOf("/") + 1);
        }

        RequestParams params = new RequestParams();
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inDither = false;                     //Disable Dithering mode
        opts.inPurgeable = true;                   //Tell to gc that whether it needs free memory, the Bitmap can be cleared
        opts.inInputShareable = true;              //Which kind of reference will be used to recover the Bitmap data after being clear, when it will be used in the future
        opts.inTempStorage = new byte[32 * 1024];
        Bitmap bmp = BitmapFactory.decodeFile(picPathString);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 70, bos);
        InputStream in = new ByteArrayInputStream(bos.toByteArray());
        params.put("fbody", in);
        params.put("pictype", "article");
        if (!TextUtils.isEmpty(fname)) {
            params.put("fname", fname);
        }
        if (!TextUtils.isEmpty(ftime)) {
            params.put("ftime", ftime);
        }
        params.put("pickey", uploadFile.generateKey());

        DebugLog.logI("mMethod : " + ProtocolDef.METHOD_upload + " RequestParams : " + params.toString());
        CloudSchoolBusRestClient.upload(ProtocolDef.METHOD_upload, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                // 上传成功，更新上传列表

                String retString = new String(responseBody);
                mUploadDB.delete(objToDbEntity(uploadFile));
                mUploadFileList.remove(uploadFile);
                FileUploadedEvent event = new FileUploadedEvent(uploadFile);
                event.setIsSuccess(true);
                BusProvider.getInstance().post(event);
                uploadFirstFile();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                mUploadDB.delete(objToDbEntity(uploadFile));
                mUploadFileList.remove(uploadFile);
                FileUploadedEvent event = new FileUploadedEvent(uploadFile);
                event.setIsSuccess(true);
                BusProvider.getInstance().post(event);
                uploadFirstFile();
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                super.onProgress(bytesWritten, totalSize);
                uploadFile.progress = (int) (((double) bytesWritten / (double) totalSize) * 100);
                UploadListFragment fragment = (UploadListFragment) mFragment;
                View view = fragment.mUploadFileAdapter
                        .getFirstView();
                if (null != view) {
                    TextView progressTextView = (TextView) view
                            .findViewById(R.id.progressTextView);
                    progressTextView
                            .setText(uploadFile.progress
                                    + "%");
                }
            }
        });
    }



    private UploadingPhotoEntity objToDbEntity(UploadFile obj)
	{
		UploadingPhotoEntity entity = new UploadingPhotoEntity();
		entity.setClassuid(obj.classuid);
		entity.setIntro(obj.intro);
		entity.setPhotoTag(obj.photoTag);
		entity.setPicFileString(obj.picFileString);
		entity.setPicPathString(obj.picPathString);
		entity.setStudentId(obj.studentIdList);
		entity.setPicSizeString(obj.picSizeString);
		entity.setTeacherid(obj.teacherid);
        entity.setKey(obj.generateKey());
		return entity;
	}

	private UploadFile dBEntityToObj(UploadingPhotoEntity dbEntity)
	{
		UploadFile uploadFile = new UploadFile();
		uploadFile.studentIdList = dbEntity.getStudentId();
		uploadFile.classuid = dbEntity.getClassuid();
		uploadFile.intro = dbEntity.getIntro();
		uploadFile.photoTag = dbEntity.getPhotoTag();
		uploadFile.picFileString = dbEntity.getPicFileString();
		uploadFile.picPathString = dbEntity.getPicPathString();
		uploadFile.picSizeString = dbEntity.getPicSizeString();
		uploadFile.teacherid = dbEntity.getTeacherid();
        uploadFile.key = dbEntity.getKey();
		return uploadFile;
	}
}
