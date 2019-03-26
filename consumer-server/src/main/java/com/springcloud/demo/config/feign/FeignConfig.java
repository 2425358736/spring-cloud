package com.springcloud.demo.config.feign;

import feign.Retryer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * 开发公司：青岛海豚数据技术有限公司
 * 版权：青岛海豚数据技术有限公司
 * <p>
 * FeignConfig
 *
 * @author 刘志强
 * @created Create Time: 2019/3/26
 */
@Configuration
public class FeignConfig {
    @Bean
    // 熔断超时策略
    public Retryer feginRetryer(){
        Retryer retryer = new Retryer.Default(100, TimeUnit.SECONDS.toMillis(1), 3);
        return retryer;
    }
}