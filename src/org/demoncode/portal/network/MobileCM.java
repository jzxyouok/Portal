package org.demoncode.portal.network;

import org.demoncode.portal.App;

import android.content.Context;
import android.telephony.TelephonyManager;

public class MobileCM {
	public static boolean getDataConnEnabled() {
		// ������������ AND ����WiFiʱ��ͬ���ǹر�״̬���޷�����
		TelephonyManager tm = (TelephonyManager)
				App.getContext().getSystemService(Context.TELEPHONY_SERVICE);

	    return tm.getDataState() != TelephonyManager.DATA_DISCONNECTED;
	}
}
