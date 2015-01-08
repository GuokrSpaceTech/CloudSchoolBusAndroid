package com.Manga.Activity.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.Manga.Activity.R;
import com.Manga.Activity.utils.DensityUtil;

public class ModifiListView extends ListView {
	private RelativeLayout header;
	private RelativeLayout footer;
	private View headerImage;
	private TextView headerNote;
	private View footerImage;
	private TextView footerNote;
	private int HEADER_STATE=1;
	private int FOOTER_STATE=1;
	private MyBackCall backCall;
	private boolean headerLock;
	private boolean footerLock;
	private boolean isCancelHeader;
	private boolean isCancelFootder;

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
	private static int DEFAULT;
	private float startValue;
	private float lastValue;
	private boolean initV;
	public ModifiListView(Context context) {
		super(context);
		init();
	}
	/**
	 * @param context
	 * @param attrs
	 */
	public ModifiListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	public ModifiListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	private void init(){
		header=(RelativeLayout) View.inflate(getContext(), R.layout.listview_header, null);
		footer=(RelativeLayout) View.inflate(getContext(), R.layout.listview_footer, null);
		headerImage=header.findViewById(R.id.showImage);
		footerImage=footer.findViewById(R.id.show_Image);
		headerNote=(TextView) header.findViewById(R.id.resfrishNote);
		footerNote=(TextView) footer.findViewById(R.id.resfrish_Note);
		DEFAULT=DensityUtil.dip2px(getContext(), 50);
		setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent ev) {
				
				switch(ev.getAction()){
				case MotionEvent.ACTION_DOWN:
					break;
				case MotionEvent.ACTION_MOVE:
					if(initV==false){
						startValue=ev.getRawY();
						initV=true;
					}
					lastValue=ev.getRawY();
					int tmp=(int)(lastValue-startValue);
					if(tmp>0&&getFirstVisiblePosition()==0){
						if (DEFAULT>tmp) {
							if(HEADER_STATE!=HEADERPULLDOWN){
								HEADER_STATE=HEADERPULLDOWN;
								checkHeaderState();
							}
							if(isCancelHeader){
								try {
									addHeaderView(header);
								} catch (Exception e) {

								}
								isCancelHeader=false;
							}
							setHeaderH(tmp);
						}else if(DEFAULT*2.5>tmp){
							if(HEADER_STATE!=HEADERPULLDO){
								HEADER_STATE=HEADERPULLDO;
								checkHeaderState();
							}
							setHeaderH(tmp);
						}
//						else {
//							if(HEADER_STATE!=HEADERPULLDOING){
//								HEADER_STATE=HEADERPULLDOING;
//								checkHeaderState();
//							}
//						}
					}else if(tmp<0&&getLastVisiblePosition()+1==getAdapter().getCount()){
						int foo=0-tmp;
						if (DEFAULT>foo) {
							if(FOOTER_STATE!=FOOTERPULLDOWN){
								FOOTER_STATE=FOOTERPULLDOWN;
								checkFooterState();
							}
							setFooterH(foo);
						}else if(DEFAULT*4.0>foo){
							if(FOOTER_STATE!=FOOTERPULLDO){
								FOOTER_STATE=FOOTERPULLDO;
								checkFooterState();
							}
							setFooterH(foo);
						}else {
							if(FOOTER_STATE!=FOOTERPULLDOING){
								FOOTER_STATE=FOOTERPULLDOING;
								checkFooterState();
							}
						}
						if(isCancelFootder){
							addFooterView(footer);
							isCancelFootder=false;
						}
					}
					break;
				case MotionEvent.ACTION_UP:
					initV=false;
					if(isCancelHeader==false&&(HEADER_STATE==HEADERPULLDOWN||HEADER_STATE==HEADERPULLDO)){
						HEADER_STATE=HEADERPULLDOWN;
						checkHeaderState();
						cancelHeader();
					}
					if(isCancelFootder==false&&(FOOTER_STATE==FOOTERPULLDOWN||FOOTER_STATE==FOOTERPULLDO)){
						FOOTER_STATE=FOOTERPULLDOWN;
						checkFooterState();
						cancelFooter();
					}
					break;
				}
				return false;
			}
		});
	}
	@Override
	public void setAdapter(ListAdapter adapter) {
		addHeaderView(header);
		addFooterView(footer);
		super.setAdapter(adapter);
	}
	/**
	 * 检查状态，并更新视图
	 */
	private void checkHeaderState(){
		if(!isCancelHeader){
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
					if(backCall!=null){
						if(headerLoadingLock){
							Toast.makeText(getContext(), R.string.isLoading, Toast.LENGTH_SHORT).show();
						}else{
							backCall.executeHeader();
							headerLoadingLock=true;
						}
					}
					setHeaderH(DEFAULT);
					break;
			}
		}
	}
	/**
	 * 检查状态，并更新视图
	 */
	private void checkFooterState(){
		if(!isCancelFootder){
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
					if(backCall!=null){
						if(footerLoadingLock){
							Toast.makeText(getContext(), R.string.isLoading, Toast.LENGTH_SHORT).show();
						}else{
							backCall.executeFooter();
							footerLoadingLock=true;
						}
					}
					setFooterH(DEFAULT);
					break;
			}
		}
	}
	/**
	 * 设置头高度
	 * @param height
	 */
	public void setHeaderH(int height){
		ListView.LayoutParams lp=new ListView.LayoutParams(LayoutParams.FILL_PARENT, height);
		header.setLayoutParams(lp);
	}
	/**
	 * 设置脚高度
	 * @param height
	 */
	public void setFooterH(int height){
		ListView.LayoutParams lp=new ListView.LayoutParams(LayoutParams.FILL_PARENT, height);
		footer.setLayoutParams(lp);
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
	 * 取消头显示
	 */
	public void cancelHeader(){
		HEADER_STATE=HEADERPULLDOWN;
		checkHeaderState();
		setHeaderH(1);
		removeHeaderView(header);
		isCancelHeader=true;
		headerLoadingLock=false;
	}
	/**
	 * 取消脚显示
	 */
	public void cancelFooter(){
		FOOTER_STATE=FOOTERPULLDOWN;
		checkFooterState();
		setFooterH(1);
		removeFooterView(footer);
		isCancelFootder=true;
		footerLoadingLock=false;
	}
	public boolean isHeaderLock() {
		return headerLock;
	}
	public void setHeaderLock(boolean headerLock) {
		this.headerLock = headerLock;
	}
	public boolean isFooterLock() {
		return footerLock;
	}
	public void setFooterLock(boolean footerLock) {
		this.footerLock = footerLock;
	}
}
