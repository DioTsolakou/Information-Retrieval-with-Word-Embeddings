package Phase5;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;

public class Utilities
{
    public static void checkDuplicates(String filename)
    {
        try
        {
            String line;
            String[] array;
            ArrayList<String> faults = new ArrayList<>();
            String check;
            File oldFile = new File(filename);
            BufferedReader br = new BufferedReader(new FileReader(oldFile));
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(filename.substring(0, filename.lastIndexOf('.')) + "_fixed.txt")));
            HashSet set = new HashSet();

            while ((line = br.readLine()) != null)
            {
                array = line.split("\t");
                check = array[0] + " " +array[2];
                int length = set.size();
                set.add(check);
                if (set.size() == length) faults.add(check);
                else bw.write(line + "\n");
            }

            br.close();
            bw.close();
            if (oldFile.delete()) System.out.println("Old file deleted!");
            for (String s : faults) System.out.println(s);
            System.out.println("Size: " + faults.size() + " of file : " + filename);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void fixRels(String filename)
    {
        try
        {
            String line;
            String[] array;

            BufferedReader br = new BufferedReader(new FileReader(new File(filename)));
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File("qrels_fixed.txt")));

            while ((line = br.readLine()) != null)
            {
                //line = line.replace(" ", "\t");
                array = line.split("\\s");

                bw.write(array[0] + "\t" + "0" + "\t" + array[1] + "\t" + "1" + "\n");
            }
            br.close();
            bw.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
