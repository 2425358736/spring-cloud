package com.springcloud.demo.service.feign.impl;

import com.springcloud.demo.domain.User;
import com.springcloud.demo.service.feign.TestService;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 开发公司：青岛海豚数据技术有限公司
 * 版权：青岛海豚数据技术有限公司
 * <p>
 * TestImpl
 *
 * @author 刘志强
 * @created Create Time: 2019/3/26
 */
@Component
public class TestImpl implements TestService {
    @Override
    public String getName() {
        return "王妍";
    }

    @Override
    public Map<String, Object> getUser(String authorization, Long id) {
        return new HashMap<>();
    }

    @Override
    public User getUserInfo(User user) {
        return new User();
    }
}