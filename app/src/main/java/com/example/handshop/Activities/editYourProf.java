package com.example.handshop.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.handshop.R;
import com.example.handshop.models.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.concurrent.ScheduledExecutorService;

public class editYourProf extends AppCompatActivity {
    private ImageView editProfileImage,profileImage,close;
    private MaterialEditText usernameEditText,bioEditText,websiteEditText,profileFullName;
    private Button save;
    private Uri imgURi;
    private StorageTask uploadTask;
    private StorageReference storageReference;
    private FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_your_prof);
        initiateWidgets();
        storageReference= FirebaseStorage.getInstance().getReference("uploads");
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                profileFullName.setText(user.getFullName());
                usernameEditText.setText(user.getUserName());
                bioEditText.setText(user.getBio());
                websiteEditText.setText(user.getWebsite());
                Glide.with(getApplicationContext())
                        .load(user.getImgUrl())
                        .into(profileImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        editProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setAspectRatio(1,1)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .start(editYourProf.this);
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile(profileFullName.getText().toString()
                ,usernameEditText.getText().toString()
                ,bioEditText.getText().toString()
                ,websiteEditText.getText().toString());
                finish();
            }
        });
    }

    private void updateProfile(String fullName, String username, String bio, String website) {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
        HashMap<String , Object> hashMap=new HashMap<>();
        hashMap.put("fullName" , fullName);
        hashMap.put("userName" , username);
        hashMap.put("Bio",bio);
        hashMap.put("website" , website);
        reference.updateChildren(hashMap);
    }
    public String getFileExtension(Uri uri){
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap map=MimeTypeMap.getSingleton();
        return map.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    public void uploadImage(){
        final ProgressDialog dialog=new ProgressDialog(this);
        dialog.setMessage("Updating");
        dialog.show();
        if(imgURi!=null){
            final StorageReference reference= storageReference.child(System.currentTimeMillis()+""+getFileExtension(imgURi));
            uploadTask=reference.putFile(imgURi);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return reference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri downloadUri=task.getResult();
                        String Uri=downloadUri.toString();
                        DatabaseReference reference1=FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
                        HashMap<String , Object> hashMap=new HashMap<>();
                        hashMap.put("imgUrl",Uri);
                        reference1.updateChildren(hashMap);
                        dialog.dismiss();
                    }else {
                        Toast.makeText(editYourProf.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(editYourProf.this ,e.getMessage() , Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            Toast.makeText(editYourProf.this, "no image selected", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK){
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            imgURi=result.getUri();
            uploadImage();
        }else {
            Toast.makeText(this, "Something gone wrong!", Toast.LENGTH_SHORT).show();

        }
    }

    public void initiateWidgets(){
        editProfileImage=findViewById(R.id.editProfileImage);
        profileImage=findViewById(R.id.profileImage);
        close=findViewById(R.id.backFromEditDetails);
        profileFullName=findViewById(R.id.profileFullName);
        usernameEditText=findViewById(R.id.usernameEditText);
        bioEditText=findViewById(R.id.bioEditText);
        websiteEditText=findViewById(R.id.websiteEditText);
        save=findViewById(R.id.saveEditing);
    }

}