package com.Manga.Activity.ClassUpdate;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class ResizeRelativeLayout extends RelativeLayout {  
  
    public ResizeRelativeLayout(Context context) {  
        super(context);  
    }  
  
    public ResizeRelativeLayout(Context context, AttributeSet attrs, int defStyle) {  
        super(context, attrs, defStyle);  
    }  
  
    public ResizeRelativeLayout(Context context, AttributeSet attrs) {  
        super(context, attrs);  
    }  
  
    public void setOnResizeListener(OnResizeListener l) {  
        mListener = l;  
    }  
  
    @Override  
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {       
    	super.onSizeChanged(w, h, oldw, oldh);       
    	if (mListener != null) {
    		mListener.OnResize(w, h, oldw, oldh);   
    	}   
    } 
    private OnResizeListener mListener;  
  
    public interface OnResizeListener {  
        void OnResize(int w, int h, int oldw, int oldh);  
    };  
    public interface CheckLog {  
        void Oncheck(int tmp);  
    }
} 
