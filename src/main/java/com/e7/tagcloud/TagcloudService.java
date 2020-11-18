package com.e7.tagcloud;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.mapreduce.lib.reduce.IntSumReducer;
import org.apache.log4j.BasicConfigurator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TagcloudService {
    static {
        System.setProperty("hadoop.home.dir", "/");
    }

    @Value("${paths.upload}")
    private String uploadPath;

    @Value("${paths.tagclouds}")
    private String tagcloudPath;

//    private Job job;

    public void makeTagcloud(String name) throws IOException, ClassNotFoundException, InterruptedException { // not sure what it will throw b now :D
        BasicConfigurator.configure();
        Configuration cfg = new Configuration();

        Job job = Job.getInstance(cfg, "tagcloud");
        job.setJarByClass(Tokenizer.class);
        job.setMapperClass(Mapper.class);
        job.setReducerClass(Reducer.class);
        job.setOutputKeyClass(IntWritable.class);
        job.setOutputValueClass(Text.class);

        job.setNumReduceTasks(2);

//        job.setSortComparatorClass(MyDescendingComparator.class);
//        job.setInputFormatClass(SequenceFileInputFormat.class);
        job.setInputFormatClass(TextInputFormat.class);

        FileInputFormat.addInputPath(job, new Path(uploadPath + name));
        FileOutputFormat.setOutputPath(job, new Path(tagcloudPath + name));

        job.waitForCompletion(true);
    }
}