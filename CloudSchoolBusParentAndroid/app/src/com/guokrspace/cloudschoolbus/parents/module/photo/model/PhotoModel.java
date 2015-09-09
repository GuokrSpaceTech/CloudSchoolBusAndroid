package com.guokrspace.cloudschoolbus.parents.module.photo.model;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.android.support.handlerui.HandlerToastUI;
import com.android.support.utils.SDCardToolUtil;
import com.guokrspace.cloudschoolbus.parents.database.daodb.UploadingPhotoEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.UploadingPhotoEntityDao;
import com.guokrspace.cloudschoolbus.parents.module.photo.service.PhotoOperation;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by macbook on 15/9/9.
 */
public class PhotoModel {
    public static final String[] IMAGE_PROJECTION = new String[] {
            MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.DATE_MODIFIED, MediaStore.Images.ImageColumns.DATA };
    public static final String[] THUMB_PROJECTION = {
            MediaStore.Images.Thumbnails._ID, MediaStore.Images.Thumbnails.IMAGE_ID, MediaStore.Images.Thumbnails.DATA };
    public static final String CAMERA_IMAGE_BUCKET_NAME = Environment.getExternalStorageDirectory().toString() + "/DCIM/Camera";
    public static final String CAMERA_IMAGE_BUCKET_ID = getBucketId(CAMERA_IMAGE_BUCKET_NAME);
    public static final String selection = MediaStore.Images.Media.BUCKET_ID + " = ?";
    public static final String[] selectionArgs = { CAMERA_IMAGE_BUCKET_ID };

    private UploadingPhotoEntityDao uploadingPhotosDb;
    private List<Picture> mSelectedPictures;

    public static PhotoModel new_instance(UploadingPhotoEntityDao db)
    {
        return new PhotoModel(db);
    }

    public PhotoModel(UploadingPhotoEntityDao db) {
        uploadingPhotosDb = db;
    }

    public void addUploadingPhoto(UploadingPhotoEntity entity)
    {

    }

    public void removeUploadingPhoto(UploadingPhotoEntity entity)
    {

    }

    public List<UploadingPhotoEntity> queryUploadingList()
    {
        return null;
    }

    /**
     * Matches code in MediaProvider.computeBucketValues. Should be a common
     * function.
     */
    private static String getBucketId(String path) {
        return String.valueOf(path.toLowerCase().hashCode());
    }


