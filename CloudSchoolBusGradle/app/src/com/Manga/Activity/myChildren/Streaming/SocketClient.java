package com.Manga.Activity.myChildren.Streaming;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;


import com.Manga.Activity.myChildren.Streaming.Messages.NET_LAYER;

import android.util.Log;

public class SocketClient {

	private static final int SOCKET_ERROR = -1;

	
	private Socket client = null;
	public BufferedInputStream in = null;
	public BufferedOutputStream out = null;	
	
	private int        m_iPreRecvLen;
	private int        m_iPackageLen;
	private int        m_iFrameLen;
	
	private ByteBuffer m_pRecvBuff;
	
	public ByteBuffer m_pStreamData;
	public String     m_sRecvXmlData;
	public int        m_iStreamFrameLen;
	
	
	public String m_sStreamIP;//222.128.71.186
	public int m_iStreamPort;
    public String m_sDVRName;

	public int m_iChnNo = 0;
	private static int m_iStreamType = 1;
	private String user = "super";
	private String pass = "super";
	
	int iRecvBytes = 0;
	int iHeadLen= NET_LAYER.PACKET_EXTRA_LEN;//28
	NET_LAYER pPackage = new NET_LAYER();
	int	 iDataType;
	int  iDataLen;
	String sTemp;
	int  iLeftBytes=0;//处理完后剩余字节
	boolean  processMoreData = false;
	
	public SocketClient() {
		m_pStreamData = ByteBuffer.allocate(512*1024);
		m_pRecvBuff   = ByteBuffer.allocate(NET_LAYER.NET_LAYER_STRUCT_LEN);
		m_sRecvXmlData = "";
	}

	/**
	 * 是否開啟
	 * @return Boolean
	 */
	public Boolean IsOpen() {
		if(client == null)
			return false;
		else
		    return client.isConnected();
	}

	/**
	 * 中斷與伺服器連線
	 * @throws IOException
	 */
	public void closeConnect() throws IOException {
		if (client != null) {
			client.close();
			client = null;
		}

		if (in != null) {
			in.close();
			in = null;
		}

		if (out != null) {
			out.close();
			out = null;
		}
	}

	/**
	 * 連接伺服器
	 * @throws IOException
	 */
	public void openConnect() throws IOException {
		if (client == null) {
			InetAddress serverAddr = InetAddress.getByName(m_sStreamIP);//
            //InetAddress serverAddr = InetAddress.getByName("192.168.1.109");
            //InetAddress serverAddr = InetAddress.getByName("54.223.197.23");
			SocketAddress socAddress = new InetSocketAddress(serverAddr, m_iStreamPort); 
			client = new Socket();
			client.connect(socAddress, 30000);
		}

		if (out == null) {
			out = new BufferedOutputStream(client.getOutputStream());
		}
		
		if (in == null) {
			in = new BufferedInputStream(client.getInputStream());
		}
	}
	
