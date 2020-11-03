package comp5216.sydney.edu.au.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.text.method.ScrollingMovementMethod;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import comp5216.sydney.edu.au.myapplication.notes.Announcement;

/**
 * Display Announcement details
 *
 * @author Bowen Sun
 */

public class DisplayAnnouncement extends AppCompatActivity {

    TextView AnnouncementTitle,AnnouncementNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_announcement);
        AnnouncementTitle = findViewById(R.id.show_announcement_title);
        AnnouncementNote = findViewById(R.id.show_announcement_content);
        AnnouncementTitle.setMovementMethod(ScrollingMovementMethod.getInstance());
        AnnouncementNote.setMovementMethod(ScrollingMovementMethod.getInstance());
        Announcement announcement = (Announcement) getIntent().getSerializableExtra("announcement");
        AnnouncementTitle.setText(announcement.getTitle());
        AnnouncementNote.setText(announcement.getContent());
    }

    public void goBack(View view){
        Intent intent = new Intent(DisplayAnnouncement.this, ShowAnnouncement.class);
        startActivity(intent);
    }
}