package com.lz217_yq47.SOS.activity;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class LockService extends Service implements MediaControl{
	private static final boolean DBG = true;
	private static final String TAG = "FxLockService";
	private Intent mFxLockIntent = null;
	private KeyguardManager mKeyguardManager = null ;
	private KeyguardManager.KeyguardLock mKeyguardLock = null ;
	
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate() 
	{
		// TODO Auto-generated method stub
		super.onCreate();
		Log.d(TAG, "-->onCreate()");
		if (DBG)Log.d(TAG, "-->onCreate()");
		mFxLockIntent = new Intent(LockService.this, RunningActivity.class);
		mFxLockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		registerComponent();
		Log.d(TAG, "1");
		mKeyguardManager = (KeyguardManager)getSystemService(Context.KEYGUARD_SERVICE);
		mKeyguardLock = mKeyguardManager.newKeyguardLock("FxLock");
		mKeyguardLock.disableKeyguard();
		Log.d(TAG, "SCREEN_ON_disableKeyguard");
//		startActivity(mFxLockIntent);
		
		
	}
	
	
	
	@Override
	public void onStart(Intent intent, int startId)
	{
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		if(DBG) Log.d(TAG, "-->onStart()");
	}



	@Override
	public void onDestroy() 
	{
		// TODO Auto-generated method stub
		super.onDestroy();
		if(DBG) Log.d(TAG, "-->onDestroy()");
		unregisterComponent();
		startService(new Intent(LockService.this, LockService.class));
	}
	
	@Override
	public IBinder onBind(Intent arg0)
	{
		// TODO Auto-generated method stub
		if(DBG) Log.d(TAG, "-->onBind()");
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		if(DBG) Log.d(TAG, "-->onStartCommand()");
		return Service.START_STICKY;
	}
	
	private BroadcastReceiver mScreenOnOrOffReceiver = new BroadcastReceiver() {
		
		@SuppressWarnings("deprecation")
		@Override
		public void onReceive(Context context, Intent intent)
		{
			if(DBG) Log.d(TAG, "mScreenOffReceiver-->" + intent.getAction());
			
			if (intent.getAction().equals("android.intent.action.SCREEN_ON")
					|| intent.getAction().equals("android.intent.action.SCREEN_OFF"))
//			if(intent.getAction().equals("android.intent.action.SCREEN_ON"))
			{
				mKeyguardManager = (KeyguardManager)context.getSystemService(Context.KEYGUARD_SERVICE);
				mKeyguardLock = mKeyguardManager.newKeyguardLock("FxLock");
				mKeyguardLock.disableKeyguard();
				Log.d(TAG, "SCREEN_ON_disableKeyguard");
				startActivity(mFxLockIntent);
//				Log.d(TAG, "3");
			}	
		}
	};
	

	
	@Override
	public void connectMediaService()
	{
		// TODO Auto-generated method stub
		//MainActivity.mStatusViewManager.connectMediaService();
	}

	@Override
	public void registerComponent()
	{
		// TODO Auto-generated method stub
		if(DBG) Log.d(TAG, "registerComponent()");
		IntentFilter mScreenOnOrOffFilter = new IntentFilter();
		Log.d(TAG, "9");
		mScreenOnOrOffFilter.addAction("android.intent.action.SCREEN_ON");
		mScreenOnOrOffFilter.addAction("android.intent.action.SCREEN_OFF");
		Log.d(TAG, "10");
		mScreenOnOrOffFilter.addAction(LockView.SHOW_MUSIC);
		LockService.this.registerReceiver(mScreenOnOrOffReceiver, mScreenOnOrOffFilter);
		Log.d(TAG, "11");

	}

	@Override
	public void unregisterComponent() 
	{
		// TODO Auto-generated method stub
		if(DBG) Log.d(TAG, "unregisterComponent()");
		if (mScreenOnOrOffReceiver != null)
		{
			LockService.this.unregisterReceiver(mScreenOnOrOffReceiver);
			Log.d(TAG, "12");
		}
	}

	@Override
	public void initViews() {
		// TODO Auto-generated method stub
		Log.d(TAG, "13");
		
	}

}
