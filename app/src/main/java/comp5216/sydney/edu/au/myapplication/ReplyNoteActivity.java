package comp5216.sydney.edu.au.myapplication;

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

public class ReplyNoteActivity extends Activity {
    // Initializing variable
    private DatabaseReference database;
    private FirebaseUser mAuth;
    ListView listView;
    public int position=0;
    TextView Title,Note;
    EditText editNote;
    ArrayList<Reply> orderedItems;
    ReplyAdapter itemsAdapter;
    Note note;
    Reply reply = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //populate the screen using the layout

        mAuth = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance().getReference();
        setContentView(R.layout.activity_reply_note);
        //Get the data from the main screen
        note = (Note) getIntent().getSerializableExtra("note");
        orderedItems = new ArrayList<Reply>();
        Title = (TextView)findViewById(R.id.title);
        Note = (TextView)findViewById(R.id.note);
        position = getIntent().getIntExtra("position",-1);
        listView = findViewById(R.id.replys);
        // show original content or hint in the text field
        ValueEventListener noteListener = new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("reply1", "Here!!!");
                // Get Post object and use the values to update the UI
                for (DataSnapshot noteSnapshot: dataSnapshot.getChildren()) {
                    Reply reply = noteSnapshot.getValue(Reply.class);
                    orderedItems.add(0,reply);
                }
                itemsAdapter = new ReplyAdapter(this, R.layout.reply_layout, orderedItems);
                listView.setAdapter(itemsAdapter);
                Log.d("reply2", "Here!!!");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("error", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        Log.d("reply", "name:"+note.getName());
        Query allQuery = database.child("replys").orderByChild("ownerName").equalTo(note.getName());
        allQuery.addListenerForSingleValueEvent(noteListener);
        Title.setText(note.getTitle());
        Note.setText(note.getContent());
    }

    public void onSubmit(View v) {
        if(reply != null) {
            // Prepare data intent for sending it back
            Intent data = new Intent();
            // Pass relevant data back as a result
            editNote = (EditText)findViewById(R.id.editReply);
            data.putExtra("name", reply.getName());
            data.putExtra("reply", reply.getContent());
            data.putExtra("createTime", reply.getData());
            data.putExtra("ownerName", reply.getOwnerName());
            data.putExtra("ownerID", reply.getOwnerID());

            data.putExtra("position", position);
            // Set result code and bundle data for response
            setResult(RESULT_OK, data);
            // Closes the activity, pass data to parent
            finish();
        }
        else Toast.makeText(this, "Please click post first", Toast.LENGTH_SHORT).show();
    }

    public void Post(View v) {
        Long createTime = new Date().getTime();
        editNote = (EditText)findViewById(R.id.editReply);
        reply = new Reply(createTime+" : "+mAuth.getUid(),editNote.getText().toString(),createTime,note.getName(),mAuth.getUid());
        Log.d("reply3", " reply: "+reply);
        orderedItems.add(0,reply);
        itemsAdapter.notifyDataSetChanged();
    }


    // When user give up edit make a dialog
    public void Cancel(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ReplyNoteActivity.this);
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
