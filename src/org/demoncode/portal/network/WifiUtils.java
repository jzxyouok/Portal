package org.demoncode.portal.network;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Calendar;
import java.util.Enumeration;

import org.demoncode.portal.App;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;

public class WifiUtils {
	public static WifiManager getWM() {
		return (WifiManager) App.getContext().getSystemService(Context.WIFI_SERVICE);	
	}
	
	public static void ensureWifiEnabled(boolean enabled) throws InterruptedException {
		final int desiredState = enabled ?
				WifiManager.WIFI_STATE_ENABLED : WifiManager.WIFI_STATE_DISABLED;
		WifiManager wm = getWM();
		
		if (wm.getWifiState() == desiredState)
			return;
		
		final Object enabledNotifier = new Object();
		
		BroadcastReceiver wifiEnabledReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				int newState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1);
				if (newState == desiredState) {
					synchronized(enabledNotifier) {
						context.unregisterReceiver(this);
						enabledNotifier.notify();
					}
				}
			}
		};
		
		App.getContext().registerReceiver(wifiEnabledReceiver, new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));
		wm.setWifiEnabled(enabled);
		Log.i("EN-", Calendar.getInstance().getTimeInMillis() + "");
		synchronized(enabledNotifier) {
			while (wm.getWifiState() != desiredState) {
				enabledNotifier.wait();
				Log.i("EN-", Calendar.getInstance().getTimeInMillis() + "");
			}
		}
	}
	
	public static void ensureApEnabled(boolean enabled) throws InterruptedException {
		final int desiredState = enabled ?
				WifiManager.WIFI_STATE_ENABLED : WifiManager.WIFI_STATE_DISABLED;
		
		final Object apDisableNotifier = new Object();
		BroadcastReceiver apDisableRecv = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				 int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0) % 10;
				 if (state == desiredState) {
					 context.unregisterReceiver(this);
					 synchronized(apDisableNotifier) {
						 apDisableNotifier.notify();
					 }
				 }
			}
		};
		App.getContext().registerReceiver(apDisableRecv, new IntentFilter(ApWM.WIFI_AP_STATE_CHANGED_ACTION));
		ApWM.setWifiApEnabled(ApWM.getWifiApConfiguration(), enabled);
		synchronized(apDisableNotifier) {
			if (ApWM.getWifiApState() % 10 != desiredState) {
				apDisableNotifier.wait();
			}
		}
	}

	public static String getIP() {
	    try {
	        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
	        		en.hasMoreElements();)
	        {
	            NetworkInterface intf = en.nextElement();
	            if (intf.getName().contains("wl") || intf.getName().contains("ap")) {
	                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses();
	                		enumIpAddr.hasMoreElements();)
	                {
	                    InetAddress inetAddress = enumIpAddr.nextElement();
	                    if (!inetAddress.isLoopbackAddress()
	                            && (inetAddress.getAddress().length == 4)) {
	                        Log.d("getIP", inetAddress.getHostAddress());
	                        return inetAddress.getHostAddress();
	                    }
	                }
	            }
	        }
	    } catch (SocketException ex) {
	        Log.e("getIP", ex.toString());
	    }
	    return null;
	}
	
	public static int getDhcpIp() {
		return getWM().getDhcpInfo().serverAddress;
	}
	
	@SuppressWarnings("deprecation")
	public static String formatIp(int ip) {
		return Formatter.formatIpAddress(ip);
	}
	
	public static WifiConfiguration makeSimpleConfig(String SSID, boolean quoted) {
		WifiConfiguration conf = new WifiConfiguration();
		if (quoted) {
			conf.SSID = "\"" + SSID + "\"";
		} else {
			conf.SSID = SSID;
		}
		// conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
		return conf;
	}
}
