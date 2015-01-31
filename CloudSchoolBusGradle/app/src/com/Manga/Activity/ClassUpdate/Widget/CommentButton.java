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

public class CommentButton extends RelativeLayout {
	private View image;
	private TextView  zanNum;
	public CommentButton(Context context) {
		super(context);
		initView(context);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public CommentButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	private void initView(Context context) {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		RelativeLayout myShow = (RelativeLayout)(LayoutInflater.from(context).inflate(R.layout.lun_layout, null));
		image=myShow.findViewById(R.id.image);
		zanNum=(TextView) myShow.findViewById(R.id.zan_num);
		addView(myShow,params);
	}
	/**
	 * 设置赞数字活文字
	 * @param num
	 */
	public void setZanNum(String num){
		zanNum.setText(num);
	}
	/**
	 * 增加评论并修改赞显示
	 */
	public void addCommentNum(){
		String tmp=zanNum.getText().toString().trim();
		if(tmp.equals("评论")||tmp.equals("Comm")){
			zanNum.setText("1");
		}else{
			int foo=Integer.parseInt(tmp);
			foo++;
			zanNum.setText(foo+"");
		}
	}
	/**
	 * 消减评论数字
	 */
	public void subCommentNum(){
		String tmp=zanNum.getText().toString().trim();
		int foo=Integer.parseInt(tmp);
		if(foo==1){
			zanNum.setText(getResources().getString(R.string.comment));
		}else{
			foo--;
			zanNum.setText(foo+"");
		}
	}

	public View getImage() {
		return image;
	}

	public void setImage(View image) {
		this.image = image;
	}
	
}
