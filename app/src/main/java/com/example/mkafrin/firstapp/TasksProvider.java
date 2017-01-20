package com.example.mkafrin.firstapp;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.Selection;

public class TasksProvider extends ContentProvider {

    private static final String AUTHORITY = "com.example.mkafrin.firstapp.tasksprovider";
    private static final String BASE_PATH = "tasks";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH );

    // Constant to identify the requested operation
    private static final int TASKS = 1;
    private static final int TASKS_ID = 2;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    public static final String CONTENT_ITEM_TYPE = "Task";

    static {
        uriMatcher.addURI(AUTHORITY, BASE_PATH, TASKS);
        uriMatcher.addURI(AUTHORITY, BASE_PATH + "/#", TASKS_ID);
    }

    private SQLiteDatabase database;

    @Override
    public boolean onCreate() {
        DBConnector connector = new DBConnector(getContext());
        database = connector.getWritableDatabase();
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] strings, String selection, String[] strings1, String s1) {

        if (uriMatcher.match(uri) == TASKS_ID) {
            selection = DBConnector.TASK_ID + "=" + uri.getLastPathSegment();
        }

        return database.query(DBConnector.TABLE_TASKS, DBConnector.ALL_COLUMNS,
                selection, null, null, null,
                DBConnector.TASK_COMPLETED + " ASC, " + DBConnector.TASK_CREATED + " DESC");

    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        long id = database.insert(DBConnector.TABLE_TASKS, null, contentValues);
        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return database.delete(DBConnector.TABLE_TASKS, s, strings);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return database.update(DBConnector.TABLE_TASKS, contentValues, s, strings);
    }
}
