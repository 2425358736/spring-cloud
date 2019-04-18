package com.springcloud.demo.domain;

import org.springframework.security.core.GrantedAuthority;

/**
 * 开发公司：青岛海豚数据技术有限公司
 * 版权：青岛海豚数据技术有限公司
 * <p>
 * Permission
 *权限
 * @author 刘志强
 * @created Create Time: 2019/4/15
 */
public class Permission implements GrantedAuthority {

    public Permission(String authority){
        this.authority = authority;
    }
    private String authority;

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    @Override
    public String getAuthority() {
        return this.authority;
    }
}