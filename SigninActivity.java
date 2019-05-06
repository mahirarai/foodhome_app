package itp341.raihan.mahira.project;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import itp341.raihan.mahira.project.model.Singleton;

// Using https://javapapers.com/android/android-firebase-authentication/
public class SigninActivity extends AppCompatActivity {
    private static final String TAG = SigninActivity.class.getSimpleName();

    private TextView textError;
    private EditText editEmail;
    private EditText editPass;
    private Button buttonSignup;
    private Button buttonLogin;

    FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // initialize references
        firebaseAuth = FirebaseAuth.getInstance();
        textError = findViewById(R.id.text_error);
        editEmail = findViewById(R.id.edit_email);
        editPass = findViewById(R.id.edit_password);
        buttonSignup = findViewById(R.id.button_sign_up);
        buttonLogin = findViewById(R.id.button_login);

        // valid user
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    Singleton.get(getApplicationContext()).setEmail(editEmail.getText().toString());
                    Intent i = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(i);
                }
                else{
                    textError.setText(getResources().getString(R.string.signin_login));
                }
            }
        };

        // button signup
        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editEmail.getText().toString();
                String password = editPass.getText().toString();
                if(email.isEmpty()){
                    textError.setText(getResources().getString(R.string.signin_email));
                }
                else if(password.isEmpty()){
                    textError.setText(getResources().getString(R.string.signin_password));
                }
                else if (email.isEmpty() && password.isEmpty()){
                    textError.setText(getResources().getString(R.string.signin_empty));
                }
                else if(!(email.isEmpty() && password.isEmpty())){
                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SigninActivity.this,
                            new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(!task.isSuccessful()){
                                        textError.setText(task.getException().getMessage());
                                    }
                                    else{
                                        textError.setText(getResources().getString(R.string.signin_login_begin));
                                    }
                                }
                            });
                }
                else{
                    textError.setText(getResources().getString(R.string.signin_error));
                }
            }
        });

        // button login
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = editEmail.getText().toString();
                String password = editPass.getText().toString();
                if(email.isEmpty()){
                    textError.setText(getResources().getString(R.string.signin_email));
                }
                else if (password.isEmpty()){
                    textError.setText(getResources().getString(R.string.signin_password));
                }
                else if(email.isEmpty() && password.isEmpty()){
                    textError.setText(getResources().getString(R.string.signin_empty));
                }
                else if(!(email.isEmpty() && password.isEmpty())){
                    firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(SigninActivity.this,
                            new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()) {
                                        textError.setText(getResources().getString(R.string.signin_unsuccessful));
                                    } else {
                                        Singleton.get(getApplicationContext()).setEmail(email);
                                        Intent i = new Intent(getApplicationContext(), HomeActivity.class);
                                        startActivity(i);
                                    }
                                }
                            });
                }
                else{
                    textError.setText(getResources().getString(R.string.signin_error));
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

}

