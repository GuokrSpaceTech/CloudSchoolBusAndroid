package com.Manga.Activity.ClassUpdate.Widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.Manga.Activity.R;
import com.Manga.Activity.utils.Student_Info;

public class ZanButton extends RelativeLayout {
	private View image;
	private TextView  zanNum;
	private boolean isZan;
	public ZanButton(Context context) {
		super(context);
		initView(context);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public ZanButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	private void initView(Context context) {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		RelativeLayout myShow = (RelativeLayout)(LayoutInflater.from(context).inflate(R.layout.zan_layout, null));
		image=myShow.findViewById(R.id.image_pic);
		zanNum=(TextView) myShow.findViewById(R.id.zan_num);
		addView(myShow,params);
	}
	/**
	 * 设置赞与否
	 * @param isZan 是赞穿true,否穿false
	 */
	public void setIsZan(boolean isZan){
		this.isZan=isZan;
		if(isZan){
			image.setBackgroundResource(R.drawable.zan_hou_);
		}else{
			if(Student_Info.likeAble){
				image.setBackgroundResource(R.drawable.zan_qian);
			}else{
				image.setBackgroundResource(R.drawable.zan_qian_);
			}
		}
	}
	/**
	 * 设置赞数字活文字
	 * @param num
	 */
	public void setZanNum(String num){
		zanNum.setText(num);
	}
	/**
	 * 咱数字增加并修改赞显示
	 */
	public void addZanNum(){
		String tmp=zanNum.getText().toString().trim();
		if(tmp.equals("赞")||tmp.equals("Like")){
			zanNum.setText("1");
		}else{
			int foo=Integer.parseInt(tmp);
			foo++;
			zanNum.setText(foo+"");
		}
		setIsZan(true);
	}
	/**
	 * 赞数字消减并修改赞显示
	 */
	public void subZanNum(){
		String tmp=zanNum.getText().toString().trim();
		int foo=Integer.parseInt(tmp);
		if(foo==1){
			zanNum.setText(getResources().getString(R.string.zan));
		}else{
			foo--;
			zanNum.setText(foo+"");
		}
		setIsZan(false);
	}
	public  void setClick(){
		image.clearAnimation();
		ScaleAnimation animation =new ScaleAnimation(0.0f, 1.4f, 0.0f, 1.4f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f); 
		animation.setDuration(300);
		image.setAnimation(animation);
	}
	/**
	 * 获取赞状态
	 * @return
	 */
	public boolean getIsZan(){
		return isZan;
	}
}
