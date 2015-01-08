package com.cytx.adapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.Manga.Activity.R;
import com.cytx.AssessActivity;
import com.cytx.CYTXApplication;
import com.cytx.DoctorInfoActivity;
import com.cytx.ShowPictureActivity;
import com.cytx.constants.Constants;
import com.cytx.constants.HandlerConstants;
import com.cytx.domain.QuestionDetailContentDomain;
import com.cytx.domain.QuestionDetailContentItemDomain;
import com.cytx.domain.QuestionDetailDoctorDomain;
import com.cytx.domain.QuestionDetailDomain;
import com.cytx.utility.DateTools;
import com.cytx.utility.FastJsonTools;
import com.cytx.utility.JsonHelp;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 问题详情adapter
 * 
 * @author xilehang
 * 
 */
public class DetailAdapter extends BaseAdapter {

	private Context context;
	private Handler handler;
	private QuestionDetailDomain questionDetailDomain;
	private List<QuestionDetailContentDomain> contents;
	private int screenType;
	private String user_id;
	private long problem_id;
	private String status;
	private String currentAudioPath = "";// 当前播放的音频文件路径

	public String getCurrentAudioPath() {
		return currentAudioPath;
	}

	public void setCurrentAudioPath(String currentAudioPath) {
		this.currentAudioPath = currentAudioPath;
	}

	public QuestionDetailDomain getQuestionDetailDomain() {
		return questionDetailDomain;
	}

	public void setQuestionDetailDomain(
			QuestionDetailDomain questionDetailDomain) {
		this.questionDetailDomain = questionDetailDomain;
		contents = this.questionDetailDomain.getContent();
		sortByTime();
		regroup();

	}

	public DetailAdapter(Handler handler, Context context,
			QuestionDetailDomain questionDetailDomain, int screenType,
			String user_id, long problem_id, String status) {
		this.context = context;
		this.questionDetailDomain = questionDetailDomain;
		this.screenType = screenType;
		this.user_id = user_id;
		this.problem_id = problem_id;
		this.status = status;
		this.handler = handler;
		contents = this.questionDetailDomain.getContent();
		sortByTime();
		regroup();
	}

	// 根据时间排序
	private void sortByTime() {
		if (!contents.isEmpty()) {

			Collections.sort(contents,
					new Comparator<QuestionDetailContentDomain>() {

						@Override
						public int compare(QuestionDetailContentDomain object1,

						QuestionDetailContentDomain object2) {

							return (object1.getCreated_time_ms() + "")
									.compareTo(object2.getCreated_time_ms()
											+ "");

						}

					});
		}
	}

	// 重组contents数组，注：此方法可以优化
	private void regroup() {
		List<QuestionDetailContentDomain> lists = new ArrayList<QuestionDetailContentDomain>();
		for (int i = 0; i < contents.size(); i++) {
			QuestionDetailContentDomain questionDetailContentDomain = contents
					.get(i);
			String jsonString = questionDetailContentDomain.getContent();
			List<QuestionDetailContentItemDomain> contentItem = FastJsonTools
					.getListObject(jsonString,
							QuestionDetailContentItemDomain.class);
			// 有内容时
			if (contentItem != null && contentItem.size() != 0) {

				for (int j = 0; j < contentItem.size(); j++) {
					QuestionDetailContentDomain questionDetailContentDomain2 = new QuestionDetailContentDomain();
					questionDetailContentDomain2
							.setId(questionDetailContentDomain.getId());
					questionDetailContentDomain2
							.setCreated_time_ms(questionDetailContentDomain
									.getCreated_time_ms());
					questionDetailContentDomain2
							.setType(questionDetailContentDomain.getType());
					List<Object> twoList = new ArrayList<Object>();
					twoList.add(contentItem.get(j));
					questionDetailContentDomain2.setContent(JsonHelp
							.jsonObjectToString(twoList));
					lists.add(questionDetailContentDomain2);

				}

			}

		}

		contents.clear();
		contents.addAll(lists);
		// System.out.println("contents=" +
		// JsonHelp.jsonObjectToString(contents));
	}

	@Override
	public int getCount() {
		if (contents == null) {
			return 0;
		}
		return contents.size() + 1;
	}

