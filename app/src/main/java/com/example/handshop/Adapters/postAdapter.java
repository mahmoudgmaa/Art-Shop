package com.example.handshop.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.handshop.R;
import com.example.handshop.models.User;
import com.example.handshop.Activities.commentActivity;
import com.example.handshop.models.posts;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class postAdapter extends RecyclerView.Adapter<postAdapter.viewHolder>{
    private Context mContext;
    private ArrayList<posts> postList;
    private FirebaseUser firebaseUser;

    public postAdapter(Context mContext, ArrayList<posts> postList) {
        this.mContext = mContext;
        this.postList = postList;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.post_item , parent , false);
        viewHolder holder=new viewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final viewHolder holder, int position) {
        final FirebaseUser currentUser= FirebaseAuth.getInstance().getCurrentUser();
        final posts post=postList.get(position);
        Glide.with(mContext).load(post.getPostImage()).into(holder.postImage);
        if (post.getDescription()== ""){
            holder.description.setVisibility(View.GONE);
        }else{
            holder.description.setVisibility(View.VISIBLE);
            holder.description.setText(post.getDescription());
        }
        publisherInfo(holder.publisher , holder.username , holder.imageProfile , post.getPublisher());
        likeSetUp(post.getPostId() , holder.like , holder.likes);
        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.like.getTag().equals("like")){
                    FirebaseDatabase.getInstance().getReference().child("likes").child(post.getPostId())
                            .child(currentUser.getUid()).setValue(true);
                }else if(holder.like.getTag().equals("liked")){
                    FirebaseDatabase.getInstance().getReference().child("likes").child(post.getPostId())
                            .child(currentUser.getUid()).removeValue();
                }
            }
        });
        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, commentActivity.class);
                intent.putExtra("postId", post.getPostId());
                intent.putExtra("publisher", post.getPublisher());
                mContext.startActivity(intent);
            }
        });

        getComments(post.getPostId(), holder.comments);

        isSaved(post.getPostId(), holder.save);
        holder.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.save.getTag().equals("save")) {
                    FirebaseDatabase.getInstance().getReference().child("saved").child(currentUser.getUid())
                            .child(post.getPostId()).setValue(true);
                } else {
                    FirebaseDatabase.getInstance().getReference().child("saved").child(currentUser.getUid())
                            .child(post.getPostId()).removeValue();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{
        private ImageView more ,postImage , like , comment , save ;
        private CircleImageView imageProfile;
        private TextView username , likes , publisher , description , comments;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            more=itemView.findViewById(R.id.more);
            postImage=itemView.findViewById(R.id.post_image);
            like=itemView.findViewById(R.id.like);
            comment=itemView.findViewById(R.id.comment);
            save=itemView.findViewById(R.id.save);
            imageProfile=itemView.findViewById(R.id.image_profile);
            username=itemView.findViewById(R.id.usernamePost);
            likes=itemView.findViewById(R.id.likes);
            publisher=itemView.findViewById(R.id.publisher);
            description=itemView.findViewById(R.id.postDescription);
            comments=itemView.findViewById(R.id.comments);

        }
    }

    private void getComments(String postId , final TextView comments){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("comments").child(postId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                comments.setText("View All "+dataSnapshot.getChildrenCount()+" comments");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void likeSetUp(final String postID , final ImageView likeImage , final TextView likesNum ){
        final FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("likes").child(postID);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                likesNum.setText(dataSnapshot.getChildrenCount()+" likes");

                if(dataSnapshot.child(firebaseUser.getUid()).exists()){
                    likeImage.setImageResource(R.drawable.ic_liked);
                    likeImage.setTag("liked");
                }else {
                    likeImage.setImageResource(R.drawable.ic_like);
                    likeImage.setTag("like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void publisherInfo(final TextView publisher , final TextView username , final CircleImageView imageProfile, final String userId){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("users").child(userId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                publisher.setText(user.getUserName());
                username.setText(user.getUserName());
                Glide.with(mContext)
                        .load(user.getImgUrl())
                        .into(imageProfile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void isSaved(final String postId, final ImageView save) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("saved").child(user.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(postId).exists()) {
                    save.setTag("saved");
                    save.setImageResource(R.drawable.ic_save_black);
                } else {
                    save.setTag("save");
                    save.setImageResource(R.drawable.ic_savee_black);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
