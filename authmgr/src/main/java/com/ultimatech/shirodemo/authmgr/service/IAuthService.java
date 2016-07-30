package com.ultimatech.shirodemo.authmgr.service;

import com.ultimatech.shirodemo.base.model.AuthcMap;
import org.apache.shiro.authc.AuthenticationException;

import java.util.List;

/**
 * Created by 张乐平 on 7/28 0028.
 */
public interface IAuthService {
    void logIn(String userName, String password) throws AuthenticationException;

    void logOut();

    List<AuthcMap> getFilterChainDefinitions();
}
