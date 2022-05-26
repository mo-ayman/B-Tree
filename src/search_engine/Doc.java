package search_engine;

public class Doc implements Comparable<Doc>{
    private String id;
    private String title;
    private String text;

    public Doc(String id, String title, String text) {
        this.title = title;
        this.text = text;
        this.id = id;
    }

    @Override
    public int compareTo(Doc o) {
        return this.id.compareTo(o.id);
    }

    @Override
    public String toString(){
        return this.id + " " + this.title + " " + this.text;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
