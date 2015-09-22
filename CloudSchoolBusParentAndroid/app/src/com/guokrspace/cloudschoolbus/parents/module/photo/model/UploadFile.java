package com.guokrspace.cloudschoolbus.parents.module.photo.model;

import com.loopj.android.http.RequestHandle;

/**
 * 上传文件列表
 * @author jiahongfei
 *
 */
public class UploadFile {

	/**表示是否选中，true选中，false没有选中*/
	public boolean isSelected = false;

	//上传文件接口参数
	/**进度*/
	public int progress;
	/**照片路径保存，需要判断sd卡中得文件还是否存在*/
	public String picPathString;
	/**照片名字*/
	public String picFileString;
	/**照片大小,返回的时字节byte*/
	public String picSizeString;
	/**是否全选*/
	public boolean isAllSelected;
	/**学生id*/
	public String studentIdList;
	/**班级uid*/
	public String classuid;
	/**内容,不是必须*/
	public String intro;
	/**照片标签，不是必须*/
	public String photoTag;
	/**上传者老师id*/
	public String teacherid;
	/**当前上传文件的句柄，可以再等待队列中删除*/
//	public Future<?> future;
	/**网络请求句柄*/
    public String key;

	public RequestHandle requestHandle;

    public int getUploadType() {
        return uploadType;
    }

    private int uploadType = 0;

    @Override
	public boolean equals(Object o) {
		if (null == o) {
			return false;
		}
		if (!(o instanceof UploadFile)) {
			return false;
		}
		if (((UploadFile) o).picPathString.equals(picPathString)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return picPathString.hashCode();
	}

    public void setFileType(int pictureType) {
        this.uploadType = pictureType;
    }


    public String generateKey() {
        this.key = System.currentTimeMillis() + teacherid;
        return this.key;
    }
}
