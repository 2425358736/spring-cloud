package com.springcloud.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/**
 * 开发公司：青岛海豚数据技术有限公司
 * 版权：青岛海豚数据技术有限公司
 * <p>
 * RegisterServerApplication
 *
 * @author 刘志强
 * @created Create Time: 2019/3/26
 */
@SpringBootApplication
@EnableEurekaClient
@EnableZuulProxy
@EnableAutoConfiguration
public class GatewayServerApplication implements CommandLineRunner {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public static void main(String[] args) {
        SpringApplication.run(GatewayServerApplication.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {
        logger.info("服务器启动成功");
    }
}