package com.e7.tagcloud.processing;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.e7.tagcloud.util.Paths;
import com.e7.tagcloud.TagcloudApplication;
import com.e7.tagcloud.processing.batch.WordCountMapper;
import com.e7.tagcloud.processing.batch.WordCountReducer;
import com.kennycason.kumo.CollisionMode;
import com.kennycason.kumo.WordCloud;
import com.kennycason.kumo.WordFrequency;
import com.kennycason.kumo.bg.CircleBackground;
import com.kennycason.kumo.bg.RectangleBackground;
import com.kennycason.kumo.font.scale.SqrtFontScalar;
import com.kennycason.kumo.nlp.FrequencyFileLoader;
import com.kennycason.kumo.palette.ColorPalette;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.reduce.IntSumReducer;
import org.apache.log4j.BasicConfigurator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternUtils;
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

//    private Job job;

    public void makeWordCount(String name) throws IOException, ClassNotFoundException, InterruptedException { // not sure what it will throw b now :D
        BasicConfigurator.configure();
        Configuration cfg = new Configuration();

        Job wordCountJob = Job.getInstance(cfg, "word count");
        wordCountJob.setJarByClass(TagcloudApplication.class);
        wordCountJob.setMapperClass(WordCountMapper.class);
        wordCountJob.setCombinerClass(IntSumReducer.class);
        wordCountJob.setReducerClass(WordCountReducer.class);
        wordCountJob.setNumReduceTasks(2);
        wordCountJob.setOutputKeyClass(Text.class);
        wordCountJob.setOutputValueClass(IntWritable.class);

//        job.setOutputFormatClass(SequenceFileOutputFormat.class);

        FileInputFormat.addInputPath(wordCountJob, new Path(paths.getUpload() + name));
        FileOutputFormat.setOutputPath(wordCountJob, new Path(paths.getWordcounts() + name));

        wordCountJob.waitForCompletion(true);

//        job = Job.getInstance(cfg, "tagcloud");
//        job.setJarByClass(TagcloudApplication.class);
//        job.setMapperClass(Mapper.class);
//        job.setReducerClass(Reducer.class);
//        job.setOutputKeyClass(IntWritable.class);
//        job.setOutputValueClass(Text.class);
//
//        job.setNumReduceTasks(2);
//
////        job.setSortComparatorClass(MyDescendingComparator.class);
////        job.setInputFormatClass(SequenceFileInputFormat.class);
//        job.setInputFormatClass(TextInputFormat.class);
//
//        FileInputFormat.addInputPath(job, new Path(uploadPath + name));
//        FileOutputFormat.setOutputPath(job, new Path(tagcloudPath + name));
//
//        job.waitForCompletion(true);
    }

    public void makeTagcloudByName(String name) throws IOException {
        final FrequencyFileLoader frequencyFileLoader = new FrequencyFileLoader();
        List<File> wordCountFiles = getWordCountFiles(name);
        List<WordFrequency> wordFrequencies = new ArrayList<>();
        for (File f : wordCountFiles) {
            wordFrequencies.addAll(frequencyFileLoader.load(f));
        }


        final Dimension dimension = new Dimension(600, 600);
        final WordCloud wordCloud = new WordCloud(dimension, CollisionMode.PIXEL_PERFECT);
        wordCloud.setPadding(2);
        wordCloud.setBackground(new CircleBackground(300));
        wordCloud.setColorPalette(new ColorPalette(new Color(0x4055F1), new Color(0x408DF1), new Color(0x40AAF1), new Color(0x40C5F1), new Color(0x40D3F1), new Color(0xFFFFFF)));
        wordCloud.setFontScalar(new SqrtFontScalar(10, 40));
        wordCloud.build(wordFrequencies);
        File outputFile = new File(paths.getTagclouds() + name + ".png");
        outputFile.getParentFile().mkdirs();
        outputFile.createNewFile();
        wordCloud.writeToFile(outputFile.getAbsolutePath());
    }

    public void makeTagcloud(List<WordFrequency> wordFrequencies, File outputFile) throws IOException {
        final Dimension dimension = new Dimension(600, 600);
        final WordCloud wordCloud = new WordCloud(dimension, CollisionMode.RECTANGLE);
        wordCloud.setPadding(2);
        wordCloud.setBackground(new RectangleBackground(dimension));
        wordCloud.setColorPalette(new ColorPalette(new Color(0x4055F1), new Color(0x408DF1), new Color(0x40AAF1), new Color(0x40C5F1), new Color(0x40D3F1), new Color(0xFFFFFF)));
        wordCloud.setFontScalar(new SqrtFontScalar(5, 25));
        wordCloud.build(wordFrequencies);
        outputFile.getParentFile().mkdirs();
        outputFile.createNewFile();
        wordCloud.writeToFile(outputFile.getAbsolutePath());
    }

    private List<File> getWordCountFiles(String name) throws IOException {
        Resource[] resources = ResourcePatternUtils.getResourcePatternResolver(resourceLoader).getResources("file:" + paths.getWordcounts() + name + "/part*");
        List<File> wordCountFiles = new ArrayList<>();
        for (Resource r : resources) {
            wordCountFiles.add(r.getFile());
        }
        return wordCountFiles;
    }

}

