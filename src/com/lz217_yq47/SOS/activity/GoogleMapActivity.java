/*
 * this class was created by Lufan: 11/10/2014
 */
package com.lz217_yq47.SOS.activity;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.lz217_yq47.SOS.R;


public class GoogleMapActivity extends Activity implements
OnSeekBarChangeListener{
	private Button update;
   // private TextView longitude,latitude,address;
    private ImageView myImage;
    private GoogleMap mMap;
    private LocationManager locManager;
    private static final double EARTH_RADIUS = 6378137;  
    private Geocoder geocoder;
    private Location location;
    private String locationProvider; 
    private MarkerOptions markerOpt;
    private Marker mOpt;
    private Marker globalMarker;
    private CameraPosition cameraPosition;
    private String addr;
    private Bundle bundle = null;  
    private Intent locationMsgIntent = null;  
    private String groupID;
    

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressLint("NewApi")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.activity_googlemap);
    	findViews();
    	initProvider();
    	
    	mMap = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
    	mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    	Intent intent = getIntent();
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
          
          locManager.requestLocationUpdates(locationProvider,  60 * 1000, 0, locationListener); 
          System.out.println(android.os.Process.getThreadPriority(android.os.Process.myTid()));
	}

    private void updateToNewLocation(){
        mMap.clear();
        location = locManager.getLastKnownLocation(locationProvider);
        markerOpt = new MarkerOptions();
        double dLong = 0.00;
        double dLat = 0.00;
        
        Double[] lat_long = new Double[] { location.getLatitude(), location.getLongitude() };
		new ReverseGeocodingTask(getBaseContext()).execute(lat_long);
        
        if(location != null){
        	dLong = location.getLongitude();
        	dLat = location.getLatitude();
        	initMarker(dLat,dLong,markerOpt,"0.00");
        	markerOpt.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        	
        	markerOpt.anchor(0.5f, 0.5f);
        	mOpt = mMap.addMarker(markerOpt);
        	
        	cameraPosition = new CameraPosition.Builder()
        		.target(new LatLng(dLat, dLong))              
            	.zoom(12)                   
            	.bearing(0)                
            	.tilt(30)                  
            	.build();                   
            	mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    
    private void initMarker(double dLat,double dLong,MarkerOptions marker,String distance){
    	marker.position(new LatLng(dLat, dLong));
    	marker.draggable(false);
    	marker.visible(true);
    	bundle = new Bundle();  
    }
    private double rad(double d) {  
        return d * Math.PI / 180.0;  
    } 
   
    private void initProvider() {
    	locManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
    	locationProvider = LocationManager.NETWORK_PROVIDER;
    	//locationProvider = LocationManager.GPS_PROVIDER;
    	location = locManager.getLastKnownLocation(locationProvider);
    }
    
    private void findViews() {
    	update = (Button)findViewById(R.id.updateBtn);
    	update.setOnClickListener(new updateListener());
     }
    //use AsyncTask to get the address from latitude and longitude
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
				addresses = geocoder.getFromLocation(latitude, longitude, 1);
			} catch (IOException e) {
				//System.out.println("IOException e");
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
		@Override
		protected void onPostExecute(String addressText) {
			
		}
	}
	public void sendLocation(View view) {
		Intent intent = this.getIntent();
		intent.putExtra("latitude", location.getLatitude());
		intent.putExtra("longitude", location.getLongitude());
		intent.putExtra("address", addr);
		this.setResult(RESULT_OK, intent);
		finish();
		overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
		
	}
	public void back(View v) {
		finish();
	}
  class updateListener implements OnClickListener{

	@Override
	public void onClick(View v) {
		sendLocation(v);
	}
	  
  }
  
@Override
public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
	// TODO Auto-generated method stub
}
@Override
public void onStartTrackingTouch(SeekBar seekBar) {
	// TODO Auto-generated method stub
}
@Override
public void onStopTrackingTouch(SeekBar seekBar) {
	// TODO Auto-generated method stub
	
}

}
