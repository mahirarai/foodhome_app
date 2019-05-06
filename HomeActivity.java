package itp341.raihan.mahira.project;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import itp341.raihan.mahira.project.model.Business;
import itp341.raihan.mahira.project.model.Businesses;
import itp341.raihan.mahira.project.model.Note;
import itp341.raihan.mahira.project.model.User;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = HomeActivity.class.getSimpleName();

    private Button buttonSearch;
    private Spinner spinSearch;
    private String userSearch;

    // navigation
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:

                    return true;
                case R.id.navigation_community:
                    Intent intentCommunity = new Intent(getApplicationContext(), CommunityActivity.class);
                    startActivityForResult(intentCommunity, 0);
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
        setContentView(R.layout.activity_home);

        // navigation
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // initiate references
        buttonSearch = findViewById(R.id.button_search);
        spinSearch = findViewById(R.id.spin_search);

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userSearch = spinSearch.getSelectedItem().toString();
                Log.d(TAG, "User entered: " + userSearch);

                Intent i = new Intent(getApplicationContext(), ResultActivity.class);
                startActivity(i);

            }
        });
    }

}
