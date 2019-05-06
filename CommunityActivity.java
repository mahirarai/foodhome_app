package itp341.raihan.mahira.project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

public class CommunityActivity extends AppCompatActivity {

    private static final String TAG = CommunityActivity.class.getSimpleName();
    public static final String EXTRA_URL = "itp341.raihan.mahira.project.EXTRA_URL";

    private ListView listView;
    private Button buttonAdd;

    // database references
    private DatabaseReference dbRefNotes;
    private DatabaseReference dbRefUsers;
    private DatabaseReference dbRefUserToEdit;

    private NoteListAdapter adapter;
    private User user;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    Intent intentMain = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivityForResult(intentMain, 0);

                    return true;
                case R.id.navigation_community:

                    return true;
                case R.id.navigation_saved:
                    Intent intentSaved = new Intent(getApplicationContext(), SavedActivity.class);
                    startActivityForResult(intentSaved, 0);

                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);

        listView = findViewById(R.id.list_view);
        buttonAdd = findViewById(R.id.button_add);

        // get database
        FirebaseDatabase firebase = FirebaseDatabase.getInstance();
        // get database reference paths
        dbRefNotes = firebase.getReference(FirebaseRefs.NOTE);
        dbRefUsers = firebase.getReference(FirebaseRefs.USER);
        // instantiate adapter
        adapter = new NoteListAdapter(this, Note.class, R.layout.layout_list_note, dbRefNotes);
        // set adapter for listView
        listView.setAdapter(adapter);

        user = new User();

        Intent i = getIntent();
        String stringReference = i.getStringExtra(EXTRA_URL);
        if(stringReference != null){
            dbRefUserToEdit = FirebaseDatabase.getInstance().getReferenceFromUrl(stringReference);
        }
        if(dbRefUserToEdit != null){
            dbRefUserToEdit.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    user = dataSnapshot.getValue(User.class);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d(TAG, "Error reading from database");
                }
            });
        }
        if(dbRefUserToEdit == null){
            DatabaseReference dbRefNewUser = dbRefUsers.push();
            dbRefNewUser.setValue(user);
        }
        else{
            dbRefUserToEdit.setValue(user);
        }

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), CreateNoteActivity.class);
                startActivityForResult(i, 0);
            }
        });

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }

    // custom adapter for interfacing between list and firebase
    private class NoteListAdapter extends FirebaseListAdapter<Note>{
        public NoteListAdapter(Activity activity, Class<Note> modelClass, int modelLayout, DatabaseReference ref) {
            super(activity, modelClass, modelLayout, ref);
        }

        @Override
        protected void populateView(View v, final Note model, final int position) {
            TextView textTitle = v.findViewById(R.id.list_note_title);
            textTitle.setText(model.getTitle());
            TextView textAuthor = v.findViewById(R.id.list_note_author);
            textAuthor.setText(model.getAuthor());
            Button buttonMore = v.findViewById(R.id.button_more);
            buttonMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DatabaseReference clickedRef = adapter.getRef(position);
                    Intent i = new Intent(getApplicationContext(), DetailNoteActivity.class);
                    i.putExtra(DetailNoteActivity.EXTRA_URL, clickedRef.toString());
                    startActivityForResult(i, 0);
                }
            });
        }
    }


}
