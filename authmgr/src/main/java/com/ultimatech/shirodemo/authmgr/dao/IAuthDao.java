package com.ultimatech.shirodemo.authmgr.dao;


import com.ultimatech.shiroDemo.authmgr.model.User;

/**
 * Created by 张乐平 on 7/28 0028.
 */
public interface IAuthDao {

    User findByName(String name);
}
