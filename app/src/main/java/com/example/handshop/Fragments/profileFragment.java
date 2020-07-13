package com.example.handshop.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.handshop.Adapters.FragmentTabLayoutAdapter;
import com.example.handshop.Adapters.profilePhotosAdapter;
import com.example.handshop.R;
import com.example.handshop.Activities.editYourProf;
import com.example.handshop.models.User;
import com.example.handshop.models.posts;
import com.example.handshop.myPosts;
import com.example.handshop.savedPosts;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import de.hdodenhof.circleimageview.CircleImageView;


public class profileFragment extends Fragment {

    private CircleImageView image_profile;
    private ImageView options;
    private TextView nposts, followers, following, fullname, bio, username;
    private Button edit_profile;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FragmentTabLayoutAdapter fragmentTabLayoutAdapter;

//    private RecyclerView recyclerView;
//    private ArrayList<posts> arrayList;
//    private profilePhotosAdapter adapter;

//    private ArrayList<String>  savedIds;
//    private RecyclerView recyclerView_saves;
//    private ArrayList<posts> savedArrayList;
//    private profilePhotosAdapter savedAdapter;

//    ImageButton my_fotos, saved_fotos;


    FirebaseUser firebaseUser;
    String profileId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        SharedPreferences prefs = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profileId = prefs.getString("profileId", "none");

        image_profile = view.findViewById(R.id.image_profile);
        nposts = view.findViewById(R.id.posts);
        followers = view.findViewById(R.id.followers);
        following = view.findViewById(R.id.following);
        fullname = view.findViewById(R.id.fullname);
        bio = view.findViewById(R.id.bio);
        edit_profile = view.findViewById(R.id.edit_profile);
        username = view.findViewById(R.id.username);
        options = view.findViewById(R.id.options);
        tabLayout = view.findViewById(R.id.tabs);
        viewPager = view.findViewById(R.id.mainContainerViewPager);


//        my_fotos = view.findViewById(R.id.my_fotos);
//        saved_fotos = view.findViewById(R.id.saved_fotos);
//        recyclerView = view.findViewById(R.id.recycler_view);
//        recyclerView.setHasFixedSize(true);
//        GridLayoutManager gridLayoutManager=new GridLayoutManager(getContext() , 3);
//        recyclerView.setLayoutManager(gridLayoutManager);
//        arrayList=new ArrayList<>();
//        myPosts();
//        adapter=new profilePhotosAdapter(getContext() , arrayList);
//        recyclerView.setAdapter(adapter);

//        recyclerView_saves = view.findViewById(R.id.recycler_view_save);
//        recyclerView_saves.setVisibility(View.GONE);

//        saved_fotos.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                recyclerView.setVisibility(View.GONE);
//                recyclerView_saves.setVisibility(View.VISIBLE);
//            }
//        });

//        recyclerView_saves.setHasFixedSize(true);
//        GridLayoutManager gridLayoutManager2=new GridLayoutManager(getContext() , 3);
//        recyclerView_saves.setLayoutManager(gridLayoutManager2);
//        savedArrayList=new ArrayList<>();
//        savedPosts();
//        savedAdapter=new profilePhotosAdapter(getContext() , savedArrayList);
//        recyclerView_saves.setAdapter(savedAdapter);

        viewPagerConfig(tabLayout, viewPager);
        tabsConfig(tabLayout);
        userInfo();
        numberOfFollowers();
        numberOfFollowing();
        numberOfPosts();


        if (profileId.equals(firebaseUser.getUid())) {
            edit_profile.setText("Edit your profile");
            viewPagerConfig(tabLayout, viewPager);
            tabsConfig(tabLayout);
        }else {
            checkFollowing();
//            fragmentTabLayoutAdapter.removeFragment(1);
//            saved_fotos.setVisibility(View.GONE);
            othersViewPagerConfig(tabLayout, viewPager);
            othersTabsConfig(tabLayout);
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
                nposts.setText("" + i);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void tabsConfig(TabLayout tabLayout) {
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_grid);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_save);

    }

    public void othersTabsConfig(TabLayout tabLayout) {
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_grid);

    }

    public void viewPagerConfig(TabLayout tabLayout, ViewPager viewPager) {
        fragmentTabLayoutAdapter = new FragmentTabLayoutAdapter(getChildFragmentManager());
        fragmentTabLayoutAdapter.addFragment(new myPosts());
        fragmentTabLayoutAdapter.addFragment(new savedPosts());
        viewPager.setAdapter(fragmentTabLayoutAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    public void othersViewPagerConfig(TabLayout tabLayout, ViewPager viewPager) {
        fragmentTabLayoutAdapter = new FragmentTabLayoutAdapter(getChildFragmentManager());
        fragmentTabLayoutAdapter.addFragment(new myPosts());
        viewPager.setAdapter(fragmentTabLayoutAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

//    public void myPosts(){
//        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("posts");
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                arrayList.clear();
//                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
//                    posts posts=snapshot.getValue(com.example.handshop.models.posts.class);
//                    if(posts.getPublisher().equals(profileId)){
//                        arrayList.add(posts);
//                    }
//                }
//                Collections.reverse(arrayList);
//                adapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }
//    public void savedPosts(){
//        savedIds=new ArrayList<>();
//        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("saved").child(firebaseUser.getUid());
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
//                    savedIds.add(snapshot.getKey());
//                }
//                readSaved();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }
//
//    private void readSaved() {
//        DatabaseReference reference2=FirebaseDatabase.getInstance().getReference("posts");
//        reference2.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                savedArrayList.clear();
//                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
//                    posts post=snapshot.getValue(posts.class);
//
//                    for (String id:savedIds){
//                        if(id.equals(post.getPostId())){
//                            savedArrayList.add(post);
//                        }
//                    }
//                }
//                savedAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }
}