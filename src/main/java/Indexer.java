import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Indexer
{
    public Indexer()
    {
        String filename = "..//CACM//cacm.all";
        String indexLocation = "index";
        try
        {
            System.out.println("Indexing to directory '" + indexLocation + "'...");

            Directory dir = FSDirectory.open(Paths.get(indexLocation));
            Analyzer analyzer = new EnglishAnalyzer();
            Similarity similarity = new ClassicSimilarity();
            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
            iwc.setSimilarity(similarity);

            iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            IndexWriter indexWriter = new IndexWriter(dir, iwc);
            ArrayList<DocumentData> data = Preprocess.tokenizer(filename);

            //if (data == null) throw new NullPointerException();
            for (DocumentData d : data)
            {
                indexDoc(indexWriter, d);
            }
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

            StoredField b = new StoredField("b", docData.getB());
            doc.add(b);

            StoredField authors = new StoredField("authors", String.join("/", docData.getAuthors()));
            doc.add(authors);

            StoredField name = new StoredField("name", docData.getName());
            doc.add(name);

            StoredField citation = new StoredField("citation", String.join("/", (CharSequence) docData.getCitation()));
            doc.add(citation);

            String fullSearchableText =
                    String.join(" ",
                            String.valueOf(docData.getId()), docData.getTitle(), docData.getB(), String.join("/",
                                    docData.getAuthors()), docData.getName(), String.join("/", (CharSequence) docData.getCitation()));

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
