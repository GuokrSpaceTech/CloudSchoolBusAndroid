package com.Manga.Activity.ClassUpdate;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.Manga.Activity.BaseActivity;
import com.Manga.Activity.R;
import com.Manga.Activity.httputils.HttpUtil;
import com.Manga.Activity.httputils.Params;
import com.Manga.Activity.httputils.Result;
import com.Manga.Activity.utils.ActivityUtil;
import com.Manga.Activity.utils.DensityUtil;
import com.Manga.Activity.utils.ImageUtil;
import com.Manga.Activity.widget.LisStudentHeaderView;
import com.Manga.Activity.widget.MyVideoView;
import com.cytx.utility.FastJsonTools;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.widget.RelativeLayout.LayoutParams;

public class ClassUpdateCommentActivity extends BaseActivity {
	private LinearLayout likeList;
	private LinearLayout commentList;
    private TextView content;
	private String articleid;
	private EditText inputContent;
	private Thread thread1;
	private boolean deleteFile = true;

	private TextView tv_tip1, tv_tip2, tv_tip3, tv_tip4, tv_tip5, tv_tip6;
	LinearLayout layout_tip_row1, layout_tip_row2, layout_tip;
	List<TagDto> tagList;
	public static String tagDesc;

    private static final int LIKELIST = 0;
	private static final int ZANLISTNONET = 1;
	private static final int COMMENT = 2;
	private static final int COMMENTNONET = 3;
	private static final int NETISNOTWORKING = 4;
	private static final int ZANSUCCESS = 5;
	private static final int CANCELZANFAIL = 6;
	private static final int COMMENTFAIL = 7;
	private static final int COMMENTSUESS = 8;
	private static final int COMMENTDE_FAIL = 9;
	private static final int ZAN = 10;
	private static final int ZANDEL = 11;
	private static final int CLEAR_ZAN_LIST = 12;
	private ProgressDialog progressDialog;
	private static final int SHOWPROGRESS = 13;
	private static final int DISMISSPROGRESS = 14;
	private static final int LUNOUT = 15;
	private static final int NOLIKE = 16;
	private static final int NOCOMMENT = 17;
	private SimpleDateFormat spl = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat toYearSdf = new SimpleDateFormat("MM-dd HH:mm");
	private long toYear;
	private long nowTime;
	private boolean isKeyUp;
	private String isHavingLikes;
    ImageLoader imageLoader = ImageLoader.getInstance();
    DisplayImageOptions options;
    ImageLoaderConfiguration config;
	private Button like;
	private ScrollView sc;
	private MyVideoView myVideo;
	private ProgressBar progress_image;
    private Handler handler;
    {
        handler = new Handler(new Handler.Callback() {

            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case LIKELIST:
                        List<LikeDto> likeDtoList = FastJsonTools.getListObject((String) msg.obj, LikeDto.class);
                        likeList.removeAllViews();
                        for (int i = 0; i < likeDtoList.size(); i++) {
                            LikeDto comment = likeDtoList.get(i);
                            LisStudentHeaderView headerView = new LisStudentHeaderView(ClassUpdateCommentActivity.this);
                            String strAvatar = comment.getAvatar();
                            if (strAvatar.equals("null")) {
                                headerView.setDefH();
                            } else {
                                headerView.setImageBackgroundDrawable(strAvatar);
                            }
                            LayoutParams params_ = new LayoutParams(DensityUtil.dip2px(ClassUpdateCommentActivity.this, 3),
                                    DensityUtil.dip2px(ClassUpdateCommentActivity.this, 30));
                            LayoutParams params = new LayoutParams(DensityUtil.dip2px(ClassUpdateCommentActivity.this, 30),
                                    DensityUtil.dip2px(ClassUpdateCommentActivity.this, 30));
                            View view = new View(ClassUpdateCommentActivity.this);
                            view.setBackgroundColor(Color.WHITE);
                            view.setLayoutParams(params_);
                            headerView.setLayoutParams(params);
                            likeList.addView(headerView);
                            likeList.addView(view);
                        }
                        break;
                    case NETISNOTWORKING:
                        Toast.makeText(ClassUpdateCommentActivity.this, R.string.out_time, Toast.LENGTH_SHORT).show();
                        break;
                    case COMMENTDE_FAIL:
                        Toast.makeText(ClassUpdateCommentActivity.this, R.string.comment_de_fail, Toast.LENGTH_SHORT).show();
                        break;
                    case COMMENTSUESS:
                        InputMethodManager imm1 = (InputMethodManager) getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
                        initCommentList();
                        inputContent.setText("");
                        sc.fullScroll(ScrollView.FOCUS_DOWN);
                    break;
                    case COMMENT:
                        commentList.removeAllViews();
                            List<CommentDto> commentDtoList = FastJsonTools.getListObject((String) msg.obj, CommentDto.class);
                            for (int i = 0; i < commentDtoList.size(); i++) {
                                CommentDto commentDto = commentDtoList.get(i);
                                View item = View.inflate(ClassUpdateCommentActivity.this, R.layout.comment_item, null);
                                LisStudentHeaderView headerView;
                                headerView = (LisStudentHeaderView) item.findViewById(R.id.header);
                                TextView title = (TextView) item.findViewById(R.id.title);
                                TextView content = (TextView) item.findViewById(R.id.content);
                                TextView time = (TextView) item.findViewById(R.id.time);
                                String isStudent = commentDto.getIsstudent();
                                String title_ = commentDto.getNickname();
                                String content_ = commentDto.getContent();
                                String mapAddtime = commentDto.getAddtime();
                                if ("1".equals(isStudent)) {
                                    title_ = title_ + getResources().getString(R.string.de_paret);
                                } else {
                                    title_ = title_ + getResources().getString(R.string.de_teacher);
                                }
                                if (commentDto.getReplynickname() == null) {
                                    Log.i("", "");
                                } else {
                                    content_ = getResources().getString(R.string.huifu) + commentDto.getReplynickname() + ":" + content_;
                                }
                                title.setText(title_);
                                content.setText(content_);
                                if (mapAddtime != null) {
                                    long foo = Long.parseLong(mapAddtime) * 1000;
                                    long tmp = nowTime - foo;
                                    if (foo > toYear) {
                                        if (tmp < 12 * 60 * 60 * 1000) {
                                            if (tmp < 60 * 60 * 1000) {
                                                if (tmp <= 60 * 1000) {
                                                    time.setText("1" + getResources().getString(R.string.minute_befor));
                                                } else {
                                                    time.setText((tmp) / (60 * 1000)
                                                            + getResources().getString(R.string.minute_befor));
                                                }
                                            } else {
                                                time.setText(tmp / (60 * 60 * 1000)
                                                        + getResources().getString(R.string.hour_befor));
                                            }
                                        } else {
                                            time.setText(toYearSdf.format(new Date(foo)));
                                        }
                                    } else {
                                        time.setText(spl.format(new Date(foo)));
                                    }
                                }
                                headerView.setImageBackgroundDrawable(commentDto.getAvatar());

                                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
                                commentList.addView(item, params);
                            }
//						item.setOnClickListener(new OnClickListener() {
//
//							@Override
//							public void onClick(View arg0) {
//								try {
//									if (HttpUtil.isNetworkConnected(ClassUpdateCommentActivity.this)) {
//										AlertDialog.Builder builder = new AlertDialog.Builder(
//												ClassUpdateCommentActivity.this);
//										View view = View.inflate(ClassUpdateCommentActivity.this,
//												R.layout.dialog_reply, null);
//										Button reply = (Button) view.findViewById(R.id.reply);
//										Button del = (Button) view.findViewById(R.id.delete);
//										Button cancel = (Button) view.findViewById(R.id.cancel);
//										final AlertDialog dialog = builder.create();
//										if (!Student_Info.uid.equals(object.getString("adduserid"))) {
//											del.setVisibility(View.GONE);
//										} else {
//											del.setVisibility(View.VISIBLE);
//										}
//										if (Student_Info.uid.equals(object.getString("adduserid"))) {
//											reply.setVisibility(View.GONE);
//										} else {
//											reply.setVisibility(View.VISIBLE);
//										}
//										dialog.setView(view, 0, 0, 0, 0);
//										dialog.show();
//										reply.setOnClickListener(new View.OnClickListener() {
//
//											@Override
//											public void onClick(View arg0) {
//												dialog.dismiss();
//												Intent intent = new Intent(ClassUpdateCommentActivity.this,
//														ReplyActivity.class);
//												try {
//													intent.putExtra("commentid", object.getString("commentid"));
//													intent.putExtra("articleid", articleid);
//												} catch (JSONException e) {
//													e.printStackTrace();
//												}
//												ActivityUtil.startActivity(ClassUpdateCommentActivity.this, intent);
//											}
//										});
//										del.setOnClickListener(new View.OnClickListener() {
//
//											@Override
//											public void onClick(View v) {
//												dialog.dismiss();
//												try {
//													deComment(object.getString("commentid"));
//												} catch (JSONException e) {
//													e.printStackTrace();
//												}
//											}
//										});
//										cancel.setOnClickListener(new View.OnClickListener() {
//
//											@Override
//											public void onClick(View v) {
//												dialog.dismiss();
//
//											}
//										});
//									} else {
//										handler.sendEmptyMessage(NETISNOTWORKING);
//									}
//								} catch (Exception e) {
//									e.printStackTrace();
//								}
//							}
//						});

//						DB db_comment = new DB(ClassUpdateCommentActivity.this);
//						SQLiteDatabase sql_comment = db_comment.getWritableDatabase();
//						Cursor cur_comment = sql_comment.query("comment", null,
//								"u_id=? and commentid=? and articleid=?",
//								new String[] { Student_Info.uid, object.getString("commentid"), articleid }, null,
//								null, null);
//						ContentValues values = new ContentValues();
//						values.put("u_id", Student_Info.uid);
//						values.put("commentid", object.getString("commentid"));
//						values.put("articleid", articleid);
//						values.put("content", object.getString("content"));
//						values.put("isstudent", object.getString("isstudent"));
//						values.put("nickname", object.getString("nickname"));
//						values.put("replynickname", object.getString("replynickname"));
//						values.put("avatar", object.getString("avatar"));
//						values.put("addtime", object.getString("addtime"));
//						values.put("candelete", object.getString("candelete"));
//						values.put("adduserid", object.getString("adduserid"));
//						if (cur_comment == null || cur_comment.getCount() == 0) {
//							sql_comment.insert("comment", "avatar", values);
//						} else {
//							sql_comment.update("comment", values, "u_id=? and commentid=? and articleid=?",
//									new String[] { Student_Info.uid, object.getString("commentid"), articleid });
//						}
//						if (cur_comment != null) {
//							cur_comment.close();
//						}
//						sql_comment.close();
//						db_comment.close();
//					}
//				} catch (JSONException e) {
//					e.printStackTrace();
//				} catch (JSONException e) {
//                        e.printStackTrace();
//                    }

                        break;
                }
                    return false;
            }
        });
    }

   @Override
   protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classupdate_comment);

		sc = (ScrollView) findViewById(R.id.scroll_view_comment_social);
                    TextView time = (TextView) findViewById(R.id.text_view_comment_time);
       content = (TextView)findViewById(R.id.text_view_comment_article_content);
		inputContent = (EditText) findViewById(R.id.edit_text_comment_input);
                    ImageView thumbNailImg = (ImageView)findViewById(R.id.image_view_comment_picture);
