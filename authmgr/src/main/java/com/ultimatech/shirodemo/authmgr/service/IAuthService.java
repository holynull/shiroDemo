package com.ultimatech.shirodemo.authmgr.service;

import org.apache.shiro.authc.AuthenticationException;

/**
 * Created by 张乐平 on 7/28 0028.
 */
public interface IAuthService {
    void logIn(String userName,String password) throws AuthenticationException;
    void logOut();
}
