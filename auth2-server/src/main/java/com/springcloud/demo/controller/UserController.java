package com.springcloud.demo.controller;/**
 * Created by lzq on 2019/4/15.
 */

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * 开发公司：青岛海豚数据技术有限公司
 * 版权：青岛海豚数据技术有限公司
 * <p>
 * UserController
 *
 * @author 刘志强
 * @created Create Time: 2019/4/15
 */
@RestController
@RequestMapping("users")
public class UserController {
    @RequestMapping(value = "/current", method = RequestMethod.GET)
    public Principal getUser(Principal principal){
        return principal;
    }
}