//		ShareImage image01 = (ShareImage) findViewById(R.id.share_image_one);
//		ShareImage image02 = (ShareImage) findViewById(R.id.share_image_two);
//		ShareImage image03 = (ShareImage) findViewById(R.id.share_image_three);
//		ShareImage image04 = (ShareImage) findViewById(R.id.share_image_four);
//		ShareImage image05 = (ShareImage) findViewById(R.id.share_image_five);
//		ShareImage image06 = (ShareImage) findViewById(R.id.share_image_six);
//		ShareImage image07 = (ShareImage) findViewById(R.id.share_image_seven);
//		ShareImage image08 = (ShareImage) findViewById(R.id.share_image_eight);
//		ShareImage image09 = (ShareImage) findViewById(R.id.share_image_nine);
//		tv_tip1 = (TextView) findViewById(R.id.tv_tip1);
//		tv_tip2 = (TextView) findViewById(R.id.tv_tip2);
//		tv_tip3 = (TextView) findViewById(R.id.tv_tip3);
//		tv_tip4 = (TextView) findViewById(R.id.tv_tip4);
//		tv_tip5 = (TextView) findViewById(R.id.tv_tip5);
//		tv_tip6 = (TextView) findViewById(R.id.tv_tip6);
//		layout_tip = (LinearLayout) findViewById(R.id.layout_tip);
//		layout_tip_row1 = (LinearLayout) findViewById(R.id.layout_tip_row1);
//		layout_tip_row2 = (LinearLayout) findViewById(R.id.layout_tip_row2);
//
//		/*
//		 * image01.setBackgroundResource(R.drawable.imageplaceholder);
//		 * image02.setBackgroundResource(R.drawable.imageplaceholder);
//		 * image03.setBackgroundResource(R.drawable.imageplaceholder);
//		 * image04.setBackgroundResource(R.drawable.imageplaceholder);
//		 * image05.setBackgroundResource(R.drawable.imageplaceholder);
//		 * image06.setBackgroundResource(R.drawable.imageplaceholder);
//		 * image07.setBackgroundResource(R.drawable.imageplaceholder);
//		 * image08.setBackgroundResource(R.drawable.imageplaceholder);
//		 * image09.setBackgroundResource(R.drawable.imageplaceholder);
//		 */
                    likeList = (LinearLayout) findViewById(R.id.linear_layout_likes_list);
                    commentList = (LinearLayout) findViewById(R.id.linear_layout_comment_commentlist);

                    Intent intent = getIntent();
                    articleid = intent.getStringExtra("articleid");
