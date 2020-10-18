package comp5216.sydney.edu.au.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.IOException;


public class UserProfile extends AppCompatActivity {
    private FirebaseUser mAuth;
    private StorageReference storageRef;
    private FirebaseStorage storage;
    private File localFile;
    TextView name;
    ImageView photo;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile_layout);

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        mAuth = FirebaseAuth.getInstance().getCurrentUser();
        Log.d("User","userID: "+mAuth.getUid());
        name = findViewById(R.id.name);
        photo = findViewById(R.id.photo);
        name.setText(mAuth.getUid());

        StorageReference photoRef = storageRef.child("users/"+mAuth.getUid());

        try {
            localFile = File.createTempFile("images", ".jpg");
            Log.d("User","File: "+localFile);
            photoRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Log.d("User","success: ");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.d("User","fail1: ");
                }
            }).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                    Bitmap takenImage = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    photo.setImageBitmap(takenImage);
                }
            });

        } catch (IOException e) {
            Log.d("User","fail2: ");
            e.printStackTrace();
        }



    }

    public void showNotes(View view){
        Intent intent=new Intent(UserProfile.this, ShowNotesAndRepliesActivity.class);
        startActivity(intent);
    }

    public void goBack(View view){
        Intent intent=new Intent(UserProfile.this, MainActivity.class);
        startActivity(intent);
    }
}
