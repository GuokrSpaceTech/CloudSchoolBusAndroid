/*
 * Description: This is a customized view for drawing the the gesture password
 * Function: Init the canvas, paint, and touchListener
 * Copyright: 2014 @Beijing Guokrspace Tech Co.,Ltd 
 */
package com.Manga.Activity.Gestures;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.Manga.Activity.utils.ActivityUtil;
public class drawlSet extends View{

	private float mov_x;//声明起点坐标
	private float mov_y;
	private Paint paint;//声明画笔
	private Canvas canvas = new Canvas();//画布
	private Bitmap bitmap;//位图
	private float fStartX = 95;
	private float fStartY = 340;
	private float fDisX = 145;
	private float fDisY = 149;
	private float fFormX = 30;
	private float fFormY = 30;
	private List<String> nSelectArr = new ArrayList<String>();
	
	public drawlSet(Context context) {
		super(context);
		nSelectArr.clear();
		paint=new Paint(Paint.DITHER_FLAG);//创建一个画笔
		WindowManager wm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);
		int width = wm.getDefaultDisplay().getWidth();
		int height = wm.getDefaultDisplay().getHeight();
		bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888); //设置位图的宽高
		canvas=new Canvas();
		canvas.setBitmap(bitmap);
		
		paint.setStyle(Style.STROKE);//设置非填充
		paint.setStrokeWidth(5);//笔宽5像素
		paint.setColor(Color.parseColor("#F0FF00"));//设置为红笔
		paint.setAntiAlias(true);//锯齿不显示
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC));
		paint.setStrokeWidth(20);//笔宽5像素  
		//canvas.drawLine(95, 340, 385, 638, paint);//画线
	}
 	@Override	
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
	 canvas.drawBitmap(bitmap,0,0,null);
 	}
 //触摸事件
 	@Override
	public boolean onTouchEvent(MotionEvent event) {

 		//Get the position of the top left of 3x3 circles
		fStartX = (float) (ActivityUtil.select1.getStartX(1) + ActivityUtil.select1.getWidth()/2.0);
		fStartY = (float) (ActivityUtil.select1.getStartY(1) + ActivityUtil.select1.getHeight()/2.0);
		//Get the distance( x, y axis) between the circles
		fDisX = ActivityUtil.select1.getStartX(2)-ActivityUtil.select1.getStartX(1);
		fDisY = ActivityUtil.select1.getStartX(4)-ActivityUtil.select1.getStartX(1);
		
		//Get the radius of the circles
		fFormX = ActivityUtil.select1.getWidth()/2-5;
		fFormY = ActivityUtil.select1.getHeight()/2-5;
		
		//Draw per the User finger movement
 		if (event.getAction()==MotionEvent.ACTION_MOVE) {
            
 			//Setup the paint
 			paint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
 			canvas.drawPaint(paint);
 			paint.setXfermode(new PorterDuffXfermode(Mode.SRC));
 			paint.setStrokeWidth(20);//笔宽5像素
 			
            //Check which circles the user finger pass through 
 			for(int ii=0; ii<3; ii++){
 				for(int jj=0; jj<3; jj++){
 					boolean blnIsHave = false;
 					if((Math.abs(event.getX() - (fStartX+ii*fDisX))<fFormX)&&(Math.abs(event.getY() - (fStartY+jj*fDisY))<fFormY)){
 						for(int kk=0; kk<nSelectArr.size();kk++){
 							if(nSelectArr.get(kk).equals(ii+jj*3+1+"")){
 								blnIsHave = true;
 								break;
 							}
 						}
 						if(!blnIsHave){
 							mov_x = event.getX();
 							mov_y = event.getY();
 							nSelectArr.add(ii+jj*3+1+"");
 							ActivityUtil.select1.ChangePicBg(ii+jj*3+1+"");
 						}
 					}
 				}
 			}
// 			if(mov_x!=0 && mov_y!=0){
// 				canvas.drawLine(mov_x, mov_y, event.getX(), event.getY(), paint);//画线
// 			} 			

 			for(int kk=0; kk<nSelectArr.size()-1; kk++){
 				//测试
 				int ii=0, jj=0, mm =0, nn = 0;
 				jj =(Integer.parseInt(nSelectArr.get(kk))-1)/3;
 				ii =(Integer.parseInt(nSelectArr.get(kk))-1)%3;
 				nn =(Integer.parseInt(nSelectArr.get(kk+1))-1)/3;
 				mm =(Integer.parseInt(nSelectArr.get(kk+1))-1)%3;
 				canvas.drawLine(fStartX+ii*fDisX, fStartY+jj*fDisY, fStartX+mm*fDisX, fStartY+nn*fDisY, paint);
 			}
 			
 			invalidate();
 			
 		}
 		if(event.getAction() == MotionEvent.ACTION_UP){
 			String strLock = "";
 			for(int kk=0; kk<nSelectArr.size(); kk++){
 				//测试
 				strLock +=nSelectArr.get(kk);
 			}
 			if(nSelectArr.size()<4&& nSelectArr.size()>0){
 				ActivityUtil.select1.UpdateNumber();
 	 			ActivityUtil.select1.ChangePicBg("0");
 	 			paint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
 	 			canvas.drawPaint(paint); 
 	 			mov_x =0;
 	 			mov_x =0;
 	 			nSelectArr.clear();
 	 			invalidate();
 				return true;
 				
 			}
			SharedPreferences sp =getContext().getSharedPreferences("GestureData",Context.MODE_PRIVATE);
 			if(sp.getString("strLockOld", "").equals("")){
 				ActivityUtil.select1.UpdateOldLock(strLock);
 			}else{
 				ActivityUtil.select1.UpdateNewLock(strLock);
 			}
 			ActivityUtil.select1.ChangePicBg("0");
 			paint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
 			canvas.drawPaint(paint); 
 			mov_x =0;
 			mov_x =0;
 			nSelectArr.clear();
 			invalidate();
 			return true;
 		}
 		if(event.getAction() == MotionEvent.ACTION_CANCEL){
 			ActivityUtil.select1.ChangePicBg("0");
 			paint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
 			canvas.drawPaint(paint); 
 			mov_x =0;
 			mov_x =0;
 			nSelectArr.clear();
 			invalidate();
 			return true;
 		}
 		return true;
 	}

	public drawlSet(Context context, AttributeSet attrs, int defStyle) {  
	    super(context, attrs, defStyle);  
	     // TODO Auto-generated constructor stub  
	}  
	public drawlSet(Context context, AttributeSet attrs) {  
	    super(context, attrs);  
	}
	
}
