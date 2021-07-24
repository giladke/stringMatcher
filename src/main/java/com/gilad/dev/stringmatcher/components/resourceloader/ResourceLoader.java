package com.gilad.dev.stringmatcher.components.resourceloader;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Component
public class ResourceLoader {

    private final org.springframework.core.io.ResourceLoader resourceLoader;

    public ResourceLoader(org.springframework.core.io.ResourceLoader resourceLoader){
        this.resourceLoader = resourceLoader;
    }

    public String getResourceAsString(String resourceName) throws IOException {
        Resource resource = resourceLoader.getResource(String.format("%s:%s","classpath", resourceName));

        try(InputStream inputStream = resource.getInputStream())
        {
            byte[] bdata = FileCopyUtils.copyToByteArray(inputStream);
            return new String(bdata, StandardCharsets.UTF_8);
        }
    }
}
