package com.developer.sagar.prevoice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {
    private EditText phoneText;
    private EditText codeText;
    private Button nextButton;
    private String checker="",phoneNumber="";
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private String verificationId;
    private FirebaseAuth mAuth;
    private LinearLayout linearLayout;
    private DatabaseReference userRef;
    private PhoneAuthProvider.ForceResendingToken forceResendingToken;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        loadingBar = new ProgressDialog(this);
        linearLayout = findViewById(R.id.linearLayout);
        phoneText = findViewById(R.id.phoneText);
        codeText = findViewById(R.id.codeText);
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        nextButton = findViewById(R.id.continueNextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(nextButton.getText().equals("Submit") || checker.equals("Code Sent")){
                    String verificationCode = codeText.getText().toString();
                    if(verificationCode.equals(""))
                        Toast.makeText(getApplicationContext(),"Please write verification code first",Toast.LENGTH_SHORT).show();
                    else {
                        loadingBar.setTitle("Phone Number Verification");
                        loadingBar.setMessage("Please wait");
                        loadingBar.setCanceledOnTouchOutside(false);
                        loadingBar.show();
                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId,verificationCode);
                        signInWithPhoneAuthCredential(credential);
                    }
                }
                else {
                    phoneNumber = "+91"+phoneText.getText().toString();
                    if(!phoneNumber.equals("")){
                        loadingBar.setTitle("Phone Number Verification");
                        loadingBar.setMessage("Please wait");
                        loadingBar.setCanceledOnTouchOutside(false);
                        loadingBar.show();
                        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                phoneNumber,
                                60,
                                TimeUnit.SECONDS,
                                LoginActivity.this,
                                callbacks
                        );
                    }
                    else
                        Toast.makeText(getApplicationContext(),"Please write valid number",Toast.LENGTH_SHORT).show();
                }


            }
        });
        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(getApplicationContext(),"Please write valid number",Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();

                nextButton.setText("Continue");
                codeText.setVisibility(View.GONE);
                linearLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken1) {
                super.onCodeSent(s, forceResendingToken);
                verificationId = s;
                forceResendingToken = forceResendingToken1;
                linearLayout.setVisibility(View.GONE);
                checker = "Code Sent";
                nextButton.setText("Submit");
                codeText.setVisibility(View.VISIBLE);
                loadingBar.dismiss();
                Toast.makeText(getApplicationContext(),"Code has been sent",Toast.LENGTH_SHORT).show();

            }
        };
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            loadingBar.dismiss();
                            Toast.makeText(getApplicationContext(),"Logged In Successfully",Toast.LENGTH_SHORT).show();
                            sendUserToMainActivity();
                        } else {
                            loadingBar.dismiss();
                            Toast.makeText(getApplicationContext(),"Error"+task.getException().toString(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void sendUserToMainActivity(){
        Intent intent= new Intent(LoginActivity.this, ProfileDetails.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser != null){
            startActivity(new Intent(LoginActivity.this, Splash.class));
            finish();
        }
    }
}
