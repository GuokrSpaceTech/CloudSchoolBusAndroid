package com.Manga.Activity.ClassUpdate;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import cn.sharesdk.onekeyshare.OnekeyShare;

import com.Manga.Activity.R;
import com.Manga.Activity.DB.DB;
import com.Manga.Activity.bigPicture.BigPictureActivity;
import com.Manga.Activity.httputils.HttpUtil;
import com.Manga.Activity.httputils.Params;
import com.Manga.Activity.httputils.Result;
import com.Manga.Activity.utils.ActivityUtil;
import com.Manga.Activity.utils.ImageUtil;
import com.Manga.Activity.utils.Student_Info;
import com.Manga.Activity.widget.CommentButton;
import com.Manga.Activity.widget.FullScreenVideoView;
import com.Manga.Activity.widget.MyVideoView;
import com.Manga.Activity.widget.ShareButton;
import com.Manga.Activity.widget.ZanButton;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

@SuppressLint("SimpleDateFormat")
public class ArticleAdapter extends BaseAdapter {
	private SimpleDateFormat spl = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat toYearSdf = new SimpleDateFormat("MM-dd HH:mm");
	private long toYear;
	ImageLoader imageLoader = ImageLoader.getInstance();
	DisplayImageOptions options;
	ImageLoaderConfiguration config;
	Context cntx;
	TagDto tag;
	MyVideoView currentPlayingVideoView = null;
	

	/**
	 * 网络没有连通
	 */
	private static final int NETISNOTWORKING = 0;
	/**
	 * 赞评论失败
	 */
	private static final int ZANFAIL = 1;
	/**
	 * 取消赞失败
	 */
	private static final int CANCELZANFAIL = 2;
	/**
	 * 赞成功
	 */
	private static final int ZANSUCCESS = 3;
	/**
	 * 取消赞成功
	 */
	private static final int CANCELZANSUCCESS = 4;
	private ArrayList<ArticleDto> list;
	
	private Handler handler = new Handler(new Callback() {

		@Override
		public boolean handleMessage(Message message) {
			switch (message.what) {
			case NETISNOTWORKING:
				Toast.makeText(cntx, R.string.network_broken,
						Toast.LENGTH_SHORT).show();
				break;
			case ZANFAIL:
				Toast.makeText(cntx, R.string.zan_fail,
						Toast.LENGTH_SHORT).show();
				break;
			case CANCELZANFAIL:
				Toast.makeText(cntx, R.string.cancel_zan_fail,
						Toast.LENGTH_SHORT).show();
				break;
			case ZANSUCCESS:
				ZanButton zanButton = ((ZanButton) message.obj);
				zanButton.setIsZan(true);
				zanButton.addZanNum();
				break;
			case CANCELZANSUCCESS:
				ZanButton zanButton_ = ((ZanButton) message.obj);
				zanButton_.setIsZan(false);
				zanButton_.subZanNum();
				break;
			}
			return false;
		}
	});


