package com.ultimatech.shirodemo.authmgr.dao.impl;

import com.ultimatech.shirodemo.base.model.User;
import com.ultimatech.shirodemo.base.BaseDao;
import com.ultimatech.shirodemo.authmgr.dao.IAuthDao;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by 张乐平 on 7/28 0028.
 */
@Component("authDao")
public class AuthDao extends BaseDao implements IAuthDao {
    @Autowired
    public AuthDao(@Qualifier("sessionFactory") SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public User findByName(String name) {
        Criteria cri = this.getSessionFactory().getCurrentSession().createCriteria(User.class);
        cri.add(Restrictions.eq("username", name));
        List<User> list = cri.list();
        return list != null && list.size() > 0 ? list.get(0) : null;
    }
}
