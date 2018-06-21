package com.beetech.meter.dao;


import java.util.Date;
import java.util.List;

import com.xianhua.tempmonitor.po.MeterData;
import com.xianhua.tempmonitor.po.MeterDataDaily;
import com.xianhua.tempmonitor.po.MeterT;

public interface IMeterDataDao {
	public void updateMeterId(Integer meterId, Float meterUsed,Date date, String username);
	public void saveMeterData(MeterData meterData);
	public List<MeterT> searchMeterT();
	public int updateMeterDataDaily(MeterDataDaily mdd);
	public void saveMeterDataDaily(MeterDataDaily mdd);
}