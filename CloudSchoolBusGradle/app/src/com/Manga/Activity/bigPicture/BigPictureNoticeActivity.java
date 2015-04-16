package com.Manga.Activity.bigPicture;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.Manga.Activity.BaseActivity;
import com.Manga.Activity.R;
import com.Manga.Activity.httputils.HttpUtil;
import com.Manga.Activity.utils.ActivityUtil;
import com.Manga.Activity.utils.ImageUtil;
import com.Manga.Activity.widget.LoadingCircleView;
import com.Manga.Activity.widget.ShowBigPic;
import com.umeng.analytics.MobclickAgent;

@SuppressLint("SimpleDateFormat")
public class BigPictureNoticeActivity extends BaseActivity {
	private String[] reImage;
	private ShowBigPic image;
	private int position;
	private TextView biaoti;
	private static final int OK = 0;
	private static final int OUTTIME = 1;
	private static final int SAVESUCCESS = 2;
	private static final int SAVEFAIL = 3;
	private static final int SHOWDIALOG = 4;
	private static final int SHOWMISS = 5;
	/**
	 * 显示进度对话框
	 */
	private static final int SHOW_PRO_DIALOG = 6;
	/**
	 * 刷新进度
	 */
	private static final int SHOW_PRO_DIALOG_FRE = 7;
	/**
	 * 设置进度最大值
	 */
	private static final int SHOW_PRO_DIALOG_FRE_MAX = 8;
	/**
	 * 设置进度完成
	 */
	private static final int SHOW_PRO_DIALOG_OVER = 9;
	private String title;
	private String content;
	private String strPath = "";
	private ProgressDialog dialog;
	private LoadingCircleView proText;
	private AlertDialog loadDialog;
	private String mSavePath;
	private Handler handler = new Handler(new Callback() {

		@Override
		public boolean handleMessage(Message mes) {
			// TODO Auto-generated method stub
			switch (mes.what) {
			case OK:
				image.setBackgroundDrawable(new BitmapDrawable((Bitmap) mes.obj));
				break;
			case OUTTIME:
				Toast.makeText(BigPictureNoticeActivity.this, R.string.out_time,Toast.LENGTH_SHORT).show();
				break;
			case SAVESUCCESS:
				Toast.makeText(BigPictureNoticeActivity.this,R.string.bigpic_save_success, Toast.LENGTH_SHORT).show();
				break;
			case SAVEFAIL:
				Toast.makeText(BigPictureNoticeActivity.this,R.string.bigpic_save_fail, Toast.LENGTH_SHORT).show();
				break;
			case SHOWDIALOG:
				dialog = new ProgressDialog(BigPictureNoticeActivity.this);
				dialog.setMessage(getResources().getString(R.string.bigpic_down));
				dialog.setIndeterminate(false);
				dialog.setCancelable(true);
				dialog.show();
				break;
			case SHOWMISS:
				dialog.dismiss();
				break;
			case SHOW_PRO_DIALOG:
				if(loadDialog==null){
					AlertDialog.Builder builder=new AlertDialog.Builder(BigPictureNoticeActivity.this);
					View view =View.inflate(BigPictureNoticeActivity.this, R.layout.big_pic_pro, null);
					proText=(LoadingCircleView) view.findViewById(R.id.myLoading);
					proText.setRingColor(getResources().getColor(R.color.white));
					proText.setTextColor(getResources().getColor(R.color.white));
					proText.setTextSize(24);
					proText.setProgressColor(Color.parseColor("#2F58CF"));
					proText.setRingWidthDip(3);
					loadDialog=builder.create();
					loadDialog.setView(view, 0, 0, 0, 0);
				}
				loadDialog.show();
				break;
			case SHOW_PRO_DIALOG_FRE:
				proText.setProgress((Integer)mes.obj);
				break;
			case SHOW_PRO_DIALOG_FRE_MAX:
				proText.setMax((Integer)mes.obj);
				break;
			case SHOW_PRO_DIALOG_OVER:
				loadDialog.dismiss();
				image.setImageBitmap(BitmapFactory.decodeFile(mSavePath));
				Log.v("mSavePath", mSavePath);
				break;
			}
			return false;
		}
	});

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.big_picture);
		image = (ShowBigPic) findViewById(R.id.image);
		//biaoti = (TextView) findViewById(R.id.biaoti);
		Intent intent = getIntent();
		reImage = intent.getStringArrayExtra("image");
		position = intent.getIntExtra("position", 0);
		title = intent.getStringExtra("title");
		content = intent.getStringExtra("content");
		if (reImage == null) {
			Log.v("BigPicture", "无图片地址");
		} else {
			setImageBackgroundDrawable(reImage[position]);
		}
		checkTitle();
		image.setCall(new ShowBigPic.ImageBackCall() {
			
			@Override
			public void next() {
				// TODO Auto-generated method stub
				if (position + 1 < reImage.length) {
					position++;
					setImageBackgroundDrawable(reImage[position]);
					checkTitle();
				}
			}
			
			@Override
			public void last() {
				// TODO Auto-generated method stub
				if (position - 1 > -1) {
					position--;
					setImageBackgroundDrawable(reImage[position]);
					checkTitle();
				}
			}

			@Override
			public void click() {
				// TODO Auto-generated method stub
				
			}
		});
	}
	public void setImageBackgroundDrawable(final String image) {
		handler.sendEmptyMessage(SHOW_PRO_DIALOG);
		Thread thread=new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				//判断存储卡
			  if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
				  try {
					String foo=reImage[position];
					  foo=foo.substring(foo.lastIndexOf("/"));
					  String sdpath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/云中校车/";
					  File f=new File(sdpath);
					  if(!f.exists()){
						  f.mkdirs();
					  }
					  File nativeFile=new File(f.getAbsolutePath()+"/"+foo);
					  mSavePath = nativeFile.getAbsolutePath();
					  if(nativeFile.createNewFile()){
						  //文件不存在操作
						  URL url = new URL(reImage[position]);
						  // 创建连接  
						  HttpURLConnection conn = (HttpURLConnection) url.openConnection();  
						  conn.connect();  
						  // 获取文件大小  
						  int length = conn.getContentLength();
						  handler.sendMessage(handler.obtainMessage(SHOW_PRO_DIALOG_FRE_MAX, length));
						  // 获取输入流  
						  InputStream is = conn.getInputStream();
						  //文件输出流
						  FileOutputStream fos = new FileOutputStream(mSavePath);
						  // 缓存  
						  byte buf[] = new byte[1024];
						  // 写入到文件中  
						  int progress=0;
						  while(true){
							  int numread = is.read(buf);  
						      progress += numread;
						      Log.v("a",progress+"");
						      if (numread <= 0){
						          // 下载完成  
						          break;  
						      }
						      // 写入文件  
						      fos.write(buf, 0, numread);
						      // 计算进度条位置
						      handler.sendMessage(handler.obtainMessage(SHOW_PRO_DIALOG_FRE, progress));
						  }
						  fos.close();  
						  is.close();
						  handler.sendEmptyMessage(SHOW_PRO_DIALOG_OVER);
					  }else{
						  //文件已经存在操作
						  handler.sendEmptyMessage(SHOW_PRO_DIALOG_OVER);
					  }
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  
			  }
			}
		});
		thread.start();
	}
	private void checkTitle() {
		biaoti.setText(position + 1 + "/" + reImage.length);
	}

	public void backMenu(View v) {
		ActivityUtil.close(this);
	}

	private void showShare(boolean silent, String platform, String path) {
		OnekeyShare oks = new OnekeyShare();
		oks.setNotification(R.drawable.icon,getString(R.string.app_name));
		oks.setTitle(title);
		if (content.length() > 70) {
			content = content.substring(0, 70);
		}
		oks.setText(content);
		oks.setImagePath(path);
		//oks.setImageUrl(reImage[position]);
		oks.setSilent(silent);
		oks.setPlatform(platform);
		oks.show(BigPictureNoticeActivity.this);
	}

	public void more(View v) {
		if (HttpUtil.isNetworkConnected(BigPictureNoticeActivity.this)) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			View view = View.inflate(this, R.layout.dialog_bigpic, null);
			Button boy = (Button) view.findViewById(R.id.boy);
			Button girl = (Button) view.findViewById(R.id.girl);
			Button cancel = (Button) view.findViewById(R.id.cancel);
			final AlertDialog dialog = builder.create();
			dialog.setView(view, 0, 0, 0, 0);
			dialog.show();
			boy.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					dialog.dismiss();
					savepic();
				}

			});
			girl.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dialog.dismiss();
					loadpic();
					
				}
			});
			cancel.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dialog.dismiss();
				}
			});
		} else {
			handler.sendEmptyMessage(OUTTIME);
		}
	}

	public static void Copy(String oldPath, String newPath) {
		try {
			int byteread = 0;
			File oldfile = new File(oldPath);
			if (oldfile.exists()) {
				InputStream inStream = new FileInputStream(oldPath);
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1444];
				while ((byteread = inStream.read(buffer)) != -1) {
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
				fs.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void savepic() {
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				SimpleDateFormat sDateFormat = new SimpleDateFormat(
						"yyyyMMddhmmss");
				String date = sDateFormat.format(new java.util.Date());
				Bitmap bitmap = BitmapFactory.decodeFile(mSavePath);
				String imgurl = reImage[position].substring(reImage[position]
						.lastIndexOf("."));
				String temp = Environment.getExternalStorageDirectory()
						.getAbsolutePath();
				ContentResolver cr = getContentResolver();
				MediaStore.Images.Media.insertImage(cr, bitmap, "myPhoto", "this is a Photo");
				File f = new File(temp + "/" +date+imgurl);
				try {
					if (f.exists() == false) {
						f.createNewFile();
					} else {
						return;
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					handler.sendEmptyMessage(SAVEFAIL);
					return;
				}
				FileOutputStream fOut = null;
				try {
					fOut = new FileOutputStream(f);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					handler.sendEmptyMessage(SAVEFAIL);
					return;
				}
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
				try {
					fOut.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					handler.sendEmptyMessage(SAVEFAIL);
					return;
				}
				try {
					fOut.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					handler.sendEmptyMessage(SAVEFAIL);
					return;
				}
				// File f = aq.getCachedFile(reImage[position]);
				handler.sendEmptyMessage(SAVESUCCESS);

			}
		});
		thread.start();

	}
	private void loadpic() {
		strPath = ""; 
			
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				handler.sendEmptyMessage(SHOWDIALOG);
				// TODO Auto-generated method stub
				SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMddhmmss");
				String date = sDateFormat.format(new java.util.Date());
				Bitmap bitmap = ImageUtil.getImage(reImage[position]);
				String imgurl = reImage[position].substring(reImage[position].lastIndexOf("."));
				String temp = Environment.getExternalStorageDirectory().getAbsolutePath();
				strPath = temp + "/" +date+imgurl;
				File f = new File(temp + "/" +date+imgurl);
				try {
					if (f.exists() == false) {
						f.createNewFile();
					} else {
						handler.sendEmptyMessage(SHOWMISS);
						return;
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					handler.sendEmptyMessage(SHOWMISS);
					return;
				}
				FileOutputStream fOut = null;
				try {
					fOut = new FileOutputStream(f);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					handler.sendEmptyMessage(SHOWMISS);
					return;
				}
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
				try {
					fOut.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					handler.sendEmptyMessage(SHOWMISS);
					e.printStackTrace();
					return;
				}
				try {
					fOut.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					handler.sendEmptyMessage(SHOWMISS);
					e.printStackTrace();
					return;
				}
				handler.sendEmptyMessage(SHOWMISS);
				showShare(false, "", strPath);
				// File f = aq.getCachedFile(reImage[position]);
				return;
			}
		});
		thread.start();
	}public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
