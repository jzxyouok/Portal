package org.demoncode.portal.debug;

import org.demoncode.portal.service.MySession;

import android.util.Log;

public class FakeMySession extends MySession {

	@Override
	public void write(Object o) {
		Log.d("MySession", "write: " + o.toString());
	}

	@Override
	public void close() {
		Log.d("MySession", "closed.");
	}

}
