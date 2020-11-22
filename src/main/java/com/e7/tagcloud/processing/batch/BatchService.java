package com.e7.tagcloud.processing.batch;

import com.e7.tagcloud.TagcloudApplication;
import com.e7.tagcloud.processing.TagcloudService;
import com.e7.tagcloud.util.Paths;
import com.kennycason.kumo.Word;
import com.kennycason.kumo.WordFrequency;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
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
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
public class BatchService {
    @Autowired
    TagcloudService tagcloudService;
    @Autowired
    ResourceLoader resourceLoader;
    @Autowired
    Paths paths;

    public void run() throws IOException, ClassNotFoundException, InterruptedException {
        long timestamp = System.currentTimeMillis();
        Configuration conf1 = new Configuration();
        FileSystem fs = FileSystem.get(conf1);
        // get doc count
         int docsCount = fs.listStatus(new Path(paths.getUpload())).length;
        // Job: WordCount /////////////////////////////////////////////////////
        // word@filename : count

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
        conf3.setStrings("docscount", ""+docsCount);
        Job tfidfJob = Job.getInstance(conf3, "tf-idf job");
        tfidfJob.setMapperClass(TFIDFMapper.class);
        tfidfJob.setReducerClass(TFIDFReducer.class);
        tfidfJob.setNumReduceTasks(2);
        tfidfJob.setOutputKeyClass(Text.class);
        tfidfJob.setOutputValueClass(Text.class);

        tfidfJob.setInputFormatClass(SequenceFileInputFormat.class);
        FileInputFormat.addInputPath(tfidfJob, new Path(paths.getJob2() + timestamp));
        FileOutputFormat.setOutputPath(tfidfJob, new Path(paths.getJob3() + timestamp));

        tfidfJob.waitForCompletion(true);


        // tag cloud per doc
        Map<String, List<WordFrequency>> freqs = getWordFreq(getWordCountFiles(""+timestamp));
        for (String key : freqs.keySet()) {
            tagcloudService.makeTagcloud(freqs.get(key), new File(paths.getTagclouds()+ key+"out.png"));
        }

    }

    private List<File> getWordCountFiles(String tst) throws IOException {
        Resource[] resources = ResourcePatternUtils.getResourcePatternResolver(resourceLoader).getResources("file:" + paths.getJob3() + tst + "/part*");
        List<File> wordCountFiles = new ArrayList<>();
        for (Resource r : resources) {
            wordCountFiles.add(r.getFile());
        }
        return wordCountFiles;
    }


    private Map<String, List<WordFrequency>> getWordFreq(List<File> fs) throws IOException{
        Map<String, List<WordFrequency>> tfidfMap = new HashMap<>();

        for (File f : fs) {
            Scanner reader = new Scanner(f);
            while (reader.hasNextLine()) {
                String val = reader.nextLine();
                // word@doc #
                String[] tokens = val.split("\t");
                String[] word_doc = tokens[0].split("@");
                List<WordFrequency> tmp = tfidfMap.get(word_doc[1]);
                if (tmp == null) { // doc not in map
                    tmp = new ArrayList<>();
                    tfidfMap.put(word_doc[1], tmp);
                }
                tmp.add(new WordFrequency(word_doc[0], Integer.parseInt(tokens[1])));
            }
        }
        return tfidfMap;
    }
}
