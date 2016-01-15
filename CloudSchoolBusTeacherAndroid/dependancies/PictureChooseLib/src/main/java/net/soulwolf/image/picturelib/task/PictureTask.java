/**
 * <pre>
 * Copyright (C) 2015  Soulwolf PictureChooseLib
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </pre>
 */
package net.soulwolf.image.picturelib.task;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.toaker.common.tlog.TLog;

import net.soulwolf.image.picturelib.model.Picture;
import net.soulwolf.image.picturelib.rx.ObservableWrapper;
import net.soulwolf.image.picturelib.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import rx.Observable;
import rx.Subscriber;

/**
 * author : Soulwolf Create by 2015/7/14 17:15
 * email  : ToakerQin@gmail.com.
 */
public class PictureTask {

    public static final String[] IMAGE_PROJECTION = new String[] {
            MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.DATE_MODIFIED, MediaStore.Images.ImageColumns.DATA };
    public static final String[] THUMB_PROJECTION = new String []{
            MediaStore.Images.Thumbnails._ID, MediaStore.Images.Thumbnails.IMAGE_ID, MediaStore.Images.Thumbnails.DATA };
    public static final String CAMERA_IMAGE_BUCKET_NAME = Environment.getExternalStorageDirectory().toString() + "/DCIM/Camera";
    public static final String CAMERA_IMAGE_BUCKET_ID = getBucketId(CAMERA_IMAGE_BUCKET_NAME);
    public static final String selection = MediaStore.Images.Media.BUCKET_ID + " = ?";
    public static final String[] selectionArgs = { CAMERA_IMAGE_BUCKET_ID };

    private static final boolean DEBUG = false;

    private static final String LOG_TAG = "PictureTask:";

    public static Observable<List<Picture>> getRecentlyPicture(final ContentResolver resolver,final int maxCount){
        return ObservableWrapper.create(new Observable.OnSubscribe<List<Picture>>() {
            @Override
            public void call(Subscriber<? super List<Picture>> subscriber) {
                try{
                    subscriber.onStart();
                    List<Picture> pictures = getRecentlyPictureModel(resolver, maxCount);

                    if(DEBUG){
                        TLog.i(LOG_TAG,"getRecentlyPicture :perform:%s",pictures);
                    }
                    subscriber.onNext(pictures);
                    subscriber.onCompleted();
                }catch (Exception e){
                    subscriber.onError(e);
                }
            }
        });
    }

    public static Observable<List<Picture>> getNextBatchPictures(final ContentResolver resolver,final int maxCount, final String dateModified){
        return ObservableWrapper.create(new Observable.OnSubscribe<List<Picture>>() {
            @Override
            public void call(Subscriber<? super List<Picture>> subscriber) {
                try{
                    subscriber.onStart();
                    List<Picture> pictures = getNextBatchPictureModel(resolver, maxCount, dateModified);

                    if(DEBUG){
                        TLog.i(LOG_TAG,"getRecentlyPicture :perform:%s",pictures);
                    }
                    subscriber.onNext(pictures);
                    subscriber.onCompleted();
                }catch (Exception e){
                    subscriber.onError(e);
                }
            }
        });
    }

    public static Observable<List<Picture>> getPreviousBatchPictures(final ContentResolver resolver,final int maxCount, final String dateModified){
        return ObservableWrapper.create(new Observable.OnSubscribe<List<Picture>>() {
            @Override
            public void call(Subscriber<? super List<Picture>> subscriber) {
                try{
                    subscriber.onStart();
                    List<Picture> pictures = getPreviousBatchPictureModel(resolver, maxCount, dateModified);

                    if(DEBUG){
                        TLog.i(LOG_TAG,"getRecentlyPicture :perform:%s",pictures);
                    }
                    subscriber.onNext(pictures);
                    subscriber.onCompleted();
                }catch (Exception e){
                    subscriber.onError(e);
                }
            }
        });
    }


