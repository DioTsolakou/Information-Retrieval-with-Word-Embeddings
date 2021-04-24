import java.util.ArrayList;
import java.util.List;

public class DocumentData {
    int id;
    String title, w, b, name;
    ArrayList<String> authors, keys, c;
    ArrayList<String[]> citation;

    public DocumentData(int id, String title, String w, String b, ArrayList<String> authors, ArrayList<String> keys, ArrayList<String> c, String name, ArrayList<String[]> citation) {
        this.id = id;
        this.title = title;
        this.w = w;
        this.b = b;
        this.authors = authors;
        this.keys = keys;
        this.c = c;
        this.name = name;
        this.citation = citation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getW() {
        return w;
    }

    public void setW(String w) {
        this.w = w;
    }

    public String getB() {
        return b;
    }

    public void setB(String b) {
        this.b = b;
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

    public ArrayList<String> getKeys() {
        return keys;
    }

    public void setKeys(ArrayList<String> keys) {
        this.keys = keys;
    }

    public ArrayList<String> getC() {
        return c;
    }

    public void setC(ArrayList<String> c) {
        this.c = c;
    }

    public List<String[]> getCitation() {
        return citation;
    }

    public void setCitation(ArrayList<String[]> citation) {
        this.citation = citation;
    }
}
