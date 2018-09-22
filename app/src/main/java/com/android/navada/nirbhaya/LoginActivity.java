package com.android.navada.nirbhaya;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private TextView forgotPasswordTextView;
    private ProgressDialog mProgressDialog;
    private Intent intent;
    private SharedPreferences mSharedPreferences;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseUser mFireBaseUser;

    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        forgotPasswordTextView = findViewById(R.id.forgotPasswordTextView);
        mAuth = FirebaseAuth.getInstance();
        mSharedPreferences = this.getSharedPreferences(getPackageName(), MODE_PRIVATE);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Signing In");

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                mFireBaseUser = firebaseAuth.getCurrentUser();
                if(mFireBaseUser!=null) {

                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(intent);
                }
            }
        };

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)

            Log.d(TAG, "onConfigurationChanged: " + "Landscape");

        else if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT)

            Log.d(TAG, "onConfigurationChanged: " + "Portrait");
    }

    @Override
    protected void onResume() {
        super.onResume();

        mAuth.addAuthStateListener(mAuthStateListener);

        forgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                intent = new Intent(LoginActivity.this,ResetPasswordActivity.class);
                startActivity(intent);

            }
        });

    }

    public void onClickSignInButton(View view){

        final String userEmail,userPassword;

        userEmail = emailEditText.getText().toString().trim();
        userPassword = passwordEditText.getText().toString();

        if(userEmail.isEmpty() || userPassword.isEmpty())
            makeToast("Fields cannot be empty!");
        else {

            mProgressDialog.show();
            mAuth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {

                        mProgressDialog.cancel();

                                mSharedPreferences.edit().putString("email",userEmail).apply();
                                mSharedPreferences.edit().putString("password", userPassword).apply();

                        }

                    else
                        {

                            mProgressDialog.cancel();
                        makeToast(task.getException().getMessage());
                    }
                }
            });
        }


    }

    @Override
    protected void onPause() {
        super.onPause();

        //Detaching listener when the activity is no longer visible

        if(mAuthStateListener!=null)
            mAuth.removeAuthStateListener(mAuthStateListener);

    }

    private void makeToast(String message){

        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();

    }

    public void onClickSignUpButton(View view){

        Intent intent = new Intent(this,SignUpActivity.class);
        startActivity(intent);

    }

}