    public static Observable<List<Picture>> getAllPictures(final ContentResolver resolver){
        return ObservableWrapper.create(new Observable.OnSubscribe<List<Picture>>() {
            @Override
            public void call(Subscriber<? super List<Picture>> subscriber) {
                try{
                    subscriber.onStart();
                    List<Picture> pictures = getAppPictureModel(resolver);

                    if(DEBUG){
                        TLog.i(LOG_TAG,"getRecentlyPicture :perform:%s",pictures);
                    }
                    subscriber.onNext(pictures);
                    subscriber.onCompleted();
                }catch (Exception e){
                    subscriber.onError(e);
                }
            }
        });
    }

    public static Observable<List<String>> getPictureForGallery(final String galleryDir){
        return ObservableWrapper.create(new Observable.OnSubscribe<List<String>>() {
            @Override
            public void call(Subscriber<? super List<String>> subscriber) {
                try{
                    subscriber.onStart();
                    List<String> picturePath = getPictureListForGallery(galleryDir);
                    if(DEBUG){
                        TLog.i(LOG_TAG,"getPictureForGallery :perform:%s",picturePath);
                    }
                    subscriber.onNext(picturePath);
                    subscriber.onCompleted();
                }catch (Exception e){
                    subscriber.onError(e);
                }
            }
        });
    }

    private static List<String> getPictureListForGallery(String galleryPath) {
        String[] pictureNames = new File(galleryPath).list();
        if (pictureNames == null || pictureNames.length == 0) {
            return null;
        }
        ArrayList<String> pictures = new ArrayList<String>();
        for (String name:pictureNames){
            if(Utils.isPicture(name) && !TextUtils.isEmpty(name)){
                pictures.add(Utils.and(galleryPath,File.separator,name));
            }
        }
        return pictures;
    }

    private static List<Picture> getRecentlyPictureModel(ContentResolver cr,int maxCount) {
        List<Picture> recentlyPictures = new ArrayList<>();

        String MIME_TYPE = MediaStore.Images.Media.MIME_TYPE;
        Cursor cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.DATE_MODIFIED, MediaStore.Images.ImageColumns.DATA},
//                "("+MIME_TYPE + "=? or " + MIME_TYPE + "=? or " + MIME_TYPE + "=? ) and " + MediaStore.Images.Media.BUCKET_ID + " = ?",
                MediaStore.Images.Media.BUCKET_ID + " = ?",
//                new String[]{"image/jpg", "image/jpeg", "image/png", CAMERA_IMAGE_BUCKET_ID},
                new String[]{CAMERA_IMAGE_BUCKET_ID},
                MediaStore.Images.Media.DATE_MODIFIED);

        if(cursor == null){
            return null;
        }

        if (cursor.moveToLast()) {
            while (true) {
                int id = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns._ID);
                String idString = cursor.getString(id);
                int data = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA);
                String dataString = cursor.getString(data);
                int tmCol = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATE_MODIFIED);
                String timestamp = cursor.getString(tmCol);

                Cursor cursorThumb = cr.query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
                        new String[]{MediaStore.Images.Thumbnails._ID, MediaStore.Images.Thumbnails.IMAGE_ID, MediaStore.Images.Thumbnails.DATA},// 指定所要查询的字段
                        MediaStore.Images.Thumbnails.IMAGE_ID + " = ?", // 查询条件
                        new String[]{idString}, // 查询条件中问号对应的值
                        null);
                Picture picture = new Picture();
                if(cursorThumb.getCount()>0) {
                    cursorThumb.moveToFirst();
                    int thumbId = cursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails._ID);
                    int thumbData = cursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails.DATA);
                    String thumbIdString = cursorThumb.getString(thumbId);
                    String thumbDataString = cursorThumb.getString(thumbData);
                    picture.thumbIds = thumbIdString;
                    picture.thumbPath = thumbDataString;
                }

                picture.setPicturePath(dataString);
                picture.pictureIds = idString;
                picture.timestamp = Long.parseLong(timestamp);

                recentlyPictures.add(picture);

                if (recentlyPictures.size() >= maxCount || !cursor.moveToPrevious()) {
                    break;
                }
            }
        }

        Utils.closeQuietly(cursor);

        return recentlyPictures;
    }

    private static List<Picture> getNextBatchPictureModel(ContentResolver cr,int maxCount, String dateModified) {
        List<Picture> recentlyPictures = new ArrayList<>();

        String MIME_TYPE = MediaStore.Images.Media.MIME_TYPE;
        Cursor cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.DATE_MODIFIED, MediaStore.Images.ImageColumns.DATA},
