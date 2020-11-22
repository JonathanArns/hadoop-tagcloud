package com.e7.tagcloud.processing.batch;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.io.Text;

import java.io.IOException;

public class WordCountInDocMapper extends Mapper<Text, IntWritable, Text, Text>{
    private Text docName = new Text();
    private Text wordAndCount = new Text();

    public void map(Text key, IntWritable value, Context context) throws IOException, InterruptedException {
            String[] wordDoc = key.toString().split("@");
            this.docName.set(wordDoc[1]);
            this.wordAndCount.set(wordDoc[0] + "=" + value.get());
            context.write(this.docName, this.wordAndCount);
    }
}
