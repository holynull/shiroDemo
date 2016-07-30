package com.ultimatech.shirodemo.authmgr.service.impl;

import com.ultimatech.shirodemo.authmgr.dao.IAuthDao;
import com.ultimatech.shirodemo.authmgr.service.IAuthService;
import com.ultimatech.shirodemo.base.model.AuthcMap;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by 张乐平 on 7/28 0028.
 */
@Service("authSvr")
public class AuthServiceImpl implements IAuthService {

    public IAuthDao getAuthDao() {
        return authDao;
    }

    public void setAuthDao(IAuthDao authDao) {
        this.authDao = authDao;
    }

    @Autowired
    private IAuthDao authDao;

    public void logIn(String userName, String password) throws AuthenticationException {
        UsernamePasswordToken token= new UsernamePasswordToken(userName,password);
        SecurityUtils.getSubject().login(token);
    }

    public void logOut() {
        SecurityUtils.getSubject().logout();
    }

    public List<AuthcMap> getFilterChainDefinitions() {
        return this.getAuthDao().getAllAuthcMap();
    }
}
