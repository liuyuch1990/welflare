package com.unicorn.wsp.config;

import com.unicorn.wsp.interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Value("${upload.path}")
    private String path;

    @Value("${upload.dir}")
    private String dir;

    @Autowired
    LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor).addPathPatterns("/**")
                .excludePathPatterns("/pics/**")
                .excludePathPatterns("/*/error")
                .excludePathPatterns("/*/admin/login")
                .excludePathPatterns("/*/login")
                .excludePathPatterns("/*/check/*")

                .excludePathPatterns("/*/register");   //   /**行。。。  /equipment-management/user不行。。。
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(path + "**")//  /pics/**
                .addResourceLocations("file:" + dir);//file:D:/workspace/upload/wsp-boot
        //http://localhost:9209/wsp-boot/pics/ngafsu8vaus89fu83d8fuasdf8das7f98.jpg
    }


}
