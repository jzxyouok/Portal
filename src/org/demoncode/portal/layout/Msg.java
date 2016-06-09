package org.demoncode.portal.layout;

public class Msg {
	public static final int MSG_TYPE_RECEIVE = 0;
	public static final int MSG_TYPE_SEND = 1;
	
	private Object content;
	private int type;
	
	public Msg(Object content, int type){
		this.content = content;
		this.type = type;
	}

	public Object getContent() {
		return content;
	}

	public int getType() {
		return type;
	}
	
}
