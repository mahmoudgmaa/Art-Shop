package com.example.handshop.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.handshop.Adapters.searchUserAdapter;
import com.example.handshop.R;
import com.example.handshop.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class searchFragment extends Fragment {
    private RecyclerView usersRecycler;
    private List<User> mUser;
    private searchUserAdapter adapter;
    EditText searchBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_search, container, false);
        usersRecycler=view.findViewById(R.id.usersRecycler);
        searchBar=view.findViewById(R.id.searchBar);
        usersRecycler.setHasFixedSize(true);
        usersRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mUser=new ArrayList<>();
        adapter=new searchUserAdapter(mUser , getContext());
        usersRecycler.setAdapter(adapter);
        readUsers();
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setUpSearchBar(s.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;
    }

    private void setUpSearchBar(String s){
        Query query= FirebaseDatabase.getInstance().getReference("users").orderByChild("userName")
                .startAt(s)
                .endAt(s+"\uf0ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUser.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    User user=snapshot.getValue(User.class);
                    mUser.add(user);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void readUsers(){
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("recentSearch");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (searchBar.getText().toString().equals("")){
                    mUser.clear();
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                       User user= snapshot.getValue(User.class);
                       mUser.add(user);
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}