//                "("+MIME_TYPE + "=? or " + MIME_TYPE + "=? or " + MIME_TYPE + "=? ) and " + MediaStore.Images.Media.BUCKET_ID + " = ?",
                MediaStore.Images.Media.BUCKET_ID + " = ? AND " + MediaStore.Images.Media.DATE_MODIFIED + "< ?",
//                new String[]{"image/jpg", "image/jpeg", "image/png", CAMERA_IMAGE_BUCKET_ID},
                new String[]{CAMERA_IMAGE_BUCKET_ID, dateModified},
                MediaStore.Images.Media.DATE_MODIFIED);

        if(cursor == null){
            return null;
        }

        if (cursor.moveToLast()) {
            while (true) {
                int id = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns._ID);
                String idString = cursor.getString(id);
                int data = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA);
                String dataString = cursor.getString(data);
                int tmCol = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATE_MODIFIED);
                String timestamp = cursor.getString(tmCol);

                Cursor cursorThumb = cr.query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
                        new String[]{MediaStore.Images.Thumbnails._ID, MediaStore.Images.Thumbnails.IMAGE_ID, MediaStore.Images.Thumbnails.DATA},// 指定所要查询的字段
                        MediaStore.Images.Thumbnails.IMAGE_ID + " = ?", // 查询条件
                        new String[]{idString}, // 查询条件中问号对应的值
                        null);
                Picture picture = new Picture();
                if(cursorThumb.getCount()>0) {
                    cursorThumb.moveToFirst();
                    int thumbId = cursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails._ID);
                    int thumbData = cursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails.DATA);
                    String thumbIdString = cursorThumb.getString(thumbId);
                    String thumbDataString = cursorThumb.getString(thumbData);
                    picture.thumbIds = thumbIdString;
                    picture.thumbPath = thumbDataString;
                }

                picture.setPicturePath(dataString);
                picture.pictureIds = idString;
                picture.timestamp = Long.parseLong(timestamp);

                recentlyPictures.add(picture);

                if (recentlyPictures.size() >= maxCount || !cursor.moveToPrevious()) {
                    break;
                }
            }
        }

        Utils.closeQuietly(cursor);

        return recentlyPictures;
    }

    private static List<Picture> getPreviousBatchPictureModel(ContentResolver cr,int maxCount, String dateModified) {
        List<Picture> recentlyPictures = new ArrayList<>();

        String MIME_TYPE = MediaStore.Images.Media.MIME_TYPE;
        Cursor cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.DATE_MODIFIED, MediaStore.Images.ImageColumns.DATA},
//                "("+MIME_TYPE + "=? or " + MIME_TYPE + "=? or " + MIME_TYPE + "=? ) and " + MediaStore.Images.Media.BUCKET_ID + " = ?",
                MediaStore.Images.Media.BUCKET_ID + " = ? AND " + MediaStore.Images.Media.DATE_MODIFIED + "> ?",
