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
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
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
        long timestamp = System.currentTimeMillis();

        // Job: WordCount /////////////////////////////////////////////////////
        // word@filename : count
        Configuration conf1 = new Configuration();
        Job wordCountJob = Job.getInstance(conf1, "word count");
        wordCountJob.setJarByClass(TagcloudApplication.class);
        wordCountJob.setMapperClass(WordCountMapper.class);
        wordCountJob.setCombinerClass(IntSumReducer.class);
        wordCountJob.setReducerClass(IntSumReducer.class);
        wordCountJob.setNumReduceTasks(2);
        wordCountJob.setOutputKeyClass(Text.class);
        wordCountJob.setOutputValueClass(IntWritable.class);

        wordCountJob.setOutputFormatClass(SequenceFileOutputFormat.class);
        FileInputFormat.addInputPath(wordCountJob, new Path(paths.getUpload()));
        FileOutputFormat.setOutputPath(wordCountJob, new Path(paths.getJob1() + timestamp));

        wordCountJob.waitForCompletion(true);
        ///////////////////////////////////////////////////////////////////////

        // Job: WordCount per docs ////////////////////////////////////////////
        // word@filename : count/file_total
        Configuration conf2 = new Configuration();
        Job wordDocJob = Job.getInstance(conf2, "word count per doc");
        wordDocJob.setMapperClass(WordCountInDocMapper.class);
//        wordDocJob.setCombinerClass(IntSumReducer.class);
        wordDocJob.setReducerClass(WordCountInDocReducer.class);
        wordDocJob.setNumReduceTasks(2);
        wordDocJob.setOutputKeyClass(Text.class);
        wordDocJob.setOutputValueClass(Text.class);

        wordDocJob.setInputFormatClass(SequenceFileInputFormat.class);
        wordDocJob.setOutputFormatClass(SequenceFileOutputFormat.class);
        FileInputFormat.addInputPath(wordDocJob, new Path(paths.getJob1() + timestamp));
        FileOutputFormat.setOutputPath(wordDocJob, new Path(paths.getJob2() + timestamp));

        wordDocJob.waitForCompletion(true);

        // Job: TF-IDF ////////////////////////////////////////////////////////
        Configuration conf3 = new Configuration();
        Job tfidfJob = Job.getInstance(conf3, "tf-idf job");
        tfidfJob.setMapperClass(TFIDFMapper.class);
//        tfidfJob.setCombinerClass(IntSumReducer.class);
        tfidfJob.setReducerClass(TFIDFReducer.class);
        tfidfJob.setNumReduceTasks(2);
        tfidfJob.setOutputKeyClass(Text.class);
        tfidfJob.setOutputValueClass(Text.class);

        tfidfJob.setInputFormatClass(SequenceFileInputFormat.class);
        FileInputFormat.addInputPath(tfidfJob, new Path(paths.getJob2() + timestamp));
        FileOutputFormat.setOutputPath(tfidfJob, new Path(paths.getJob3() + timestamp));

        tfidfJob.waitForCompletion(true);




        // last job output format in text file:
        // count: word

    }
}
