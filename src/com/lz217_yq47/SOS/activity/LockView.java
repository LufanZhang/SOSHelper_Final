package com.lz217_yq47.SOS.activity;

import com.lz217_yq47.SOS.R;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class LockView extends ViewGroup implements OnClickListener
{

	private static final boolean DBG = true;
	private Context mContext;
	private Handler mainHandler = null;
	private float mx;
	private float my;
//	private int count = 0;
//	private long firstClick = 0;
//	private long secondClick = 0;
	
	private int mWidth, mHight;
	private int mScreenHalfWidth;
	private int mAlphaViewWidth, mAlphaViewHeight;
	private int mCenterViewWidth, mCenterViewHeight;
	private int mCenterViewTop, mCenterViewBottom;
	private int mAlphaViewTop, mAlphaViewBottom;
	private int mSmsViewHalfWidth, mSmsViewHalfHeight;
	private int mDialViewHalfWidth, mDialViewHalfHeight;
	private int mCameraViewHalfWidth, mHalfCameraViewHeight;
	private int mUnlockViewHalfWidth, mUnlockViewHalfHeight;	
	private int mLightViewHalfWidth, mLightViewHalfHeight;
	private int mMusicViewHalfWidth, mMusicViewHalfHeight;

	private ImageView mSmsView, mDialView, mCameraView, mUnLockView;
	private ImageView mCenterView, mAlphaView;
	private ImageView mSmsLightView, mUnLockLightView,
	                  mCameraLightView, mDialLightView;
	
	private ImageView mPlayView, mNextView, mPrevView, mStopView;

	private Rect smsRect, dialRect, cameraRect, unlockRect;
	private Rect mCenterViewRect;
	
	private AlphaAnimation alpha;
	private boolean mTracking = false;
	
	private static final String TAG = "FxLockView";
	public static final String SHOW_MUSIC = "com.phicomm.hu.action.music";
	private static final String SERVICECMD = "com.android.music.musicservicecommand";
	private static final String CMDNAME = "command";
	private static final String CMDSTOP = "stop";
	private static final String CMDPAUSE = "pause";
	private static final String CMDPLAY = "play";
	private static final String CMDPREV = "previous";
	private static final String CMDNEXT = "next";
	
	public LockView(Context context, AttributeSet attrs) {
		super(context, attrs, 0);
		mContext = context;
		if(DBG) Log.d(TAG, "FxLockView2");
		//connectMediaService();
//		if(DBG) Log.d(TAG, "FxLockView-->isMusic--->" + MusicInfo.isMusic());
//		MusicInfo.setMusic(false);
		initViews(context);
		setViewId();
		//setViewOnClick();
		onAnimationStart();
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		if (changed) {
			mWidth = r;
			mHight = b;
			mScreenHalfWidth = mWidth >> 1;
			
			getViewMeasure();
			mCenterViewTop = 4 * mHight / 7 - (mCenterViewHeight >> 1);
			mCenterViewBottom = 4 * mHight / 7 + (mCenterViewHeight >> 1);
			mAlphaViewTop = 4 * mHight / 7 - (mAlphaViewHeight >> 1);
			mAlphaViewBottom = 4 * mHight / 7 + (mAlphaViewHeight >> 1);
			
			setChildViewLayout();
			setMusicButtonsLayout();
			setActivatedViewLayout();
			getChildViewRect();

			mCenterViewRect = new Rect(mWidth / 2 - mAlphaViewWidth / 2, mAlphaViewTop,
					mWidth / 2 + mAlphaViewWidth / 2, mAlphaViewBottom);

		}

		if(DBG) Log.d(TAG, "l-->" + l);
		if(DBG) Log.d(TAG, "t-->" + t);
		if(DBG) Log.d(TAG, "r-->" + r);
		if(DBG) Log.d(TAG, "b-->" + b);
	}

	private void getViewMeasure()
	{
		mAlphaView.measure(View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
				.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
		mAlphaViewWidth = mAlphaView.getMeasuredWidth();
		mAlphaViewHeight = mAlphaView.getMeasuredHeight();

		mCenterView.measure(View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
				.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
		mCenterViewWidth = mCenterView.getMeasuredWidth();
		mCenterViewHeight = mCenterView.getMeasuredHeight();
		
		mSmsView.measure(View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
				.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
		mSmsViewHalfWidth = (mSmsView.getMeasuredWidth()) >> 1;
		mSmsViewHalfHeight = (mSmsView.getMeasuredHeight()) >> 1;
		
		mDialView.measure(View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
				.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
		mDialViewHalfWidth = (mDialView.getMeasuredWidth()) >> 1;
		mDialViewHalfHeight = (mDialView.getMeasuredHeight()) >> 1;
		
		mCameraView.measure(View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
				.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
		mCameraViewHalfWidth = (mCameraView.getMeasuredWidth()) >> 1;
		mHalfCameraViewHeight = (mCameraView.getMeasuredHeight()) >> 1;
		
		mUnLockView.measure(View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
				.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
		mUnlockViewHalfWidth = (mUnLockView.getMeasuredWidth()) >> 1;
		mUnlockViewHalfHeight = (mUnLockView.getMeasuredHeight()) >> 1;
		
		mSmsLightView.measure(View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
				.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
		mLightViewHalfWidth = (mSmsLightView.getMeasuredWidth()) >> 1;
		mLightViewHalfHeight = (mSmsLightView.getMeasuredHeight()) >> 1;
		
		mPlayView.measure(View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
				.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
		mMusicViewHalfWidth = (mPlayView.getMeasuredWidth()) >> 1;
		mMusicViewHalfHeight = (mPlayView.getMeasuredHeight()) >> 1;
	}
	
	private void setActivatedViewLayout()
	{
		mUnLockLightView.layout(mScreenHalfWidth - mLightViewHalfWidth - 5, 
				(mCenterViewTop + 2 * mCenterViewHeight) - mLightViewHalfHeight, 
				mScreenHalfWidth + mLightViewHalfWidth - 5,
				(mCenterViewBottom + mCenterViewHeight) + mLightViewHalfHeight);
		mSmsLightView.layout((mScreenHalfWidth + 3 * mCenterViewWidth / 2) - 2 * mLightViewHalfWidth,
				(mCenterViewTop + mCenterViewHeight / 2) - mLightViewHalfHeight, 
				(mScreenHalfWidth + 3 * mCenterViewWidth / 2) + 2 * mLightViewHalfWidth,
				(mAlphaViewBottom - mCenterViewHeight / 2) + mLightViewHalfHeight);
		mDialLightView.layout((mScreenHalfWidth - 3 * mCenterViewWidth / 2) - mLightViewHalfWidth, 
				(mCenterViewTop + mCenterViewHeight / 2) - mLightViewHalfHeight, 
				(mScreenHalfWidth - 3 * mCenterViewWidth / 2) + mLightViewHalfWidth, 
				(mAlphaViewBottom - mCenterViewHeight / 2) + mLightViewHalfHeight);
		mCameraLightView.layout(mScreenHalfWidth - mLightViewHalfWidth, 
				(mCenterViewTop - mCenterViewHeight) - mLightViewHalfHeight,
				mScreenHalfWidth + mLightViewHalfWidth,
				(mCenterViewBottom - 2 * mCenterViewHeight) + mLightViewHalfHeight);
	}
	
	private void setChildViewLayout()
	{
		mAlphaView.layout(mScreenHalfWidth - mAlphaViewWidth / 2, mAlphaViewTop,
				mScreenHalfWidth + mAlphaViewWidth / 2, mAlphaViewBottom);
		
		mCenterView.layout(mScreenHalfWidth - mCenterViewWidth / 2, mCenterViewTop,
				mScreenHalfWidth + mCenterViewWidth / 2, mCenterViewBottom);
		
		mSmsView.layout((mScreenHalfWidth + 3 * mCenterViewWidth / 2) - 2 * mSmsViewHalfWidth,
				(mCenterViewTop + mCenterViewHeight / 2) - mSmsViewHalfHeight, 
				(mScreenHalfWidth + 3 * mCenterViewWidth / 2) + 2 * mSmsViewHalfWidth,
				(mAlphaViewBottom - mCenterViewHeight / 2) + mSmsViewHalfHeight);
		
		mDialView.layout((mScreenHalfWidth - 3 * mCenterViewWidth / 2) - mDialViewHalfWidth, 
				(mCenterViewTop + mCenterViewHeight / 2) - mDialViewHalfHeight, 
				(mScreenHalfWidth - 3 * mCenterViewWidth / 2) + mDialViewHalfWidth, 
				(mAlphaViewBottom - mCenterViewHeight / 2) + mDialViewHalfHeight);
		
		mCameraView.layout(mScreenHalfWidth - mCameraViewHalfWidth, 
				(mCenterViewTop - mCenterViewHeight) - mHalfCameraViewHeight,
				mScreenHalfWidth + mCameraViewHalfWidth,
				(mCenterViewBottom - 2 * mCenterViewHeight) + mHalfCameraViewHeight);
		
		mUnLockView.layout(mScreenHalfWidth - mUnlockViewHalfWidth, 
				(mCenterViewTop + 2 * mCenterViewHeight) - mUnlockViewHalfHeight,
				mScreenHalfWidth + mUnlockViewHalfWidth,
				(mCenterViewBottom + mCenterViewHeight) + mUnlockViewHalfHeight);
		
	}
	
	private void setMusicButtonsLayout()
	{
		mNextView.layout((mScreenHalfWidth + 3 * mCenterViewWidth / 2) - 2 * mMusicViewHalfWidth,
				(mCenterViewTop + mCenterViewHeight / 2) - mMusicViewHalfHeight, 
				(mScreenHalfWidth + 3 * mCenterViewWidth / 2) + 2 * mMusicViewHalfWidth,
				(mAlphaViewBottom - mCenterViewHeight / 2) + mMusicViewHalfHeight);
		
		mPrevView.layout((mScreenHalfWidth - 3 * mCenterViewWidth / 2) - mMusicViewHalfWidth, 
				(mCenterViewTop + mCenterViewHeight / 2) - mMusicViewHalfHeight, 
				(mScreenHalfWidth - 3 * mCenterViewWidth / 2) + mMusicViewHalfWidth, 
				(mAlphaViewBottom - mCenterViewHeight / 2) + mMusicViewHalfHeight);
		
		mStopView.layout(mScreenHalfWidth - mMusicViewHalfWidth, 
				(mCenterViewTop + 2 * mCenterViewHeight) - mMusicViewHalfHeight,
				mScreenHalfWidth + mMusicViewHalfWidth,
				(mCenterViewBottom + mCenterViewHeight) + mMusicViewHalfHeight);
		
		mPlayView.layout(mScreenHalfWidth - mMusicViewHalfWidth, 
				(mCenterViewTop - mCenterViewHeight) - mMusicViewHalfHeight,
				mScreenHalfWidth + mMusicViewHalfWidth,
				(mCenterViewBottom - 2 * mCenterViewHeight) + mMusicViewHalfHeight);
	}
	
	private void getChildViewRect()
	{
		smsRect = new Rect((mScreenHalfWidth + 3 * mCenterViewWidth / 2) - 2 * mSmsViewHalfWidth,
				(mCenterViewTop + mCenterViewHeight / 2) - mSmsViewHalfHeight, 
				(mScreenHalfWidth + 3 * mCenterViewWidth / 2) + 2 * mSmsViewHalfWidth,
				(mAlphaViewBottom - mCenterViewHeight / 2) + mSmsViewHalfHeight);
			
		dialRect = new Rect((mScreenHalfWidth - 3 * mCenterViewWidth / 2) - mDialViewHalfWidth, 
				(mCenterViewTop + mCenterViewHeight / 2) - mDialViewHalfHeight, 
				(mScreenHalfWidth - 3 * mCenterViewWidth / 2) + mDialViewHalfWidth, 
				(mAlphaViewBottom - mCenterViewHeight / 2) + mDialViewHalfHeight);
		
		cameraRect = new Rect(mScreenHalfWidth - mCameraViewHalfWidth, 
				(mCenterViewTop - mCenterViewHeight) - mHalfCameraViewHeight,
				mScreenHalfWidth + mCameraViewHalfWidth,
				(mCenterViewBottom - 2 * mCenterViewHeight) + mHalfCameraViewHeight);
			
		unlockRect = new Rect(mScreenHalfWidth - mUnlockViewHalfWidth, 
				(mCenterViewTop + 2 * mCenterViewHeight) - mUnlockViewHalfHeight,
				mScreenHalfWidth + mUnlockViewHalfWidth,
				(mCenterViewBottom + mCenterViewHeight) + mUnlockViewHalfHeight);
	}
	
	private void initViews(Context context) {
		mAlphaView = new ImageView(context);
		mAlphaView.setImageResource(R.drawable.centure2);
		setViewsLayout(mAlphaView);
		mAlphaView.setVisibility(View.INVISIBLE);

		mCenterView = new ImageView(context);
		mCenterView.setImageResource(R.drawable.centure1);
		setViewsLayout(mCenterView);
		mCenterView.setVisibility(View.VISIBLE);
		
		mSmsView = new ImageView(context);
		mSmsView.setImageResource(R.drawable.sms);
		setViewsLayout(mSmsView);
		mSmsView.setVisibility(View.VISIBLE);
		
		mDialView = new ImageView(context);
		mDialView.setImageResource(R.drawable.dial);
		setViewsLayout(mDialView);
		mDialView.setVisibility(View.VISIBLE);

		mCameraView = new ImageView(context);
		mCameraView.setImageResource(R.drawable.camera);
		setViewsLayout(mCameraView);
		mCameraView.setVisibility(View.VISIBLE);
	
		mUnLockView = new ImageView(context);
		mUnLockView.setImageResource(R.drawable.home);
		setViewsLayout(mUnLockView);
		mUnLockView.setVisibility(View.VISIBLE);
		
		mNextView = new ImageView(context);
		mNextView.setImageResource(R.drawable.next);
		setViewsLayout(mNextView);
		setMusicButtonBackground(mNextView);
		
		mPrevView = new ImageView(context);
		mPrevView.setImageResource(R.drawable.prev);
		setViewsLayout(mPrevView);
		setMusicButtonBackground(mPrevView);
		
		mPlayView = new ImageView(context);
		setPlayViewDrawable();
		setViewsLayout(mPlayView);
		setMusicButtonBackground(mPlayView);
			
		mStopView = new ImageView(context);
		mStopView.setImageResource(R.drawable.stop);
		setViewsLayout(mStopView);
		setMusicButtonBackground(mStopView);
		//mStopView.setVisibility(View.INVISIBLE);
		
		mSmsLightView= new ImageView(context);
		setLightDrawable(mSmsLightView);
		setViewsLayout(mSmsLightView);
		mSmsLightView.setVisibility(INVISIBLE);
		
		mUnLockLightView = new ImageView(context);
		setLightDrawable(mUnLockLightView);
		setViewsLayout(mUnLockLightView);
		mUnLockLightView.setVisibility(INVISIBLE);
		
		mCameraLightView = new ImageView(context);
		setLightDrawable(mCameraLightView);
		setViewsLayout(mCameraLightView);
		mCameraLightView.setVisibility(INVISIBLE);
		
		mDialLightView = new ImageView(context);
		setLightDrawable(mDialLightView);
		setViewsLayout(mDialLightView);
		mDialLightView.setVisibility(INVISIBLE);
	}

	private void setLightDrawable(ImageView img)
	{
		img.setImageResource(R.drawable.light);
	}
	
	private void setViewsLayout(ImageView image) {
		image.setScaleType(ScaleType.CENTER);
		image.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		addView(image);
	}

	private void setMusicButtonBackground(ImageView musicIcon)
	{
		musicIcon.setBackgroundResource(R.drawable.music_button_bg);
		musicIcon.setVisibility(View.INVISIBLE);
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		final int action = ev.getAction();
		final float x = ev.getX();
		final float y = ev.getY();

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			if (mCenterViewRect.contains((int) x, (int) y)) 
			{
				mTracking = true;
				//stopViewAnimation();
				onAnimationEnd();
				mAlphaView.setVisibility(View.INVISIBLE);
				return true;
			} 
//			else if(MusicInfo.isMusic())
//			{
//				setMusicViewsOnClick();
//			}		
//			break;

		default:
			break;
		}
		if(DBG) Log.d(TAG, "onInterceptTouchEvent()");
		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		if (mTracking)
		{
			final int action = event.getAction();
			final float nx = event.getX();
			final float ny = event.getY();

			switch (action) {
			case MotionEvent.ACTION_DOWN:
				//showMusicButtons();
				break;
			case MotionEvent.ACTION_MOVE:
			     setTargetViewVisible(nx, ny);
				 handleMoveView(nx, ny);
				break;
			case MotionEvent.ACTION_UP:
				 mTracking = false;
				 doTriggerEvent(mx, my);
				 resetMoveView();
				break;
			case MotionEvent.ACTION_CANCEL:
				 mTracking = false;
				 doTriggerEvent(mx, my);
				 resetMoveView();
				break;
			}
		}
		if(DBG) Log.d(TAG, "onTouchEvent()");
		return mTracking || super.onTouchEvent(event);
	}


	private float dist2(float dx, float dy)
	{
		return dx * dx + dy * dy;
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressLint("NewApi")
	private void handleMoveView(float x, float y)
	{
		
		int mHalfCenterViewWidth = mCenterViewWidth >> 1;
			
		int Radius = mCenterViewWidth + mHalfCenterViewWidth;
		
		
		if (Math.sqrt(dist2(x - mScreenHalfWidth, y - (mCenterView.getTop() + mCenterViewWidth / 2)
				)) > Radius)		
		{
			x = (float) ((Radius / (Math.sqrt(dist2(x - mScreenHalfWidth, y - (mCenterView.getTop() + mHalfCenterViewWidth)
			)))) * (x - mScreenHalfWidth) + mScreenHalfWidth);
			
			y = (float) ((Radius / (Math.sqrt(dist2(x - mScreenHalfWidth, y - (mCenterView.getTop() + mHalfCenterViewWidth)
			)))) * (y - (mCenterView.getTop() + mHalfCenterViewWidth)) + mCenterView.getTop() + mHalfCenterViewWidth);
		}
		
		mx = x;
		my = y;
		
		mCenterView.setX((int)x - mCenterView.getWidth()/2);
		mCenterView.setY((int)y - mCenterView.getHeight()/2);
		ShowLightView(x, y);
	    invalidate();
	}
	
	private void doTriggerEvent(float a, float b)
	{
		if (smsRect.contains((int)a, (int) b))
		{
			//stopViewAnimation();
			onAnimationEnd();
			setTargetViewInvisible(mSmsView);
			virbate();
			mainHandler.obtainMessage(RunningActivity.MSG_LAUNCH_SMS).sendToTarget();
		}
		else if (dialRect.contains((int)a , (int)b))
		{
			onAnimationEnd();
			setTargetViewInvisible(mDialView);
			virbate();
			mainHandler.obtainMessage(RunningActivity.MSG_LAUNCH_DIAL).sendToTarget();
		}
		else if (cameraRect.contains((int)a, (int)b))
		{
			onAnimationEnd();
			setTargetViewInvisible(mCameraView);
			virbate();
			mainHandler.obtainMessage(RunningActivity.MSG_LAUNCH_SOS).sendToTarget();
		}
		else if (unlockRect.contains((int)a, (int)b))
		{
			onAnimationEnd();
			setTargetViewInvisible(mUnLockView);
			virbate();
			mainHandler.obtainMessage(RunningActivity.MSG_LAUNCH_HOME).sendToTarget();
		}
	}
	
	private void ShowLightView(float a, float b)
	{
		if (unlockRect.contains((int)a, (int)b))
		{
			setLightVisible(mUnLockLightView);
		}
		else if (smsRect.contains((int)a, (int) b))
		{
			setLightVisible(mSmsLightView);
		}
		else if (dialRect.contains((int)a , (int)b))
		{
			setLightVisible(mDialLightView);
		}
		else if (cameraRect.contains((int)a, (int)b))
		{
			setLightVisible(mCameraLightView);
		}
		else
		{
			setLightInvisible();
		}
	}
	
	private void setLightVisible(ImageView view)
	{
		view.setVisibility(View.VISIBLE);
        mCenterView.setVisibility(View.INVISIBLE);
	}
	
	private void setLightInvisible()
	{
		final View mActivatedViews[] = {mUnLockLightView, mSmsLightView, 
				mDialLightView, mCameraLightView};
		for (View view : mActivatedViews)
		{
			view.setVisibility(View.INVISIBLE);
		}

        mCenterView.setVisibility(View.VISIBLE);
	}
	
	private void setTargetViewInvisible(ImageView img)
	{
		img.setVisibility(View.INVISIBLE);	
	}
	
	private void setTargetViewVisible(float x, float y)
	{
		if(Math.sqrt(dist2(x - mScreenHalfWidth, y - (mCenterView.getTop() + mCenterViewWidth / 2)
		)) > mAlphaViewHeight / 4)
		{
		        return;
		}
	}
	

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressLint("NewApi")
	private void resetMoveView()
	{
		mCenterView.setX(mWidth / 2 - mCenterViewWidth /2);
		mCenterView.setY((mCenterView.getTop() + mCenterViewHeight / 2) - mCenterViewHeight / 2);
		onAnimationStart();
		invalidate();
	}
	
	public void setMainHandler(Handler handler)
	{
		mainHandler = handler;
	}
	
	private void virbate()
	{
		Vibrator vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
		vibrator.vibrate(200);
	}

	private void setMusicViewsOnClick()
	{
		final View mMusicViews[] = {mPlayView, mNextView, mPrevView, mStopView};
		for (View view : mMusicViews)
		{
			view.setOnClickListener(this);
		}
	}
	
	private void setViewId()
	{
		if(DBG) Log.d(TAG, "setViewId()");
		mPlayView.setId(0);
		mNextView.setId(1);
		mPrevView.setId(2);
		mStopView.setId(3);
	}
	
	@Override
	public void onClick(View v) 
	{

	}
	
	private void setPlayViewDrawable()
	{
		mPlayView.setImageResource(R.drawable.play);
	}

	@Override
	protected void onAnimationEnd() {
		// TODO Auto-generated method stub
		super.onAnimationEnd();
		if (alpha != null)
		{
			alpha = null;
		}
		mAlphaView.setAnimation(null);
	}

	@Override
	protected void onAnimationStart() {
		// TODO Auto-generated method stub
		super.onAnimationStart();
		Log.v(TAG, "onAnimationStar()");
		mAlphaView.setVisibility(View.VISIBLE);

		if (alpha == null) {
			alpha = new AlphaAnimation(0.0f, 1.0f);
			alpha.setDuration(1000);
		}
		alpha.setRepeatCount(Animation.INFINITE);
		mAlphaView.startAnimation(alpha);
	}
	
}