package com.e7.tagcloud.processing.batch;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

// word=#
public class WordCountInDocReducer extends Reducer<Text, Text, Text, Text> {


    protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        System.out.println(key.toString());
        System.out.println("Values:" + values);
        int sumOfAll = 0;
        Map<String, Integer> someMap = new HashMap<>();
        for (Text val : values) {
            String[] countWord = val.toString().split("=");
            int numOfWord = Integer.parseInt(countWord[1]);
            sumOfAll += numOfWord;
            someMap.put(countWord[0], numOfWord);
        }

        for(String wKey : someMap.keySet()) {
            Text ctxKey = new Text();
            ctxKey.set(wKey + "@" + key.toString());
            System.out.println(ctxKey.toString());
            Text ctxVal = new Text();
            ctxVal.set(someMap.get(wKey) + "/" + sumOfAll);
            context.write(ctxKey, ctxVal);
        }
    }
}
