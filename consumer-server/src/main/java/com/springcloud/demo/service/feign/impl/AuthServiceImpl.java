package com.springcloud.demo.service.feign.impl;

import com.springcloud.demo.domain.AuthToken;
import com.springcloud.demo.service.feign.AuthService;
import com.springcloud.demo.service.feign.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 开发公司：青岛海豚数据技术有限公司
 * 版权：青岛海豚数据技术有限公司
 * <p>
 * AuthServiceImpl
 *
 * @author 刘志强
 * @created Create Time: 2019/4/16
 */
@Component
public class AuthServiceImpl implements AuthService, JWTService {
    @Autowired
    public AuthService authService;
    @Override
    public AuthToken getToken(String authorization, String userName, String password, String grant_type) {
        return null;
    }
}