package com.example.handshop.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.handshop.R;
import com.example.handshop.models.User;
import com.example.handshop.models.comment;
import com.example.handshop.Activities.secondMainAc;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class commentsAdapter extends RecyclerView.Adapter<commentsAdapter.viewHolder> {
    private Context mContext;
    private ArrayList<comment> commentList;
    private FirebaseUser firebaseUser;
    private FirebaseUser currentUser;

    public commentsAdapter(Context mContext, ArrayList<comment> commentList) {
        this.mContext = mContext;
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.comment_item , parent , false);
        viewHolder holder=new viewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        currentUser=FirebaseAuth.getInstance().getCurrentUser();
        final comment comment=commentList.get(position);
        holder.comment.setText(comment.getComment());
        getUserInfo(holder.username , holder.profileImg , comment.getPublisher());
        holder.profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(mContext , secondMainAc.class);
                intent.putExtra("publisherId" , comment.getPublisher());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{
        private CircleImageView profileImg;
        private TextView username , comment;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            profileImg=itemView.findViewById(R.id.image_profileInComments);
            username=itemView.findViewById(R.id.usernameInComments);
            comment=itemView.findViewById(R.id.theComment);
        }
    }

    public void getUserInfo(final TextView username ,final CircleImageView profileImage , String publisherId){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("users").child(publisherId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                username.setText(user.getUserName());
                Glide.with(mContext)
                        .load(user.getImgUrl())
                        .into(profileImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
