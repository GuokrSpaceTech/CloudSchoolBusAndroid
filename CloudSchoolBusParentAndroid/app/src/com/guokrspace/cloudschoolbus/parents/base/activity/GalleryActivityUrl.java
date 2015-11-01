package com.guokrspace.cloudschoolbus.parents.base.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.support.handlerui.HandlerToastUI;
import com.android.support.utils.ImageUtil;
import com.guokrspace.cloudschoolbus.parents.R;
import com.guokrspace.cloudschoolbus.parents.base.include.Version;
import com.guokrspace.cloudschoolbus.parents.database.daodb.MessageEntity;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXImageObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

import com.tencent.mm.sdk.openapi.WXAPIFactory;


import org.w3c.dom.Text;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

//import cn.sharesdk.framework.Platform;
//import cn.sharesdk.framework.ShareSDK;
//import cn.sharesdk.onekeyshare.OnekeyShare;
import ru.truba.touchgallery.GalleryWidget.BasePagerAdapter.OnItemChangeListener;
import ru.truba.touchgallery.GalleryWidget.GalleryViewPager;
import ru.truba.touchgallery.GalleryWidget.UrlPagerAdapter;

/**
 * Created by Yang Kai on 15/9/5.
 */
public class GalleryActivityUrl extends BaseActivity implements IWXAPIEventHandler {
    private GalleryViewPager mViewPager;
    private UrlPagerAdapter pagerAdapter;
    private TextView descTextView;
    private TextView countTextView;

    private List<String> items;
    private int position;
    private String descritption;
    private String title;

    private IWXAPI api;

//    private ImageView mDownloadImageView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
         * Get Arguments
         */
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null) {
            items = bundle.getStringArrayList("fileUrls");
            position = bundle.getInt("currentFile");
            descritption = bundle.getString("description");
            title = bundle.getString("title");
        }

        /*
         * UI
         */
        setContentView(R.layout.activity_gallery);
        descTextView = (TextView)findViewById(R.id.descTextView);
        countTextView = (TextView)findViewById(R.id.countTextView);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle(title);
        descTextView.setText(descritption);
        countTextView.setText(String.valueOf(position) + "/" + String.valueOf(items.size()));
        /*
         * Set the Adapter
         */
        pagerAdapter = new ru.truba.touchgallery.GalleryWidget.UrlPagerAdapter(this, items);
        final List<String> finalItems = items;
        pagerAdapter.setOnItemChangeListener(new OnItemChangeListener()
        {
            @Override
            public void onItemChange(int currentPosition)
            {
                position = currentPosition;
                countTextView.setText(String.valueOf(position+1) + "/" + String.valueOf(items.size()));
            }
        });

        mViewPager = (GalleryViewPager)findViewById(R.id.view_pager);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setCurrentItem(position);

        api = WXAPIFactory.createWXAPI(this, Version.APP_ID);
        api.registerApp(Version.APP_ID);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.wechat_friend:
                wxsharefriend(items.get(position));
                break;
            case R.id.wechat_moments:
                break;
            case R.id.picture_save:
                Bitmap bitmap = pagerAdapter.currUrlTouchImageView.getmBmp();
                if(bitmap!=null) {
                    ImageUtil.insertImage(getContentResolver(), bitmap, title, descritption);
                    HandlerToastUI.getHandlerToastUI(mContext, "图片已经保存到相册");
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_gallery_activity, menu);

        MenuItem submenu = menu.findItem(R.id.picture_share);

        getMenuInflater().inflate(R.menu.menu_share_platform, submenu.getSubMenu());

        return true;
    }

    private String title(int current, int total)
    {
        return getResources().getString(R.string.picturetype)+"("+ current + "/" + total +")";
    }

//    public void showShare(String url) {
//        ShareSDK.initSDK(mContext);
//        OnekeyShare oks = new OnekeyShare();
//        //关闭sso授权
//        oks.disableSSOWhenAuthorize();
//
//        oks.setText("");
//        oks.setImageUrl(url);
//        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
////        oks.setTitle("");
//////         titleUrl是标题的网络链接，仅在人人网和QQ空间使用
////        oks.setTitleUrl("http://sharesdk.cn");
////        // text是分享文本，所有平台都需要这个字段
////        oks.setText("");
////        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
////        oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
////        // url仅在微信（包括好友和朋友圈）中使用
////        oks.setUrl(url);
////        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
////        oks.setComment("我是测试评论文本");
//////        // site是分享此内容的网站名称，仅在QQ空间使用
////        oks.setSite(getString(R.string.app_name));
//////        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
////        oks.setSiteUrl("http://sharesdk.cn");
//
//// 启动分享GUI
//        oks.show(mContext);
//    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_share_platform, popup.getMenu());
        popup.show();
    }

    private void wxsharefriend(String url)
    {
        try{
            WXImageObject imgObj = new WXImageObject();
            imgObj.imageUrl = url;

            WXMediaMessage msg = new WXMediaMessage();
            msg.mediaObject = imgObj;

            Bitmap bmp = BitmapFactory.decodeStream(new URL(url).openStream());
            Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 150, 150, true);
            bmp.recycle();
            msg.thumbData = ImageUtil.bmpToByteArray(thumbBmp, true);

            SendMessageToWX.Req req = new SendMessageToWX.Req();
            req.transaction = buildTransaction("img");
            req.message = msg;
            req.scene = SendMessageToWX.Req.WXSceneSession;
            api.sendReq(req);

//            finish();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {

    }
}
