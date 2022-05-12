package com.geekub.cinema.web.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        exposePath("web-app/src/main/resources/static/images/", registry);
    }

    private void exposePath(String pathName, ResourceHandlerRegistry registry) {
        String uploadPath = Paths.get(pathName).toFile().getAbsolutePath();

        if (pathName.startsWith("../")) pathName = pathName.replace("../", "");

        registry.addResourceHandler("/" + pathName + "/**").addResourceLocations("file:/"+ uploadPath + "/");
    }
}
