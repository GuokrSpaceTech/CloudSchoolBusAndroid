package com.Manga.Activity.ClassUpdate;

import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.Manga.Activity.BaseActivity;
import com.Manga.Activity.R;
import com.Manga.Activity.httputils.HttpUtil;
import com.Manga.Activity.httputils.Params;
import com.Manga.Activity.httputils.Result;
import com.Manga.Activity.utils.ActivityUtil;
import com.umeng.analytics.MobclickAgent;

public class ReplyActivity extends BaseActivity {
	private String articleid;
	private String commentid;
	private EditText content;
	private static final int SEND_COMMENT_FAIL=0;
	private Handler handler=new Handler(new Callback() {
		
		@Override
		public boolean handleMessage(Message mes) {
			// TODO Auto-generated method stub
			switch(mes.what){
			case SEND_COMMENT_FAIL:
				Toast.makeText(ReplyActivity.this, R.string.reply_fail, Toast.LENGTH_SHORT).show();
				break;
			}
			return false;
		}
	});
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reply);
		content=(EditText) findViewById(R.id.content);
		Intent intent=getIntent();
		articleid=intent.getStringExtra("articleid");
		commentid=intent.getStringExtra("commentid");
	}
	public void backMenu(View v){
		ActivityUtil.shareMain.initCommentList();
		ActivityUtil.close(this);
	}
	public void submit(View v){
		reComment();
	}
	private void reComment(){
		if("".equals(content.getText().toString().trim())){
			Toast.makeText(this, R.string.reply_cant_null, Toast.LENGTH_SHORT).show();
			return;
		}
		if(!checkLength(content.getText().toString())){
			Toast.makeText(this, R.string.lun_out, Toast.LENGTH_SHORT).show();
			return;
		}
		
		Thread thread=new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				HashMap<String, String> map=new HashMap<String, String>();
				map.put("itemid", articleid);
				map.put("itemtype", "article");
				map.put("content", content.getText().toString());
				map.put("reply", commentid);
				Params param=new Params("comment", map);
				Result result=HttpUtil.httpPost(ReplyActivity.this, param);
				if(result==null){
					
				}else if("1".equals(result.getCode())){
					ActivityUtil.shareMain.initCommentList();
					ActivityUtil.close(ReplyActivity.this);
				}else{
					handler.sendEmptyMessage(SEND_COMMENT_FAIL);
				}
			}	
		});
		thread.start();
	}
	private boolean checkLength(String tmp){
		int count=0;
		for(int i=0;i<tmp.length();i++){
			char c=tmp.charAt(i);
			if(c>=0&&c<=9){
				count++;
			}else if(c>='a'&&c<='z'){
				count++;
			}else if(c>='A'&&c<='Z'){
				count++;
			}else if(Character.isLetter(c)){
				count+=2;
			}else {
				count++;
			}
		}
		if(count>140){
			return false;
		}
		return true;
	}public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
