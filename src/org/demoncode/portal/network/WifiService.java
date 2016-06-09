package org.demoncode.portal.network;

import org.demoncode.portal.App;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

public abstract class WifiService {
	protected WifiServiceHandler mHandler;
	protected WifiConfiguration mConfig;
	protected BroadcastReceiver mReceiver;
	protected Thread mThread;
	protected boolean mOpened, mClosed, mFailed;
	protected boolean mCancelled;
	protected Object mCancelLock;

	protected static WifiManager getWM() {
		return (WifiManager) App.getContext().getSystemService(Context.WIFI_SERVICE);	
	}
	
	protected WifiService(WifiConfiguration config) {
		mConfig = config;
		mOpened = mClosed = mFailed = mCancelled = false;
		mCancelLock = new Object();
	}
	
	public void setHandler(WifiServiceHandler handler) {
		mHandler = handler;
	}
	
	protected abstract class CancellableReceiver extends BroadcastReceiver {
		protected abstract void doReceive(Context context, Intent intent);
		@Override
		public void onReceive(Context context, Intent intent) {
			synchronized(mCancelLock) {
				if (mCancelled)
					return;
				
				doReceive(context, intent);
			}
		}
	}
	
	protected abstract class InterruptableStarter implements Runnable {
		protected abstract void start() throws InterruptedException;
		@Override
		public void run() {
			try {
				start();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void start() {
		mThread = new Thread(createStarter());
		mThread.start();
	}
	public void stop() {
		synchronized(mCancelLock) {
			if (!mOpened) {
				if (!mFailed) {
					mThread.interrupt();
					mCancelled = true;
					cleanup();
				}
			} else {
				if (!mClosed)
					close();
			}
		}
	}
	
	protected abstract void cleanup();
	protected abstract Runnable createStarter();
	protected abstract void close();
}
