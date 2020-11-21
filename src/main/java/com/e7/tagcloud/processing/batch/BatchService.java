package com.e7.tagcloud.processing.batch;

import com.e7.tagcloud.TagcloudApplication;
import com.e7.tagcloud.processing.TagcloudService;
import com.e7.tagcloud.util.Paths;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.reduce.IntSumReducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class BatchService {
    @Autowired
    TagcloudService tagcloudService;
    @Autowired
    Paths paths;

    public run() throws IOException, ClassNotFoundException, InterruptedException {
        Configuration conf = new Configuration();

        Job wordCountJob = Job.getInstance(conf, "word count");
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

    }
}
