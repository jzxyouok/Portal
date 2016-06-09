package org.demoncode.portal.activity;

import org.demoncode.portal.network.WifiProvider;
import org.demoncode.portal.network.WifiService;
import org.demoncode.portal.network.WifiUtils;
import org.demoncode.portal.service.MyService;
import org.demoncode.portal.service.Server;

import android.net.wifi.WifiConfiguration;

public class HostingActivity extends AbstractConnActivity  {
	private String mSSID;

	@Override
	protected void postCreate() {
		mSSID = getIntent().getStringExtra("SSID");
	}

	@Override
	protected String getOuterMessage() {
		return "正在开启WiFi热点...";
	}

	@Override
	protected String getInnerMessage() {
		return "等待连接……";
	}

	@Override
	protected WifiService createWifiService() {
		WifiConfiguration conf = WifiUtils.makeSimpleConfig(mSSID, false);
		WifiProvider provider = new WifiProvider(conf);
		provider.setHandler(new ChatNetworkHandler("开启WiFi热点失败。"));
		return provider;
	}

	@Override
	protected MyService createService() {
		Server server = new Server(8080);
		server.setHandler(new ChatSessionHandler(mWifiServ));
		return server;
	}

	
}
