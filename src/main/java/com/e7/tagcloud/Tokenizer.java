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
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.mapreduce.lib.reduce.IntSumReducer;
import org.apache.log4j.BasicConfigurator;

public class Tokenizer extends Mapper<Object, Text, Text, IntWritable>{
    private final static IntWritable _intWr = new IntWritable();
    private Text txt = new Text();

    public void map(Object key, Text val, Context ctx) throws IOException, InterruptedException {

        Pattern p = Pattern.compile("(\\b[^\\s]+\\b)");
        Matcher m = p.matcher(val.toString());
        while(m.find()) {
            txt.set(val.toString().substring(m.start(), m.end()).toLowerCase());
            ctx.write(txt, _intWr);
        }
    }
}