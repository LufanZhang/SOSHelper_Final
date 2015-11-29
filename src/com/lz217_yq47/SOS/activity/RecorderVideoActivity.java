/************************************************************
  *  * EaseMob CONFIDENTIAL 
  * __________________ 
  * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved. 
  *  
  * NOTICE: All information contained herein is, and remains 
  * the property of EaseMob Technologies.
  * Dissemination of this information or reproduction of this material 
  * is strictly forbidden unless prior written permission is obtained
  * from EaseMob Technologies.
  */
package com.lz217_yq47.SOS.activity;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.OnInfoListener;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.text.TextUtils;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.easemob.util.EMLog;
import com.easemob.util.PathUtil;
import com.lz217_yq47.SOS.R;
import com.lz217_yq47.SOS.video.util.Utils;

public class RecorderVideoActivity extends BaseActivity implements OnClickListener, Callback, OnErrorListener, OnInfoListener {

	private final static String CLASS_LABEL = "RecordActivity";
	private PowerManager.WakeLock mWakeLock;
	private ImageView btnStart;
	private ImageView btnStop;
	private MediaRecorder mediarecorder;
	private SurfaceView surfaceview;
	private SurfaceHolder surfaceHolder;
	String localPath = "";
	private Camera mCamera;
	private int previewWidth = 480;
	private int previewHeight = 480;

	Parameters cameraParameters = null;

