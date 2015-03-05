package com.cashwerkz.challenge.test;

public class SamplePoolObject{
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + ":" + this.hashCode();
	}
		
}
