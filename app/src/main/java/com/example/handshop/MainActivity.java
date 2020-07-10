package com.example.handshop;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
private Button signUp , logIn;
private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initiateWidgets();
        clickListeners();
        //FirebaseDatabase.getInstance().getReference().child("user").child("hi").setValue("bye");
        if(firebaseAuth.getCurrentUser()!=null){
            startActivity(new Intent(getApplicationContext() , secondMainAc.class));
            finish();
        }
    }


    public void initiateWidgets(){
        signUp=findViewById(R.id.signUpBtn);
        logIn=findViewById(R.id.logInBtn);
        firebaseAuth=FirebaseAuth.getInstance();
    }


    public void clickListeners(){
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this , signUp.class));
            }
        });
        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this , login.class));
            }
        });
    }
}
