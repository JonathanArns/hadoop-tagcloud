package com.e7.tagcloud.hadoop;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class TagcloudMapper extends Mapper<Text, IntWritable, Text, IntWritable> {

    public void map(Text word, IntWritable count, Context context) throws IOException, InterruptedException {

        context.write(word, new IntWritable(1));
    }

}