package com.wzwshop.moko;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Database {
    private static final String TAG = "Database";

    private static final String DATABASE_NAME = "moko.db";
    private static final int DATABASE_VERSION = 1;

    private Context mCtx;
    private DatabaseOpenHelper mDbHelper;
    private SQLiteDatabase mDb;

    private static final String TABLE_FAVOURITE = "favourite";
    private static final String DATABASE_CREATE =
            "CREATE TABLE "+ TABLE_FAVOURITE +" (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "id INTEGER NOT NULL," +
                    "title TEXT NOT NULL," +
                    "poster TEXT);";

    private volatile static Database instance;

    /** Returns singleton class instance */
    public static Database getInstance() {
        if (instance == null) {
            synchronized (Database.class) {
                if (instance == null) {
                    instance = new Database();
                }
            }
        }
        return instance;
    }

    protected Database() {}

    public synchronized void open(Context context) {
        mCtx = context;
        mDbHelper = new DatabaseOpenHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
    }

    public void close() {
        mDbHelper.close();
    }

    public boolean removeMovieFromFavourite(long id) {
        return mDb.delete(TABLE_FAVOURITE, "id='"+id+"'", null) > 0;
    }

    public boolean isMovieInFavourite(long id) {
        Cursor mCursor = mDb.query(true, TABLE_FAVOURITE, new String[] {"id"}, "id='"+id+"'", null, null, null, null, "1");
        boolean isIn = (mCursor != null && mCursor.moveToFirst());
        if (mCursor != null) {
            mCursor.close();
        }
        return isIn;
    }

    public int getRowIdByMovieId(long movieId) {
        int rowId = -1;
        Cursor cursor = mDb.query(TABLE_FAVOURITE, new String[] {"_id"}, "id='"+movieId+"'", null, null, null, null, 0+","+1);
        if (cursor != null) {
            cursor.moveToFirst();
            while (cursor.isAfterLast() == false) {
                rowId = cursor.getInt(cursor.getColumnIndex("_id"));
                cursor.moveToNext();
            }
            cursor.close();
        }
        return rowId;
    }

    private static class DatabaseOpenHelper extends SQLiteOpenHelper {
        DatabaseOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVOURITE);
            onCreate(db);
        }
    }
}
