package itp341.raihan.mahira.project;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import itp341.raihan.mahira.project.model.Business;
import itp341.raihan.mahira.project.model.Note;
import itp341.raihan.mahira.project.model.Singleton;
import itp341.raihan.mahira.project.model.User;

public class DetailNoteActivity extends AppCompatActivity {

    private static final String TAG = DetailNoteActivity.class.getSimpleName();
    public static final String EXTRA_URL = "itp341.raihan.mahira.project.EXTRA_URL";

    private TextView textNoteTitle;
    private TextView textNoteAuthor;
    private TextView textNoteTag;
    private TextView textNoteText;
    private ListView listComments;
    private EditText editComment;
    private Button buttonComment;
    private ImageButton buttonSaveNote;

    // database references
    DatabaseReference dbRefNoteCount;
    DatabaseReference dbRefNotes;
    DatabaseReference dbRefNoteToEdit; // to edit existing note
    DatabaseReference dbRefUsers;
    DatabaseReference dbRefUserToEdit; // if editing existing note

    private Note note;
    private ArrayList<String> comments = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    ArrayList<Note> savedNotesList;
    private String cleanEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_note);
        Log.d(TAG, "onCreate");

        // get database
        FirebaseDatabase firebase = FirebaseDatabase.getInstance();

        // get database reference paths
        dbRefNotes = firebase.getReference(FirebaseRefs.NOTE);
        dbRefNoteCount = firebase.getReference(FirebaseRefs.NOTES_COUNT);

        textNoteTitle = findViewById(R.id.detail_note_title);
        textNoteAuthor = findViewById(R.id.detail_note_author);
        textNoteTag = findViewById(R.id.detail_note_tag);
        textNoteText = findViewById(R.id.detail_note_text);
        listComments = findViewById(R.id.list_comments);
        editComment = findViewById(R.id.edit_comment);
        buttonComment = findViewById(R.id.button_comment);
        buttonSaveNote = findViewById(R.id.button_save_note);

        savedNotesList = Singleton.get(this).getSavedNotes();

        readDatabase();

        Intent i = getIntent();
        String stringReference = i.getStringExtra(EXTRA_URL);

        if(stringReference != null){
            Log.d(TAG, "stringReference != null");
            dbRefNoteToEdit = FirebaseDatabase.getInstance().getReferenceFromUrl(stringReference);
        }

        if(dbRefNoteToEdit != null){
            Log.d(TAG, "dbRefNoteToEdit != null");
            dbRefNoteToEdit.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    note = dataSnapshot.getValue(Note.class);

                    textNoteTitle.setText(note.getTitle());
                    textNoteAuthor.setText(note.getAuthor());
                    textNoteTag.setText(note.getTag());
                    textNoteText.setText(note.getText());
                    comments = note.getComments();
                    adapter = new ArrayAdapter<String>(getApplicationContext(),
                            android.R.layout.simple_list_item_1, comments);
                    listComments.setAdapter(adapter);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d(TAG, "Error reading from database");
                }
            });
        }

        // add comment to note
        buttonComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "buttonComment pressed");

                // add comment to comments list
                String comment = editComment.getText().toString();
                add(comment);
                editComment.getText().clear();

                Note n = new Note();
                n.setTitle(note.getTitle());
                n.setAuthor(note.getAuthor());
                n.setTag(note.getTag());
                n.setText(note.getText());
                n.setComments(comments);

                if(dbRefNoteToEdit == null){
                    DatabaseReference dbRefNewNote = dbRefNotes.push();
                    dbRefNewNote.setValue(n);
                }
                else{
                    dbRefNoteToEdit.setValue(n);
                }

            }
        });

        // save note to saved list
        buttonSaveNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "buttonSaveNote pressed");

                // add to database
                dbRefUsers.child(cleanEmail).child("notes").push().setValue(note);

                // add note to savedNotes list
                Singleton.get(getApplicationContext()).addSavedNote(note);

                // toast note was added
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.message_save_note),
                        Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void add(String comment) {
        Log.d(TAG, "add comment: " + comment);
        comments.add(comment);
    }

    private void readDatabase() {
        // get database
        FirebaseDatabase firebase = FirebaseDatabase.getInstance();
        dbRefUsers = firebase.getReference(FirebaseRefs.USER);

        Intent i = getIntent();
        String stringReference = i.getStringExtra(EXTRA_URL);

        if(stringReference != null){
            dbRefUserToEdit = FirebaseDatabase.getInstance().getReferenceFromUrl(stringReference);
        }

        if(dbRefUserToEdit != null){
            dbRefUserToEdit.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User u = dataSnapshot.getValue(User.class);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d(TAG, "Error reading from database");
                }
            });
        }

        String email = Singleton.get(this).getEmail();
        cleanEmail = email.replace(".", ",");
        if(dbRefUserToEdit == null){
            dbRefUsers.child(cleanEmail).setValue(cleanEmail);

        }

        //else {
        //    dbRefUserToEdit.setValue(businesses);
        //}
    }


}
