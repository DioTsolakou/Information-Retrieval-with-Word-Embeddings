import java.util.Scanner;

public class main
{
    public static void main(String[] args)
    {
        Scanner in = new Scanner(System.in);
        System.out.println("Give similarity please");
        String similarity = in.nextLine();

        Indexer indexer = new Indexer("..//CACM//cacm.all", similarity);
        System.out.println("------------------------------------");
        System.out.println("Starting 20:");
        System.out.println("------------------------------------");
        QueryParsing queryParsing20 = new QueryParsing("..//CACM//query.text", 20, similarity);
        System.out.println("------------------------------------");
        System.out.println("Starting 30:");
        System.out.println("------------------------------------");
        QueryParsing queryParsing30 = new QueryParsing("..//CACM//query.text", 30, similarity);
        System.out.println("------------------------------------");
        System.out.println("Starting 50:");
        System.out.println("------------------------------------");
        QueryParsing queryParsing50 = new QueryParsing("..//CACM//query.text", 50, similarity);

        //Utilities.checkDuplicates("..//Information-Retrieval-with-Word-Embeddings//ClassicSimilarity_our_results_20.txt");
        //Utilities.checkDuplicates("..//Information-Retrieval-with-Word-Embeddings//ClassicSimilarity_our_results_30.txt");
        //Utilities.checkDuplicates("..//Information-Retrieval-with-Word-Embeddings//ClassicSimilarity_our_results_50.txt");
        Utilities.fixRels("..//trec_eval//qrels.txt");
    }
}
