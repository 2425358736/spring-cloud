package com.springcloud.demo.config.error;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MyAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        AppResult appResult = AppResult.errorReturn(401, accessDeniedException.getMessage(),accessDeniedException.getLocalizedMessage());
        MyAuthenticationEntryPoint.writer(request,response,appResult);
    }
}
