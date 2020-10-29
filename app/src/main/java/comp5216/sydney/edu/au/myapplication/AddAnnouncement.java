package comp5216.sydney.edu.au.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class AddAnnouncement extends AppCompatActivity {
    EditText Title,Note;
    private int position;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_announcement);
    }

    public void submit(View view){
        Title = findViewById(R.id.announcementTitle);
        Note = findViewById(R.id.announcementNote);
        position = getIntent().getIntExtra("position",-1);
        Intent data = new Intent();
        // Pass relevant data back as a result
        data.putExtra("title", Title.getText().toString());
        data.putExtra("note", Note.getText().toString());
        data.putExtra("position", position);
        // Set result code and bundle data for response
        setResult(RESULT_OK, data);
        // Closes the activity, pass data to parent
        finish();
    }
}