package com.beetech.meter.dao.hibernate;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.beetech.meter.dao.IMeterDataDao;
import com.beetech.service.MeterService;
import com.beetech.util.DateUtils;
import com.xianhua.tempmonitor.po.MeterData;
import com.xianhua.tempmonitor.po.MeterDataDaily;
import com.xianhua.tempmonitor.po.MeterT;

public class MeterDataDaoImpl extends ShareBaseDAO implements IMeterDataDao {

	private static Logger logger = Logger.getLogger(MeterService.class);

	@Override
	public void saveMeterData(MeterData meterData) {
		if (null != meterData) {
			getHibernateTemplate().save(meterData);
		}
	}

	@Override
	public void updateMeterId(Integer meterId, Float meterUsed, Date date, String username) {
		SessionFactory sf = this.getHibernateTemplate().getSessionFactory();
		Session s = sf.getCurrentSession();
		String datestr = DateUtils.parseDateToString(date, DateUtils.C_YYYY_MM_DD_HH_MM_SS);
		String sql = "update meter_t t set t.meter_temp_data = " + meterUsed + " , t.read_time = '" + datestr
				+ "' , t.username = '"+username+"' where t.meter_id = " + meterId;
		logger.info("------------------------ sql -----------------------   " + sql);
		s.createSQLQuery(sql).executeUpdate();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<MeterT> searchMeterT() {
		return this.getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				String hql = "from MeterT t ";
				Query query = session.createQuery(hql);
				return query.list();
			}
		});
	}

	@Override
	public int updateMeterDataDaily(MeterDataDaily mdd) {
		SessionFactory sf = this.getHibernateTemplate().getSessionFactory();
		Session s = sf.getCurrentSession();
		Date bt = mdd.getBeforeTime();
		Date rt = mdd.getReadTime();
		String btstr= DateUtils.parseDateToString(bt, DateUtils.C_YYYY_MM_DD);
		String rtstr = DateUtils.parseDateToString(rt, DateUtils.C_YYYY_MM_DD);
//		String datestr = DateUtils.parseDateToString(date, DateUtils.C_YYYY_MM_DD_HH_MM_SS);
//		String sql = "update meter_t t set t.meter_temp_data = " + meterUsed + " , t.read_time = '" + datestr
//				+ "' , t.username = '"+username+"' where t.meter_id = " + meterId;
		String sql = "update meter_data_daily m set m.read_time= '"+rtstr+"' , m.before_time= '"+btstr+"', "
		+ "m.before_value = "+mdd.getBeforeValue()+", m.read_value="+mdd.getReadValue()+", m.cha = "+mdd.getCha() +"where m.meter_address = "+mdd.getMeterAddress()+
		" and m.read_time = '"+rtstr+"'";
		logger.info("----------------------- sql in update MeterDataDaily -----------------------  "+sql);
		int i = s.createSQLQuery(sql).executeUpdate();
		return i;
	}

	@Override
	public void saveMeterDataDaily(MeterDataDaily mdd) {
		SessionFactory sf = this.getHibernateTemplate().getSessionFactory();
		Session s = sf.getCurrentSession();
		Date bt = mdd.getBeforeTime();
		Date rt = mdd.getReadTime();
		String btstr= DateUtils.parseDateToString(bt, DateUtils.C_YYYY_MM_DD);
		String rtstr = DateUtils.parseDateToString(rt, DateUtils.C_YYYY_MM_DD);
		String sql = "insert into meter_data_daily"
				+ "(read_time,read_value,before_time,before_value,meter_address,cha) values"
				+ " ( '"+rtstr+"' ,"+mdd.getReadValue()+", '"+btstr+"' ,"+mdd.getBeforeValue()+", "+mdd.getMeterAddress()+","+mdd.getCha()+")";
		logger.info("----------------------- sql in saveMeterDataDaily -----------------------  "+sql);
		s.createSQLQuery(sql).executeUpdate();
	}
}