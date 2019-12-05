package com.example.fbtestapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.icu.text.UnicodeSet;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "userDataManager";
    private static final String TABLE_NAME_ONUPDATE = "onUpdate";
    private static final String TABLE_NAME_NOTIF = "notification";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_SURNAME = "surname";
    private static final String KEY_SEX = "sex";
    private static final String KEY_AGE = "age";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_TIMESTAMP = "timestamp";
    private static final String KEY_TSTAMP = "tstamp";

    public DatabaseHandler(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_ONUPDATE_TABLE = "CREATE TABLE " + TABLE_NAME_ONUPDATE + " (" + KEY_ID + " INTEGER PRIMARY KEY, " + KEY_NAME + " TEXT, "
                + KEY_SURNAME + " TEXT, " + KEY_SEX + " TEXT, " + KEY_AGE + " INTEGER, " + KEY_TOKEN + " TEXT" +")";
        String CREATE_NOTIF_TABLE = "CREATE TABLE " + TABLE_NAME_NOTIF + " (" + KEY_ID + " INTEGER PRIMARY KEY, " + KEY_NAME + " TEXT, "
                + KEY_SURNAME + " TEXT, " + KEY_SEX + " TEXT, " + KEY_AGE + " INTEGER, " + KEY_TOKEN + " TEXT" +")";
        sqLiteDatabase.execSQL(CREATE_ONUPDATE_TABLE);
        sqLiteDatabase.execSQL(CREATE_NOTIF_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_NOTIF);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_ONUPDATE);
        onCreate(sqLiteDatabase);
    }

    public long addDataNotif(UserData userData){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_NAME, userData.getName());
        contentValues.put(KEY_SURNAME, userData.getSurname());
        contentValues.put(KEY_SEX, userData.getSex());
        contentValues.put(KEY_AGE, userData.getAge());

        long rowId = db.insert(TABLE_NAME_NOTIF, null, contentValues);
        db.close();

        return rowId;
    }

    public long addDataUpdate(UserData userData){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_NAME, userData.getName());
        contentValues.put(KEY_SURNAME, userData.getSurname());
        contentValues.put(KEY_SEX, userData.getSex());
        contentValues.put(KEY_AGE, userData.getAge());

        long rowId = db.insert(TABLE_NAME_ONUPDATE, null, contentValues);
        db.close();

        return rowId;
    }

    public void setDataUpdate(UserData userData){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_NAME, userData.getName());
        contentValues.put(KEY_SURNAME, userData.getSurname());
        contentValues.put(KEY_SEX, userData.getSex());
        contentValues.put(KEY_AGE, userData.getAge());

        int temp = db.update(TABLE_NAME_ONUPDATE, contentValues, KEY_ID + " = " + userData.getId(), null);
        if(temp == 0)
            addDataUpdate(userData);
        db.close();
    }

    public List<UserData> getListData (int k){
        List<UserData> userDataList = new ArrayList<>();

        String selectQuery;
        if(k == 0)
            selectQuery = "SELECT * FROM " + TABLE_NAME_ONUPDATE;
        else
            selectQuery = "SELECT * FROM " + TABLE_NAME_NOTIF;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()){
            do{
                UserData userData = new UserData();
                userData.setId(cursor.getLong(0));
                userData.setName(cursor.getString(1));
                userData.setSurname(cursor.getString(2));
                userData.setSex(cursor.getString(3));
                userData.setAge(cursor.getInt(4));

                userDataList.add(userData);
            }while(cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return userDataList;
    }
}
