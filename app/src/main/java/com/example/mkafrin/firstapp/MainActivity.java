package com.example.mkafrin.firstapp;

import android.app.ActivityManager;
import android.app.Dialog;
import android.app.LoaderManager;
import android.content.*;
import android.content.pm.ApplicationInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.example.mkafrin.helper.ThemeColor;
import com.vstechlab.easyfonts.EasyFonts;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int EDITOR_REQUEST_CODE = 1001;
    public static final int SETTINGS_REQUEST_CODE = 1002;
    private CursorAdapter cursorAdapter;
    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeColor.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        cursorAdapter = new TasksCursorAdapter(this, null, 0);
        ListView list = (ListView) findViewById(android.R.id.list);
        list.setAdapter(cursorAdapter);

        getLoaderManager().initLoader(0, null, this);
        list.getParent().requestChildFocus(list, list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        fixFontOnStartup();
    }

    @Override
    protected void onResume() {
        ThemeColor.setTheme(this);
        super.onResume();
        ListView list = (ListView) findViewById(android.R.id.list);
        list.getParent().requestChildFocus(list, list);
    }

    private void fixFontOnStartup() {

        EditText taskTextInput = (EditText) findViewById(R.id.taskTextInput);
        taskTextInput.setTypeface(EasyFonts.robotoRegular(this));
    }

    private void insertTask(String taskText) {
        ContentValues values = new ContentValues();
        values.put(DBConnector.TASK_TEXT, taskText);
        Uri taskUri = getContentResolver().insert(TasksProvider.CONTENT_URI, values);
        Log.d("MainActivity", "Inserted Task " + taskUri.getLastPathSegment());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_debug, menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_create_sample:
                insertSampleData();
                break;
            case R.id.action_delete_all:
                deleteAllTasks();
                break;
            case R.id.action_delete_completed:
                deleteCompletedTasks();
                break;
            case R.id.action_settings:
                goToSettingsActivity();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void goToSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivityForResult(intent, SETTINGS_REQUEST_CODE);
    }

    private void deleteCompletedTasks() {
        DialogInterface.OnClickListener dialogClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int button) {
                        if (button == DialogInterface.BUTTON_POSITIVE) {
                            getContentResolver().delete(TasksProvider.CONTENT_URI, DBConnector.TASK_COMPLETED+"=1", null);
                            restartLoader();
                            singleToast(getString(R.string.completed_deleted), Toast.LENGTH_SHORT);
                        }
                    }
                };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.are_you_sure_completed))
                .setPositiveButton(getString(android.R.string.yes), dialogClickListener)
                .setNegativeButton(getString(android.R.string.no), dialogClickListener)
                .show();
    }

    private void deleteAllTasks() {
        DialogInterface.OnClickListener dialogClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int button) {
                        if (button == DialogInterface.BUTTON_POSITIVE) {
                            getContentResolver().delete(TasksProvider.CONTENT_URI, null, null);
                            restartLoader();
                            singleToast(getString(R.string.all_deleted), Toast.LENGTH_SHORT);
                        }
                    }
                };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.are_you_sure_all))
                .setPositiveButton(getString(android.R.string.yes), dialogClickListener)
                .setNegativeButton(getString(android.R.string.no), dialogClickListener)
                .show();
    }

    private void insertSampleData() {
        insertTask("Simple task");
        insertTask("Multi-line\nnote");
        insertTask("Very long note with a lot of text that exceeds the width of the screen");
        restartLoader();
    }

    public void onAddTaskBtnClick(View v) {
        TextView txtview = (TextView) findViewById(R.id.taskTextInput);
        if (txtview.getText().toString().equalsIgnoreCase("")) {
            Intent intent = new Intent(this, EditorActivity.class);
            startActivityForResult(intent, EDITOR_REQUEST_CODE);
            return;
        }
        String txt = txtview.getText().toString();
        insertTask(txt);
        txtview.setText(null);
        restartLoader();
        //Toast.makeText(MainActivity.this, getString(R.string.task_added), Toast.LENGTH_SHORT).show();
        singleToast(getString(R.string.task_added), Toast.LENGTH_SHORT);
    }

    public void onTrashBtnClick(View view) {
        View parentRow = (View) view.getParent().getParent();
        ListView listView = (ListView) view.getParent().getParent().getParent();
        int position = listView.getPositionForView(parentRow);
        long id = cursorAdapter.getItemId(position);

        String taskFilter = DBConnector.TASK_ID + "=" + id;
        getContentResolver().delete(TasksProvider.CONTENT_URI, taskFilter, null);
        singleToast(getString(R.string.task_deleted), Toast.LENGTH_SHORT);
        restartLoader();
    }

    public void onTaskTextInListClick(View view) {
        View parentRow = (View) view.getParent().getParent();
        ListView listView = (ListView) view.getParent().getParent().getParent();
        int position = listView.getPositionForView(parentRow);
        long id = cursorAdapter.getItemId(position);
        Intent intent = new Intent(MainActivity.this, EditorActivity.class);
        Uri uri = Uri.parse(TasksProvider.CONTENT_URI + "/" + id);
        intent.putExtra(TasksProvider.CONTENT_ITEM_TYPE, uri);
        String filter = DBConnector.TASK_ID + "=" + id;
        //singleToast(String.valueOf(queryCompleted(id)), 3);

        Cursor cursor = getContentResolver().query(uri, DBConnector.ALL_COLUMNS, filter, null, null);
        cursor.moveToFirst();
        //String date = cursor.getString(cursor.getColumnIndex(DBConnector.TASK_CREATED));
        //singleToast(date, 5);

        startActivityForResult(intent, EDITOR_REQUEST_CODE);
    }

    private int queryCompleted(long id) {
        String filter = DBConnector.TASK_ID + "=" + id;
        Uri uri = Uri.parse(TasksProvider.CONTENT_URI + "/" + id);

        Cursor cursor = getContentResolver().
                 query(uri, DBConnector.ALL_COLUMNS, filter, null, null);
        cursor.moveToFirst();
        int completed = (cursor.getInt(cursor.getColumnIndex(DBConnector.TASK_COMPLETED)));
        return completed;
    }

    private void updateTaskCompleted(int taskCompleted, long id) {

        String taskFilter = DBConnector.TASK_ID + "=" + id;

        ContentValues values = new ContentValues();
        values.put(DBConnector.TASK_COMPLETED, taskCompleted);
        getContentResolver().update(TasksProvider.CONTENT_URI, values, taskFilter, null);
        setResult(RESULT_OK);
    }

    public void onCheckBtnClick(View view) {
        View parentRow = (View) view.getParent().getParent();
        ListView listView = (ListView) view.getParent().getParent().getParent();
        int position = listView.getPositionForView(parentRow);
        long id = cursorAdapter.getItemId(position);

        if(queryCompleted(id)==1) {
            updateTaskCompleted(0, id);
        } else {
            updateTaskCompleted(1, id);
        }
        restartLoader();
    }

    private void singleToast(String text, int duration) {
        if (mToast != null) mToast.cancel();
        mToast = Toast.makeText(MainActivity.this, text, duration);
        mToast.show();
    }

    private void restartLoader() {
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this, TasksProvider.CONTENT_URI,
                null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EDITOR_REQUEST_CODE && resultCode == RESULT_OK) {
            restartLoader();
        }
    }

}
