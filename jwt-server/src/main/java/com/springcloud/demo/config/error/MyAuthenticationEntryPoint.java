package com.springcloud.demo.config.error;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author admin
 */
public class MyAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) {
        AppResult appResult = AppResult.errorReturn(401, authException.getMessage(),authException.getLocalizedMessage());
        writer(request,response,appResult);
    }

    public static boolean writer(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AppResult appResult){
        PrintWriter writer = null;
        String originHeader = httpServletRequest.getHeader("Origin");
        httpServletResponse.setHeader("Access-Control-Allow-Origin", originHeader);
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.addHeader("Access-Control-Allow-Credentials", "true");
        httpServletResponse.addHeader("Vary", "Origin");
        httpServletResponse.setContentType("application/json; charset=utf-8");
        try {
            JSONObject jsonObject = (JSONObject) JSON.toJSON(appResult);
            writer = httpServletResponse.getWriter();
            writer.append(jsonObject.toString());
        } catch (IOException e) {
            System.out.println("response error" + e);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
        return false;
    }

}
