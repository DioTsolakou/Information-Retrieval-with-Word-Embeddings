package Phase5;

import java.util.Locale;
import java.util.Scanner;

public class main
{
    public static void main(String[] args)
    {
        Scanner in = new Scanner(System.in);
        System.out.println("Give similarity please");
        String similarity;
        do {similarity = in.nextLine();}
        while (similarity.split(" ").length != 2 ||
                similarity.split(" ")[0].equals(similarity.split(" ")[1]));

        float LMJfloat;
        if (similarity.contains("lmj"))
        {
            System.out.println("Give LMJ float");
            LMJfloat = in.nextFloat();
        }
        else LMJfloat = 0.f;

        String w2v_algo = null;
        boolean pretrained_bool = false;
        if (similarity.contains("w2v")) {
            System.out.println("Give Word2Vec learning algorithm please\nYou can choose between Skipgram and CBOW");
            w2v_algo = in.nextLine();
            System.out.println("Do you want to use a pretrained model? 0 for no, 1 for yes");
            String pretrained = in.nextLine();
            pretrained_bool = pretrained.equals("1");
        }

        Indexer indexer = new Indexer("..//CACM//cacm.all", similarity, LMJfloat);
        System.out.println("------------------------------------");
        System.out.println("Starting 20:");
        System.out.println("------------------------------------");
        QueryParsing queryParsing20 = new QueryParsing("..//CACM//query.text", 20, similarity, LMJfloat, w2v_algo, pretrained_bool);
        /*System.out.println("------------------------------------");
        System.out.println("Starting 30:");
        System.out.println("------------------------------------");
        QueryParsing queryParsing30 = new QueryParsing("..//CACM//query.text", 30, similarity, LMJfloat, w2v_algo, pretrained_bool);
        System.out.println("------------------------------------");
        System.out.println("Starting 50:");
        System.out.println("------------------------------------");
        QueryParsing queryParsing50 = new QueryParsing("..//CACM//query.text", 50, similarity, LMJfloat, w2v_algo, pretrained_bool);*/

        /*if (similarity.equalsIgnoreCase("lmj"))
        {
            Utilities.checkDuplicates("..//Information-Retrieval-with-Word-Embeddings//LM_Jelinek-Mercer(" + LMJfloat +")_our_results_20.txt");
            Utilities.checkDuplicates("..//Information-Retrieval-with-Word-Embeddings//LM_Jelinek-Mercer(" + LMJfloat +")_our_results_30.txt");
            Utilities.checkDuplicates("..//Information-Retrieval-with-Word-Embeddings//LM_Jelinek-Mercer(" + LMJfloat +")_our_results_50.txt");
        }
        else if (similarity.equalsIgnoreCase("bm25"))
        {
            Utilities.checkDuplicates("..//Information-Retrieval-with-Word-Embeddings//BM25(k1=1.2,b=0.75)_our_results_20.txt");
            Utilities.checkDuplicates("..//Information-Retrieval-with-Word-Embeddings//BM25(k1=1.2,b=0.75)_our_results_30.txt");
            Utilities.checkDuplicates("..//Information-Retrieval-with-Word-Embeddings//BM25(k1=1.2,b=0.75)_our_results_50.txt");
        }
        else
        {
            Utilities.checkDuplicates("..//Information-Retrieval-with-Word-Embeddings//ClassicSimilarity_our_results_20.txt");
            Utilities.checkDuplicates("..//Information-Retrieval-with-Word-Embeddings//ClassicSimilarity_our_results_30.txt");
            Utilities.checkDuplicates("..//Information-Retrieval-with-Word-Embeddings//ClassicSimilarity_our_results_50.txt");
        }*/

        //Utilities.fixRels("..//trec_eval//qrels.txt");
    }
}
