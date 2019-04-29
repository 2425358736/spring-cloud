antMatchers(HttpMethod.OPTIONS,"/**").permitAll()
放过OPTIONS请求


创建auth2-server服务
### 配置文件
```
spring:
  application:
    name: auth2-server
  #redis配置数据
  redis:
    hostName: 117.73.8.227
    port: 6379
    database: 0
    password:
    pool:
      maxActive: 8
      maxWait: -1
      maxIdle: 100
      minIdle: 0
    timeout: 5000
server:
  port: 5005
#注册中心地址
eureka:
  client:
    serviceUrl:
      defaultZone: http://127.0.0.1:5000/eureka/
  instance:
    hostname: 127.0.0.1
    instance-id: 127.0.0.1:${eureka.instance.appname}:5005
    appname: auth2-server
security:
  oauth2:
    resource:
      filter-order: 3

```

### 常见安全服务适配器 WebSecurityConfig

```java
package com.springcloud.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * 配置安全配置
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsServiceImpl userDetailsService ;
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .csrf().disable();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // auth.userDetailsService(userDetailsService).passwordEncoder(new BCryptPasswordEncoder());
        // passwordEncoder 选择加密的方式 loadUserByUsername 接口返回的对象密码验证方式
            auth.userDetailsService(userDetailsService);
    }

    @Override
    @Bean(name = "authenticationManagerBean")
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

}

```

### 创建授权服务适配器

```
package com.springcloud.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
@EnableAuthorizationServer // 开启授权服务功能
public class OAuth2Config extends AuthorizationServerConfigurerAdapter {

    @Autowired
    @Qualifier(value = "authenticationManagerBean")
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserDetailsServiceImpl userDetailsService ;
    @Autowired
    private TokenStore tokenStore;

    @Bean(name = "tokenStore")
    public TokenStore tokenStore(JedisConnectionFactory jedisConnectionFactory) {
        RedisTokenStore redis = new RedisTokenStore(jedisConnectionFactory);
        return redis;
    }
    // 配置客户端基本信息
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient("consumer-server")// 创建一个客户端 名字是spring
                .secret("123")
                .scopes("server")
                .authorizedGrantTypes("refresh_token","password")
                .accessTokenValiditySeconds(36000);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.tokenStore(tokenStore)
                .authenticationManager(authenticationManager)
                .userDetailsService(userDetailsService);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer authorizationServer) {
        authorizationServer
                .tokenKeyAccess("permitAll()")
                .checkTokenAccess("isAuthenticated()");
    }


    @Bean(name = "jedisPoolConfig")
    @ConfigurationProperties(prefix = "spring.redis.pool")
    public JedisPoolConfig jedisPoolConfig() {
        JedisPoolConfig config = new JedisPoolConfig();
        return config;
    }

    @Bean(name = "jedisConnectionFactory")
    @ConfigurationProperties(prefix = "spring.redis")
    public JedisConnectionFactory jedisConnectionFactory(JedisPoolConfig jedisPoolConfig) {
        JedisConnectionFactory factory = new JedisConnectionFactory();
        JedisPoolConfig config = jedisPoolConfig;
        factory.setPoolConfig(config);
        return factory;
    }
}

```
### OAuth2 也需要访问 所有也是资源服务，创建资源服务器配置

```
package com.springcloud.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;

/**
 * 配置资源服务配置
 */
@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter{

    @Autowired
    private TokenStore tokenStore;

    @Override
    public void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf().disable()
                .authorizeRequests()
                .antMatchers("/**").authenticated()
                .anyRequest()
                .permitAll();
    }
    @Override
    public void configure(ResourceServerSecurityConfigurer resourceServerSecurityConfigurer) throws Exception {
        resourceServerSecurityConfigurer.tokenStore(tokenStore);
    }
}

```



### 创建UserDetailsService

