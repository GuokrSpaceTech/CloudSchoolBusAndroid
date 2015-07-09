package com.android.support.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.android.support.handlerui.HandlerToastUI;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Contacts.People;
import android.provider.ContactsContract;
import android.widget.ListView;

/**
 * 在app中跳转到别的应用，或者系统界面
 * 
 * @author hongfeijia
 * 
 */
public class AppJump {
	/**
	 * 跳转到市场评论
	 */
	public static void marketComment(Context context) {
		try {
			Uri marketUri = Uri
					.parse("market://details?id=com.sohu.auto.helper&feature=ic_back_to_top-free");
			Intent viewIntent = new Intent();
			viewIntent.setData(marketUri);
			context.startActivity(viewIntent);
		} catch (Exception e) {
			Uri uri = Uri
					.parse("https://play.google.com/store/apps/details?id=com.sohu.auto.helper");
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			context.startActivity(intent);
		}
	}

	/**
	 * 调用浏览器
	 * 
	 * @param context
	 */
	public static void goBrowser(Context context, String urlString) {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		Uri content_url = Uri.parse(urlString);
		intent.setData(content_url);
		context.startActivity(intent);
	}

	/**
	 * 跳转到通讯录 if (resultCode == RESULT_OK) { if (data == null) { return; } Uri
	 * result = data.getData(); contactId = result.getLastPathSegment();
	 * contactName = getPhoneContacts(contactId); }
	 * 
	 * @param context
	 * @param requestContact
	 */
	public static void goContacts(Activity context, int requestContact) {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_PICK);
		intent.setData(ContactsContract.Contacts.CONTENT_URI);
		context.startActivityForResult(intent, requestContact);
	}

	public static Map<String, List<String>> contactsResult(Activity context,
			Intent data) {
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		List<String> phoneList = new ArrayList<String>();
		// ContentProvider展示数据类似一个单个数据库表
		// ContentResolver实例带的方法可实现找到指定的ContentProvider并获取到ContentProvider的数据
		ContentResolver reContentResolverol = context.getContentResolver();
		// URI,每个ContentProvider定义一个唯一的公开的URI,用于指定到它的数据集
		Uri contactData = data.getData();
		// 查询就是输入URI等参数,其中URI是必须的,其他是可选的,如果系统能找到URI对应的ContentProvider将返回一个Cursor对象.
		Cursor cursor = null;
		cursor = context.managedQuery(contactData, null, null, null, null);

		cursor.moveToFirst();
		// 获得DATA表中的名字
		String username = cursor.getString(cursor
				.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
		map.put(username, phoneList);
		// 条件为联系人ID
		String contactId = cursor.getString(cursor
				.getColumnIndex(ContactsContract.Contacts._ID));
		// 获得DATA表中的电话号码，条件为联系人ID,因为手机号码可能会有多个
		Cursor phone = reContentResolverol.query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
				ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = "
						+ contactId, null, null);
		while (phone.moveToNext()) {
			String usernumber = phone
					.getString(phone
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
			phoneList.add(usernumber);
		}
		return map;
	}

	/**
	 * 跳转到发送邮件
	 * 
	 * @param context
	 * @param emailString
	 * @param titleString
	 * @param contentString
	 */
	public static void goSendMail(Context context, String emailString,
			String titleString, String contentString) {
		try {
			// 系统邮件系统的动作为android.content.Intent.ACTION_SEND 
			Intent email = new Intent(android.content.Intent.ACTION_SEND);
			email.setType("plain/text");
			// 设置邮件默认地址 
			email.putExtra(android.content.Intent.EXTRA_EMAIL,
					new String[] { emailString });
			// 设置邮件默认标题 
			email.putExtra(android.content.Intent.EXTRA_SUBJECT, titleString);
			// 设置要默认发送的内容 
			email.putExtra(android.content.Intent.EXTRA_TEXT, contentString);
			// 调用系统的邮件系统 
			context.startActivity(Intent.createChooser(email, "请选择邮件发送软件"));
		} catch (ActivityNotFoundException e) {
			e.printStackTrace();
			HandlerToastUI.getHandlerToastUI(context, "没有找到发送邮件的应用");
		}
	}
}
