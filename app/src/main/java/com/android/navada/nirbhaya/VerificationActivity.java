package com.android.navada.nirbhaya;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.CountDownTimer;
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
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class VerificationActivity extends AppCompatActivity {

    private static final String TAG = "VerificationActivity";

    private TextView timerTextView;
    private EditText verificationCodeEditText;
    private ProgressDialog mProgressDialog;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseReference;
    private String userName,userEmail,userPassword,userMobileNumber,verificationId;
    private CountDownTimer mCountDownTimer;
    private SharedPreferences mSharedPreferences;
    private PhoneAuthProvider.ForceResendingToken token;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        timerTextView = findViewById(R.id.timerTextView);
        verificationCodeEditText = findViewById(R.id.verificationCodeEditText);
        mSharedPreferences = this.getSharedPreferences(getPackageName(),MODE_PRIVATE);
        mAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        Intent intent = getIntent();
        userName = intent.getStringExtra("userName");
        userEmail = intent.getStringExtra("userEmail");
        userPassword = intent.getStringExtra("userPassword");
        userMobileNumber = intent.getStringExtra("userMobileNumber");
        verificationId = intent.getStringExtra("verificationId");
        token = SignUpActivity.token;
        startTimer();

    }

    @Override
    protected void onResume() {
        super.onResume();

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                mProgressDialog.cancel();
                makeToast(e.getMessage());
            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(verificationId, forceResendingToken);
                mProgressDialog.cancel();
                startTimer();
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(String s) {
                super.onCodeAutoRetrievalTimeOut(s);
            }
        };

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mCountDownTimer!=null)
            mCountDownTimer.cancel();
    }

    private void startTimer(){

        if(mCountDownTimer!=null)
            mCountDownTimer.cancel();
        mCountDownTimer = new CountDownTimer(61100, 1000) {
            @Override
            public void onTick(long l) {
                String mTimeToDisplay = (l / 1000 - 1) + ":00";
                timerTextView.setText(mTimeToDisplay);
            }

            @Override
            public void onFinish() {

                makeToast("Time out! Try again");
                finish();

            }
        }.start();

    }

    public void onClickVerifyButton(View view){

        String codeEntered = verificationCodeEditText.getText().toString().trim();

        if(codeEntered.isEmpty())
            makeToast("Please enter the verification code");
        else {

            mProgressDialog.setMessage("Verifying Credentials");
            mProgressDialog.show();

            final PhoneAuthCredential mPhoneAuthCredential = PhoneAuthProvider.getCredential(verificationId,codeEntered);

            mAuth.signInWithCredential(mPhoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful())
                    {

                        mCountDownTimer.cancel();

                        /*Since we want users to sign up with their email,auth object constructed using
                          the phone number is deleted */

                        signUpWithEmail();

                    }
                    else {
                        mProgressDialog.cancel();
                        verificationCodeEditText.setText("");
                        makeToast(task.getException().getMessage());
                    }
                }
            });
        }

    }

    private void signUpWithEmail()
    {
        mAuth.getCurrentUser().delete();

        mAuth.createUserWithEmailAndPassword(userEmail,userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()) {



                    mSharedPreferences.edit().putString("email",userEmail).apply();

                    mSharedPreferences.edit().putString("phoneNumber",userMobileNumber).apply();

                    mSharedPreferences.edit().putString("name",userName).apply();

                    User user = new User(userName,userEmail,userMobileNumber);

                    mDatabaseReference.child(mAuth.getUid()).setValue(user);

                    makeToast("Welcome to URSafe");

                    mProgressDialog.cancel();

                    finish();

                }
                else {

                    mProgressDialog.cancel();
                    makeToast(task.getException().getMessage());
                    finish();
                }
            }
        });
    }

    public void onClickResendCode(View view){

        mProgressDialog.setMessage("Resending Code");
        mProgressDialog.show();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                userMobileNumber,
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks,
                token);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)

            Log.d(TAG, "onConfigurationChanged: " + "Landscape");

        else if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT)

            Log.d(TAG, "onConfigurationChanged: " + "Portrait");
    }

    private void makeToast(String message){

        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();

    }

}
