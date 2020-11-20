package com.e7.tagcloud;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "paths")
@Component
public class Paths {

    private String upload;
    private String tagclouds;
    private String globalTagcloud;
    private String wordcounts;

    public String getUpload() {
        return upload;
    }

    public void setUpload(String upload) {
        this.upload = upload;
    }

    public String getTagclouds() {
        return tagclouds;
    }

    public void setTagclouds(String tagclouds) {
        this.tagclouds = tagclouds;
    }

    public String getGlobalTagcloud() {
        return globalTagcloud;
    }

    public void setGlobalTagcloud(String globalTagcloud) {
        this.globalTagcloud = globalTagcloud;
    }

    public String getWordcounts() {
        return wordcounts;
    }

    public void setWordcounts(String wordcounts) {
        this.wordcounts = wordcounts;
    }
}
