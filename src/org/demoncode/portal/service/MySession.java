package org.demoncode.portal.service;

public abstract class MySession {
	public abstract void write(Object o);
	public abstract void close();
}
