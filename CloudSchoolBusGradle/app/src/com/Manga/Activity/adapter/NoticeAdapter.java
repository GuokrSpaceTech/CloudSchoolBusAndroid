package com.Manga.Activity.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.Manga.Activity.R;
import com.Manga.Activity.DB.DB;
import com.Manga.Activity.bigPicture.BigPictureNoticeActivity;
import com.Manga.Activity.httputils.HttpUtil;
import com.Manga.Activity.httputils.Params;
import com.Manga.Activity.httputils.Result;
import com.Manga.Activity.utils.ActivityUtil;
import com.Manga.Activity.utils.ImageUtil;
import com.Manga.Activity.utils.Student_Info;
import com.Manga.Activity.widget.ShareImage;

public class NoticeAdapter extends ArrayAdapter<Map<String, String>> {
	/**
	 * 网络没有连通
	 */
	private static final int NETISNOTWORKING = 0;
	/**
	 * 确认回执失败
	 */
	private static final int CONFIRMRECEIPTFAIL = 1;
	/**
	 * 确认回执成功
	 */
	private static final int CONFIRMRECEIPTSUCCESS = 2;
	private static final int CONFIRMRECEIPTSUCCESSALL = 3;
	private TextView textView;
	private Button button;
	private String strTitle;
	private String strNoticekey;
	private ArrayList<Map<String, String>> list;
	private SimpleDateFormat spl = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat toYearSdf = new SimpleDateFormat("MM-dd HH:mm");
	private long toYear;
	private long nowTime;
	private Handler handler = new Handler(new Callback() {

		@Override
		public boolean handleMessage(Message message) {
			// TODO Auto-generated method stub
			switch (message.what) {
			case NETISNOTWORKING:
				Toast.makeText(getContext(), R.string.network_broken,
						Toast.LENGTH_SHORT).show();
				break;
			case CONFIRMRECEIPTFAIL:
				Toast.makeText(getContext(), R.string.confirm_receipt_fail,
						Toast.LENGTH_SHORT).show();
				break;
			case CONFIRMRECEIPTSUCCESS:
				Toast.makeText(getContext(), R.string.confirm_receipt_success,
						Toast.LENGTH_SHORT).show();
				textView.setText(strTitle + "");
				button.setVisibility(View.GONE);
				textView.setTextColor(Color.parseColor("#FF000000"));
				break;
			}
			return false;
		}
	});