	@Override
	public Object getItem(int arg0) {
		return arg0;
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@SuppressWarnings("unused")
	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		ViewHolder holder = null;
		if (arg1 == null) {
			holder = new ViewHolder();
			if (screenType == CYTXApplication.getInstance().SCREEN_480) {
				arg1 = LayoutInflater.from(context).inflate(
						R.layout.list_detail_item_480, arg2, false);
			} else {
				arg1 = LayoutInflater.from(context).inflate(
						R.layout.list_detail_item, arg2, false);
			}

			holder.contentRelativeLayout = (RelativeLayout) arg1
					.findViewById(R.id.relativeLayout_detail_item);
			holder.assessRelativeLayout = (RelativeLayout) arg1
					.findViewById(R.id.relativeLayout_detail_assess);
			holder.assessLineLayout = (LinearLayout) arg1
					.findViewById(R.id.linearLayout_detail_assess);

			// 日期
			holder.dateTextView = (TextView) arg1
					.findViewById(R.id.textView_date);
			// 病人追问问题
			holder.patientLinearLayout = (LinearLayout) arg1
					.findViewById(R.id.linearLayout_patient);
			holder.patientTextView = (TextView) arg1
					.findViewById(R.id.textView_patient_content);
			holder.patientImageView = (ImageView) arg1
					.findViewById(R.id.imageView_patient_content_image);
			holder.patientTextLinearLayout = (LinearLayout) arg1
					.findViewById(R.id.linearLayout_patient_content_text);
			// 医生解答问题
			holder.doctorRelativeLayout = (RelativeLayout) arg1
					.findViewById(R.id.relativeLayout_doctor);
			holder.doctorContentLinearLayout = (LinearLayout) arg1
					.findViewById(R.id.linearLayout_doctor_content);
			holder.doctorTextView = (TextView) arg1
					.findViewById(R.id.textView_doctor_content);
			holder.doctorImageView = (ImageView) arg1
					.findViewById(R.id.imageView_doctor_content);
			holder.doctorContentTextLinearLayout = (LinearLayout) arg1
					.findViewById(R.id.linearLayout_doctor_content_text);
			holder.doctorContentAudioLinearLayout = (RelativeLayout) arg1
					.findViewById(R.id.linearLayout_doctor_content_audio);
			holder.audioProgressBar = (ProgressBar) arg1
					.findViewById(R.id.progressBar_audio);
			holder.labaImageView = (ImageView) arg1
					.findViewById(R.id.imageView_doctor_laba);
			// 医生基本信息
			holder.doctorPortraitImageView = (ImageView) arg1
					.findViewById(R.id.imageView_doctor);
			holder.doctorNameTextView = (TextView) arg1
					.findViewById(R.id.TextView_doctor_name);
			holder.doctorJobTextView = (TextView) arg1
					.findViewById(R.id.TextView_doctor_job);
			arg1.setTag(holder);
		} else {
			holder = (ViewHolder) arg1.getTag();
		}

		if (arg0 < contents.size()) {
			holder.contentRelativeLayout.setVisibility(View.VISIBLE);
			holder.assessRelativeLayout.setVisibility(View.GONE);
			QuestionDetailContentDomain questionDetailContentDomain = contents
					.get(arg0);
			QuestionDetailDoctorDomain questionDetailDoctorDomain = questionDetailDomain
					.getDoctor();
			String type = questionDetailContentDomain.getType();

			// 解析json字符串，转化为问题内容对象
			String jsonString = questionDetailContentDomain.getContent();
			List<QuestionDetailContentItemDomain> contentItem = FastJsonTools
					.getListObject(jsonString,
							QuestionDetailContentItemDomain.class);

			if ("d".equalsIgnoreCase(type)) {
				// 显示医生回答问题界面
				holder.doctorRelativeLayout.setVisibility(View.VISIBLE);
				holder.patientLinearLayout.setVisibility(View.GONE);
				holder.doctorJobTextView.setText(questionDetailDoctorDomain
						.getTitle());
				holder.doctorNameTextView.setText(questionDetailDoctorDomain
						.getName());

				if (contentItem != null && contentItem.size() != 0) {
					// 动态加载医生答复内容界面，可能有多个
					for (int i = 0; i < contentItem.size(); i++) {
						final QuestionDetailContentItemDomain qdci = contentItem
								.get(i);
						if ("text".equals(qdci.getType())) {
							holder.doctorTextView.setText(qdci.getText());
							holder.doctorContentTextLinearLayout
									.setVisibility(View.VISIBLE);
							holder.doctorContentAudioLinearLayout
									.setVisibility(View.GONE);
							holder.doctorImageView.setVisibility(View.GONE);
						} else if ("image".equals(qdci.getType())) {
							ImageLoader.getInstance().displayImage(
									qdci.getFile(), holder.doctorImageView);
							holder.doctorImageView.setVisibility(View.VISIBLE);
							holder.doctorContentAudioLinearLayout
									.setVisibility(View.GONE);
							holder.doctorContentTextLinearLayout
									.setVisibility(View.GONE);
							// 点击查看大图
							holder.doctorImageView
									.setOnClickListener(new OnClickListener() {

										@Override
										public void onClick(View arg0) {

											Intent intent = new Intent(context,
													ShowPictureActivity.class);
											intent.putExtra("filePath",
													qdci.getFile());
											context.startActivity(intent);

										}
									});
						} else if ("audio".equals(qdci.getType())) {
							final String localFilePath = getFilePath(qdci
									.getFile());
							holder.doctorContentAudioLinearLayout
									.setVisibility(View.VISIBLE);
							holder.doctorContentTextLinearLayout
									.setVisibility(View.GONE);
							holder.doctorImageView.setVisibility(View.GONE);
							holder.doctorContentAudioLinearLayout
									.setOnClickListener(new OnClickListener() {

										@Override
										public void onClick(View v) {

											// 点击播放音频
											Message msg = handler
													.obtainMessage(
															HandlerConstants.AUDIO,
															localFilePath);
											handler.sendMessage(msg);
										}
									});
							// 判断音频是否在播放：currentAuPath==localFilePath
							AnimationDrawable mAnimationDrawable = null;
							if (currentAudioPath.equals(localFilePath)) {
								holder.labaImageView
										.setImageResource(R.drawable.laba);
								mAnimationDrawable = (AnimationDrawable) holder.labaImageView
										.getDrawable();
								mAnimationDrawable.start();
							} else {
								if (mAnimationDrawable != null
										&& mAnimationDrawable.isRunning()) {
									mAnimationDrawable.stop();
								}
								holder.labaImageView
										.setImageResource(R.drawable.audio_003);
							}

							// 如果本地已经缓存音频，那么不需要下载；否则下载音频文件
							File file = new File(localFilePath);
							if (file.exists() && file.isFile()
									&& file.length() > 0) {
								holder.audioProgressBar
										.setVisibility(View.GONE);
							} else {
								// 执行线程，下载音频文件
								new LoadingAudioTask(holder.audioProgressBar,
										qdci.getFile()).execute();
							}

						}

					}
				}
				// 加载网络图片：医生头像
				ImageLoader.getInstance().displayImage(
						questionDetailDoctorDomain.getImage(),
						holder.doctorPortraitImageView);
			} else if ("p".equalsIgnoreCase(type)) {
				// 显示客人提问界面
				holder.doctorRelativeLayout.setVisibility(View.GONE);
				holder.patientLinearLayout.setVisibility(View.VISIBLE);

				if (contentItem != null && contentItem.size() != 0) {
					// 动态加载医生答复内容界面，可能有多个
					for (int i = 0; i < contentItem.size(); i++) {
						final QuestionDetailContentItemDomain qdci = contentItem
								.get(i);
						if ("text".equals(qdci.getType())) {
							holder.patientTextView.setText(qdci.getText());
							holder.patientTextLinearLayout
									.setVisibility(View.VISIBLE);
							holder.patientImageView.setVisibility(View.GONE);
						} else if ("image".equals(qdci.getType())) {
							ImageLoader.getInstance().displayImage(
									qdci.getFile(), holder.patientImageView);
							holder.patientImageView.setVisibility(View.VISIBLE);
							holder.patientTextLinearLayout
									.setVisibility(View.GONE);
							holder.patientImageView
									.setOnClickListener(new OnClickListener() {

										@Override
										public void onClick(View arg0) {

											Intent intent = new Intent(context,
													ShowPictureActivity.class);
											intent.putExtra("filePath",
													qdci.getFile());
											context.startActivity(intent);

										}
									});
						}

					}
				}
			}
			holder.dateTextView.setText(DateTools.getDateForm(
					"yyyy-MM-dd HH:mm",
					questionDetailContentDomain.getCreated_time_ms()));

			final QuestionDetailDoctorDomain qddd = questionDetailDomain
					.getDoctor();
			// 点击医生头像进入医生基本信息界面
			holder.doctorPortraitImageView
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							Intent intent = new Intent(context,
									DoctorInfoActivity.class);
							intent.putExtra("doctor_id", qddd.getId());
							context.startActivity(intent);
						}
					});
		} else {
			holder.contentRelativeLayout.setVisibility(View.GONE);
			// 已答复、已评价、待评价
			if ("v".equals(status) || "s".equals(status) || "d".equals(status)
					|| "c".equals(status)) {
				holder.assessRelativeLayout.setVisibility(View.VISIBLE);
				// 已答复
				if ("v".equals(status) || "s".equals(status)) {
					holder.assessLineLayout.setVisibility(View.VISIBLE);
				}
				// 已评价、待评价
				else {
					holder.assessLineLayout.setVisibility(View.GONE);
				}
			} else {
				holder.assessRelativeLayout.setVisibility(View.GONE);
			}
			holder.assessLineLayout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(context, AssessActivity.class);
					intent.putExtra("user_id", user_id);
					intent.putExtra("problem_id", problem_id);
					intent.putExtra("status", status);
					context.startActivity(intent);
				}
			});
		}

		return arg1;
	}

	class ViewHolder {
		public TextView dateTextView;// 日期
		public LinearLayout patientLinearLayout;// 病人追问
		public TextView patientTextView;
		public ImageView patientImageView;
		public LinearLayout patientTextLinearLayout;

		public RelativeLayout doctorRelativeLayout;// 医生答复
		public LinearLayout doctorContentLinearLayout;// 医生答复的内容
		public TextView doctorTextView;// text
		public ImageView doctorImageView;// image
		public LinearLayout doctorContentTextLinearLayout;// textLinearyout
		public RelativeLayout doctorContentAudioLinearLayout;// audioLinearLayout
		public ProgressBar audioProgressBar;// 提示加载
		public ImageView labaImageView;// 小喇叭

		public ImageView doctorPortraitImageView;// 医生头像
		public TextView doctorNameTextView;// 医生姓名
		public TextView doctorJobTextView;// 医生职务

		public RelativeLayout contentRelativeLayout;
		public RelativeLayout assessRelativeLayout;
		public LinearLayout assessLineLayout;

	}

	// 下载音频文件
	public class LoadingAudioTask extends AsyncTask<Void, Void, String> {

		private String audioPath;
		private ProgressBar progressBar;

		public LoadingAudioTask(ProgressBar progressBar, String audioPath) {
			this.audioPath = audioPath;
			this.progressBar = progressBar;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressBar.setVisibility(View.VISIBLE);
		}

		@Override
		protected String doInBackground(Void... params) {
			String filePath = "";
			try {
				// 构造URL
				URL url = new URL(audioPath);
				// 打开连接
				URLConnection con = url.openConnection();
				// 输入流
				InputStream is = con.getInputStream();
				// 1K的数据缓冲
				byte[] bs = new byte[1024];
				// 读取到的数据长度
				int len;
				// 文件路劲
				filePath = getFilePath(audioPath);
				// 输出的文件流
				OutputStream os = new FileOutputStream(filePath);
				// 开始读取
				while ((len = is.read(bs)) != -1) {
					os.write(bs, 0, len);
				}
				// 完毕，关闭所有链接
				os.close();
				is.close();
			} catch (Exception e) {
				e.printStackTrace();
				return "";

			}
			return filePath;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			progressBar.setVisibility(View.GONE);
			if ("".equals(result)) {
				// 显示并提示音频加载失败
				Toast.makeText(
						context,
						context.getResources().getString(
								R.string.detail_audio_loading_failed),
						Toast.LENGTH_LONG).show();
			}
		}

	}

	/**
	 * 存储到本地的音频文件名称
	 * 
	 * @return
	 */
	private String getFilePath(String audioPath) {
		String filePath = "";
		if (audioPath == null || "".equals(audioPath)) {
			return filePath;
		}
		// 取追后“/”最后一段字符串作为fileName
		filePath = Constants.AUDIO_DIR
				+ audioPath.substring(audioPath.lastIndexOf("/"),
						audioPath.length());
		File fileDir = new File(Constants.AUDIO_DIR);
		if (!fileDir.exists()) {
			fileDir.mkdirs();
		}
		File file = new File(filePath);
		// 文件不存在，才创建
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				return "";
			}
		}

		return filePath;
	}

	/**
	 * 获取音频时长
	 * 
	 * @param mpFullname
	 * @return
	 */
	public int getAudioDuration(String mpFullname) {
		int duration = 0;
		try {
			String selection = MediaStore.Audio.Media.DATA + " = ?";
			String[] selectionArgs = { mpFullname };
			String[] projection = { MediaStore.Audio.Media.DURATION };

			Cursor cursor = null;
			cursor = context.getContentResolver().query(
					MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection,
					selection, selectionArgs, null);
			duration = cursor.getInt(cursor
					.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
		} catch (Exception e) {
			return 0;
		}
		return duration;
	}

}
