package com.gilad.dev.stringmatcher;


import com.gilad.dev.stringmatcher.service.StringMatcherService;
import com.gilad.dev.stringmatcher.service.properties.LocationProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@SpringBootApplication
public class StringMatcherApplication implements CommandLineRunner {

    @Autowired
    private StringMatcherService service;
    @Autowired
    private LocationProperties locationProperties;

    private static URL contentUrl;

    @PostConstruct
    private void init() throws MalformedURLException {
        contentUrl = new URL(locationProperties.getBigFileUrl());
    }

    public static void main(String[] args){
        log.info("starting");
        SpringApplication.run(StringMatcherApplication.class, args);
        log.info("done");
    }

    @Override
    public void run(String... args) throws Exception {
        if(args.length != 1){
            System.out.println("usage error, path must be provided");
            System.exit(-1);
        }
        try {
            Path contentDestination = Paths.get(args[0]);
            downloadToLocation(contentDestination);
            service.process(contentDestination);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("error has occurred");
            System.exit(-1);
        }
    }


    private static void downloadToLocation(Path contentLocation) throws IOException {
        try(ReadableByteChannel readableByteChannel = Channels.newChannel(contentUrl.openStream());
            FileOutputStream fos = new FileOutputStream(contentLocation.toFile());
            FileChannel fileChannel = fos.getChannel()) {

            fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
        }
    }
}
