package Phase2;

import Phase1.DocumentData;
import Phase1.Preprocess;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import org.apache.lucene.classification.utils.DocToDoubleVectorUtils;

public class TermDocMatrix
{
    public void createMatrix()
    {
        try
        {
            EnglishAnalyzer analyzer = new EnglishAnalyzer();
            Similarity similarity = new ClassicSimilarity();

            Directory index = new RAMDirectory();

            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
            iwc.setSimilarity(similarity);

            FieldType type = new FieldType();
            type.setIndexOptions(IndexOptions.DOCS_AND_FREQS);
            type.setTokenized(true);
            type.setStored(true);
            type.setStoreTermVectors(true);
            IndexWriter indexWriter = new IndexWriter(index, iwc);

            //ArrayList<Phase1.DocumentData> data = Preprocess.documentPreprocessor(filename);

            /*for (DocumentData d : data)
            {
                addDocWithTermVector(indexWriter, d, type);
            }*/

            indexWriter.close();

            IndexReader indexReader = DirectoryReader.open(index);
            testSparseFreqDoubleArrayConversion(indexReader);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void addDocWithTermVector(IndexWriter writer, String value, FieldType type) throws IOException
    {
        Document doc = new Document();
        //TextField title = new TextField("title", value, Field.Store.YES);
        Field field = new Field("title", value, type);
        doc.add(field);
        writer.addDocument(doc);
    }

    private static void testSparseFreqDoubleArrayConversion(IndexReader reader) throws IOException
    {
        Terms fieldTerms = MultiFields.getTerms(reader, "title");

        TermsEnum it = fieldTerms.iterator();
        while (it.next() != null)
        {
            System.out.print(it.term().utf8ToString() + " ");
        }
        if (fieldTerms != null && fieldTerms.size() != -1)
        {
            IndexSearcher indexSearcher = new IndexSearcher(reader);
            for (ScoreDoc scoreDoc : indexSearcher.search(new MatchAllDocsQuery(), Integer.MAX_VALUE).scoreDocs)
            {
                System.out.println("DocID: " + scoreDoc.doc);
                Terms docTerms = reader.getTermVector(scoreDoc.doc, "title");
                Double[] vector = DocToDoubleVectorUtils.toSparseLocalFreqDoubleArray(docTerms, fieldTerms);
                NumberFormat nf = new DecimalFormat("0.#");
                for (int i = 0; i <= vector.length - 1; i++)
                {
                    System.out.println(nf.format(vector[i]) + " ");
                }
            }
        }
    }
}
