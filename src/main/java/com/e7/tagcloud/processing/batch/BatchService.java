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

    public void run() throws IOException, ClassNotFoundException, InterruptedException {
        Configuration conf = new Configuration();

        // Job: WordCount /////////////////////////////////////////////////////
        Job wordCountJob = Job.getInstance(conf, "word count");
        wordCountJob.setJarByClass(TagcloudApplication.class);
        wordCountJob.setMapperClass(WordCountMapper.class);
        wordCountJob.setCombinerClass(IntSumReducer.class);
        wordCountJob.setReducerClass(WordCountReducer.class);
        wordCountJob.setNumReduceTasks(2);
        wordCountJob.setOutputKeyClass(Text.class);
        wordCountJob.setOutputValueClass(IntWritable.class);

        FileInputFormat.addInputPath(wordCountJob, new Path(paths.getUpload() + name));
        FileOutputFormat.setOutputPath(wordCountJob, new Path(paths.getWordcounts() + name));

        wordCountJob.waitForCompletion(true);
        ///////////////////////////////////////////////////////////////////////

        // Job: WordCount per docs ////////////////////////////////////////////
        Job wordDocJob = Job.getInstance(conf, "word count per doc");
        wordDocJob.setMapperClass(WordCountInDocMapper.class);
        wordDocJob.setCombinerClass(IntSumReducer.class);
        wordDocJob.setReducerClass(WordCountInDocReducer.class);
        wordDocJob.setNumReduceTasks(2);
        wordDocJob.setOutputKeyClass(Text.class);
        wordDocJob.setOutputValueClass(IntWritable.class);

        FileInputFormat.addInputPath(wordCountJob, new Path(paths.getUpload() + name));
        FileOutputFormat.setOutputPath(wordCountJob, new Path(paths.getWordcounts() + name));

        wordDocJob.waitForCompletion(true);

        // Job: TF-IDF ////////////////////////////////////////////////////////
        Job tfidfJob = Job.getInstance(conf, "tf-idf job");
        tfidfJob.setMapperClass(TFIDFMapper.class);
        tfidfJob.setCombinerClass(IntSumReducer.class);
        tfidfJob.setReducerClass(TFIDFReducer.class);
        tfidfJob.setNumReduceTasks(2);
        tfidfJob.setOutputKeyClass(Text.class);
        tfidfJob.setOutputValueClass(IntWritable.class);

        FileInputFormat.addInputPath(wordCountJob, new Path(paths.getUpload() + name));
        FileOutputFormat.setOutputPath(wordCountJob, new Path(paths.getWordcounts() + name));

        tfidfJob.waitForCompletion(true);

    }
}