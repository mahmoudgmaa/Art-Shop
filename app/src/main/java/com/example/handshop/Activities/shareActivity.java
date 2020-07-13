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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.handshop.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

public class shareActivity extends AppCompatActivity {
    private Uri imgUrl;
    private String myUrl="";
    private StorageTask uploadTask;
    private StorageReference storageReference;
    private ImageView close , addedImg;
    private TextView post;
    private EditText description;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
       initiateWidgets();
       storageReference= FirebaseStorage.getInstance().getReference("posts");
       close.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               startActivity(new Intent(shareActivity.this , MainActivity.class));
               finish();
           }
       });
       post.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               uploadImage();
           }
       });
        CropImage.activity()
                .setAspectRatio(1 , 1)
                .start(shareActivity.this);

    }



    private void uploadImage() {
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("posting");
        progressDialog.show();
        if (imgUrl!=null){
            final StorageReference fileReference= storageReference
                    .child(System.currentTimeMillis()+"."+getFileExtension(imgUrl));
            uploadTask=fileReference.putFile(imgUrl);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri newUri=task.getResult();
                        myUrl=newUri.toString();
                        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("posts");
                        String postId=reference.push().getKey();
                        HashMap<String , Object> hashMap=new HashMap<>();
                        hashMap.put("postId" , postId);
                        hashMap.put("postImage",myUrl);
                        hashMap.put("description" , description.getText().toString());
                        hashMap.put("publisher" , FirebaseAuth.getInstance().getCurrentUser().getUid());
                        reference.child(postId).setValue(hashMap);
                        progressDialog.dismiss();
                        startActivity(new Intent(shareActivity.this , MainActivity.class));
                        finish();
                    }else {
                        Toast.makeText(shareActivity.this , "Failed" , Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(shareActivity.this , ""+e.getMessage() , Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Toast.makeText(shareActivity.this , "No image selected" , Toast.LENGTH_SHORT).show();
        }
    }

    private void initiateWidgets() {
        close=findViewById(R.id.closeShareIntent);
        addedImg=findViewById(R.id.imgWillBeShared);
        post=findViewById(R.id.postText);
        description=findViewById(R.id.description);
    }
    public String getFileExtension(Uri uri){
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK){
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            imgUrl=result.getUri();
            addedImg.setImageURI(imgUrl);
        }else {
            Toast.makeText(getApplicationContext() , "something went wrong" ,Toast.LENGTH_SHORT );
            startActivity(new Intent(shareActivity.this , MainActivity.class));
        }
    }
}