//		final String titleValue = intent.getStringExtra("title");
                    final String contentValue = intent.getStringExtra("content");
                    final String fext = intent.getStringExtra("fext");
                    String showTime = intent.getStringExtra("showTime");
                    final String plist = intent.getStringExtra("plist");
                    isHavingLikes = intent.getStringExtra("havezan");
//		if (Integer.parseInt(isHavingLikes) > 0) {
//			like.setBackgroundDrawable(getResources().getDrawable(R.drawable.class_share_main_body_like_selector));
//		} else {
//			like.setBackgroundDrawable(getResources().getDrawable(R.drawable.class_share_main_body_like_del_selector));
//		}
//		title.setText(titleValue);
//		if ("".equals(titleValue)) {
//			title.setVisibility(View.GONE);
//		}
//		/**
//		 * 2014-08-13 dido update
//		 * 
//		 * 文字描述区 显示全部的文字内容，当无文字描述时显示拍摄时间---拍摄于2014-01-01 11:11
//		 */

                    content.setText(contentValue);
                    time.setText(showTime);
                    final ProgressBar spinner = (ProgressBar)findViewById(R.id.progress_bar_comment_progress);
                    File cacheDir = com.nostra13.universalimageloader.utils.StorageUtils.getCacheDirectory(getApplicationContext());
                    options = new DisplayImageOptions.Builder()
                            .showImageForEmptyUri(R.drawable.play_clean)
                            .showImageOnFail(R.drawable.play_clean)
                            .cacheInMemory(true)
                            .cacheOnDisc(true)
                            .considerExifParams(true)
                            .bitmapConfig(Bitmap.Config.RGB_565)
                            .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                            .displayer(new FadeInBitmapDisplayer(300))
                            .build();
                    ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                            .defaultDisplayImageOptions(options)
                            .memoryCache(new UsingFreqLimitedMemoryCache(1 * 1024 * 1024))
                            .discCache(new UnlimitedDiscCache(cacheDir))
                            .discCacheFileCount(1)
                            .build();
                    imageLoader.init(config);

                    imageLoader.displayImage(plist + ImageUtil.TINY, thumbNailImg, options, new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {
                            spinner.setVisibility(View.VISIBLE);
                        }
                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                            String message = null;
                            switch (failReason.getType()) {
                                case IO_ERROR:
                                    message = "Input/Output error";
                                    break;
                                case DECODING_ERROR:
                                    message = "Image can't be decoded";
                                    break;
                                case NETWORK_DENIED:
                                    message = "Downloads are denied";
                                    break;
                                case OUT_OF_MEMORY:
                                    message = "Out Of Memory error";
                                    break;
                                case UNKNOWN:
                                    message = "Unknown error";
                                    break;
                            }
                            Toast.makeText(getApplicationContext(),message, Toast.LENGTH_SHORT).show();

                            spinner.setVisibility(View.GONE);
                        }

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            spinner.setVisibility(View.GONE);
                        }
                    });

       initLikeList();

       initCommentList();
   }
