package com.cytx.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.Manga.Activity.R;
import com.cytx.CYTXApplication;
import com.cytx.domain.QuestionHistoryDomain;
import com.cytx.domain.QuestionHistoryProblemDomain;
import com.cytx.utility.DateTools;

/**
 * 历史问题adapter
 * @author xilehang
 *
 */
public class ConsultAdapter extends BaseAdapter {
	
	private Context context;
	private List<QuestionHistoryDomain> lists;
	private int screenType;
	private Map<String, String> statusMaps = new HashMap<String, String>();
	private List<String> clinicZnList = new ArrayList<String>();// 中文科室
	private String [] currentClinicArr = null;
	
	public ConsultAdapter(Context context, List<QuestionHistoryDomain> lists, int screenType){
		this.context = context;
		this.lists = lists;
		this.screenType = screenType;
		// 初始化status问题状态
		statusMaps.clear();
		String statusKey [] = context.getResources().getStringArray(R.array.status_key);
		String statusValue [] = context.getResources().getStringArray(R.array.status_value);
		for (int i = 0; i < statusValue.length; i++) {
			statusMaps.put(statusKey[i], statusValue[i]);
		}
		
		// 中文科室
		clinicZnList.clear();
		String clinicZnArr [] = context.getResources().getStringArray(R.array.clinic_value_zh);
		for (int i = 0; i < clinicZnArr.length; i++) {
			clinicZnList.add(clinicZnArr[i]);
		}
		
		// 当前的科室（中英文）
		currentClinicArr = context.getResources().getStringArray(R.array.clinic_value);
		
	}
	
	

	public List<QuestionHistoryDomain> getLists() {
		return lists;
	}



	public void setLists(List<QuestionHistoryDomain> lists) {
		this.lists = lists;
	}



	@Override
	public int getCount() {
		if (lists == null) {
			return 0;
		}
		return lists.size();
	}

	@Override
	public Object getItem(int arg0) {
		return arg0;
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			if (screenType == CYTXApplication.getInstance().SCREEN_480) {
				convertView = LayoutInflater.from(context).inflate(R.layout.list_consult_item_480, parent, false);
			} else {
				convertView = LayoutInflater.from(context).inflate(R.layout.list_consult_item, parent, false);
			}
			
			holder.clinicTextView = (TextView) convertView.findViewById(R.id.textView_clinic);
			holder.contentTextView = (TextView) convertView.findViewById(R.id.TextView_content);
			holder.timeTextView = (TextView) convertView.findViewById(R.id.textView_time);
			holder.statusTextView = (TextView) convertView.findViewById(R.id.TextView_status);
			holder.statusImageView = (ImageView) convertView.findViewById(R.id.image_status);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		QuestionHistoryDomain qhd = lists.get(position);
		QuestionHistoryProblemDomain qhpd = qhd.getProblem();
		
		int index = clinicZnList.indexOf(qhpd.getClinic_name());
		if (index != -1) {
			holder.clinicTextView.setText(currentClinicArr[index]);
		} else {
			holder.clinicTextView.setText(qhpd.getClinic_name());
		}
		
		holder.contentTextView.setText(qhpd.getTitle());
		holder.timeTextView.setText(DateTools.ReturnBeforeTime(DateTools.strToDateLong(qhpd.getCreated_time()), context));
		if ( !"".equals(qhpd.getStatus())) {
			holder.statusTextView.setText(statusMaps.get(qhpd.getStatus()));
		} else {
			holder.statusTextView.setText(qhpd.getStatus());
		}

		// 用户是否查看过此问题
		if (qhpd.isIs_viewed()) {
			holder.statusImageView.setVisibility(View.GONE);
		} else {
			holder.statusImageView.setVisibility(View.VISIBLE);
		}
		
		return convertView;
	}
	
	class ViewHolder {
		public TextView clinicTextView;
		public TextView contentTextView;
		private TextView timeTextView;
		private TextView statusTextView;
		private ImageView statusImageView;
	}

}
