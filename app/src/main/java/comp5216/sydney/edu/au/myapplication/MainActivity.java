package comp5216.sydney.edu.au.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import comp5216.sydney.edu.au.myapplication.adapter.NotesAdapter;
import comp5216.sydney.edu.au.myapplication.notes.Note;
import comp5216.sydney.edu.au.myapplication.notes.Reply;

public class MainActivity extends AppCompatActivity {
    int POST_NOTE = 1;
    int Reply_NOTE = 2;
    ListView listView;
    ArrayList<Note> orderedItems;
    // Custom Adapter
    NotesAdapter itemsAdapter;
    private FirebaseAuth mAuth;
    private DatabaseReference database;
    Button addAnnouncement;
    FirebaseUser currentUser;
    @Override
    public void onStart() {
        super.onStart();
        Log.d("post", "database: "+ database);
        Log.d("post", "database: "+ database);
        // Check if user is signed in (non-null) and update UI accordingly.

        Log.d("adapter", "auth: "+currentUser);
        if(currentUser==null){
            Intent intent=new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }
        Log.w("post", "notes: "+ orderedItems);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();
        super.onCreate(savedInstanceState);
        orderedItems = new ArrayList<Note>();
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.lstView);
        addAnnouncement = findViewById(R.id.add_announcement);
        currentUser = mAuth.getCurrentUser();
        addAnnouncement.setVisibility(View.GONE);
        if (currentUser.getUid().equals("8IbdHSrb9DTLjsTeGq6Eg4Cr0xv1")){
            addAnnouncement.setVisibility(View.VISIBLE);
        }
        ValueEventListener noteListener = new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("post1", "Here!!!");
                // Get Post object and use the values to update the UI
                for (DataSnapshot noteSnapshot: dataSnapshot.getChildren()) {
                    Note note = noteSnapshot.getValue(Note.class);
                    Log.d("post", "notes: "+ orderedItems);
                    orderedItems.add(note);
                    Log.d("post", "notes: "+ orderedItems);
                    Log.d("post", "note: "+ note);
                    Log.d("post", "noteTitle: "+ note.getTitle());
                }
                itemsAdapter = new NotesAdapter(this, R.layout.notes_layout, orderedItems);
                listView.setAdapter(itemsAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("error", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        Log.d("post", "database: "+ database);
        Log.d("post", "noteListener: "+ noteListener);
        Query allQuery = database.child("notes").orderByChild("data");
        allQuery.addValueEventListener(noteListener);
        Log.d("post", "Here notes: "+ orderedItems);
        setupListViewListener();

    }

    public void addNote(View view){
        Intent intent=new Intent(MainActivity.this, AddNote.class);
        startActivityForResult(intent,POST_NOTE);
    }

    public void userProfile(View view){
        Intent intent=new Intent(MainActivity.this, UserProfile.class);
        startActivity(intent);
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // When edit item
        if (requestCode == POST_NOTE) {
            if (resultCode == RESULT_OK) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    String uid = user.getUid();
                    String title = data.getExtras().getString("title");
                    String note = data.getExtras().getString("note");
                    Long createTime = new Date().getTime();
                    Log.d("User", "uid:" + uid);
                    Log.d("User", "title:" + title);
                    Log.d("User", "note:" + note);
                    Log.d("User", "time:" + createTime);
                    Log.d("User", "time:" + stampToDate(createTime));
                    Note Note = new Note(title, note, createTime, uid,createTime+" : "+uid);
                    database.child("notes").child(createTime+" : "+uid).setValue(Note);
                    database.child("users").child(uid).child("questions").child(createTime+" : "+uid).setValue(Note);
                    orderedItems.add(Note);
                    itemsAdapter.notifyDataSetChanged();
                }
            }
        }

        else if(requestCode == Reply_NOTE){
            if (resultCode == RESULT_OK) {
                String name = data.getExtras().getString("name");
                String content = data.getExtras().getString("reply");
                String ownerName = data.getExtras().getString("ownerName");
                String ownerID = data.getExtras().getString("ownerID");
                Long createTime = data.getExtras().getLong("createTime");
                Reply reply = new Reply(name,content,createTime,ownerName,ownerID);
                itemsAdapter.notifyDataSetChanged();
                database.child("replys").child(name).setValue(reply);
            }
        }
    }

    private void setupListViewListener() {
        // Short click to edit item
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Note updateNote = (Note) itemsAdapter.getItem(i);

                Intent intent = new Intent(MainActivity.this, ReplyNoteActivity.class);
                // put "extras" into the bundle for access in the edit activity
                intent.putExtra("note", (Serializable) updateNote);
                intent.putExtra("position", i);
                // brings up the second activity
                startActivityForResult(intent, Reply_NOTE);
                // Notify listView adapter to update the list
                itemsAdapter.notifyDataSetChanged();
            }});
    }

    public static String stampToDate(long time) {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = new Date(time);
        res = simpleDateFormat.format(date);
        return res;
    }
}