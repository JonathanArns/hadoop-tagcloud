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

    private Text word = new Text();
    private Text docAndCounters = new Text();

    public void map(Text key, Text value, Context context) throws IOException, InterruptedException {
        String[] wordAndDoc = key.toString().split("@-_-@");
        this.word.set(new Text(wordAndDoc[0]));
        this.docAndCounters.set(wordAndDoc[1] + "=" + value.toString());
        context.write(this.word, this.docAndCounters);
    }
}
