package com.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.reggie.common.BaseContext;
import com.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.RequestWrapper;
import java.io.IOException;

/**
 * 检查用户是否已经登陆
 */
@Slf4j
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
public class LoginCheckFilter implements Filter {

    //路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;


        long ThreadId = Thread.currentThread().getId();
        log.info("当前线程的id: {}",ThreadId);


        //1. 获取本次请求的URI
        String requestURI = request.getRequestURI();
        log.info("拦截到请求：{}",requestURI);
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/user/login",
                "/user/sendMsg"
        };
        //2. 判断本次请求是否需要处理
        boolean check = check(urls, requestURI);

        //3. 如果不需要处理则放行
        if (check){
            log.info("请求不需要处理");
            filterChain.doFilter(request,response);
            return;
        }

        //4.1 判断登陆状态，如果已经登陆，则放行
        if (request.getSession().getAttribute("employee") != null){
            log.info("用户一登陆，用户id为：{}",request.getSession().getAttribute("employee"));
            Long empId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);
            filterChain.doFilter(request,response);
            return;
        }

        //4.2 判断登陆状态，如果已经登陆，则放行
        if (request.getSession().getAttribute("user") != null){
            log.info("用户一登陆，用户id为：{}",request.getSession().getAttribute("user"));
            Long empId = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(empId);
            filterChain.doFilter(request,response);
            return;
        }

        //5. 如果未登录则返回未登录结果，通过输出流方式想客服端页面响应数据
        log.info("用户未登录");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
    }

    /**
     * 路径匹配，检查本次请求是否可以放行
     * @param urls
     * @param requestURI
     * @return
     */
    public boolean check(String[] urls,String requestURI){
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match){
                return true;
            }
        }
        return false;
    }
}