//                new String[]{"image/jpg", "image/jpeg", "image/png", CAMERA_IMAGE_BUCKET_ID},
                new String[]{CAMERA_IMAGE_BUCKET_ID, dateModified},
                MediaStore.Images.Media.DATE_MODIFIED);

        if(cursor == null){
            return null;
        }

        if (cursor.moveToLast()) {
            while (true) {
                int id = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns._ID);
                String idString = cursor.getString(id);
                int data = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA);
                String dataString = cursor.getString(data);
                int tmCol = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATE_MODIFIED);
                String timestamp = cursor.getString(tmCol);

                Cursor cursorThumb = cr.query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
                        new String[]{MediaStore.Images.Thumbnails._ID, MediaStore.Images.Thumbnails.IMAGE_ID, MediaStore.Images.Thumbnails.DATA},// 指定所要查询的字段
                        MediaStore.Images.Thumbnails.IMAGE_ID + " = ?", // 查询条件
                        new String[]{idString}, // 查询条件中问号对应的值
                        null);
                Picture picture = new Picture();
                if(cursorThumb.getCount()>0) {
                    cursorThumb.moveToFirst();
                    int thumbId = cursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails._ID);
                    int thumbData = cursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails.DATA);
                    String thumbIdString = cursorThumb.getString(thumbId);
                    String thumbDataString = cursorThumb.getString(thumbData);
                    picture.thumbIds = thumbIdString;
                    picture.thumbPath = thumbDataString;
                }

                picture.setPicturePath(dataString);
                picture.pictureIds = idString;
                picture.timestamp = Long.parseLong(timestamp);

                recentlyPictures.add(picture);

                if (recentlyPictures.size() >= maxCount || !cursor.moveToPrevious()) {
                    break;
                }
            }
        }

        Utils.closeQuietly(cursor);

        return recentlyPictures;
    }

    private static List<Picture> getAppPictureModel(ContentResolver cr) {
        List<Picture> recentlyPictures = new ArrayList<>();

        String MIME_TYPE = MediaStore.Images.Media.MIME_TYPE;
        Cursor cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.DATE_MODIFIED, MediaStore.Images.ImageColumns.DATA},
//                "("+MIME_TYPE + "=? or " + MIME_TYPE + "=? or " + MIME_TYPE + "=? ) and " + MediaStore.Images.Media.BUCKET_ID + " = ?",
                MediaStore.Images.Media.BUCKET_ID + " = ?",
//                new String[]{"image/jpg", "image/jpeg", "image/png", CAMERA_IMAGE_BUCKET_ID},
                new String[]{CAMERA_IMAGE_BUCKET_ID},
                MediaStore.Images.Media.DATE_MODIFIED);

        if(cursor == null){
            return null;
        }

        if (cursor.moveToLast()) {
            while (true) {

                int id = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns._ID);
                String idString = cursor.getString(id);
                int data = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA);
                String dataString = cursor.getString(data);
                int tmCol = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATE_MODIFIED);
                String timestamp = cursor.getString(tmCol);

                Cursor cursorThumb = cr.query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
                        new String[]{MediaStore.Images.Thumbnails._ID, MediaStore.Images.Thumbnails.IMAGE_ID, MediaStore.Images.Thumbnails.DATA},// 指定所要查询的字段
                        MediaStore.Images.Thumbnails.IMAGE_ID + " = ?", // 查询条件
                        new String[]{idString}, // 查询条件中问号对应的值
                        null);
                Picture picture = new Picture();
                if(cursorThumb.getCount()>0) {
                    cursorThumb.moveToFirst();
                    int thumbId = cursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails._ID);
                    int thumbData = cursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails.DATA);
                    String thumbIdString = cursorThumb.getString(thumbId);
                    String thumbDataString = cursorThumb.getString(thumbData);
                    picture.thumbIds = thumbIdString;
                    picture.thumbPath = thumbDataString;
                }

                picture.setPicturePath(dataString);
                picture.pictureIds = idString;
                picture.timestamp = Long.parseLong((timestamp==null)?"0":timestamp);

                recentlyPictures.add(picture);

                if (!cursor.moveToPrevious()) {
                    break;
                }
            }
        }

        Utils.closeQuietly(cursor);

        return recentlyPictures;
    }

    /**
     * Matches code in MediaProvider.computeBucketValues. Should be a common
     * function.
     */
    private static String getBucketId(String path) {
        return String.valueOf(path.toLowerCase().hashCode());
    }
}