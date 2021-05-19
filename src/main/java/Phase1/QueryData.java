package Phase1;

import java.util.ArrayList;

public class QueryData
{
    int id;
    String words, name;
    ArrayList<String> authors;

    public QueryData(int id, String words, String name, ArrayList<String> authors)
    {
        this.id = id;
        this.words = words;
        this.name = name;
        this.authors = authors;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWords() {
        return words;
    }

    public void setWords(String words) {
        this.words = words;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getAuthors() {
        return authors;
    }

    public void setAuthors(ArrayList<String> authors) {
        this.authors = authors;
    }
}