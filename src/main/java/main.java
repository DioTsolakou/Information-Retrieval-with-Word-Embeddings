import java.util.Scanner;

public class main
{
    public static void main(String args[])
    {
        /*Scanner in = new Scanner(System.in);
        System.out.println("Gib filenam plox");
        String filename = in.nextLine();*/

        Indexer indexer = new Indexer("..//CACM//cacm.all");
        System.out.println("------------------------------------");
        System.out.println("Starting 20:");
        System.out.println("------------------------------------");
        QueryParsing queryParsing20 = new QueryParsing("..//CACM//query.text", 20);
        System.out.println("------------------------------------");
        System.out.println("Starting 30:");
        System.out.println("------------------------------------");
        QueryParsing queryParsing30 = new QueryParsing("..//CACM//query.text", 30);
        System.out.println("------------------------------------");
        System.out.println("Starting 50:");
        System.out.println("------------------------------------");
        QueryParsing queryParsing50 = new QueryParsing("..//CACM//query.text", 50);
    }
}
