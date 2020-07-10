package com.example.handshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class signUp extends AppCompatActivity {
    private EditText mfirstName, mlastName, memailSignUp, mpasswordSignUp , mUserName;
    private CheckBox acceptOurPolicies;
    private Button signUpBtn2;
    private String firstName, lastName, phoneEmail, password, userId, fullName , userName;
    private Boolean canIComplete , firstNameCheck;
    private TextView firstNameRequired, lastNameRequired, phoneEmailRequired, passwordRequired, shortPassword, exists;
    private ProgressDialog dialog;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private String TAG , check, defaultImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initiateWidgets();
        //FirebaseDatabase.getInstance().getReference().child("user").child("hi").setValue("byr");


        mfirstName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    storeStrings();
                    if (TextUtils.isEmpty(firstName)) {
                        //Toast.makeText(signUp.this, "last Name is required field", Toast.LENGTH_SHORT).show();
                        canIComplete = false;
                        mfirstName.setBackgroundResource(R.drawable.button4);
                        mfirstName.setError("first name is required");
                        return;
                    } else {
                        canIComplete = true;
                        mfirstName.setBackgroundResource(R.drawable.button);

                    }
                }
            }
        });


        mlastName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    storeStrings();
                    if (TextUtils.isEmpty(lastName)) {
                        //Toast.makeText(signUp.this, "last Name is required field", Toast.LENGTH_SHORT).show();
                        canIComplete = false;
                        mlastName.setBackgroundResource(R.drawable.button4);
                        mlastName.setError("last name is required");
                        return;
                    } else {
                        canIComplete = true;
                        mlastName.setBackgroundResource(R.drawable.button);

                    }
                }
            }
        });



        memailSignUp.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    storeStrings();
                    if (TextUtils.isEmpty(phoneEmail)) {
                        //Toast.makeText(signUp.this, "First Name is required field", Toast.LENGTH_SHORT).show();
                        canIComplete = false;
                        memailSignUp.setBackgroundResource(R.drawable.button4);
                        memailSignUp.setError("You have to enter email or phone number");
                        return;
                    } else {
                        canIComplete = true;
                        memailSignUp.setBackgroundResource(R.drawable.button);
                    }
                }
            }
        });


        mpasswordSignUp.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    storeStrings();
                    if (TextUtils.isEmpty(password)) {
                        //Toast.makeText(signUp.this, "First Name is required field", Toast.LENGTH_SHORT).show();
                        canIComplete = false;
                        mpasswordSignUp.setBackgroundResource(R.drawable.button4);
                        mpasswordSignUp.setError("password is required");
                        return;
                    } else if (password.length() < 8) {
                        canIComplete = false;
                        mpasswordSignUp.setBackgroundResource(R.drawable.button4);
                        mpasswordSignUp.setError("password must be at least 8 characters");
                        return;
                    } else {
                        canIComplete = true;
                        mpasswordSignUp.setBackgroundResource(R.drawable.button);
                    }
                }
            }
        });

        mUserName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    storeStrings();
                    Query usernameNameQuery = FirebaseDatabase.getInstance().getReference().child("users").orderByChild("username")
                            .equalTo(userName);
                    usernameNameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getChildrenCount() > 0) {
                                //firstNameCheck = false;
                                check="yes";
                                mUserName.setError("username already taken");
                                mUserName.setBackgroundResource(R.drawable.button4);
                            } else {
                                //firstNameCheck=true;
                                check="no";
                                if (TextUtils.isEmpty(userName)) {
                                    //Toast.makeText(signUp.this, "First Name is required field", Toast.LENGTH_SHORT).show();
                                    canIComplete = false;
                                    mUserName.setBackgroundResource(R.drawable.button4);
                                    mUserName.setError("username is required");
                                    return;
                                }else if(userName.length()<6){
                                    mUserName.setBackgroundResource(R.drawable.button4);
                                    mUserName.setError("username must be at least 6 characters");
                                }
                                else {
                                    canIComplete = true;
                                    mUserName.setBackgroundResource(R.drawable.button);

                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }
            }
        });



        signUpBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!acceptOurPolicies.isChecked()) {
                    Toast.makeText(signUp.this, "You must agree our terms first", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName) || TextUtils.isEmpty(phoneEmail) || TextUtils.isEmpty(password) || TextUtils.isEmpty(userName)) {
                    Toast.makeText(signUp.this, "Missing fields", Toast.LENGTH_SHORT).show();
                } else if (password.length() < 8) {
                    Toast.makeText(signUp.this, "password must be at least 8 characters", Toast.LENGTH_SHORT).show();
                }else if(check.equals("yes")){
                    Toast.makeText(signUp.this, "use different username" , Toast.LENGTH_SHORT).show();
                }
                else {
                    storeStrings();
                    dialog.setTitle("creating your account");
                    dialog.setMessage("please wait , we are checking your credentials");
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                    //validation(firstName, lastName, phoneEmail, password);
                    signUPMethod(phoneEmail, password);

                }
            }
        });
    }


    private void initiateWidgets() {
        mfirstName = findViewById(R.id.firstName);
        mfirstName.setFocusableInTouchMode(true);
        mlastName = findViewById(R.id.lastName);
        mUserName=findViewById(R.id.userNameSignUpEditText);
        memailSignUp = findViewById(R.id.PhoneEmailSignUpEditText);
        mpasswordSignUp = findViewById(R.id.passwordSignUpEditText);
        acceptOurPolicies = findViewById(R.id.agreeOurPolicies);
        signUpBtn2 = findViewById(R.id.signUpBtn2);
        dialog = new ProgressDialog(signUp.this);
        exists = findViewById(R.id.exist);
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        defaultImageUrl="https://firebasestorage.googleapis.com/v0/b/hand-shop-14e8e.appspot.com/o/60-602450_profile-clipart-profile-icon-round-profile-pic-png.png?alt=media&token=130554d2-44ec-4c3a-9423-4ad9c5ec2886"

;    }

    public void storeStrings() {
        firstName = mfirstName.getText().toString();
        lastName = mlastName.getText().toString();
        phoneEmail = memailSignUp.getText().toString();
        password = mpasswordSignUp.getText().toString();
        userName=mUserName.getText().toString();
        fullName = firstName + lastName;
    }

    private void signUPMethod(final String phoneEmail, final String password) {
        firebaseAuth.createUserWithEmailAndPassword(phoneEmail, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    dialog.dismiss();
                    userId = firebaseAuth.getCurrentUser().getUid();
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
                    DatabaseReference reference1=FirebaseDatabase.getInstance().getReference().child("follow").child(userId);
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("id" , userId);
                    hashMap.put("firstName", firstName);
                    hashMap.put("lastName", lastName);
                    hashMap.put("userName" , userName);
                    hashMap.put("email", phoneEmail);
                    hashMap.put("password", password);
                    hashMap.put("fullName", fullName);
                    hashMap.put("imgUrl", defaultImageUrl);
                    reference.setValue(hashMap);
                    Toast.makeText(signUp.this, "registered successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), secondMainAc.class));
                    finish();
                } else {
                    dialog.dismiss();
                    Toast.makeText(signUp.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void logInFromRegisterAc(View view) {
        startActivity(new Intent(getApplicationContext(), login.class));
        finish();
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }



    /*private void validation(final String firstName, final String lastName, final String phoneEmail, final String password) {
        final DatabaseReference reference;
        reference= FirebaseDatabase.getInstance().getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!(dataSnapshot.child("User").child(phoneEmail).exists())){
                    exists.setVisibility(View.GONE);

                    HashMap<String , Object> userMap = new HashMap<>();
                    userMap.put("firstName" , firstName);
                    userMap.put("lastName" , lastName);
                    userMap.put("Email" , phoneEmail);
                    userMap.put("password" , password);
                    reference.child("User").child(firstName).updateChildren(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(signUp.this, "Registered successfully", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                                //Intent intent=new Intent(signUp.this , login.class);
                                //startActivity(intent);
                            }else {
                                Toast.makeText(signUp.this, "Network Error, please try again", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else {
                    dialog.dismiss();
                    exists.setVisibility(View.VISIBLE);
                    memailSignUp.setBackgroundResource(R.drawable.button4);
                    Toast.makeText(signUp.this, "please try again using different Email or phone number", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/
    //}


}
