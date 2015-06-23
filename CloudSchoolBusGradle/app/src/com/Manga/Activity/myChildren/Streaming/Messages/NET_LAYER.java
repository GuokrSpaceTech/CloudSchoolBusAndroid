package com.Manga.Activity.myChildren.Streaming.Messages;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;


public class NET_LAYER {
    public int iActLength;                      //实际长度;
    public byte byProtocolType;    //新增,协议类型,流媒体为0,一点通盒子为1,手机通讯时,此值固定为0
    public byte byProtocolVer;    //新增,协议版本,目前固定为9,以后如果有升级,按1增加,可作为C/S端通讯版本匹配提示
    public byte byDataType;        //数据类型,手机通讯时,DATA_TYPE_REAL_XML:9:交互命令,DATA_TYPE_SMS_CMD:10:云台控制命令,DATA_TYPE_SMS_MEDIA:13:流媒体数据
    public byte byFrameType;    //FRAMETYPE_BP:0:视频非关键帧,FRAMETYPE_KEY:1:视频关键帧,FRAMETYPE_HEAD:2:文件头,FRAMETYPE_SPECIAL:3:特殊帧,收到此帧可直接忽略掉 FRAMETYPE_AUDIO:4:音频帧
    public int iTimeStampHigh;        //音/视频帧时间戳高位,目前保留
    public int iTimeStampLow;         //音/视频帧时间戳地位,目前保留
    public int iVodFilePercent;              //VOD文件播放进度
    public int iVodCurFrameNo;                  //VOD文件当前帧,需要*2,因为最大为65535,视频文件最大可能为25*3600=90000
    public byte byBlockHeadFlag;                  //当前包头标识
    public byte byBlockEndFlag;                  //当前包尾标识
    public byte byReserved1;    //保留1
    public byte byReserved2;    //保留2
    public byte[] cBuffer = new byte[NET_BUFFER_LEN]; //XML数据或音视频数据,扩充3个对齐字节。发送和接受数据时，数据按8*1024进行拆分或者合并

	public static final int NET_BUFFER_LEN = (8*1024);
    public static final int HEADLEN = 28;
    public static final int NET_LAYER_STRUCT_LEN = NET_BUFFER_LEN + HEADLEN;
    public static final int PACKET_EXTRA_LEN = (NET_LAYER_STRUCT_LEN - NET_BUFFER_LEN);

	
	public void initWithInputStream(ByteBuffer bBufInput)
	{	
		if (bBufInput!=null)
		{
			bBufInput.order(ByteOrder.LITTLE_ENDIAN);
			bBufInput.position(0);
			this.iActLength = bBufInput.getInt();
			this.byProtocolType = bBufInput.get();
			this.byProtocolVer = bBufInput.get();
            this.byDataType = bBufInput.get();
			this.byFrameType = bBufInput.get();
			this.iTimeStampHigh = bBufInput.getInt();
            this.iTimeStampLow = bBufInput.getInt();
			this.iVodFilePercent = bBufInput.getInt();
			this.iVodCurFrameNo = bBufInput.getInt();
			this.byBlockHeadFlag = bBufInput.get();
            this.byBlockEndFlag = bBufInput.get();
            this.byReserved1 = bBufInput.get();
            this.byReserved2 = bBufInput.get();
            bBufInput.get(this.cBuffer);
		}
	}
	
	public ByteBuffer composeOutputStream()
	{
	    // Create an empty ByteBuffer with right byte capacity
	    ByteBuffer bbuf = ByteBuffer.allocate(NET_LAYER_STRUCT_LEN);
	    bbuf.order(ByteOrder.LITTLE_ENDIAN);
	    bbuf.putInt(this.iActLength);	     //int      iActLength
	    bbuf.put(this.byProtocolType);	     //byte	    byVodFilePercent;
	    bbuf.put(this.byProtocolVer);	 //short	wVodCurFrameNo;
        bbuf.put(this.byDataType);	     //char     byDataType
        bbuf.put(this.byFrameType);	     //int		iTotalSplits;
	    bbuf.putInt(this.iTimeStampHigh);	     //int		iCurSplit;
        bbuf.putInt(this.iTimeStampLow);	     //int		iCurSplit;
	    bbuf.putInt(this.iVodFilePercent);	     //int 	    iBlockHeadFlag;
	    bbuf.putInt(this.iVodCurFrameNo);	     //int 	    iBlockEndFlag;
	    bbuf.put(this.byBlockHeadFlag);	         //byte 	byFilepercentOrFrameType;
        bbuf.put(this.byBlockEndFlag);	         //byte 	byFilepercentOrFrameType;
        bbuf.put(this.byReserved1);	         //byte 	byFilepercentOrFrameType;
        bbuf.put(this.byReserved2);	         //byte 	byFilepercentOrFrameType;
	    bbuf.put(this.cBuffer);//public   byte[]   cBuffer = new byte[8*1024+3];
	    	    
	    return bbuf;
	}
}