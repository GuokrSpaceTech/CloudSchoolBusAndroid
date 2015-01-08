package com.cytx;

import java.util.List;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.Manga.Activity.R;
import com.cytx.adapter.ConsultAdapter;
import com.cytx.constants.Constants;
import com.cytx.domain.QuestionHistoryDomain;
import com.cytx.dto.QuestionHistoryDto;
import com.cytx.freshlist.PullToRefreshBase;
import com.cytx.freshlist.PullToRefreshBase.OnRefreshListener;
import com.cytx.freshlist.PullToRefreshListView;
import com.cytx.service.WebService;
import com.cytx.service.impl.WebServiceImpl;
import com.cytx.utility.FastJsonTools;
import com.cytx.utility.FileTools;
import com.cytx.utility.LanguageHelp;
import com.cytx.utility.MD5;
import com.cytx.utility.SharePreferencTools;
import com.cytx.utility.UtilUI;
import com.loopj.android.http.AsyncHttpResponseHandler;

/**
 * 医生咨询界面
 * 
 * @author xilehang
 * 
 */
public class ConsultActivity extends BaseActivity {

	// private ListView questionListView;
	private PullToRefreshListView mPullRefreshListView;// 下拉刷新，更多
	private ListView actualListView;
	private Button backButton;
	private Button questionButton;
	private TextView tv_consult;
	// 问题历史记录列表
	private List<QuestionHistoryDomain> lists;
	private ConsultAdapter adapter;
	private RelativeLayout tipFirstRelativeLayout;// 第一次进入，提示界面
	private RelativeLayout titleRelativeLayout;// 顶部菜单栏
	private Button confirmButton;// 点击确定按钮（提示对话框消失）
	private ImageView tipImageView;

	private GestureDetector mGestureDetector;// 监听手势
	private CYTXApplication cytxApp = CYTXApplication.getInstance();

	private final int NEW_COUNT = 20;// 每页加载20项问题历史
	// private TextView foot;// 底部显示“更多”
	private LinearLayout loadingLinearLayout;// Loading……
	private TextView loadingOverTextView;// 已加载完毕
	private int currentPage;// 有多少页内容

	private int flag = -1;// 表示初始加载

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (cytxApp.getScreenType(screenWidth, screenHeight) == cytxApp.SCREEN_480) {
			setContentView(R.layout.activity_consult_480);
		} else {
			setContentView(R.layout.activity_consult);
		}

