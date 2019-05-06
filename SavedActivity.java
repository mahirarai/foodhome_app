package itp341.raihan.mahira.project;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.ArrayList;

import itp341.raihan.mahira.project.model.Business;
import itp341.raihan.mahira.project.model.Note;
import itp341.raihan.mahira.project.model.Singleton;

public class SavedActivity extends AppCompatActivity {

    private static final String TAG = SavedActivity.class.getSimpleName();

    private ListView listNotes;
    private ListView listSearches;
    private Button buttonLogout;

    // database reference
    private DatabaseReference dbRefUsers;

    ArrayAdapter<Note> NoteListAdapter;
    ArrayAdapter<Business> SearchListAdapter;

    private NoteListAdapter noteAdapter;
    private SearchListAdapter searchAdapter;
    private String cleanEmail;

    // navigation
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
                    Intent intentCommunity = new Intent(getApplicationContext(), CommunityActivity.class);
                    startActivityForResult(intentCommunity, 0);
                    return true;
                case R.id.navigation_saved:

                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        listNotes = (ListView) findViewById(R.id.list_view_notes);
        listSearches = (ListView) findViewById(R.id.list_view_search);
        buttonLogout = (Button) findViewById(R.id.button_logout);

        FirebaseDatabase firebase = FirebaseDatabase.getInstance();
        dbRefUsers = firebase.getReference(FirebaseRefs.USER);

        // set adapters and listviews
        String email = Singleton.get(this).getEmail();
        cleanEmail = email.replace(".", ",");
        noteAdapter = new NoteListAdapter(this, Note.class, R.layout.layout_list_note, dbRefUsers.child(cleanEmail).child("notes"));
        listNotes.setAdapter(noteAdapter);
        searchAdapter = new SearchListAdapter(this, Business.class, R.layout.layout_list_result, dbRefUsers.child(cleanEmail).child("businesses"));
        listSearches.setAdapter(searchAdapter);

        // logout button
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(SavedActivity.this, SigninActivity.class);
                startActivity(i);
            }
        });

    }

    // adapter for interfacing between list and firebase
    private class NoteListAdapter extends FirebaseListAdapter<Note> {
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
                    Log.d(TAG, "Note view clicked");
//                    DatabaseReference clickedRef = noteAdapter.getRef(position);
//                    Intent i = new Intent(getApplicationContext(), DetailNoteActivity.class);
//                    i.putExtra(DetailNoteActivity.EXTRA_URL, clickedRef.toString());
//                    startActivity(i);
                }
            });
        }
    }

    // adapter for interfacing between list and firebase
    private class SearchListAdapter extends FirebaseListAdapter<Business> {
        public SearchListAdapter(Activity activity, Class<Business> modelClass, int modelLayout, DatabaseReference ref) {
            super(activity, modelClass, modelLayout, ref);
        }

        @Override
        protected void populateView(View v, final Business model, final int position) {
            TextView textTitle = v.findViewById(R.id.list_place_title);
            textTitle.setText(model.name);
            ImageView image = v.findViewById(R.id.list_image_background);
            try{
                Picasso.get().load(model.image_url).into(image);
            }
            catch (Exception e){
                Log.d(TAG, "Picasso Error");
            }
            ImageButton buttonMore = v.findViewById(R.id.button_more);
            buttonMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Search delete clicked");
                    // delete item from list
                }
            });
        }
    }

}
