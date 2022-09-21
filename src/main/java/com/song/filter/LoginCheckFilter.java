package com.song.filter;

import com.alibaba.fastjson.JSON;
import com.song.common.BaseContext;
import com.song.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 检查用户是否已经登录
 *
 * 这个拦截器会多次调用
 *      1. 地址栏输入 /backend/index.html
 *          本次请求不需要处理/backend/index.html
 *
 *      2./backend/page/member/list.html
 *          本次请求不需要处理/backend/page/member/list.html
 *
 *      3. 拦截到请求/employee/page
 *          用户未登录
 */

@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {

    //专门进行路径比较的
    //路径匹配器 支持通配符这个写法
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request=(HttpServletRequest) servletRequest;
        HttpServletResponse response=(HttpServletResponse) servletResponse;

        /*
            A. 获取本次请求的URI

            B. 判断本次请求, 是否需要登录, 才可以访问

            C. 如果不需要，则直接放行

            D. 判断登录状态，如果已登录，则直接放行

            E. 如果未登录, 则返回未登录结果

        */

        //A. 获取本次请求的URI
        String requestURI=request.getRequestURI();// /backend/index.html
        log.info("拦截到请求{}",requestURI);

        /**
         *  这里需要判断 不需要处理的请求,获取本次的uri 去调用定义的方法check 如果返回true
         *   如果不写 /backend/** 和 /front/**
         *   当输入 /backend/index.html uri是这个的时候 则需要去处理
         *      导致 check() 方法返回false if (check) 则放行
         *      if (request.getSession().getAttribute("employee") != null) 也会放行
         *      最后返回的就是 response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN"))); 这个
         */
        //定义不需要处理的请求
        String[] urls=new String[]{
                "/employee/login",// 登录 处理 controller
                "/employee/logout",//退出 处理 controller
                "/backend/**",  // backend 下所有的资源
                "/front/**",     // front 下所有的资源
                "/common/**",
                //"user/sendMsg",
                //"user/login"
        };


        //B. 判断本次请求, 是否需要登录, 才可以访问
        boolean check = check(urls, requestURI);

        //C. 如果不需要，则直接放行
        if (check) {
            log.info("本次请求不需要处理{}",requestURI);
            filterChain.doFilter(request,response);
            return;
        }

        //D-1. 判断登录状态，如果已登录，则直接放行 pc端
        if (request.getSession().getAttribute("employee") != null){
            log.info("用户已登录,用户ID为{}",request.getSession().getAttribute("employee"));

            Long empId=(Long) request.getSession().getAttribute("employee");

            BaseContext.setCurrentId(empId);//设置值

            filterChain.doFilter(request,response);
            return;
        }
        //D-2. 判断登录状态，如果已登录，则直接放行 移动端

        /*
        这里就演示一下 因为没有阿里云的验证码的
        if (request.getSession().getAttribute("user") != null){
            log.info("用户已登录,用户ID为{}",request.getSession().getAttribute("user"));

            Long userId=(Long) request.getSession().getAttribute("employee");

            BaseContext.setCurrentId(userId);//设置值

            filterChain.doFilter(request,response);
            return;
        }

         */
        log.info("用户未登录");
        //E. 如果未登录, 则返回未登录结果,通过输出流的方式向客户端响应数据
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;

    }


    /**
     * 路径匹配,检查本次请求是否需要放行
     * @param urls
     * @param requestURI
     * @return
     */
    public boolean check(String[] urls,String requestURI){
        for (String url : urls) {
            //match 匹配  匹配成功返回true
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match){
                return true;
            }
        }
        return false;
    }
}
