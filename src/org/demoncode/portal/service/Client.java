package org.demoncode.portal.service;

import java.net.SocketAddress;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

public class Client extends MyService {
	private SocketAddress mRemoteAddr;
	private NioSocketConnector mConnector;
	private ClientInnerHandler mHandler;
	private ConnectFuture mConnectFuture;
	
	public Client(SocketAddress remoteAddr) {
		mRemoteAddr = remoteAddr;
		
		mConnector = new NioSocketConnector();
		mHandler = new ClientInnerHandler(mConnector);
		
		mConnector.setConnectTimeoutMillis(5000);
		mConnector.getFilterChain().addLast(
			"codec",
			new ProtocolCodecFilter(
					new ObjectSerializationCodecFactory()));
		mConnector.getFilterChain().addLast("logger", new LoggingFilter());
		mConnector.setHandler(mHandler);
	}
	
	public void start0() {
		mConnectFuture = mConnector.connect(mRemoteAddr);
		mConnectFuture.addListener(new IoFutureListener<IoFuture>() {
			@Override
			public void operationComplete(IoFuture f) {
				// note: this method could be executed in NIO thread
				//       as well as in the thread calling start()
				ConnectFuture cf = (ConnectFuture) f;
				if (!cf.isConnected()) {
					if (!cf.isCanceled())
						mHandler.getDelegateHandler().failed(cf.getException());
					mConnector.dispose();
				}
			}
		});
	}
	
	@Override
	public void start() {
		// why???
		new Thread() {
			@Override public void run() {
				start0();
			}
		}.start();
	}
	
	@Override
	public void stop() {
		// note: if connection hasn't be established, setting passive here is useless
		mHandler.setPassive(false);
		mConnectFuture.cancel();
		mConnector.dispose();
	}
	
	public void setHandler(ConnHandler handler) {
		mHandler.setDelegateHandler(handler);
	}
}
