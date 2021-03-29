package ru.overscan.lib.sys;

import android.app.Application;
import ru.overscan.lib.data.JsonUtils;

abstract public class OverscanApplication extends Application {
	private static OverscanApplication instance;	   
	public static OverscanApplication getInstance() { return instance; }
	
	public static String access_token;
	public static String serverUrl = "http://192.168.1.3:3000";
	
	public String userName;
	public int userCode;
	public String userLocality = "9b968c73-f4d4-4012-8da8-3dacd4d4c1bd";

	
	public void onCreate() {
    	super.onCreate();
		instance = this;
		  
		JsonUtils.initialize(this);
    }
	
}
