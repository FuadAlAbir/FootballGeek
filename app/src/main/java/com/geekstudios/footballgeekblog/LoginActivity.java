package com.geekstudios.footballgeekblog;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText loginEmail;
    private EditText loginPassword;
    private TextView loginForgotPassword;
    private Button loginButton;
    private TextView loginCreateAccount;
    private ProgressBar loginProgressBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_login );

        mAuth = FirebaseAuth.getInstance();

        loginEmail = findViewById( R.id.login_email_edit_text );
        loginPassword = findViewById( R.id.login_password_edit_text );
        loginForgotPassword = findViewById( R.id.login_forgot_password_text_view );
        loginButton = findViewById( R.id.login_login_button );
        loginCreateAccount = findViewById( R.id.login_create_new_account_text_view );
        loginProgressBar = findViewById( R.id.login_progress_bar );

        loginButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String email = loginEmail.getText().toString().trim();
                String password = loginPassword.getText().toString();

                if(!TextUtils.isEmpty( email ) && !TextUtils.isEmpty( password )) {

                    loginProgressBar.setVisibility( View.VISIBLE );

                    mAuth.signInWithEmailAndPassword( email, password ).addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {

                                sendToNewBlog();

                            } else {
                                String error = task.getException().getMessage();
                                Toast.makeText( LoginActivity.this, "Error:" + error, Toast.LENGTH_SHORT ).show();
                            }
                        }
                    } );

                    loginProgressBar.setVisibility( View.VISIBLE );

                } else {
                    Toast.makeText(LoginActivity.this, "Error: Enter valid email and password", Toast.LENGTH_SHORT).show();
                }
            }
        } );
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null) {
            sendToMain();
        }
    }

    private void sendToMain() {
        Intent mainIntent = new Intent( LoginActivity.this, MainActivity.class );
        startActivity(mainIntent);
        finish();
    }

    private void sendToNewBlog() {
        Intent newBlogIntent = new Intent( LoginActivity.this, NewBlogActivity.class );
        startActivity(newBlogIntent);
        finish();
    }
}
