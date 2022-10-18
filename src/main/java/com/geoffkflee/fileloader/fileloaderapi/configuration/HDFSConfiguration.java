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

    private final String hadoopHome;

    public HDFSConfiguration(
            @Value("${hadoop.hdfs.base_uri}") String hdfsURI,
            @Value("${hadoop.hdfs.username}") String hdfsUsername,
            @Value("${hadoop.config.library_path}") String hadoopHome
    ) {
        this.hdfsUri = hdfsURI;
        this.hdfsUsername = hdfsUsername;
        this.hadoopHome = hadoopHome;
    }

    @Bean
    FileSystem fileSystem() throws IOException, InterruptedException {
        org.apache.hadoop.conf.Configuration hadoopConfiguration
                = new org.apache.hadoop.conf.Configuration(true);
        System.setProperty("hadoop.home.dir", hadoopHome);
        return FileSystem.newInstance(URI.create(hdfsUri), hadoopConfiguration, hdfsUsername);
    }

}
