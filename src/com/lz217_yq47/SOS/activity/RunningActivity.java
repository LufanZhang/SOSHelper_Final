/*
 * this class is created by Yubo
 */
package com.lz217_yq47.SOS.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

import com.lz217_yq47.SOS.R;

public class RunningActivity extends Activity{
	 private static final boolean DBG = true;
	 private static final String TAG = "RunningActivity";
	 public static final int MSG_LAUNCH_HOME = 0;
	 public static final int MSG_LAUNCH_DIAL = 1;
	 public static final int MSG_LAUNCH_SMS = 2;
	 public static final int MSG_LAUNCH_SOS = 3;
	 
	 public static final int FLAG_HOMEKEY_DISPATCHED = 0x00000003 ; //for block home key 0x80000000
	 
	 private LockView mLockView;
	 public static StatusViewManager mStatusViewManager;
	 
	 /** Called when the activity is first created. */
	    @Override
	    public void onCreate(Bundle savedInstanceState) 
	    {
	        super.onCreate(savedInstanceState);
	        if(DBG) Log.d(TAG, "Running_onCreate()");
	        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					   WindowManager.LayoutParams.FLAG_FULLSCREEN);
			 setContentView(R.layout.activity_running_activity);
			  this.getWindow().setFlags(FLAG_HOMEKEY_DISPATCHED, FLAG_HOMEKEY_DISPATCHED);//to block home key     
	       
	        
	        initViews();
	        mStatusViewManager = new StatusViewManager(this, this.getApplicationContext());
	        mLockView.setMainHandler(mHandler);
	    }
	     
	    private Handler mHandler = new Handler()
	    {

			@Override
			public void handleMessage(Message msg) 
			{
				// TODO Auto-generated method stub
				switch (msg.what) 
				{
				case MSG_LAUNCH_HOME:
					launchHome();
					finish();
					break;
				case MSG_LAUNCH_SMS:
					launchSms();
					finish();
					break;
				case MSG_LAUNCH_DIAL:
					launchDial();
					finish();
					break;
				case MSG_LAUNCH_SOS:
					launchSOS();
					launchHome();
					finish();
					break;
				}
			}
	    	
	    };
	    
	    private void launchSms() {

			//mFocusView.setVisibility(View.GONE);
			Intent intent = new Intent();
			ComponentName comp = new ComponentName("com.android.mms",
					"com.android.mms.ui.ConversationList");
			intent.setComponent(comp);
			intent.setAction("android.intent.action.VIEW");
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
			startActivity(intent);
		}
	    
	    private void launchDial() {
			Intent intent = new Intent(Intent.ACTION_DIAL);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
			startActivity(intent);
		}
	    
	    private void launchCamera() {
			Intent intent = new Intent();
			ComponentName comp = new ComponentName("com.android.camera",
					"com.android.camera.Camera");
			intent.setComponent(comp);
			intent.setAction("android.intent.action.VIEW");
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
			startActivity(intent);
		}
	    //Start SOS service
	    private void launchSOS(){
	    	Intent SOSService = new Intent();
			SOSService.setClass(RunningActivity.this,SOSService.class);
			startService(SOSService);
	    }
	    
	    //Launch home screen of Android
	   private void launchHome(){
		   Intent homeIntent= new Intent(Intent.ACTION_MAIN);
		   homeIntent.addCategory(Intent.CATEGORY_HOME);
		   homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		   startActivity(homeIntent);
	   }


	   @Override
		public boolean onKeyDown(int keyCode, KeyEvent event) {
			// TODO Auto-generated method stub
			return disableKeycode(keyCode, event);
		}
	    
	    private boolean disableKeycode(int keyCode, KeyEvent event)
	    {
	    	int key = event.getKeyCode();
	    	switch (key)
	    	{
			case KeyEvent.KEYCODE_BACK:	
				 Log.d(TAG,"back key");
				 return true;	
				 
			case KeyEvent.KEYCODE_VOLUME_DOWN:
				Log.d(TAG,"volume_down key");
				return true;	
			case KeyEvent.KEYCODE_VOLUME_UP:
				Log.d(TAG,"volume_up key");
				return true;	
			case KeyEvent.KEYCODE_HOME:  //BLOCK HOME KEY
				Log.d(TAG,"home key");
				return true;	
						
			}
	    	return super.onKeyDown(keyCode, event);
	    }

		@Override
		protected void onDestroy() {
			// TODO Auto-generated method stub
			super.onDestroy();
			if(DBG) Log.d(TAG, "onDestroy()");
			
		}

		@Override
		protected void onResume() 
		{
			// TODO Auto-generated method stub
			super.onResume();
			if(DBG) Log.d(TAG, "onResume()");
		}
		
		protected void onStop() {
			super.onStop();
			if(DBG) Log.d(TAG, "onStop()");
		}

		@Override
		public void onDetachedFromWindow() 
		{
			// TODO Auto-generated method stub
			super.onDetachedFromWindow();
			if(DBG) Log.d(TAG, "onDetachedFromWindow()");
		    mStatusViewManager.unregisterComponent();
		}
	    
		public void initViews() 
		{
			// TODO Auto-generated method stub
			mLockView = (LockView) findViewById(R.id.FxView);
		}
}
