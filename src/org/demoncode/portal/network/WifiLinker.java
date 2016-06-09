package org.demoncode.portal.network;

import org.demoncode.portal.App;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public class WifiLinker extends WifiService {
	private int mNetId;
	
	public WifiLinker(WifiConfiguration config) {
		super(config);
		mNetId = -1;
	}
	
	@Override
	public void cleanup() {
		if (mNetId >= 0) {
			getWM().removeNetwork(mNetId);
		}
		if (mReceiver != null) {
			App.getContext().unregisterReceiver(mReceiver);
		}
	}
	
	private class Receiver extends CancellableReceiver {
		private boolean prevClosed = false;
		
		@Override
		public void doReceive(Context context, Intent intent) {			
			String action = intent.getAction();
			if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
				if (!mOpened) {
					int newState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1);
					if (newState == WifiManager.WIFI_STATE_DISABLED) {
						cleanup();
						mFailed = true;
						mHandler.failed();
					}
				}
			} else {
				NetworkInfo info = (NetworkInfo) intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
				NetworkInfo.State newState = info.getState();
				
				if (NetworkInfo.State.CONNECTED.equals(newState)) {
					WifiInfo wifiInfo = (WifiInfo) intent.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);
					Log.d("supp", "pre connected, id = " + wifiInfo.getNetworkId());
					if (!mOpened) {
						if (wifiInfo.getNetworkId() == mNetId) {
							mOpened = true;
							mHandler.established();
						} else if (prevClosed) {
							cleanup();
							mFailed = true;
							mHandler.failed();
						}
					}
				} else if (NetworkInfo.State.DISCONNECTED.equals(newState)) {
					if (mOpened) {
						cleanup();
						mClosed = true;
						mHandler.closed();
					}
					prevClosed = true;
				}
			}
		}
	}
	
	private class WifiStarter extends InterruptableStarter {
		@Override
		public void start() throws InterruptedException {
			WifiUtils.ensureApEnabled(false);
			WifiUtils.ensureWifiEnabled(true);
			
			WifiManager wm = getWM();
			
			mReceiver = new Receiver();
			IntentFilter filter = new IntentFilter();
			filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
			filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
			App.getContext().registerReceiver(mReceiver, filter);
			
			Log.i("A", "wifi linker start!");
			int netId = wm.addNetwork(mConfig);
			if (netId == -1) {
				cleanup();
				mFailed = true;
				mHandler.failed();
				return;
			}
			mNetId = netId;
			Log.i("A", "added, id =" + netId);

			boolean res = wm.enableNetwork(netId, true);
			if (!res) {
				cleanup();
				mFailed = true;
				mHandler.failed();
				return;
			}
			Log.i("A", "enabled");
		}
	}

	@Override
	protected Runnable createStarter() {
		return new WifiStarter();
	}

	@Override
	protected void close() {
		getWM().disableNetwork(mNetId);
	}
}
