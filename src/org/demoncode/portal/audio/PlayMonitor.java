package org.demoncode.portal.audio;

public class PlayMonitor {
	private Player mPlayer;
	
	public void accept(Player player) {
		stopCurrent();
		mPlayer = player;
		player.start();
	}
	
	public void stopCurrent() {
		if (mPlayer != null) {
			mPlayer.stop();
			mPlayer = null;
		}
	}
}
