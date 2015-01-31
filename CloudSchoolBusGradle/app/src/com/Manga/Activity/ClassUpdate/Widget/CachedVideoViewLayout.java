package com.Manga.Activity.ClassUpdate.Widget;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.Manga.Activity.R;

public class CachedVideoViewLayout extends RelativeLayout {
	private TextView progressTXT;
	private ResizableVideoView mVideoView;
	//private ShareImage image;
	private ImageView playIcon;
	private float maxSize;
	private float currentSize;
	private Uri mUri;
	private final static int NO_SDAR = 0;
	private final static int SHOW_PRO = 1;
	private final static int CURRENT_SIZE = 2;
	private final static int DELETE_PRO = 3;
	private final static int PLAY = 4;
	private boolean playing = false;
	private File file;
	private Handler handler = new Handler(new Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case NO_SDAR:
				// 无卡通知
				Toast.makeText(getContext(), R.string.please_check_care, Toast.LENGTH_SHORT).show();
				break;
			case SHOW_PRO:
				// 显示进度
				progressTXT.setVisibility(View.VISIBLE);
				break;
			case CURRENT_SIZE:
				// 更新进度
				float showNum = (currentSize / maxSize)* 100f;
				String showTxt;
				if (showNum < 0) {
					showTxt = "?";
				} else if (showNum < 100) {
					String foo=showNum + "";
					showTxt = (showNum + "").substring(0, foo.indexOf("."));
				} else {
					showTxt = getResources().getString(R.string.please_waiting);
					break;
				}
				progressTXT.setText(showTxt + "\t" + "%");
				break;
			case DELETE_PRO:
				// 取消进度显示
				progressTXT.setVisibility(View.GONE);
				break;
			case PLAY:
				// 播放
				mUri = Uri.parse((String) msg.obj);
				mVideoView.setVideoURI(mUri);
				//R.drawable.play
//				if (checkPlay()) {
					mVideoView.setVisibility(View.VISIBLE);
					mVideoView.start();
					//image.setVisibility(View.GONE);
					playing = true;
//				}else{
//					playIcon.setVisibility(View.VISIBLE);
//				}
				mVideoView.requestFocus();
				mVideoView.setOnCompletionListener(new OnCompletionListener() {

					@Override
					public void onCompletion(MediaPlayer mp) {
						mVideoView.setVideoURI(mUri);
						mVideoView.start();
					}
				});
				break;
			}
			
			return false;
		}
	});

	public CachedVideoViewLayout(Context context) {
		super(context);
		init();
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public CachedVideoViewLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		View view = View.inflate(getContext(), R.layout.my_video_view, null);
		progressTXT = (TextView) view.findViewById(R.id.progress);
		mVideoView = (ResizableVideoView) view.findViewById(R.id.videoView);
		playIcon=(ImageView) view.findViewById(R.id.play_icon);
	    addView(view);
	}

	public void loading(final String url) {
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
					String name = url.substring(url.lastIndexOf("/"));
					String sdpath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/云中校车/";
					File f = new File(sdpath);
					if (!f.exists()) {
						f.mkdirs();
					}
					File nativeFile = new File(f.getAbsolutePath() + "/" + name);
					try {
						if (nativeFile.createNewFile()) {
							// 文件不存在操作
							URL webUrl = new URL(url);
							// 创建连接
							HttpURLConnection conn = (HttpURLConnection) webUrl.openConnection();
							conn.connect();
							// 获取文件大小
							maxSize = conn.getContentLength();
							// 获取输入流
							InputStream is = conn.getInputStream();
							// 文件输出流
							FileOutputStream fos = new FileOutputStream(nativeFile.getAbsolutePath());
							// 缓存
							byte buf[] = new byte[1024];
							// 写入到文件中
							currentSize = 0;
							while (true) {
								int numread = is.read(buf);
								currentSize += numread;
								if (numread <= 0) {
									// 下载完成
									handler.sendEmptyMessage(DELETE_PRO);
									file=nativeFile;
									handler.sendMessage(handler.obtainMessage(PLAY, nativeFile.getAbsolutePath()));
									break;
								}
								// 写入文件
								fos.write(buf, 0, numread);
								// 计算进度条位置
								if (progressTXT.getVisibility() != View.VISIBLE) {
									handler.sendEmptyMessage(SHOW_PRO);
								}
								handler.sendEmptyMessage(CURRENT_SIZE);
								float showNum = (currentSize / maxSize)* 100f;
							}
							fos.close();
							is.close();
						} else {
							// 文件已经存在
							handler.sendMessage(handler.obtainMessage(PLAY, nativeFile.getAbsolutePath()));
						}
					} catch (MalformedURLException e) {
						e.printStackTrace();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					handler.sendEmptyMessage(NO_SDAR);
				}
			}
		});
		thread.start();
	}

	private boolean checkPlay() {
		SharedPreferences sp = getContext().getSharedPreferences("auto_paly", Context.MODE_PRIVATE);
		boolean closeA=sp.getBoolean("closeAutoPlay", false);
		boolean wifiA=sp.getBoolean("wifiAutoPlay", false);
		boolean allA=sp.getBoolean("allAutoPlay", false);
		if(closeA){//关闭自动播放
			return false;
		}
		if(allA){//自动播放
			return true;
		}
		if(wifiA){
			ConnectivityManager connManager = (ConnectivityManager) (getContext().getSystemService(Context.CONNECTIVITY_SERVICE));
			NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			return mWifi.isConnected();
		}
		//默认关闭自动播放
		return false;
	}

	public void stop() {
		mVideoView.stopPlayback();
	}

//	public ShareImage getImage() {
//		return image;
//	}
	public void deleteFile(){
		if (file!=null&&file.isFile() && file.exists()) {  
	        file.delete();  
	    }  
	}
	public void click(){
		if (playing) {
			mVideoView.pause();
			playing = false;
			playIcon.setVisibility(View.VISIBLE);
		} else {
			mVideoView.start();
			mVideoView.setVisibility(View.VISIBLE);
			//image.setVisibility(View.GONE);
			playing = true;
			playIcon.setVisibility(View.GONE);
		}
	}

	public ResizableVideoView getmVideoView() {
		return mVideoView;
	}

	public void setmVideoView(ResizableVideoView mVideoView) {
		this.mVideoView = mVideoView;
	}
}
