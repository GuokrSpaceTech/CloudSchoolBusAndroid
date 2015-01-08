package com.Manga.Activity.widget;

import com.Manga.Activity.utils.DensityUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.ImageView;

@SuppressLint("FloatMath")
public class ShowBigPic extends ImageView {
	private int init_left;
	private int init_top;
	private int init_right;
	private int init_bottom;
	private int left;
	private int top;
	private int right;
	private int bottom;
	private int win_width;
	private int win_height;
	private int bitmap_W;
	private int bitmap_H;
	private int MAX_W;
	private float afterLenght;
	private float beforeLenght;
	private boolean isControl_V;
	private boolean isControl_H;
	private float beforeX;
	private float beforeY;
	public ImageBackCall call;
	private int statusBarHeight;
	private boolean isTwoCon;
	public interface ImageBackCall{
		public void last();
		public void next();
		public void click();
	}
	public ShowBigPic(Context context) {
		// TODO Auto-generated constructor stub
		super(context);
		init();
	}

	public ShowBigPic(Context context, AttributeSet set) {
		// TODO Auto-generated constructor stub
		super(context, set);
		init();
	}

	public ShowBigPic(Context context, AttributeSet set, int defStyle) {
		// TODO Auto-generated constructor stub
		super(context, set, defStyle);
		init();
	}
	private void init(){
		WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
		win_width = wm.getDefaultDisplay().getWidth();
		win_height = wm.getDefaultDisplay().getHeight();
		statusBarHeight=getStatusBarHeight();
	}
	@Override
	public void setBackgroundDrawable(Drawable background) {
		// TODO Auto-generated method stub
		super.setBackgroundDrawable(background);
	}
	@Override
	public void setImageBitmap(Bitmap bm) {
		// TODO Auto-generated method stub
		this.init_left=0;
		this.init_top=0;
		this.init_right=0;
		this.init_bottom=0;
		if(bm!=null){
			final float x=bm.getWidth();
			final float y=bm.getHeight();
			final float tmp=win_height-statusBarHeight-DensityUtil.dip2px(getContext(), 50);
			if(tmp*(x/y)<win_height){
				//将图片的高度设置成最大高度
				Matrix m=new Matrix();
				float bi=tmp/y;
				m.setScale(bi, bi);
				bm=Bitmap.createBitmap(bm, 0, 0	, bm.getWidth(), bm.getHeight(), m, true);
			}else{
				Matrix m=new Matrix();
				float bi=win_width/x;
				m.setScale(bi, bi);
				bm=Bitmap.createBitmap(bm, 0, 0	, bm.getWidth(), bm.getHeight(), m, true);
			}
			super.setImageBitmap(bm);
			if(bm!=null){
				bitmap_W = bm.getWidth();  
			    bitmap_H = bm.getHeight();
			    MAX_W = bitmap_W * 3;  
			}
		}
	}
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		switch (ev.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			break;
		case MotionEvent.ACTION_MOVE:
			onTouchMove(ev);
			break;
		case MotionEvent.ACTION_UP:
			if(isTwoCon){
				//两个手指控制
				if(isControl_V==false&&isControl_H==false&&(left>0||top>0||right<win_width||bottom<win_height-statusBarHeight-DensityUtil.dip2px(getContext(), 50))){
					layout(init_left, init_top, init_right, init_bottom);
				}
			}else{
				//一个手指控制
				if(call!=null){
					if(ev.getX()-beforeX>250||this.getLeft()>(win_width/2)){
						call.last();
					}
					else if(beforeX-ev.getX()>250||this.getRight()<win_width/2){
						call.next();
					}
					else
					{
						call.click();
					}
					
				}
				beforeX=0;
				beforeY=0;
			}
			isTwoCon=false;
			break;
		case MotionEvent.ACTION_POINTER_UP:
			beforeLenght=0;
			break;
		}
		return true;
	}
	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		this.left=left;
		this.top=top;
		this.right=right;
		this.bottom=bottom;
		if(init_left==0&&init_top==0&&init_right==0&&init_bottom==0){
			this.init_left=left;
			this.init_top=top;
			this.init_right=right;
			this.init_bottom=bottom;
		}
		Log.v("onLayout()", left+","+top+","+right+","+bottom);
	}
	private void onTouchMove(MotionEvent ev) {
		if(ev.getPointerCount()==2&&beforeLenght==0){
			beforeLenght=spacing(ev);
		}
		if(ev.getPointerCount()>=2){
			isTwoCon=true;
			afterLenght = spacing(ev);
	        float gapLenght = afterLenght - beforeLenght;
	        float scale_temp=0f;
	        if (Math.abs(gapLenght) > 5f) {  
	            scale_temp = afterLenght / beforeLenght;
	            this.setScale(scale_temp);  
	            beforeLenght = afterLenght;  
	        }
		}else if(ev.getPointerCount()==1){
			if(beforeX==0||beforeY==0){
				beforeX=ev.getX();
				beforeY=ev.getY();
			}
			if(isControl_V){
				if(ev.getY()<beforeY){
					float tmp=beforeY-ev.getY();
					layout(this.getLeft(), this.getTop()-(int)tmp, this.getRight(), this.getBottom()-(int)tmp);
				}else{
					float tmp=ev.getY()-beforeY;
					layout(this.getLeft(), this.getTop()+(int)tmp, this.getRight(), this.getBottom()+(int)tmp);
				}
			}
			if(isControl_H){
				if(ev.getY()<beforeX){
					float tmp=beforeX-ev.getX();
					layout(this.getLeft()-(int)tmp, this.getTop(), this.getRight()-(int)tmp, this.getBottom());
				}else{
					float tmp=ev.getX()-beforeX;
					layout(this.getLeft()+(int)tmp, this.getTop(), this.getRight()+(int)tmp, this.getBottom());
				}
			}
		}
	}
	/**
	 *	
	 * @param event
	 * @return
	 */
	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}
	void setScale(float scale) {
        int disX = (int) (this.getWidth() * Math.abs(1 - scale)) / 4;
        int disY = (int) (this.getHeight() * Math.abs(1 - scale)) / 4;
		if (scale > 1 && this.getWidth() <= MAX_W) {
			layout( this.getLeft() - disX,  this.getTop() - disY, this.getRight()+disX, this.getBottom()+disY);
		}else if(scale < 1&&this.getWidth() >= bitmap_W/2){
			layout( this.getLeft() +disX,  this.getTop() +disY, this.getRight()-disX, this.getBottom()-disY);
		}
		if (top <= 0 && bottom >= statusBarHeight-DensityUtil.dip2px(getContext(), 50)) {
			isControl_V = true;
		} else {
			isControl_V = false;
		}
		if (left <= 0 && right >= win_width) {
			isControl_H = true;
		} else {
			isControl_H = false;
		}
    }

	public ImageBackCall getCall() {
		return call;
	}

	public void setCall(ImageBackCall call) {
		this.call = call;
	}
	public int getStatusBarHeight()  
    {  
        Class<?> c = null;  
        Object obj = null;  
        java.lang.reflect.Field field = null;  
        int x = 0;  
        int statusBarHeight = 0;  
        try  
        {  
            c = Class.forName("com.android.internal.R$dimen");  
            obj = c.newInstance();  
            field = c.getField("status_bar_height");  
            x = Integer.parseInt(field.get(obj).toString());  
            statusBarHeight = getResources().getDimensionPixelSize(x);  
            return statusBarHeight;  
        }  
        catch (Exception e)  
        {  
            e.printStackTrace();  
        }  
        return statusBarHeight;  
    }  
}
