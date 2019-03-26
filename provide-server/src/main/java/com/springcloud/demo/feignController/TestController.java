package com.springcloud.demo.feignController;

import com.springcloud.demo.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
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
    public HttpServletRequest httpServletRequest;

    @GetMapping("/getName")
    public String getName() {
        return "刘志强";
    }

    @PostMapping("/getUser")
    public Map<String,Object> getUser(Long id) {
        String auth = httpServletRequest.getHeader("Authorization");
        Map<String,Object> map = new HashMap<>();
        map.put("id",id);
        map.put("name","刘志强");
        map.put("auth",auth);
        return map;
    }

    @PostMapping("/getUserInfo")
    public User getUserInfo(@RequestBody User user) {
        user.setUserName("王妍");
        return user;
    }
}