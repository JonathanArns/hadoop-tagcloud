package com.e7.tagcloud.util;

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

    private String job1;
    private String job2;
    private String job3;

    public String getJob1() {
        return job1;
    }

    public void setJob1(String job1) {
        this.job1 = job1;
    }

    public String getJob2() {
        return job2;
    }

    public void setJob2(String job2) {
        this.job2 = job2;
    }

    public String getJob3() {
        return job3;
    }

    public void setJob3(String job3) {
        this.job3 = job3;
    }

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
