package com.lz217_yq47.SOS.activity;


import java.lang.ref.WeakReference;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;

import com.lz217_yq47.SOS.R;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.graphics.Typeface;
import android.os.Handler;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class StatusViewManager implements MediaControl
{

    private static final String SYSTEM = "/system/fonts/";
    private static final String SYSTEM_FONT_TIME_BACKGROUND = SYSTEM + "AndroidClock.ttf";
    private static final String SYSTEM_FONT_TIME_FOREGROUND = SYSTEM + "AndroidClock_Highlight.ttf";
    
    private final static String M12 = "h:mm";
    private final static String M24 = "kk:mm";
    
	private TextView mDateView;
	private TextView mTimeView;
	public TextView mArtistView;
	public TextView mMusicView;
	
	private String mDateFormat;
	private String mFormat;
	
	private static Activity mActivity;
	private AmPm mAmPm;
    private Calendar mCalendar;
    public ContentObserver mFormatChangeObserver;
    public BroadcastReceiver mIntentReceiver;
   
    private final Handler mHandler = new Handler();
    
    private static final Typeface sBackgroundFont;
    private static final Typeface sForegroundFont;
    
    private static Context mContext;
	
    static 
    {
        sBackgroundFont = Typeface.createFromFile(SYSTEM_FONT_TIME_BACKGROUND);
        sForegroundFont = Typeface.createFromFile(SYSTEM_FONT_TIME_FOREGROUND);
    }
    
	public StatusViewManager(Activity activity, Context context)
	{
		mContext = context;
		mActivity = activity;
		initViews();
		refreshDate();
	}
	
	private View findViewById(int id) 
	{
        return mActivity.findViewById(id);
    }
	
    private void refreshDate()
    {
    	if (mDateView != null)
    	{
    		mDateView.setText(DateFormat.format(mDateFormat, new Date()));
    	}
    }
	
    private String getString(int id)
    {
    	return mActivity.getString(id);
    }
    
    class AmPm {
        private TextView mAmPmTextView;
        private String mAmString, mPmString;

        AmPm(Typeface tf) {
            mAmPmTextView = (TextView)findViewById(R.id.am_pm);
            if (mAmPmTextView != null && tf != null) {
                mAmPmTextView.setTypeface(tf);
            }

            String[] ampm = new DateFormatSymbols().getAmPmStrings();
            mAmString = ampm[0];
            mPmString = ampm[1];
        }

        void setShowAmPm(boolean show) {
            if (mAmPmTextView != null) {
                mAmPmTextView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        }

        void setIsMorning(boolean isMorning) {
            if (mAmPmTextView != null) {
                mAmPmTextView.setText(isMorning ? mAmString : mPmString);
            }
        }
    }
    
    private static class TimeChangedReceiver extends BroadcastReceiver {
        private WeakReference<StatusViewManager> mStatusViewManager;
        //private Context mContext;

        public TimeChangedReceiver(StatusViewManager status) {
        	mStatusViewManager = new WeakReference<StatusViewManager>(status);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            // Post a runnable to avoid blocking the broadcast.
            final boolean timezoneChanged =
                    intent.getAction().equals(Intent.ACTION_TIMEZONE_CHANGED);
            final StatusViewManager status = mStatusViewManager.get();
            if (status != null) {
            	status.mHandler.post(new Runnable() {
                    public void run() {
                        if (timezoneChanged) {
                        	status.mCalendar = Calendar.getInstance();
                        }
                        status.updateTime();
                    }
                });
            } else {
                try {
                	mContext.unregisterReceiver(this);
                } catch (RuntimeException e) {
                    // Shouldn't happen
                }
            }
        }
    };
    
   
    private static class FormatChangeObserver extends ContentObserver {
        private WeakReference<StatusViewManager> mStatusViewManager;
        //private Context mContext;
        public FormatChangeObserver(StatusViewManager status) {
            super(new Handler());
            mStatusViewManager = new WeakReference<StatusViewManager>(status);
        }
        @Override
        public void onChange(boolean selfChange) {
        	StatusViewManager mStatusManager = mStatusViewManager.get();
            if (mStatusManager != null) {
            	mStatusManager.setDateFormat();
            	mStatusManager.updateTime();
            } else {
                try {
                	mContext.getContentResolver().unregisterContentObserver(this);
                } catch (RuntimeException e) {
                    // Shouldn't happen
                }
            }
        }
    }
    
    private void updateTime() 
    {
        mCalendar.setTimeInMillis(System.currentTimeMillis());

        CharSequence newTime = DateFormat.format(mFormat, mCalendar);
        mTimeView.setText(newTime);
        mAmPm.setIsMorning(mCalendar.get(Calendar.AM_PM) == 0);
    }
    
    private void setDateFormat() 
    {
        mFormat = android.text.format.DateFormat.is24HourFormat(mContext)
            ? M24 : M12;
        mAmPm.setShowAmPm(mFormat.equals(M12));
    }
    
  
	@Override
	public void connectMediaService() 
	{
		/*
		Intent intent = new Intent();
		intent.setClassName("com.android.music", "com.android.music.MediaPlaybackService");
	    mContext.startService(intent);*/
	}

	@Override
	public void registerComponent() 
	{
		// TODO Auto-generated method stub
		Log.d("Status_MainActivity", "registerComponent()");
		
        if (mIntentReceiver == null) {
            mIntentReceiver = new TimeChangedReceiver(this);
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_TIME_TICK);
            filter.addAction(Intent.ACTION_TIME_CHANGED);
            filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
            mContext.registerReceiver(mIntentReceiver, filter);
        }

        if (mFormatChangeObserver == null) {
            mFormatChangeObserver = new FormatChangeObserver(this);
            mContext.getContentResolver().registerContentObserver(
                    Settings.System.CONTENT_URI, true, mFormatChangeObserver);
        }
        
        updateTime();
	}

	@Override
	public void unregisterComponent()
	{
		// TODO Auto-generated method stub
		Log.d("Status_MainActivity", "unregisterComponent()");
        if (mIntentReceiver != null) {
        	mContext.unregisterReceiver(mIntentReceiver);
        }
        if (mFormatChangeObserver != null) {
        	mContext.getContentResolver().unregisterContentObserver(
                    mFormatChangeObserver);
        }
        mFormatChangeObserver = null;
        mIntentReceiver = null;
	}

	@Override
	public void initViews() 
	{
		// TODO Auto-generated method stub
		mDateView = (TextView)findViewById(R.id.date);
    	mDateFormat = getString(R.string.month_day_year);
    	mTimeView = (TextView) findViewById(R.id.time);
    	mTimeView.setTypeface(sBackgroundFont);
    	mArtistView = (TextView)findViewById(R.id.artist);
    	mMusicView = (TextView)findViewById(R.id.music);
    	
    	mArtistView.setSelected(true);
    	mMusicView.setSelected(true);
    	
        mAmPm = new AmPm(null);
        mCalendar = Calendar.getInstance();
        
        setDateFormat();
        registerComponent();
	}
    
}