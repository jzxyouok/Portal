package org.demoncode.portal.network;

import org.demoncode.portal.App;

import android.content.Context;
import android.telephony.TelephonyManager;

public class MobileCM {
	public static boolean getDataConnEnabled() {
		// 启用数据流量 AND 连接WiFi时，同样是关闭状态，无法区分
		TelephonyManager tm = (TelephonyManager)
				App.getContext().getSystemService(Context.TELEPHONY_SERVICE);

	    return tm.getDataState() != TelephonyManager.DATA_DISCONNECTED;
	}
}
