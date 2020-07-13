package com.example.handshop;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.handshop.Adapters.profilePhotosAdapter;
import com.example.handshop.models.posts;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;


public class myPosts extends Fragment {

    private RecyclerView recyclerView;
    private ArrayList<posts> arrayList;
    private profilePhotosAdapter adapter;
    String profileId;
    FirebaseUser currentUser;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_posts, container, false);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        SharedPreferences Prefs = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profileId = Prefs.getString("profileId", "none");
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(gridLayoutManager);
        arrayList = new ArrayList<>();
        myPosts();
        adapter = new profilePhotosAdapter(getContext(), arrayList);
        recyclerView.setAdapter(adapter);
        return view;
    }

    public void myPosts() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    posts posts = snapshot.getValue(com.example.handshop.models.posts.class);
                    if (posts.getPublisher().equals(profileId)) {
                        arrayList.add(posts);
                    }
                }
                Collections.reverse(arrayList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}