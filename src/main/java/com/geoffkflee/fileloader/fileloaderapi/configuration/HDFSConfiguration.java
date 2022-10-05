package com.geoffkflee.fileloader.fileloaderapi.configuration;

import org.apache.hadoop.fs.FileSystem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.net.URI;

@Configuration
public class HDFSConfiguration {

    private final String hdfsUri;
    private final String hdfsUsername;

    public HDFSConfiguration(
            @Value("${hdfs.base_uri}") String hdfsURI,
            @Value("${hdfs.username}") String hdfsUsername
    ) {
        this.hdfsUri = hdfsURI;
        this.hdfsUsername = hdfsUsername;
    }

    @Bean
    FileSystem configureFileSystem() throws IOException, InterruptedException {
        org.apache.hadoop.conf.Configuration hadoopConfiguration
                = new org.apache.hadoop.conf.Configuration(true);
        return FileSystem.newInstance(URI.create(hdfsUri), hadoopConfiguration, hdfsUsername);
    }

}
