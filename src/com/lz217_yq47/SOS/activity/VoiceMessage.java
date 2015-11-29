package com.lz217_yq47.SOS.activity;

import java.io.Serializable;

public class VoiceMessage implements Serializable{
	private static final long serialVersionUID = -7060210544600464482L;
	private String filePath;
	private String fileName;
	private String length;
	private boolean flag;
	
	public void setFilePath(String fP){
		this.filePath = fP;
	}
	public void setFileName(String fN){
		this.fileName = fN;
	}
	public void setLength(String len){
		this.length = len;
	}
	public void setFlag(boolean f){
		this.flag = f;
	}
	
	public String getFilePath(){
		return this.filePath;
	}
	public String getFileName(){
		return this.fileName;
	}
	public String getLength(){
		return this.length;
	}
	public boolean getFlag(){
		return this.flag;
	}
}
