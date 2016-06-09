package org.demoncode.portal.activity;

import org.demoncode.portal.App;
import org.demoncode.portal.R;
import org.demoncode.portal.audio.Player;
import org.demoncode.portal.audio.Recorder;
import org.demoncode.portal.network.MobileCM;
import org.demoncode.portal.network.WifiLinker;
import org.demoncode.portal.network.WifiProvider;
import org.demoncode.portal.network.WifiServiceHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity_kang extends Activity {
	
	private static final Logger logger = LoggerFactory.getLogger(MainActivity_kang.class);
	
	private class WifiLogHandler implements WifiServiceHandler {
		private final String TAG;
		public WifiLogHandler(String tag) {
			TAG = tag;
		}
		@Override
		public void failed() {
			Log.d(TAG, "failed");
		}
		
		@Override
		public void established() {
			Log.d(TAG, "established");
		}
		
		@Override
		public void closed() {
			Log.d(TAG, "closed");
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Button btnStartHosting = (Button) findViewById(R.id.btn_start_hosting);
		btnStartHosting.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity_kang.this, HostingActivity.class);
				intent.putExtra("SSID", "Xxx");
				startActivity(intent);
				logger.info("Host started.");
			}
		});
		
		Button btnStartConnecting = (Button) findViewById(R.id.btn_start_connecting);
		btnStartConnecting.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity_kang.this, ConnectingActivity.class);
				intent.putExtra("HOST", "192.168.31.102");
				startActivity(intent);
				logger.info("Connect started.");
			}
		});
		
		final WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		Button btnTest1 = (Button) findViewById(R.id.btn_test1);
		btnTest1.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Log.d("always", wifiManager.isScanAlwaysAvailable() + "");
				
				// THIS ACTIVITY: never seen before
				// Intent intent = new Intent(WifiManager.ACTION_REQUEST_SCAN_ALWAYS_AVAILABLE);
				// startActivity(intent);
				
				// wifiManager.setWifiEnabled(true);
				// wifiManager.startScan();
				// Log.d("RES", "n = " + wifiManager.getScanResults().size());
				// Log.d("INFO", Formatter.formatIpAddress(wifiManager.getDhcpInfo().serverAddress));
			}
		});
		
		WifiConfiguration conf = new WifiConfiguration();
		conf.SSID = "\"xx\"";
		conf.preSharedKey = "\"31415926\"";
		// conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
		
		final WifiLinker linker = new WifiLinker(conf);
		final Button btnTest2 = (Button) findViewById(R.id.btn_test2);
		final Button btnTest3 = (Button) findViewById(R.id.btn_test4);
		
		linker.setHandler(new WifiLogHandler("WIFI"));
		
		btnTest2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				btnTest2.setEnabled(false);
				btnTest3.setEnabled(true);
				linker.start();
			}
		});
		
		btnTest3.setEnabled(false);
		btnTest3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				btnTest3.setEnabled(false);
				linker.stop();
			}
		});
		
		final WifiConfiguration conf2 = new WifiConfiguration();
		conf2.SSID = "xxx";
		conf2.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
		
		final WifiProvider provider = new WifiProvider(conf2);
		provider.setHandler(new WifiLogHandler("-AP-"));
		
		Button btnTest5 = (Button) findViewById(R.id.btn_test5);
		Button btnTest6 = (Button) findViewById(R.id.btn_test6);
		btnTest5.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				provider.start();
			}
		});
		btnTest6.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.i("btn6", ".");
				provider.stop();
			}
		});
		Button btnTest10 = (Button) findViewById(R.id.btn_test10);
		btnTest10.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			}
		});
		
		final Recorder rec = new Recorder();
		
		Button btnRec = (Button) findViewById(R.id.btn_rec);
		Button btnPlay = (Button) findViewById(R.id.btn_play);
		
		btnRec.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					rec.start();
					return true;
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					rec.stop();
					Toast.makeText(MainActivity_kang.this, rec.getDuration() + " ms", Toast.LENGTH_LONG).show();
					return true;
				}
				return false;
			}
		});
		
		btnPlay.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new Player(rec.getData()).start();
			}
		});
		

		registerReceiver(recv3, new IntentFilter("android.net.wifi.WIFI_AP_STATE_CHANGED"));
		
		BroadcastReceiver recv = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				Log.d("RECV", "1");
				Log.d("RES", "n = " + wifiManager.getScanResults().size());
			}
		};
		// registerReceiver(recv, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
	}
	
	final BroadcastReceiver recv3 = new BroadcastReceiver() {
	    @Override
	    public void onReceive(Context context, Intent intent) { 
            int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0) % 10;
            Log.d("wifiAp", "status = " + state);

	    }
	};
	
	@Override
	protected void onDestroy() {
		unregisterReceiver(recv3);
		super.onDestroy();
	};
}
