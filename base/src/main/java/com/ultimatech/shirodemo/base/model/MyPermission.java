package com.ultimatech.shirodemo.base.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by 张乐平 on 7/28 0028.
 */
@Entity
@Table(name="t_permission")
public class MyPermission {

    private Integer id;
    @NotNull
    private String dataType;
    @NotNull
    private String operation;
    private String dataDomain;
    private Role role;//一个权限对应一个角色

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="role_id")
    public Role getRole() {
        return role;
    }
    public void setRole(Role role) {
        this.role = role;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getDataDomain() {
        return dataDomain;
    }

    public void setDataDomain(String dataDomain) {
        this.dataDomain = dataDomain;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
}
