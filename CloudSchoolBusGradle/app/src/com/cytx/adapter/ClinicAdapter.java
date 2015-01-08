package com.cytx.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.Manga.Activity.R;
import com.cytx.CYTXApplication;

/**
 * 18种科室adapter
 * @author xilehang
 *
 */
public class ClinicAdapter extends BaseAdapter {
	
	private Context context;
	private String clinicTypes [];
	private int screenType;
	
	public ClinicAdapter(Context context, String clinicTypes [], int screenType){
		this.context = context;
		this.clinicTypes = clinicTypes;
		this.screenType = screenType;
	}

	@Override
	public int getCount() {
		if (clinicTypes == null) {
			return 0;
		}
		return clinicTypes.length;
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
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		ViewHolder holder = null;
		if (arg1 == null) {
			holder = new ViewHolder();
			if (screenType == CYTXApplication.getInstance().SCREEN_480) {
				arg1 = LayoutInflater.from(context).inflate(R.layout.list_clinic_item_480, arg2, false);
			} else {
				arg1 = LayoutInflater.from(context).inflate(R.layout.list_clinic_item, arg2, false);
			}
			
			holder.clinicType = (TextView) arg1.findViewById(R.id.textView_clinic_type);
			arg1.setTag(holder);
		} else {
			holder = (ViewHolder) arg1.getTag();
		}
		
		holder.clinicType.setText(clinicTypes[arg0]);
		
		return arg1;
	}
	
	class ViewHolder{
		public TextView clinicType;
	}

}
