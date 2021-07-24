package com.gilad.dev.stringmatcher.service.properties;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConfigurationProperties(prefix = "location")
@ConstructorBinding
@AllArgsConstructor
@Getter
@ToString
public class LocationProperties {
    String bigFileUrl;
}
