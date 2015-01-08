package com.Manga.Activity.myChildren.Streaming.Messages;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;


public class NET_LAYER {
    public int 	    iActLength;					  //TCP包长,=sizeof(int 4,iActLength)+sizeof(BYTE 1,byDataType)+sizeof(BYTE 1,byVodFilePercent)+sizeof(WORD 2,wVodCurFrameNo)+4*sizeof(int)+sizeof(BYTE)+cBuffer实际长度+3;
    public byte 	byDataType;					  //数据类型，12:流媒体请求和应答时的XML类型，13:音视频帧，见byFilepercentOrFrameType
    public byte	    byVodFilePercent;			  //VOD文件播放进度
    public short	wVodCurFrameNo;				  //VOD文件当前帧,需要*2,因为最大为65535,视频文件最大可能为25*3600=90000
    public int		iTotalSplits;				  //总包数
    public int		iCurSplit;					  //当前包
    public int 	    iBlockHeadFlag;				  //当前包头标识
    public int 	    iBlockEndFlag;				  //当前包尾标识
    public byte 	byFilepercentOrFrameType;	  //当前帧类型,0:B/P帧 1:关键帧 2:文件头(只发一次,第一包) 3:心跳帧 4:音频帧,5:片段帧,6:VOD结束标识，7:FTP文件数据,8:文件结束标识
    public byte[]   cBuffer = new byte[NET_BUFFER_LEN+ALIGNMENT]; //XML数据或音视频数据,扩充3个对齐字节。发送和接受数据时，数据按8*1024进行拆分或者合并

	private static final int NET_BUFFER_LEN = (8*1024);
	private static final int ALIGNMENT = 3;
	private static final int HEADLEN = 25;
	private static final int NET_FRAME_LEN = (NET_BUFFER_LEN + ALIGNMENT + HEADLEN);

	
	public void initWithInputStream(ByteBuffer bBufInput)
	{	
		if (bBufInput!=null)
		{
			bBufInput.order(ByteOrder.LITTLE_ENDIAN);
			bBufInput.position(0);
			this.iActLength = bBufInput.getInt();
			this.byDataType = bBufInput.get();
			this.byVodFilePercent = bBufInput.get();
			this.wVodCurFrameNo = bBufInput.getShort();
			this.iTotalSplits = bBufInput.getInt();
			this.iCurSplit = bBufInput.getInt();
			this.iBlockHeadFlag = bBufInput.getInt();
			this.iBlockEndFlag = bBufInput.getInt();
			this.byFilepercentOrFrameType = bBufInput.get();
			bBufInput.get(this.cBuffer);
		}
	}
	
	public ByteBuffer composeOutputStream()
	{
	    // Create an empty ByteBuffer with right byte capacity
	    ByteBuffer bbuf = ByteBuffer.allocate(NET_FRAME_LEN);
	    bbuf.order(ByteOrder.LITTLE_ENDIAN); 
	    bbuf.putInt(this.iActLength);	     //int      iActLength
	    bbuf.put(this.byDataType);	     //char     byDataType 
	    bbuf.put(this.byVodFilePercent);	     //byte	    byVodFilePercent;
	    bbuf.putShort(this.wVodCurFrameNo);	 //short	wVodCurFrameNo;	
	    bbuf.putInt(this.iTotalSplits);	     //int		iTotalSplits;
	    bbuf.putInt(this.iCurSplit);	     //int		iCurSplit;	
	    bbuf.putInt(this.iBlockHeadFlag);	     //int 	    iBlockHeadFlag;
	    bbuf.putInt(this.iBlockEndFlag);	     //int 	    iBlockEndFlag;
	    bbuf.put(this.byFilepercentOrFrameType);	         //byte 	byFilepercentOrFrameType;
	    bbuf.put(this.cBuffer);//public   byte[]   cBuffer = new byte[8*1024+3];
	    	    
	    return bbuf;
	}
}