package org.demoncode.portal.activity;

import java.util.ArrayList;
import java.util.List;

import org.demoncode.portal.App;
import org.demoncode.portal.R;
import org.demoncode.portal.audio.PlayMonitor;
import org.demoncode.portal.audio.Player;
import org.demoncode.portal.audio.Recorder;
import org.demoncode.portal.debug.FakeMySession;
import org.demoncode.portal.layout.Msg;
import org.demoncode.portal.layout.MsgAdapter;
import org.demoncode.portal.model.AudioMsg;
import org.demoncode.portal.model.TextMsg;
import org.demoncode.portal.service.MySession;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

/*
 * @author Kang Qiao, Deng Xianmin
 */
public class ChatActivity extends Activity {

	public static final String MESSAGE = "org.demoncode.portal.MESSAGE";
	public static final String PASSIVELY_CLOSED = "org.demoncode.portal.PASSIVELY_CLOSED";
	
	private MySession mSession;
	private BroadcastReceiver mReceiver;
	private boolean mClosed;
	
	private ListView listView;
	private EditText inputText;
	private Button send;
	private MsgAdapter adapter;
	private List<Msg> msgList = new ArrayList<Msg>();
	
	private ImageView img_record, img_txt;
	private LinearLayout ll_record, ll_txt;
	
	Recorder recorder;
	private PlayMonitor mPlayMonitor;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        
        // DEBUG
        // IntentHelper.store("SESSION", new FakeMySession());
        
        mClosed = false;
		mSession = (MySession) IntentHelper.fetch("SESSION");
		
		final AlertDialog closedDialog = new AlertDialog.Builder(this)
				.setMessage("连接被对方关闭，将要退出。")
				.setPositiveButton("好的", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				})
				.setCancelable(false)
				.create();
		
		mReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				if (action == MESSAGE) {
					Object obj = IntentHelper.fetch("MSG" + intent.getIntExtra("NO", -1));
					writeMsg(obj, Msg.MSG_TYPE_RECEIVE);
				} else if (action == PASSIVELY_CLOSED) {
					mClosed = true;
					closedDialog.show();
				}
			}
		};
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(MESSAGE);
		filter.addAction(PASSIVELY_CLOSED);
		App.getBroadcaster().registerReceiver(mReceiver, filter);
        
		mPlayMonitor = new PlayMonitor();
		
        initMsg();
        adapter = new MsgAdapter(ChatActivity.this, R.layout.msg_item, msgList, mPlayMonitor);
        send = (Button)findViewById(R.id.send);
        inputText = (EditText)findViewById(R.id.input_text);
        listView = (ListView)findViewById(R.id.msg_list_view);
        listView.setAdapter(adapter);
        
        send.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String content = inputText.getText().toString();
				if (!"".equals(content)){
					TextMsg packet = new TextMsg(content);
					writeMsg(packet, Msg.MSG_TYPE_SEND);
					mSession.write(packet);
					inputText.setText("");
				}
			}
		});
        
        Button btnRecord = (Button) findViewById(R.id.btn_record);
        btnRecord.setOnTouchListener(new View.OnTouchListener() {
        	
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					Toast.makeText(ChatActivity.this, "录音开始", Toast.LENGTH_SHORT).show();
					mPlayMonitor.stopCurrent();
					recorder = new Recorder();
					recorder.start();
					Log.d("INFO", "DOWN");
					return true;
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					v.performClick();
					recorder.stop();
					Log.d("INFO", "CLICK-preend");
					AudioMsg packet = new AudioMsg(recorder.getData(), recorder.getDuration());
					recorder = null;
					Toast.makeText(ChatActivity.this, "录音结束", Toast.LENGTH_SHORT).show();
					writeMsg(packet, Msg.MSG_TYPE_SEND);
					mSession.write(packet);
					return true;
				}
				return false;
			}
		});
        
        ll_record = (LinearLayout) findViewById(R.id.ll_bottom2);//录音界面的按钮操作布局
		ll_txt = (LinearLayout) findViewById(R.id.ll_bottom);//文字界面的按钮操作布局
		
		img_txt = (ImageView) findViewById(R.id.iv_txt);
		img_record = (ImageView) findViewById(R.id.iv_record); //切换录音的按钮
		
		final View.OnClickListener switchListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (v == img_txt) {
					ll_txt.setVisibility(View.VISIBLE);
					ll_record.setVisibility(View.GONE);
				} else if (v == img_record) {
					ll_txt.setVisibility(View.GONE);
					ll_record.setVisibility(View.VISIBLE);
				}
			}
		};
		
		img_txt.setOnClickListener(switchListener);
		img_record.setOnClickListener(switchListener);
    }
    
    public void initMsg(){
//    	Msg msg1 = new Msg("hello", Msg.MSG_TYPE_RECEIVE);
//    	msgList.add(msg1);
//    	Msg msg2 = new Msg("ni hao", Msg.MSG_TYPE_SEND);
//    	msgList.add(msg2);
//    	Msg msg3 = new Msg("what are you doing", Msg.MSG_TYPE_RECEIVE);
//    	msgList.add(msg3);
    }
    
    protected void writeMsg(Object data, int msgType) {
    	Msg msg = new Msg(data, msgType);
    	msgList.add(msg);
    	adapter.notifyDataSetChanged();
		listView.setSelection(msgList.size());	//将列表的数据定位到最后一行
    }
    
	@Override
	protected void onDestroy() {
		mPlayMonitor.stopCurrent();
		App.getBroadcaster().unregisterReceiver(mReceiver);
		
		if (!mClosed)
			mSession.close();
		super.onDestroy();
	}
	
	@Override
	public void onBackPressed() {
		new AlertDialog.Builder(this)
		.setMessage("将会中断与对方的连接。")
		.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		})
		.setNegativeButton("取消", null)
		.create()
		.show();
	}
}