//		tagList = ActivityUtil.share.tagMap.get(articleid);
//		if (tagList == null || tagList.size() == 0) {
//			layout_tip.setVisibility(View.GONE);
//		} else {
//			if (tagList.size() <= 3) {
//				layout_tip_row1.setVisibility(View.VISIBLE);
//				layout_tip_row2.setVisibility(View.GONE);
//				if (tagList.size() == 1) {
//					tv_tip2.setVisibility(View.GONE);
//					tv_tip3.setVisibility(View.GONE);
//					tv_tip1.setVisibility(View.VISIBLE);
//					tv_tip1.setText(tagList.get(0).getTagName());
//					tv_tip1.setOnClickListener(tagListener);
//				} else if (tagList.size() == 2) {
//					tv_tip2.setVisibility(View.VISIBLE);
//					tv_tip3.setVisibility(View.GONE);
//					tv_tip1.setVisibility(View.VISIBLE);
//					tv_tip1.setText(tagList.get(0).getTagName());
//					tv_tip2.setText(tagList.get(1).getTagName());
//					tv_tip1.setOnClickListener(tagListener);
//					tv_tip2.setOnClickListener(tagListener);
//				} else if (tagList.size() == 3) {
//					tv_tip2.setVisibility(View.VISIBLE);
//					tv_tip3.setVisibility(View.VISIBLE);
//					tv_tip1.setVisibility(View.VISIBLE);
//					tv_tip1.setText(tagList.get(0).getTagName());
//					tv_tip2.setText(tagList.get(1).getTagName());
//					tv_tip3.setText(tagList.get(2).getTagName());
//					tv_tip1.setOnClickListener(tagListener);
//					tv_tip2.setOnClickListener(tagListener);
//					tv_tip3.setOnClickListener(tagListener);
//				}
//			} else {
//				layout_tip_row1.setVisibility(View.VISIBLE);
//				layout_tip_row2.setVisibility(View.VISIBLE);
//				if (tagList.size() == 4) {
//					tv_tip5.setVisibility(View.GONE);
//					tv_tip6.setVisibility(View.GONE);
//					tv_tip4.setVisibility(View.VISIBLE);
//					tv_tip1.setText(tagList.get(0).getTagName());
//					tv_tip2.setText(tagList.get(1).getTagName());
//					tv_tip3.setText(tagList.get(2).getTagName());
//					tv_tip4.setText(tagList.get(3).getTagName());
//					tv_tip1.setOnClickListener(tagListener);
//					tv_tip2.setOnClickListener(tagListener);
//					tv_tip3.setOnClickListener(tagListener);
//					tv_tip4.setOnClickListener(tagListener);
//
//				} else if (tagList.size() == 5) {
//					tv_tip5.setVisibility(View.VISIBLE);
//					tv_tip6.setVisibility(View.GONE);
//					tv_tip4.setVisibility(View.VISIBLE);
//					tv_tip1.setText(tagList.get(0).getTagName());
//					tv_tip2.setText(tagList.get(1).getTagName());
//					tv_tip3.setText(tagList.get(2).getTagName());
//					tv_tip4.setText(tagList.get(3).getTagName());
//					tv_tip5.setText(tagList.get(4).getTagName());
//					tv_tip1.setOnClickListener(tagListener);
//					tv_tip2.setOnClickListener(tagListener);
//					tv_tip3.setOnClickListener(tagListener);
//					tv_tip4.setOnClickListener(tagListener);
//					tv_tip5.setOnClickListener(tagListener);
//
//				} else if (tagList.size() == 6) {
//					tv_tip5.setVisibility(View.VISIBLE);
//					tv_tip6.setVisibility(View.VISIBLE);
//					tv_tip4.setVisibility(View.VISIBLE);
//					tv_tip1.setText(tagList.get(0).getTagName());
//					tv_tip2.setText(tagList.get(1).getTagName());
//					tv_tip3.setText(tagList.get(2).getTagName());
//					tv_tip4.setText(tagList.get(3).getTagName());
//					tv_tip5.setText(tagList.get(4).getTagName());
//					tv_tip6.setText(tagList.get(5).getTagName());
//					tv_tip1.setOnClickListener(tagListener);
//					tv_tip2.setOnClickListener(tagListener);
//					tv_tip3.setOnClickListener(tagListener);
//					tv_tip4.setOnClickListener(tagListener);
//					tv_tip5.setOnClickListener(tagListener);
//					tv_tip6.setOnClickListener(tagListener);
//
//				}
//
//			}
//		}
//		//ActivityUtil.shareMain = this;
//		try {
//			Date date = new Date();
//			SimpleDateFormat foo = new SimpleDateFormat("yyyy");
//			Date tmp = foo.parse(spl.format(date).split("-")[0]);
//			toYear = tmp.getTime();
//			nowTime = System.currentTimeMillis();
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
//		initLikeList();
//		initCommentList();
//		if (Student_Info.commentAble) {// 评论控制
//
//		} else {
//			inputContent.setEnabled(false);
//		}
//		ResizeRelativeLayout layout = (ResizeRelativeLayout) findViewById(R.id.relativeLayoutall);
//		layout.setOnResizeListener(new ResizeRelativeLayout.OnResizeListener() {
//			public void OnResize(int w, int h, int oldw, int oldh) {
//				if (h < oldh) {
//					isKeyUp = true;
//					like.setBackgroundDrawable(getResources().getDrawable(
//							R.drawable.class_share_main_body_comment_finish_selector));
//				} else {
//					isKeyUp = false;
//					if (Integer.parseInt(isHavingLikes) > 0) {
//						like.setBackgroundDrawable(getResources().getDrawable(
//								R.drawable.class_share_main_body_like_selector));
//					} else {
//						like.setBackgroundDrawable(getResources().getDrawable(
//								R.drawable.class_share_main_body_like_del_selector));
//					}
//				}
//			}
//		});
//		if ("mp4".equals(fext)) {
//			myVideo.setVisibility(View.VISIBLE);
//			if (plist.equals("")) {
//
//			} else {
//				//myVideo.getImage().setImageBackgroundDrawable(plist + ".jpg");
//				myVideo.loading(plist);
//			}
//		} else {
//			if (plist.equals("")) {
//
//			} else if (plist.indexOf(",") == -1) {
//				// image01.setImageBackgroundDrawable(plist);
//				image01.setImageBackgroundDrawable(plist, progress_image);
//				image01.setVisibility(View.VISIBLE);
//				RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) image01
//						.getLayoutParams();
//				params.height = DensityUtil.dip2px(this, 250);
//				params.width = DensityUtil.dip2px(this, 250);
//				image01.setLayoutParams(params);
//				image01.setOnClickListener(new OnClickListener() {
//
//					@Override
//					public void onClick(View arg0) {
//						Intent intent = new Intent(ClassUpdateCommentActivity.this, BigPictureActivity.class);
//						intent.putExtra("image", new String[] { plist });
//						intent.putExtra("position", 0);
//						intent.putExtra("title", titleValue);
//						intent.putExtra("content", contentValue);
//						ActivityUtil.startActivity(ActivityUtil.share, intent);
//					}
//				});
//			} else {
//				final String[] tmp = plist.split(",");
//				for (int i = 0; i < tmp.length; i++) {
//					switch (i) {
//					case 0:
//						image01.setImageBackgroundDrawable(tmp[i]);
//						image01.setVisibility(View.VISIBLE);
//						image01.setOnClickListener(new OnClickListener() {
//
//							@Override
//							public void onClick(View arg0) {
//								Intent intent = new Intent(ClassUpdateCommentActivity.this, BigPictureActivity.class);
//								intent.putExtra("image", tmp);
//								intent.putExtra("position", 0);
//								intent.putExtra("title", titleValue);
//								intent.putExtra("content", contentValue);
//								ActivityUtil.startActivity(ActivityUtil.share, intent);
//							}
//						});
//						break;
//					case 1:
//						image02.setImageBackgroundDrawable(tmp[i]);
//						image02.setVisibility(View.VISIBLE);
//						image02.setOnClickListener(new OnClickListener() {
//
//							@Override
//							public void onClick(View arg0) {
//								Intent intent = new Intent(ClassUpdateCommentActivity.this, BigPictureActivity.class);
//								intent.putExtra("image", tmp);
//								intent.putExtra("position", 1);
//								intent.putExtra("title", titleValue);
//								intent.putExtra("content", contentValue);
//								ActivityUtil.startActivity(ActivityUtil.share, intent);
//							}
//						});
//						break;
//					case 2:
//						image03.setImageBackgroundDrawable(tmp[i]);
//						image03.setVisibility(View.VISIBLE);
//						image03.setOnClickListener(new OnClickListener() {
//
//							@Override
//							public void onClick(View arg0) {
//								Intent intent = new Intent(ClassUpdateCommentActivity.this, BigPictureActivity.class);
//								intent.putExtra("image", tmp);
//								intent.putExtra("position", 2);
//								intent.putExtra("title", titleValue);
//								intent.putExtra("content", contentValue);
//								ActivityUtil.startActivity(ActivityUtil.share, intent);
//							}
//						});
//						break;
//					case 3:
//						image04.setImageBackgroundDrawable(tmp[i]);
//						image04.setVisibility(View.VISIBLE);
//						image04.setOnClickListener(new OnClickListener() {
//
//							@Override
//							public void onClick(View arg0) {
//								Intent intent = new Intent(ClassUpdateCommentActivity.this, BigPictureActivity.class);
//								intent.putExtra("image", tmp);
//								intent.putExtra("position", 3);
//								intent.putExtra("title", titleValue);
//								intent.putExtra("content", contentValue);
//								ActivityUtil.startActivity(ActivityUtil.share, intent);
//							}
//						});
//						break;
//					case 4:
//						image05.setImageBackgroundDrawable(tmp[i]);
//						image05.setVisibility(View.VISIBLE);
//						image05.setOnClickListener(new OnClickListener() {
//
//							@Override
//							public void onClick(View arg0) {
//								Intent intent = new Intent(ClassUpdateCommentActivity.this, BigPictureActivity.class);
//								intent.putExtra("image", tmp);
//								intent.putExtra("position", 4);
//								intent.putExtra("title", titleValue);
//								intent.putExtra("content", contentValue);
//								ActivityUtil.startActivity(ActivityUtil.share, intent);
//							}
//						});
//						break;
//					case 5:
//						image06.setImageBackgroundDrawable(tmp[i]);
//						image06.setVisibility(View.VISIBLE);
//						image06.setOnClickListener(new OnClickListener() {
//
//							@Override
//							public void onClick(View arg0) {
//								Intent intent = new Intent(ClassUpdateCommentActivity.this, BigPictureActivity.class);
//								intent.putExtra("image", tmp);
//								intent.putExtra("position", 5);
//								intent.putExtra("title", titleValue);
//								intent.putExtra("content", contentValue);
//								ActivityUtil.startActivity(ActivityUtil.share, intent);
//							}
//						});
//						break;
//					case 6:
//						image07.setImageBackgroundDrawable(tmp[i]);
//						image07.setVisibility(View.VISIBLE);
//						image07.setOnClickListener(new OnClickListener() {
//
//							@Override
//							public void onClick(View arg0) {
//								Intent intent = new Intent(ClassUpdateCommentActivity.this, BigPictureActivity.class);
//								intent.putExtra("image", tmp);
//								intent.putExtra("position", 6);
//								intent.putExtra("title", titleValue);
//								intent.putExtra("content", contentValue);
//								ActivityUtil.startActivity(ActivityUtil.share, intent);
//							}
//						});
//						break;
//					case 7:
//						image08.setImageBackgroundDrawable(tmp[i]);
//						image08.setVisibility(View.VISIBLE);
//						image08.setOnClickListener(new OnClickListener() {
//
//							@Override
//							public void onClick(View arg0) {
//								Intent intent = new Intent(ClassUpdateCommentActivity.this, BigPictureActivity.class);
//								intent.putExtra("image", tmp);
//								intent.putExtra("position", 7);
//								intent.putExtra("title", titleValue);
//								intent.putExtra("content", contentValue);
//								ActivityUtil.startActivity(ActivityUtil.share, intent);
//							}
//						});
//						break;
//					case 8:
//						image09.setImageBackgroundDrawable(tmp[i]);
//						image09.setVisibility(View.VISIBLE);
//						image09.setOnClickListener(new OnClickListener() {
//
//							@Override
//							public void onClick(View arg0) {
//								Intent intent = new Intent(ClassUpdateCommentActivity.this, BigPictureActivity.class);
//								intent.putExtra("image", tmp);
//								intent.putExtra("position", 8);
//								intent.putExtra("title", titleValue);
//								intent.putExtra("content", contentValue);
//								ActivityUtil.startActivity(ActivityUtil.share, intent);
//							}
//						});
//						break;
//					}
//				}
//			}
//		}
//		myVideo.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				myVideo.click();
//			}
//		});
//		myVideo.setOnLongClickListener(new OnLongClickListener() {
//
//			@Override
//			public boolean onLongClick(View v) {
//				View view = View.inflate(ClassUpdateCommentActivity.this, R.layout.londing_mp4, null);
//				Button cancel = (Button) view.findViewById(R.id.cancel);
//				Button relog = (Button) view.findViewById(R.id.relog);
//				final PopupWindow pw = new PopupWindow(view, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
//						false);
//				pw.setBackgroundDrawable(new BitmapDrawable());
//				pw.setOutsideTouchable(true);
//				pw.setFocusable(true);
//				pw.setAnimationStyle(R.style.popwin_anim_style);
//				cancel.setOnClickListener(new View.OnClickListener() {
//
//					@Override
//					public void onClick(View v) {
//						// TODO Auto-generated method stub
//						pw.dismiss();
//					}
//				});
//				relog.setOnClickListener(new View.OnClickListener() {
//
//					@Override
//					public void onClick(View v) {
//						// TODO Auto-generated method stub
//						deleteFile = false;
//						pw.dismiss();
//						Toast.makeText(ClassUpdateCommentActivity.this, R.string.xia_zai_wan_cheng, Toast.LENGTH_SHORT)
//								.show();
//					}
//				});
//				pw.showAsDropDown(v, 0, -200);
//				return false;
//			}
//		});
//	}
//

       public void close(View v) {
		      ActivityUtil.close(this);
	   }
