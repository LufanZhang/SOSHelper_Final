package com.lz217_yq47.SOS.activity;
/**
 * LocationInformation contains latitude, longitude and address, this class implements Serializable in order to
 * be passed to BroadcastReceiver by bundle
 */
import java.io.Serializable;
//在Android中的不同Activity之间传递对象，我们可以考虑采用Bundle.putSerializable(Key,Object);
public class LocationInformation implements Serializable{
	private static final long serialVersionUID = -7060210544600464481L;   
	private double lat;
	private double longi;
	private String address;
	
	LocationInformation(double latitude, double longitude,String addr){
		this.lat = latitude;
		this.longi = longitude;
		this.address = addr;
	}
	public void setLat(double lat){
		this.lat = lat;
	}
	
	public void setLongi(double longi){
		this.longi = longi;
	}
	
	public void setAddress(String address){
		this.address = address;
	}
	public double getLat(){
		return this.lat;
	}
	public double getLongi(){
		return this.longi;
	}
	public String getAddress(){
		return this.address;
	}
}
