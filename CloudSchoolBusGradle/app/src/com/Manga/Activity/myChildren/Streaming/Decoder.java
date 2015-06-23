package com.Manga.Activity.myChildren.Streaming;

import android.util.Log;

public class Decoder {
	
	static {
//		System.loadLibrary("H264Decoder");
        System.loadLibrary("videodecoder");

        Log.i("H264Decoder", "Load Library");
	}
	public native int Init();

	public native int free();

	public native int decodeFrame(byte[] in, int insize, byte[] out);
	
	public native int getWidth();
	
	public native int getHeight();

	public native int isInited();
}
