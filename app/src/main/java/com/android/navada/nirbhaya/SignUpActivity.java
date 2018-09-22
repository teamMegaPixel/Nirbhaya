package com.android.navada.nirbhaya;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.util.concurrent.TimeUnit;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "SignUpActivity";

    private EditText nameEditText,emailEditText,passwordEditText,mobileNumberEditText;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    public static PhoneAuthProvider.ForceResendingToken token;
    private String userName,userEmail,userPassword,userMobileNumber;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        mobileNumberEditText = findViewById(R.id.mobileNumberEditText);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Sending Verification Code...");
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
                token = forceResendingToken;
                Intent intent = new Intent(SignUpActivity.this,VerificationActivity.class);
                intent.putExtra("userName",userName);
                intent.putExtra("userEmail",userEmail);
                intent.putExtra("userPassword",userPassword);
                intent.putExtra("userMobileNumber",userMobileNumber);
                intent.putExtra("verificationId",verificationId);
                startActivity(intent);

            }

            @Override
            public void onCodeAutoRetrievalTimeOut(String s) {
                super.onCodeAutoRetrievalTimeOut(s);
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

    public void onClickSubmitButton(View view) {


        userName = nameEditText.getText().toString().trim();
        userEmail = emailEditText.getText().toString().trim();
        userPassword = passwordEditText.getText().toString();
        userMobileNumber = mobileNumberEditText.getText().toString().trim();

        if (userName.isEmpty() || userEmail.isEmpty() || userPassword.isEmpty() || userMobileNumber.isEmpty())
            makeToast("Fields cannot be empty!");
        else if (userPassword.length() < 6)
            makeToast("Password should be minimum of 6 characters!");
        else {
            //Verify Phone Number
            PhoneNumberUtil mPhoneNumberUtil = PhoneNumberUtil.getInstance();
            try {
                Phonenumber.PhoneNumber mPhoneNumber = mPhoneNumberUtil.parse(userMobileNumber, "IN");
                if (mPhoneNumberUtil.isValidNumber(mPhoneNumber)) {
                    userMobileNumber = mPhoneNumberUtil.format(mPhoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164);

                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            userMobileNumber,
                            60,
                            TimeUnit.SECONDS,
                            this,
                            mCallbacks);
                    mProgressDialog.show();
                } else
                    makeToast("Please enter a valid mobile number!");
            } catch (NumberParseException e) {
                makeToast(e.getMessage());
            }

        }
    }

    private void makeToast(String message){

        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();

    }

}
