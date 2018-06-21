package com.beetech.meter.dao;

import java.io.Serializable;

public interface IShareBaseDAO{
	public void saveObject(Object obj);
	
	public void updateObject(Object obj);
	
	public void saveOrUpdateObject(Object obj);
	
	public void deleteObject(Object obj);
	
	@SuppressWarnings("rawtypes")
	public Object getObjectById(Class objClass,Serializable id);
	
}