	public ArticleAdapter(Context context) {
		super();
		this.cntx = context;
		File cacheDir = com.nostra13.universalimageloader.utils.StorageUtils.getCacheDirectory(cntx);
		options = new DisplayImageOptions.Builder()
	      .showImageForEmptyUri(R.drawable.play_clean)
	      .showImageOnFail(R.drawable.play_clean)
	      .cacheInMemory(true)
	      .cacheOnDisc(true)
	      .considerExifParams(true)
	      .bitmapConfig(Bitmap.Config.RGB_565)
	      .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
	      .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
	      .defaultDisplayImageOptions(options)
	      .memoryCache(new UsingFreqLimitedMemoryCache(5 * 1024 * 1024))
	      .discCache(new UnlimitedDiscCache(cacheDir))
	      .discCacheFileCount(100)
	      .build();
        imageLoader.init(config);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		final ViewHolder holder;
		final ArticleDto article;
		if (convertView == null || convertView.getTag() == null) {
			holder = new ViewHolder();
			convertView = View.inflate(cntx, R.layout.class_share_item, null);
			//holder.contentLayout = (RelativeLayout) convertView.findViewById(R.id.content_layout);
			holder.content = (TextView) convertView.findViewById(R.id.share_content);
			holder.shareTime = (TextView) convertView.findViewById(R.id.share_time);
			holder.justOne = (ImageView) convertView.findViewById(R.id.just_one);
			holder.playIcon = (ImageView) convertView.findViewById(R.id.play_icon);
			holder.videoView = (MyVideoView)convertView.findViewById(R.id.videoView);
			holder.tag1 = (Button)convertView.findViewById(R.id.tag1);
			holder.tag2 = (Button)convertView.findViewById(R.id.tag2);
			holder.tag3 = (Button)convertView.findViewById(R.id.tag3);			
			holder.tag4 = (Button)convertView.findViewById(R.id.tag4);
			holder.share = (ShareButton) convertView.findViewById(R.id.share);
			holder.zan = (ZanButton) convertView.findViewById(R.id.zan);
			holder.comment = (CommentButton) convertView.findViewById(R.id.comment);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		if(list==null || list.size()==0)
			return convertView;
		else
		    article = list.get(position);
		
		String upnum = article.getUpnum();
		String commentnum = article.getCommentnum();

		holder.content.setText(article.getContent());

		if (Integer.parseInt(article.getHavezan()) > 0) {
			holder.zan.setIsZan(true);
		} else {
			holder.zan.setIsZan(false);
		}

		holder.share.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String strPath = article.getPlist().get(0).getSource();
				showShare(false, "", strPath, article.getContent());
			}
		});
		
		//Tag List
		List<TagDto> tagList = article.getTaglist();
		if(tagList != null && tagList.size() > 0)
		{	
		    for(int i=0; i<tagList.size(); i++)
			{
		    	tag = tagList.get(i);
		    	switch(i)
		    	{
		    	    case 0:
		    	    	holder.tag1.setVisibility(View.VISIBLE);
		    	    	holder.tag1.setText(tag.getTagName());
		    	    	holder.tag1.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								Intent intent = new Intent(ActivityUtil.share, TagDialogActivity.class);
								intent.putExtra("description", tag.getTagnamedesc());
								cntx.startActivity(intent);
							}
			            });
		    	        break;
		    	    case 1:
		    	    	holder.tag2.setVisibility(View.VISIBLE);
		    	    	holder.tag2.setText(tag.getTagName());
		    	    	holder.tag2.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								Intent intent = new Intent(ActivityUtil.share, TagDialogActivity.class);
								intent.putExtra("description", tag.getTagnamedesc());
								cntx.startActivity(intent);
							}
			            });
		    	    	break;
		    	    case 2:
		    	    	holder.tag3.setVisibility(View.VISIBLE);
		    	    	holder.tag3.setText(tag.getTagName());
		    	    	holder.tag3.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								Intent intent = new Intent(ActivityUtil.share, TagDialogActivity.class);
								intent.putExtra("description", tag.getTagnamedesc());
								cntx.startActivity(intent);
							}
			            });
		    	    	break;
		    	    case 3:
		    	    	holder.tag4.setVisibility(View.VISIBLE);
		    	    	holder.tag4.setText(tag.getTagName());
		    	    	holder.tag4.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								Intent intent = new Intent(ActivityUtil.share, TagDialogActivity.class);
								intent.putExtra("description", tag.getTagnamedesc());
								cntx.startActivity(intent);
							}
			            });
		    		    break;
		    		default:
		    			break;
		    	}
			}
		}else{
			holder.tag1.setVisibility(View.INVISIBLE);
			holder.tag1.setOnClickListener(null);
			holder.tag2.setVisibility(View.INVISIBLE);
			holder.tag2.setOnClickListener(null);
			holder.tag3.setVisibility(View.INVISIBLE);
			holder.tag3.setOnClickListener(null);
			holder.tag4.setVisibility(View.INVISIBLE);
			holder.tag4.setOnClickListener(null);
		}
		
		holder.zan.setZanNum(upnum);
		holder.zan.setTag(position);
		holder.zan.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (Student_Info.likeAble) {
					holder.zan.setClick();
					if (holder.zan.getIsZan()) {
						cancelZan(holder.zan, article);
					} else {
						zan(holder.zan, article);
					}
				}
			}
		});
		if (Student_Info.commentAble) {
			holder.comment.getImage().setBackgroundResource(R.drawable.comment);
		} else {
			holder.comment.getImage()
					.setBackgroundResource(R.drawable.comment_);
		}
		holder.comment.setZanNum(commentnum);
		String publishTime = article.getPublishtime();
		if (publishTime != null) {
			long foo = Long.parseLong(publishTime) * 1000;
			long tmp = System.currentTimeMillis() - foo;
			if (foo > toYear) {
				if (tmp < 12 * 60 * 60 * 1000) {

					if (tmp < 60 * 60 * 1000) {
						if (tmp <= 60 * 1000) {
							holder.shareTime.setText("1"
									+ cntx.getResources().getString(
											R.string.minute_befor));
						} else {
							holder.shareTime.setText(tmp
									/ (60 * 1000)
									+ cntx.getResources().getString(
											R.string.minute_befor));
						}

					} else {
						holder.shareTime.setText(tmp
								/ (60 * 60 * 1000)
								+ cntx.getResources().getString(
										R.string.hour_befor));
					}
				} else {
					holder.shareTime.setText(toYearSdf.format(new Date(foo)));

				}
			} else {
				holder.shareTime.setText(spl.format(new Date(foo)));
			}
		}
		
		final String file = article.getPlist().get(0).getSource();
		final String fext = article.getPlist().get(0).getFext();
		holder.comment.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(cntx,
						ClassUpdateCommentActivity.class);
				intent.putExtra("articleid", article.getArticleid());
				intent.putExtra("content", article.getContent());
				intent.putExtra("showTime", holder.shareTime.getText().toString());
				intent.putExtra("plist", file);
				intent.putExtra("fext", fext);
				intent.putExtra("havezan", article.getHavezan());
				ActivityUtil.main.comeIn(intent);
			}
		});
