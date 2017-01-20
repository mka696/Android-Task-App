package com.example.mkafrin.firstapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.vstechlab.easyfonts.EasyFonts;

public class EditorActivity extends AppCompatActivity {

    private Toast mToast;
    private String action;
    private EditText editor;
    private String taskFilter;
    private String oldText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Toolbar toolbar = (Toolbar) findViewById(R.id.info_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editor = (EditText) findViewById(R.id.editText);
        Intent intent = getIntent();

        Uri uri = intent.getParcelableExtra(TasksProvider.CONTENT_ITEM_TYPE);

        if(uri == null) {
            action = Intent.ACTION_INSERT;
            setTitle(getString(R.string.new_task));
        }
        else {
            setTitle(R.string.edit_task);
            action = Intent.ACTION_EDIT;
            taskFilter = DBConnector.TASK_ID + "=" + uri.getLastPathSegment();

            Cursor cursor = getContentResolver().query(uri, DBConnector.ALL_COLUMNS,
                    taskFilter, null, null);
            cursor.moveToFirst();
            oldText = cursor.getString(cursor.getColumnIndex(DBConnector.TASK_TEXT));
            editor.setText(oldText);
            editor.requestFocus();
        }
        fixFontsOnStartup();
    }

    private void fixFontsOnStartup() {
        EditText editText = (EditText) findViewById(R.id.editText);
        editText.setTypeface(EasyFonts.robotoRegular(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(action.equals(Intent.ACTION_EDIT)) {
            getMenuInflater().inflate(R.menu.menu_editor1, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_editor, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (item.getItemId()) {
            case android.R.id.home:
                finishEditing();
                break;
            case R.id.action_delete:
                deleteTask();
                break;
            case R.id.action_save:
                finishEditing();
                break;
        }

        return true;
    }

    private void deleteTask() {
        getContentResolver().delete(TasksProvider.CONTENT_URI, taskFilter, null);
        singleToast(getString(R.string.task_deleted), Toast.LENGTH_SHORT);
        setResult(RESULT_OK);
        finish();
    }

    private void finishEditing() {
        String newText = editor.getText().toString().trim();
        switch (action) {
            case Intent.ACTION_INSERT:
                if (newText.length() == 0) {
                    setResult(RESULT_CANCELED);
                }
                else {
                    insertTask(newText);
                }
                break;
            case Intent.ACTION_EDIT:
                if (newText.length() == 0) {
                    deleteTask();
                }
                else if(oldText.equals(newText)){
                    setResult(RESULT_CANCELED);
                }
                else {
                    updateTask(newText);
                }
        }
        finish();
    }

    private void updateTask(String taskText) {
        ContentValues values = new ContentValues();
        values.put(DBConnector.TASK_TEXT, taskText);
        getContentResolver().update(TasksProvider.CONTENT_URI, values, taskFilter, null);
        singleToast(getString(R.string.task_update), Toast.LENGTH_SHORT);
        setResult(RESULT_OK);
    }

    private void insertTask(String taskText) {
        ContentValues values = new ContentValues();
        values.put(DBConnector.TASK_TEXT, taskText);
        getContentResolver().insert(TasksProvider.CONTENT_URI, values);
        setResult(RESULT_OK);
        singleToast("Task Added", Toast.LENGTH_SHORT);
    }

    @Override
    public void onBackPressed(){
        finishEditing();
    }

    private void singleToast(String text, int duration) {
        if (mToast != null) mToast.cancel();
        mToast = Toast.makeText(EditorActivity.this, text, duration);
        mToast.show();
    }
}
