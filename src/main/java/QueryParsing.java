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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.ArrayList;

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
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String line;

            for (QueryData d : data)
            {
                Query query = queryParser.parse(d.getWords());
                TopDocs results = indexSearcher.search(query, topK);
                ScoreDoc[] hits = results.scoreDocs;
                long numTotalHits = results.totalHits;
                System.out.println("Query id : " +d.getId()+ " has " +numTotalHits + " total matching documents");

                for (ScoreDoc hit : hits)
                {
                    Document hitDoc = indexSearcher.doc(hit.doc);
                    //System.out.println("\tScore "+hits[i].score +"\ttitle="+hitDoc.get("title")+"\tcaption:"+hitDoc.get("caption")+"\tmesh:"+hitDoc.get("mesh")); //use ours later
                }
            }


            /*while ((line = br.readLine()) != null)
            {
                Query query = queryParser.parse(line);
                TopDocs results = indexSearcher.search(query, topK);
                ScoreDoc[] hits = results.scoreDocs;
                long numTotalHits = results.totalHits;
                System.out.println(numTotalHits + " total matching documents");

                for (int i = 0; i < hits.length; i++)
                {
                    Document hitDoc = indexSearcher.doc(hits[i].doc);
                    //System.out.println("\tScore "+hits[i].score +"\ttitle="+hitDoc.get("title")+"\tcaption:"+hitDoc.get("caption")+"\tmesh:"+hitDoc.get("mesh")); //use ours later
                }
            }*/
        }
        catch (IOException | ParseException e)
        {
            e.printStackTrace();
        }
    }
}
