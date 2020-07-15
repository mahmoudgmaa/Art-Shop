package com.example.handshop.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.handshop.Adapters.postAdapter;
import com.example.handshop.R;
import com.example.handshop.models.posts;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class postDetailsFragment extends Fragment {
private ImageView back;
private ArrayList<posts> postsArrayList;
private RecyclerView recyclerView;
private postAdapter postAdapter;
private String postId;
private Context mContext;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_post_details, container, false);
//        back=view.findViewById(R.id.backFromPostsDetails);
////        back.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////
////            }
////        });
        SharedPreferences Prefs= getContext().getSharedPreferences("PREFS" ,Context.MODE_PRIVATE);
        postId=Prefs.getString("postId" , "none");
        recyclerView=view.findViewById(R.id.postsDetailsRecycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        postsArrayList=new ArrayList<>();
        postAdapter=new postAdapter(getContext() , postsArrayList);
        recyclerView.setAdapter(postAdapter);
        readPosts();
        return view;
    }

    private void readPosts() {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("posts").child(postId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postsArrayList.clear();
                posts post=dataSnapshot.getValue(posts.class);
                postsArrayList.add(post);
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}