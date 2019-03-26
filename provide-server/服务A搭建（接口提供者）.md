### [项目地址](https://github.com/2425358736/spring-cloud)
## 服务A搭建（接口提供者）

1. 在spring-cloud项目中新建provide-server （maven）模块
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

    <artifactId>provide-server</artifactId>

    <dependencies>
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
3. java目录下创建路径(com.springcloud.demo)及启动类  ProvideServerApplication.java

```java
package com.springcloud.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

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
@EnableEurekaClient // //启动EnableEureka客户端 向Eureka注册中心注册
public class ProvideServerApplication implements CommandLineRunner {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public static void main(String[] args) {
        SpringApplication.run(ProvideServerApplication.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {
        logger.info("服务器启动成功");
    }
}
```
4. 创建配置文件application.yml

```bash
server:
  port: 5001 # 注册中心占用6000端口
spring:
  application:
    name: provide-server

#注册中心地址
eureka:
  client:
    serviceUrl:
      defaultZone: http://127.0.0.1:5000/eureka/
  instance:
    hostname: 127.0.0.1
    instance-id: http://127.0.0.1:5001

```
5. 创建测试接口api，以供服务B(接口消费者)使用

```java
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
    public Map<String,Object>  getUser(Long id) {
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
```
6. 启动ProvideServerApplication 访问 http://127.0.0.1:5001/test/getName 查看是否可访问
7. 访问注册服务 http://127.0.0.1:5000/ 查看服务是否已注册



