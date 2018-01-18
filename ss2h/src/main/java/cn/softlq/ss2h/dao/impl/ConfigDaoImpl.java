package cn.softlq.ss2h.dao.impl;

import cn.softlq.ss2h.dao.IConfigDao;
import cn.softlq.ss2h.po.TSimpleColEntity;
import cn.softlq.ss2h.po.TSurveyColEntity;
import cn.softlq.ss2h.po.TSurveyTypeEntity;
import cn.softlq.ss2h.vo.ConfigItemView;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ConfigDaoImpl implements IConfigDao {
    @Autowired
    private HibernateTemplate hibernateTemplate;
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public List<TSurveyTypeEntity> getVisibleConfig() throws Exception {
        Session session = sessionFactory.openSession();
        String hql = "FROM TSurveyTypeEntity";
        Query query = session.createQuery(hql);
        List<TSurveyTypeEntity> list = query.list();
        session.close();
        return list;
    }

    @Override
    public List<ConfigItemView> configFindAll(int currentTotal, int current, String typeId) throws Exception {
        Session session = sessionFactory.openSession();
        String hql;
        if (Integer.parseInt(typeId) < 1) {
            hql = "FROM TSurveyTypeEntity";
        } else {
            hql = "FROM TSurveyTypeEntity WHERE typeId = ?";
        }
        Query query = session.createQuery(hql);
        List<ConfigItemView> items = new ArrayList<>();
        if (Integer.parseInt(typeId) >= 1) {
            query.setParameter(0, typeId);
            //获取唯一的专项
            TSurveyTypeEntity result = (TSurveyTypeEntity) query.uniqueResult();
            //根据typeId查找 currentTotal * (current - 1) 到 currentTotal * (current - 1)+currentTotal数据

            //找到所有相关的数据
            Query query2 = session.createSQLQuery("SELECT COL,COL_NAME FROM T_SURVEY_COL WHERE TYPE_ID = ?")
                    .setParameter(0, result.getTypeId())
                    .addScalar("COL", StandardBasicTypes.STRING)
                    .addScalar("COL_NAME", StandardBasicTypes.STRING);

            List<TSurveyColEntity> list = new ArrayList<>();
            //取出一条数据
            for (Object o : query2.list()) {
                Object[] objects = (Object[]) o;
                String col = (String) objects[0];
                String colName = (String) objects[1];
                TSurveyColEntity surveyColEntity = new TSurveyColEntity(typeId, col, colName);
                list.add(surveyColEntity);
            }

            for (int i = currentTotal * (current - 1); i < list.size(); i++) {
                TSurveyColEntity surveyTypeEntity = list.get(i);

                ConfigItemView configItemView = new ConfigItemView();
                configItemView.setSurveyType(result.getSurveyType());//专项名称
                configItemView.setShowNum(String.valueOf(result.getShowNum()));//展示数量
                configItemView.setCol(surveyTypeEntity.getCol());//显示字段
                configItemView.setColName(surveyTypeEntity.getColName());//字段说明

                items.add(configItemView);
                if (i == (currentTotal * (current - 1) + currentTotal)) break;
            }
        } else {
            //获取所有的专项
            List<TSurveyTypeEntity> list = query.list();
            List<TSurveyColEntity> list2 = new ArrayList<>();
            int index = 0;//计数器
            //获取到多少条就退出。
            isend:
            for (TSurveyTypeEntity result : list) {
                //根据typeId查找 currentTotal * (current - 1) 到 currentTotal * (current - 1)+currentTotal数据

                //找到所有相关的数据
                Query query2 = session.createSQLQuery("SELECT COL,COL_NAME FROM T_SURVEY_COL WHERE TYPE_ID = ?")
                        .setParameter(0, result.getTypeId())
                        .addScalar("COL", StandardBasicTypes.STRING)
                        .addScalar("COL_NAME", StandardBasicTypes.STRING);

                //取出一条数据
                for (Object o : query2.list()) {
                    Object[] objects = (Object[]) o;
                    String col = (String) objects[0];
                    String colName = (String) objects[1];
                    TSurveyColEntity surveyColEntity = new TSurveyColEntity(typeId, col, colName);
                    list2.add(surveyColEntity);
                }
                for (int i = currentTotal * (current - 1); i < list2.size(); i++) {
                    TSurveyColEntity surveyTypeEntity = list2.get(i);

                    ConfigItemView configItemView = new ConfigItemView();
                    configItemView.setSurveyType(result.getSurveyType());//专项名称
                    configItemView.setShowNum(String.valueOf(result.getShowNum()));//展示数量
                    configItemView.setCol(surveyTypeEntity.getCol());//显示字段
                    configItemView.setColName(surveyTypeEntity.getColName());//字段说明

                    items.add(configItemView);
                    index++;
                    if (index == (currentTotal * (current - 1) + currentTotal)) break isend;
                }
            }
        }
        session.close();
        return items;
    }

    @Override
    public Integer configFindAllTotal(String typeId) throws Exception {
        Session session = sessionFactory.openSession();
        String hql;
        if (Integer.parseInt(typeId) < 1) {
            hql = "FROM TSurveyTypeEntity";
        } else {
            hql = "FROM TSurveyTypeEntity WHERE typeId = ?";
        }
        Query query = session.createQuery(hql);
        Integer total = 0;
        if (Integer.parseInt(typeId) >= 1) {
            query.setParameter(0, typeId);
            //获取唯一的专项
            TSurveyTypeEntity result = (TSurveyTypeEntity) query.uniqueResult();
            //根据typeId查找 currentTotal * (current - 1) 到 currentTotal * (current - 1)+currentTotal数据

            //找到所有相关的数据
            Query query2 = session.createSQLQuery("SELECT COL,COL_NAME FROM T_SURVEY_COL WHERE TYPE_ID = ?")
                    .setParameter(0, result.getTypeId())
                    .addScalar("COL", StandardBasicTypes.STRING)
                    .addScalar("COL_NAME", StandardBasicTypes.STRING);

            List<TSurveyColEntity> list = new ArrayList<>();
            //取出一条数据
            for (Object o : query2.list()) {
                Object[] objects = (Object[]) o;
                String col = (String) objects[0];
                String colName = (String) objects[1];
                TSurveyColEntity surveyColEntity = new TSurveyColEntity(typeId, col, colName);
                list.add(surveyColEntity);
            }
            total = list.size();
        } else {
            //获取所有的专项
            List<TSurveyTypeEntity> list = query.list();
            List<TSurveyColEntity> list2 = new ArrayList<>();
            for (TSurveyTypeEntity result : list) {
                //根据typeId查找 currentTotal * (current - 1) 到 currentTotal * (current - 1)+currentTotal数据

                //找到所有相关的数据
                Query query2 = session.createSQLQuery("SELECT COL,COL_NAME FROM T_SURVEY_COL WHERE TYPE_ID = ?")
                        .setParameter(0, result.getTypeId())
                        .addScalar("COL", StandardBasicTypes.STRING)
                        .addScalar("COL_NAME", StandardBasicTypes.STRING);

                //取出一条数据
                for (Object o : query2.list()) {
                    Object[] objects = (Object[]) o;
                    String col = (String) objects[0];
                    String colName = (String) objects[1];
                    TSurveyColEntity surveyColEntity = new TSurveyColEntity(typeId, col, colName);
                    list2.add(surveyColEntity);
                }
            }
            total = list2.size();
        }
        session.close();
        return total;
    }

    @Override
    public Integer configDelete(ConfigItemView configItemView) throws Exception {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        String sql = "DELETE FROM T_SURVEY_COL WHERE TYPE_ID = ? AND COL = ? AND  COL_NAME = ?";
        NativeQuery query = session.createSQLQuery(sql);

        //根据调查类型名称值找到id
        TSurveyTypeEntity surveyTypeEntity = configFindOne(configItemView.getSurveyType());

        //删除
        query.setParameter(0, surveyTypeEntity.getTypeId());
        query.setParameter(1, configItemView.getCol());
        query.setParameter(2, configItemView.getColName());
        Integer result = query.executeUpdate();
        transaction.commit();
        session.close();
        return result;
    }

    @Override
    public TSurveyTypeEntity configFindOne(String surveyType) throws Exception {
        Session session = sessionFactory.openSession();
        //根据调查类型名称值找到id
        Query query = session.createQuery("FROM TSurveyTypeEntity WHERE surveyType = ?");
        query.setParameter(0, surveyType);
        TSurveyTypeEntity surveyTypeEntity = (TSurveyTypeEntity) query.uniqueResult();
        session.close();
        return surveyTypeEntity;
    }

    @Override
    public void configUpdate(TSurveyTypeEntity surveyTypeEntity) throws Exception {
        hibernateTemplate.update(surveyTypeEntity);
    }

    @Override
    public List<TSimpleColEntity> configFindTabCol(String typeId) throws Exception {
        Session session = sessionFactory.openSession();
        Query query = session.createQuery("FROM TSurveyTypeEntity WHERE typeId = ?");
        query.setParameter(0, typeId);
        TSurveyTypeEntity surveyTypeEntity = (TSurveyTypeEntity) query.uniqueResult();
        String sql = "SELECT COL,COL_NAME FROM T_SIMPLE_COL WHERE TAB = ?";
        NativeQuery query2 = session.createSQLQuery(sql)
                .setParameter(0, surveyTypeEntity.getTab())
                .addScalar("COL", StandardBasicTypes.STRING)
                .addScalar("COL_NAME", StandardBasicTypes.STRING);
        List<TSimpleColEntity> list = new ArrayList<>();
        for (Object o : query2.list()) {
            Object[] objects = (Object[]) o;
            String col = (String) objects[0];
            String colName = (String) objects[1];
            TSimpleColEntity simpleColEntity = new TSimpleColEntity(col, colName);
            list.add(simpleColEntity);
        }
        session.close();
        return list;
    }

    @Override
    public void configAdd(TSurveyTypeEntity surveyTypeEntity, String[] fruit) throws Exception {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        String sql = "INSERT INTO T_SURVEY_COL(TYPE_ID, COL, COL_NAME) VALUES (?,?,?)";
        for (int i = 0; i < fruit.length; i++) {
            NativeQuery sqlQuery = session.createSQLQuery(sql);
            sqlQuery.setParameter(0, configFindOne(surveyTypeEntity.getSurveyType()).getTypeId());
            sqlQuery.setParameter(1, fruit[i]);
            String sql2 = "SELECT COL_NAME FROM T_SURVEY_COL WHERE COL = ? AND ROWNUM = 1";
            NativeQuery query2 = session.createSQLQuery(sql2)
                    .setParameter(0, fruit[i])
                    .addScalar("COL_NAME", StandardBasicTypes.STRING);
            String str = (String) (query2.list().get(0));
            sqlQuery.setParameter(2, str);
            sqlQuery.executeUpdate();
        }
        transaction.commit();
        session.close();
    }
}
