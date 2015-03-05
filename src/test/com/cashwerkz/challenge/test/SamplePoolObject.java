package com.cashwerkz.challenge.test;

import java.util.UUID;

public class SamplePoolObject{
	
	private String objectName;
	
	public SamplePoolObject(String objectName){
		this.objectName = objectName;
	}
	
	public SamplePoolObject(){
		this.objectName = UUID.randomUUID().toString().substring(0, 3);
	}
	
	@Override
	public String toString() {
		return objectName;
	}
		
	public String getName(){
		return objectName;
	}
}
