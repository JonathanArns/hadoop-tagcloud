package com.e7.tagcloud.processing.batch;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class TFIDFReducer extends Reducer<Text, Text, Text, Text> {
    private static final DecimalFormat DF = new DecimalFormat("###.########");

    private Text wordAtDocument = new Text();
    private Text tfidfCounts = new Text();

    public TFIDFReducer() {
    }

    protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {

        int numberOfDocumentsInCorpus = context.getConfiguration().getInt("docscount", 1);

        int numberOfDocumentsInCorpusWhereKeyAppears = 0;
        Map<String, String> tempFrequencies = new HashMap<>();
        for (Text val : values) {
            String[] documentAndFrequencies = val.toString().split("=");
            tempFrequencies.put(documentAndFrequencies[0], documentAndFrequencies[1]);
            if (documentAndFrequencies[0].equals("global")) continue;
            if (Integer.parseInt(documentAndFrequencies[1].split("/")[0]) > 0)
                numberOfDocumentsInCorpusWhereKeyAppears++;

        }

        double idf = Math.log10((double) numberOfDocumentsInCorpus /
                (double) ((numberOfDocumentsInCorpusWhereKeyAppears == 0 ? 1 : 0) +
                        numberOfDocumentsInCorpusWhereKeyAppears));

        for (String document : tempFrequencies.keySet()) {
            String[] wordFrequenceAndTotalWords = tempFrequencies.get(document).split("/");

            double tf = Double.parseDouble(wordFrequenceAndTotalWords[0]) / Double.parseDouble(wordFrequenceAndTotalWords[1]);

            int tfIdf = (int) (tf * idf * 10000);

            this.wordAtDocument.set(key + "@" + document);
            this.tfidfCounts.set(DF.format(tfIdf) + "");

            context.write(this.wordAtDocument, this.tfidfCounts);
        }
    }
}
