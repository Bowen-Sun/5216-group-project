package comp5216.sydney.edu.au.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import comp5216.sydney.edu.au.myapplication.adapter.NotesAdapter;
import comp5216.sydney.edu.au.myapplication.adapter.ReplyAdapter;
import comp5216.sydney.edu.au.myapplication.notes.Note;
import comp5216.sydney.edu.au.myapplication.notes.Reply;
import android.os.Bundle;

public class EditNotesAndRepliesActivity extends AppCompatActivity {
    private DatabaseReference database;
    private FirebaseUser mAuth;
    ListView listView;
    public int position;
    TextView Title,Note;
    EditText editTitle,editNote;

    Note note = null;
    Reply reply = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_notes_and_replies);
        Title = findViewById(R.id.textView1);
        Note = findViewById(R.id.textView2);
        editTitle = findViewById(R.id.editTitle);
        editNote = findViewById(R.id.editNote);
        position = getIntent().getIntExtra("position",-1);
        note = (Note) getIntent().getSerializableExtra("note");
        reply = (Reply) getIntent().getSerializableExtra("reply");
        if(note == null){
            Title.setVisibility(View.GONE);
            editTitle.setVisibility(View.GONE);
        }


    }

    public void onSubmit(View v) {
        if(note != null && reply == null) {
            // Prepare data intent for sending it back
            Intent data = new Intent();
            // Pass relevant data back as a result
            note.setTitle(editTitle.getText().toString());
            note.setContent(editNote.getText().toString());
            data.putExtra("note", note);
            data.putExtra("position", position);
            // Set result code and bundle data for response
            setResult(RESULT_OK, data);
            // Closes the activity, pass data to parent
            finish();
        }
        else if(reply != null && note == null){
            Intent data = new Intent();
            // Pass relevant data back as a result
            reply.setContent(editNote.getText().toString());
            data.putExtra("reply", reply);
            data.putExtra("position", position);
            // Set result code and bundle data for response
            setResult(RESULT_OK, data);
            // Closes the activity, pass data to parent
            finish();
        }
    }

    // When user give up edit make a dialog
    public void Cancel(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditNotesAndRepliesActivity.this);
        builder.setTitle(R.string.do_not_save_title);
        builder.setMessage(R.string.do_not_save_message);
        builder.setPositiveButton(R.string.do_not_save, new
                DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User cancel the editing and go back to main activity
                        finish();
                    }});
        builder.setNegativeButton(R.string.save, new
                DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User cancelled the dialog
                        // Nothing happens
                    }});
        builder.create().show();
    }
}