package org.demoncode.portal.layout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.demoncode.portal.R;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

public class EquipAdapter extends BaseAdapter{
	
	private LayoutInflater listInflater;
	private List<ScanResult> itemList;
	
	public final class EquipListViewItem{
		public TextView equip_name;
		public ImageButton equip_signal;
		public TextView link_state;
		public String BSSID;
		public String SSID;
	}
	
	@SuppressWarnings("static-access")
	public EquipAdapter(Context context, List<ScanResult> itemList) {
		super();
		listInflater = listInflater.from(context);
		this.itemList = itemList;
	}

	@Override
	public int getCount() {
		return itemList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return itemList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//convertView = View.inflate(context, R.layout., root)
		//final int id = position;
		EquipListViewItem equipListViewItem = null;
		if (null==convertView) {
			equipListViewItem = new EquipListViewItem();
			convertView = listInflater.inflate(R.layout.equip_item, null);
			equipListViewItem.equip_name = (TextView)convertView.findViewById(R.id.equip_name);
			equipListViewItem.equip_signal = (ImageButton)convertView.findViewById(R.id.equip_singal);
			equipListViewItem.link_state = (TextView)convertView.findViewById(R.id.link_state);
			convertView.setTag(equipListViewItem); 
		}else {
			equipListViewItem = (EquipListViewItem)convertView.getTag();  
		}
		//设置图片和文字
		equipListViewItem.equip_name.setText(itemList.get(position).SSID);
		equipListViewItem.equip_signal.setBackgroundResource(R.drawable.wifi3);
		equipListViewItem.link_state.setText("未连接");
		equipListViewItem.BSSID = itemList.get(position).BSSID;
		equipListViewItem.SSID = itemList.get(position).SSID;
		
		return convertView;
	}

	
}
