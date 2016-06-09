package org.demoncode.portal.activity;

import org.demoncode.portal.App;
import org.demoncode.portal.network.WifiService;
import org.demoncode.portal.service.ConnHandler;
import org.demoncode.portal.service.MySession;
import org.demoncode.portal.util.IntentHelper;

import android.content.Intent;

public class ChatSessionHandler implements ConnHandler {
	
	private int messageNo = 0;
	
	private WifiService mBoundServ;
	
	public ChatSessionHandler(WifiService wifiServ) {
		mBoundServ = wifiServ;
	}

	@Override
	public void established(MySession session) {
		Intent intent = new Intent(AbstractConnActivity.ESTABLISHED);
		IntentHelper.store("SESSION", session);
		App.getBroadcaster().sendBroadcast(intent);
	}
	
	@Override
	public void failed(Throwable ex) {
		Intent intent = new Intent(AbstractConnActivity.FAILED);
		IntentHelper.store("ERROR", ex);
		App.getBroadcaster().sendBroadcast(intent);
	}

	@Override
	public void messageReceived(Object message) {
		Intent intent = new Intent(ChatActivity.MESSAGE);
		int no = messageNo++;
		IntentHelper.store("MSG" + no, message);
		intent.putExtra("NO", no);
		App.getBroadcaster().sendBroadcast(intent);
	}

	@Override
	public void closed(boolean passive) {
		if (passive) {
			Intent intent = new Intent(ChatActivity.PASSIVELY_CLOSED);
			App.getBroadcaster().sendBroadcast(intent);
		}
		mBoundServ.stop();
	}
}
