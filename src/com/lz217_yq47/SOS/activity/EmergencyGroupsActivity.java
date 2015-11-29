/*
 * this class is created by Lufan: 11/16/2014
 */
package com.lz217_yq47.SOS.activity;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.lz217_yq47.SOS.R;
import com.lz217_yq47.SOS.adapter.EmergencyGroupAdapter;


public class EmergencyGroupsActivity extends BaseActivity {
	private ListView groupListView;
	protected List<EMGroup> grouplist;
	private EmergencyGroupAdapter groupAdapter;
	private InputMethodManager inputMethodManager;
	public static EmergencyGroupsActivity instance;
	
	private String groupIdString;
	
	//test for action
	public SharedPreferences sharedPreferences;
	public Editor editor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.v("TAG", "on Emergency onCreak");
		//Remove title bar
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.fragment_emergency_groups);
		
		sharedPreferences = getSharedPreferences("EmergencyGroupId", MODE_PRIVATE);
		editor = sharedPreferences.edit();
		
		instance = this;
		inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		grouplist =	EMGroupManager.getInstance().getAllGroups();
		groupListView = (ListView)findViewById(R.id.list);
		groupAdapter = new EmergencyGroupAdapter(this, 1, grouplist);
		groupListView.setAdapter(groupAdapter);
		groupListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				groupIdString = groupAdapter.getItem(position - 1).getGroupId();
					
					Intent intent = new Intent(EmergencyGroupsActivity.this, SOSSettingsActivity.class);
					// it is group chat
					//intent.putExtra("chatType", ChatActivity.CHATTYPE_GROUP);
					intent.putExtra("groupId", groupIdString);	
					Log.v("groupId","group activity groupID " + groupIdString);
					startActivityForResult(intent, 0);  //in order to get result back
					
					editor.putString("emergencyGroupId", groupIdString);
					editor.commit();
					
					
					
//				}
			}

		});
		groupListView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
					if (getCurrentFocus() != null)
						inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);
				}
				return false;
			}
		});
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		grouplist = EMGroupManager.getInstance().getAllGroups();
		groupAdapter = new EmergencyGroupAdapter(this, 1, grouplist);
		groupListView.setAdapter(groupAdapter);
		groupAdapter.notifyDataSetChanged();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		instance = null;
	}
	
	/**
	 * @param view
	 */
	public void back(View view){
		finish();
	}
}
