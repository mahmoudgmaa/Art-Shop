package com.example.handshop.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.handshop.R;
import com.example.handshop.models.User;
import com.example.handshop.Fragments.profileFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class searchUserAdapter extends RecyclerView.Adapter<searchUserAdapter.viewHolder> {
    private List<User> usersList;
    private Context mcontext;
    private FirebaseUser firebaseUser;
    private boolean isFragment;
    private String TAG = "search adapter";

    public searchUserAdapter(List<User> usersList, Context mcontext) {
        this.usersList = usersList;
        this.mcontext = mcontext;
    }


    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mcontext).inflate(R.layout.search_item_layout, parent, false);
        viewHolder holder = new viewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final viewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final User user = usersList.get(position);
        holder.profileSearchFullName.setText(user.getFullName());
        holder.ProfileSearchUsername.setText(user.getUserName());
        holder.SearchFollowButton.setVisibility(View.VISIBLE);
        isFollowing(user.getId(), holder.SearchFollowButton);

        Glide.with(mcontext)
                .load(user.getImgUrl())
                .into(holder.profileSearchImage);

        if (user.getId().equals(firebaseUser.getUid())) {
            holder.SearchFollowButton.setVisibility(View.GONE);
      }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("recentSearch").child(firebaseUser.getUid());

                HashMap<String ,Object> map=new HashMap<>();
                map.put("id" , user.getId());
                map.put("firstName", user.getFirstName());
                map.put("lastName", user.getLastName());
                map.put("userName" , user.getUserName());
                map.put("email", user.getEmail());
                map.put("fullName", user.getFullName());
                map.put("imgUrl", user.getImgUrl());
                reference.setValue(map);

                Log.d(TAG, "onClick: itemClickListener");
                SharedPreferences.Editor editor = mcontext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("profileId", user.getId());
                editor.apply();
                ((FragmentActivity) mcontext).getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer
                        , new profileFragment()).commit();
            }
        });

        holder.SearchFollowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"onClick: buttonClickListenerDec");
                if (holder.SearchFollowButton.getText().equals("Follow")) {
                    FirebaseDatabase.getInstance().getReference().child("follow").child(firebaseUser.getUid()).child("following")
                            .child(user.getId()).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("follow").child(user.getId()).child("followers")
                            .child(firebaseUser.getUid()).setValue(true);
                } else {
                    FirebaseDatabase.getInstance().getReference().child("follow").child(firebaseUser.getUid()).child("following")
                            .child(user.getId()).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("follow").child(user.getId()).child("followers")
                            .child(firebaseUser.getUid()).removeValue();
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        public CircleImageView profileSearchImage;
        public TextView ProfileSearchUsername;
        public TextView profileSearchFullName;
        public Button SearchFollowButton;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            profileSearchFullName = itemView.findViewById(R.id.searchProfileFullName);
            profileSearchImage = itemView.findViewById(R.id.searchProfileImage);
            ProfileSearchUsername = itemView.findViewById(R.id.searchProfileUserName);
            SearchFollowButton=itemView.findViewById(R.id.followButton);
        }
    }


    public void isFollowing(final String userId, final Button followButton) {
        Log.d(TAG, "isFollowing: isfollowingfunction");
        final FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("follow").child(firebaseUser.getUid())
                .child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(userId).exists()) {
                    followButton.setBackgroundResource(R.drawable.button2);
                    followButton.setText("Following");
                } else {
                    followButton.setText("Follow");
                    followButton.setBackgroundResource(R.drawable.button);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
