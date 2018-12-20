package com.app.androidebookapp.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.app.androidebookapp.models.ItemBook;

import java.util.ArrayList;
import java.util.List;

public class DbHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "ebook_db";
    private static final String TABLE_NAME = "tbl_book";
    private static final String KEY_ID = "id";
    private static final String KEY_BOOK_ID = "book_id";
    private static final String KEY_BOOK_NAME = "book_name";
    private static final String KEY_BOOK_IMAGE = "book_image";
    private static final String KEY_AUTHOR = "author";
    private static final String KEY_TYPE = "type";
    private static final String KEY_PDF_NAME = "pdf_name";
    private static final String KEY_COUNT = "count";

    public DbHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_BOOK_ID + " TEXT,"
                + KEY_BOOK_NAME + " TEXT,"
                + KEY_BOOK_IMAGE + " TEXT,"
                + KEY_AUTHOR + " TEXT,"
                + KEY_TYPE + " TEXT,"
                + KEY_PDF_NAME + " TEXT,"
                + KEY_COUNT + " TEXT"
                + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);

    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    //Adding Record in Database

    public void AddtoFavorite(ItemBook pj) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_BOOK_ID, pj.getBook_id());
        values.put(KEY_BOOK_NAME, pj.getBook_name());
        values.put(KEY_BOOK_IMAGE, pj.getBook_image());
        values.put(KEY_AUTHOR, pj.getAuthor());
        values.put(KEY_TYPE, pj.getType());
        values.put(KEY_PDF_NAME, pj.getPdf_name());
        values.put(KEY_COUNT, pj.getCount());
        // Inserting Row
        db.insert(TABLE_NAME, null, values);
        db.close(); // Closing database connection

    }

    // Getting All Data
    public List<ItemBook> getAllData() {
        List<ItemBook> dataList = new ArrayList<ItemBook>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NAME + " ORDER BY id DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ItemBook contact = new ItemBook();

                contact.setId(Integer.parseInt(cursor.getString(0)));
                contact.setBook_id(cursor.getString(1));
                contact.setBook_name(cursor.getString(2));
                contact.setBook_image(cursor.getString(3));
                contact.setAuthor(cursor.getString(4));
                contact.setType(cursor.getString(5));
                contact.setPdf_name(cursor.getString(6));
                contact.setCount(cursor.getString(7));

                // Adding contact to list
                dataList.add(contact);
            } while (cursor.moveToNext());
        }

        // return contact list
        db.close();
        return dataList;
    }

    //getting single row

    public List<ItemBook> getFavRow(String id) {
        List<ItemBook> dataList = new ArrayList<ItemBook>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NAME + " WHERE book_id=" + id;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ItemBook contact = new ItemBook();

                contact.setId(Integer.parseInt(cursor.getString(0)));
                contact.setBook_id(cursor.getString(1));
                contact.setBook_name(cursor.getString(2));
                contact.setBook_image(cursor.getString(3));
                contact.setAuthor(cursor.getString(4));
                contact.setType(cursor.getString(5));
                contact.setPdf_name(cursor.getString(6));
                contact.setCount(cursor.getString(7));

                // Adding contact to list
                dataList.add(contact);
            } while (cursor.moveToNext());
        }
        // return contact list
        db.close();
        return dataList;
    }

    //for remove favorite

    public void RemoveFav(ItemBook contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, KEY_BOOK_ID + " = ?",
                new String[]{String.valueOf(contact.getBook_id())});
        db.close();
    }

    public enum DatabaseManager {
        INSTANCE;
        private SQLiteDatabase db;
        private boolean isDbClosed = true;
        DbHandler dbHelper;

        public void init(Context context) {
            dbHelper = new DbHandler(context);
            if (isDbClosed) {
                isDbClosed = false;
                this.db = dbHelper.getWritableDatabase();
            }

        }

        public boolean isDatabaseClosed() {
            return isDbClosed;
        }

        public void closeDatabase() {
            if (!isDbClosed && db != null) {
                isDbClosed = true;
                db.close();
                dbHelper.close();
            }
        }
    }
}

