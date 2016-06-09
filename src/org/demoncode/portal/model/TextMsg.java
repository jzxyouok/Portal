package org.demoncode.portal.model;

import java.io.Serializable;

public class TextMsg implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7319227303566348681L;
	
	private final String mText;
	
	public TextMsg(String text) {
		mText = text;
	}
	
	public String getText() {
		return mText;
	}
}
