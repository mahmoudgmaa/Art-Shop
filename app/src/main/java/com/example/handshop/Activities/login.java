package com.example.handshop.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.handshop.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class login extends AppCompatActivity {
     private EditText mEmail , mPassword;
     private String email , password;
     private Button logInBtn3;
     private ProgressDialog dialog;
     private FirebaseAuth firebaseAuth;
     private TextView forget;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initialWidgets();
        mEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    storeStrings();
                    if(TextUtils.isEmpty(email)){
                        mEmail.setBackgroundResource(R.drawable.button4);
                        mEmail.setError("you have to enter Email address or phone number");
                        return;
                    }else {
                        mEmail.setBackgroundResource(R.drawable.button);
                    }
                }

            }
        });

        mPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    storeStrings();
                    if(TextUtils.isEmpty(password)){
                        mPassword.setBackgroundResource(R.drawable.button4);
                        mPassword.setError("password is required");
                        return;
                    }else {
                        mPassword.setBackgroundResource(R.drawable.button);
                    }
                }
            }
        });

        logInBtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storeStrings();
                if (TextUtils.isEmpty(email)||TextUtils.isEmpty(password)){
                    Toast.makeText(login.this, "Missing fields", Toast.LENGTH_SHORT).show();
                }
                else {
                    dialog.setTitle("logging you in");
                    dialog.setMessage("please wait , we are checking your credentials");
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                    firebaseAuth.signInWithEmailAndPassword(email , password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                dialog.dismiss();
                                Toast.makeText(login.this, "logged in successfully", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext() , secondMainAc.class));
                                finish();
                            }else{
                                dialog.dismiss();
                                Toast.makeText(login.this, "Error! "+ task.getException().getMessage() , Toast.LENGTH_SHORT).show();
                                forget.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }
            }
        });
    }


    private void initialWidgets() {
        mEmail=findViewById(R.id.PhoneEmailLogInEditText);
        mPassword=findViewById(R.id.passwordLoginEditText);
        logInBtn3=findViewById(R.id.logInBtn3);
        dialog=new ProgressDialog(login.this);
        firebaseAuth=FirebaseAuth.getInstance();
        forget=findViewById(R.id.forget);

    }
    private void storeStrings(){
        email=mEmail.getText().toString();
        password=mPassword.getText().toString();
    }













    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }

}
