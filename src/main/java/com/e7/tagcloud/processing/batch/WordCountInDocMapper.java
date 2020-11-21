package com.e7.tagcloud.processing.batch;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.io.Text;

import java.io.IOException;

public class WordCountInDocMapper extends Mapper<Object, Text, Text, Text>{
    private Text docName = new Text();
    private Text wordAndCount = new Text();

    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String[] wordDocCounter = value.toString().split("\t");
            String[] wordDoc = wordDocCounter[0].split("@");
            this.docName.set(wordDoc[1]);
            this.wordAndCount.set(wordDoc[0] + "=" + wordDocCounter[1]);
            context.write(this.docName, this.wordAndCount);
    }
}