```
package com.springcloud.demo.config;

import com.springcloud.demo.domain.Permission;
import com.springcloud.demo.domain.User;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Override
    public User loadUserByUsername(String userName) throws UsernameNotFoundException {
        if (StringUtils.equals(userName,"liuzhiqiang")) {
            User user = new User();
            user.setUserName(userName);
            user.setPassword("123456789");
            List<Permission> list = new ArrayList<>();
            list.add(new Permission("permission1"));
            list.add(new Permission("permission2"));
            list.add(new Permission("permission3"));
            user.setList(list);
            // 以上内容在真实环境中数据来自于数据库
            return user;
        } else {
            User user = new User();
            user.setUserName(userName);
            user.setPassword("123456");
            List<Permission> list = new ArrayList<>();
            list.add(new Permission("ceshi1"));
            list.add(new Permission("ceshi2"));
            list.add(new Permission("ceshi3"));
            user.setList(list);
            // 以上内容在真实环境中数据来自于数据库
            return user;
        }
    }
}


```

### 创建资源服务器consumer-server

### yml配置（redis配置和OAuth2服务配置一致）

```
spring:
  application:
    name: consumer-server
  #redis配置数据
  redis:
    hostName: 117.73.8.227
    port: 6379
    database: 0
    password:
    pool:
      maxActive: 8
      maxWait: -1
      maxIdle: 100
      minIdle: 0
    timeout: 5000
server:
  port: 5002

# 开启熔断
feign:
  hystrix:
    enabled: true

hystrix:
  command:
    default:  #default全局有效，service id指定应用有效
      execution:
        timeout:
          #如果enabled设置为false，则请求超时交给ribbon控制,为true,则超时作为熔断根据
          enabled: true
        isolation:
          thread:
            timeoutInMilliseconds: 10000 #断路器超时时间，默认1000ms
#注册中心地址
eureka:
  client:
    serviceUrl:
      defaultZone: http://127.0.0.1:5000/eureka/
  instance:
    hostname: 127.0.0.1
    instance-id: 127.0.0.1:${eureka.instance.appname}:5002
    appname: consumer-server


```

### 开启注解

```java
package com.springcloud.demo.config.auth2;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class GlobalMethodSecurityConfig {
}

```

### 资源服务安全配置

```
package com.springcloud.demo.config.auth2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter{

    @Autowired
    private TokenStore tokenStore;

    @Override
    public void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf().disable()
                .authorizeRequests()
                .antMatchers("/test/getToken","/test/getJWTToken").permitAll()
                .antMatchers("/**").authenticated()
                .anyRequest()
                .permitAll();
    }

    @Bean(name = "tokenStore")
    public TokenStore tokenStore(JedisConnectionFactory jedisConnectionFactory) {
        RedisTokenStore redis = new RedisTokenStore(jedisConnectionFactory);
        return redis;
        // return new JwtTokenStore( jwtAccessTokenConverter());
    }
    @Override
    public void configure(ResourceServerSecurityConfigurer resourceServerSecurityConfigurer) throws Exception {
        resourceServerSecurityConfigurer.tokenStore(tokenStore);
    }

//    @Bean(name = "jwtAccessTokenConverter")
//    public JwtAccessTokenConverter jwtAccessTokenConverter(){
//        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
//        jwtAccessTokenConverter.setSigningKey("123456");
//        return jwtAccessTokenConverter;
//    }

    @Bean(name = "jedisPoolConfig")
    @ConfigurationProperties(prefix = "spring.redis.pool")
    public JedisPoolConfig jedisPoolConfig() {
        JedisPoolConfig config = new JedisPoolConfig();
        return config;
    }

    @Bean(name = "jedisConnectionFactory")
    @ConfigurationProperties(prefix = "spring.redis")
    public JedisConnectionFactory jedisConnectionFactory(JedisPoolConfig jedisPoolConfig) {
        JedisConnectionFactory factory = new JedisConnectionFactory();
        JedisPoolConfig config = jedisPoolConfig;
        factory.setPoolConfig(config);
        return factory;
    }
}
```

### 测试注解

```
    @PreAuthorize("hasAnyAuthority('dddddddddd')")
    @PostMapping("/ceshi2")
    public String ceshi2() {
        return "ceshi2";
    }
```

源码地址： https://github.com/2425358736/spring-cloud.git







