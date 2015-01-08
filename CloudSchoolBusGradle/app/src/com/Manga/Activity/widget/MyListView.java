package com.Manga.Activity.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.Manga.Activity.R;
import com.Manga.Activity.adapter.MyAdapter;
import com.Manga.Activity.utils.DensityUtil;

public class MyListView extends RelativeLayout {
	private LinearLayout listView;
	private GestureDetector detector;
	private LinearLayout bufferView;
	private RelativeLayout header;
	private TextView headerNote;
	private View headerImage;
	private RelativeLayout footer;
	private TextView footerNote;
	private View footerImage;
	private ScrollView myScrollView;
	private MyAdapter adapter;
	private MyBackCall backCall;
	private boolean headerLock;
	private boolean footerLock;
	private int HEADER_STATE=1;
	private int FOOTER_STATE=1;
	private static int DEFAULT;
	private static int current_header_h;
	private static int current_footer_h;
	/**
	 * 下拉刷新
	 */
	private static final int HEADERPULLDOWN=1;
	/**
	 * 松手刷新
	 */
	private static final int HEADERPULLDO=2;
	/**
	 * 加载中
	 */
	private static final int HEADERPULLDOING=3;
	/**
	 * 上拉刷新
	 */
	private static final int FOOTERPULLDOWN=1;
	/**
	 * 松手刷新
	 */
	private static final int FOOTERPULLDO=2;
	/**
	 * 加载中
	 */
	private static final int FOOTERPULLDOING=3;
	/**
	 * 头部载入进行锁
	 */
	private boolean headerLoadingLock;
	/**
	 * 脚部载入进行锁
	 */
	private boolean footerLoadingLock;
	public MyListView(Context context) {
		super(context);
		initView(context);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public MyListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}
	/**
	 * 初始化界面
	 * @param context
	 */
	private void initView(Context context) {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		RelativeLayout myShow = (RelativeLayout)(LayoutInflater.from(context).inflate(R.layout.my_listview, null));
		listView=(LinearLayout)myShow.findViewById(R.id.my_listview);
		bufferView=(LinearLayout) myShow.findViewById(R.id.buffer_view);
		header=(RelativeLayout) myShow.findViewById(R.id.header);
		headerNote=(TextView) myShow.findViewById(R.id.resfrishNote);
		headerImage=myShow.findViewById(R.id.showImage);
		footer=(RelativeLayout) myShow.findViewById(R.id.footer);
		footerNote=(TextView) myShow.findViewById(R.id.resfrish_Note);
		footerImage=myShow.findViewById(R.id.show_Image);
		myScrollView=(ScrollView) myShow.findViewById(R.id.my_scrollview);
		setHeaderH(0);
		setFooterH(0);
		addView(myShow, params);
		detector = new GestureDetector(new MyGesture());
		DEFAULT=DensityUtil.dip2px(getContext(), 50);
		myScrollView.setLongClickable(true);
		myScrollView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(listView.getHeight()<myScrollView.getScrollY()+myScrollView.getHeight()+250&&(myScrollView.getScrollY()!=0||myScrollView.getScrollY()<0)){
					if(footerLock){
						
					}else{
						footerLock=true;
					}
				}else if(myScrollView.getScrollY()==0||myScrollView.getScrollY()<0){
					if(headerLock){
						
					}else{
						headerLock=true;
					}
				}else{
					footerLock=false;
					headerLock=false;
				}
				Log.v("footerLock", footerLock+"");
				Log.v("headerLock", headerLock+"");
				Log.v("myScrollView.getScrollY()", myScrollView.getScrollY()+"");
				detector.onTouchEvent(event);
				if(event.getAction()==MotionEvent.ACTION_UP){
					//头动画操作
					if(HEADER_STATE==HEADERPULLDO){
						HEADER_STATE=HEADERPULLDOING;
						checkHeaderState();
					}
					if(current_header_h<DEFAULT||HEADER_STATE==HEADERPULLDOWN){
						setHeaderH(0);
					}
					//脚动画操作
					if(FOOTER_STATE==FOOTERPULLDO){
						FOOTER_STATE=FOOTERPULLDOING;
						checkFooterState();
					}
					if(current_footer_h<DEFAULT||FOOTER_STATE==FOOTERPULLDOWN){
						setFooterH(0);
					}
				}
				return false;
			}
		});
	}
	/**
	 * 检查状态，并更新视图
	 */
	private void checkHeaderState(){
		headerImage.clearAnimation();
		switch(HEADER_STATE){
			case HEADERPULLDOWN:
				headerImage.setBackgroundResource(R.drawable.refresh_up);
				headerNote.setText(R.string.pull_down);
				Log.v("header", "下拉刷新");
				break;
			case HEADERPULLDO:
				RotateAnimation rotateAnimation = new RotateAnimation(0, 180,Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,0.5f);
				rotateAnimation.setDuration(500);
				rotateAnimation.setFillAfter(true);
				headerImage.setAnimation(rotateAnimation);
				Log.v("header", "松手刷新");
				break;
			case HEADERPULLDOING:
				headerNote.setText(R.string.loading);
				headerImage.setBackgroundResource(R.drawable.domob_loading_);
				RotateAnimation retateCircel = new RotateAnimation(0, 1440*10000, Animation.RELATIVE_TO_SELF,0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
				retateCircel.setDuration(13000000);
				retateCircel.setInterpolator(new LinearInterpolator());
				retateCircel.setFillAfter(true);
				headerImage.setAnimation(retateCircel);
				Log.v("header", "加载中");
				setHeaderH(DEFAULT);
				if(backCall!=null){
					if(headerLoadingLock){
						Toast.makeText(getContext(), R.string.isLoading, Toast.LENGTH_SHORT).show();
					}else{
						backCall.executeHeader();
						headerLoadingLock=true;
					}
				}
				break;
		}
	}
	/**
	 * 检查状态，并更新视图
	 */
	private void checkFooterState(){
		footerImage.clearAnimation();
		switch(FOOTER_STATE){
			case FOOTERPULLDOWN:
				footerImage.setBackgroundResource(R.drawable.refresh_down);
				footerNote.setText(R.string.pull_up);
				break;
			case FOOTERPULLDO:
				RotateAnimation rotateAnimation = new RotateAnimation(0, 180,Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,0.5f);
				rotateAnimation.setDuration(500);
				rotateAnimation.setFillAfter(true);
				footerImage.setAnimation(rotateAnimation);
				break;
			case FOOTERPULLDOING:
				footerNote.setText(R.string.loading);
				footerImage.setBackgroundResource(R.drawable.domob_loading_);
				RotateAnimation retateCircel = new RotateAnimation(0, 1440*10000, Animation.RELATIVE_TO_SELF,0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
				retateCircel.setDuration(13000000);
				retateCircel.setInterpolator(new LinearInterpolator());
				retateCircel.setFillAfter(true);
				footerImage.setAnimation(retateCircel);
				setFooterH(DEFAULT);
				if(backCall!=null){
					if(footerLoadingLock){
						Toast.makeText(getContext(), R.string.isLoading, Toast.LENGTH_SHORT).show();
					}else{
						backCall.executeFooter();
						footerLoadingLock=true;
					}
				}
				break;
		}
	}
	/**
	 * 设置头高度
	 * @param height
	 */
	private void setHeaderH(int height){
		RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) header.getLayoutParams();
		lp.height=height;
		header.setLayoutParams(lp);
	}
	/**
	 * 设置脚高度
	 * @param height
	 */
	private void setFooterH(int height){
		RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) footer.getLayoutParams();
		lp.height=height;
		footer.setLayoutParams(lp);
	}
	/**
	 * 给listView设置Adapter
	 * @param adapter
	 */
	public  void setAdapter(MyAdapter adapter){
		this.adapter=adapter;
		if(adapter.getList()!=null){
			for(int i=0;i<adapter.getList().size();i++){
				LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
				listView.addView(adapter.getView(i),params);
			}
		}
	}
	/**
	 * 取消头显示
	 */
	public void cancelHeader(){
		HEADER_STATE=HEADERPULLDOWN;
		checkHeaderState();
		setHeaderH(0);
		headerLoadingLock=false;
	}
	/**
	 * 取消脚显示
	 */
	public void cancelFooter(){
		FOOTER_STATE=FOOTERPULLDOWN;
		checkFooterState();
		setFooterH(0);
		footerLoadingLock=false;
	}
	public MyAdapter getAdapter(){
		return this.adapter;
	}
	/**
	 * 获取第缓冲视图
	 * @return
	 */
	public LinearLayout getBufferView() {
		return bufferView;
	}
	/**
	 * 移除缓冲区视图
	 */
	public void removeBufferView(int index){
		bufferView.removeViewAt(index);
	}
	/**
	 * 设置缓冲区
	 * @param view
	 */
	public void addBufferView(View view) {
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
		bufferView.addView(view, params);
	}
	public MyBackCall getBackCall() {
		return backCall;
	}
	/**
	 * 设置回调方法
	 * @param backCall
	 */
	public void setBackCall(MyBackCall backCall) {
		this.backCall = backCall;
	}
	/**
	 * MyListView回调接口
	 * @author guokr
	 *
	 */
	public interface MyBackCall{
		public void executeHeader();
		public void executeFooter();
	}
	/**
	 * 手势控制
	 * @author guokr
	 *
	 */
	private class MyGesture extends SimpleOnGestureListener {
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,float distanceX, float distanceY) {
			int tmp = (int) (e2.getY() - e1.getY());
			if(tmp>0&&headerLock){
				if (DEFAULT>tmp) {
					if(HEADER_STATE!=HEADERPULLDOWN){
						HEADER_STATE=HEADERPULLDOWN;
						checkHeaderState();
					}
				}else if(DEFAULT*2<tmp){
					if(HEADER_STATE!=HEADERPULLDO){
						HEADER_STATE=HEADERPULLDO;
						checkHeaderState();
					}
				}
				if(DEFAULT*3>tmp){
					setHeaderH(tmp);
					current_header_h=tmp;
				}else{
					if(current_header_h!=DEFAULT*3){
						setHeaderH(DEFAULT*3);
						current_header_h=DEFAULT*3;
					}
				}
			}
			if(tmp<0&&footerLock){
				int foo=0-tmp;
				if (DEFAULT>foo) {
					if(FOOTER_STATE!=FOOTERPULLDOWN){
						FOOTER_STATE=FOOTERPULLDOWN;
						checkFooterState();
					}
				}else if(DEFAULT*3<foo){
					if(FOOTER_STATE!=FOOTERPULLDO){
						FOOTER_STATE=FOOTERPULLDO;
						checkFooterState();
					}
				}
				if(DEFAULT*4>foo){
					setFooterH(foo);
					current_footer_h=foo;
				}else{
					if(current_footer_h!=DEFAULT*4){
						setFooterH(DEFAULT*4);
						current_footer_h=DEFAULT*4;
					}
				}
			}
			return super.onScroll(e1, e2, distanceX, distanceY);
		}
	}
}
