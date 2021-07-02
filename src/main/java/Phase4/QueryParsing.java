package Phase4;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;
import org.deeplearning4j.models.embeddings.learning.impl.elements.CBOW;
import org.deeplearning4j.models.embeddings.learning.impl.elements.SkipGram;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class QueryParsing
{
    public QueryParsing(String filename, int topK)
    {
        String indexLocation = "index";
        String field = "contents";

        try {
            IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexLocation)));
            IndexSearcher searcher = new IndexSearcher(reader);
            ArrayList<QueryData> data = Preprocess.queryPreprocessor(filename);

            search(searcher, reader, data, field, topK);

            reader.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void search(IndexSearcher searcher, IndexReader reader, ArrayList<QueryData> data, String field, int topK)
    {
        try
        {
            FieldValuesSentenceIterator iterator = new FieldValuesSentenceIterator(reader, field);

            Word2Vec vec = new Word2Vec.Builder()
                    .layerSize(100)
                    .windowSize(2)
                    .tokenizerFactory(new DefaultTokenizerFactory())
                    .iterate(iterator)
                    .elementsLearningAlgorithm(new CBOW<>())
                    //.elementsLearningAlgorithm(new SkipGram<>())
                    .build();

            vec.fit();

            CharArraySet stopWords = new CharArraySet(Arrays.asList("a", "an", "the"), true);
            QueryParser parser = new QueryParser(field, new StopAnalyzer(stopWords));

            File resultsFile = new File( "phase4_our_results_" + topK +".txt");
            if (resultsFile.exists()) {
                resultsFile.delete();
                resultsFile.createNewFile();
            }

            for (QueryData q : data)
            {
                Query query = parser.parse(QueryParser.escape(q.getWords()));
                /*TopDocs results = searcher.search(query, topK);
                ScoreDoc[] hits = results.scoreDocs;

                int i = 0;
                for (ScoreDoc hit : hits)
                {
                    Explanation explanation = searcher.explain(query, hit.doc);
                    //System.out.println(explanation);

                    Document hitDoc = searcher.doc(hit.doc);
                    System.out.println(i+ " ---------------------------------------");
                    System.out.println("\t["+hit.score+"] \tID: " + hitDoc.get("id") + " \tTITLE: " + hitDoc.get("title") + " \tW: " + hitDoc.get("w") + " \tB: " + hitDoc.get("b") +
                            " \tAUTHORS: " + hitDoc.get("authors") + " \tKEYS: " + hitDoc.get("keys") + " \tC: " + hitDoc.get("c") + " \tNAME: " + hitDoc.get("name"));
                    i++;
                }*/



                //ArrayList<ScoreDoc> scoreDocs = new ArrayList<ScoreDoc>(Arrays.asList(hits));
                Collection<String> search;
                String words[] = query.toString().split("\\s");

                for (String word : words)
                {
                    if (word.length() == 0) continue;
                    word = word.replace("contents:", "");
                    search = vec.wordsNearestSum(word, 2);
                    System.out.println(word+ " : " +search);
                }


                /*BufferedWriter bw = new BufferedWriter(new FileWriter(resultsFile, true));
                String queryId = String.valueOf(q.getId());
                if (q.getId() < 10) queryId = "0" + queryId;

                for (ScoreDoc sd : scoreDocs) {
                    Document hitDoc = searcher.doc(sd.doc);
                    StringBuilder docId = new StringBuilder(hitDoc.get("id"));

                    *//*String queryIdDocIdComb = queryId + "|" + docId;
                    if (queryIdDocIdCombinations.contains(queryIdDocIdComb)) continue;
                    queryIdDocIdCombinations.add(queryIdDocIdComb);*//*

                    while (docId.length() < 4)
                    {
                        docId.insert(0, "0");
                    }
                    bw.append(queryId + "\t0" + "\t" + docId.toString() + "\t0" + "\t" + sd.score + "\tstandard_run_id\n");
                }
                bw.close();*/
            }


        }
        catch (IOException | ParseException e)
        {
            e.printStackTrace();
        }

    }
}
