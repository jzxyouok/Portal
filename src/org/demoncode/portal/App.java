package org.demoncode.portal;

import org.demoncode.portal.util.LogUtils;

import android.app.Application;
import android.content.Context;
import android.support.v4.content.LocalBroadcastManager;

public class App extends Application {

    private static Context context;
    private static LocalBroadcastManager broadcaster;

    @Override
    public void onCreate() {
        super.onCreate();
        App.context = getApplicationContext();
        App.broadcaster = LocalBroadcastManager.getInstance(context);
        
        LogUtils.configLog4j();
    }

    public static Context getContext() {
        return App.context;
    }
    
    public static LocalBroadcastManager getBroadcaster() {
    	return App.broadcaster;
    }
}
