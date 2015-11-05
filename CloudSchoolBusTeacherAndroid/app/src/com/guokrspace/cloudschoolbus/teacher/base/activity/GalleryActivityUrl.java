package com.guokrspace.cloudschoolbus.teacher.base.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.support.handlerui.HandlerToastUI;
import com.android.support.utils.ImageUtil;
import com.guokrspace.cloudschoolbus.teacher.R;
import com.guokrspace.cloudschoolbus.teacher.base.include.Version;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.net.URL;
import java.util.List;

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
                wxsharefriend(items.get(position), descritption);
                break;
            case R.id.wechat_moments:
                wxshareMoments(items.get(position), descritption);
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

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_share_platform, popup.getMenu());
        popup.show();
    }

    private void wxsharefriend(final String url, final String descritption)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Bitmap bmp = BitmapFactory.decodeStream(new URL(url).openStream());
                    int scale = ImageUtil.reckonThumbnail(bmp.getWidth(), bmp.getHeight(), 80, 80);
                    Bitmap thumbBmp = ImageUtil.PicZoom(bmp, (int) (bmp.getWidth() / scale), (int) (bmp.getHeight() / scale));
                    bmp.recycle();

                    WXWebpageObject webObj = new WXWebpageObject();
                    webObj.webpageUrl = url;

                    WXMediaMessage msg = new WXMediaMessage();
                    msg.mediaObject = webObj;
                    msg.thumbData = ImageUtil.bmpToByteArray(thumbBmp, true);
                    msg.description = descritption;


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
        }).start();

    }

    private void wxshareMoments(final String url, final String descritption)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Bitmap bmp = BitmapFactory.decodeStream(new URL(url).openStream());
                    int scale = ImageUtil.reckonThumbnail(bmp.getWidth(), bmp.getHeight(), 80, 80);
                    Bitmap thumbBmp = ImageUtil.PicZoom(bmp, (int) (bmp.getWidth() / scale), (int) (bmp.getHeight() / scale));
                    bmp.recycle();

                    WXWebpageObject webObj = new WXWebpageObject();
                    webObj.webpageUrl = url;

                    WXMediaMessage msg = new WXMediaMessage();
                    msg.mediaObject = webObj;
                    msg.thumbData = ImageUtil.bmpToByteArray(thumbBmp, true);
                    msg.description = descritption;
                    msg.title = descritption;

                    SendMessageToWX.Req req = new SendMessageToWX.Req();
                    req.transaction = buildTransaction("img");
                    req.message = msg;
                    req.scene = SendMessageToWX.Req.WXSceneTimeline;
                    api.sendReq(req);

//            finish();
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    @Override
    public void onReq(BaseReq baseReq) {
        Log.i("","");
    }

    @Override
    public void onResp(BaseResp baseResp) {
        Log.i("","");
    }
}
