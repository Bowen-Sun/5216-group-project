package comp5216.sydney.edu.au.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import comp5216.sydney.edu.au.myapplication.adapter.NotesAdapter;
import comp5216.sydney.edu.au.myapplication.adapter.ReplyAdapter;
import comp5216.sydney.edu.au.myapplication.notes.Note;
import comp5216.sydney.edu.au.myapplication.notes.Reply;

public class ShowNotesAndRepliesActivity extends AppCompatActivity {
    private FirebaseUser mAuth;
    private DatabaseReference storageRef;
    private File localFile;
    ListView Notes,Replies;
    ArrayList<Note> ownNotes;
    ArrayList<Reply> ownReplys;
    NotesAdapter notesAdapter;
    ReplyAdapter replyAdapter;
    private DatabaseReference database;
    int EditNote = 1;
    int EditReply = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("show", "Here1");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_notes);
        Notes = findViewById(R.id.ShowNotes);
        Replies = findViewById(R.id.ShowReplies);
        mAuth = FirebaseAuth.getInstance().getCurrentUser();
        storageRef = FirebaseDatabase.getInstance().getReference();
        ownNotes = new ArrayList<Note>();
        ownReplys = new ArrayList<Reply>();
        database = FirebaseDatabase.getInstance().getReference();
        Log.d("show", "User"+mAuth.getUid());
        ValueEventListener noteListener = new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("show", "Here!!!");
                // Get Post object and use the values to update the UI
                for (DataSnapshot noteSnapshot: dataSnapshot.getChildren()) {
                    Note note = noteSnapshot.getValue(Note.class);
                    Log.d("show", "Here2");
                    ownNotes.add(note);
                    Log.d("show", "notes: "+ ownNotes);
                    Log.d("show", "note: "+ note);
                    Log.d("show", "noteTitle: "+ note.getTitle());
                }
                notesAdapter = new NotesAdapter(this, R.layout.notes_layout, ownNotes);
                Notes.setAdapter(notesAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("error", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        Log.d("show", "Here3");
        Query noteQuery1 = storageRef.child("notes").child("ownerID").equalTo(mAuth.getUid());
        Query noteQuery2 = storageRef.child("notes").orderByChild("ownerID").equalTo(mAuth.getUid());
        Log.d("show", "Here3 +"+noteQuery1);
        Log.d("show", "Here3 +"+noteQuery2);
        noteQuery2.addListenerForSingleValueEvent(noteListener);


        ValueEventListener replyListener = new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("show", "Here6");
                // Get Post object and use the values to update the UI
                for (DataSnapshot noteSnapshot: dataSnapshot.getChildren()) {
                    Log.d("show", "Here4");
                    Reply reply = noteSnapshot.getValue(Reply.class);
                    ownReplys.add(reply);
                }
                replyAdapter = new ReplyAdapter(this, R.layout.reply_layout, ownReplys);
                Replies.setAdapter(replyAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("error", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        Log.d("show", "Here5");
        Query ReplyQuery = storageRef.child("replys").orderByChild("ownerID").equalTo(mAuth.getUid());
        ReplyQuery.addListenerForSingleValueEvent(replyListener);
        Log.d("show", "Here5 +"+ReplyQuery);
        Log.d("show", "Here5 +"+ReplyQuery);
        setupNotesListViewListener();
    }

    private void setupNotesListViewListener() {
        // Short click to edit item
        Notes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Note updateNote = (Note) notesAdapter.getItem(i);

                Intent intent = new Intent(ShowNotesAndRepliesActivity.this, EditNotesAndRepliesActivity.class);
                // put "extras" into the bundle for access in the edit activity
                intent.putExtra("note", (Serializable) updateNote);
                intent.putExtra("position", i);
                // brings up the second activity
                startActivityForResult(intent, EditNote);
                // Notify listView adapter to update the list
                notesAdapter.notifyDataSetChanged();
            }});

        Notes.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int
                    position, long rowId)
            {
                Log.i("MainActivity", "Long Clicked item " + position);
                // Build a alert dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(ShowNotesAndRepliesActivity.this);
                // Set text for the dialog
                builder.setTitle(R.string.dialog_delete_title);
                builder.setMessage(R.string.dialog_delete_msg);
                builder.setPositiveButton(R.string.delete, new
                        DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Note deleteNote = ownNotes.get(position);
                                ownNotes.remove(position);
                                notesAdapter.notifyDataSetChanged();
                                database.child("notes").child(deleteNote.getName()).removeValue();
                                database.child("users").child(deleteNote.getOwnerID()).child("questions").child(deleteNote.getName()).removeValue();
                            }});
                builder.setNegativeButton(R.string.cancel, new
                        DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User cancelled the dialog
                                // Nothing happens
                            }});
                // Show dialog
                builder.create().show();
                return true;
            }});

        Replies.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Reply updateReply = (Reply) replyAdapter.getItem(i);

                Intent intent = new Intent(ShowNotesAndRepliesActivity.this, EditNotesAndRepliesActivity.class);
                // put "extras" into the bundle for access in the edit activity
                intent.putExtra("reply", (Serializable) updateReply);
                intent.putExtra("position", i);
                // brings up the second activity
                startActivityForResult(intent, EditReply);
                // Notify listView adapter to update the list
                notesAdapter.notifyDataSetChanged();
            }});

        Replies.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int
                    position, long rowId)
            {
                Log.i("MainActivity", "Long Clicked item " + position);
                // Build a alert dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(ShowNotesAndRepliesActivity.this);
                // Set text for the dialog
                builder.setTitle(R.string.dialog_delete_title);
                builder.setMessage(R.string.dialog_delete_msg);
                builder.setPositiveButton(R.string.delete, new
                        DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Reply deleteReply = ownReplys.get(position);
                                ownReplys.remove(position);
                                replyAdapter.notifyDataSetChanged();
                                database.child("replys").child(deleteReply.getName()).removeValue();
                            }});
                builder.setNegativeButton(R.string.cancel, new
                        DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User cancelled the dialog
                                // Nothing happens
                            }});
                // Show dialog
                builder.create().show();
                return true;
            }});
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // When edit item
        if (requestCode == EditNote) {
            if (resultCode == RESULT_OK) {
                Note editedNote = (Note) data.getSerializableExtra("note");
                database.child("notes").child(editedNote.getName()).setValue(editedNote);
                database.child("users").child(editedNote.getOwnerID()).child("questions").child(editedNote.getName()).setValue(editedNote);
                int position = data.getIntExtra("position", -1);
                ownNotes.set(position,editedNote);
                notesAdapter.notifyDataSetChanged();
            }
        }

        else if(requestCode == EditReply){
            if (resultCode == RESULT_OK) {
                Reply editedReply = (Reply) data.getSerializableExtra("reply");
                database.child("replys").child(editedReply.getName()).setValue(editedReply);
                int position = data.getIntExtra("position", -1);
                ownReplys.set(position,editedReply);
                replyAdapter.notifyDataSetChanged();
            }
        }
    }
}