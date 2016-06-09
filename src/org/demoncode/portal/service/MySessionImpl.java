package org.demoncode.portal.service;

import org.apache.mina.core.session.IoSession;

public class MySessionImpl extends MySession {
	private CommonHandler mHandler;
	private IoSession mSession;
	
	public MySessionImpl(CommonHandler handler, IoSession session) {
		mHandler = handler;
		mSession = session;
	}
	
	@Override
	public void write(Object o) {
		mSession.write(o);
	}
	
	@Override
	public void close() {
		mHandler.setPassive(false);
		mSession.closeNow();
	}
}
