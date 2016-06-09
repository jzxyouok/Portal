package org.demoncode.portal.activity;

import java.net.InetSocketAddress;

import org.demoncode.portal.network.WifiLinker;
import org.demoncode.portal.network.WifiService;
import org.demoncode.portal.network.WifiUtils;
import org.demoncode.portal.service.Client;
import org.demoncode.portal.service.MyService;

import android.net.wifi.WifiConfiguration;
import android.util.Log;

public class ConnectingActivity extends AbstractConnActivity {
	private String BSSID;
	private String name;

	@Override
	protected void postCreate() {
		BSSID = getIntent().getStringExtra("BSSID");
		name = getIntent().getStringExtra("SSID");
	}
	
	@Override
	protected WifiService createWifiService() {
		WifiConfiguration conf = new WifiConfiguration();
		conf.BSSID = BSSID;
		conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
//		conf.preSharedKey="\"31415926\"";
		Log.i("BSSID", BSSID);
		WifiLinker linker = new WifiLinker(conf);
		linker.setHandler(new ChatNetworkHandler("连接此WiFi失败"));
		return linker;
	}
	
	@Override
	protected MyService createService() {
		Client client = new Client(
				new InetSocketAddress(WifiUtils.formatIp(WifiUtils.getDhcpIp()), 8080));
		Log.d("Connect", "to " + WifiUtils.formatIp(WifiUtils.getDhcpIp()));
		Log.d("Connect", "from " + WifiUtils.getIP());
		client.setHandler(new ChatSessionHandler(mWifiServ));
		return client;
	}


	@Override
	protected String getOuterMessage() {
		return "连接WiFi到" + name + "...";
	}


	@Override
	protected String getInnerMessage() {
		return "连接到对方手机...";
	}
}
