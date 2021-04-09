import java.util.ArrayList;
import java.util.List;

public class DocumentData {
    int id;
    String title, b, name;
    ArrayList<String> authors;
    List<String[]> citation;

    public DocumentData(int id, String title, String b, ArrayList<String> authors, String name, List<String[]> citation) {
        this.id = id;
        this.title = title;
        this.b = b;
        this.authors = authors;
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

    public List<String[]> getCitation() {
        return citation;
    }

    public void setCitation(List<String[]> citation) {
        this.citation = citation;
    }
}