	int defaultCameraId = -1, defaultScreenResolution = -1, cameraSelection = 0;
	int defaultVideoFrameRate = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		setContentView(R.layout.recorder_activity);
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, CLASS_LABEL);
		mWakeLock.acquire();

		btnStart = (ImageView) findViewById(R.id.recorder_start);
		btnStop = (ImageView) findViewById(R.id.recorder_stop);
		btnStart.setOnClickListener(this);
		btnStop.setOnClickListener(this);
		surfaceview = (SurfaceView) this.findViewById(R.id.surfaceview);
		SurfaceHolder holder = surfaceview.getHolder();// 
		holder.addCallback(this); // 
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	public void back(View view) {

		if (mediarecorder != null) {
			mediarecorder.stop();
			mediarecorder.release();
			mediarecorder = null;
		}
		try {
			mCamera.reconnect();
		} catch (IOException e) {
			Toast.makeText(this, "reconect fail", 0).show();
		}
		finish();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mWakeLock == null) {
			PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, CLASS_LABEL);
			mWakeLock.acquire();
		}
	}

	private void handleSurfaceChanged() {
		if (mCamera == null) {
			finish();
			return;
		}

		boolean hasSupportRate = false;
		List<Integer> supportedPreviewFrameRates = mCamera.getParameters().getSupportedPreviewFrameRates();
		if (supportedPreviewFrameRates != null && supportedPreviewFrameRates.size() > 0) {
			Collections.sort(supportedPreviewFrameRates);
			for (int i = 0; i < supportedPreviewFrameRates.size(); i++) {
				int supportRate = supportedPreviewFrameRates.get(i);

				if (supportRate == 15) {
					hasSupportRate = true;
				}

			}
			if (hasSupportRate) {
				defaultVideoFrameRate = 15;
			} else {
				defaultVideoFrameRate = supportedPreviewFrameRates.get(0);
			}

		}

		System.out.println("supportedPreviewFrameRates" + supportedPreviewFrameRates);

		List<Camera.Size> resolutionList = Utils.getResolutionList(mCamera);
		if (resolutionList != null && resolutionList.size() > 0) {
			Collections.sort(resolutionList, new Utils.ResolutionComparator());
			Camera.Size previewSize = null;
			if (defaultScreenResolution == -1) {
				boolean hasSize = false;
				for (int i = 0; i < resolutionList.size(); i++) {
					Size size = resolutionList.get(i);
					if (size != null && size.width == 640 && size.height == 480) {
						previewSize = size;
						previewWidth = previewSize.width;
						previewHeight = previewSize.height;
						hasSize = true;
						break;
					}
				}
				if (!hasSize) {
					int mediumResolution = resolutionList.size() / 2;
					if (mediumResolution >= resolutionList.size())
						mediumResolution = resolutionList.size() - 1;
					previewSize = resolutionList.get(mediumResolution);
					previewWidth = previewSize.width;
					previewHeight = previewSize.height;

				}

			}

		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mWakeLock != null) {
			mWakeLock.release();
			mWakeLock = null;
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.recorder_start:
			mCamera.unlock();
			mediarecorder = new MediaRecorder();
			mediarecorder.reset();
			mediarecorder.setCamera(mCamera);
			mediarecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
			mediarecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
			mediarecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
			mediarecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
			mediarecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
			
			mediarecorder.setVideoSize(previewWidth, previewHeight);
			mediarecorder.setVideoEncodingBitRate(384 * 1024);
			if (defaultVideoFrameRate != -1) {
				mediarecorder.setVideoFrameRate(defaultVideoFrameRate);
			}
			mediarecorder.setPreviewDisplay(surfaceHolder.getSurface());
			localPath = PathUtil.getInstance().getVideoPath() + "/" + System.currentTimeMillis() + ".mp4";
			mediarecorder.setOutputFile(localPath);
			mediarecorder.setOnErrorListener(this);
			mediarecorder.setOnInfoListener(this);
			try {
				mediarecorder.prepare();
				mediarecorder.start();
				Toast.makeText(this, "Record start", Toast.LENGTH_SHORT).show();
				btnStart.setVisibility(View.INVISIBLE);
				btnStop.setVisibility(View.VISIBLE);
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			break;
		case R.id.recorder_stop:

			if (mediarecorder != null) {
				mediarecorder.stop();
				mediarecorder.release();
				mediarecorder = null;
			}
			try {
				mCamera.reconnect();
			} catch (IOException e) {
				Toast.makeText(this, "reconect fail", 0).show();
			}
			btnStart.setVisibility(View.VISIBLE);
			btnStop.setVisibility(View.INVISIBLE);

			new AlertDialog.Builder(this).setMessage("Send it or not？").setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					arg0.dismiss();
					sendVideo(null);

				}
			}).setNegativeButton(R.string.cancel, null).show();

			break;

		default:
			break;
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		surfaceHolder = holder;

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		surfaceHolder = holder;
		try {
			initpreview();
		} catch (Exception e) {
			showFailDialog();
			return;
		}
		handleSurfaceChanged();

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		surfaceview = null;
		surfaceHolder = null;
		mediarecorder = null;
		releaseCamera();
	}

	protected void releaseCamera() {
		try {
			if (mCamera != null) {
				mCamera.stopPreview();
				mCamera.release();
				mCamera = null;
			}
		} catch (Exception e) {
		}
	}

	@SuppressLint("NewApi")
	protected void initpreview() throws Exception {
		try {

			if (Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO) {
				int numberOfCameras = Camera.getNumberOfCameras();
				CameraInfo cameraInfo = new CameraInfo();
				for (int i = 0; i < numberOfCameras; i++) {
					Camera.getCameraInfo(i, cameraInfo);
					if (cameraInfo.facing == cameraSelection) {
						defaultCameraId = i;
					}
				}

			}
			if (mCamera != null) {
				mCamera.stopPreview();
			}

			mCamera = Camera.open(CameraInfo.CAMERA_FACING_BACK);
			mCamera.setPreviewDisplay(surfaceHolder);
			setCameraDisplayOrientation(this, CameraInfo.CAMERA_FACING_BACK, mCamera);
			mCamera.startPreview();
		} catch (Exception e) {
			EMLog.e("###", e.getMessage());
			throw new Exception(e.getMessage());
		}

	}

	@SuppressLint("NewApi")
	public static void setCameraDisplayOrientation(Activity activity, int cameraId, android.hardware.Camera camera) {
		android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
		android.hardware.Camera.getCameraInfo(cameraId, info);
		int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
		int degrees = 0;
		switch (rotation) {
		case Surface.ROTATION_0:
			degrees = 0;
			break;
		case Surface.ROTATION_90:
			degrees = 90;
			break;
		case Surface.ROTATION_180:
			degrees = 180;
			break;
		case Surface.ROTATION_270:
			degrees = 270;
			break;
		}

		int result;
		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			result = (info.orientation + degrees) % 360;
			result = (360 - result) % 360; // compensate the mirror
		} else { // back-facing
			result = (info.orientation - degrees + 360) % 360;
		}
		camera.setDisplayOrientation(result);
	}

	MediaScannerConnection msc = null;

	public void sendVideo(View view) {
		if (TextUtils.isEmpty(localPath)) {
			EMLog.e("Recorder", "recorder fail please try again!");
			return;
		}

		msc = new MediaScannerConnection(this, new MediaScannerConnectionClient() {

			@Override
			public void onScanCompleted(String path, Uri uri) {
				System.out.println("scanner completed");
				msc.disconnect();
				setResult(RESULT_OK, getIntent().putExtra("uri", uri));
				finish();
			}

			@Override
			public void onMediaScannerConnected() {
				msc.scanFile(localPath, "video/*");
			}
		});
		msc.connect();

	}

	@Override
	public void onInfo(MediaRecorder arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onError(MediaRecorder arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		releaseCamera();

		if (mWakeLock != null) {
			mWakeLock.release();
			mWakeLock = null;
		}

	}

	@Override
	public void onBackPressed() {
		back(null);
	}

	private void showFailDialog() {
		new AlertDialog.Builder(this).setTitle("Notification").setMessage("Fail to open device！")
				.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();

					}
				}).setCancelable(false).show();

	}

}
