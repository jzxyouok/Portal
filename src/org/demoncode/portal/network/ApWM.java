package org.demoncode.portal.network;

import java.lang.reflect.Method;

import org.demoncode.portal.App;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

public class ApWM {
	private static WifiManager getWM() {
		return (WifiManager) App.getContext().getSystemService(Context.WIFI_SERVICE);	
	}
	
	public static final String WIFI_AP_STATE_CHANGED_ACTION = "android.net.wifi.WIFI_AP_STATE_CHANGED";
	
	public static boolean setWifiApEnabled(WifiConfiguration config, boolean enabled) {
		try {
			Method method = WifiManager.class.getMethod(
					"setWifiApEnabled", WifiConfiguration.class, boolean.class);
			return (Boolean) method.invoke(getWM(), config, enabled);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static boolean isWifiApEnabled() {
		try {
			Method method = WifiManager.class.getMethod("isWifiApEnabled");
			return (Boolean) method.invoke(getWM());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static WifiConfiguration getWifiApConfiguration() {
		try {
			Method method = WifiManager.class.getMethod("getWifiApConfiguration");
            return (WifiConfiguration) method.invoke(getWM());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static int getWifiApState() {
		try {
			Method method = WifiManager.class.getMethod("getWifiApState");
            return (Integer) method.invoke(getWM());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 4;
	}
}
