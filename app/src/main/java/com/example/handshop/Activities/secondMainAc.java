package com.example.handshop.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;

import com.example.handshop.Fragments.HomeFragment;
import com.example.handshop.Fragments.likeFragment;
import com.example.handshop.Fragments.profileFragment;
import com.example.handshop.Fragments.searchFragment;
import com.example.handshop.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class secondMainAc extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private Button logout;
    private BottomNavigationView bottomNavigationView;
    private Fragment selectedFragment=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_main);
        initialWidgets();
        getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer , new HomeFragment()).commit();

        setUpBottomNavigator();

        Bundle intent=getIntent().getExtras();
        if(intent!=null) {
            String publisher = intent.getString("publisherId");
            SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
            editor.apply();
            getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, new profileFragment()).commit();
        }else {
            getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer ,new HomeFragment()).commit();
        }
    }

    private void setUpBottomNavigator() {
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.homeBottomMenu:
                        selectedFragment=new HomeFragment();
                        break;
                    case R.id.searchBottomMenu:
                        selectedFragment=new searchFragment();
                        break;
                    case R.id.addBottomMenu:
                        selectedFragment=null;
                        startActivity(new Intent(secondMainAc.this , shareActivity.class));
                        break;
                    case R.id.loveBottomMenu:
                        selectedFragment=new likeFragment();
                        break;
                    case R.id.userBottomMenu:
                        SharedPreferences.Editor editor=getSharedPreferences("PREFS" , MODE_PRIVATE).edit();
                        editor.putString("profileId" , FirebaseAuth.getInstance().getCurrentUser().getUid());
                        editor.apply();
                        selectedFragment=new profileFragment();
                        break;
                }
                if(selectedFragment!=null){
                    getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer , selectedFragment).commit();
                }
                return true;
            }
        });
    }

    private void initialWidgets() {
        //logout=findViewById(R.id.logout);
        firebaseAuth=FirebaseAuth.getInstance();
        bottomNavigationView=findViewById(R.id.bottomNavigator);
    }
}
