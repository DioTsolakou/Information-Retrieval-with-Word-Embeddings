package Phase1;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Preprocess {
    private static ArrayList<DocumentData> docData = new ArrayList<>();
    private static ArrayList<QueryData> queryData = new ArrayList<>();

    private enum DocStatus {TITLE, B, W, NAME, AUTHORS, KEYS, C, CITATION}
    private enum QueryStatus {W, N, A}

    public static ArrayList<DocumentData> documentPreprocessor(String filename)
    {
        try {
            String line;
            int id = 0;
            StringBuilder w, title;
            String b;
            String name;
            b = name = "-";
            title = new StringBuilder();
            w = new StringBuilder();
            ArrayList<String> authors, keys, c;
            authors = keys = c = new ArrayList<>();
            ArrayList<String[]> citation = new ArrayList<>();
            DocStatus docStatus = null;

            int counter = 0;
            BufferedReader br = new BufferedReader(new FileReader(new File(filename)));
            while ((line = br.readLine()) != null) {
                counter++;
                String l2 = line.substring(0, 2);
                switch (l2) {
                    case ".I":
                        if (counter != 1) {
                            docData.add(new DocumentData(id, title.toString(), w.toString(), b, authors, keys, c, name, citation));
                        }
                        b = name = "-";
                        title = new StringBuilder();
                        w = new StringBuilder();
                        authors = new ArrayList<>();
                        citation = new ArrayList<>();
                        keys = new ArrayList<>();
                        c = new ArrayList<>();
                        id = Integer.parseInt(line.substring(line.indexOf(" ") + 1));
                        break;

                    case ".T":
                        docStatus = DocStatus.TITLE;
                        break;
                    case ".W":
                        docStatus = DocStatus.W;
                        break;
                    case ".B":
                        docStatus = DocStatus.B;
                        break;
                    case ".A":
                        docStatus = DocStatus.AUTHORS;
                        break;
                    case ".K":
                        docStatus = DocStatus.KEYS;
                        break;
                    case ".C":
                        docStatus = DocStatus.C;
                        break;
                    case ".N":
                        docStatus = DocStatus.NAME;
                        break;
                    case ".X":
                        docStatus = DocStatus.CITATION;
                        break;
                    default:
                        switch (docStatus) {
                            case TITLE:
                                title.append(line).append(" ");
                                break;
                            case W:
                                w.append(line).append(" ");
                                break;
                            case B:
                                b = line;
                                break;
                            case AUTHORS:
                                authors.add(line);
                                break;
                            case KEYS:
                                keys.addAll(Arrays.asList(line.split(",")));
                                break;
                            case C:
                                c.addAll(Arrays.asList(line.split("\\s")));
                                break;
                            case NAME:
                                name = line;
                                break;
                            case CITATION:
                                citation.add(line.split("\\s"));
                                break;
                            default:
                                break;
                        }
                        break;
                }
            }
            docData.add(new DocumentData(id, title.toString(), w.toString(), b, authors, keys, c, name, citation));
            //System.out.println(counter);
            br.close();
            return docData;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ArrayList<QueryData> queryPreprocessor(String filename)
    {
        try {
            String line;
            int id = 0;
            StringBuilder words, name;
            name = new StringBuilder();
            words = new StringBuilder();
            ArrayList<String> authors = new ArrayList<>();
            QueryStatus queryStatus = null;

            int counter = 0;
            BufferedReader br = new BufferedReader(new FileReader(new File(filename)));
            while ((line = br.readLine()) != null)
            {
                counter++;
                if (line.length() <= 1) continue;
                String l2 = line.substring(0, 2);

                switch (l2)
                {
                    case ".I":
                        if (counter != 1) {
                            queryData.add(new QueryData(id, words.toString(), name.toString(), authors));
                        }
                        name = new StringBuilder();
                        words = new StringBuilder();
                        authors = new ArrayList<>();
                        id = Integer.parseInt(line.substring(line.indexOf(" ") + 1));
                        break;
                    case ".N":
                        queryStatus = QueryStatus.N;
                        break;
                    case ".W":
                        queryStatus = QueryStatus.W;
                        break;
                    case ".A":
                        queryStatus = QueryStatus.A;
                        break;
                    default:
                        switch (queryStatus)
                        {
                            case N:
                                name.append(line).append(" ");
                                break;
                            case W:
                                words.append(line).append(" ");
                                break;
                            case A:
                                authors.add(line);
                                break;
                            default:
                                break;
                        }
                        break;
                }
            }
            String w = words.toString().replace(".", "");
            w = w.replace(",", "");
            w = w.replace("\"", "");
            queryData.add(new QueryData(id, w, name.toString(), authors));
            //System.out.println(counter);
            br.close();
            return queryData;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }
}