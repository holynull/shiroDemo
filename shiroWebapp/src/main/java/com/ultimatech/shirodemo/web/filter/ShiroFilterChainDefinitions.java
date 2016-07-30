package com.ultimatech.shirodemo.web.filter;

import com.ultimatech.shirodemo.authmgr.service.IAuthService;
import com.ultimatech.shirodemo.base.AuthcType;
import com.ultimatech.shirodemo.base.model.AuthcMap;
import org.apache.shiro.config.Ini;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.MessageFormat;
import java.util.List;

/**
 * Created by zhangleping on 16/7/30.
 */
public class ShiroFilterChainDefinitions implements FactoryBean<Ini.Section> {

    @Autowired
    private IAuthService authService;

    private String filterChainDefinitions;

    public IAuthService getAuthService() {
        return authService;
    }

    public void setAuthService(IAuthService authService) {
        this.authService = authService;
    }

    public String getFilterChainDefinitions() {
        return filterChainDefinitions;
    }

    public void setFilterChainDefinitions(String filterChainDefinitions) {
        this.filterChainDefinitions = filterChainDefinitions;
    }

    public static final String PREMISSION_STRING = "perms[{0}]";

    public static final String ROLE_STRING = "roles[{0}]";

    public Ini.Section getObject() throws Exception {
        List<AuthcMap> list = this.getAuthService().getFilterChainDefinitions();
        Ini ini = new Ini();
        ini.load(this.getFilterChainDefinitions());
        Ini.Section section = ini.getSection(Ini.DEFAULT_SECTION_NAME);
        for (AuthcMap map : list) {
            String s = null;
            switch (AuthcType.valueOf(map.getAuthcType())) {
                case roles:
                    s = MessageFormat.format(ROLE_STRING, map.getVal());
                    break;
                case perms:
                    s = MessageFormat.format(PREMISSION_STRING, map.getVal());
                    break;
                case authc:
                    s = AuthcType.authc.name();
                case anon:
                    s = AuthcType.anon.name();
                default:
                    s = AuthcType.authc.name();
            }
            section.put(map.getUrl(), s);
        }
        return section;
    }

    public Class<?> getObjectType() {
        return this.getClass();
    }

    public boolean isSingleton() {
        return false;
    }
}
