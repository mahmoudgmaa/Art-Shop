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


public class savedPosts extends Fragment {

    String profileId;
    private ArrayList<String> savedIds;
    private RecyclerView recyclerView_saves;
    private ArrayList<posts> savedArrayList;
    private profilePhotosAdapter savedAdapter;
    private FirebaseUser currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_saved_posts, container, false);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        SharedPreferences Prefs = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profileId = Prefs.getString("profileId", "none");
        recyclerView_saves = view.findViewById(R.id.recycler_view_save);
        recyclerView_saves.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager2 = new GridLayoutManager(getContext(), 3);
        recyclerView_saves.setLayoutManager(gridLayoutManager2);
        savedArrayList = new ArrayList<>();
        savedPosts();
        savedAdapter = new profilePhotosAdapter(getContext(), savedArrayList);
        recyclerView_saves.setAdapter(savedAdapter);
        return view;
    }

    public void savedPosts() {
        savedIds = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("saved").child(currentUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    savedIds.add(snapshot.getKey());
                }
                readSaved();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readSaved() {
        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("posts");
        reference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                savedArrayList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    posts post = snapshot.getValue(posts.class);

                    for (String id : savedIds) {
                        if (id.equals(post.getPostId())) {
                            savedArrayList.add(post);
                        }
                    }
                }
                savedAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}