package com.cjy.config;

import com.cjy.interceptors.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .excludePathPatterns(
                        "/user/login", "/user/login/**",
                        "/user/register", "/user/register/**",

                        // 页面和静态资源
                        "**/*.html",
                        "**/*.css",
                        "**/*.js",
                        "**/*.ico",
                        "**/*.png", "**/*.jpg", "**/*.jpeg",
                        
                        "/", "/index.html", "/main.html", "/favicon.ico",
                        "/css/**", "/js/**", "/BootStrap/**", "/fonts/**", "/iconfont/**",
                        "/content/**", "/img/**", "/images/**"
                );
    }
}
