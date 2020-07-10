package com.example.handshop;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import de.hdodenhof.circleimageview.CircleImageView;


public class profileFragment extends Fragment {

    CircleImageView image_profile;
        ImageView options;
    TextView nposts, followers, following, fullname, bio, username;
    Button edit_profile;

    private RecyclerView recyclerView;
    private ArrayList<posts> arrayList;
    private profilePhotosAdapter adapter;

    private RecyclerView recyclerView_saves;
    ImageButton my_fotos, saved_fotos;
    FirebaseUser firebaseUser;
    String profileId;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_profile, container, false);
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        SharedPreferences prefs=getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profileId=prefs.getString("profileId","none");

        image_profile = view.findViewById(R.id.image_profile);
        nposts = view.findViewById(R.id.posts);
        followers = view.findViewById(R.id.followers);
        following = view.findViewById(R.id.following);
        fullname = view.findViewById(R.id.fullname);
        bio = view.findViewById(R.id.bio);
        edit_profile = view.findViewById(R.id.edit_profile);
        username = view.findViewById(R.id.username);
        my_fotos = view.findViewById(R.id.my_fotos);
        saved_fotos = view.findViewById(R.id.saved_fotos);
        options = view.findViewById(R.id.options);


        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager=new GridLayoutManager(getContext() , 3);
        recyclerView.setLayoutManager(gridLayoutManager);
        arrayList=new ArrayList<>();
        myPosts();
        adapter=new profilePhotosAdapter(getContext() , arrayList);
        recyclerView.setAdapter(adapter);

        recyclerView_saves = view.findViewById(R.id.recycler_view_save);
        userInfo();
        numberOfFollowers();
        numberOfFollowing();
        numberOfPosts();
        if (profileId.equals(firebaseUser.getUid())){
            edit_profile.setText("Edit your profile");
        }else{
            checkFollowing();
            saved_fotos.setVisibility(View.GONE);
        }


        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String btn=edit_profile.getText().toString();
                if(btn.equals("Edit your profile")){
                    Intent intent=new Intent(getContext() , editYourProf.class);
                    intent.putExtra("profileId" , profileId);
                    startActivity(intent);
                }else if(btn.equals("follow")){
                    FirebaseDatabase.getInstance().getReference().child("follow").child(firebaseUser.getUid()).child("following")
                            .child(profileId).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("follow").child(profileId).child("followers")
                            .child(firebaseUser.getUid()).setValue(true);
                }else if(btn.equals("following")){ FirebaseDatabase.getInstance().getReference().child("follow").child(firebaseUser.getUid()).child("following")
                        .child(profileId).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("follow").child(profileId).child("followers")
                            .child(firebaseUser.getUid()).removeValue();

                }
           }
        });

        return view;
    }
    public void userInfo(){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("users").child(profileId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(getContext()==null){
                    return;
                }
                User user=dataSnapshot.getValue(User.class);
                Glide.with(getContext())
                        .load(user.getImgUrl())
                        .into(image_profile);
                username.setText(user.getUserName());
                fullname.setText(user.getFullName());
                bio.setText(user.getBio());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void checkFollowing(){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("follow").child(firebaseUser.getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(profileId).exists()){
                    edit_profile.setText("following");
                }else {
                    edit_profile.setText("follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void numberOfFollowers(){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("follow").child(profileId).child("followers");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followers.setText(""+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void numberOfFollowing(){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("follow").child(profileId).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                following.setText(""+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void numberOfPosts(){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i=0;
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    posts post=snapshot.getValue(posts.class);
                    if(post.getPublisher().equals(profileId)){
                        i++;
                    }
                }
                nposts.setText(""+i);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void myPosts(){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayList.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    posts posts=dataSnapshot.getValue(com.example.handshop.posts.class);
                    if(posts.getPublisher().equals(profileId)){
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