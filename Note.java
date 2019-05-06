package itp341.raihan.mahira.project.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Note implements Serializable {

    // Instance variables
    private String title;
    private String author;
    private String tag;
    private String text;
    private ArrayList<String> comments = new ArrayList<>();

    // Constructors
    public Note(){
        super();
    }

    public Note(String title, String author, String tag, String text, ArrayList<String> comments) {
        this.title = title;
        this.author = author;
        this.tag = tag;
        this.text = text;
        this.comments = comments;
    }

    // toString
    @Override
    public String toString() {
        return title + "\n" + author + "\n" + text;
    }

    // Getters and Setters
    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public ArrayList<String> getComments() {
        return comments;
    }

    public void setComments(ArrayList<String> comments) {
        this.comments = comments;
    }

    public void addComment(String comment){
        comments.add(comment);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
