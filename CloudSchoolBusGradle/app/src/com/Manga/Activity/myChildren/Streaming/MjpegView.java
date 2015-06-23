package com.Manga.Activity.myChildren.Streaming;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.Bitmap.Config;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.Manga.Activity.R;
import com.Manga.Activity.myChildren.Streaming.H264Decoder;
import com.Manga.Activity.myChildren.Streaming.Messages.NET_LAYER;

public class MjpegView extends SurfaceView implements SurfaceHolder.Callback {
    public final static int POSITION_UPPER_LEFT = 9;
    public final static int POSITION_UPPER_RIGHT = 3;
    public final static int POSITION_LOWER_LEFT = 12;
    public final static int POSITION_LOWER_RIGHT = 6;

    public final static int SIZE_STANDARD = 1;
    public final static int SIZE_BEST_FIT = 4;
    public final static int SIZE_FULLSCREEN = 8;

    private static final int ALIGNMENT = 0;
    private static final int HEADLEN = NET_LAYER.HEADLEN;
    private static final int NET_BUFFER_LEN = NET_LAYER.NET_BUFFER_LEN;
    private static final int ALIGN_HEADLEN = (HEADLEN + ALIGNMENT);
    private static final int NET_LAYER_STRUCT_LEN = (NET_BUFFER_LEN + ALIGN_HEADLEN);
    private static final int SOCKET_ERROR = -1;

    private MjpegViewThread thread;
    private boolean showFps = false;
    private boolean mRun = false;
    private boolean surfaceDone = false;
    private int streamReady = 0; // 0: Not ready 1: Ready -1: Socket Error
    private Paint overlayPaint;
    private int overlayTextColor;
    private int overlayBackgroundColor;
    private int ovlPos;
    private int dispWidth;
    private int dispHeight;
    private int displayMode;
    public H264Decoder h264Decoder = null;
    public SocketClient source;

    private ByteBuffer mPixel = ByteBuffer.allocateDirect(1080 * 720 * 2);
    private int native_width = 0;
    private int native_height = 0;

    private int m_iPreRecvLen;
    private int m_iPackageLen;
    private int m_iFrameLen;
    private ByteBuffer m_pStreamData = ByteBuffer.allocateDirect(128 * 1024);
    private ByteBuffer m_pRecvBuff = ByteBuffer.allocateDirect(NET_LAYER_STRUCT_LEN);
    private String m_sRecvXmlData = "";
    /**
     * 检查版本网络超时
     */
    private int error_cnt = 0;
    public Handler msgHandler;

    public class MjpegViewThread extends Thread {
        private SurfaceHolder mSurfaceHolder;
        private int frameCounter = 0;
        private long start;
        private Bitmap ovl;
        private Context cntx;


        public MjpegViewThread(SurfaceHolder surfaceHolder, Context context) {
            mSurfaceHolder = surfaceHolder;
            cntx = context;
        }

        private Rect destRect(int bmw, int bmh) {
            int tempx;
            int tempy;
            if (displayMode == MjpegView.SIZE_STANDARD) {
                tempx = (dispWidth / 2) - (bmw / 2);
                tempy = (dispHeight / 2) - (bmh / 2);
                return new Rect(tempx, tempy, bmw + tempx, bmh + tempy);
            }
            if (displayMode == MjpegView.SIZE_BEST_FIT) {
                float bmasp = (float) bmw / (float) bmh;
                bmw = dispWidth;
                bmh = (int) (dispWidth / bmasp);
                if (bmh > dispHeight) {
                    bmh = dispHeight;
                    bmw = (int) (dispHeight * bmasp);
                }
                tempx = (dispWidth / 2) - (bmw / 2);
                tempy = (dispHeight / 2) - (bmh / 2);
                return new Rect(tempx, tempy, bmw + tempx, bmh + tempy);
            }
            if (displayMode == MjpegView.SIZE_FULLSCREEN)
                return new Rect(0, 0, dispWidth, dispHeight);
            return null;
        }

        public void setSurfaceSize(int width, int height) {
            synchronized (mSurfaceHolder) {
                dispWidth = width;
                dispHeight = height;
            }
        }

