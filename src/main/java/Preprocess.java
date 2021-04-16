import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Preprocess {
    private static ArrayList<DocumentData> data = new ArrayList<>();
    private enum Status {TITLE, B, W, NAME, AUTHORS, KEYS, C, CITATION}

    public static ArrayList<DocumentData> tokenizer(String filename)
    {

        try
        {
            String line;
            int id = 0;
            String title, w, b, name;
            title = w = b = name = "-";
            ArrayList<String> authors, keys, c;
            authors = keys = c = new ArrayList<>();
            ArrayList<String[]> citation = new ArrayList<>();
            Status status = null;

            int counter = 0;
            BufferedReader br = new BufferedReader(new FileReader(new File(filename)));
            while((line = br.readLine()) != null)
            {
                counter++;
                String l2 = line.substring(0,2);
                switch (l2) {
                    case ".I":
                        if (counter != 1) {
                        /*System.out.println(String.join(" ",
                                String.valueOf(id), title, b, String.join("/",
                                        authors), name));*/
                            //for (String[] s: citation) System.out.println(s[0] + s[1] + s[2]);
                            data.add(new DocumentData(id, title, w, b, authors, keys, c, name, citation));
                        }
                        title = w = b = name = "-";
                        authors = new ArrayList<>();
                        citation = new ArrayList<>();
                        keys = new ArrayList<>();
                        c = new ArrayList<>();
                        id = Integer.parseInt(line.substring(line.indexOf(" ")+1));
                        break;

                    case ".T": status = Status.TITLE; break;
                    case ".W": status = Status.W; break;
                    case ".B": status = Status.B; break;
                    case ".A": status = Status.AUTHORS; break;
                    case ".K": status = Status.KEYS; break;
                    case ".C": status = Status.C; break;
                    case ".N": status = Status.NAME; break;
                    case ".X": status = Status.CITATION; break;
                    default:
                        switch (status) {
                            case TITLE: title = line; break;
                            case W: w = line; break;
                            case B: b = line; break;
                            case AUTHORS: authors.add(line); break;
                            case KEYS: keys.addAll(Arrays.asList(line.split(","))); break;
                            case C: c.addAll(Arrays.asList(line.split("\\s"))); break;
                            case NAME: name = line; break;
                            case CITATION: citation.add(line.split("\\s")); break;
                            default: break;
                        }
                        break;
                }
            }
            data.add(new DocumentData(id, title, w, b, authors, keys, c, name, citation));
            System.out.println(counter);
            return data;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
