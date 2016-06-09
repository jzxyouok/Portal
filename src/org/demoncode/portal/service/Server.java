package org.demoncode.portal.service;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import android.util.Log;

public class Server extends MyService {
	private NioSocketAcceptor mAcceptor;
	private ServerInnerHandler mHandler;
	private int mPort;
	
	public Server(int port) {
		mAcceptor = new NioSocketAcceptor();
		mHandler = new ServerInnerHandler(mAcceptor);
		mPort = port;
		
		mAcceptor.getFilterChain().addLast(
				"codec",
				new ProtocolCodecFilter(
						new ObjectSerializationCodecFactory()));
		mAcceptor.getFilterChain().addLast("logger", new LoggingFilter());
		mAcceptor.setHandler(mHandler);
		mAcceptor.setCloseOnDeactivation(false);
	}
	
	@Override
	public void start() {
		try {
			mAcceptor.bind(new InetSocketAddress(mPort));
		} catch(IOException ex) {
			mAcceptor.dispose();
			mHandler.getDelegateHandler().failed(ex);
		}
	}
	
	@Override
	public void stop() {
		mHandler.setPassive(false);
		mAcceptor.dispose();
	}
	
	public void setHandler(ConnHandler handler) {
		mHandler.setDelegateHandler(handler);
	}
}
