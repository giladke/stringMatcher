package com.gilad.dev.stringmatcher.components.filehandler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConfigurationProperties(prefix = "part")
@ConstructorBinding
@Getter
@AllArgsConstructor
public class PartProperties {
    int maxPartSize;
}
