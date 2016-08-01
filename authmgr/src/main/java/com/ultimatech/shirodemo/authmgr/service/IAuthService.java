package com.ultimatech.shirodemo.authmgr.service;

import com.ultimatech.shirodemo.base.model.AuthcMap;
import org.apache.shiro.authc.AuthenticationException;

import java.util.List;

/**
 * Created by 张乐平 on 7/28 0028.
 */
public interface IAuthService {
    /**
     * 用户登录接口
     * @param userName 登录用户名
     * @param password 密码
     * @throws AuthenticationException
     */
    void logIn(String userName, String password) throws AuthenticationException;

    /**
     * 用户登出系统
     */
    void logOut();

    /**
     * 获得数据库中存储的访问控制数据
     * @return
     */
    List<AuthcMap> getFilterChainDefinitions();
}
