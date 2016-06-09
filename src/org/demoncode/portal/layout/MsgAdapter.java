package org.demoncode.portal.layout;

import java.util.List;

import org.demoncode.portal.R;
import org.demoncode.portal.audio.PlayMonitor;
import org.demoncode.portal.audio.Player;
import org.demoncode.portal.model.AudioMsg;
import org.demoncode.portal.model.TextMsg;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MsgAdapter extends ArrayAdapter<Msg> {

	private PlayMonitor mPlayMonitor;
	private int resourceId;
	private ViewHolder viewHolder;
	public int mLastViewIndex;
	
	public MsgAdapter(Context context, int resource, List<Msg> objects, PlayMonitor playMonitor) {
		super(context, resource, objects);
		resourceId = resource;
		mPlayMonitor = playMonitor;
		mLastViewIndex = objects.size() - 1;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Msg msg;
		View view;
		msg = getItem(position);
		if (convertView == null){
			view = LayoutInflater.from(getContext()).inflate(resourceId, null);
			viewHolder = new ViewHolder();
			viewHolder.leftLayout = (LinearLayout)view.findViewById(R.id.left_layout);
			viewHolder.rightLayout = (LinearLayout)view.findViewById(R.id.right_layout);
			viewHolder.leftMsg = (TextView)view.findViewById(R.id.left_msg);
			viewHolder.rightMsg = (TextView)view.findViewById(R.id.right_msg);
			view.setTag(viewHolder);
		}else{
			view = convertView;
			viewHolder = (ViewHolder)view.getTag(); 
		}
		
		TextView containerView = null;
		if (msg.getType() == Msg.MSG_TYPE_SEND){
			viewHolder.rightLayout.setVisibility(View.VISIBLE);
			viewHolder.leftLayout.setVisibility(View.GONE);
			containerView = viewHolder.rightMsg;
		} else if (msg.getType() == Msg.MSG_TYPE_RECEIVE){
			viewHolder.rightLayout.setVisibility(View.GONE);
			viewHolder.leftLayout.setVisibility(View.VISIBLE);
			containerView = viewHolder.leftMsg;
		}
		
		Object obj = msg.getContent();
		if (obj instanceof TextMsg) {
			containerView.setText(((TextMsg) obj).getText());
			containerView.setOnClickListener(null);
		} else { // AudioMsg
			// 已经用setHasTransistentState保证了正在播放动画的view不会被重用
			// 所以此处的使用的view都没有在播放动画，不必担心破坏
			final AudioMsg audioMsg = (AudioMsg) obj;
			containerView.setText(getAudioText(audioMsg));
			
			final View parentView = view;
			final TextView textView = containerView;
			final Runnable playThis = new Runnable() {
				@Override
				public void run() {
					View_setHasTransistentState(parentView, true);
					
					final PlayAnimThread animThread = new PlayAnimThread(textView);
					
					Player player = new Player(audioMsg.getData());
					player.setOnStopListener(new Player.OnStopListener() {
						@Override
						public void stopped() {
							parentView.post(new Runnable() {
								@Override
								public void run() {
									animThread.end();
									textView.setText(getAudioText(audioMsg));
									View_setHasTransistentState(parentView, false);
								};
							});
						}
					});
					mPlayMonitor.accept(player);
					animThread.start();
				}
			};
			
			containerView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					playThis.run();
				}
			});
			
			if (msg.getType() == Msg.MSG_TYPE_RECEIVE && position > mLastViewIndex) {
				mLastViewIndex = position;
				playThis.run();
			}
		}
		
		return view;
	}
	
	private String getAudioText(AudioMsg msg) {
		return "[音频] " + (msg.getDuration() + 999) / 1000 + "\"";
	}
	
	private void View_setHasTransistentState(View view, boolean b) {
		 android.support.v4.view.ViewCompat.setHasTransientState(view, b);
	}
	
	private class PlayAnimThread extends Thread {
		public boolean isRunning = true;
		private int n = 0;
		private TextView tvText;
		
		public PlayAnimThread(TextView tv) {
			tvText = tv;
		}
		@Override
		public void run() {
			while (isRunning) {
				int i = n;
				final StringBuilder sb = new StringBuilder();
				while (i-- > 0) {
					sb.append('.');
				}
				// lock until finish?
				tvText.post(new Runnable() {
					@Override
					public void run() {
						tvText.setText("playing" + sb.toString());
					}
				});
				n = (n + 1) % 4;
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					break;
				}
			}
		}
		
		public void end() {
			isRunning = false;
			interrupt();
		}
	}

	class ViewHolder{
		
		LinearLayout leftLayout;
		LinearLayout rightLayout;
		TextView leftMsg;
		TextView rightMsg;
	}
}
