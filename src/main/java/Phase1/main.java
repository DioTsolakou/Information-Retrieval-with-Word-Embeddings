package Phase1;

import java.util.Scanner;

public class main
{
    public static void main(String[] args)
    {
        Scanner in = new Scanner(System.in);
        System.out.println("Give similarity please");
        String similarity = in.nextLine();

        float LMJfloat;
        if (similarity.equalsIgnoreCase("lmj"))
        {
            System.out.println("Give LMJ float");
            LMJfloat = in.nextFloat();
        }
        else LMJfloat = 0.f;

        Indexer indexer = new Indexer("..//CACM//cacm.all", similarity, LMJfloat);
        System.out.println("------------------------------------");
        System.out.println("Starting 20:");
        System.out.println("------------------------------------");
        QueryParsing queryParsing20 = new QueryParsing("..//CACM//query.text", 20, similarity, LMJfloat);
        System.out.println("------------------------------------");
        System.out.println("Starting 30:");
        System.out.println("------------------------------------");
        QueryParsing queryParsing30 = new QueryParsing("..//CACM//query.text", 30, similarity, LMJfloat);
        System.out.println("------------------------------------");
        System.out.println("Starting 50:");
        System.out.println("------------------------------------");
        QueryParsing queryParsing50 = new QueryParsing("..//CACM//query.text", 50, similarity, LMJfloat);

        if (similarity.equalsIgnoreCase("lmj"))
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
        }






        Utilities.fixRels("..//trec_eval//qrels.txt");
    }
}
