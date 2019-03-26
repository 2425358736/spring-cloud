package com.springcloud.demo.service.feign;

import com.springcloud.demo.config.feign.FeignConfig;
import com.springcloud.demo.domain.User;
import com.springcloud.demo.service.feign.impl.TestImpl;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 开发公司：青岛海豚数据技术有限公司
 * 版权：青岛海豚数据技术有限公司
 * <p>
 * TestService
 *
 * @author 刘志强
 * @created Create Time: 2019/3/26
 */
// 可以自定义超时规则，但不推荐
// fallback 走熔断处理的类 当请求第三方超时或出错时会执行TestImpl类中对应的方法
//@FeignClient(value = "provide-server", configuration = FeignConfig.class, fallback = TestImpl.class)
@FeignClient(value = "provide-server", fallback = TestImpl.class)
public interface TestService {
    @RequestMapping(method = RequestMethod.GET, value = "/test/getName")
    String getName();


    // RequestHeader 可以携带header请求 ，这对使用jwt时很有效
    @RequestMapping(method = RequestMethod.POST, value = "/test/getUser")
    Map<String,Object> getUser(@RequestHeader("Authorization") String authorization, @RequestParam(value = "id") Long id);

    @RequestMapping(method = RequestMethod.POST, value = "/test/getUserInfo")
    User getUserInfo(@RequestBody User user);
}