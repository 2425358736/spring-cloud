package com.springcloud.demo.controller;

import com.springcloud.demo.domain.User;
import com.springcloud.demo.service.feign.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 开发公司：青岛海豚数据技术有限公司
 * 版权：青岛海豚数据技术有限公司
 * <p>
 * TestController
 *
 * @author 刘志强
 * @created Create Time: 2019/3/26
 */
@RestController
@RequestMapping("/test")
public class TestController {
    @Autowired
    public TestService testService;

    @GetMapping("/getName")
    public String getName() {
        return testService.getName();
    }

    @PostMapping("/getUser")
    public Map<String,Object> getUser(Long id) {
        return testService.getUser("11111", id);
    }

    @PostMapping("/getUserInfo")
    public User getUserInfo() {
        User user = new User();
        user.setId(new Long("123"));
        user.setUserName("刘志强");
        user.setPassword("123456");
        return testService.getUserInfo(user);
    }
}