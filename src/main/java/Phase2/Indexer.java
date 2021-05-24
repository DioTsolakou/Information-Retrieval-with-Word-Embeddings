package Phase2;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.classification.utils.DocToDoubleVectorUtils;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

public class Indexer
{
    public Indexer(String docFilename, String queryFilename)
    {
        String indexDocLocation = "index2doc";
        String indexQueryLocation = "index2query";
        try
        {
            Directory indexDoc = FSDirectory.open(Paths.get(indexDocLocation));
            Directory indexQuery = FSDirectory.open(Paths.get(indexQueryLocation));

            EnglishAnalyzer analyzer = new EnglishAnalyzer();
            Similarity similarity = new ClassicSimilarity();

            IndexWriterConfig iwcDoc = new IndexWriterConfig(analyzer);
            iwcDoc.setSimilarity(similarity);
            IndexWriterConfig iwcQuery = new IndexWriterConfig(analyzer);
            iwcQuery.setSimilarity(similarity);

            FieldType type = new FieldType();
            type.setIndexOptions(IndexOptions.DOCS_AND_FREQS);
            type.setTokenized(true);
            type.setStored(true);
            type.setStoreTermVectors(true);

            iwcDoc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            IndexWriter indexDocWriter = new IndexWriter(indexDoc, iwcDoc);
            ArrayList<DocumentData> docData = Preprocess.documentPreprocessor(docFilename);
            for (DocumentData d : docData) indexDoc(indexDocWriter, d, type);
            indexDocWriter.close();

            iwcQuery.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            IndexWriter indexQueryWriter = new IndexWriter(indexQuery, iwcQuery);
            ArrayList<QueryData> queryData = Preprocess.queryPreprocessor(queryFilename);
            for (QueryData q : queryData) indexQuery(indexQueryWriter, q, type);
            indexQueryWriter.close();

            IndexReader indexDocReader = DirectoryReader.open(indexDoc);
            testSparseFreqDoubleArrayConversion(indexDocReader, "docXterm.txt");

            IndexReader indexQueryReader = DirectoryReader.open(indexQuery);
            testSparseFreqDoubleArrayConversion(indexQueryReader, "queryXterm.txt");

            Process process = Runtime.getRuntime().exec("python LSI.py 50");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void indexDoc(IndexWriter indexWriter, DocumentData docData, FieldType type) throws IOException
    {
        // make a new, empty document
        Document doc = new Document();

        // create the fields of the document and add them to the document
        Field id = new Field("id", String.valueOf(docData.getId()), type);
        doc.add(id);

        Field title = new Field("title", docData.getTitle(), type);
        doc.add(title);

        Field w = new Field("w", docData.getW(), type);
        doc.add(w);

        Field b = new Field("b", docData.getB(), type);
        doc.add(b);

        Field authors = new Field("authors", String.join("/", docData.getAuthors()), type);
        doc.add(authors);

        Field keys = new Field("keys", String.join("/", docData.getKeys()), type);
        doc.add(keys);

        Field c = new Field("c", String.join("/", docData.getC()), type);
        doc.add(c);

        Field name = new Field("name", docData.getName(), type);
        doc.add(name);

        ArrayList<String> cit = new ArrayList<>();
        for (String[] s: docData.getCitation())
            cit.add(String.join(" ", s));

        String fullSearchableText =
                String.join(" ",
                        String.valueOf(docData.getId()), docData.getTitle(), docData.getW(), docData.getB(), String.join("/",
                                docData.getAuthors()), String.join("/", docData.getKeys()), String.join("/", docData.getC()),
                        docData.getName(), String.join("/", cit));

        Field contents = new Field("contents", fullSearchableText, type);
        doc.add(contents);

        if (indexWriter.getConfig().getOpenMode() == IndexWriterConfig.OpenMode.CREATE)
        {
            indexWriter.addDocument(doc);
        }
    }

    private void indexQuery(IndexWriter indexWriter, QueryData queryData, FieldType type) throws IOException
    {
        // make a new, empty document
        Document doc = new Document();

        // create the fields of the document and add them to the document
        Field id = new Field("id", String.valueOf(queryData.getId()), type);
        doc.add(id);

        Field words = new Field("words", queryData.getWords(), type);
        doc.add(words);

        Field name = new Field("name", queryData.getName(), type);
        doc.add(name);

        Field authors = new Field("authors", String.join("/", queryData.getAuthors()), type);
        doc.add(authors);

        String fullSearchableText = String.join(" ",
                        String.valueOf(queryData.getId()), queryData.getWords(), queryData.getName(),  String.join("/", queryData.getAuthors()));

        Field contents = new Field("contents", fullSearchableText, type);
        doc.add(contents);

        if (indexWriter.getConfig().getOpenMode() == IndexWriterConfig.OpenMode.CREATE)
        {
            indexWriter.addDocument(doc);
        }
    }

    private static void testSparseFreqDoubleArrayConversion(IndexReader reader, String filename) throws IOException
    {
        Terms fieldTerms = MultiFields.getTerms(reader, "contents");

        if (fieldTerms != null && fieldTerms.size() != -1)
        {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(filename)));
            IndexSearcher indexSearcher = new IndexSearcher(reader);
            for (ScoreDoc scoreDoc : indexSearcher.search(new MatchAllDocsQuery(), Integer.MAX_VALUE).scoreDocs)
            {
                String idType = filename.contains("doc") ? "DocID: " : "QueryID: ";
                System.out.println(idType + scoreDoc.doc);
                Terms docTerms = reader.getTermVector(scoreDoc.doc, "contents");
                //System.out.println(docTerms.toString());

                Double[] vector = DocToDoubleVectorUtils.toSparseLocalFreqDoubleArray(docTerms, fieldTerms);
                NumberFormat nf = new DecimalFormat("0.#");

                StringBuilder sb = new StringBuilder();
                for (int i = 0; i <= vector.length - 1; i++)
                {
                    //System.out.println(nf.format(vector[i]) + " ");
                    sb.append(nf.format(vector[i])).append(" ");
                }
                //System.out.println(sb.toString());
                bw.write(sb.toString() + "\n");
            }
            bw.close();
        }
    }
}