package Phase5;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.similarities.*;
import org.apache.lucene.store.FSDirectory;
import org.deeplearning4j.models.embeddings.learning.impl.elements.CBOW;
import org.deeplearning4j.models.embeddings.learning.impl.elements.SkipGram;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class main {
    public static void main(String[] args) {

        Indexer indexer = new Indexer("..//CACM//cacm.all");
        IndexReader indexReader = null;
        try {indexReader = DirectoryReader.open(FSDirectory.open(Paths.get("index")));}
        catch (IOException e) {e.printStackTrace();}

        Scanner in = new Scanner(System.in);
        System.out.println("Give similarity please");
        String similarity;
        do {similarity = in.nextLine();}
        while (similarity.split(" ").length != 2 ||
                similarity.split(" ")[0].equals(similarity.split(" ")[1]));

        if (similarity.contains("lmj")) System.out.println("Give LMJ float");
        float LMJfloat = similarity.contains("lmj") ? in.nextFloat() : 0.f;

        Word2Vec vec = null;
        String w2v_algo = null;
        boolean pretrained = false;
        if (similarity.contains("w2v")) {
            System.out.println("Give Word2Vec learning algorithm please\nYou can choose between Skipgram and CBOW");
            w2v_algo = in.nextLine();
            System.out.println("Do you want to use a pretrained model? 0 for no, 1 for yes");
            pretrained = in.nextLine().equals("1");
        }

        ArrayList<Similarity> similaritiesList = new ArrayList<>();
        if (similarity.contains("lmj"))
            similaritiesList.add(new LMJelinekMercerSimilarity(LMJfloat));
        if (similarity.contains("bm25"))
            similaritiesList.add(new BM25Similarity());
        if (similarity.contains("classic"))
            similaritiesList.add(new ClassicSimilarity());
        if (similarity.contains("w2v")) {
            FieldValuesSentenceIterator iterator = new FieldValuesSentenceIterator(indexReader, "contents");
            if (!pretrained) {
                vec = new Word2Vec.Builder()
                        .layerSize(100)
                        .windowSize(6)
                        .tokenizerFactory(new DefaultTokenizerFactory())
                        .iterate(iterator)
                        .elementsLearningAlgorithm(w2v_algo.equalsIgnoreCase("cbow") ? new CBOW<>() : new SkipGram<>())
                        .build();
                vec.fit();
            } else vec = WordVectorSerializer.readWord2VecModel("..//model//model.txt");
            similaritiesList.add(new WordEmbeddingsSimilarity(vec, "contents", WordEmbeddingsSimilarity.Smoothing.MEAN));
        }

        Similarity[] similaritiesArray = new Similarity[2];
        similaritiesArray = similaritiesList.toArray(similaritiesArray);
        MultiSimilarity multiSimilarity = new MultiSimilarity(similaritiesArray);

        QueryParsing queryParsing20 = new QueryParsing("..//CACM//query.text", 20, multiSimilarity, vec, similarity.replace(' ', '_'));
        QueryParsing queryParsing30 = new QueryParsing("..//CACM//query.text", 30, multiSimilarity, vec, similarity.replace(' ', '_'));
        QueryParsing queryParsing50 = new QueryParsing("..//CACM//query.text", 50, multiSimilarity, vec, similarity.replace(' ', '_'));
        Utilities.checkDuplicates("phase5_our_results_" + similarity.replace(' ', '_') + "_20.txt");
        Utilities.checkDuplicates("phase5_our_results_" + similarity.replace(' ', '_') + "_30.txt");
        Utilities.checkDuplicates("phase5_our_results_" + similarity.replace(' ', '_') + "_50.txt");

        //Utilities.fixRels("..//trec_eval//qrels.txt");
    }
}