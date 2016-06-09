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
		return "���ڿ���WiFi�ȵ�...";
	}

	@Override
	protected String getInnerMessage() {
		return "�ȴ����ӡ���";
	}

	@Override
	protected WifiService createWifiService() {
		WifiConfiguration conf = WifiUtils.makeSimpleConfig(mSSID, false);
		WifiProvider provider = new WifiProvider(conf);
		provider.setHandler(new ChatNetworkHandler("����WiFi�ȵ�ʧ�ܡ�"));
		return provider;
	}

	@Override
	protected MyService createService() {
		Server server = new Server(8080);
		server.setHandler(new ChatSessionHandler(mWifiServ));
		return server;
	}

	
}
