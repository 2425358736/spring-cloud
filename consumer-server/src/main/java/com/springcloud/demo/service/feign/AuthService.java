package com.springcloud.demo.service.feign;

import com.springcloud.demo.domain.AuthToken;
import com.springcloud.demo.service.feign.impl.AuthServiceImpl;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * 开发公司：青岛海豚数据技术有限公司
 * 版权：青岛海豚数据技术有限公司
 * <p>
 * AuthService
 *
 * @author 刘志强
 * @created Create Time: 2019/4/16
 */
@FeignClient(value = "auth2-server", fallback = AuthServiceImpl.class)
public interface AuthService {
    @RequestMapping(method = RequestMethod.POST, value = "/oauth/token")
    AuthToken getToken(@RequestHeader(value = "Authorization") String authorization, @RequestParam("username") String userName, @RequestParam("password") String password, @RequestParam("grant_type") String grant_type);
}