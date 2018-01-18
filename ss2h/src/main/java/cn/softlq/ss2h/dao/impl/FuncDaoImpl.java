package cn.softlq.ss2h.dao.impl;

import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cn.softlq.ss2h.dao.IFuncDao;
import cn.softlq.ss2h.po.TFuncEntity;
import cn.softlq.ss2h.po.TUserEntity;
@Repository
@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW )
public class FuncDaoImpl implements IFuncDao {
@Autowired
 private HibernateTemplate hibernateTemplate;
@Autowired
 private SessionFactory sessionFactory;
   @Override
	public List<TFuncEntity> findFuncAll(int currentTotal, int current) {
		// TODO Auto-generated method stub
	   Session session = sessionFactory.openSession();
       String hql;
       hql="FROM TFuncEntity";
       Query query = session.createQuery(hql);
       query.setFirstResult(currentTotal * (current - 1));
       query.setMaxResults(currentTotal);
       List<TFuncEntity> list = query.list();
       session.close();
       return list;
	}

	@Override
	public Integer findFuncTotal() {
		// TODO Auto-generated method stub
		Session session = sessionFactory.openSession();
        String hql;
        hql="FROM TFuncEntity";
        Query query = session.createQuery(hql);
        List<TUserEntity> list = query.list();
        session.close();
        return list.size();
	}

	@Override
	public TFuncEntity findOne(String fId) {
		// TODO Auto-generated method stub
		 return hibernateTemplate.get(TFuncEntity.class,fId);
	}

	@Override
	public void funcUpdate(TFuncEntity funcentity) throws Exception {
		// TODO Auto-generated method stub
		 hibernateTemplate.update(funcentity);
	}

	@Override
	public void delete(TFuncEntity funcentity) throws Exception {
		// TODO Auto-generated method stub
		 hibernateTemplate.delete(funcentity);
	}

	@Override
	public void add(TFuncEntity funcentity) {
		// TODO Auto-generated method stub
		 hibernateTemplate.save(funcentity);
	}

}
