package com.ultimatech.shirodemo.base.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 张乐平 on 7/28 0028.
 */
@Entity
@Table(name = "t_role")
public class Role {

    private Integer id;
    private String rolename;
    private List<MyPermission> permissionList;//一个角色对应多个权限
    private List<User> userList;//一个角色对应多个用户

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRolename() {
        return rolename;
    }

    public void setRolename(String rolename) {
        this.rolename = rolename;
    }

    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
    public List<MyPermission> getPermissionList() {
        return permissionList;
    }

    public void setPermissionList(List<MyPermission> permissionList) {
        this.permissionList = permissionList;
    }

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "t_user_role", joinColumns = {@JoinColumn(name = "role_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")})
    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    @Transient
    public List<String> getPermissionsString() {
        List<String> list = new ArrayList<String>();
        for (MyPermission per : this.permissionList) {
            list.add(per.getDataType() + ":" + per.getOperation() + (per.getDataDomain() == null || per.getDataDomain()
                    .equals("") ? "" : (":" + per.getDataDomain())));
        }
        return list;
    }
}
