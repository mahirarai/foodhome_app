package itp341.raihan.mahira.project;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import itp341.raihan.mahira.project.model.Business;
import itp341.raihan.mahira.project.model.Businesses;
import itp341.raihan.mahira.project.model.Note;
import itp341.raihan.mahira.project.model.Singleton;
import itp341.raihan.mahira.project.model.User;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

// Yelp API
// Volley - Using https://developer.android.com/training/volley/simple.html
// Location Services - Using https://github.com/googlesamples/easypermissions and https://developer.android.com/guide/topics/location/strategies#FastFix

public class ResultActivity extends AppCompatActivity {

    private static final String TAG = ResultActivity.class.getSimpleName();
    private static final String CLIENT_ID = "5bAf2Meq1lSG3NMaZn1IXQ";
    private static final String API_KEY = "CVsyJpejO2X9Q9PG0xjl6ci8PbtN2TStpJt0eovXUp64eLwr29CAqcTUtYvc8rsZmoIY72kkhVBKPeaz06t4KFWwo36zbGFYx6sJ65AYmkeKf78_2FEHIWWI5IvGXHYx";

    public static final String EXTRA_URL = "itp341.raihan.mahira.project.EXTRA_URL";
    private final int REQUEST_LOCATION_PERMISSION = 1;

    private Button buttonSearch;
    private ListView listResults;
    private Spinner spinSearch;

    private String userSearch;
    private double longitude;
    private double latitude;
    private String cleanEmail;

    Businesses b;
    ArrayList<Business> businesses;
    ArrayAdapter<Business> BusinessesAdapter;

    DatabaseReference dbRefUsers;
    DatabaseReference dbRefUserToEdit; // if editing existing note


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:

                    Intent intentHome = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivityForResult(intentHome, 0);

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
        setContentView(R.layout.activity_result);
        requestLocationPermission();

        buttonSearch = findViewById(R.id.button_search_result);
        listResults = (ListView) findViewById(R.id.list_view_results);
        spinSearch = findViewById(R.id.spin_search_result);

        getRequest(userSearch);

        readDatabase();

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userSearch = spinSearch.getSelectedItem().toString();
                getRequest(userSearch);
            }
        });

        // Navigation
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

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

        // database
        String email = Singleton.get(this).getEmail();
        cleanEmail = email.replace(".", ",");
        if(dbRefUserToEdit == null){
            dbRefUsers.child(cleanEmail).setValue(cleanEmail);
        }

        else{
            dbRefUserToEdit.child(cleanEmail).setValue(cleanEmail);
        }


    }

    // location permission
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    // location permission
    @AfterPermissionGranted(REQUEST_LOCATION_PERMISSION)
    public void requestLocationPermission() {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};
        if(EasyPermissions.hasPermissions(this, perms)) {
            Log.d(TAG, "Permission already granted");
        }
        else {
            EasyPermissions.requestPermissions(this, "Please grant the location permission", REQUEST_LOCATION_PERMISSION, perms);
        }
    }

    private boolean getLocation() {
        // acquire reference to system Location Manager
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // define listener responding to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // new location is found by the network location provider
            }
            public void onStatusChanged(String provider, int status, Bundle extras) { }
            public void onProviderEnabled(String provider) { }
            public void onProviderDisabled(String provider) { }
        };

        // register  listener with Location Manager to receive location updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Location permission not granted");
            requestLocationPermission();
            return false;
        }

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        String locationProvider = LocationManager.GPS_PROVIDER;
        Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);

        // store values
        longitude = lastKnownLocation.getLongitude();
        latitude = lastKnownLocation.getLatitude();
        Log.d(TAG, "Longitude: " + longitude + ", Latitude: " + latitude);

        return true;
    }

    private void getRequest(String userSearch) {

        boolean getLocation = getLocation();
        if(!getLocation){
            Log.d(TAG, "getLocation is false");
            longitude = -118.29442;     // default value
            latitude = 34.03138;        // default value
        }

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        // YELP API
        // String url = "https://api.yelp.com/v3/businesses/search?term=" + userSearch + "&latitude=34.03138&longitude=-118.29442";
        String term = userSearch + "&latitude=";
        String latitudeString = latitude + "&longitude=";
        String url = "https://api.yelp.com/v3/businesses/search?term=" + term + latitudeString + longitude;

        // Request string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        b = gson.fromJson(response, Businesses.class);

                        businesses = new ArrayList<Business>();
                        businesses = (ArrayList<Business>)b.businesses;
                        BusinessesAdapter = new BusinessesAdapter(getApplicationContext(),
                                R.layout.layout_list_result, businesses);
                        listResults.setAdapter(BusinessesAdapter);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "ErrorResponse: String URL didn't work!");
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + API_KEY);
                return params;
            }
        };
        queue.add(stringRequest);
    }

    private void refresh(){
        BusinessesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode: " + requestCode);

        refresh();
    }


    // BusinessesAdapter
    private class BusinessesAdapter extends ArrayAdapter<Business> {
        private final ArrayList<Business> businesses;

        public BusinessesAdapter(Context context, int resource, ArrayList<Business> objects){
            super(context, resource, objects);
            this.businesses = objects;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent){
            if(convertView == null){
                convertView = getLayoutInflater().inflate(
                        R.layout.layout_list_result, null);
            }

            final Business b = businesses.get(position);

            TextView textTitle = (TextView) convertView.findViewById(R.id.list_place_title);
            textTitle.setText(b.name);
            ImageView image = (ImageView) convertView.findViewById(R.id.list_image_background);
            try{
                Picasso.get().load(b.image_url).into(image);
            }
            catch (Exception e){
                Log.d(TAG, "Picasso Error");
            }

            ImageButton buttonMore = (ImageButton) convertView.findViewById(R.id.button_more);
            buttonMore.setTag(position);
            buttonMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Add business to database
                    dbRefUsers.child(cleanEmail).child("businesses").push().setValue(b);

                    // Add business to savedBusinesses
                    Singleton.get(getApplicationContext()).addSavedBusiness(b);
                    // Send Toast
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.message_save_search),
                            Toast.LENGTH_SHORT).show();

                }
            });

            return convertView;
        }
    }


}
