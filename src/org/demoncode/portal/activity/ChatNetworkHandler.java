package org.demoncode.portal.activity;

import org.demoncode.portal.App;
import org.demoncode.portal.network.WifiServiceHandler;
import org.demoncode.portal.util.IntentHelper;

import android.content.Intent;

public class ChatNetworkHandler implements WifiServiceHandler {

	private final String mFailMsg;
	
	public ChatNetworkHandler(String failMsg) {
		mFailMsg = failMsg;
	}
	
	@Override
	public void failed() {
		Intent intent = new Intent(AbstractConnActivity.FAILED);
		IntentHelper.store("ERROR", new Throwable(mFailMsg));
		App.getBroadcaster().sendBroadcast(intent);
	}

	@Override
	public void established() {
		Intent intent = new Intent(AbstractConnActivity.LINKED);
		App.getBroadcaster().sendBroadcast(intent);
	}

	@Override
	public void closed() {
		
	}

}
