package org.demoncode.portal.network;

import org.demoncode.portal.App;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

public class WifiProvider extends WifiService {
	
	public WifiProvider(WifiConfiguration config) {
		super(config);
	}
	
	@Override
	protected void cleanup() {
		if (mReceiver != null)
			App.getContext().unregisterReceiver(mReceiver);
	}
	
	private class Receiver extends CancellableReceiver {
		@Override
		public void doReceive(Context context, Intent intent) {
			if (mCancelled) {
				cleanup();
				return;
			}
			
            int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0) % 10;
            Log.d("wifiProvider", "status = " + state);
            if (WifiManager.WIFI_STATE_ENABLED == state) {
            	if (!mOpened) {
            		mOpened = true;
            		mHandler.established();
            	}
            } else if (WifiManager.WIFI_STATE_DISABLED == state) {
            	if (mOpened) {
            		cleanup();
            		mClosed = true;
            		mHandler.closed();
            	}
            }
		}
	}
	
	private class HotSpotStarter extends InterruptableStarter {
		@Override
		protected void start() throws InterruptedException {
			WifiUtils.ensureWifiEnabled(false);
			WifiUtils.ensureApEnabled(false);
			
			mReceiver = new Receiver();
			IntentFilter filter = new IntentFilter();
			filter.addAction(ApWM.WIFI_AP_STATE_CHANGED_ACTION);
			App.getContext().registerReceiver(mReceiver, filter);
			
			ApWM.setWifiApEnabled(mConfig, true);
		}
	}
	
	@Override
	protected Runnable createStarter() {
		return new HotSpotStarter();
	}
	
	@Override
	protected void close() {
		ApWM.setWifiApEnabled(mConfig, false);
	}
}