	private int	SendData(byte[] srcdata, int iLength,int iDataType)
	{
		m_sRecvXmlData="";//一次请求前，清空字符串XML，避免累加.
		int i;
		int pSrcOffset;
		NET_LAYER	_NetLayer = null;
		int iSplit;				//如果大于8K，拆分的包数
		int iLastBlockLength;	//拆分后，前面包有效数据长度为8K，最后一包的长度
		
		_NetLayer = new NET_LAYER();
        _NetLayer.byProtocolType = 0;
        _NetLayer.byProtocolVer = 9;
		_NetLayer.byDataType=BigInteger.valueOf(iDataType).byteValue();
		_NetLayer.byFrameType=0;//无效
        _NetLayer.iTimeStampHigh=0;
        _NetLayer.iTimeStampLow=0;
        _NetLayer.iVodFilePercent=0;
        _NetLayer.iVodCurFrameNo=0;
		
		if (iLength%NET_LAYER.NET_BUFFER_LEN==0)
		{
			iSplit=iLength/NET_LAYER.NET_BUFFER_LEN;
			iLastBlockLength=NET_LAYER.NET_BUFFER_LEN;
		}
		else
		{
			iSplit=(iLength+NET_LAYER.NET_BUFFER_LEN)/NET_LAYER.NET_BUFFER_LEN;
			iLastBlockLength=iLength%NET_LAYER.NET_BUFFER_LEN;
		}
		//_NetLayer.iTotalSplits=iSplit;
		for(i=0;i<iSplit;i++)
		{
			//_NetLayer.iCurSplit=i;
			pSrcOffset=i*NET_LAYER.NET_BUFFER_LEN;
			if (i==iSplit-1)//最后一包
			{
				_NetLayer.iActLength= NET_LAYER.PACKET_EXTRA_LEN+iLastBlockLength;
				
				for(int j=pSrcOffset; j<pSrcOffset+iLastBlockLength; j++ )
				    _NetLayer.cBuffer[j-pSrcOffset]=srcdata[j];
				
				if(iSplit==1)
				{
					_NetLayer.byBlockHeadFlag=1;
					_NetLayer.byBlockEndFlag=1;
				}
				else
				{
					_NetLayer.byBlockHeadFlag=0;
					_NetLayer.byBlockEndFlag=0;
				}
			}
			else//前面的包
			{
				_NetLayer.iActLength=NET_LAYER.NET_LAYER_STRUCT_LEN;
				
				for(int j=pSrcOffset; j<pSrcOffset+NET_LAYER.NET_BUFFER_LEN; j++ )
					_NetLayer.cBuffer[j-pSrcOffset]=srcdata[j];
				
				if(i==0)
				{
					_NetLayer.byBlockHeadFlag=1;
					_NetLayer.byBlockEndFlag=0;
				}
				else
				{
					_NetLayer.byBlockHeadFlag=0;
					_NetLayer.byBlockEndFlag=0;
				}
			}
			
			try {
				byte arr[] = _NetLayer.composeOutputStream().array();
				out.write(arr,0,_NetLayer.iActLength);
				out.flush();
			} catch (Exception e) {
				return -1;
			}
			//iSendBytes=send(m_hSocket,(char *)&_NetLayer,_NetLayer.iActLength,0);
		}
		
		return _NetLayer.iActLength;
	} 
	
