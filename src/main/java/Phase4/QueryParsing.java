package Phase4;

import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;
import org.deeplearning4j.models.embeddings.learning.impl.elements.CBOW;
import org.deeplearning4j.models.embeddings.learning.impl.elements.SkipGram;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class QueryParsing
{
    public QueryParsing(String filename, int topK, String w2v_algo, boolean pretrained)
    {
        String indexLocation = "index";
        String field = "contents";

        try {
            IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexLocation)));
            IndexSearcher searcher = new IndexSearcher(reader);
            ArrayList<QueryData> data = Preprocess.queryPreprocessor(filename);

            FieldValuesSentenceIterator iterator = new FieldValuesSentenceIterator(reader, field);

            Word2Vec vec;
            if (!pretrained)
            {
                vec = new Word2Vec.Builder()
                        .layerSize(100)
                        .windowSize(6)
                        .tokenizerFactory(new DefaultTokenizerFactory())
                        .iterate(iterator)
                        .elementsLearningAlgorithm(w2v_algo.equalsIgnoreCase("cbow") ? new CBOW<>() : new SkipGram<>())
                        .build();

                vec.fit();
            }
            else
                vec = WordVectorSerializer.readWord2VecModel("..//model//model.txt");

            luceneSearch(data, searcher, reader, field, topK, vec);

            reader.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void luceneSearch(ArrayList<QueryData> data, IndexSearcher indexSearcher, IndexReader reader, String field, int topK, Word2Vec vec)
    {
        try
        {
            indexSearcher.setSimilarity(new WordEmbeddingsSimilarity(vec, field, WordEmbeddingsSimilarity.Smoothing.MEAN));

            QueryParser queryParser = new QueryParser(field, new WhitespaceAnalyzer());
            queryParser.setAllowLeadingWildcard(true);

            File resultsFile = new File( "phase4_our_results_" + topK +".txt");
            if (resultsFile.exists()) {
                resultsFile.delete();
                resultsFile.createNewFile();
            }

            for (QueryData q : data)
            {
                Query query = queryParser.parse(QueryParser.escape(q.getWords()));
                ArrayList<String> words = new ArrayList<>(Arrays.asList(query.toString().split("\\s")));
                StringBuilder fixedQueryTerms = new StringBuilder();

                for (String word : words)
                {
                    //if (word.length() == 0) continue;
                    word = word.replace("contents:", "");
                    if (!vec.hasWord(word)) words.remove(word);
                    fixedQueryTerms.append(word).append(" ");
                }

                q.setWords(fixedQueryTerms.toString());
                query = queryParser.parse(QueryParser.escape(q.getWords()));

                TopDocs results = indexSearcher.search(query, topK);
                ScoreDoc[] hits = results.scoreDocs;

                /*for (ScoreDoc hit : hits)
                {
                    Document hitDoc = indexSearcher.doc(hit.doc);
                    System.out.println("\t["+hit.score+"] \tID: " + hitDoc.get("id") + " \tTITLE: " + hitDoc.get("title") + " \tW: " + hitDoc.get("w") + " \tB: " + hitDoc.get("b") +
                            " \tAUTHORS: " + hitDoc.get("authors") + " \tKEYS: " + hitDoc.get("keys") + " \tC: " + hitDoc.get("c") + " \tNAME: " + hitDoc.get("name"));
                }*/

                ArrayList<ScoreDoc> scoreDocs = new ArrayList<ScoreDoc>(Arrays.asList(hits));
                for (ScoreDoc sd : scoreDocs)
                {
                    if (Float.isNaN(sd.score)) sd.score = (float) 0;
                }

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

                for (ScoreDoc sd : scoreDocs) {
                    Document hitDoc = indexSearcher.doc(sd.doc);
                    StringBuilder docId = new StringBuilder(hitDoc.get("id"));

                    while (docId.length() < 4)
                    {
                        docId.insert(0, "0");
                    }
                    bw.append(queryId + "\t0" + "\t" + docId + "\t0" + "\t" + sd.score + "\tstandard_run_id\n");
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
