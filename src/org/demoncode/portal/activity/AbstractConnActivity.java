package org.demoncode.portal.activity;

import java.net.InetSocketAddress;

import org.demoncode.portal.App;
import org.demoncode.portal.R;
import org.demoncode.portal.network.WifiService;
import org.demoncode.portal.service.Client;
import org.demoncode.portal.service.MyService;
import org.demoncode.portal.util.IntentHelper;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public abstract class AbstractConnActivity extends Activity implements View.OnClickListener {
	public static final String FAILED = "org.demoncode.portal.FAILED";
	public static final String LINKED = "org.demoncode.portal.LINKED";
	public static final String ESTABLISHED = "org.demoncode.portal.ESTABLISHED";
	
	protected WifiService mWifiServ;
	protected MyService mMyServ;
	
	private BroadcastReceiver receiver;
	protected boolean linked, connected;
	protected TextView tvPrompt;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_connecting);
		
		tvPrompt = (TextView) findViewById(R.id.tv_prompt);

		Button btnCancel = (Button) findViewById(R.id.btn_cancel);
		btnCancel.setOnClickListener(this);
		
		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				if (FAILED.equals(action)) {
					onFailed();
				} else if(LINKED.equals(action)) {
					linked = true;
					onLinked();
				} else if (ESTABLISHED.equals(action)) {
					connected = true;
					onEstablished();
				}
			}
		};
		IntentFilter filter = new IntentFilter();
		filter.addAction(ESTABLISHED);
		filter.addAction(LINKED);
		filter.addAction(FAILED);
		App.getBroadcaster().registerReceiver(receiver, filter);
		
		linked = false;
		connected = false;
		postCreate();
		
		tvPrompt.setText(getOuterMessage());
		mWifiServ = createWifiService();
		mWifiServ.start();
	}
	
	@Override
	protected void onDestroy() {
		cleanup();
		super.onDestroy();
	}
	
	protected abstract void postCreate();
	
	protected void cleanup() {
		if (!connected) {
			if (linked)
				mMyServ.stop();
			mWifiServ.stop();
		}
		App.getBroadcaster().unregisterReceiver(receiver);
	}
	
	public void onFailed() {
		final Throwable e = (Throwable) IntentHelper.fetch("ERROR");
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				new AlertDialog.Builder(AbstractConnActivity.this)
				.setMessage("Ê§°Ü£º\n" + e.toString())
				.setPositiveButton("È·¶¨", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				})
				.setCancelable(false)
				.create()
				.show();
			}
		});
	}
	
	protected void onLinked() {
		tvPrompt.setText(getInnerMessage());
		
		mMyServ = createService();
		mMyServ.start();
	}
	
	public void onEstablished() {
		IntentHelper.store("WIFI_SERV", mMyServ);
		
		Intent intent = new Intent(this, ChatActivity.class);
		startActivity(intent);
		finish();
	}
	
	protected abstract String getOuterMessage();
	protected abstract String getInnerMessage();
	
	protected abstract WifiService createWifiService(); 
	protected abstract MyService createService();
	
	@Override
	public void onClick(View v) {
		finish();
	}
}
