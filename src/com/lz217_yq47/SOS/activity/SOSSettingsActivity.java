/*
 * this class is created by Lufan: 11/15/2014
 */
package com.lz217_yq47.SOS.activity;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.easemob.chat.EMGroupManager;
import com.lz217_yq47.SOS.R;

@SuppressLint("ClickableViewAccessibility")
public class SOSSettingsActivity extends BaseActivity implements OnClickListener {

		private static final String TAG = "SOSSettingsActivity";
	
		private String groupID; 
		private EditText editEmergencyMsg, msgSendingInterval, locationSharingInterval;
		private Button selectGroup, changeGroup, deleteGroup, stopService;
		private TextView groupIdTextView;
		public static SOSSettingsActivity instance;
		
		private String emergencyMsg;
		private int msgSendingIntervalTime, locationSharingIntervalTime;
		
		public SharedPreferences sharedPreferences;
		public Editor editor;
		
		

		@SuppressLint("ClickableViewAccessibility")
		@Override
		protected void onCreate(Bundle savedInstanceState) {
		
			Log.d(TAG,"In SOSSettingsActivity onCreate");
			
			super.onCreate(savedInstanceState);
			//Set keyboard hidden as default
			getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
			
			setContentView(R.layout.activity_sos_settings_details);
			instance = this;
			
			editEmergencyMsg = (EditText) findViewById(R.id.edit_emergency_msg);
			msgSendingInterval = (EditText) findViewById(R.id.msg_sending_interval);
			locationSharingInterval = (EditText) findViewById(R.id.location_sharing_interval);
			selectGroup = (Button) findViewById(R.id.btn_select_grp);
			changeGroup = (Button) findViewById(R.id.btn_change_grp);
			deleteGroup = (Button) findViewById(R.id.btn_delete_grp);
			stopService = (Button) findViewById(R.id.btn_stop_service);
			groupIdTextView = (TextView) findViewById(R.id.groupId); 
			
			selectGroup.setOnClickListener(this);
			changeGroup.setOnClickListener(this);
			deleteGroup.setOnClickListener(this);
			
			sharedPreferences = getSharedPreferences("EmergencyGroupId",MODE_PRIVATE);
			editor = sharedPreferences.edit();
			
			groupID = sharedPreferences.getString("emergencyGroupId", "null");
			Log.v("SOS", sharedPreferences.getString("emergencyGroupId", "Now,it's null"));
			
			//Setting button visibility and the Emergency group name
			if(groupID == "null"){
				groupIdTextView.setText("Please select a emergency group.");
			}
			else{
				groupIdTextView.setText("Emergency Group name: "+EMGroupManager.getInstance().getGroup(groupID).getGroupName());
				selectGroup.setVisibility(8);//slectGroup gone---8
				changeGroup.setVisibility(0);//changeGroup visible-----0
				deleteGroup.setVisibility(0);//deleteGroup button visible---0
//				stopService.setVisibility(0);//stop service button visible
			}
			
			if(chechIfServiceRunning()){
				stopService.setVisibility(0);//stop service button visible
				stopService.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Log.v("TAG", "Clicked stop service");
						stopEmergencyService();
						
					}
				});
			}
			
			
				
			
	}
		
		protected void onResume() {
		    super.onResume();	
		}


		@Override
		public void onClick(View view) {
			Log.v("TAG", "on SOS click");
			switch(view.getId()){
			case R.id.btn_select_grp:
				getEmergencyMsg();
				getMsgSendingIntervalTime();
				getLocationSharingIntervalTime();
				Log.v("TAG", "on SOS go to EmergencyGroupsActivity");
				startActivity(new Intent(this, EmergencyGroupsActivity.class));
				
				break;
			case R.id.btn_change_grp:
				//go to EmergencyGroupActivity
				getEmergencyMsg();
				getMsgSendingIntervalTime();
				getLocationSharingIntervalTime();
				startActivity(new Intent(this, EmergencyGroupsActivity.class));
				break;
			case R.id.btn_delete_grp:
				editor.clear().commit(); //clear all data in the sharedPreferrence
				groupIdTextView.setText("Please select a emergency group."); //update group information
				deleteGroup.setVisibility(8); //hide delete group button
				changeGroup.setVisibility(8);//hide change Group button
				selectGroup.setVisibility(0);//set slectGroup visible
				break;
			default:
				break;
			}
		}

	       protected String getEmergencyMsg(){
	    	   Log.v("TAG", "on get emergency msg click");
	    	   emergencyMsg = editEmergencyMsg.getText().toString();
	    	   editor.putString("EmergencyMsg", emergencyMsg);
	    	   editor.commit();
			return emergencyMsg;  	   
	       }
	       
	       protected int getMsgSendingIntervalTime() {
	    	   Log.v("TAG", "on get msg sending interval click");
			msgSendingIntervalTime = Integer.parseInt(msgSendingInterval.getText().toString());
			//call editor interface
			//Interface used for modifying values in a SharedPreferences object. All changes you make in an editor are batched, 
			//and not copied back to the original SharedPreferences until you call commit() or apply()
			editor.putInt("MsgSendingIntervalTime", msgSendingIntervalTime);
			editor.commit();
			return msgSendingIntervalTime;
		}
	       protected int getLocationSharingIntervalTime() {
	    	   Log.v("TAG", "on get location sharing interval click");
			locationSharingIntervalTime = Integer.parseInt(locationSharingInterval.getText().toString());
			editor.putInt("LocationSharingIntervalTime", locationSharingIntervalTime);
			editor.commit();
			return locationSharingIntervalTime;
		}
	       
	     protected void stopEmergencyService(){
	    	 Log.v("TAG", "Into stop service method");
	    	 Intent timeService = new Intent();
				timeService.setClass(SOSSettingsActivity.this,SOSService.class);
				stopService(timeService);
				stopService.setVisibility(8); //invisible stop service button
				Log.v("TAG", "after stop service");
	     }
	     //check if the service is runing
	     protected boolean chechIfServiceRunning() {
	    	 ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
	    	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) 
	    	            {
	    	                if ("com.lz217_yq47.SOS.activity.SOSService"
	    	                        .equals(service.service.getClassName())) 
	    	                {
	    	                    return true;
	    	                }
	    	            }
	    	         return false;
		}
	     

			
			//when click on back, everything will be stored automatically
			public void back(View view){
				//When click back button, will turn back to Setting fragment
				//Refresh preference data when click back button
				getEmergencyMsg();
				getMsgSendingIntervalTime();
				System.out.println("getMsgSendingIntervalTime------------->"+getMsgSendingIntervalTime());
				getLocationSharingIntervalTime();
				startActivity(new Intent(this,MainActivity.class));
			}
		
		
		
		
		
}
			

		

