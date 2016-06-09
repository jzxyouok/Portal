package org.demoncode.portal.audio;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

/*
 * @author: Zou Kunhong
 */
public class Recorder implements Runnable{
    private AudioRecord record; 
    private int         buf_size;
    private byte[]      bytes;
    private ByteArrayOutputStream dataStream;
    private byte[]      data;
    private long        startTime;
    private long        endTime;
    private boolean     isRecording;
    private Object finishLock = new Object();
    private boolean finished = false;
    
    public Recorder() {
		init();
	}
	public void init(){
    	buf_size =  AudioRecord.getMinBufferSize(44100,
                        AudioFormat.CHANNEL_IN_STEREO,
                        AudioFormat.ENCODING_PCM_16BIT);
    	record = new AudioRecord(MediaRecorder.AudioSource.MIC,
    			44100,AudioFormat.CHANNEL_IN_STEREO,
    			AudioFormat.ENCODING_PCM_16BIT,
    			buf_size);
    	bytes = new byte[buf_size];
    	isRecording = false;
    }
    public void start(){
    	startTime = System.currentTimeMillis();
    	isRecording = true;
        new Thread(this).start();
    }
    public void stop(){
		endTime = System.currentTimeMillis();
		isRecording = false;
    	synchronized (finishLock) {
    		if (!finished) {
				try {
					finishLock.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
    		}
    	}
    }
    public byte[] getData(){
    	return data;
    }
    public int getDuration() {
    	return (int) (endTime - startTime);
    }
	@Override
	public void run() {
		dataStream = new ByteArrayOutputStream();
        record.startRecording();
		while(isRecording){
			Log.e("REC", "do recording.");
			record.read(bytes, 0, buf_size);
            try {
            	dataStream.write(bytes);
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
        record.stop();
        try {
        	data = dataStream.toByteArray();
        	dataStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	    synchronized (finishLock) {
        	finished = true;
        	finishLock.notify();
        }
	} 
}
