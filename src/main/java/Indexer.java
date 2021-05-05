import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.LMJelinekMercerSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Indexer
{
    public Indexer(String filename, String similarityName)
    {
        String indexLocation = "index";
        try
        {
            System.out.println("Indexing to directory '" + indexLocation + "'...");

            Directory dir = FSDirectory.open(Paths.get(indexLocation));
            Analyzer analyzer = new EnglishAnalyzer();
            Similarity similarity;
            if (similarityName.equalsIgnoreCase("lmj"))
            {
                similarity = new LMJelinekMercerSimilarity((float)0.3);
            }
            else if (similarityName.equalsIgnoreCase("bm25"))
            {
                similarity = new BM25Similarity();
            }
            else
            {
                similarity = new ClassicSimilarity();
            }
            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
            iwc.setSimilarity(similarity);

            iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            IndexWriter indexWriter = new IndexWriter(dir, iwc);
            ArrayList<DocumentData> data = Preprocess.documentPreprocessor(filename);

            //if (data == null) throw new NullPointerException();
            for (DocumentData d : data)
            {
                indexDoc(indexWriter, d);
            }

            indexWriter.close();
        }
        catch (IOException | NullPointerException e)
        {
            e.printStackTrace();
        }
    }

    private void indexDoc(IndexWriter indexWriter, DocumentData docData)
    {
        try {
            // make a new, empty document
            Document doc = new Document();

            // create the fields of the document and add them to the document
            StoredField id = new StoredField("id", docData.getId());
            doc.add(id);

            StoredField title = new StoredField("title", docData.getTitle());
            doc.add(title);

            StoredField w = new StoredField("w", docData.getW());
            doc.add(w);

            StoredField b = new StoredField("b", docData.getB());
            doc.add(b);

            StoredField authors = new StoredField("authors", String.join("/", docData.getAuthors()));
            doc.add(authors);

            StoredField keys = new StoredField("keys", String.join("/", docData.getKeys()));
            doc.add(keys);

            StoredField c = new StoredField("c", String.join("/", docData.getC()));
            doc.add(c);

            StoredField name = new StoredField("name", docData.getName());
            doc.add(name);

            ArrayList<String> cit = new ArrayList<>();
            for (String[] s: docData.getCitation())
                cit.add(String.join(" ", s));

            StoredField citation = new StoredField("citation", String.join("/", cit));
            doc.add(citation);

            String fullSearchableText =
                    String.join(" ",
                            String.valueOf(docData.getId()), docData.getTitle(), docData.getW(), docData.getB(), String.join("/",
                                    docData.getAuthors()), String.join("/", docData.getKeys()), String.join("/", docData.getC()),
                                        docData.getName(), String.join("/", cit));

            TextField contents = new TextField("contents", fullSearchableText, Field.Store.NO);
            doc.add(contents);

            if (indexWriter.getConfig().getOpenMode() == IndexWriterConfig.OpenMode.CREATE) {
                // New index, so we just add the document (no old document can be there):
                System.out.println("adding " + docData);
                indexWriter.addDocument(doc);
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}