import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Preprocess {
    ArrayList<DocumentData> data;

    private void tokenizer(String filename)
    {
        try
        {
            String line;
            int id = 0;
            String title, b, name;
            title = b = name = null;
            ArrayList<String> authors = null;
            List<String[]> citation = null;

            int counter = 0;
            BufferedReader br = new BufferedReader(new FileReader(new File(filename)));

            while((line = br.readLine()) != null)
            {
                if (line.startsWith(".I"))
                {
                    id = Integer.parseInt(line.substring(line.indexOf(" ")+1));
                    line = br.readLine();
                    if (line.startsWith(".T"))
                    {
                        line = br.readLine();
                        title = line;
                    }
                    if (line.startsWith(".B"))
                    {
                        line = br.readLine();
                        b = line;
                    }
                    if (line.startsWith(".A"))
                    {
                        authors = new ArrayList<String>();
                        while(!(line = br.readLine()).startsWith("."))
                        {
                            authors.add(line);
                        }
                    }
                    if (line.startsWith(".N"))
                    {
                        line = br.readLine();
                        name = line;
                    }
                    if (line.startsWith(".X"))
                    {
                        citation = new ArrayList<String[]>();
                        while(!(line = br.readLine()).startsWith("."))
                        {
                            citation.add(line.split("\\s"));
                        }
                    }
                }
                data.add(new DocumentData(id, title, b, authors, name, citation));
            }
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
