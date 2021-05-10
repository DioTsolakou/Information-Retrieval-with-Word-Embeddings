import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.LMJelinekMercerSimilarity;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.document.Document;
import java.io.*;
import java.nio.file.Paths;
import java.util.*;

public class QueryParsing
{
    public QueryParsing(String filename, int topK, String similarityName)
    {
        String indexLocation = "index";
        String field = "contents";
        try
        {
            IndexReader indexReader = DirectoryReader.open(FSDirectory.open(Paths.get(indexLocation)));
            IndexSearcher indexSearcher = new IndexSearcher(indexReader);
            if (similarityName.equalsIgnoreCase("lmj"))
            {
                indexSearcher.setSimilarity(new LMJelinekMercerSimilarity((float)0.3));
            }
            else if (similarityName.equalsIgnoreCase("bm25"))
            {
                indexSearcher.setSimilarity(new BM25Similarity());
            }
            else
            {
                indexSearcher.setSimilarity(new ClassicSimilarity());
            }

            ArrayList<QueryData> data = Preprocess.queryPreprocessor(filename);

            search(data, indexSearcher, field, topK);

            indexReader.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void search(ArrayList<QueryData> data, IndexSearcher indexSearcher, String field, int topK)
    {
        try
        {
            Analyzer analyzer = new EnglishAnalyzer();
            QueryParser queryParser = new QueryParser(field, analyzer);
            queryParser.setAllowLeadingWildcard(true);
            String similarityName = String.valueOf(indexSearcher.getSimilarity(true));
            similarityName = similarityName.replace(" ", "_");
            File resultsFile = new File(similarityName + "_our_results_" + topK +".txt");
            if (resultsFile.exists()) {
                resultsFile.delete();
                resultsFile.createNewFile();
            }

            ArrayList<String> queryIdDocIdCombinations = new ArrayList<>();
            for (QueryData q : data)
            {
                //queryIdocIdCombinations.clear();
                Query query = queryParser.parse(QueryParser.escape(q.getWords()));
                TopDocs results = indexSearcher.search(query, topK);
                ScoreDoc[] hits = results.scoreDocs;

                for (ScoreDoc hit : hits)
                {
                    Document hitDoc = indexSearcher.doc(hit.doc);
                    System.out.println("\t["+hit.score+"] \tID: " + hitDoc.get("id") + " \tTITLE: " + hitDoc.get("title") + " \tW: " + hitDoc.get("w") + " \tB: " + hitDoc.get("b") +
                                        " \tAUTHORS: " + hitDoc.get("authors") + " \tKEYS: " + hitDoc.get("keys") + " \tC: " + hitDoc.get("c") + " \tNAME: " + hitDoc.get("name"));
                }
                ArrayList<ScoreDoc> scoreDocs = new ArrayList<ScoreDoc>(Arrays.asList(hits));
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

                for (ScoreDoc sd: scoreDocs) {
                    Document hitDoc = indexSearcher.doc(sd.doc);
                    StringBuilder docId = new StringBuilder(hitDoc.get("id"));

                    String queryIdDocIdComb = queryId + "|" + docId;
                    if (queryIdDocIdCombinations.contains(queryIdDocIdComb)) continue;
                    queryIdDocIdCombinations.add(queryIdDocIdComb);

                    while (docId.length() < 4)
                    {
                        docId.insert(0, "0");
                    }
                    bw.append(queryId + "\t0" + "\t" + docId.toString() + "\t0" + "\t" + sd.score + "\tstandard_run_id\n");
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