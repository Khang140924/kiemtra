package com.example.demo;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Cấu hình để khi web gọi link /images/ten-file.png 
        // nó sẽ tìm trong thư mục uploads/ bên ngoài dự án trước, rồi mới tìm trong resources/static/images/
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:uploads/", "classpath:/static/images/");
    }
}
