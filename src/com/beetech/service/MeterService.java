package com.beetech.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.beetech.meter.dao.IMeterDataDao;
import com.beetech.util.DateUtils;
import com.beetech.util.MeterUtils;
import com.xianhua.tempmonitor.po.MeterData;
import com.xianhua.tempmonitor.po.MeterDataDaily;
import com.xianhua.tempmonitor.po.MeterT;

public class MeterService {
	private static Logger logger = Logger.getLogger(MeterService.class);

	public void init() {
		logger.info("---------------------------------------------------init");
	}
	
	public static final long REFRESH_INTERVAL = 60000;
	public static long TOKEN_RECORD_TIME;
	public static String TOKEN;
	public static List<Integer> METER_ID;
	public MeterUtils meterUtils;
	private IMeterDataDao meterDataDao;

	public static synchronized void refreshToken() {
		// httpclient 每隔一小时获取一次此token 并存放于全局变量中。
		Long NowTime = System.currentTimeMillis();
		// 如果TOKEN为空，或者现在的时间减去上次获取token记录的时间大于1小时的常量了，均需要执行重新获取TOKEN的任务。
		if (StringUtils.isEmpty(MeterService.getTOKEN())
				|| (NowTime - MeterService.TOKEN_RECORD_TIME) > MeterService.REFRESH_INTERVAL) {
//			MeterService.setTOKEN(MeterUtils.getToken());
			MeterUtils.getToken();
			System.out.println("TOKEN in MeterService --> " + MeterService.TOKEN);
			TOKEN_RECORD_TIME=NowTime;
			logger.info("--------------------------------------------------- TOKEN_RECORD_TIME --> "+TOKEN_RECORD_TIME);
		}
	}

	/**
	 * 写一个每天执行一次的更新或者插入按日电表统计数值的方法
	 */
	public void updateMeterDaily() {
//		MeterUtils.getMeterDataByDay(TOKEN, "", "");
	}

	public void mainService() {
		logger.info("---------------------------------------------------mainService activated");
		String meterIdParam = new String();
		Map<Integer, String> IdUserMap = new HashMap<Integer, String>();
		Map<Integer,Float> meterTMap = new HashMap<Integer, Float>();
		// 获取token
		MeterService.refreshToken();
		// 查询meterT表，规定必须有电表数据，如果为空，就返回
		List<MeterT> meterTList = meterDataDao.searchMeterT();
		if (meterTList == null||meterTList.isEmpty()) {
			logger.info("---------------------------------------------------meterT is empty");
			return;
		}
		else {
			//将查到的meterT表中的所有meterId的值变成 {"meterid1","meterid2","meterid3" ....}
			for (MeterT meterT : meterTList) {
				Integer meterId = meterT.getMeterId();
				Float meterTempData = meterT.getMeterTempData();
				if (null != meterId) {
					meterTMap.put(meterId, meterTempData);
					meterIdParam += meterId+",";
					//对每一个meterId都执行一次接口4 获取电表用户信息，并写入map中形成对应关系
					String username = MeterUtils.getMeterUser(MeterService.getTOKEN(), meterId);
					if(StringUtils.isNotEmpty(username)) {
						IdUserMap.put(meterId, username);
					}
				}
				else {
					logger.info("---------------------------------------------------meterT just got a null meterId");
					return;
				}
			}
			//把排好加好逗号的meterId最后一个多余的逗号去掉
	        String s =meterIdParam.substring(meterIdParam.length()-1,meterIdParam.length());
	        if(null!=s&&s.contains(",")) {
	        	meterIdParam = meterIdParam.substring(0,meterIdParam.length() - 1);
	        }
	        //使用接口3查出指定meterId的电表数据
	        List<MeterData> meterDataList = MeterUtils.getMeterData(MeterService.getTOKEN(),meterIdParam);
	    	//通过计算得出per_record的值。
			for (MeterData md : meterDataList) {
				Integer meterId = md.getMeterId();
				Date date = md.getReadTime();
				if(null==meterId||null==date) {
					continue;
				}
				Float meterTempData;
				if (null == meterTMap.get(meterId)) {
					meterTempData = 0f;
				} else {
					meterTempData = meterTMap.get(meterId);
				}
				// 已使用的总量-上次已使用的总量=这次就上次以来的增量
				Float meterUsed = md.getMeterUsed();
				if(null==meterUsed) {
					meterUsed = 0f;
				}
				Float meterUsedPerRecord = meterUsed - meterTempData;
				md.setMeterUsedPerRecord(meterUsedPerRecord);
				meterDataDao.saveMeterData(md);
				// 比较完毕存好meterData数据后，更新meterT表，插入新的总量和新的meterId
				if (null != meterId && null != meterUsed && null != date) {
					String username = IdUserMap.get(meterId);
					if(StringUtils.isNotEmpty(username)) {
						meterDataDao.updateMeterId(meterId, meterUsed,date,username);
					}
				}
			}
			
			String date = DateUtils.parseDateToString(new Date(), DateUtils.C_YYYY_MM);
			List<MeterDataDaily> mddList =MeterUtils.getMeterDataByDay(TOKEN,meterIdParam, date);
			for(MeterDataDaily mdd : mddList) {
				//每个meterDataDaily对象经过处理判断后选择更新或者插入到库中
				int count = meterDataDao.updateMeterDataDaily(mdd);
				if(count ==1){
					//假如更新成功，则返回继续执行下一个mdd
					continue;
				}
				else {
					//如果更新失败，则执行插入操作。
					meterDataDao.saveMeterDataDaily(mdd);
				}
			}
		}
	}
	
	public void updateMeterTempData() {

	}

	// --------------------------set--------------------------------------
	public static void setTOKEN(String tOKEN) {
		TOKEN = tOKEN;
	}

	public static void setMETER_ID(List<Integer> mETER_ID) {
		METER_ID = mETER_ID;
	}

	public static String getTOKEN() {
		return TOKEN;
	}
	public IMeterDataDao getMeterDataDao() {
		return meterDataDao;
	}

	public void setMeterDataDao(IMeterDataDao meterDataDao) {
		this.meterDataDao = meterDataDao;
	}

	public static void main(String[] args) {
//		/raas-meter/conf/applicationContext.xml
	}
}