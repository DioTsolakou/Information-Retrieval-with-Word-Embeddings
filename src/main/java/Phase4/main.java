package Phase4;

import Phase1.Utilities;

import java.util.Scanner;

public class main
{
    public static void main(String[] args)
    {
        Scanner in = new Scanner(System.in);
        System.out.println("Give Word2Vec learning algorithm please\nYou can choose between Skipgram and CBOW");
        String w2v_algo = in.nextLine();
        System.out.println("Do you want to use a pretrained model? 0 for no, 1 for yes");
        String pretrained = in.nextLine();
        boolean pretrained_bool = pretrained.equals("1");
        Indexer indexer = new Indexer("..//CACM//cacm.all");
        System.out.println("------------------------------------");
        System.out.println("Starting 20:");
        System.out.println("------------------------------------");
        QueryParsing queryParsing20 = new QueryParsing("..//CACM//query.text", 20, w2v_algo, pretrained_bool);
        System.out.println("------------------------------------");
        System.out.println("Starting 30:");
        System.out.println("------------------------------------");
        QueryParsing queryParsing30 = new QueryParsing("..//CACM//query.text", 30, w2v_algo, pretrained_bool);
        System.out.println("------------------------------------");
        System.out.println("Starting 50:");
        System.out.println("------------------------------------");
        QueryParsing queryParsing50 = new QueryParsing("..//CACM//query.text", 50, w2v_algo, pretrained_bool);

        //Utilities.checkDuplicates("..//Information-Retrieval-with-Word-Embeddings//phase4_our_results_20.txt");
        Utilities.checkDuplicates("..//Information-Retrieval-with-Word-Embeddings//phase4_our_results_30.txt");
        Utilities.checkDuplicates("..//Information-Retrieval-with-Word-Embeddings//phase4_our_results_50.txt");
    }
}