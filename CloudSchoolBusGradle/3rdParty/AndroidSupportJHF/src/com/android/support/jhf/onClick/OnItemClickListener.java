package com.android.support.jhf.onClick;


import com.android.support.jhf.utils.CommonUtils;

import android.view.View;
import android.widget.AdapterView;

public abstract class OnItemClickListener implements AdapterView.OnItemClickListener{

	CommonUtils commonUtils = new CommonUtils();
	
	public OnItemClickListener() {
		commonUtils.setLastClickTime();
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		
		if (commonUtils.isFastDoubleClick()) {  
	        return;  
	    } else {
	    	onItemClick(parent, view, position);
	    }
		
		
	}
	
	public abstract void onItemClick(AdapterView<?> parent, View view, int position);

}
