package com.tcftu.user.tcftuApp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class CourseRepo {
    private DBHelper dbHelper;

    public CourseRepo(Context context) {
        dbHelper = new DBHelper(context);
    }

    public int insert(Course course) {

        //Open connection to write data
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Course.KEY_courseID, course.courseID);
        values.put(Course.KEY_name, course.name);
        values.put(Course.KEY_teacher, course.teacher);
        values.put(Course.KEY_applyDate, course.applyDate);
        values.put(Course.KEY_startApplyDate, course.startApplyDate);
        values.put(Course.KEY_startEnd, course.startEnd);
        values.put(Course.KEY_classDay, course.classDay);
        values.put(Course.KEY_people, course.people);
        values.put(Course.KEY_totalHr, course.totalHr);
        values.put(Course.KEY_kAdd, course.kAdd);
        values.put(Course.KEY_sAdd, course.sAdd);
        values.put(Course.KEY_labor, course.labor);
        values.put(Course.KEY_contact, course.contact);
        values.put(Course.KEY_phone, course.phone);
        values.put(Course.KEY_aYear, course.aYear);
        values.put(Course.KEY_aMonth, course.aMonth);
        values.put(Course.KEY_aDay, course.aDay);
        values.put(Course.KEY_eYear, course.eYear);
        values.put(Course.KEY_eMonth, course.eMonth);
        values.put(Course.KEY_eDay, course.eDay);

        // Inserting Row
        long student_Id = db.insert(Course.TABLE, null, values);
        db.close(); // Closing database connection
        return (int) student_Id;
    }

    public Cursor getStudentList() {
        //Open connection to read only
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT  rowid as " +
                Course.KEY_ROWID + "," +
                Course.KEY_courseID + "," +
                Course.KEY_name + "," +
                Course.KEY_teacher + "," +
                Course.KEY_applyDate + "," +
                Course.KEY_startApplyDate + "," +
                Course.KEY_startEnd + "," +
                Course.KEY_classDay + "," +
                Course.KEY_people + "," +
                Course.KEY_totalHr + "," +
                Course.KEY_kAdd + "," +
                Course.KEY_sAdd + "," +
                Course.KEY_labor + "," +
                Course.KEY_contact + "," +
                Course.KEY_phone + "," +
                Course.KEY_aYear + "," +
                Course.KEY_aMonth + "," +
                Course.KEY_aDay + "," +
                Course.KEY_eYear + "," +
                Course.KEY_eMonth + "," +
                Course.KEY_eDay +
        " FROM " + Course.TABLE;


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


    public Cursor getStudentListByKeyword(String search) {
        //Open connection to read only
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT  rowid as " +
                Course.KEY_ROWID + "," +
                Course.KEY_courseID + "," +
                Course.KEY_name + "," +
                Course.KEY_teacher + "," +
                Course.KEY_applyDate + "," +
                Course.KEY_startApplyDate + "," +
                Course.KEY_startEnd + "," +
                Course.KEY_classDay + "," +
                Course.KEY_people + "," +
                Course.KEY_totalHr + "," +
                Course.KEY_kAdd + "," +
                Course.KEY_sAdd + "," +
                Course.KEY_labor + "," +
                Course.KEY_contact + "," +
                Course.KEY_phone + "," +
                Course.KEY_aYear + "," +
                Course.KEY_aMonth + "," +
                Course.KEY_aDay + "," +
                Course.KEY_eYear + "," +
                Course.KEY_eMonth + "," +
                Course.KEY_eDay +
                " FROM " + Course.TABLE +
                " WHERE " + Course.KEY_name + "  LIKE  '%" + search + "%'or " + Course.KEY_teacher + "  LIKE  '%" + search + "%'or " +  Course.KEY_classDay + "  LIKE  '%" + search + "%'";


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
        return db.delete(Course.TABLE, Course.KEY_courseID +  "= '" + id + "' ", null) > 0;
    }
    public int update(String id,Course course) {
        //Open connection to read only
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        //values.put(Course.KEY_isSelected, course.isSelected);

        return db.update(Course.TABLE,values, Course.KEY_courseID + "=" + id, null);
    }
}