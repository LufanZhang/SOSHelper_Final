/*
 * this class was created by Lufan: 11/20/2014
 */
package com.lz217_yq47.SOS.activity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.easemob.util.VoiceRecorder;
import com.lz217_yq47.SOS.R;
import com.lz217_yq47.SOS.adapter.MessageAdapter;
import com.lz217_yq47.SOS.adapter.VoicePlayClickListener;
import com.lz217_yq47.SOS.utils.CommonUtils;




public class SOSService extends Service{
	private String TAG = "SOSService";  
    private Timer voiceTimer = null, locationTimer = null;  
    private Intent emergencyMsgIntent = null,  locationMsgIntent = null;  
    private Bundle bundle = null;  
    //For preference
    public SharedPreferences sharedPreferences;
    public Editor editor;
    private String emergencyMsg, defaultEmergencyMsg = "I'm in danger now! Please help me call 911! Here is where I'm and live voice.";
	private int msgSendingIntervalTime, locationSharingIntervalTime;
	private String groupID;

	
    private LocationManager locManager;
	private Geocoder geocoder;
	private Location location;
	double dLong;
	double dLat;
	private String addr;
	private String locationProvider;
	private LocationInformation locInfo;
	//For voice
	private boolean runFlag = false;
	private VoiceRecorder voiceRecorder;
	private VoiceMessage voiceMessage;
	private Intent voiceMsgIntent = null;

    
    @Override  
    public void onCreate() { 
        super.onCreate();  
        Log.v("SOSService", "Started");
        this.init();  
//        //************************Init location*************************************
		initProvider();
		LocationListener locationListener = new LocationListener() {
    	    public void onLocationChanged(Location location) {
    	    	updateToNewLocation();
    	    }
    	    public void onStatusChanged(String provider, int status, Bundle extras) {   	  	
    	    }
    	    public void onProviderEnabled(String provider) { 	    	
    	    }
    	    public void onProviderDisabled(String provider) {
    	    	updateToNewLocation();
    	    }
    	  };
    	  locManager.requestLocationUpdates(locationProvider,  1 * 1000, 0, locationListener); 
       //*******************************************************************
        
        //Send a emergency msg at first
        sendEmergencyMsgBroadcast();
        
        
        //Timer used to broadcast voice msg  
        voiceTimer.schedule(new TimerTask() {  
            @Override  
            public void run() {  
                startRecord();
                runFlag = true;
                long startTime = System.currentTimeMillis();
                while(runFlag){
                	long endTime = System.currentTimeMillis();
                	if(endTime - startTime > msgSendingIntervalTime*1000){
                		stopRecord();
                		runFlag = false;
                	}              	
                }
            }  
        }, 0,msgSendingIntervalTime*1000 + 3000);  //give 3sec for system to send out voice
        
        
//        Timer used to broadcast location msg  
        locationTimer.schedule(new TimerTask() {  
            @Override  
            public void run() {  
                sendLocationMsgBroadcast();  
            }  
        }, 0,locationSharingIntervalTime*1000);  //This is minus
    }  
    /** 
     */  
    private void init(){  
        voiceTimer = new Timer();  
        locationTimer = new Timer();

        emergencyMsgIntent = new Intent();  
        voiceMsgIntent = new Intent();
        locationMsgIntent = new Intent();
        
        voiceRecorder = new VoiceRecorder(micImageHandler);
        
        locInfo = new LocationInformation(0.0,0.0,"");
        
        bundle = new Bundle();  

        sharedPreferences = getSharedPreferences("EmergencyGroupId",MODE_PRIVATE);
		editor = sharedPreferences.edit();
		
		groupID = sharedPreferences.getString("emergencyGroupId", "null");//defalult is null
		msgSendingIntervalTime = sharedPreferences.getInt("MsgSendingIntervalTime", 15);//default is 15 sec
		locationSharingIntervalTime = sharedPreferences.getInt("LocationSharingIntervalTime", 60);//default is 60 sec
		emergencyMsg = sharedPreferences.getString("EmergencyMsg", defaultEmergencyMsg);
        
    } 
    
