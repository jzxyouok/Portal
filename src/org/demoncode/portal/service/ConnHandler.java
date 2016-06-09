package org.demoncode.portal.service;

public interface ConnHandler {
	public void failed(Throwable ex);
	public void established(MySession session);
	public void messageReceived(Object message);
	public void closed(boolean passive);
}
