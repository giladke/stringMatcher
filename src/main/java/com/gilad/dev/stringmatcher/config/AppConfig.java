package com.gilad.dev.stringmatcher.config;

import com.gilad.dev.stringmatcher.components.filehandler.PartProperties;
import com.gilad.dev.stringmatcher.service.properties.LocationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({LocationProperties.class, PartProperties.class})
public class AppConfig {
    public static final String RESOURCE_NAME = "commonFirstNames.txt";
}
