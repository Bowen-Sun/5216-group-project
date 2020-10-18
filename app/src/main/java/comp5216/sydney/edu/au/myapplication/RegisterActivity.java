package comp5216.sydney.edu.au.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import comp5216.sydney.edu.au.myapplication.users.UserModel;

public class RegisterActivity extends Activity {

    private static final String APP_TAG = "Camera";
    String photoFileName = "photo.jpg";
    EditText id,name,password;
    ImageView profile;
    Button btn;
    Uri imageUri = null;
    private File file;
    MarshmallowPermission marshmallowPermission;
    private static final int MY_PERMISSIONS_REQUEST_OPEN_CAMERA = 101;
    private static final int MY_PERMISSIONS_REQUEST_READ_PHOTOS = 102;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        id=findViewById(R.id.id);
        password=findViewById(R.id.password);
        name=findViewById(R.id.name);
        profile=findViewById(R.id.img_profile);
        btn=findViewById(R.id.signup);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signup();
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upload();
                Log.d("Test6", "66666666");
            }
        });

        marshmallowPermission = new MarshmallowPermission(this);
    }

    private void upload() {
        Toast.makeText(RegisterActivity.this,"photo error",Toast.LENGTH_SHORT).show();
        Log.d("Test1", "1111111");
        if (!marshmallowPermission.checkPermissionForCamera()
                || !marshmallowPermission.checkPermissionForExternalStorage()) {
            marshmallowPermission.requestPermissionForCamera();
        }

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Log.d("Test2", "22222222");
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        photoFileName = "IMG_" + timeStamp + ".jpg";
        // Create a photo file reference
        Uri file_uri = getFileUri(photoFileName);
        Log.d("Test3", "uri1: "+imageUri);
        imageUri = file_uri;
        Log.d("Test3", "uri2: "+imageUri);
        // Add extended data to the intent
        intent.putExtra(MediaStore.EXTRA_OUTPUT, file_uri);
        Log.d("Test4", "444444444");

        if (intent.resolveActivity(getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, MY_PERMISSIONS_REQUEST_OPEN_CAMERA);
            Log.d("Test5", "555555555");

        }
    }

    private void signup() {
        Log.d("Test7", "7777777");
        Log.d("Test8", "uri2: "+id.getText());
        Log.d("Test9", "uri2: "+password.getText());
        Log.d("Test10", "uri: "+imageUri);
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(id.getText().toString(),password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        Log.d("Test11", "!!!!!!!!");
                        final String uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
                        final StorageReference storageReference= FirebaseStorage.getInstance().getReference().child("users").child(uid);
                        storageReference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                Log.d("Test12", "uid:"+uid);
                                if (task.isSuccessful()){
                                    Log.d("Test13", "getDownload");
                                     storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                         @Override
                                         public void onComplete(@NonNull Task<Uri> task) {
                                             Log.d("Test14", "getDownloadSuccess");
                                             String imageurl=task.toString();
                                             UserModel userModel=new UserModel(uid,imageurl,name.getText().toString());
                                             FirebaseDatabase.getInstance().getReference().child("users").child(uid).setValue(userModel);
                                             Log.d("Test14", "userName"+name.getText().toString());
                                             Log.d("Test14", "imageurl"+imageurl);
                                             Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                             startActivity(intent);
                                         }
                                     });
                                }else{
                                    Log.d("Test14", "!!!!!!!!");
                                    Toast.makeText(RegisterActivity.this,"error2",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }else{
                        Log.d("Test15", "!!!!!!!!");
                        Toast.makeText(RegisterActivity.this,"error3",Toast.LENGTH_SHORT).show();

                    }
                }
            });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("Test", " "+resultCode);
        if  (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(file.getAbsolutePath());
                // Load the taken image into a preview
                profile.setImageBitmap(takenImage);

            Log.d("Test", "result "+imageUri);
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken AAA!",
                        Toast.LENGTH_SHORT).show();
            }
        }



    public Uri getFileUri(String fileName) {
        Uri fileUri = null;

        String typestr = "/images/"; //default to images type
        // Get safe storage directory depending on type
        File mediaStorageDir = new
                File(this.getExternalFilesDir(null).getAbsolutePath(), typestr+fileName);
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.getParentFile().exists() && !mediaStorageDir.getParentFile().mkdirs()) {
            Log.d(APP_TAG, "failed to create directory");
        }
        // Create the file target for the media based on filename
        file = new File(mediaStorageDir.getParentFile().getPath() + File.separator + fileName);
        // Wrap File object into a content provider, required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-withIntents#sharing-files-with-api-24-or-higher
        if (Build.VERSION.SDK_INT >= 24) {
            fileUri = FileProvider.getUriForFile(
                    this.getApplicationContext(),
                    "comp5216.sydney.edu.au.myapplication.fileProvider", file);
        } else {
            fileUri = Uri.fromFile(mediaStorageDir);
        }
        Log.d("Test", " "+fileUri);
        return fileUri;
    }

}
