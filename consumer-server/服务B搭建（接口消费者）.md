### [项目地址](https://github.com/2425358736/spring-cloud)
## 服务b搭建（接口消费者）
1. 在spring-cloud项目中新建consumer-server （maven）模块
2. pom.xml 引入开发包及插件
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <artifactId>spring-cloud</artifactId>
        <groupId>spring-cloud</groupId>
        <version>1.0-SNAPSHOT</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <artifactId>consumer-server</artifactId>

    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-eureka</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-feign</artifactId>
        </dependency>
        
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <!-- maven打包插件 能够以Maven的方式为应用提供Spring Boot的支持，即为Spring Boot应用提供了执行Maven操作的可能 -->
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```
3. java目录下创建路径(com.springcloud.demo)及启动类  ConsumerServerApplication.java

```java
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
```
4. 创建配置文件 application.yml


```yml
spring:
  application:
    name: consumer-server

server:
  port: 5002 # 注册中心占用6000端口

# 开启熔断
feign:
  hystrix:
    enabled: true

#注册中心地址
eureka:
  client:
    serviceUrl:
      defaultZone: http://127.0.0.1:5000/eureka/
  instance:
    hostname: 127.0.0.1
    instance-id: http://127.0.0.1:5002

```


5. 创建测试controller

```java
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
```

6. 创建接口


```java
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
```

7. 实现接口类

```java
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
```






