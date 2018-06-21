package com.beetech.service;

import org.springframework.context.support.FileSystemXmlApplicationContext;

public class TestService{
	
	private MeterService meterService;

	public MeterService getMeterService() {
		return meterService;
	}

	public void setMeterService(MeterService meterService) {
		this.meterService = meterService;
	}
	
	public void init() {
		meterService.mainService();
	}
	
	public static void main(String[] args) {
		args = new String[]{
			 	 "classpath:applicationContext.xml",
			 	 "classpath:log4j.properties"
		};
		FileSystemXmlApplicationContext context = new FileSystemXmlApplicationContext(args[0]);
		context.start();
		while(true) {
			
		}
	}


}
