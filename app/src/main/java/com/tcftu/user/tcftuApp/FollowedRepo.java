package com.tcftu.user.tcftuApp;

/**
 * Created by user on 2017/7/24.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class FollowedRepo {
    private DBHelper dbHelper;

    public FollowedRepo(Context context) {
        dbHelper = new DBHelper(context);
    }

    public int insert(Followed followed) {

        //Open connection to write data
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Followed.KEY_courseID, followed.courseID);
        values.put(Followed.KEY_name, followed.name);
        values.put(Followed.KEY_teacher, followed.teacher);
        values.put(Followed.KEY_applyDate,followed.applyDate);
        values.put(Followed.KEY_startApplyDate,followed.startApplyDate);
        values.put(Followed.KEY_startEnd,followed.startEnd);
        values.put(Followed.KEY_classDay,followed.classDay);
        values.put(Followed.KEY_people,followed.people);
        values.put(Followed.KEY_totalHr,followed.totalHr);
        values.put(Followed.KEY_kAdd,followed.kAdd);
        values.put(Followed.KEY_sAdd,followed.sAdd);
        values.put(Followed.KEY_labor,followed.labor);
        values.put(Followed.KEY_contact,followed.contact);
        values.put(Followed.KEY_phone,followed.phone);
        values.put(Followed.KEY_aYear, followed.aYear);
        values.put(Followed.KEY_aMonth, followed.aMonth);
        values.put(Followed.KEY_aDay, followed.aDay);
        values.put(Followed.KEY_eYear, followed.eYear);
        values.put(Followed.KEY_eMonth, followed.eMonth);
        values.put(Followed.KEY_eDay, followed.eDay);

        // Inserting Row
        long student_Id = db.insert(Followed.TABLE, null, values);
        db.close(); // Closing database connection
        return (int) student_Id;
    }
    public Cursor  getStudentList() {
        //Open connection to read only
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery =  "SELECT  rowid as " +
                Followed.KEY_ROWID + "," +
                Followed.KEY_courseID + "," +
                Followed.KEY_name + "," +
                Followed.KEY_teacher + "," +
                Followed.KEY_applyDate+","  +
                Followed.KEY_startApplyDate+","  +
                Followed.KEY_startEnd + "," +
                Followed.KEY_classDay + "," +
                Followed.KEY_people + "," +
                Followed.KEY_totalHr + "," +
                Followed.KEY_kAdd + "," +
                Followed.KEY_sAdd + "," +
                Followed.KEY_labor + "," +
                Followed.KEY_contact + "," +
                Followed.KEY_phone + "," +
                Followed.KEY_aYear + "," +
                Followed.KEY_aMonth + "," +
                Followed.KEY_aDay + "," +
                Followed.KEY_eYear + "," +
                Followed.KEY_eMonth + "," +
                Followed.KEY_eDay +
                " FROM " + Followed.TABLE;


        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list

        if (cursor == null) {
            return null;
        } else if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        return cursor;
    }
    public boolean delete(String id) {
        //Open connection to read only
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db.delete(Followed.TABLE, Followed.KEY_courseID + "= '" + id + "' ", null) > 0;
        //db.execSQL("delete from " + Course.TABLE + " where" + Course.KEY_courseID + "=" + id );
    }
    public int update(String id,Followed followed) {
        //Open connection to read only
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Followed.KEY_applyDate, followed.applyDate);
        values.put(Followed.KEY_startApplyDate,followed.startApplyDate);
        values.put(Followed.KEY_startEnd,followed.startEnd);
        values.put(Followed.KEY_classDay,followed.classDay);
        values.put(Followed.KEY_aYear,followed.aYear);
        values.put(Followed.KEY_aMonth,followed.aMonth);
        values.put(Followed.KEY_aDay,followed.aDay);
        values.put(Followed.KEY_eYear,followed.eYear);
        values.put(Followed.KEY_eMonth,followed.eMonth);
        values.put(Followed.KEY_eDay,followed.eDay);

        return db.update(Followed.TABLE,values, Followed.KEY_courseID + "=" + id, null);
    }
}
