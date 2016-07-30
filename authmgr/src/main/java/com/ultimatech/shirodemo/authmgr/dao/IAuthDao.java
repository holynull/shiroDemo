package com.ultimatech.shirodemo.authmgr.dao;


import com.ultimatech.shirodemo.base.model.AuthcMap;
import com.ultimatech.shirodemo.base.model.User;

import java.util.List;

/**
 * Created by 张乐平 on 7/28 0028.
 */
public interface IAuthDao {

    User findByName(String name);
    List<AuthcMap> getAllAuthcMap();
}