    //Get all the images in the mobile
    public static final List<Picture> getImageInCameralRoll(final Context context) {
        List<Picture> pictures = new ArrayList<Picture>();
        final Uri uriImages = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        final Uri uriThumbImages = MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI;

        final ContentResolver cr = context.getContentResolver();
        Cursor cursorImages = null;
        try {
            cursorImages = cr.query(uriImages, IMAGE_PROJECTION, selection, selectionArgs,
                    null);

            if (cursorImages != null && cursorImages.moveToFirst()) {
                final int size = cursorImages.getCount();
                // DebugLog.logI("Images.Media.EXTERNAL_CONTENT_URI : " + size);
                do {
                    if (Thread.interrupted()) {
                        break;
                    }

                    Picture picture = new Picture();

                    int id = cursorImages
                            .getColumnIndexOrThrow(MediaStore.Images.ImageColumns._ID);
                    String idString = cursorImages.getString(id);
                    picture.pictureIds = idString;
                    // DebugLog.logI("id : " + idString);

                    Cursor cursorThumb = null;
                    try {
                        cursorThumb = context.getContentResolver()
                                .query(uriThumbImages,// 指定缩略图数据库的Uri
                                        THUMB_PROJECTION,// 指定所要查询的字段
                                        MediaStore.Images.Thumbnails.IMAGE_ID + " = ?", // 查询条件
                                        new String[] { idString }, // 查询条件中问号对应的值
                                        null);
                        cursorThumb.moveToFirst();
                        int thumbId = cursorImages
                                .getColumnIndexOrThrow(MediaStore.Images.Thumbnails._ID);
                        String thumbIdString = cursorThumb.getString(thumbId);
                        picture.thumbIds = thumbIdString;
                        // DebugLog.logI("thumbIdString : " + thumbIdString);
                        int thumbIData = cursorImages
                                .getColumnIndexOrThrow(MediaStore.Images.Thumbnails.DATA);
                        String thumbIDataString = cursorThumb
                                .getString(thumbIData);
                        picture.thumbPath = thumbIDataString;
                        // DebugLog.logI("thumbIDataString : " +
                        // thumbIDataString);

                        int thumbImageId = cursorImages
                                .getColumnIndexOrThrow(MediaStore.Images.Thumbnails.IMAGE_ID);
                        String imageIdStrigng = cursorThumb
                                .getString(thumbImageId);
                        System.out
                                .println("imageIdString : " + imageIdStrigng);

                    } catch (Exception e) {
//						e.printStackTrace();
                    } finally {
                        if(null != cursorThumb){
                            cursorThumb.close();
                            cursorThumb = null;
                        }
                    }

                    // int dateModify = cursorImages
                    // .getColumnIndexOrThrow(Images.ImageColumns.DATE_MODIFIED);
                    // String dateModifyString = cursorImages
                    // .getString(dateModify);
                    // System.out
                    // .println("dateModifyString : " + dateModifyString);

                    int data = cursorImages
                            .getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA);
                    String dataString = cursorImages.getString(data);
                    picture.setPicturePath(dataString);
                    // DebugLog.logI("picPathString : " + dataString);
                    // 临时的用于过滤忽略的
                    Picture tempPicture = new Picture();
                    tempPicture.setPicturePath(Uri.decode(Uri.fromFile(
                            new File(dataString)).toString()));
                    pictures.add(0,picture);

                } while (cursorImages.moveToNext());
            }

        } catch (Exception e) {
            // If the database operation failed for any reason
//			e.printStackTrace();
        } finally {
            if (null != cursorImages) {
                cursorImages.close();
                cursorImages = null;
            }
        }
        return pictures;
    }

    /**
     * 获取教师助手目录下的照片
     *
     * @param context
     * @return
     */
    public static List<Picture> getImagesApp(Context context) {
        List<Picture> pictures = new ArrayList<Picture>();
        if (SDCardToolUtil.isExistSDCard()) {
            String teacherPicture = PhotoOperation.SDCARD_ROOT_PATH + "/"
                    + PhotoOperation.DCIM + "/" + PhotoOperation.APP_PIC_FOLDER + "/";
            pictures = getFiles(teacherPicture);
        } else {
            HandlerToastUI.getHandlerToastUI(context, "请插入sd卡");
        }

        return pictures;
    }

    /**
     * 获取教师助手目录下的视频
     *
     * @param context
     * @return
     */
    public static List<Picture> getVideosApp(Context context) {
        List<Picture> pictures = new ArrayList<Picture>();
        if (SDCardToolUtil.isExistSDCard()) {
//			VideoIgnoreDB videoIgnoreDB = VideoIgnoreDB
//					.getVideoIgnoreDB(context);
//			List<Picture> ignoreList = videoIgnoreDB.getPictureList();
            String teacherPicture = PhotoOperation.SDCARD_ROOT_PATH + "/"
                    + PhotoOperation.DCIM + "/" + PhotoOperation.APP_VIDEO_FOLDER + "/";
            pictures = getVideoFiles(teacherPicture);
        } else {
            HandlerToastUI.getHandlerToastUI(context, "请插入sd卡");
        }
        return pictures;
    }

    private static List<Picture> getFiles(String root) {
        File f = new File(root);

        File[] files = f.listFiles();
        List<Picture> pictures = new ArrayList<Picture>();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                final File ff = files[i];
                if (ff.isDirectory()) {
                    pictures.addAll(0,getFiles(ff.getPath()));
                } else {
                    String fName = ff.getName();
                    if (fName.indexOf(".") > -1) {
                        String end = fName.substring(
                                fName.lastIndexOf(".") + 1, fName.length())
                                .toUpperCase();
                        if (getExtens().contains(end)) {
                            Picture picture = new Picture();
                            picture.setPicturePath(ff.getPath());

                            //临时的用于过滤忽略的
                            Picture tempPicture = new Picture();
                            tempPicture.setPicturePath(Uri.decode(Uri.fromFile(new File(ff.getPath())).toString()));

                            pictures.add(0, picture);
                        }
                    }
                }
            }
        }
        return pictures;
    }
    public static List<Picture> getVideoFiles(String root) {
        File f = new File(root);

        File[] files = f.listFiles();
        List<Picture> pictures = new ArrayList<Picture>();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                final File ff = files[i];
                if (ff.isDirectory()) {
                    pictures.addAll(0,getFiles(ff.getPath()));
                } else {
                    String fName = ff.getName();
                    if (fName.indexOf(".") > -1) {
                        String end = fName.substring(
                                fName.lastIndexOf(".") + 1, fName.length())
                                .toUpperCase();
                        if (getVideoExtens().contains(end)) {
                            Picture picture = new Picture();
                            picture.setPicturePath(ff.getPath());

//							//临时的用于过滤忽略的
                            Picture tempPicture = new Picture();
                            tempPicture.setPicturePath(Uri.decode(Uri.fromFile(new File(ff.getPath())).toString()));
//							if (!ignore.contains(tempPicture)) {
                            pictures.add(0,picture);
//							}

//							if (!ignore.contains(picture)) {
//								pictures.add(picture);
//							}
                        }
                    }
                }
            }
        }
        return pictures;
    }

    private static LinkedList<String> extens = null;

    private static LinkedList<String> getExtens() {
        if (extens == null) {
            extens = new LinkedList<String>();
            extens.add("JPEG");
            extens.add("JPG");
            extens.add("PNG");
            extens.add("GIF");
            extens.add("BMP");
        }
        return extens;
    }


    private static LinkedList<String> extensVideo = null;

    private static LinkedList<String> getVideoExtens() {
        if (extensVideo == null) {
            extensVideo = new LinkedList<String>();
            extensVideo.add("MP4");
        }
        return extensVideo;
    }
}
