package com.e7.tagcloud.processing.batch;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileSplit;
import org.apache.hadoop.mapreduce.Mapper;

public class TFIDFMapper extends Mapper<LongWritable, Text, Text, Text> {
    public TFIDFMapper() {
    }

    private Text wordAndDoc = new Text();
    private Text wordAndCounters = new Text();

    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String[] wordAndCounters = value.toString().split("\t");
        String[] wordAndDoc = wordAndCounters[0].split("@");
        this.wordAndDoc.set(new Text(wordAndDoc[0]));
        this.wordAndCounters.set(wordAndDoc[1] + "=" + wordAndCounters[1]);
        context.write(this.wordAndDoc, this.wordAndCounters);
    }
}
