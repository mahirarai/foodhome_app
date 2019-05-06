package itp341.raihan.mahira.project.model;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {

    private String email;
    private ArrayList<Note> notes;
    private ArrayList<Business> businesses;

    public User(){
        super();
    }

    public User(String email) {
        this.email = email;
        this.notes = new ArrayList<>();
        this.businesses = new ArrayList<>();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ArrayList<Note> getNotes() {
        return notes;
    }

    public void setNotes(ArrayList<Note> notes) {
        this.notes = notes;
    }

    public ArrayList<Business> getBusinesses() {
        return businesses;
    }

    public void setBusinesses(ArrayList<Business> businesses) {
        this.businesses = businesses;
    }

    public void addBusiness(Business b){
        businesses.add(b);
    }

    public void addNote(Note n){
        notes.add(n);
    }


}
