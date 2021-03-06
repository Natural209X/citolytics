package org.wikipedia.citolytics.linkgraph;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;
import org.wikipedia.citolytics.WikiSimJob;
import org.wikipedia.citolytics.cpa.io.WikiDocumentDelimitedInputFormat;
import org.wikipedia.citolytics.cpa.operators.DocumentProcessor;
import org.wikipedia.citolytics.cpa.types.WikiDocument;

import java.util.List;
import java.util.Map;

/**
 * Extracts internal links from Wikipedia articles and creates CSV for DB import
 * <p/>
 * table structure: article (primary key), link target
 */
public class LinksExtractor extends WikiSimJob<Tuple2<String, String>> {

    public static void main(String[] args) throws Exception {
        new LinksExtractor().start(args);
    }

    public void plan() {

        if (args.length <= 1) {
            System.err.println("Input/output parameters missing!");
            System.err.println("USAGE: <wiki-xml-dump> <output>");
            System.exit(1);
        }

        String inputFilename = args[0];
        outputFilename = args[1];

        result = env.readFile(new WikiDocumentDelimitedInputFormat(), inputFilename)
                .flatMap(new FlatMapFunction<String, Tuple2<String, String>>() {
                    public void flatMap(String content, Collector out) {
                        collectLinks(content, out);
                    }
                })
                .distinct();
    }

    public static void collectLinks(String content, Collector<Tuple2<String, String>> out) {
        WikiDocument doc = new DocumentProcessor().processDoc(content);
        if (doc == null) return;

        List<Map.Entry<String, Integer>> links = doc.getOutLinks();

        for (Map.Entry<String, Integer> outLink : links) {

            out.collect(new Tuple2<>(
                    doc.getTitle(),
                    outLink.getKey()
            ));
        }
    }
}
