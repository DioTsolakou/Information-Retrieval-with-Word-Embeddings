package Phase5;

import Phase4.WordEmbeddingsSimilarity;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.*;
import org.apache.lucene.store.FSDirectory;
import org.deeplearning4j.models.word2vec.Word2Vec;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class QueryParsing
{
    public QueryParsing(String filename, int topK, MultiSimilarity similarity, Word2Vec vec, String resultsFilename)
    {
        String indexLocation = "index";
        String field = "contents";
        try
        {
            IndexReader indexReader = DirectoryReader.open(FSDirectory.open(Paths.get(indexLocation)));
            IndexSearcher indexSearcher = new IndexSearcher(indexReader);
            indexSearcher.setSimilarity(similarity);

            ArrayList<QueryData> data = Preprocess.queryPreprocessor(filename);
            search(data, indexSearcher, field, topK, vec, resultsFilename);

            indexReader.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void search(ArrayList<QueryData> data, IndexSearcher indexSearcher, String field, int topK, Word2Vec vec, String resultsFilename)
    {
        try
        {
            QueryParser queryParser = new QueryParser(field, new WhitespaceAnalyzer());
            queryParser.setAllowLeadingWildcard(true);

            File resultsFile = new File( "phase5_our_results_" + topK +".txt");
            if (resultsFile.exists()) {
                resultsFile.delete();
                resultsFile.createNewFile();
            }

            for (QueryData q : data)
            {
                Query query = queryParser.parse(QueryParser.escape(q.getWords()));
                ArrayList<String> words = new ArrayList<>(Arrays.asList(query.toString().split("\\s")));
                StringBuilder fixedQueryTerms = new StringBuilder();

                for (String word : words)
                {
                    //if (word.length() == 0) continue;
                    word = word.replace("contents:", "");
                    if (!vec.hasWord(word)) words.remove(word);
                    fixedQueryTerms.append(word).append(" ");
                }

                q.setWords(fixedQueryTerms.toString());
                query = queryParser.parse(QueryParser.escape(q.getWords()));

                TopDocs results = indexSearcher.search(query, topK);
                ScoreDoc[] hits = results.scoreDocs;

                /*for (ScoreDoc hit : hits)
                {
                    Document hitDoc = indexSearcher.doc(hit.doc);
                    System.out.println("\t["+hit.score+"] \tID: " + hitDoc.get("id") + " \tTITLE: " + hitDoc.get("title") + " \tW: " + hitDoc.get("w") + " \tB: " + hitDoc.get("b") +
                            " \tAUTHORS: " + hitDoc.get("authors") + " \tKEYS: " + hitDoc.get("keys") + " \tC: " + hitDoc.get("c") + " \tNAME: " + hitDoc.get("name"));
                }*/

                ArrayList<ScoreDoc> scoreDocs = new ArrayList<ScoreDoc>(Arrays.asList(hits));
                for (ScoreDoc sd : scoreDocs)
                {
                    if (Float.isNaN(sd.score)) sd.score = (float) 0;
                }

                scoreDocs.sort(new Comparator() {
                    public int compare(Object o1, Object o2) {
                        ScoreDoc sd1 = (ScoreDoc) o1;
                        ScoreDoc sd2 = (ScoreDoc) o2;
                        return -1 * Float.compare((sd1.score), (sd2.score));
                    }
                });
                BufferedWriter bw = new BufferedWriter(new FileWriter(resultsFile, true));
                String queryId = String.valueOf(q.getId());
                if (q.getId() < 10) queryId = "0" + queryId;

                for (ScoreDoc sd : scoreDocs) {
                    Document hitDoc = indexSearcher.doc(sd.doc);
                    StringBuilder docId = new StringBuilder(hitDoc.get("id"));

                    while (docId.length() < 4)
                    {
                        docId.insert(0, "0");
                    }
                    bw.append(queryId + "\t0" + "\t" + docId + "\t0" + "\t" + sd.score + "\tstandard_run_id\n");
                }
                bw.close();
            }
        }
        catch (IOException | ParseException e)
        {
            e.printStackTrace();
        }
    }
}