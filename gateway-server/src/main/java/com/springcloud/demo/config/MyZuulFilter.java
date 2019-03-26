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