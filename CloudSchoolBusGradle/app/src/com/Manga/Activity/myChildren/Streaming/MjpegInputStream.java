package com.Manga.Activity.myChildren.Streaming;


import java.nio.ByteBuffer;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;

import com.Manga.Activity.myChildren.Streaming.Decoder;
import com.Manga.Activity.myChildren.Streaming.Messages.NET_LAYER;


public class MjpegInputStream {

	private final static int FRAME_MAX_LENGTH = 512*1024;
	private Decoder h264Decoder = null;
	private byte[] mPixel = new byte[640*480*2];
	private Bitmap VideoBit = Bitmap.createBitmap(640 ,480, Config.RGB_565);
	private ByteBuffer buffers = ByteBuffer.wrap(mPixel);
	private byte[] inBuf = new byte[FRAME_MAX_LENGTH]; 
	private SocketClient sc;
	
	private ByteBuffer packetsbuf = ByteBuffer.allocate(512 * 1024);
	private int frameLen;

	private static final int ALIGNMENT = 3;
	private static final int HEADLEN = 25;
	
	public MjpegInputStream(SocketClient sclient, Decoder h264Decoder) {
		this.h264Decoder = h264Decoder;
        sc = sclient;
        frameLen = 0;
	}

	public Bitmap readFrame() {
		
		sc.RecvData();
		int dataLen = sc.m_iStreamFrameLen;
//		int iFramePos = _find_head(sc.m_pStreamData.array(), dataLen);
		
//		//Not found iFrame,just put the data into the buffer
//		if( iFramePos== -1 )
//		{
//			packetsbuf.put(sc.m_pStreamData.array(), 0 , dataLen );
//			frameLen = frameLen + dataLen;
//			return (Bitmap)null;
//		}
//		//Found iFrame, construct the packet 
//		else
//		{
//			if( (iFramePos == 0 && frameLen > 0) || iFramePos > 0 )
//			{	
//				packetsbuf.put(sc.m_pStreamData.array(), 0, iFramePos);
//				frameLen = frameLen + iFramePos;

		int len = h264Decoder.decodeFrame(sc.m_pStreamData.array(), dataLen, mPixel);

//				int len = h264Decoder.decodeFrame(packetsbuf.array(), frameLen, mPixel);
//				
//			    if(len == -1){
//				    return (Bitmap)null;
//			    }
//			    
			    VideoBit.copyPixelsFromBuffer(buffers);
			    buffers.position(0);
//			    
//			    //Move the rest data into the packetsBuffer
//			    packetsbuf.rewind();
//			    
//			    packetsbuf.put(sc.m_pStreamData.array(), iFramePos, dataLen - iFramePos);//Include the iFrame Flag
//			    
				return Utils.RotateBitmap(VideoBit, 90);
//			}
//		}
//		
//		return (Bitmap)null;
	}
	
	
	 
	private int _find_head(byte[] rawframe, int len)
	{
	    int i;
	    boolean isMatch=false;
	    for (i=0;i<len;i++){
	        if (rawframe[i] == 0 && rawframe[i+1] == 0 && rawframe[i+2] == 0 && rawframe[i+3] == 1 &&   rawframe[i+4]==0x67){
	            isMatch=true;
	            break;
	        }
	    }

	    return isMatch?i:-1;
	}

}
