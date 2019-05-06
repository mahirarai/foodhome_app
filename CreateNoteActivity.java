package itp341.raihan.mahira.project;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import itp341.raihan.mahira.project.model.Note;

public class CreateNoteActivity extends AppCompatActivity {

    private static final String TAG = CreateNoteActivity.class.getSimpleName();
    public static final String EXTRA_URL = "itp341.raihan.mahira.project.EXTRA_URL";

    private EditText editTitle;
    private EditText editAuthor;
    private Spinner spinTag;
    private EditText editText;
    private Button buttonPublish;

    private Note note;

    DatabaseReference dbRefNoteCount;
    DatabaseReference dbRefNotes;
    DatabaseReference dbRefNoteToEdit; // if editing existing note

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        editTitle = findViewById(R.id.edit_title);
        editAuthor = findViewById(R.id.edit_name);
        spinTag = findViewById(R.id.spin_tag);
        editText = findViewById(R.id.edit_text);
        buttonPublish = findViewById(R.id.button_publish);

        // get database
        FirebaseDatabase firebase = FirebaseDatabase.getInstance();

        // get database reference paths
        dbRefNotes = firebase.getReference(FirebaseRefs.NOTE);
        dbRefNoteCount = firebase.getReference(FirebaseRefs.NOTES_COUNT);

        // read selected node
        Intent i = getIntent();
        String stringReference = i.getStringExtra(EXTRA_URL);

        if(stringReference != null){
            // convert string URL to database Location
            dbRefNoteToEdit = FirebaseDatabase.getInstance().getReferenceFromUrl(stringReference);
        }

        if(dbRefNoteToEdit != null){
            dbRefNoteToEdit.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // converts container object to note
                    Note n = dataSnapshot.getValue(Note.class);
                    editTitle.setText(n.getText());
                    editAuthor.setText(n.getAuthor());
                    editText.setText(n.getText());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // error
                    Log.d(TAG, "Error reading from database");
                }
            });
        }

        note = new Note();

        spinTag.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                note.setTag(spinTag.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        // button publish - make note and add to database
        buttonPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAndClose();
            }
        });

    }

    private void saveAndClose() {
        Log.d(TAG, "saveAndClose()");

        note.setTitle(editTitle.getText().toString());
        note.setAuthor(editAuthor.getText().toString());
        note.setTag(spinTag.getSelectedItem().toString());
        note.setText(editText.getText().toString());

        if(dbRefNoteToEdit == null){
            DatabaseReference dbRefNewNote = dbRefNotes.push();
            dbRefNewNote.setValue(note);
        }
        else{
            dbRefNoteToEdit.setValue(note);
        }

        finish();
    }

}