        private Bitmap makeFpsOverlay(Paint p, String text) {
            Rect b = new Rect();
            p.getTextBounds(text, 0, text.length(), b);
            int bwidth = b.width() + 2;
            int bheight = b.height() + 2;
            Bitmap bm = Bitmap.createBitmap(bwidth, bheight,
                    Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(bm);
            p.setColor(overlayBackgroundColor);
            c.drawRect(0, 0, bwidth, bheight, p);
            p.setColor(overlayTextColor);
            c.drawText(text, -b.left + 1,
                    (bheight / 2) - ((p.ascent() + p.descent()) / 2) + 1, p);
            return bm;
        }

        private Bitmap makeErrorOverlay(Paint p, String text) {
            Rect b = new Rect();
            p.setTextSize(50);
            p.getTextBounds(text, 0, text.length(), b);
            int bwidth = b.width() + 2;
            int bheight = b.height() + 2;
            Bitmap bm = Bitmap.createBitmap(bwidth, bheight,
                    Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(bm);
            p.setColor(overlayBackgroundColor);
            c.drawRect(0, 0, bwidth, bheight, p);
            p.setColor(overlayTextColor);

            c.drawText(text, -b.left + 1,
                    (bheight / 2) - ((p.ascent() + p.descent()) / 2) + 1, p);
            return bm;
        }

        public void run() {
            start = System.currentTimeMillis();
            PorterDuffXfermode mode = new PorterDuffXfermode(
                    PorterDuff.Mode.DST_OVER);
            Bitmap bm = null;
            Bitmap videoBits = null;
            int width;
            int height;
            Rect destRect;
            Canvas c = null;
            Paint p = new Paint();
            String fps = "";
            long len = 0;

            int iRecvBytes = 0;
            int iHeadLen = ALIGN_HEADLEN - ALIGNMENT;//25
            NET_LAYER pPackage = new NET_LAYER();
            int iDataType;
            int iDataLen;
            String sTemp;
            int iLeftBytes = 0;//处理完后剩余字节
            boolean processMoreData = false; //Simulate the Goto

            c = mSurfaceHolder.lockCanvas();

            ovl = makeErrorOverlay(overlayPaint, getResources().getString(R.string.isLoading));
            bm = Utils.RotateBitmap(ovl, 90);
            p.setXfermode(mode);
            if (ovl != null) {
                height = (getHeight() - bm.getHeight()) / 2;
                width = (getWidth() - bm.getWidth()) / 2;
                c.drawBitmap(bm, width, height, null);
            }
            p.setXfermode(null);

            mSurfaceHolder.unlockCanvasAndPost(c);

            //Wait until the streaming is ready
            while (streamReady != 1) {
                if (streamReady == -1) {
                    c = mSurfaceHolder.lockCanvas();
                    ovl = makeErrorOverlay(overlayPaint, getResources().getString(R.string.streaming_error));
                    bm = Utils.RotateBitmap(ovl, 90);
                    p.setXfermode(mode);
                    if (ovl != null) {
                        height = (getHeight()
                                - bm.getHeight()) / 2;
                        width = (getWidth()
                                - bm.getWidth()) / 2;
                        c.drawBitmap(bm, width, height, null);
                    }
                    p.setXfermode(null);
                    mSurfaceHolder.unlockCanvasAndPost(c);
                    try {
                        sleep(3000);
                    } catch (InterruptedException e) {
                        ((Activity) cntx).finish();
                    }
                    ((Activity) cntx).finish();
                    return;
                }
            }

            while (mRun) {
                if (surfaceDone) {
                    try {
                        synchronized (mSurfaceHolder) {
                            {
                                /*
								 *  This is the block ported from HBGK Receive Streaming Demo
								 */
                                RecvData:
                                if (!processMoreData) {
                                    if (source == null) {
                                        error_cnt += 1;
                                        continue;
                                    }
                                    if (!source.IsOpen()) {
                                        error_cnt += 1; //This may be caused by user shutting down the view
                                        continue;
                                    }
                                    //iRecvBytes=recv(m_hSocket,(char *)m_pRecvBuff+m_iPreRecvLen,Net_LAYER_STRUCT_LEN-m_iPreRecvLen,0);
                                    byte[] content = new byte[NET_LAYER_STRUCT_LEN - m_iPreRecvLen];

                                    try {
                                        iRecvBytes = source.in.read(content);
                                    } catch (IOException e) {
                                        iRecvBytes = -1;
                                    }
                                    m_pRecvBuff.position(m_iPreRecvLen);
                                    m_pRecvBuff.put(content, 0, NET_LAYER_STRUCT_LEN - m_iPreRecvLen);
                                }

                                if ((iRecvBytes <= 0 || iRecvBytes == SOCKET_ERROR) && !processMoreData) {
                                    iRecvBytes = -1;
                                    error_cnt += 1;
                                    continue;
                                } else {
                                    if (iRecvBytes + m_iPreRecvLen < iHeadLen && !processMoreData)//小于25
                                    {
                                        m_iPreRecvLen += iRecvBytes;
                                        continue;
                                    } else {
                                        Process_More_Data:
                                        pPackage.initWithInputStream(m_pRecvBuff);
                                        m_iPackageLen = pPackage.iActLength;
                                        if (m_iPreRecvLen + iRecvBytes >= m_iPackageLen) {
                                            iDataType = pPackage.byDataType;
                                            iDataLen = m_iPackageLen - ALIGN_HEADLEN;//减28

                                            if (iDataType == 12 || iDataType == 9)//DATA_TYPE_SMS_CMD,或者 DATA_TYPE_REAL_XML
                                            {
                                                try {
                                                    ByteBuffer bbuf = ByteBuffer.allocate(iDataLen);
                                                    bbuf.position(0);
                                                    bbuf.put(pPackage.cBuffer, 0, iDataLen);
                                                    sTemp = new String(pPackage.cBuffer, "UTF-8");
                                                    m_sRecvXmlData += sTemp;
                                                    //Log.d("",sTemp);
                                                } catch (UnsupportedEncodingException e) {
                                                }
                                            } else if (iDataType == 13)//音视频数据
                                            {
                                                if (pPackage.byFrameType == 0 || pPackage.byFrameType == 1 || pPackage.byFrameType == 4)//BP帧或I帧
                                                {
                                                    m_pStreamData.position(m_iFrameLen);
                                                    m_pStreamData.put(pPackage.cBuffer, 0, iDataLen);//未做最大单帧校验......,可能越界
                                                    m_iFrameLen += iDataLen;
                                                }

                                                if (pPackage.byBlockEndFlag == 1) {
                                                    if (pPackage.byFrameType == 0 || pPackage.byFrameType == 1 || pPackage.byFrameType == 4)//BP帧或I帧
                                                    {
                                                        //if(pPackage->byFilepercentOrFrameType==1)
                                                        {
                                                            //sTemp = String.format("Len:%d FrameType:%d\n",m_iFrameLen,pPackage.byFilepercentOrFrameType);
                                                            //Log.d("",sTemp);
                                                        }

                                                        //m_Decoder.SWInputVideoData(m_pStreamData,m_iFrameLen,pPackage->byFilepercentOrFrameType);
                                                        m_pStreamData.position(0);
                                                        mPixel.position(0);
                                                        if (h264Decoder != null) {
                                                            //len = h264Decoder.decodeFrame(m_pStreamData.array(), m_iFrameLen, mPixel.array());
                                                            h264Decoder.consumeNalUnitsFromDirectBuffer(m_pStreamData, m_iFrameLen, H264Decoder.AV_NOPTS_VALUE);
                                                            if( h264Decoder.isFrameReady() )
                                                                len = h264Decoder.decodeFrameToDirectBuffer(mPixel);
                                                            else
                                                                len = -1;
                                                        }
                                                            m_iFrameLen = 0;

                                                        //In case surface is destroyed
                                                        if (len == -1 || h264Decoder == null) {
                                                            bm = null;
                                                        } else {
                                                            native_width = h264Decoder.getWidth();
                                                            native_height = h264Decoder.getHeight();

                                                            videoBits = Bitmap.createBitmap(native_width, native_height, Config.RGB_565);
                                                            videoBits.copyPixelsFromBuffer(mPixel);

                                                            bm = Utils.RotateBitmap(videoBits, 90);

                                                            destRect = destRect(bm.getWidth(), bm.getHeight());
                                                            c = mSurfaceHolder.lockCanvas();
                                                            c.drawBitmap(bm, null, destRect, p);

                                                            if (showFps) {
                                                                p.setXfermode(mode);
                                                                if (ovl != null) {
                                                                    height = ((ovlPos & 1) == 1) ? destRect.top
                                                                            : destRect.bottom
                                                                            - ovl.getHeight();
                                                                    width = ((ovlPos & 8) == 8) ? destRect.left
                                                                            : destRect.right
                                                                            - ovl.getWidth();
                                                                    c.drawBitmap(ovl, width, height, null);
                                                                }
                                                                p.setXfermode(null);
                                                                frameCounter++;
                                                                if ((System.currentTimeMillis() - start) >= 1000) {
                                                                    fps = String.valueOf(frameCounter)
                                                                            + "fps";
                                                                    frameCounter = 0;
                                                                    start = System.currentTimeMillis();
                                                                    ovl = makeFpsOverlay(overlayPaint, fps);
                                                                }
                                                            }
                                                            mSurfaceHolder.unlockCanvasAndPost(c);
                                                        } // else draw bitmap
                                                    }
                                                }
                                            } // Datatype 13

                                            iLeftBytes = m_iPreRecvLen + iRecvBytes - m_iPackageLen;
                                            if (iLeftBytes == 0) {
                                                m_iPreRecvLen = 0;//复位
                                                m_iPackageLen = 0;
                                                if (pPackage.byBlockEndFlag == 1)//拆包的最后一包
                                                {
                                                    //break;
                                                    //processMoreData = false;
                                                    iRecvBytes = 0;
                                                    iDataLen = 0;
                                                    sTemp = "";
                                                    continue;
                                                } else {
                                                    //processMoreData = false;
                                                    continue;
                                                }
                                            }
//												
                                            if (iLeftBytes > 0) {
                                                //memmove((char *)m_pRecvBuff,(char *)m_pRecvBuff+m_iPackageLen,iLeftBytes);
                                                byte[] tempBBuf = new byte[iLeftBytes];
                                                m_pRecvBuff.position(m_iPackageLen);
                                                m_pRecvBuff.get(tempBBuf, 0, iLeftBytes);
                                                m_pRecvBuff.rewind();
                                                m_pRecvBuff.put(tempBBuf);

                                                m_iPreRecvLen = iLeftBytes;
                                                m_iPackageLen = 0;

                                                if (iLeftBytes < iHeadLen) {
                                                    processMoreData = false;
                                                    continue;
                                                } else {
                                                    iRecvBytes = 0;
                                                    processMoreData = true; // Go to ProcessMoredata
                                                    continue;
                                                }
                                            }
                                        } else//单包少于pPackage->iActLength
                                        {
                                            m_iPreRecvLen += iRecvBytes;
                                            processMoreData = false; //Got to RecvData
                                            continue;
                                        }
                                    }
                                }
                            }

                        } //Sync surfaceholder
                    } finally {
                        if (bm != null)
                            bm.recycle();
                        if (videoBits != null)
                            videoBits.recycle();

                        if (error_cnt == 1000) {
                            ovl = makeErrorOverlay(overlayPaint, getResources().getString(R.string.out_time));
                            bm = Utils.RotateBitmap(ovl, 90);
                            p.setXfermode(mode);
                            if (ovl != null) {
                                height = (getHeight()
                                        - bm.getHeight()) / 2;
                                width = (getWidth()
                                        - bm.getWidth()) / 2;
                                c = mSurfaceHolder.lockCanvas();
                                //There is chance when the surface is destroyed and the thread is still running
                                if (c != null)
                                    c.drawBitmap(bm, width, height, null);
                                mSurfaceHolder.unlockCanvasAndPost(c);
                            }
                            p.setXfermode(null);
                            try {
                                sleep(3000);
                            } catch (InterruptedException e) {
                                ((Activity) cntx).finish();
                            }
                            ((Activity) cntx).finish();
                        }

                    }
                } //Surface Done
            }
        }
    }

    public MjpegView(Context context) {
        super(context);
        init(context);
    }

    public MjpegView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        thread = new MjpegViewThread(holder, context);

        setFocusable(true);
        overlayPaint = new Paint();
        overlayPaint.setTextAlign(Paint.Align.LEFT);
        overlayPaint.setTextSize(30);
        overlayPaint.setTypeface(Typeface.DEFAULT);
        overlayTextColor = Color.WHITE;
        overlayBackgroundColor = Color.BLACK;
        ovlPos = MjpegView.POSITION_LOWER_RIGHT;
        displayMode = MjpegView.SIZE_STANDARD;
        dispWidth = getWidth();
        dispHeight = getHeight();

        error_cnt = 0;
    }

