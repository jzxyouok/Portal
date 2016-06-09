package org.demoncode.portal.service;

import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

public class ServerInnerHandler extends CommonHandler {
	public ServerInnerHandler(NioSocketAcceptor acceptor) {
		super(acceptor);
	}
	
	@Override
	protected void opened() {
		((NioSocketAcceptor) mService).unbind();
	}
}
