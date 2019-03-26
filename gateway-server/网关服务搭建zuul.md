### 网关服务搭建zuul

1. 在spring-cloud项目中新建gateway-server （maven）模块
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

    <artifactId>gateway-server</artifactId>

    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-zuul</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-eureka</artifactId>
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
3. java目录下创建路径(com.springcloud.demo)及启动类  GatewayServerApplication.java


```java
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
```
4. 创建配置文件 application.yml

```yml
spring:
  application:
    name: gateway-server
server:
  port: 5003
zuul:
  sensitive-headers:
  routes:
    webService:
      path: /provide/**
      serviceId: provide-server
    appService:
      path: /consumer/**
      serviceId: consumer-server

#注册中心地址
eureka:
  client:
    serviceUrl:
      defaultZone: http://127.0.0.1:5000/eureka/
  instance:
    hostname: 127.0.0.1
    instance-id: http://127.0.0.1:5003


```
5. 使用熔断

```java
package com.springcloud.demo.config;

import org.springframework.cloud.netflix.zuul.filters.route.ZuulFallbackProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 开发公司：青岛海豚数据技术有限公司
 * 版权：青岛海豚数据技术有限公司
 * <p>
 * MyZuulFallBack
 *网关熔断 当provide-server 服务没有启动时或访问超时等时会执行 fallbackResponse 方法
 * @author 刘志强
 * @created Create Time: 2019/3/26
 */
@Component
public class MyZuulFallBack implements ZuulFallbackProvider {
    @Override
    public String getRoute() {
        return "provide-server";
//        return "*"; 返回* 所有的服务都会走此熔断处理
    }

    @Override
    public ClientHttpResponse fallbackResponse() {
        return new ClientHttpResponse() {
            @Override
            public HttpStatus getStatusCode() throws IOException {
                return HttpStatus.OK;
            }

            @Override
            public int getRawStatusCode() throws IOException {
                return 200;
            }

            @Override
            public String getStatusText() throws IOException {
                return "OK";
            }

            @Override
            public void close() {

            }

            @Override
            public InputStream getBody() throws IOException {
                return new ByteArrayInputStream("服务器中断连接".getBytes());
            }

            @Override
            public HttpHeaders getHeaders() {
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                return httpHeaders;
            }
        };
    }
}
```
6. 使用过滤器

```
package com.springcloud.demo.config;

import com.netflix.zuul.ZuulFilter;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 开发公司：青岛海豚数据技术有限公司
 * 版权：青岛海豚数据技术有限公司
 * <p>
 * MyZuulFilter
 *
 * PRE:这种过滤器在请求被路由之前调用。可利用这种过滤器实现身份验证、在集群中选择请求的微服务，记录调试信息等。
 *ROUTING:这种过滤器将请求路由到微服务。这种过滤器用于构建发送给微服务的请求，并使用Apache HttpClient或Netflix Ribbon请求微服务。
 *POST:这种过滤器在路由到微服务以后执行。这种过滤器可用来为响应添加标准的HTTP header、收集统计信息和指标、将响应从微服务发送给客户端等。
 *ERROR:在其他阶段发送错误时执行该过滤器。
 * @author 刘志强
 * @created Create Time: 2019/3/26
 */
@Component
public class MyZuulFilter extends ZuulFilter {
    @Autowired
    public HttpServletRequest httpServletRequest;
    @Autowired
    public HttpServletResponse httpServletResponse;
    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    // 过滤顺序
    public int filterOrder() {
        return 0;
    }

    @Override
    // true 执行过滤逻辑 false 不执行
    public boolean shouldFilter() {
        return true;
    }

    @Override
    // 过滤逻辑处理
    public Object run() {
        String auth = httpServletRequest.getHeader("Authorization");
        if (StringUtils.isEmpty(auth)) {
            PrintWriter writer = null;
            String originHeader = httpServletRequest.getHeader("Origin");
            httpServletResponse.setHeader("Access-Control-Allow-Origin", originHeader);
            httpServletResponse.setCharacterEncoding("UTF-8");
            httpServletResponse.addHeader("Access-Control-Allow-Credentials", "true");
            httpServletResponse.addHeader("Vary", "Origin");
            httpServletResponse.setContentType("application/json; charset=utf-8");
            try {
                writer = httpServletResponse.getWriter();
                writer.append("请求非法");
            } catch (IOException e) {
                return null;
            } finally {
                if (writer != null) {
                    writer.close();
                }
            }
        }
        return null;
    }
}
```





