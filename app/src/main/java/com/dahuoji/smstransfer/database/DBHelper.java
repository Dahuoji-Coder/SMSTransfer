package com.dahuoji.smstransfer.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by 10732 on 2018/5/22.
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "sms_transfer_db";
    private static final int DATABASE_VERSION = 1;

    DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Log.d(">>>DBHelper", "onCreate");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Log.d(">>>DBHelper", "onUpgrade oldVersion = " + oldVersion + "; newVersion = " + newVersion);
        dropDB(db, oldVersion);
    }

    private void dropDB(SQLiteDatabase db, int oldVersion) {

    }
}