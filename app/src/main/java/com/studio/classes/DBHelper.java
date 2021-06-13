package com.studio.classes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.sql.Date;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DBNAME = "VirtualCabinet.db";
    private final SQLiteDatabase database = this.getWritableDatabase();

    public DBHelper(Context context) {
        super(context, DBNAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create Table activities(id TEXT primary key, email TEXT, name TEXT, type TEXT, location TEXT, " +
                "date TEXT, combineId TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop Table if exists activities");
    }

    public boolean insertEvent(String id, String email, String name, String type,
                               String location, Date date, String combineId) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("id",id);
        contentValues.put("email", email);
        contentValues.put("name", name);
        contentValues.put("type", type);
        contentValues.put("location", location);
        contentValues.put("date", String.valueOf(date));
        contentValues.put("combineId", combineId);
        long result = database.insert("activities",null, contentValues);
        return !(result == -1);
    }

    public ArrayList<Event> getAllEvents(String email) {
        ArrayList<Event> events = new ArrayList<>();
        Cursor cursor = database.rawQuery("select * from activities where email= ?", new String[]{String.valueOf(email)});
        try {
            while (cursor.moveToNext()) {
                int eventId = cursor.getColumnIndexOrThrow("id");
                int eventName = cursor.getColumnIndexOrThrow("name");
                int eventType = cursor.getColumnIndexOrThrow("type");
                int eventLocation = cursor.getColumnIndexOrThrow("location");
                int eventDate = cursor.getColumnIndexOrThrow("date");
                int eventCombineId = cursor.getColumnIndexOrThrow("combineId");
                String Id = cursor.getString(eventId);
                String Name = cursor.getString(eventName);
                String Type = cursor.getString(eventType);
                String Location = cursor.getString(eventLocation);
                String DateI = cursor.getString(eventDate);
                String CombineId = cursor.getString(eventCombineId);
                events.add(new Event(Id, Name, Type, Date.valueOf(DateI), Location, CombineId));
            }
        } finally {
            cursor.close();
        }
        return events;
    }

    public boolean updateEvent(Event event, String email) {
        try {
            database.execSQL("update activities set name = ?, type = ?, location = ?, date = ?, combineId = ? " +
                            "where  id = ? and email = ?",
                    new String[]{event.getName(), event.getKind(), event.getLocation(),
                             String.valueOf(event.getDate()), event.getCombineId(), event.getId(), email});
            return true;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}