//		holder.contentLayout.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				Intent intent = new Intent(cntx,
//						ClassUpdateMainBodyActivity.class);
//				intent.putExtra("articleid", article.getArticleid());
//				intent.putExtra("content", article.getContent());
//				intent.putExtra("showTime", holder.shareTime.getText()
//						.toString());
//				intent.putExtra("plist", file);
//				intent.putExtra("fext", fext);
//				intent.putExtra("havezan", article.getHavezan());
//				ActivityUtil.main.comeIn(intent);
//			}
//		});

		holder.justOne.setImageResource(R.drawable.play_clean);
		if ("mp4".equals(fext)) {
			// 如果是视频
			//imageLoader.displayImage(file + ".jpg", holder.justOne, options);
			holder.videoView.setVisibility(View.VISIBLE);
			FullScreenVideoView tempVideoView = holder.videoView.getmVideoView();
			RelativeLayout reLayout = (RelativeLayout) holder.videoView.getParent();
			
			//The video is square
			tempVideoView.setVideoHeight(reLayout.getWidth());
			tempVideoView.setVideoWidth(reLayout.getWidth());
			
			//Start the video stop the previously played first
			if(currentPlayingVideoView != null)
				currentPlayingVideoView.stop();
			holder.videoView.loading(file);
			currentPlayingVideoView = holder.videoView;
			
//				imageLoader.displayImage(file + ".jpg", holder.justOne, options);
//				holder.justOne.setVisibility(View.VISIBLE);
//				holder.playIcon.setVisibility(View.VISIBLE);
//				
//				holder.justOne.setOnClickListener(new OnClickListener() {
//
//					@Override
//					public void onClick(View arg0) {
//
//						//The pic and icon gone
//						holder.justOne.setVisibility(View.INVISIBLE);
//						holder.playIcon.setVisibility(View.INVISIBLE);
//						
//						//The video comes out
//						holder.videoView.setVisibility(View.VISIBLE);
//						FullScreenVideoView tempVideoView = holder.videoView.getmVideoView();
//						tempVideoView.setVideoHeight(holder.justOne.getHeight());
//						tempVideoView.setVideoWidth(holder.justOne.getWidth());
//						holder.videoView.loading(file);
//					}
//				});
		} else {
			// 如果是图片
				imageLoader.displayImage(file + ImageUtil.SMALL, holder.justOne, options);
				//Video gone
				holder.playIcon.setVisibility(View.INVISIBLE);
				holder.videoView.setVisibility(View.INVISIBLE);
				holder.videoView.stop();
				if(currentPlayingVideoView != null)
				{
					currentPlayingVideoView.stop();
				    currentPlayingVideoView = null;
				}
                //Picture comes out
				holder.justOne.setVisibility(View.VISIBLE);
				holder.justOne.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						Intent intent = new Intent(cntx,
								BigPictureActivity.class);
						intent.putExtra("image", new String[] { file });
						intent.putExtra("position", 0);
						intent.putExtra("content", article.getContent());
						ActivityUtil.share.startActivity(intent);
						ActivityUtil.share.overridePendingTransition(R.anim.fade_in, R.anim.fade_in);
					}
				});
		} 
		return convertView;
	}

	static class ViewHolder {
		//RelativeLayout contentLayout;
		TextView content;
		TextView shareTime;
		ImageView justOne;
		ImageView playIcon;
		MyVideoView videoView;
		Button tag1;
		Button tag2;
		Button tag3;
		Button tag4;
		ZanButton zan;
		ShareButton share;
		CommentButton comment;
	}

	private void zan(final ZanButton zan, final ArticleDto article) {
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("itemid", article.getArticleid());
				map.put("isup", 1 + "");
				map.put("itemtype", "article");
				Params param = new Params("comment", map);
				Result result = HttpUtil.httpPost(cntx, param);
				if (result == null) {
					handler.sendEmptyMessage(NETISNOTWORKING);
				} else if ("1".equals(result.getCode())) {
					String newHaveZan = result.getContent();
					article.setHavezan(newHaveZan);
					int tmp = Integer.parseInt(article.getUpnum() + 1);
					article.setUpnum(tmp + "");
					DB db = new DB(cntx);
					SQLiteDatabase sql = db.getWritableDatabase();
					ContentValues values = new ContentValues();
					values.put("havezan", newHaveZan);
					values.put("upnum", article.getUpnum());
					sql.update(
							"article",
							values,
							"u_id=? and articleid=?",
							new String[] { Student_Info.uid,
									article.getArticleid()});
					sql.close();
					db.close();
					Message mess = handler.obtainMessage(ZANSUCCESS, zan);
					handler.sendMessage(mess);
				} else {
					handler.sendEmptyMessage(ZANFAIL);
				}
			}
		});
		thread.start();
	}

	private void cancelZan(final ZanButton zan,
			final ArticleDto article) {
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("key", article.getHavezan());
				map.put("type", "comment_action");
				Params param = new Params("delete", map);
				Result result = HttpUtil.httpPost(cntx, param);
				if (result == null) {
					handler.sendEmptyMessage(NETISNOTWORKING);
				} else if ("1".equals(result.getCode())) {
					article.setHavezan(0 + "");
					int tmp = Integer.parseInt(article.getUpnum());
					article.setUpnum(tmp-- + "");
					DB db = new DB(cntx);
					SQLiteDatabase sql = db.getWritableDatabase();
					ContentValues values = new ContentValues();
					values.put("havezan", 0 + "");
					values.put("upnum", tmp);
					sql.update(
							"article",
							values,
							"u_id=? and articleid=?",
							new String[] { Student_Info.uid,
									article.getArticleid() });
					sql.close();
					db.close();
					Message mess = handler.obtainMessage(CANCELZANSUCCESS, zan);
					handler.sendMessage(mess);
				} else {
					handler.sendEmptyMessage(CANCELZANFAIL);
				}
			}
		});
		thread.start();
	}

	public void setList(ArrayList<ArticleDto> list) {
		this.list = list;
	}
	
	private void showShare(boolean silent, String platform, String path, String content) {
		OnekeyShare oks = new OnekeyShare();
		oks.setNotification(R.drawable.icon, cntx.getString(R.string.app_name));
		if (content.length() > 70) {
			content = content.substring(0, 70);
		}
		oks.setText(content);
		oks.setImagePath(path);
		oks.setSilent(silent);
		oks.setPlatform(platform);
		oks.show(cntx);
	}

	@Override
	public int getCount() {
		if(list!=null)
		    return list.size();
		else
			return 0;
	}

	@Override
	public Object getItem(int position) {
		if(list!=null)
			return list.get(position);
		else
		   return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
}