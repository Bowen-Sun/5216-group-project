package comp5216.sydney.edu.au.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

/**
 * Add notes here
 *
 * @author Bowen Sun
 */

public class AddNote extends AppCompatActivity {
    EditText Title,Note;
    private int position;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

    }

    public void submit(View view){
        Title = findViewById(R.id.title);
        Note = findViewById(R.id.note);
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

    public void goBack(View view){
        Intent intent=new Intent(AddNote.this, MainActivity.class);
        startActivity(intent);
    }
}