	public NoticeAdapter(Context context, ArrayList<Map<String, String>> list) {
		// TODO Auto-generated constructor stub
		super(context, R.layout.message_item, list);
		this.list = list;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final Map<String, String> map = getItem(position);
		final ViewHolder holder;
		final int index = position;
		if (convertView == null || convertView.getTag() == null) {
			convertView = View.inflate(getContext(), R.layout.message_item,
					null);
			holder = new ViewHolder();
			holder.title = (TextView) convertView
					.findViewById(R.id.share_title);
			holder.content = (TextView) convertView
					.findViewById(R.id.share_content);
			holder.shareTime = (TextView) convertView
					.findViewById(R.id.share_time);
			holder.share_more = (TextView) convertView
					.findViewById(R.id.share_more);
			holder.viewline = convertView.findViewById(R.id.viewline);
			holder.image01 = (ShareImage) convertView
					.findViewById(R.id.share_image_one);
			holder.image02 = (ShareImage) convertView
					.findViewById(R.id.share_image_two);
			holder.image03 = (ShareImage) convertView
					.findViewById(R.id.share_image_three);
			holder.image04 = (ShareImage) convertView
					.findViewById(R.id.share_image_four);
			holder.image05 = (ShareImage) convertView
					.findViewById(R.id.share_image_five);
			holder.image06 = (ShareImage) convertView
					.findViewById(R.id.share_image_six);
			holder.image07 = (ShareImage) convertView
					.findViewById(R.id.share_image_seven);
			holder.image08 = (ShareImage) convertView
					.findViewById(R.id.share_image_eight);
			holder.image09 = (ShareImage) convertView
					.findViewById(R.id.share_image_nine);
			holder.justOne = (ShareImage) convertView
					.findViewById(R.id.just_one);
			holder.btnconfirm = (Button) convertView
					.findViewById(R.id.confirm_receipt);
			holder.l_line_one = (LinearLayout) convertView
					.findViewById(R.id.l_line_one);
			holder.l_line_two = (LinearLayout) convertView
					.findViewById(R.id.l_line_two);
			holder.l_line_three = (LinearLayout) convertView
					.findViewById(R.id.l_line_three);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.l_line_one.setVisibility(View.GONE);
		holder.l_line_two.setVisibility(View.GONE);
		holder.l_line_three.setVisibility(View.GONE);
		holder.justOne.setVisibility(View.GONE);
		holder.image01.setVisibility(View.INVISIBLE);
		holder.image02.setVisibility(View.INVISIBLE);
		holder.image03.setVisibility(View.INVISIBLE);
		holder.image04.setVisibility(View.INVISIBLE);
		holder.image05.setVisibility(View.INVISIBLE);
		holder.image06.setVisibility(View.INVISIBLE);
		holder.image07.setVisibility(View.INVISIBLE);
		holder.image08.setVisibility(View.INVISIBLE);
		holder.image09.setVisibility(View.INVISIBLE);
		holder.justOne.setBackgroundResource(R.drawable.imageplaceholder);
		holder.image01.setBackgroundResource(R.drawable.imageplaceholder);
		holder.image02.setBackgroundResource(R.drawable.imageplaceholder);
		holder.image03.setBackgroundResource(R.drawable.imageplaceholder);
		holder.image04.setBackgroundResource(R.drawable.imageplaceholder);
		holder.image05.setBackgroundResource(R.drawable.imageplaceholder);
		holder.image06.setBackgroundResource(R.drawable.imageplaceholder);
		holder.image07.setBackgroundResource(R.drawable.imageplaceholder);
		holder.image08.setBackgroundResource(R.drawable.imageplaceholder);
		holder.image09.setBackgroundResource(R.drawable.imageplaceholder);
		final String strTitletemp = map.get("title");
		holder.content.setText(map.get("content"));
		String mapAddtime = map.get("addtime");
		if (mapAddtime != null) {
			long foo = Long.parseLong(mapAddtime) * 1000;
			long tmp = foo - nowTime;
			if (foo > toYear) {
				if (tmp < 12 * 60 * 60 * 1000) {
					if (tmp < 60 * 60 * 1000) {
						if (tmp <= 60 * 1000) {
							holder.shareTime.setText("1"
									+ getContext().getResources().getString(
											R.string.minute_befor));
						} else {
							holder.shareTime.setText(tmp
									/ (60 * 1000)
									+ getContext().getResources().getString(
											R.string.minute_befor));
						}

					} else {
						holder.shareTime.setText(tmp
								/ (60 * 60 * 1000)
								+ getContext().getResources().getString(
										R.string.hour_befor));
					}
				} else {
					holder.shareTime.setText(toYearSdf.format(new Date(foo)));
				}
			} else {
				holder.shareTime.setText(spl.format(new Date(foo)));
			}
		}
		holder.content.setMaxLines(300);
		holder.content.setEllipsize(TruncateAt.END);

/*		Log.i("lines", "cont:" + holder.content.getLineCount());
		Log.i("lines", "cont:" + holder.content.getText());
		if (Length(map.get("content")) <= 100) {
			holder.share_more.setVisibility(View.GONE);
			holder.viewline.setVisibility(View.GONE);

		} else {

			holder.share_more.setText(getContext().getResources().getString(
					R.string.more));
			holder.share_more.setVisibility(View.VISIBLE);
			holder.viewline.setVisibility(View.VISIBLE);
		}*/
		if ("1".equals(map.get("isconfirm"))
				&& "0".equals(map.get("haveisconfirm"))) {
			holder.btnconfirm.setVisibility(View.VISIBLE);
			holder.title.setTextColor(Color.parseColor("#FFFF0000"));
			holder.title.setText(getContext().getResources().getString(
					R.string.receipt_required)
					+ strTitletemp + "");
		} else {
			holder.btnconfirm.setVisibility(View.GONE);
			holder.title.setTextColor(Color.parseColor("#FF000000"));
			holder.title.setText(strTitletemp);
		}
		holder.share_more.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if ("1".equals(v.getTag())) {
					holder.share_more.setText(getContext().getResources()
							.getString(R.string.more));
					holder.content.setMaxLines(3);
					holder.content.setEllipsize(TruncateAt.END);
					v.setTag("0");
				} else {
					holder.share_more.setText(getContext().getResources()
							.getString(R.string.pack_up));
					holder.content.setMaxLines(300);
					holder.content.setEllipsize(null);
					v.setTag("1");
				}
			}
		});
		holder.btnconfirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				textView = holder.title;
				button = holder.btnconfirm;
				strTitle = strTitletemp;
				strNoticekey = map.get("noticekey");
				confirmReceiptAll(button, index);
			}
		});
		final String foo = map.get("plist");
		if (foo.equals("")) {

		} else if (foo.indexOf(",") == -1) {
			RelativeLayout.LayoutParams tv_params = (LayoutParams) holder.viewline
					.getLayoutParams();
			tv_params.addRule(RelativeLayout.BELOW, R.id.just_one);
			holder.viewline.setLayoutParams(tv_params);
			holder.justOne.setImageBackgroundDrawable(foo + ImageUtil.SMALL);
			holder.justOne.setVisibility(View.VISIBLE);
			holder.justOne.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(getContext(),
							BigPictureNoticeActivity.class);
					intent.putExtra("image", new String[] { foo });
					intent.putExtra("position", 0);
					intent.putExtra("title", map.get("title"));
					intent.putExtra("content", map.get("content"));
					ActivityUtil.startActivity(ActivityUtil.notice, intent);
				}
			});
		} else {
			final String[] tmp = foo.split(",");
			if (tmp.length > 0 && tmp.length <= 3) {
				RelativeLayout.LayoutParams tv_params = (LayoutParams) holder.viewline
						.getLayoutParams();
				tv_params.addRule(RelativeLayout.BELOW, R.id.l_line_one);
				holder.viewline.setLayoutParams(tv_params);
			} else if (tmp.length > 3 && tmp.length <= 6) {
				RelativeLayout.LayoutParams tv_params = (LayoutParams) holder.viewline
						.getLayoutParams();
				tv_params.addRule(RelativeLayout.BELOW, R.id.l_line_two);
				holder.viewline.setLayoutParams(tv_params);
			} else if (tmp.length > 6 && tmp.length <= 9) {
				RelativeLayout.LayoutParams tv_params = (LayoutParams) holder.viewline
						.getLayoutParams();
				tv_params.addRule(RelativeLayout.BELOW, R.id.l_line_three);
				holder.viewline.setLayoutParams(tv_params);
			}
			for (int i = 0; i < tmp.length; i++) {
				switch (i) {
				case 0:
					holder.l_line_one.setVisibility(View.VISIBLE);
					holder.image01.setImageBackgroundDrawable(tmp[i]
							+ ImageUtil.TINY);
					holder.image01.setVisibility(View.VISIBLE);
					holder.image01.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							Intent intent = new Intent(getContext(),
									BigPictureNoticeActivity.class);
							intent.putExtra("image", tmp);
							intent.putExtra("position", 0);
							intent.putExtra("title", map.get("title"));
							intent.putExtra("content", map.get("content"));
							ActivityUtil.startActivity(ActivityUtil.notice,
									intent);
						}
					});
					break;
				case 1:
					holder.image02.setImageBackgroundDrawable(tmp[i]
							+ ImageUtil.TINY);
					holder.image02.setVisibility(View.VISIBLE);
					holder.image02.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							Intent intent = new Intent(getContext(),
									BigPictureNoticeActivity.class);
							intent.putExtra("image", tmp);
							intent.putExtra("position", 1);
							intent.putExtra("title", map.get("title"));
							intent.putExtra("content", map.get("content"));
							ActivityUtil.startActivity(ActivityUtil.notice,
									intent);
						}
					});
					break;
				case 2:
					holder.image03.setImageBackgroundDrawable(tmp[i]
							+ ImageUtil.TINY);
					holder.image03.setVisibility(View.VISIBLE);
					holder.image03.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							Intent intent = new Intent(getContext(),
									BigPictureNoticeActivity.class);
							intent.putExtra("image", tmp);
							intent.putExtra("position", 2);
							intent.putExtra("title", map.get("title"));
							intent.putExtra("content", map.get("content"));
							ActivityUtil.startActivity(ActivityUtil.notice,
									intent);
						}
					});
					break;
				case 3:
					holder.l_line_two.setVisibility(View.VISIBLE);
					holder.image04.setImageBackgroundDrawable(tmp[i]
							+ ImageUtil.TINY);
					holder.image04.setVisibility(View.VISIBLE);
					holder.image04.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							Intent intent = new Intent(getContext(),
									BigPictureNoticeActivity.class);
							intent.putExtra("image", tmp);
							intent.putExtra("position", 3);
							intent.putExtra("title", map.get("title"));
							intent.putExtra("content", map.get("content"));
							ActivityUtil.startActivity(ActivityUtil.notice,
									intent);
						}
					});
					break;
				case 4:
					holder.image05.setImageBackgroundDrawable(tmp[i]
							+ ImageUtil.TINY);
					holder.image05.setVisibility(View.VISIBLE);
					holder.image05.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							Intent intent = new Intent(getContext(),
									BigPictureNoticeActivity.class);
							intent.putExtra("image", tmp);
							intent.putExtra("position", 4);
							intent.putExtra("title", map.get("title"));
							intent.putExtra("content", map.get("content"));
							ActivityUtil.startActivity(ActivityUtil.notice,
									intent);
						}
					});
					break;
				case 5:
					holder.image06.setImageBackgroundDrawable(tmp[i]
							+ ImageUtil.TINY);
					holder.image06.setVisibility(View.VISIBLE);
					holder.image06.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							Intent intent = new Intent(getContext(),
									BigPictureNoticeActivity.class);
							intent.putExtra("image", tmp);
							intent.putExtra("position", 5);
							intent.putExtra("title", map.get("title"));
							intent.putExtra("content", map.get("content"));
							ActivityUtil.startActivity(ActivityUtil.notice,
									intent);
						}
					});
					break;
				case 6:
					holder.l_line_three.setVisibility(View.VISIBLE);
					holder.image07.setImageBackgroundDrawable(tmp[i]
							+ ImageUtil.TINY);
					holder.image07.setVisibility(View.VISIBLE);
					holder.image07.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							Intent intent = new Intent(getContext(),
									BigPictureNoticeActivity.class);
							intent.putExtra("image", tmp);
							intent.putExtra("position", 6);
							intent.putExtra("title", map.get("title"));
							intent.putExtra("content", map.get("content"));
							ActivityUtil.startActivity(ActivityUtil.notice,
									intent);
						}
					});
					break;
				case 7:
					holder.image08.setImageBackgroundDrawable(tmp[i]
							+ ImageUtil.TINY);
					holder.image08.setVisibility(View.VISIBLE);
					holder.image08.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							Intent intent = new Intent(getContext(),
									BigPictureNoticeActivity.class);
							intent.putExtra("image", tmp);
							intent.putExtra("position", 7);
							intent.putExtra("title", map.get("title"));
							intent.putExtra("content", map.get("content"));
							ActivityUtil.startActivity(ActivityUtil.notice,
									intent);
						}
					});
					break;
				case 8:
					holder.image09.setImageBackgroundDrawable(tmp[i]
							+ ImageUtil.TINY);
					holder.image09.setVisibility(View.VISIBLE);
					holder.image09.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							Intent intent = new Intent(getContext(),
									BigPictureNoticeActivity.class);
							intent.putExtra("image", tmp);
							intent.putExtra("position", 8);
							intent.putExtra("title", map.get("title"));
							intent.putExtra("content", map.get("content"));
							ActivityUtil.startActivity(ActivityUtil.notice,
									intent);
						}
					});
					break;
				}
			}
		}
		TextViewTask textViewTask = new TextViewTask();
		textViewTask.execute(holder.content,holder.share_more,holder.viewline);
		return convertView;
	}


	static class ViewHolder {
		TextView title;
		TextView content;
		TextView shareTime;
		TextView share_more;
		View viewline;
		LinearLayout l_line_one;
		LinearLayout l_line_two;
		LinearLayout l_line_three;
		ShareImage justOne;
		ShareImage image01;
		ShareImage image02;
		ShareImage image03;
		ShareImage image04;
		ShareImage image05;
		ShareImage image06;
		ShareImage image07;
		ShareImage image08;
		ShareImage image09;
		Button btnconfirm;
	}

	public ArrayList<Map<String, String>> getList() {
		return list;
	}

	private void confirmReceiptAll(final Button button, final int index) {
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("noticekey", strNoticekey);
				Params param = new Params("notice", map);
				Result result = HttpUtil.httpPost(getContext(), param);
				if (result == null) {
					handler.sendEmptyMessage(NETISNOTWORKING);
				} else if ("1".equals(result.getCode())) {
					Map<String, String> map_ = getList().get(index);
					map_.put("haveisconfirm", "1");
					DB db = new DB(getContext());
					SQLiteDatabase sql = db.getWritableDatabase();
					ContentValues values = new ContentValues();
					values.put("haveisconfirm", "1");
					sql.update("notice", values, "u_id=? and noticekey=?",
							new String[] { Student_Info.uid, strNoticekey });
					sql.close();
					db.close();
					handler.sendEmptyMessage(CONFIRMRECEIPTSUCCESS);
				} else {
					handler.sendEmptyMessage(CONFIRMRECEIPTFAIL);
				}
			}
		});
		thread.start();
	}

	private int Length(String tmp) {
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
		return count;
	}

	public void setList(ArrayList<Map<String, String>> list) {
		this.list = list;
	}

	private class TextViewTask extends AsyncTask<Object, Integer,  Object[]> {
		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected Object[] doInBackground(Object... params) {
			/*TextView textView = (TextView)params[0];
			TextView more = (TextView)params[1];
			View line = (View)params[2];
			if (textView.getLineCount()<=3) {
				more.setVisibility(View.GONE);
				line.setVisibility(View.GONE);
				textView.setMaxLines(3);
			} else {
				more.setText(getContext().getResources().getString(
						R.string.more));
				more.setVisibility(View.VISIBLE);
				textView.setMaxLines(300);
				textView.setVisibility(View.VISIBLE);
			}*/
			return params;
		}

		@Override
	    protected void onPostExecute(Object[] params) {
	        //super.onPostExecute(result);
			TextView textView = (TextView)params[0];
			TextView more = (TextView)params[1];
			View line = (View)params[2];
			if (textView.getLineCount()<=3) {
				more.setVisibility(View.GONE);
				line.setVisibility(View.GONE);
				textView.setMaxLines(3);
			} else {
				more.setText(getContext().getResources().getString(
						R.string.more));
				more.setVisibility(View.VISIBLE);
				textView.setMaxLines(3);
				line.setVisibility(View.VISIBLE);
			}
	    }


	}
}
