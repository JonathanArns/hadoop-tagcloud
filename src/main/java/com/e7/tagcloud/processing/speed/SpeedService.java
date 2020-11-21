package com.e7.tagcloud.processing.speed;

import com.e7.tagcloud.util.Paths;
import com.e7.tagcloud.processing.TagcloudService;
import com.kennycason.kumo.WordFrequency;
import com.kennycason.kumo.nlp.FrequencyAnalyzer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
public class SpeedService {
    @Autowired
    TagcloudService tagcloudService;
    @Autowired
    Paths paths;

    public void createTagcloud(String name) throws IOException {
        List<WordFrequency> wordFrequencies = new FrequencyAnalyzer().load(new File(paths.getUpload() + name));
        tagcloudService.makeTagcloud(wordFrequencies, new File(paths.getTagclouds() + name + ".png"));
    }
}
