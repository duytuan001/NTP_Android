package com.duytuan.ntptimesync;

import java.util.Calendar;

import android.app.Application;
import android.content.SharedPreferences;

public class Settings extends Application{
	public static final String PREFS_NAME_KEY		= "com.duytuan.ntptime.prefs_name";
	public static final String NTP_SERVER_INDEX_KEY = "com.duytuan.ntptime.prefs.ntp_server_name";
	public static final String TIMER_KEY 			= "com.duytuan.ntptime.prefs.timer";
	public static final String TYPE_CLOCK_KEY		= "com.duytuan.ntptime.type_clock";
	public static final CharSequence[] ntpServerList= 
		{"0.pool.ntp.org", "ntp0.ox.ac.uk", "pool.ntp.org", 
		"2.vn.pool.ntp.org", "asia.pool.ntp.org","ntp.pads.ufrj.br"};
	
	private boolean analogClock; 	//true:analog or false:digital
	private int timer; 				//minutes to sync
	private int ntpServerIndex; 	//index of NTP server address
	private Calendar lastUpdated; 	//last sync datetime
	
	private SharedPreferences settings;
	
	@Override
	public void onCreate(){
		super.onCreate();
		this.settings = getSharedPreferences(PREFS_NAME_KEY, 0);
	}
	
	public String getNtpServerName() {
		return ntpServerList[getNtpServerIndex()].toString();
	}
	
	public int getNtpServerIndex(){
		this.ntpServerIndex = settings.getInt(NTP_SERVER_INDEX_KEY, 0);
		return this.ntpServerIndex;
	}

	public void setNtpServerIndex(int index) {
		SharedPreferences.Editor editor = settings.edit();
        editor.putInt(NTP_SERVER_INDEX_KEY, index);
        editor.commit();
		this.ntpServerIndex = index;
	}

	public int getTimer() {
		this.timer = settings.getInt(TIMER_KEY, 10);
		return timer;
	}

	public void setTimer(int timer) {
		SharedPreferences.Editor editor = settings.edit();
        editor.putInt(TIMER_KEY, timer);
        editor.commit();
		this.timer = timer;
	}
	
	public boolean isAnalogClock() {
		this.analogClock = settings.getBoolean(TYPE_CLOCK_KEY, true);
		return this.analogClock;
	}

	public void setAnalogClock(boolean clock) {
		SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(TYPE_CLOCK_KEY, clock);
        editor.commit();
		this.analogClock = clock;
	}

	public Calendar getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Calendar lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
	
}
