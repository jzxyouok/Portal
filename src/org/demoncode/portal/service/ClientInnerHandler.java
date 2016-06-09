package org.demoncode.portal.service;

import org.apache.mina.transport.socket.nio.NioSocketConnector;

public class ClientInnerHandler extends CommonHandler {
	
	public ClientInnerHandler(NioSocketConnector connector) {
		super(connector);
	}
}
