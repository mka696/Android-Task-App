package com.example.mkafrin.firstapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBConnector extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "tasks.db";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_TASKS = "tasks";
    public static final String TASK_ID = "_id";
    public static final String TASK_TEXT = "noteText";
    public static final String TASK_CREATED = "noteCreated";
    public static final String TASK_COMPLETED = "taskCompleted";

    public static final String[] ALL_COLUMNS =
            {TASK_ID, TASK_TEXT, TASK_CREATED, TASK_COMPLETED};

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_TASKS + " (" +
                    TASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TASK_TEXT + " TEXT, " +
                    TASK_CREATED + " TEXT default CURRENT_TIMESTAMP, " +
                    TASK_COMPLETED + " INTEGER default 0" +
                    ")";

    public DBConnector(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "  + TABLE_TASKS);
        onCreate(sqLiteDatabase);
    }
}
