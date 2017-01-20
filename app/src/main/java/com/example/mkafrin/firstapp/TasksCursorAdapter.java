package com.example.mkafrin.firstapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.vstechlab.easyfonts.EasyFonts;

public class TasksCursorAdapter extends CursorAdapter{

    int past_completed = 0;

    public TasksCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.note_list_item, viewGroup, false);
        //return LayoutInflater.from(context).inflate(R.layout.list_separator, viewGroup, false);
        // TODO: add functionality to inflate header layout
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        String taskText = cursor.getString(cursor.getColumnIndex(DBConnector.TASK_TEXT)); // cursor targets text column
        int pos = taskText.indexOf(10); // 10 is ascii value of line feed character
        if(pos != -1) {
            taskText = taskText.substring(0, pos) + "...";
        }

        ImageButton checkBtn = (ImageButton) view.findViewById(R.id.checkButton);

        LinearLayout test = (LinearLayout) view.findViewById(R.id.task_completed_divider);

        int completed = cursor.getInt(cursor.getColumnIndex(DBConnector.TASK_COMPLETED));
        if(completed==1) {
            if(past_completed==0) {
                test.setVisibility(View.VISIBLE);
            } else if(past_completed==1) {
                test.setVisibility(View.GONE);
            }
            checkBtn.setImageResource(R.drawable.check_circle);
        } else {
            checkBtn.setImageResource(R.drawable.check_circle_outline);
        }

        TextView tv = (TextView) view.findViewById(R.id.tvNote);
        tv.setText(taskText);
        tv.setTypeface(EasyFonts.robotoRegular(context));
        past_completed = completed;
    }

    private int getItemViewType(Cursor cursor) {
        return cursor.getInt(cursor.getColumnIndex(DBConnector.TASK_COMPLETED));
    }

    @Override
    public int getItemViewType(int position) {
        Cursor cursor = (Cursor) getItem(position);
        return getItemViewType(cursor);
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }
}
