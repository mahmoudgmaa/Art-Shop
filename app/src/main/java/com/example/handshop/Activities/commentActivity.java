package com.example.handshop.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.handshop.Adapters.commentsAdapter;
import com.example.handshop.R;
import com.example.handshop.models.User;
import com.example.handshop.models.comment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class commentActivity extends AppCompatActivity {
    private ImageView sendPost;
    private CircleImageView profileImage;
    private EditText addComment;
    private TextView postTheComment;
    private String postId , publisherId;
    private FirebaseUser firebaseUser;
    private RecyclerView commentRecycler;
    private commentsAdapter adapter;
    private ArrayList<comment> commentArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        initiateWidgets();
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        Toolbar toolbar=findViewById(R.id.commentsToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        Intent intent=getIntent();
        postId=intent.getStringExtra("postId");
        publisherId=intent.getStringExtra("publisherId");
        postTheComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(addComment.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext() , "you can't add empty comment" , Toast.LENGTH_SHORT).show();
                }else {
                    addComment();
                }
            }
        });
        getImage();
        commentRecycler.setHasFixedSize(true);
        commentRecycler.setLayoutManager(new LinearLayoutManager(this));
        adapter=new commentsAdapter(this , commentArrayList);
        commentRecycler.setAdapter(adapter);
        readComments();
    }

    private void getImage() {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            User user=dataSnapshot.getValue(User.class);
                Glide.with(getApplicationContext())
                        .load(user.getImgUrl())
                        .into(profileImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addComment() {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("comments").child(postId);
        HashMap<String , Object>map=new HashMap<>();
        map.put("comment" , addComment.getText().toString());
        map.put("publisher" , firebaseUser.getUid());
        reference.push().setValue(map);
        addComment.setText("");
    }

    private void initiateWidgets() {
        sendPost=findViewById(R.id.sharePostFromComments);
        profileImage=findViewById(R.id.commentImageProfile);
        addComment=findViewById(R.id.add_comment);
        postTheComment=findViewById(R.id.postTheComment);
        commentRecycler=findViewById(R.id.commentRecyclerView);
        commentArrayList=new ArrayList<>();
    }
    public void readComments(){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("comments").child(postId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentArrayList.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    comment comment = snapshot.getValue(com.example.handshop.models.comment.class);
                    commentArrayList.add(comment);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}