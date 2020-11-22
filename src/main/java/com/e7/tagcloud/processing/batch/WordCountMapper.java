package com.e7.tagcloud.processing.batch;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

public class WordCountMapper extends Mapper<Object, Text, Text, IntWritable>{
    private final static IntWritable _intWr = new IntWritable(1);
    private Text txt = new Text();

    public void map(Object key, Text val, Context ctx) throws IOException, InterruptedException {
        String fileName = "@-_-@" + ((FileSplit) ctx.getInputSplit()).getPath().getName();

        Pattern p = Pattern.compile("(\\b[^\\s]+\\b)");
        Matcher m = p.matcher(val.toString());
        while(m.find()) {
            txt.set(val.toString().substring(m.start(), m.end()).toLowerCase() + fileName);
            ctx.write(txt, _intWr);
        }
    }
}