    public void surfaceChanged(SurfaceHolder holder, int f, int w, int h) {
        thread.setSurfaceSize(w, h);
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        surfaceDone = false;
        stopPlayback();
        if (h264Decoder != null) {
            //h264Decoder.free();
            h264Decoder = null;
        }
    }

    public void surfaceCreated(SurfaceHolder holder) {
        surfaceDone = true;

        int ret = 0;

        if (h264Decoder == null) {
            //h264Decoder = new Decoder();
            h264Decoder = new H264Decoder(1);
            //ret = h264Decoder.Init();
        }

        startPlayback();
    }

    public boolean isPlaying() {
        return mRun;
    }

    public String startPlayback() {

        if (mRun == false) {
            mRun = true;
            thread.start();
            return "";
        }
        return "";
    }

    public void stopPlayback() {
        Thread.interrupted();
        mRun = false;
    }

    public void showFps(boolean b) {
        showFps = b;
    }

    public void setOverlayPaint(Paint p) {
        overlayPaint = p;
    }

    public void setOverlayTextColor(int c) {
        overlayTextColor = c;
    }

    public void setOverlayBackgroundColor(int c) {
        overlayBackgroundColor = c;
    }

    public void setOverlayPosition(int p) {
        ovlPos = p;
    }

    public void setDisplayMode(int s) {
        displayMode = s;
    }

    public void setSource(SocketClient source) {
        //mIn = new MjpegInputStream(source,h264Decoder);
        this.source = source;
    }

    public void setStreamReady(int ready) {
        this.streamReady = ready;
    }

    public boolean isRecording() {
        return false;
    }

    public int getAvgFPS() {
        return 0;
    }
}
