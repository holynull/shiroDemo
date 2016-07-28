package com.ultimatech.shirodemo.authmgr.service.impl;

import com.ultimatech.shirodemo.authmgr.service.IAuthService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.springframework.stereotype.Service;

/**
 * Created by 张乐平 on 7/28 0028.
 */
@Service("authSvr")
public class AuthServiceImpl implements IAuthService {

    public void logIn(String userName, String password) throws AuthenticationException {
        UsernamePasswordToken token= new UsernamePasswordToken(userName,password);
        SecurityUtils.getSubject().login(token);
    }

    public void logOut() {
        SecurityUtils.getSubject().logout();
    }
}
