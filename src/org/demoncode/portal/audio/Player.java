package org.demoncode.portal.audio;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

/*
 * @author: Zou Kunhong
 */
public class Player implements Runnable{
    private AudioTrack audio;
    private int        buf_size;
    private byte[]     bytes;
    private boolean    isPlaying;
	private ByteArrayInputStream dataStream;
	private byte[]     data;
	private OnStopListener onStopListener;
	
	public Player(byte[] d) {
		data = d;
		init();
	}
	public void init(){
		try{
            buf_size = AudioTrack.getMinBufferSize(44100,
            				AudioFormat.CHANNEL_IN_STEREO,
                            AudioFormat.ENCODING_PCM_16BIT);
            audio = new AudioTrack(AudioManager.STREAM_MUSIC, 44100,
            				AudioFormat.CHANNEL_IN_STEREO,
                            AudioFormat.ENCODING_PCM_16BIT,
                            buf_size,AudioTrack.MODE_STREAM);
            bytes = new byte[buf_size];
            isPlaying = false;
	     }
	     catch(Exception e){
	    	 e.printStackTrace();
	     }
	}
	public void start() {
		new Thread(this).start();
	}
	public void stop(){
		isPlaying = false;
	}
	@Override
	public void run() {
		isPlaying = true;
		audio.play();
		dataStream = new ByteArrayInputStream(data);
		while(isPlaying){
            try{
	           	 if(dataStream.read(bytes) == -1){
	           		 isPlaying = false;
	           		 break;
	           	 }
                audio.write(bytes, 0, bytes.length) ;
            }
            catch(Exception e){
            	e.printStackTrace();
            }
		}
		audio.stop();
		
		try {
			dataStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (onStopListener != null)
			onStopListener.stopped();
	}
	
	public void setOnStopListener(OnStopListener o) {
		onStopListener = o;
	}
	
	public static interface OnStopListener {
		public void stopped();
	}
}
 
