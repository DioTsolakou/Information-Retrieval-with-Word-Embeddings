package Phase4;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Indexer
{
    public Indexer(String filename)
    {
        String indexLocation = "index";
        try
        {
            Analyzer defaultAnalyzer = new EnglishAnalyzer();
            CharArraySet stopWords = new CharArraySet(Arrays.asList("a", "an", "the"), true);

            Directory index = FSDirectory.open(Paths.get(indexLocation));
            Map<String, Analyzer> perFieldAnalyzers = new HashMap<>();

            perFieldAnalyzers.put("id", new KeywordAnalyzer());
            perFieldAnalyzers.put("title", new WhitespaceAnalyzer());
            perFieldAnalyzers.put("w", new StopAnalyzer(stopWords));
            perFieldAnalyzers.put("b", new WhitespaceAnalyzer());
            perFieldAnalyzers.put("authors", new WhitespaceAnalyzer());
            perFieldAnalyzers.put("keys", new WhitespaceAnalyzer());
            perFieldAnalyzers.put("c", new KeywordAnalyzer());
            perFieldAnalyzers.put("name", new StopAnalyzer(stopWords));
            perFieldAnalyzers.put("contents", new StopAnalyzer(stopWords));

            Analyzer analyzer = new PerFieldAnalyzerWrapper(defaultAnalyzer, perFieldAnalyzers);
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            IndexWriter writer = new IndexWriter(index, config);

            ArrayList<DocumentData> data = Preprocess.documentPreprocessor(filename);

            for (DocumentData d : data)
            {
                indexDoc(writer, d);
            }

            writer.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void indexDoc(IndexWriter indexWriter, DocumentData docData) throws IOException
    {
        // make a new, empty document
        Document doc = new Document();

        // create the fields of the document and add them to the document
        TextField id = new TextField("id", String.valueOf(docData.getId()), Field.Store.YES);
        doc.add(id);

        TextField title = new TextField("title", docData.getTitle(), Field.Store.YES);
        doc.add(title);

        TextField w = new TextField("w", docData.getW(), Field.Store.YES);
        doc.add(w);

        TextField b = new TextField("b", docData.getB(), Field.Store.YES);
        doc.add(b);

        TextField authors = new TextField("authors", String.join("/", docData.getAuthors()), Field.Store.YES);
        doc.add(authors);

        TextField keys = new TextField("keys", String.join("/", docData.getKeys()), Field.Store.YES);
        doc.add(keys);

        TextField c = new TextField("c", String.join("/", docData.getC()), Field.Store.YES);
        doc.add(c);

        TextField name = new TextField("name", docData.getName(), Field.Store.YES);
        doc.add(name);

        ArrayList<String> cit = new ArrayList<>();
        for (String[] s: docData.getCitation())
            cit.add(String.join(" ", s));

        String fullSearchableText =
                String.join(" ",
                        String.valueOf(docData.getId()), docData.getTitle(), docData.getW(), docData.getB(), String.join("/",
                                docData.getAuthors()), String.join("/", docData.getKeys()), String.join("/", docData.getC()),
                        docData.getName(), String.join("/", cit));

        TextField contents = new TextField("contents", fullSearchableText, Field.Store.YES);
        doc.add(contents);

        if (indexWriter.getConfig().getOpenMode() == IndexWriterConfig.OpenMode.CREATE)
        {
            indexWriter.addDocument(doc);
        }
    }
}
