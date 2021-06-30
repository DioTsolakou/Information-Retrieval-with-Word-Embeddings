package Phase4;

import Phase1.Preprocess;
import Phase1.QueryData;
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
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
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
            CharArraySet stopWords = new CharArraySet(Arrays.asList("a", "an", "the"), true);

            /*QueryParser parser = new QueryParser("id", new KeywordAnalyzer());
            QueryParser parser = new QueryParser("title", new WhitespaceAnalyzer());
            QueryParser parser = new QueryParser("w", new StopAnalyzer(stopWords));
            QueryParser parser = new QueryParser("b", new WhitespaceAnalyzer());
            QueryParser parser = new QueryParser("authors", new WhitespaceAnalyzer());
            QueryParser parser = new QueryParser("keys", new WhitespaceAnalyzer());
            QueryParser parser = new QueryParser("c", new KeywordAnalyzer());
            QueryParser parser = new QueryParser("name", new StopAnalyzer(stopWords));*/
            QueryParser parser = new QueryParser("contents", new StopAnalyzer(stopWords));


            Query query = parser.parse("+search");
            TopDocs hits = searcher.search(query, 10);
            for (int i = 0; i < hits.scoreDocs.length; i++) {
                ScoreDoc scoreDoc = hits.scoreDocs[i];
                Explanation explanation = searcher.explain(query, scoreDoc.doc);
                System.out.println(explanation);
                Document doc = searcher.doc(scoreDoc.doc);
                System.out.println("--");
                System.out.println(doc.get("title") + " : " + scoreDoc.score);
            }

            FieldValuesSentenceIterator iterator = new FieldValuesSentenceIterator(reader,"page");

            Word2Vec vec = new Word2Vec.Builder()
                    .layerSize(100)
                    .windowSize(2)
                    .tokenizerFactory(new DefaultTokenizerFactory())
                    .iterate(iterator)
                    .build();

            vec.fit();

            Collection<String> search = vec.wordsNearestSum("search", 2);
            System.out.println(search);
        }
        catch (ParseException | IOException e)
        {
            e.printStackTrace();
        }

    }
}
