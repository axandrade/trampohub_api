package br.com.alexandrade.trampohub_api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.media.root}")
    private String mediaRoot;

    @Value("${app.media.url-pattern}")
    private String mediaUrlPattern;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = mediaRoot.endsWith("/") ? mediaRoot : mediaRoot + "/";
        registry.addResourceHandler(mediaUrlPattern)
                .addResourceLocations("file:" + location);
    }
}
