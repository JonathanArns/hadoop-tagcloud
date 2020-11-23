package com.e7.tagcloud.processing;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

import com.e7.tagcloud.util.Paths;
import com.kennycason.kumo.CollisionMode;
import com.kennycason.kumo.WordCloud;
import com.kennycason.kumo.WordFrequency;
import com.kennycason.kumo.bg.CircleBackground;
import com.kennycason.kumo.bg.RectangleBackground;
import com.kennycason.kumo.font.scale.LinearFontScalar;
import com.kennycason.kumo.font.scale.LogFontScalar;
import com.kennycason.kumo.font.scale.SqrtFontScalar;
import com.kennycason.kumo.palette.ColorPalette;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

@Service
public class TagcloudService {
    static {
        System.setProperty("hadoop.home.dir", "/");
    }

    @Autowired
    Paths paths;
    @Autowired
    ResourceLoader resourceLoader;

    public void makeTagcloud(List<WordFrequency> wordFrequencies, File outputFile) throws IOException {
        final Dimension dimension = new Dimension(1000, 1000);
        final WordCloud wordCloud = new WordCloud(dimension, CollisionMode.RECTANGLE);
        wordCloud.setPadding(2);
        wordCloud.setBackground(new CircleBackground(500));
        wordCloud.setColorPalette(new ColorPalette(new Color(0x4055F1), new Color(0x408DF1), new Color(0x40AAF1), new Color(0x40C5F1), new Color(0x40D3F1), new Color(0xFFFFFF)));
        wordCloud.setFontScalar(new SqrtFontScalar(10, 40));
        wordCloud.build(wordFrequencies);
        outputFile.getParentFile().mkdirs();
        outputFile.createNewFile();
        wordCloud.writeToFile(outputFile.getAbsolutePath());
    }

}

