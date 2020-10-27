package comp5216.sydney.edu.au.myapplication.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import comp5216.sydney.edu.au.myapplication.R;
import comp5216.sydney.edu.au.myapplication.notes.Note;
import comp5216.sydney.edu.au.myapplication.notes.Reply;

public class ReplyAdapter extends BaseAdapter {
    // My own adapter
    ArrayList<Reply> list;
    public ReplyAdapter(ValueEventListener context, int resource, ArrayList<Reply> items){
        list = items;
    }

    // Override some methods
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Reply getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        // Get the data item for this position
        Reply item = getItem(position);
        Log.d("reply4", "adapter: "+item);
        // Check if an existing view is being reused, otherwise inflate the view
        if (view == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reply_layout, parent, false);
        }
        // Lookup view for data population
        TextView relpy = (TextView) view.findViewById(R.id.reply);
        TextView createTime = (TextView) view.findViewById(R.id.createTime);

        relpy.setText(item.getContent());
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
