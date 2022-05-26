package search_engine;

import java.util.HashMap;

public class Doc implements Comparable<Doc>{
    private String id;
    private String title;
    private String text;
    private HashMap<String, Integer> wordCount;

    public Doc(String id, String title, String text, HashMap<String, Integer> wordCount) {
        this.title = title;
        this.text = text;
        this.id = id;
        this.wordCount = wordCount;
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

    public HashMap<String, Integer> getWordCount() {
        return wordCount;
    }

    public void setWordCount(HashMap<String, Integer> wordCount) {
        this.wordCount = wordCount;
    }
}
