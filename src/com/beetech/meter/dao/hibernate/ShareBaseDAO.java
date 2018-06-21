package com.beetech.meter.dao.hibernate;

import java.sql.SQLException;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class ShareBaseDAO extends HibernateDaoSupport{
	public void setMySessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

    @SuppressWarnings("rawtypes")
	public List queryWithSql(final String sql,final String entityNote, final Class entityClass){
        List list = getHibernateTemplate().executeFind(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Query query = session.createSQLQuery(sql).addEntity(entityNote,entityClass);
                List list = query.list();
                return list;
            }
        });
        return list;
    }
    
}