    //*********************Location**************************
    /**
     * provider
     */
    	private void initProvider() {
        	locManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE); //to initiate the locationManager
        	locationProvider = LocationManager.NETWORK_PROVIDER;
        	//locationProvider = LocationManager.GPS_PROVIDER;
        	location = locManager.getLastKnownLocation(locationProvider);
        }
    	/**
    	 * obtain current location
    	 */
    	 private void updateToNewLocation(){
    	      
    	        location = locManager.getLastKnownLocation(locationProvider);
    	         dLong = 0.00;
    	         dLat = 0.00;
    	        
    	        Double[] lat_long = new Double[] { location.getLatitude(), location.getLongitude() };
    			new ReverseGeocodingTask(getBaseContext()).execute(lat_long);//this is an AsyncTask
    	        
    	        if(location != null){
    	        	dLong = location.getLongitude();
    	        	dLat = location.getLatitude();
    	        	locInfo.setLat(dLong);
    	        	locInfo.setLongi(dLong);
    	        }
    	    }
    	 /**
    	  * obtain address from latitude and logitude
    	  */
    	 
    	 private class ReverseGeocodingTask extends AsyncTask<Double, Void, String> {
    			Context mContext;

    			public ReverseGeocodingTask(Context context) {
    				super();
    				mContext = context;
    			}
    			@Override
    			protected String doInBackground(Double... params) {
    				geocoder = new Geocoder(mContext, Locale.ENGLISH);
    				double latitude = params[0].doubleValue();
    				double longitude = params[1].doubleValue();

    				List<Address> addresses = null;
    				 addr = "";

    				try {
    					addresses = geocoder.getFromLocation(latitude, longitude, 1);//表示最多返回一个结果
    				} catch (IOException e) {
    					e.printStackTrace();
    				}
    	    		
    				if (addresses != null && addresses.size() > 0) {
    					Address address = addresses.get(0);
    					addr+=address.getAddressLine(0)+",";
    					addr+=address.getLocality()+",";
    					addr+=address.getPostalCode()+",";
    					addr+=address.getCountryName();
    				}
    				return addr;
    			}
    			//这里的参数String addressText世界收doInBackGround返回的String-addr
    			@Override
    			protected void onPostExecute(String addressText) {
    				//address.setText(addressText);
    				//send location message after obtaining address
    				if (addressText != null && !addressText.equals("")) {
    						locInfo.setAddress(addr);
    						//sendLocationMsg(dLat, dLong, "", addressText);
    					} else {
    						//Toast.makeText(this, "cannot obtain your location information！", 0).show();
    						System.out.println("cannot obtain your location information！");
    					}
    			}
    		}
    	 
    	 
    //*********************Voice**************************
    		public void startRecord(){
    			if (!CommonUtils.isExitsSdcard()) {
    				Toast.makeText(this, "Voice message require SD card!", Toast.LENGTH_SHORT).show();
					return;
    			}
    			try {
					if (VoicePlayClickListener.isPlaying)
						VoicePlayClickListener.currentPlayListener.stopPlayVoice();
					    voiceRecorder.startRecording(null, groupID, getApplicationContext());
				} catch (Exception e) {
					if(voiceRecorder != null)
						voiceRecorder.discardRecording();
					Toast.makeText(this, R.string.recoding_fail, Toast.LENGTH_SHORT).show();
					return;
				}
    		}
    	public void stopRecord(){
    		//v.setPressed(false);
    		int length = voiceRecorder.stopRecoding();
    		if (length > 0) {
    			voiceMessage = new VoiceMessage();  			
    			voiceMessage.setFilePath(voiceRecorder.getVoiceFilePath());
    			voiceMessage.setFileName(voiceRecorder.getVoiceFileName("groupID"));
    			voiceMessage.setLength(Integer.toString(length));
    			voiceMessage.setFlag(false);

    			bundle.putString("Type", "Voice");
    			bundle.putString("GroupID", groupID);
    			bundle.putSerializable("voiceMessage", voiceMessage);  			
    	        voiceMsgIntent.putExtras(bundle);  
    	        voiceMsgIntent.setAction(ChatActivity.TIME_CHANGED_ACTION);

    	        sendBroadcast(voiceMsgIntent); 
    	        Log.v("SOSService", "Sended voice broadcast");
    		} else {
    			return;
    		}
    	}
    	 
    	private Handler micImageHandler = new Handler() {
    		@Override
    		public void handleMessage(android.os.Message msg) {
    			
    		}
    	};
//    	 
    	
//***********************************Emergency Message*********************************************
        /** 
         * Send text msg 
         */  
        private void sendEmergencyMsgBroadcast(){ 
        	//String currentAddr= "latitude = "+dLat+","+"longitude = "+dLong+","+"address = "+addr;
        	bundle.putString("Type", "Msg");
        	bundle.putString("GroupID", groupID);
            bundle.putString("emergencyMsg", emergencyMsg);  
            emergencyMsgIntent.putExtras(bundle);  
            emergencyMsgIntent.setAction(ChatActivity.TIME_CHANGED_ACTION);  
            sendBroadcast(emergencyMsgIntent);  
        }  
        
        
 //***************************************Location*********************************************************
        /** 
         * Send location msg 
         */  
        private void sendLocationMsgBroadcast(){ 
        	bundle.putString("Type", "Location");
        	bundle.putString("GroupID", groupID);
            bundle.putSerializable("locInfo", locInfo);
            locationMsgIntent.putExtras(bundle);  
            locationMsgIntent.setAction(ChatActivity.TIME_CHANGED_ACTION);  
            sendBroadcast(locationMsgIntent);  
        }  	 
    	 
    	 
    	 
    	 
      
    @Override  
    public IBinder onBind(Intent intent) {  
        Log.i(TAG,"SOSService->onBind");  
        return null;  
    }  


      

      
    @Override  
    public ComponentName startService(Intent service) {  
        Log.i(TAG,"SOSService->startService");  
        return super.startService(service);  
    }  
      
    @Override  
    public void onDestroy() {  
        super.onDestroy();  
        voiceRecorder.stopRecoding();
        Log.i(TAG,"SOSService->onDestroy");  
    }  
}