package org.demoncode.portal.model;

import java.io.Serializable;

public class AudioMsg implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1089406376578029068L;
	
	private byte[] mData;
	private int mDuration;
	
	public AudioMsg(byte[] data, int duration) {
		mData = data;
		mDuration = duration;
	}
	
	public byte[] getData() {
		return mData;
	}
	
	public int getDuration() {
		return mDuration;
	}
}
