package com.example.mkafrin.firstapp;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.vstechlab.easyfonts.EasyFonts;

import java.util.logging.Handler;

public class TasksCursorAdapter extends CursorAdapter{

    int past_completed = 0;
    Typeface robotoRegular;

    public TasksCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        robotoRegular = EasyFonts.robotoRegular(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.note_list_item, viewGroup, false);
        //return LayoutInflater.from(context).inflate(R.layout.list_separator, viewGroup, false);
    }



    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        String taskText = cursor.getString(cursor.getColumnIndex(DBConnector.TASK_TEXT)); // cursor targets text column
        int pos = taskText.indexOf(10); // 10 is ascii value of line feed character
        if(pos != -1) {
            taskText = taskText.substring(0, pos) + "...";
        }

        ImageButton checkBtn = (ImageButton) view.findViewById(R.id.checkButton);


        TextView tv = (TextView) view.findViewById(R.id.tvNote);

        int completed = cursor.getInt(cursor.getColumnIndex(DBConnector.TASK_COMPLETED));
        if(completed==1) {
            LinearLayout completed_divider = (LinearLayout) view.findViewById(R.id.task_completed_divider);
            if(past_completed==0) {
                completed_divider.setVisibility(View.VISIBLE);
            } else if(past_completed==1) {
                completed_divider.setVisibility(View.GONE);
            }
            checkBtn.setImageResource(R.drawable.check_circle);
            tv.setTextColor(R.color.completed_text);
        } else {
            checkBtn.setImageResource(R.drawable.check_circle_outline);
        }

        tv.setText(taskText);
        tv.setTypeface(robotoRegular);
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
