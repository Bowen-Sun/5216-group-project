package comp5216.sydney.edu.au.myapplication.adapter;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import comp5216.sydney.edu.au.myapplication.notes.Note;
import comp5216.sydney.edu.au.myapplication.R;
import comp5216.sydney.edu.au.myapplication.users.UserModel;

public class NotesAdapter extends BaseAdapter {
    private StorageReference storageRef;
    private FirebaseStorage storage;
    private DatabaseReference database;
    private File localFile;
    private UserModel user;

    ArrayList<Note> list;
    public NotesAdapter(ValueEventListener context, int resource, ArrayList<Note> items){
        database = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        list = items;
    }

    // Override some methods
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Note getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View view, ViewGroup parent) {

        // Get the data item for this position
        Note item = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (view == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_layout, parent, false);
        }
        TextView title = (TextView) view.findViewById(R.id.title);
        TextView note = (TextView) view.findViewById(R.id.note);
        TextView createTime = (TextView) view.findViewById(R.id.createTime);
        final TextView name = (TextView) view.findViewById(R.id.createName);
        final TextView identity = (TextView) view.findViewById(R.id.stuffOrStudent);

        ValueEventListener userListener = new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                    user = userSnapshot.getValue(UserModel.class);
                    name.setText(user.getName());
                    if(user.getUid().equals("8IbdHSrb9DTLjsTeGq6Eg4Cr0xv1")){
                        identity.setText("Stuff");
                    } else identity.setText("Student");
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("error", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        Query UserQuery = database.child("users").orderByChild("uid").equalTo(item.getOwnerID());
        UserQuery.addListenerForSingleValueEvent(userListener);


        title.setText(item.getTitle());
        note.setText(item.getContent());
        createTime.setText(stampToDate(item.getData()));


        // Return the completed view to render on screen
        return view;
    }

    public static String stampToDate(long time) {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = new Date(time);
        res = simpleDateFormat.format(date);
        return res;
    }

}
