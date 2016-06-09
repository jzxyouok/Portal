package org.demoncode.portal.util;

import java.util.HashMap;

public class IntentHelper {
	private static IntentHelper instance = new IntentHelper();
	private HashMap<String, Object> map = new HashMap<String, Object>();
	
	public static synchronized void store(String key, Object obj) {
		instance.map.put(key, obj);
	}
	
	public static synchronized Object fetch(String key) {
		Object data = instance.map.get(key);
		instance.map.remove(key);
		return data;
	}
}
