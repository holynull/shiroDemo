package com.ultimatech.shirodemo.base.model;

import javax.persistence.*;

/**
 * Created by zhangleping on 16/7/30.
 */
@Entity
@Table(name = "t_authc_map")
public class AuthcMap {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAuthcType() {
        return authcType;
    }

    public void setAuthcType(String authcType) {
        this.authcType = authcType;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    private String url;
    private String authcType;
    private String val;
}
