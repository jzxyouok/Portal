package org.demoncode.portal.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.demoncode.portal.R;
import org.demoncode.portal.layout.EquipAdapter;
import org.demoncode.portal.network.ApWM;
import org.demoncode.portal.network.MobileCM;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/*
 * Main UI.
 * 
 * @author Deng Xianmin, Kang Qiao
 */
public class EntranceActivity extends Activity{

	private ListView equip_list_view;
	private EquipAdapter equipAdapter;
    private Button btn_search;
    private ImageView imageView;
    private Button btn_server;
	private RelativeLayout rl_search;
	
	private boolean scaning = false;
	private BroadcastReceiver scanRecv;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_entrance);
		btn_search = (Button)findViewById(R.id.btn_search);
		btn_server = (Button)findViewById(R.id.btn_service);
		rl_search = (RelativeLayout) findViewById(R.id.rl_search);
		imageView = (ImageView) findViewById(R.id.radar_imageing);
		
		final Fragment fDevList = (Fragment) getFragmentManager().findFragmentById(R.id.f_devlist);
		final Fragment fApConfig = (Fragment) getFragmentManager().findFragmentById(R.id.f_apconfig);
		
		getFragmentManager()
			.beginTransaction()
			.hide(fApConfig)
			.commit();
		
		btn_search.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				getFragmentManager()
					.beginTransaction()
					.show(fDevList).hide(fApConfig)
					.commit();
				
				View view = getCurrentFocus();
				if (view != null) {
				    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
				}
				
				scanWifi();
			}
		});
		btn_server.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				getFragmentManager()
				.beginTransaction()
				.show(fApConfig).hide(fDevList)
				.commit();
				
				stopScan();
			}
		});
		
		equip_list_view = (ListView) findViewById(R.id.lst_equip);
		equip_list_view.setClickable(true);
		equip_list_view.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View item, int position, long id) {
				EquipAdapter.EquipListViewItem tagItem =
						(EquipAdapter.EquipListViewItem) item.getTag();
				
				Intent intent = new Intent(EntranceActivity.this,ConnectingActivity.class);
				intent.putExtra("BSSID", tagItem.BSSID);
				intent.putExtra("SSID", tagItem.SSID);
				startActivity(intent);
				stopScan();
			}
		});
		
		Button btnStartHotSpot = (Button) findViewById(R.id.btn_start_hotspot);
		btnStartHotSpot.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startHosting();
			}
		});
	}
	
	private void startHosting() {
		TextView tvSSID = (TextView) findViewById(R.id.tv_ssid);
		String ssid = tvSSID.getText().toString();
		Intent intent = new Intent(EntranceActivity.this, HostingActivity.class);
		intent.putExtra("SSID", ssid);
		startActivity(intent);
	}
	
	@Override
	protected void onDestroy() {
		stopScan();
		super.onDestroy();
	}
	
	private void searchAnim(){
		rl_search.setVisibility(View.VISIBLE);
		Animation animation = AnimationUtils.loadAnimation(this, R.anim.sss);
		LinearInterpolator lin = new LinearInterpolator();
		animation.setInterpolator(lin);
		imageView.startAnimation(animation);
	}
	
	private void stopScan(){
		if (scaning) {
			scaning = false;
			unregisterReceiver(scanRecv);
			imageView.clearAnimation();
			rl_search.setVisibility(View.GONE);
		}
	}
	
	private void scanWifi(){
		if (scaning)
			return;
		
		final WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
		scanRecv = new BroadcastReceiver() {
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				stopScan();
				
				List<ScanResult> result = wm.getScanResults();
 
				equipAdapter = new EquipAdapter(EntranceActivity.this, result); //¥¥Ω®  ≈‰∆˜
				equip_list_view.setAdapter((ListAdapter) equipAdapter);
			}
		};
		ApWM.setWifiApEnabled(ApWM.getWifiApConfiguration(), false);
		wm.setWifiEnabled(true);
		registerReceiver(scanRecv, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		scaning = true;
		wm.startScan();
		searchAnim();
	}
}