//
	/**
	 * 初始化赞列表
	 */
	public void initLikeList() {
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
                HashMap<String, String> maps = new HashMap<String, String>();
                maps.put("itemtype", "article");
                maps.put("iszan", "1");
                maps.put("itemid", articleid);
                if (HttpUtil.isNetworkConnected(ClassUpdateCommentActivity.this)) {
                    Result result = HttpUtil.httpGet(ClassUpdateCommentActivity.this, new Params("comment", maps));

                    if (result == null) {
                    } else {
                        if ("1".equals(result.getCode())) {
                            handler.sendMessage(handler.obtainMessage(LIKELIST, result.getContent()));
//						DB db = new DB(ClassUpdateCommentActivity.this);
//						SQLiteDatabase sql = db.getWritableDatabase();
//						Cursor cur = sql.query("like", null, "u_id=? and actionid=? and articleid=?", new String[] {
//								Student_Info.uid, object.getString("actionid"), articleid }, null, null, null);
//						ContentValues values = new ContentValues();
//						values.put("u_id", Student_Info.uid);
//						values.put("actionid", object.getString("actionid"));
//						values.put("articleid", articleid);
//						values.put("avatar", object.getString("avatar"));
//						values.put("addtime", object.getString("addtime"));
//						if (cur == null || cur.getCount() == 0) {
//							sql.insert("like", "avatar", values);
//						} else {
//							sql.update("like", values, "u_id=? and actionid=? and articleid=?", new String[] {
//									Student_Info.uid, object.getString("actionid"), articleid });
//						}
//						if (cur != null) {
//							cur.close();
//						}
//						sql.close();
//						db.close();
//					}
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}
//				break;
//					} else if ("-3".equals(result.getCode())) {
//						handler.sendEmptyMessage(NOLIKE);
//					} else {
//						Log.v("获取头像失败", "获取头像失败……");
//					}
//				} else {
//					handler.sendEmptyMessage(ZANLISTNONET);
//				}
//			}
                            }
                        }
                    }
                }
		});

		thread.start();
	}

	/**
	 * 初始化评论列表
	 */
	public void initCommentList() {
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				HashMap<String, String> maps = new HashMap<String, String>();
				maps.put("itemtype", "article");
				maps.put("iszan", "0");
				maps.put("itemid", articleid);
				Result result = HttpUtil.httpGet(ClassUpdateCommentActivity.this, new Params("comment", maps));
				if (result == null) {

				} else if ("1".equals(result.getCode())) {
					handler.sendMessage(handler.obtainMessage(COMMENT, result.getContent()));
				} else if ("-3".equals(result.getCode())) {
					handler.sendEmptyMessage(NOCOMMENT);
				}
			}
		});
		thread.start();
	}

    public void publish(View v)
    {
        if (inputContent.getText().toString().length() == 0) {
            return;
        }
        if (!checkLength(inputContent.getText().toString())) {
            handler.sendEmptyMessage(LUNOUT);
            return;
        }
        handler.sendEmptyMessage(SHOWPROGRESS);
        if (HttpUtil.isNetworkConnected(getApplicationContext())) {
            thread1 = new Thread(new Runnable() {

                @Override
                public void run() {
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("itemid", articleid);
                    map.put("content", inputContent.getText().toString());
                    map.put("itemtype", "article");
                    Params param = new Params("comment", map);
                    Result result = HttpUtil.httpPost(getApplicationContext(), param);
                    if ("1".equals(result.getCode())) {
                        handler.sendEmptyMessage(COMMENTSUESS);
                    } else {
                        handler.sendEmptyMessage(COMMENTFAIL);
                    }
                }
            });
            if (!thread1.isAlive()) {
                thread1.start();
            }
        } else {
            // 无网操作
            handler.sendEmptyMessage(NETISNOTWORKING);
        }
    }


