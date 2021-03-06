package com.example.handshop.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.handshop.Fragments.postDetailsFragment;
import com.example.handshop.R;
import com.example.handshop.models.posts;

import java.util.ArrayList;

public class profilePhotosAdapter extends RecyclerView.Adapter<profilePhotosAdapter.viewHolder> {
    private Context mContext;
    private ArrayList<posts> arrayList;

    public profilePhotosAdapter(Context mContext, ArrayList<posts> arrayList) {
        this.mContext = mContext;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.profile_photo_item, parent ,false);
        viewHolder holder=new viewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        final posts post=arrayList.get(position);
        Glide.with(mContext)
                .load(post.getPostImage())
                .into(holder.imageView);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor=mContext.getSharedPreferences("PREFS" , mContext.MODE_PRIVATE).edit();
                editor.putString("postId" , post.getPostId());
                editor.apply();
                ((FragmentActivity)mContext).getSupportFragmentManager()
                        .beginTransaction().replace(R.id.mainContainer , new postDetailsFragment()).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{
        private ImageView imageView;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.post_image);
        }
    }
}
