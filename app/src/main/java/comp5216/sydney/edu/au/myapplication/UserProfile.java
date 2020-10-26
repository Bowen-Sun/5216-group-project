package comp5216.sydney.edu.au.myapplication;

import android.content.DialogInterface;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.IOException;

import comp5216.sydney.edu.au.myapplication.notes.Note;
import comp5216.sydney.edu.au.myapplication.users.UserModel;


public class UserProfile extends AppCompatActivity {
    private FirebaseUser mAuth;
    private StorageReference storageRef;
    private FirebaseStorage storage;
    private DatabaseReference database;
    private File localFile;
    TextView name;
    ImageView photo;
    UserModel user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile_layout);
        database = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        mAuth = FirebaseAuth.getInstance().getCurrentUser();
        Log.d("User","userID: "+mAuth.getUid());
        name = findViewById(R.id.name);
        photo = findViewById(R.id.photo);


        StorageReference photoRef = storageRef.child("users/"+mAuth.getUid());

        try {
            localFile = File.createTempFile("images", ".jpg");
            Log.d("User","File: "+localFile);
            photoRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Log.d("User1111111","success: ");
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

        ValueEventListener userListener = new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                    user = userSnapshot.getValue(UserModel.class);
                    name.setText(user.getName());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("error", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        Query UserQuery = database.child("users").orderByChild("uid").equalTo(mAuth.getUid());
        UserQuery.addListenerForSingleValueEvent(userListener);



    }



    public void showNotes(View view){
        Intent intent=new Intent(UserProfile.this, ShowNotesAndRepliesActivity.class);
        startActivity(intent);
    }

    public void goBack(View view){
        Intent intent=new Intent(UserProfile.this, MainActivity.class);
        startActivity(intent);
    }

    public void logOut(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfile.this);
        // Set text for the dialog
        builder.setTitle(R.string.log_out);
        builder.setMessage(R.string.log_out_message);
        builder.setPositiveButton(R.string.log_out, new
                DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent=new Intent(UserProfile.this, LoginActivity.class);
                        startActivity(intent);
                    }});
        builder.setNegativeButton(R.string.cancel, new
                DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User cancelled the dialog
                        // Nothing happens
                    }});
        // Show dialog
        builder.create().show();
    }
}
