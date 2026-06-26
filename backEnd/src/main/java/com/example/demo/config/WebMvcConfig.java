package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry resourceHandlerRegistry) {

        // Browserから /uploads/ 로 시작하는 주소로 요청이 오면
        resourceHandlerRegistry.addResourceHandler("/uploads/**")
                // ローカルのパソコンの C:/dance_uploads/ Folderからファイルを探す.
                .addResourceLocations("file:///C:/dance_uploads/");
    }

}
