package com.e7.tagcloud.processing.batch;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapred.FileSplit;
import org.apache.hadoop.mapreduce.Mapper;

public class TFIDFMapper extends Mapper<Text, Text, Text, Text> {
    public TFIDFMapper() {
    }

    private Text wordAndDoc = new Text();
    private Text wordAndCounters = new Text();

    public void map(Text key, Text value, Context context) throws IOException, InterruptedException {
        System.out.println(key.toString() + "   |   " + value.toString());
        String[] wordAndDoc = key.toString().split("@");
        this.wordAndDoc.set(new Text(wordAndDoc[0]));
        this.wordAndCounters.set(wordAndDoc[1] + "=" + value.toString());
        context.write(this.wordAndDoc, this.wordAndCounters);
    }
}
