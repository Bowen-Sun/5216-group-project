package comp5216.sydney.edu.au.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import comp5216.sydney.edu.au.myapplication.adapter.AnnouncementAdapter;
import comp5216.sydney.edu.au.myapplication.adapter.NotesAdapter;
import comp5216.sydney.edu.au.myapplication.notes.Announcement;
import comp5216.sydney.edu.au.myapplication.notes.Note;

public class ShowAnnouncement extends AppCompatActivity {
    int POST_ANNOUNCEMENT = 5;
    int EDIT_ANNOUNCEMENT = 6;
    ListView announcementListView;
    ArrayList<Announcement> orderedAnnouncements;
    // Custom Adapter
    AnnouncementAdapter announcementAdapter;
    private FirebaseAuth mAuth;
    private DatabaseReference database;
    Button addAnnouncement;
    FirebaseUser currentUser;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_announcement);
        searchView = findViewById(R.id.search_announcement);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();
        orderedAnnouncements = new ArrayList<Announcement>();
        announcementListView = findViewById(R.id.announcementListView);
        currentUser = mAuth.getCurrentUser();
        addAnnouncement = findViewById(R.id.add_announcement);
        addAnnouncement.setVisibility(View.GONE);
        currentUser = mAuth.getCurrentUser();
        if(currentUser.getUid().equals("8IbdHSrb9DTLjsTeGq6Eg4Cr0xv1")){
            addAnnouncement.setVisibility(View.VISIBLE);
        }

        ValueEventListener noteListener = new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                orderedAnnouncements.clear();
                Log.d("post1", "Here!!!");
                // Get Post object and use the values to update the UI
                for (DataSnapshot noteSnapshot: dataSnapshot.getChildren()) {
                    Announcement announcement = noteSnapshot.getValue(Announcement.class);

                    orderedAnnouncements.add(0,announcement);

                }
                announcementAdapter = new AnnouncementAdapter(this, R.layout.notes_layout, orderedAnnouncements);
                announcementListView.setAdapter(announcementAdapter);
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
        Query allQuery = database.child("announcements").orderByChild("data");
        allQuery.addValueEventListener(noteListener);
        Log.d("post", "Here notes: "+ orderedAnnouncements);
        setupListViewListener();
        setupSearchListener();
    }

    public void addAnnouncement(View view){
        Intent intent=new Intent(ShowAnnouncement.this, AddAnnouncement.class);
        startActivityForResult(intent,POST_ANNOUNCEMENT);
    }

    private void setupListViewListener(){
        announcementListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(currentUser.getUid().equals("8IbdHSrb9DTLjsTeGq6Eg4Cr0xv1")){
                    Announcement updateAnnouncement = (Announcement) announcementAdapter.getItem(i);
                    Intent intent = new Intent(ShowAnnouncement.this, EditNotesAndRepliesAndAnnouncementActivity.class);
                    // put "extras" into the bundle for access in the edit activity
                    intent.putExtra("announcement", (Serializable) updateAnnouncement);
                    intent.putExtra("position", i);
                    // brings up the second activity
                    startActivityForResult(intent, EDIT_ANNOUNCEMENT);
                    // Notify listView adapter to update the list
                    announcementAdapter.notifyDataSetChanged();
                }
                else {
                    Announcement displayAnnouncement = (Announcement) announcementAdapter.getItem(i);
                    Intent intent = new Intent(ShowAnnouncement.this, DisplayAnnouncement.class);
                    // put "extras" into the bundle for access in the edit activity
                    intent.putExtra("announcement", (Serializable) displayAnnouncement);
                    intent.putExtra("position", i);
                    startActivity(intent);
                }
            }}
            );

        announcementListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int
                    position, long rowId)
            {
                if (currentUser.getUid().equals("8IbdHSrb9DTLjsTeGq6Eg4Cr0xv1")){
                    Log.i("MainActivity", "Long Clicked item " + position);
                    // Build a alert dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(ShowAnnouncement.this);
                    // Set text for the dialog
                    builder.setTitle(R.string.dialog_delete_title);
                    builder.setMessage(R.string.dialog_delete_msg);
                    builder.setPositiveButton(R.string.delete, new
                            DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Announcement deleteAnnouncement = orderedAnnouncements.get(position);
                                    orderedAnnouncements.remove(position);
                                    announcementAdapter.notifyDataSetChanged();
                                    database.child("announcements").child(deleteAnnouncement.getName()).removeValue();
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
                return true;
            }}
            );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // When edit item
        if (requestCode == POST_ANNOUNCEMENT) {
            if (resultCode == RESULT_OK) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    String uid = user.getUid();
                    String title = data.getExtras().getString("title");
                    String note = data.getExtras().getString("note");
                    Long createTime = new Date().getTime();
                    Announcement announcement = new Announcement(title, createTime+" : "+uid, note,createTime,uid);
                    database.child("announcements").child(createTime+" : "+uid).setValue(announcement);
                }
            }
        }

        else if(requestCode == EDIT_ANNOUNCEMENT){
            if (resultCode == RESULT_OK) {
                Announcement editedAnnouncement = (Announcement) data.getSerializableExtra("announcement");
                int position = data.getIntExtra("position",-1);
                database.child("replys").child(editedAnnouncement.getName()).setValue(editedAnnouncement);
                orderedAnnouncements.set(position,editedAnnouncement);
                announcementAdapter.notifyDataSetChanged();
            }
        }
    }

    private  void setupSearchListener(){
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                if (query.length() > 0) {
                    orderedAnnouncements.clear();
                    ValueEventListener noteListener = new ValueEventListener(){
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // Get Post object and use the values to update the UI
                            for (DataSnapshot noteSnapshot: dataSnapshot.getChildren()) {
                                Announcement announcement = noteSnapshot.getValue(Announcement.class);
                                if(announcement.getTitle().toLowerCase().contains(query.toLowerCase())) orderedAnnouncements.add(0,announcement);
                            }
                            announcementAdapter = new AnnouncementAdapter(this, R.layout.notes_layout, orderedAnnouncements);
                            announcementListView.setAdapter(announcementAdapter);
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Getting Post failed, log a message
                            Log.w("error", "loadPost:onCancelled", databaseError.toException());
                            // ...
                        }
                    };

                    Query allQuery = database.child("announcements").orderByChild("data");
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
                orderedAnnouncements.clear();
                ValueEventListener noteListener = new ValueEventListener(){
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d("post1", "Here!!!");
                        // Get Post object and use the values to update the UI
                        for (DataSnapshot noteSnapshot: dataSnapshot.getChildren()) {
                            Announcement announcement = noteSnapshot.getValue(Announcement.class);
                            orderedAnnouncements.add(0,announcement);
                        }
                        announcementAdapter = new AnnouncementAdapter(this, R.layout.notes_layout, orderedAnnouncements);
                        announcementListView.setAdapter(announcementAdapter);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                        Log.w("error", "loadPost:onCancelled", databaseError.toException());
                        // ...
                    }
                };

                Query allQuery = database.child("announcements").orderByChild("data");
                allQuery.addListenerForSingleValueEvent(noteListener);
                return false;
            }
        });
    }
}