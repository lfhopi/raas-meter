package com.beetech.service;

import java.util.TimeZone;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.tanukisoftware.wrapper.WrapperListener;
import org.tanukisoftware.wrapper.WrapperManager;

public class MeterListener implements WrapperListener {

	private static Logger logger = Logger.getLogger(MeterListener.class);

	public static void main(String[] args) {
//		args = new String[]{
//			 	 "classpath:applicationContext.xml",
//			 	 "classpath:log4j.properties"
//		};
		WrapperManager.start(new MeterListener(), args);//开始，并传入String[]，在Run Configurations中传入配置文件路径字符串，将加载对应的配置文件。
	}

	public Integer start(String[] args) {
		
		if (args == null || args.length == 0) {
			return null;
		}
		System.out.println("============"+args[0]);
		
		//设置系统默认的时区
		TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));
		System.out.println("============"+args[1]);  
		PropertyConfigurator.configure(args[1]);
		
		new FileSystemXmlApplicationContext(args[0]);
		
		
		return null;
	}

	public int stop(int exitCode) {
		logger.info("------------------------------------------"+ exitCode);
		return exitCode;
	}

	public void controlEvent(int event) {
		logger.info("controlEvent(" + event + ")");
	}
}