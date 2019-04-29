package com.springcloud.demo.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * 开发公司：青岛海豚数据技术有限公司
 * 版权：青岛海豚数据技术有限公司
 * <p>
 * TestController
 *
 * @author 刘志强
 * @created Create Time: 2019/4/17
 */
@RestController
@RequestMapping("/users")
public class TestController {
    // 获取token
    @GetMapping("/getUser")
    public Principal getUser(Principal principal) {
        return principal;
    }
}