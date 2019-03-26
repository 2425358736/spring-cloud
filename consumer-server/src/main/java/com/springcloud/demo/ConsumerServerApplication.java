package com.springcloud.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;

/**
 * 开发公司：青岛海豚数据技术有限公司
 * 版权：青岛海豚数据技术有限公司
 * <p>
 * ProvideServerApplication
 *
 * @author 刘志强
 * @created Create Time: 2019/3/26
 */
@SpringBootApplication
@EnableEurekaClient // 启动EnableEureka客户端 像Eureka注册中心注册
@EnableFeignClients // 通过EnableFeignClients调用其他服务的api
@EnableAutoConfiguration
public class ConsumerServerApplication implements CommandLineRunner {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public static void main(String[] args) {
        SpringApplication.run(ConsumerServerApplication.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {
        logger.info("服务器启动成功");
    }
}