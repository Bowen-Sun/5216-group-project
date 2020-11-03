package comp5216.sydney.edu.au.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import comp5216.sydney.edu.au.myapplication.adapter.NotesAdapter;
import comp5216.sydney.edu.au.myapplication.notes.Note;

/**
 * MainActivity to show all notes
 *
 * @author Bowen Sun
 */

public class MainActivity extends Activity {
    int POST_NOTE = 1;
    ListView listView;
    ArrayList<Note> orderedItems;
    // Custom Adapter
    NotesAdapter itemsAdapter;
    private FirebaseAuth mAuth;
    private DatabaseReference database;
    private StorageReference storageRef;
    TextView showAnnouncement;
    FirebaseUser currentUser;
    SearchView searchView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        storageRef = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if(currentUser==null){
            Intent intent=new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchView = findViewById(R.id.search_note);

        database = FirebaseDatabase.getInstance().getReference();
        orderedItems = new ArrayList<Note>();
        listView = findViewById(R.id.lstView);
        showAnnouncement = findViewById(R.id.show_announcement);

        ValueEventListener noteListener = new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                orderedItems.clear();
                // Get Post object and use the values to update the UI
                for (DataSnapshot noteSnapshot: dataSnapshot.getChildren()) {
                    Note note = noteSnapshot.getValue(Note.class);
                    orderedItems.add(0,note);
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
        Query allQuery = database.child("notes").orderByChild("data");
        allQuery.addValueEventListener(noteListener);

        setupListViewListener();
        setupSearchListener();

    }

    public void addNote(View view){
        Intent intent=new Intent(MainActivity.this, AddNote.class);
        startActivityForResult(intent,POST_NOTE);
    }

    public void userProfile(View view){
        Intent intent=new Intent(MainActivity.this, UserProfile.class);
        startActivity(intent);
    }

    public void showAnnouncement(View view){
        Intent intent=new Intent(MainActivity.this, ShowAnnouncement.class);
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
                    Note Note = new Note(title, note, createTime, uid,createTime+" : "+uid);
                    database.child("notes").child(createTime+" : "+uid).setValue(Note);
                }
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
                startActivity(intent);
                // Notify listView adapter to update the list
                itemsAdapter.notifyDataSetChanged();
            }});
    }

    private  void setupSearchListener(){
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                if (query.length() > 0) {
                    orderedItems.clear();
                    ValueEventListener noteListener = new ValueEventListener(){
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // Get Post object and use the values to update the UI
                            for (DataSnapshot noteSnapshot: dataSnapshot.getChildren()) {
                                Note note = noteSnapshot.getValue(Note.class);
                                if(note.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                                note.getContent().toLowerCase().contains(query.toLowerCase())) orderedItems.add(0,note);
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

                    Query allQuery = database.child("notes").orderByChild("data");
                    allQuery.addListenerForSingleValueEvent(noteListener);
                    searchView.setIconified(true);
                }
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener(){

            @Override
            public boolean onClose() {
                orderedItems.clear();
                ValueEventListener noteListener = new ValueEventListener(){
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get Post object and use the values to update the UI
                        for (DataSnapshot noteSnapshot: dataSnapshot.getChildren()) {
                            Note note = noteSnapshot.getValue(Note.class);
                            orderedItems.add(0,note);
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

                Query allQuery = database.child("notes").orderByChild("data");
                allQuery.addListenerForSingleValueEvent(noteListener);
                return false;
            }
        });
    }
}