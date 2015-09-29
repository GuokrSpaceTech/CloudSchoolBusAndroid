package com.guokrspace.cloudschoolbus.parents.module.classes.Streaming;

import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;


import com.android.support.utils.xmlStringDocParsor;
import com.guokrspace.cloudschoolbus.parents.R;
import com.guokrspace.cloudschoolbus.parents.base.activity.BaseActivity;
import com.guokrspace.cloudschoolbus.parents.entity.Ipcparam;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.IOException;

public class Preview extends BaseActivity {
	
	private final String TAG = this.getClass().getSimpleName();
	private SocketClient client = null;
	private MjpegView image = null;
	private FrameLayout layout = null;
	private PowerManager.WakeLock wakeLock;
    private Ipcparam mIpcparam;
	
	static final String KEY_ITEM = ""; // parent node
	static final String KEY_LINK = "LinkReturn";
	static final String KEY_DVRTYPE = "DVRType";
	static final String KEY_DEVICE = "device";
	static final String KEY_SVRNAME = "svrname";
	static final String KEY_STATUS = "Status";

	/**
	 * 检查版本网络超时
	 */	
	
	public final static int POSITION_UPPER_LEFT = 9;
	public final static int POSITION_UPPER_RIGHT = 3;
	public final static int POSITION_LOWER_LEFT = 12;
	public final static int POSITION_LOWER_RIGHT = 6;

	public final static int SIZE_STANDARD = 1;
	public final static int SIZE_BEST_FIT = 4;
	public final static int SIZE_FULLSCREEN = 8;

	public SocketClient  source;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.viewer_layout);

		layout = (FrameLayout)findViewById(R.id.flayout);

		//Create the imageView that takes over main UI thread receiving the streaming data
		image = new MjpegView(this);
		image.setDisplayMode(MjpegView.SIZE_BEST_FIT);
		image.showFps(true);
		layout.addView(image, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		
		//Init the socket
		client = new SocketClient();

        //Set all the parameters
		Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mIpcparam = (Ipcparam)bundle.getSerializable("ipcparams");
        client.m_sStreamIP = mIpcparam.getServerip();
        client.m_iStreamPort = Integer.parseInt(mIpcparam.getPort());
        int i = intent.getIntExtra("id",0);
        client.m_sDVRName = mIpcparam.getChannels().get(i).getDevice();
        client.m_iChnNo = Integer.parseInt(mIpcparam.getChannels().get(i).getChannelid());

        getSupportActionBar().setTitle(mIpcparam.getChannels().get(i).getChanneldesc());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		//Init the streaming source for the imageView 
		image.source = client;

		//启用屏幕常亮
		wakeLock = ((PowerManager)getSystemService(POWER_SERVICE)).
				newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, TAG);
		
	    wakeLock.acquire();
	}

	@Override
	public void onResume()
	{
		
		//启用屏幕常亮
		wakeLock = ((PowerManager)getSystemService(POWER_SERVICE)).
				newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, TAG);
		
	    wakeLock.acquire();
		
		/*
		 * Use thread to connect the server
	     * Init socket and send message to init streaming
	     * 
	     */
	    connectSrv(); 			
		
		super.onResume();
	}
	
	@Override
	public void onPause() {
		image.stopPlayback();
		disconnect();
		super.onPause();
	}
	
	@Override
	public void onStop(){
		super.onStop();
		
		//Once the activity is in background, just finish
		if (wakeLock != null) {
		    wakeLock.release();
		}
		image.stopPlayback();
		finish();
	}
	
	private void disconnect(){
		if (client != null) {
			try {
				client.closeConnect();
				client = null;
			} catch (IOException e) {
				Log.e(TAG, e.getMessage());
			}
		}
	}
	
	/**
	 * Connecting Streaming server, prepare for the streaming
	 */
	private void connectSrv() {
		
		//Use thread to connect the server
    	new Thread() {
			public void run() {
				if (client != null) {
					try {
						client.openConnect();
						while (!client.IsOpen()) {
						}
						
						if(getDeviceStatus(client.m_sDVRName))
						{
							startStream();
						}
		
					} catch (Exception e) {
						//Log.e(TAG, e.getMessage());
						image.setStreamReady(-1);
					}
				}
			}
		}.start();
	}
	
	/**
	 * 連接派送伺服器
	 */
	private boolean getDeviceStatus(String device) {
		int i;
		boolean isOnline = false;
		if (client.IsOpen())
		{
			//Send request to server
            client.getDeviceInfo(client.m_sDVRName);
			
		    //Receive data
		    client.RecvData();
			
		    String xml = client.m_sRecvXmlData;
			
      		xmlStringDocParsor parser = new xmlStringDocParsor();
			Document doc = parser.getDomElement(xml); // getting DOM element
			Element root = doc.getDocumentElement();
			NodeList nl = root.getElementsByTagName(KEY_SVRNAME);
	            
			// looping through all item nodes <item>
			for (i = 0; i < nl.getLength(); i++) {
	            Element nSvrname = (Element)nl.item(i);

                if(nSvrname.getAttribute(KEY_STATUS).equals("1"))
                {
                    isOnline = true;
                }
	        }
		}
		return isOnline;
	}

    /**
     * HBGK Streaming Server 9.0
     */
    private boolean getDeviceListStatus(String device) {
        int i;
        boolean isOnline = false;
        if (client.IsOpen())
        {
            //Send request to server
            // Version I Server: client.getDeviceList();
            client.getDeviceInfo(client.m_sDVRName);

            //Receive data
            client.RecvData();

            String xml = client.m_sRecvXmlData;

            xmlStringDocParsor parser = new xmlStringDocParsor();
            Document doc = parser.getDomElement(xml); // getting DOM element
            Element root = doc.getDocumentElement();
            NodeList nl = root.getElementsByTagName(KEY_DEVICE);

            // looping through all item nodes <item>
            //ArrayList<HashMap<String, String>> device_list = new ArrayList<HashMap<String, String>>();
            for (i = 0; i < nl.getLength(); i++) {
                Element nDevice = (Element)nl.item(i);

                Element elCamName = (Element)nDevice.getElementsByTagName(KEY_SVRNAME).item(0);
                String camName = elCamName.getTextContent();
                if(camName.equals(device))
                {
                    String status = elCamName.getAttribute(KEY_STATUS);
                    if (status.equals("1"))
                    {
                        isOnline = true;
                        break;
                    }
                }
            }
        }
        return isOnline;
    }
	
	private void startStream()
	{
		String result;
		if(client.IsOpen())
		{
		    client.startStream();
		    client.imOK();
		    
		    if( client.RecvData() >= 0)
		    {
		    	result = client.m_sRecvXmlData;
		    	if(result.contains("SUCCESS"))
		    	{
		    	   startShowVideo();
		    	}
		    }
	    }
	}
	
	//Start the image view thread to receive streaming data
	private void startShowVideo()
	{
		image.setStreamReady(1);	
		return;
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}