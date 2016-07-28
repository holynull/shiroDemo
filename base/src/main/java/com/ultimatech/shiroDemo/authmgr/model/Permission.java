package com.ultimatech.shiroDemo.authmgr.model;

import javax.persistence.*;

/**
 * Created by 张乐平 on 7/28 0028.
 */
@Entity
@Table(name="t_permission")
public class Permission {

    private Integer id;
    private String permissionname;
    private Role role;//一个权限对应一个角色

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getPermissionname() {
        return permissionname;
    }
    public void setPermissionname(String permissionname) {
        this.permissionname = permissionname;
    }
    @ManyToOne
    @JoinColumn(name="role_id")
    public Role getRole() {
        return role;
    }
    public void setRole(Role role) {
        this.role = role;
    }
}
