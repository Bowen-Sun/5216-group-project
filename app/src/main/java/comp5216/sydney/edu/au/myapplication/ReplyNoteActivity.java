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

import java.util.ArrayList;
import java.util.Date;

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
                orderedItems.clear();
                // Get Post object and use the values to update the UI
                for (DataSnapshot noteSnapshot: dataSnapshot.getChildren()) {
                    Reply reply = noteSnapshot.getValue(Reply.class);
                    orderedItems.add(0,reply);
                }
                itemsAdapter = new ReplyAdapter(this, R.layout.reply_layout, orderedItems);
                listView.setAdapter(itemsAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("error", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        Query allQuery = database.child("replys").orderByChild("ownerName").equalTo(note.getName());
        allQuery.addValueEventListener(noteListener);
        Title.setText(note.getTitle());
        Note.setText(note.getContent());
    }

    public void Post(View v) {
        Long createTime = new Date().getTime();
        editNote = (EditText)findViewById(R.id.editReply);
        if (!editNote.getText().toString().equals("")){
            reply = new Reply(createTime+" : "+mAuth.getUid(),editNote.getText().toString(),createTime,note.getName(),mAuth.getUid());
            orderedItems.add(0,reply);
            itemsAdapter.notifyDataSetChanged();
            database.child("replys").child(reply.getName()).setValue(reply);
            editNote.setText("");
        }

    }


    // When user give up edit make a dialog
    public void Cancel(View v) {
        Intent intent = new Intent(ReplyNoteActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
