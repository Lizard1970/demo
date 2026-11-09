package com.example.demo.config;

import com.example.demo.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Component
public class AuthInterceptor implements HandlerInterceptor {


    @Resource
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{
        //新增：跨域OPTIONS预请求直接放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())){
            return true;
        }


        // 登录接口直接放行，不用校验token

        String uri = request.getRequestURI();
        if (uri.contains("/user/login")|| uri.endsWith("login.html")||uri.endsWith("user.html")){
            return true;
        }
        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()){
            //返回401
            response.setContentType("application/json;charset=utf-8");
            Map<String, Object> map = new HashMap<>();
            map.put("code",401);
            map.put("msg","未登录，请携带token");
            new ObjectMapper().writeValue(response.getWriter(),map);
            return false;
        }
        //去掉bearer 前缀
        //token = token.substring(7);
        if (!jwtUtil.verifyToken(token)){
            response.setContentType("application/json;charset=utf-8");
            Map<String, Object> map = new HashMap<>();
            map.put("code", 401);
            map.put("msg", "token无效或已过期！");
            new ObjectMapper().writeValue(response.getWriter(), map);
            return false;

        }
        //把userId放到request，后面controller可以拿
        Long userId = jwtUtil.getUserId(token);
        request.setAttribute("loginUserId",userId);
        return true;

    }



}
