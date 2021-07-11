package Phase5;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexOptions;
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
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            IndexWriter writer = new IndexWriter(index, config);

            FieldType ft = new FieldType(TextField.TYPE_STORED);
            ft.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
            ft.setTokenized(true);
            ft.setStored(true);
            ft.setStoreTermVectors(true);
            ft.setStoreTermVectorOffsets(true);
            ft.setStoreTermVectorPositions(true);

            ArrayList<DocumentData> data = Preprocess.documentPreprocessor(filename);

            for (DocumentData d : data)
            {
                indexDoc(writer, d, ft);
            }

            writer.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void indexDoc(IndexWriter indexWriter, DocumentData docData, FieldType fieldType) throws IOException
    {
        // make a new, empty document
        Document doc = new Document();

        // create the fields of the document and add them to the document
        Field id = new Field("id", String.valueOf(docData.getId()), fieldType);
        doc.add(id);

        Field title = new Field("title", docData.getTitle(), fieldType);
        doc.add(title);

        Field w = new Field("w", docData.getW(), fieldType);
        doc.add(w);

        Field b = new Field("b", docData.getB(), fieldType);
        doc.add(b);

        Field authors = new Field("authors", String.join("/", docData.getAuthors()), fieldType);
        doc.add(authors);

        Field keys = new Field("keys", String.join("/", docData.getKeys()), fieldType);
        doc.add(keys);

        Field c = new Field("c", String.join("/", docData.getC()), fieldType);
        doc.add(c);

        Field name = new Field("name", docData.getName(), fieldType);
        doc.add(name);

        ArrayList<String> cit = new ArrayList<>();
        for (String[] s: docData.getCitation())
            cit.add(String.join(" ", s));

        String fullSearchableText =
                String.join(" ",
                        String.valueOf(docData.getId()), docData.getTitle(), docData.getW(), docData.getB(), String.join("/",
                                docData.getAuthors()), String.join("/", docData.getKeys()), String.join("/", docData.getC()),
                        docData.getName(), String.join("/", cit));

        Field contents = new Field("contents", fullSearchableText, fieldType);
        doc.add(contents);

        if (indexWriter.getConfig().getOpenMode() == IndexWriterConfig.OpenMode.CREATE)
        {
            indexWriter.addDocument(doc);
        }
    }
}
