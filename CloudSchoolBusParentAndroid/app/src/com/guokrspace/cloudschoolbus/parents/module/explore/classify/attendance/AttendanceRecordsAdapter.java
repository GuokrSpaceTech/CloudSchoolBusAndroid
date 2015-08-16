/**
 * 
 */
package com.guokrspace.cloudschoolbus.parents.module.explore.classify.attendance;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.support.utils.DateUtils;
import com.guokrspace.cloudschoolbus.parents.R;
import com.guokrspace.cloudschoolbus.parents.database.daodb.AttendanceEntity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author Kai
 *
 */
public class AttendanceRecordsAdapter extends BaseAdapter {
	private Context context;
	public List<AttendanceEntity> attendance_records;
	ImageLoader imageLoader = ImageLoader.getInstance();
	DisplayImageOptions options;
	ImageLoaderConfiguration config;

	/**
	 * @param context 
	 * 
	 */
	public AttendanceRecordsAdapter(Context cntx) {
		context = cntx;
		options = new DisplayImageOptions.Builder()
        .showImageOnLoading(R.drawable.ic_image_default)
	      .showImageForEmptyUri(R.drawable.ic_image_default)
	      .showImageOnFail(R.drawable.ic_image_default)
	      .cacheInMemory(true)
	      .cacheOnDisc(true)
	      .considerExifParams(true)
	      .bitmapConfig(Bitmap.Config.RGB_565)
	      .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
	      .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
	      .defaultDisplayImageOptions(options).discCacheSize(50 * 1024 * 1024)//
	      .discCacheFileCount(100).writeDebugLogs().build();
        imageLoader.init(config);
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		if (attendance_records == null) {
			return 0;
		}
		return attendance_records.size();
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {
		return attendance_records != null ? attendance_records.get(position) : null;
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		return position;
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.activity_attendance_records_item, parent, false);
			holder.text_view_dateTime = (TextView) convertView.findViewById(R.id.textview_attendance_record);
			holder.image_view_captured = (ImageView) convertView.findViewById(R.id.image_attendance_pic);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		if(attendance_records!=null)
			if(attendance_records.size()!=0)
		{
			AttendanceEntity record = attendance_records.get(position);
					
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//			Date dt = new Date(record.getTimestamp()*1000);
//			String str_date = df.format(dt);
//			String str_time = str_date.substring(str_date.lastIndexOf(" ")+1, str_date.length());
			holder.text_view_dateTime.setText(DateUtils.timelineTimestamp(record.getTimestamp(),context));
			
			holder.text_view_dateTime.setTextColor(Color.rgb(0, 0, 0)); 
			
			String url = "";
			if(record.getImageUrl() != "null" )
			{
			    url = "http://" + record.getImageUrl();
			    imageLoader.displayImage(url, holder.image_view_captured, options);
			}
			else
			{
				Drawable defaultDrawable = context.getResources().getDrawable(R.drawable.ic_image_default);
				holder.image_view_captured.setImageDrawable(defaultDrawable);
			}
		}
		return convertView;
	}
	
	class ViewHolder {
		public TextView   text_view_dateTime;
		private ImageView image_view_captured;
	}
}