		tv_consult = (TextView) findViewById(R.id.tv_consult);
		backButton = (Button) findViewById(R.id.button_back);
		backButton.setOnClickListener(new ClickEventListener());
		questionButton = (Button) findViewById(R.id.button_question);
		questionButton.setOnClickListener(new ClickEventListener());
		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.listView_consult);
		actualListView = mPullRefreshListView.getRefreshableView();
		// 底部：更多按钮
		int id = 0;
		if (cytxApp.getScreenType(screenWidth, screenHeight) == cytxApp.SCREEN_480) {
			id = R.layout.consult_list_footer_480;
		} else {
			id = R.layout.consult_list_footer;
		}
		final View footer = getLayoutInflater().inflate(id, null);
		// Need to use the Actual ListView when registering for Context Menu
		registerForContextMenu(actualListView);

		actualListView.addFooterView(footer);
		loadingLinearLayout = (LinearLayout) findViewById(R.id.linearLayout_loading);
		loadingOverTextView = (TextView) findViewById(R.id.textView_loading_over);
		// foot = (TextView) footer.findViewById(R.id.load_more);
		/*
		 * foot.setOnClickListener(new View.OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { foot.setText(getResources().getString(R.string.loading));
		 * loadMoreData(); } });
		 */

		// 监听ListView滑动的位置：顶部或者底部
		actualListView.setOnScrollListener(new OnScrollListener() {
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
				// 当不滚动时
				case OnScrollListener.SCROLL_STATE_IDLE:
					// 判断滚动到底部
					if (actualListView.getLastVisiblePosition() == (actualListView.getCount() - 1)) {

						loadMoreData();

					}
					// 判断滚动到顶部
					if (actualListView.getFirstVisiblePosition() == 0) {
					}

					break;
				}
			}

			@Override
			public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			}
		});

		mPullRefreshListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (position == 0 || view == footer)
					return;
				QuestionHistoryDomain questionHistoryDomain = lists.get(position - 1);
				long problem_id = questionHistoryDomain.getProblem().getId();
				String status = questionHistoryDomain.getProblem().getStatus();
				// 进入问题详情界面
				Intent intent = new Intent(ConsultActivity.this, DetailActivity.class);
				intent.putExtra("problem_id", problem_id);
				intent.putExtra("user_id", user_id);
				intent.putExtra("status", status);
				startActivity(intent);
				finish();
			}
		});
		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				String label = DateUtils.formatDateTime(ConsultActivity.this, System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
				initDatas();
			}
		});

		/*
		 * questionListView = (ListView) findViewById(R.id.listView_consult);
		 * questionListView.setOnItemClickListener(new OnItemClickListener() {
		 * 
		 * @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		 * QuestionHistoryDomain questionHistoryDomain = lists.get(position); long problem_id =
		 * questionHistoryDomain.getProblem().getId(); String status = questionHistoryDomain.getProblem().getStatus();
		 * // 进入问题详情界面 Intent intent = new Intent(ConsultActivity.this, DetailActivity.class);
		 * intent.putExtra("problem_id", problem_id); intent.putExtra("user_id", user_id); intent.putExtra("status",
		 * status); startActivity(intent); finish(); } });
		 */
		// 手势
		mGestureDetector = new GestureDetector(this, new MyGestureListener());
		mPullRefreshListView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				return mGestureDetector.onTouchEvent(arg1);
			}
		});
		/*
		 * questionListView.setOnTouchListener(new OnTouchListener() {
		 * 
		 * @Override public boolean onTouch(View arg0, MotionEvent arg1) { return mGestureDetector.onTouchEvent(arg1); }
		 * });
		 */

		tipImageView = (ImageView) findViewById(R.id.imageView_100);
		if (!LanguageHelp.isZh(ConsultActivity.this)) {
			System.out.println("english");
			if (cytxApp.getScreenType(screenWidth, screenHeight) == cytxApp.SCREEN_480) {
				tipImageView.setImageResource(R.drawable.consult_tip_first_en_480);
			} else {
				tipImageView.setImageResource(R.drawable.consult_tip_first_en);
			}

		}
		tipFirstRelativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout_consult_first);
		titleRelativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout_cosult_title);
		confirmButton = (Button) findViewById(R.id.button_consult_confirm);
		confirmButton.setOnClickListener(new ClickEventListener());

		// 开始加载数据
		mPullRefreshListView.setRefreshing();
	}

	/**
	 * 加载更多
	 */
	private void loadMoreData() {
		currentPage++;
		// 将时间戳从13位转变为10位
		String atime = (System.currentTimeMillis() / 1000) + "";
		String sign = "";
		sign = MD5.md5(MD5.getString(atime, user_id));
		int start_num = NEW_COUNT * currentPage;

		QuestionHistoryDto qhd = new QuestionHistoryDto();
		qhd.setUser_id(user_id);
		qhd.setAtime(atime);
		qhd.setCount(NEW_COUNT);
		qhd.setStart_num(start_num);
		qhd.setSign(sign);

		WebService webService = WebServiceImpl.getInstance();
		webService.questionHistory(qhd, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(String arg0) {
				super.onSuccess(arg0);

				if (Constants.isDebug) {
					Log.d("ConsultActivity", arg0);
					FileTools.save2SDCard(FileTools.getSDcardPath() + "/questionList", "question", ".json", arg0);

				}
				// foot.setText(getResources().getString(R.string.more));
				mPullRefreshListView.onRefreshComplete();

				// 加载更多数据
				List<QuestionHistoryDomain> moreLists = FastJsonTools.getListObject(arg0, QuestionHistoryDomain.class);
				if (moreLists != null) {
					tv_consult.setVisibility(View.GONE);
					mPullRefreshListView.setVisibility(View.VISIBLE);

					lists.addAll(moreLists);
					// 如果加载的数据少于NEW_COUNT,说明数据已加载完毕
					if (moreLists.size() < NEW_COUNT) {
						loadingLinearLayout.setVisibility(View.GONE);
						loadingOverTextView.setText(R.string.data_over);
						loadingOverTextView.setVisibility(View.VISIBLE);
						Toast.makeText(getApplicationContext(), getResources().getString(R.string.data_over),
								Toast.LENGTH_LONG).show();

					}
				} else {
					loadingOverTextView.setVisibility(View.VISIBLE);
					loadingOverTextView.setText(R.string.data_empty);
				}

				if (adapter == null) {
					adapter = new ConsultAdapter(ConsultActivity.this, lists, cytxApp.getScreenType(screenWidth,
							screenHeight));
					// questionListView.setAdapter(adapter);
					mPullRefreshListView.setAdapter(adapter);
				} else {
					adapter.setLists(lists);
					adapter.notifyDataSetChanged();
				}

			}

			@Override
			public void onFailure(Throwable arg0, String arg1) {
				super.onFailure(arg0, arg1);
				if (Constants.isDebug) {
					Log.e("ConsultActivity", arg1);
				}
				// foot.setText(getResources().getString(R.string.more));
				mPullRefreshListView.onRefreshComplete();
				UtilUI.showToastError(arg1, getApplicationContext());
			}

			@Override
			public void onFinish() {
				super.onFinish();

			}

		});
	}

	private final int PIXEL = Constants.PIXEL_SCROLL;

	private class MyGestureListener extends SimpleOnGestureListener {

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			if (e1 == null || e2 == null) {
				return false;
			}
			// 表示向右滑动
			if (e1.getX() - e2.getX() < -PIXEL) {

				ConsultActivity.this.finish();

				return false;
			}
			return false;
		}
	}

	private String user_id = "";

	private void initDatas() {
		currentPage = 0;
		Intent intent = getIntent();
		if (intent != null) {
			user_id = intent.getStringExtra("user_id");
		}

		// 将时间戳从13位转变为10位
		String atime = (System.currentTimeMillis() / 1000) + "";
		String sign = "";
		sign = MD5.md5(MD5.getString(atime, user_id));
		int start_num = 0;

		QuestionHistoryDto qhd = new QuestionHistoryDto();
		qhd.setUser_id(user_id);
		qhd.setAtime(atime);
		qhd.setCount(NEW_COUNT);
		qhd.setStart_num(start_num);
		qhd.setSign(sign);

		WebService webService = WebServiceImpl.getInstance();
		webService.questionHistory(qhd, new AsyncHttpResponseHandler() {

			private ProgressDialog dialog = UtilUI.getProgressMessageDialog(ConsultActivity.this, ConsultActivity.this
					.getResources().getString(R.string.loading));

			@Override
			public void onStart() {
				if (flag == -1) {
					dialog.show();
				}

				super.onStart();
			}

			@Override
			public void onSuccess(String arg0) {
				super.onSuccess(arg0);

				if (Constants.isDebug) {
					Log.d("ConsultActivity", arg0);
					FileTools.save2SDCard(FileTools.getSDcardPath() + "/questionList", "question", ".json", arg0);

				}

				if (dialog != null && dialog.isShowing()) {
					flag = 0;
					dialog.dismiss();
				}

				mPullRefreshListView.onRefreshComplete();
				boolean isFirstConsult = SharePreferencTools.getBooleanFromShares(ConsultActivity.this,
						Constants.IS_FIRST_CONSULT, true);
				if (isFirstConsult) {
					// 首次进入程序会弹出提示对话框
					SharePreferencTools.setBooleanToShares(ConsultActivity.this, Constants.IS_FIRST_CONSULT, false);
					tipFirstRelativeLayout.setVisibility(View.VISIBLE);

					// 设置对话框下面的界面触碰无效
					// questionListView.setEnabled(false);
					mPullRefreshListView.setEnabled(false);
					titleRelativeLayout.setEnabled(false);
				}

				// 加载数据
				lists = FastJsonTools.getListObject(arg0, QuestionHistoryDomain.class);

				// 如果加载的数据少于NEW_COUNT,说明数据已加载完毕
				if (lists == null || lists.size() == 0) {
					loadingLinearLayout.setVisibility(View.GONE);
					loadingOverTextView.setText(R.string.data_empty);
					loadingOverTextView.setVisibility(View.VISIBLE);
				} else if (lists != null && lists.size() < NEW_COUNT) {
					loadingLinearLayout.setVisibility(View.GONE);
					loadingOverTextView.setText(R.string.data_over);
					loadingOverTextView.setVisibility(View.VISIBLE);
				} else {
					loadingLinearLayout.setVisibility(View.VISIBLE);
					loadingOverTextView.setVisibility(View.GONE);
				}

				if (adapter == null) {
					adapter = new ConsultAdapter(ConsultActivity.this, lists, cytxApp.getScreenType(screenWidth,
							screenHeight));
					// questionListView.setAdapter(adapter);
					mPullRefreshListView.setAdapter(adapter);
				} else {
					adapter.setLists(lists);
					adapter.notifyDataSetChanged();
				}

			}

			@Override
			public void onFailure(Throwable arg0, String arg1) {
				super.onFailure(arg0, arg1);
				if (Constants.isDebug) {
					Log.e("ConsultActivity", arg1);
				}

				if (dialog != null && dialog.isShowing()) {
					dialog.dismiss();
				}
				mPullRefreshListView.onRefreshComplete();
				UtilUI.showToastError(arg1, getApplicationContext());
			}

			@Override
			public void onFinish() {
				super.onFinish();

			}

		});
	}

	class ClickEventListener implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			switch (arg0.getId()) {
			case R.id.button_back:
				ConsultActivity.this.finish();
				break;
			case R.id.button_question:
				Intent intent = new Intent(ConsultActivity.this, QuestionActivity.class);
				intent.putExtra("user_id", user_id);
				startActivity(intent);
				finish();
				break;
			case R.id.button_consult_confirm:
				tipFirstRelativeLayout.setVisibility(View.GONE);
				// questionListView.setEnabled(true);
				mPullRefreshListView.setEnabled(true);
				titleRelativeLayout.setEnabled(true);
				break;
			}
		}

	}

}
