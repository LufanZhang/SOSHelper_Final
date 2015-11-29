/*
 * this class was created by Lufan: 11/20/2014
 */
package com.lz217_yq47.SOS.activity;

import java.io.File;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.LocationMessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.chat.VoiceMessageBody;
import com.lz217_yq47.SOS.adapter.MessageAdapter;

public class SOSReceiver extends BroadcastReceiver {
	public EMConversation conversation;
	public SharedPreferences sharedPreferences;
	public Editor editor;
	private String groupID, type;
	private Bundle bundle;
	private MessageAdapter adapter;
	

	// private boolean test;
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub

		System.out.println("get call from service------>");
		String action = intent.getAction();
		bundle = intent.getExtras();
		type = bundle.getString("Type");
		groupID = bundle.getString("GroupID");

		Log.v("SOSReceiver", "GroupID " + groupID);
		Log.v("SOSReceiver", "Type is *" + type + "*");
		
//*************************************receive broadcast from SOSService and deal with different type************
		if (!groupID.equals("null")) {
			Log.v("SOSReceiver", "Into groupID comparation");

			if (type.equals("Voice")) {		
				//这句话表示发送一个消息给对方
				EMMessage message = EMMessage.createSendMessage(EMMessage.Type.VOICE);
				message.setChatType(ChatType.GroupChat);
				message.setReceipt(groupID);
				VoiceMessage voiceMessage = (VoiceMessage) bundle.getSerializable("voiceMessage");
				String filePath = voiceMessage.getFilePath();
				String fileName = voiceMessage.getFileName();
				String length = voiceMessage.getLength();
		
				int len = Integer.parseInt(length);
				VoiceMessageBody body = new VoiceMessageBody(new File(filePath), len);
				message.addBody(body);

				try {
					   EMChatManager.getInstance().sendMessage(message);
					} catch (Exception e) {
					   e.printStackTrace();
					}
				
				Log.v("SOSReceiver", "Voice send ");
			} 
			else if (type.equals("Location")) {
				Log.v("SOSReceiver", "In Location ");
				EMMessage message = EMMessage.createSendMessage(EMMessage.Type.LOCATION);
				LocationInformation locInfo = (LocationInformation) bundle.getSerializable("locInfo");
				double latitude = locInfo.getLat();
				double longitude = locInfo.getLongi();
				System.out.println("receiver get latitude-------------->"+latitude);
				System.out.println("receiver get latitude--------------->"+longitude);
				String locationAddress = locInfo.getAddress();
				LocationMessageBody locBody = new LocationMessageBody(locationAddress, latitude, longitude);
				message.setChatType(ChatType.GroupChat);
				message.addBody(locBody);
				message.setReceipt(groupID);
//				conversation = EMChatManager.getInstance().getConversation(groupID);
//				conversation.addMessage(message);		
		//		adapter.refresh();
				try {
					   EMChatManager.getInstance().sendMessage(message);
					} catch (Exception e) {
					   e.printStackTrace();
					}
			} 
			else if (type.equals("Msg")) {
				Log.v("SOSReceiver", "Msg send ");
				String emergencyMsgString = bundle.getString("emergencyMsg");
				Log.v("SOSReceiver", "emergencyMsg " + emergencyMsgString);
				EMMessage message = EMMessage
						.createSendMessage(EMMessage.Type.TXT);
				message.setChatType(ChatType.GroupChat);
				TextMessageBody txtBody = new TextMessageBody(
						emergencyMsgString);
				message.addBody(txtBody);
				message.setReceipt(groupID);
//				conversation = EMChatManager.getInstance().getConversation(
//						groupID);
//				conversation.addMessage(message);
				try {
					   EMChatManager.getInstance().sendMessage(message);
					} catch (Exception e) {
					   e.printStackTrace();
					}
				
			} else { // type not match
				Log.v("SOSReceiver", "Not match ");
				return;
			}
		} else {// groupID is null
			Toast.makeText(context, "Please choose a Emergency group!",
					Toast.LENGTH_LONG).show();
		}

	}

}