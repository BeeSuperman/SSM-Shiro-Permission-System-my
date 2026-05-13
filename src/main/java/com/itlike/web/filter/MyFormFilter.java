package com.itlike.web.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itlike.domain.AjaxRes;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

public class MyFormFilter extends FormAuthenticationFilter {

    protected boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request, ServletResponse response) throws Exception {
        response.setContentType("application/json;charset=utf-8");
        response.setCharacterEncoding("UTF-8");
        System.out.println("认证成功");
        
        AjaxRes ajaxRes = new AjaxRes();
        ajaxRes.setSuccess(true);
        ajaxRes.setMsg("登录成功");
        
        String jsonString = new ObjectMapper().writeValueAsString(ajaxRes);
        response.getWriter().print(jsonString);
        return false;
    }

    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response) {
    	System.out.println("认证失败，原因是：" + e.getMessage()); // 這一行能精準告訴我們為什麼失敗
        e.printStackTrace(); // 在控制台印出完整的錯誤堆疊
        response.setContentType("application/json;charset=utf-8");
        response.setCharacterEncoding("UTF-8");
        
        AjaxRes ajaxRes = new AjaxRes();
        ajaxRes.setSuccess(false);
        if (e != null) {
            String name = e.getClass().getName();
            if (name.equals(UnknownAccountException.class.getName())) {
                ajaxRes.setMsg("帐号不正确");
            } else if (name.equals(IncorrectCredentialsException.class.getName())) {
                ajaxRes.setMsg("密码不正确");
            } else {
                ajaxRes.setMsg("未知错误");
            }
        }

        try {
            String jsonString = new ObjectMapper().writeValueAsString(ajaxRes);
            response.getWriter().print(jsonString);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return false;
    }
}