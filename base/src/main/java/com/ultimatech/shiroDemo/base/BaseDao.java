package com.ultimatech.shiroDemo.base;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;

/**
 * Created by Think on 9/1 0001.
 */
public class BaseDao {

    private HibernateTemplate template;

    public HibernateTemplate getTemplate() {
        if (template == null) {
            this.setTemplate(new HibernateTemplate(this.getSessionFactory()));
        }
        return template;
    }

    public void setTemplate(HibernateTemplate template) {
        this.template = template;
    }

    private SessionFactory sessionFactory;

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public BaseDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * 计算翻页参数
     *
     * @param query
     * @param page
     * @return
     */
    protected PageParameter assemblePageParameter(Query query, PageParameter page) {
        query.setFirstResult(0);
        query.setMaxResults(0);
        int totalCount = query.list().size();
        page.setTotalCount(totalCount);
        int totalPage;
        if (page.getPageSize() > 0) {
            totalPage = totalCount / page.getPageSize()
                    + ((totalCount % page.getPageSize() == 0) ? 0 : 1);
        } else {
            totalPage = 1;
        }
        page.setTotalPage(totalPage);
        page.setStartIndex((page.getCurrentPage() - 1) * page.getPageSize());
        return page;
    }
}