	public int RecvData()
	{

		while(true)
		{
RecvData:			
			if(!processMoreData){
				if(!IsOpen())
					continue;
			
				//iRecvBytes=recv(m_hSocket,(char *)m_pRecvBuff+m_iPreRecvLen,Net_LAYER_STRUCT_LEN-m_iPreRecvLen,0);
				
				byte[] content = new byte[NET_LAYER.NET_LAYER_STRUCT_LEN-m_iPreRecvLen];
				
				try {
					iRecvBytes = in.read(content);
				} catch (IOException e) {
					e.printStackTrace();
					iRecvBytes=-1;
				}
				m_pRecvBuff.position(m_iPreRecvLen);
				m_pRecvBuff.put(content,0, NET_LAYER.NET_LAYER_STRUCT_LEN-m_iPreRecvLen);
				
				pPackage.initWithInputStream(m_pRecvBuff);
			}
			
			if((iRecvBytes <= 0 ||iRecvBytes==SOCKET_ERROR) && !processMoreData)
			{
				iRecvBytes=-1;
				break;
			}
			else
			{
	            if(iRecvBytes+m_iPreRecvLen<iHeadLen && !processMoreData)//小于25
				{
					m_iPreRecvLen+=iRecvBytes;
					continue;
				}
				else
                {					
Process_More_Data:	
	                pPackage.initWithInputStream(m_pRecvBuff);
	                m_iPackageLen = pPackage.iActLength;
					if(m_iPreRecvLen+iRecvBytes >= m_iPackageLen)
					{
						iDataType=pPackage.byDataType;
						iDataLen=m_iPackageLen-NET_LAYER.PACKET_EXTRA_LEN;//减28

						if(iDataType==12||iDataType==9)//DATA_TYPE_SMS_CMD,或者 DATA_TYPE_REAL_XML
						{
							try {
								byte[] barr = new byte[iDataLen];
								for(int i=0;i<iDataLen;i++)
								{
									barr[i] = pPackage.cBuffer[i];
								}
								
								sTemp=new String(barr, "GBK");
								
								} catch (UnsupportedEncodingException e1) {
									e1.printStackTrace();
								} //GB2132, GBK, UTF8...
							
								m_sRecvXmlData+=sTemp;
						}
						else if(iDataType==13)//音视频数据
						{
							if(pPackage.byFrameType==0||pPackage.byFrameType==1||pPackage.byFrameType==4)//BP帧或I帧
							{
								m_pStreamData.position(m_iFrameLen);
								m_pStreamData.put(pPackage.cBuffer, 0 , iDataLen);//未做最大单帧校验......,可能越界
								
								m_iFrameLen+=iDataLen;
							}
							
							if(pPackage.byBlockEndFlag==1)
							{
								if(pPackage.byFrameType==0||pPackage.byFrameType==1||pPackage.byFrameType==4)//BP帧或I帧
								{
									//if(pPackage->byFilepercentOrFrameType==1)
									{
										sTemp = String.format("Len:%d FrameType:%d\n",m_iFrameLen,pPackage.byFrameType);
										Log.d("",sTemp);
										int a=9;
									}
									m_iStreamFrameLen = m_iFrameLen; //Record the Streaming data length
									//m_Decoder.SWInputVideoData(m_pStreamData,m_iFrameLen,pPackage->byFilepercentOrFrameType);
									//m_File.Write(m_pStreamData,m_iFrameLen);
								    //m_Avi.SWInputVideoData(m_pStreamData,m_iFrameLen,pPackage->byFilepercentOrFrameType);
									m_iFrameLen=0;
								}
							}
						}

						iLeftBytes=m_iPreRecvLen+iRecvBytes-m_iPackageLen;
						if(iLeftBytes==0)
						{
							m_iPreRecvLen=0;//复位
							m_iPackageLen=0;
							if(pPackage.byBlockEndFlag==1)//拆包的最后一包
							{
								break;
							}
							else
							{
								continue;
							}
						}
						
						if(iLeftBytes>0)
						{	
							//memmove((char *)m_pRecvBuff,(char *)m_pRecvBuff+m_iPackageLen,iLeftBytes);
							byte[] tempBBuf = new byte[iLeftBytes];
							
							m_pRecvBuff.position(m_iPackageLen);
							m_pRecvBuff.get(tempBBuf, 0 , iLeftBytes);
							m_pRecvBuff.rewind();
							m_pRecvBuff.put(tempBBuf);
							
							m_iPreRecvLen=iLeftBytes;
							m_iPackageLen=0;
							
							if(iLeftBytes<iHeadLen)
							{
								continue;
							}
							else
							{
								iRecvBytes=0;
								processMoreData = true;
							    continue;
							}
						}
					}
					else//单包少于pPackage->iActLength
					{
						m_iPreRecvLen+=iRecvBytes;
						processMoreData = false; //Got to RecvData
						continue;
					}
				}
			}
		}
		
		m_pStreamData.position(0);
		m_pRecvBuff.position(0);
		return iRecvBytes;

	}
	
	
	public void login() {
		String msg_body = "<TYPE>CheckUser</TYPE><User>" + user + "</User><Pwd>"+ pass + "</Pwd>";
		SendData(msg_body.getBytes(),msg_body.length(),9);
		RecvData();
	}
	
	public void getDeviceInfo(String sDVRName) {
		String msg_body = String.format("<TYPE>GetDeviceInfo</TYPE><DVRName>%s</DVRName><DVCID></DVCID>", sDVRName);
		SendData(msg_body.getBytes(), msg_body.length(), 9);
		//RecvData();
	}

    public void getDeviceList() {
        String msg_body = "<TYPE>GetDeviceList</TYPE>";
        SendData(msg_body.getBytes(), msg_body.length(), 9);
        //RecvData();
    }
	
	public void startStream()
	{
		String sXmlStartStream = String.format("<TYPE>StartStream</TYPE><DVRName>%s</DVRName><ChnNo>%d</ChnNo><StreamType>%d</StreamType>",m_sDVRName,m_iChnNo,m_iStreamType);
		SendData(sXmlStartStream.getBytes(),sXmlStartStream.length(),12);//参数12为流媒体命令
		//RecvData();
	    //返回<LinkReturn>SUCCESS</LinkReturn><DVRType>PCH264</DVRType><Width>352</Width><Height>288</Height><Interval>100</Interval><AudioCodeID>86017</AudioCodeID><HZ>44100</HZ><SampleWidth>16</SampleWidth><AudioChns>2</AudioChns><BitRate>8000</BitRate>
		//Log.d("Streaming",client.m_sRecvXmlData);
	}
	
	public void imOK()
	{
		String sImOk = "<TYPE>ImOK</TYPE>";
		SendData(sImOk.getBytes(),sImOk.length(),12);//ImOK
		//RecvData();
	}
}
