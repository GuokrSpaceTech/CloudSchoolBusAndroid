package android.support.v7.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.dexafree.materialList.R;

public class LikesButton extends RelativeLayout {
    private BadgeView likesNum;
    private Context   mContext;

    public LikesButton(Context context) {
        super(context);
        mContext = context;
        initView(context);
    }

    public LikesButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

	private void initView(Context context) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        RelativeLayout likesButtonLayout = (RelativeLayout)(LayoutInflater.from(context).inflate(R.layout.likes_button_layout, null));
        View buttonImage = likesButtonLayout.findViewById(R.id.likes_button);

        buttonImage.setBackgroundResource(R.drawable.ic_launcher);
        likesNum = new BadgeView(context);
        likesNum.setTargetView(buttonImage);
        likesNum.setBadgeGravity(Gravity.TOP | Gravity.LEFT);
        likesNum.setBadgeMargin(0, 0, 8, 0);

        addView(likesButtonLayout,params);

    }

	/**
	 * 设置赞数字
	 * @param num
	 */
	public void setLikesNum(String num){
        likesNum.setBadgeCount(Integer.parseInt(num));
        animation();
    }
	/**
	 * 咱数字增加并修改赞显示
	 */
	public void addLikesNum(){
		String tmp=likesNum.getText().toString().trim();
		if(tmp.equals("")){
            likesNum.setText("1");
		}else{
			int foo=Integer.parseInt(tmp);
			foo++;
            likesNum.setText(foo+"");
		}

	}
	/**
	 * 赞数字消减并修改赞显示
	 */
	public void subLikesNum(){
		String tmp=likesNum.getText().toString().trim();
		int foo=Integer.parseInt(tmp);
		if(foo==1){
            likesNum.setText("");
		}else{
			foo--;
            likesNum.setText(foo+"");
		}
	}
	public  void animation(){
		this.clearAnimation();
		ScaleAnimation animation =new ScaleAnimation(0.0f, 1.4f, 0.0f, 1.4f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f); 
		animation.setDuration(300);
		this.setAnimation(animation);
	}
}
