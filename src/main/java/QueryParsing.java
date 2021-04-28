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
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.document.Document;

import java.io.*;
import java.lang.reflect.Array;
import java.nio.file.Paths;
import java.util.*;

public class QueryParsing
{
    public QueryParsing(String filename, int topK)
    {
        String indexLocation = "index";
        String field = "contents";
        try
        {
            IndexReader indexReader = DirectoryReader.open(FSDirectory.open(Paths.get(indexLocation)));
            IndexSearcher indexSearcher = new IndexSearcher(indexReader);
            indexSearcher.setSimilarity(new ClassicSimilarity());

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
            File resultsFile = new File("our_results_" + topK +".txt");
            if (resultsFile.exists()) {
                resultsFile.delete();
                resultsFile.createNewFile();
            }
            for (QueryData d : data)
            {
                Query query = queryParser.parse(QueryParser.escape(d.getWords()));
                TopDocs results = indexSearcher.search(query, topK);
                ScoreDoc[] hits = results.scoreDocs;
                long numTotalHits = results.totalHits;
                //System.out.println("Query id : " +d.getId()+ " has " +numTotalHits+ " total matching documents");
                //System.out.println("Query id : " +d.getId()+ " has " +hits.length+ " top matching documents");
                for (ScoreDoc hit : hits)
                {
                    Document hitDoc = indexSearcher.doc(hit.doc);
                    System.out.println("\t["+hit.score+"] \tID: " + hitDoc.get("id") + " \tTITLE: " + hitDoc.get("title") + " \tW: " + hitDoc.get("w") + " \tB: " + hitDoc.get("b") +
                                        " \tAUTHORS: " + hitDoc.get("authors") + " \tKEYS: " + hitDoc.get("keys") + " \tC: " + hitDoc.get("c") + " \tNAME: " + hitDoc.get("name"));
                    //System.out.println("\tScore "+hits[i].score +"\ttitle="+hitDoc.get("title")+"\tcaption:"+hitDoc.get("caption")+"\tmesh:"+hitDoc.get("mesh")); //use ours later
                }
                ArrayList<ScoreDoc> scoreDocs = new ArrayList<ScoreDoc>(Arrays.asList(hits));
                Collections.sort(scoreDocs, new Comparator() {
                    public int compare(Object o1, Object o2) {
                        ScoreDoc sd1 = (ScoreDoc)o1;
                        ScoreDoc sd2 = (ScoreDoc)o2;
                        return -1 * Float.compare((sd1.score), (sd2.score));
                    }
                });
                BufferedWriter bw = new BufferedWriter(new FileWriter(resultsFile, true));
                String docId = String.valueOf(d.getId());
                if (d.getId() < 10) docId = "0" + docId;
                for (ScoreDoc sd: scoreDocs) {
                    Document hitDoc = indexSearcher.doc(sd.doc);
                    bw.append(docId + " 0 " + hitDoc.get("id") + " 0 " + sd.score + " standard_run_id\n");
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
