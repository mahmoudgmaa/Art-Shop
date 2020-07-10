package com.example.handshop;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class HomeFragment extends Fragment {
    private RecyclerView postRecycler;
    private postAdapter adapter;
    private ArrayList<posts> postsArrayList;
    private ArrayList<String> followingList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_home, container, false);
        postRecycler=view.findViewById(R.id.postsRecycler);
        postRecycler.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        postRecycler.setLayoutManager(linearLayoutManager);
        postsArrayList=new ArrayList<>();
        adapter=new postAdapter(getContext() , postsArrayList);
        postRecycler.setAdapter(adapter);

        checkFollowing();

        return view;
    }

    public void checkFollowing(){
        followingList=new ArrayList<>();
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("follow")
                .child(FirebaseAuth.getInstance().getUid())
                .child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followingList.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    followingList.add(snapshot.getKey());
                }
                readPosts();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void readPosts(){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postsArrayList.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    posts post=snapshot.getValue(posts.class);
                    for(String id:followingList){
                        if(post.getPublisher().equals(id)){
                            postsArrayList.add(post);
                        }

                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}