//	public void like(View v) {
//		if (!isKeyUp) {
//			// 赞操作
//			if (HttpUtil.isNetworkConnected(ClassUpdateCommentActivity.this)) {
//				// 有网操作
//				Thread thread = new Thread(new Runnable() {
//
//					@Override
//					public void run() {
//						if (Integer.parseInt(isHavingLikes) > 0) {
//							HashMap<String, String> map = new HashMap<String, String>();
//							map.put("key", isHavingLikes);
//							map.put("type", "comment_action");
//							Params param = new Params("delete", map);
//							Result result = HttpUtil.httpPost(ClassUpdateCommentActivity.this, param);
//							if (result == null) {
//								handler.sendEmptyMessage(NETISNOTWORKING);
//							} else if ("1".equals(result.getCode())) {
//								handler.sendEmptyMessage(ZANDEL);
//								isHavingLikes = "0";
//							} else {
//								handler.sendEmptyMessage(CANCELZANFAIL);
//							}
//						} else {
//							HashMap<String, String> map = new HashMap<String, String>();
//							map.put("itemid", articleid);
//							map.put("isup", 1 + "");
//							map.put("itemtype", "article");
//							Params param = new Params("comment", map);
//							Result result = HttpUtil.httpPost(ClassUpdateCommentActivity.this, param);
//							if (result == null) {
//								handler.sendEmptyMessage(NETISNOTWORKING);
//							} else if ("1".equals(result.getCode())) {
//								DB db = new DB(ClassUpdateCommentActivity.this);
//								SQLiteDatabase sql = db.getWritableDatabase();
//								ContentValues values = new ContentValues();
//								values.put("isHavingLikes", result.getContent());
//								isHavingLikes = result.getContent();
//								sql.update("article", values, "u_id=? and articleid=?", new String[] {
//										Student_Info.uid, articleid });
//								sql.close();
//								db.close();
//								handler.sendEmptyMessage(ZAN);
//							} else {
//								// handler.sendEmptyMessage(ZANFAIL);
//							}
//						}
//					}
//				});
//				thread.start();
//			} else {
//				// 无网操作
//				handler.sendEmptyMessage(NETISNOTWORKING);
//			}
//		} else {
//			// 评论操作
//			if (inputContent.getText().toString().length() == 0) {
//				return;
//			}
//			if (!checkLength(inputContent.getText().toString())) {
//				handler.sendEmptyMessage(LUNOUT);
//				return;
//			}
//			handler.sendEmptyMessage(SHOWPROGRESS);
//			if (HttpUtil.isNetworkConnected(ClassUpdateCommentActivity.this)) {
//				thread1 = new Thread(new Runnable() {
//
//					@Override
//					public void run() {
//						HashMap<String, String> map = new HashMap<String, String>();
//						map.put("itemid", articleid);
//						map.put("content", inputContent.getText().toString());
//						map.put("itemtype", "article");
//						Params param = new Params("comment", map);
//						Result result = HttpUtil.httpPost(ClassUpdateCommentActivity.this, param);
//						if ("1".equals(result.getCode())) {
//							handler.sendEmptyMessage(COMMENTSUESS);
//						} else {
//							handler.sendEmptyMessage(COMMENTFAIL);
//						}
//					}
//				});
//				if (!thread1.isAlive()) {
//					thread1.start();
//				}
//			} else {
//				// 无网操作
//				handler.sendEmptyMessage(NETISNOTWORKING);
//			}
//		}
//	}
//
//	private void deComment(final String commentid) {
//		Thread thread = new Thread(new Runnable() {
//
//			@Override
//			public void run() {
//				HashMap<String, String> map = new HashMap<String, String>();
//				map.put("key", commentid);
//				map.put("type", "comment");
//				Params param = new Params("delete", map);
//				Result result = HttpUtil.httpPost(ClassUpdateCommentActivity.this, param);
//				if (result == null) {
//
//				} else if ("1".equals(result.getCode())) {
//					initCommentList();
//				} else {
//					handler.sendEmptyMessage(COMMENTDE_FAIL);
//				}
//			}
//		});
//		thread.start();
//	}
//
	private boolean checkLength(String tmp) {
		int count = 0;
		for (int i = 0; i < tmp.length(); i++) {
			char c = tmp.charAt(i);
			if (c >= 0 && c <= 9) {
				count++;
			} else if (c >= 'a' && c <= 'z') {
				count++;
			} else if (c >= 'A' && c <= 'Z') {
				count++;
			} else if (Character.isLetter(c)) {
				count += 2;
			} else {
				count++;
			}
		}
		if (count > 140 || count == 0) {
			return false;
		}
		return true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
