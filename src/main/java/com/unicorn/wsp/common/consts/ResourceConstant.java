package com.unicorn.wsp.common.consts;

/**
 * 访问资源 consts
 * @author zy
 */
public interface ResourceConstant {
    /**
     * 放开权限校验的接口
     */
    String[] EXCLUDE_URLS = {

            //前端的
            "/favicon.ico",
            "/static/**",

            //swagger相关的
            "/doc.html",
            "/webjars/**",
            "/swagger-resources/**",
            "/v2/api-docs",
            "/v2/api-docs-ext",

            //后端的
            "/",
            "/api/login",
            "/api/register",
            "/swagger-ui.html",
            "/api/logout",
            "/api/common/**",
            "/api/kaptcha/**",
    };
}
