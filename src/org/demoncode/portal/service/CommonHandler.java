package org.demoncode.portal.service;

import org.apache.mina.core.service.AbstractIoService;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

public abstract class CommonHandler extends IoHandlerAdapter {
	protected ConnHandler mHandler;
	protected boolean mPassive = true;
	protected AbstractIoService mService;
	
	public CommonHandler(AbstractIoService service) {
		mService = service;
	}
	
	public void setDelegateHandler(ConnHandler handler) {
		mHandler = handler;
	}
	public ConnHandler getDelegateHandler() {
		return mHandler;
	}
	
	public void setPassive(boolean passive) {
		mPassive = passive;
	}
	
	protected void opened() { }
	
	@Override
	public void sessionOpened(IoSession session) throws Exception {
		opened();
		mHandler.established(new MySessionImpl(this, session));
	}
	
	@Override
	public void sessionClosed(IoSession session) throws Exception {
		mHandler.closed(mPassive);
		mService.dispose();
	}
	
	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		cause.printStackTrace();
	}
	
	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		mHandler.messageReceived(message);
	}
}
