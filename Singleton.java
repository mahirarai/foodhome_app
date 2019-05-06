package itp341.raihan.mahira.project.model;

import android.content.Context;

import java.util.ArrayList;

public class Singleton {

    private static Singleton singleton;
    private String email;
    private ArrayList<Note> savedNotes = new ArrayList<>();
    private ArrayList<Business> savedBusinesses = new ArrayList<>();
    private Context context;

    // singleton constructor
    private Singleton(Context context){
        this.context = context;
    }

    // singleton get method
    public static Singleton get(Context context){
        if(singleton == null){
            singleton = new Singleton(context);
        }
        return singleton;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ArrayList<Note> getSavedNotes(){
        return savedNotes;
    }

    public ArrayList<Business> getSavedBusinesses(){
        return savedBusinesses;
    }

    public int getNumSavedNotes(){
        return savedNotes.size();
    }

    public int getNumSavedBusinesses(){
        return savedBusinesses.size();
    }

    public void setSavedNotes(ArrayList<Note> notes){
        savedNotes = notes;
    }

    public void setSavedBusinesses(ArrayList<Business> businesses){
        savedBusinesses = businesses;
    }

    public Note getSavedNote(int position){
        if(position >= 0 && position < savedNotes.size()){
            return savedNotes.get(position);
        }
        else{
            return null;
        }
    }

    public Business getSavedBusiness(int position){
        if(position >= 0 && position < savedBusinesses.size()){
            return savedBusinesses.get(position);
        }
        else{
            return null;
        }
    }

    public void addSavedNote(Note n){
        savedNotes.add(n);
    }
    public void updatedSavedNote(int pos, Note n){
        savedNotes.set(pos, n);
    }

    public void addSavedBusiness(Business b){
        savedBusinesses.add(b);
    }
    public void updatedSavedBusiness(int pos, Business b){
        savedBusinesses.set(pos, b);
    }

    public void removeNote(int position)  {
        savedNotes.remove(position);
    }
    public void removeSearch(int position)  {
        savedBusinesses.remove(position);
    }


}
