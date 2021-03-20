package com.tcftu.user.tcftuApp;

/**
 * Created by user on 2017/7/17.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;
import android.preference.PreferenceManager;

public class DBHelper  extends SQLiteOpenHelper {
    //version number to upgrade database version
    //each time if you Add, Edit table, you need to change the
    //version number.
    private static final int DATABASE_VERSION = 9;
    // Database Name
    private static final String DATABASE_NAME = "myCourse.db";
    private Context context;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        //All necessary tables you like to create will create here

        String CREATE_TABLE_COURSE = "CREATE TABLE " + Course.TABLE  + "("
                + Course.KEY_courseID + " TEXT, "
                + Course.KEY_name + " TEXT, "
                + Course.KEY_teacher + " TEXT, "
                + Course.KEY_applyDate + " TEXT, "
                + Course.KEY_startApplyDate + " TEXT, "
                + Course.KEY_startEnd + " TEXT, "
                + Course.KEY_classDay + " TEXT, "
                + Course.KEY_people + " TEXT, "
                + Course.KEY_totalHr + " TEXT, "
                + Course.KEY_kAdd + " TEXT, "
                + Course.KEY_sAdd + " TEXT, "
                + Course.KEY_labor + " TEXT, "
                + Course.KEY_contact + " TEXT, "
                + Course.KEY_phone + " TEXT, "
                + Course.KEY_aYear + " Integer, "
                + Course.KEY_aMonth + " Integer, "
                + Course.KEY_aDay + " Integer, "
                + Course.KEY_eYear + " Integer, "
                + Course.KEY_eMonth + " Integer, "
                + Course.KEY_eDay + " Integer)  ";

        db.execSQL(CREATE_TABLE_COURSE);

        String CREATE_TABLE_FOLLOWED = "CREATE TABLE " + Followed.TABLE  + "("
                + Followed.KEY_courseID + " TEXT, "
                + Followed.KEY_name + " TEXT, "
                + Followed.KEY_teacher + " TEXT, "
                + Followed.KEY_applyDate + " TEXT, "
                + Followed.KEY_startApplyDate + " TEXT, "
                + Followed.KEY_startEnd + " TEXT, "
                + Followed.KEY_classDay + " TEXT, "
                + Followed.KEY_people + " TEXT, "
                + Followed.KEY_totalHr + " TEXT, "
                + Followed.KEY_kAdd + " TEXT, "
                + Followed.KEY_sAdd + " TEXT, "
                + Followed.KEY_labor + " TEXT, "
                + Followed.KEY_contact + " TEXT, "
                + Followed.KEY_phone + " TEXT, "
                + Followed.KEY_aYear + " Integer, "
                + Followed.KEY_aMonth + " Integer, "
                + Followed.KEY_aDay + " Integer, "
                + Course.KEY_eYear + " Integer, "
                + Course.KEY_eMonth + " Integer, "
                + Course.KEY_eDay + " Integer) ";

        db.execSQL(CREATE_TABLE_FOLLOWED);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // Drop older table if existed, all data will be gone!!!
            db.execSQL("DROP TABLE IF EXISTS " + Course.TABLE);
            // Create tables again
            //onCreate(db);
            SharedPreferences updateData = context.getSharedPreferences("updateData", 0);
            updateData.edit().putBoolean("ifUpdate", true).apply(); //升級時 新增資料的Activity 升級標記設為 true

            SharedPreferences updateData2 = context.getSharedPreferences("updateData2", 0);
            updateData2.edit().putBoolean("ifUpdate2", true).apply(); //升級時 刪除資料的Activity 升級標記設為 true

            SharedPreferences updateData3 = context.getSharedPreferences("updateData3", 0);
            updateData3.edit().putBoolean("ifUpdate3", true).apply(); //升級時 刪除資料的Activity 升級標記設為 true

            Intent intent = new Intent();
            //從DBHelper 到MainActivity
            intent.setClass(context, MainActivity.class);
            //開啟Activity
            context.startActivity(intent);
            ((Activity)context).finish(); //關閉本頁

            onCreate_course(db);
        /*
                final ProgressDialog dialog = ProgressDialog.show(context,
                        "資料新增中", "請稍後....", true);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(10000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            dialog.dismiss();
                        }
                    }
                }).start();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {

                        //過10秒後要做的事情

                    }
                }, 10000);
            */
    }
    private void onCreate_course(SQLiteDatabase db) {
        String CREATE_TABLE_COURSE = "CREATE TABLE " + Course.TABLE  + "("
                + Course.KEY_courseID + " TEXT, "
                + Course.KEY_name + " TEXT, "
                + Course.KEY_teacher + " TEXT, "
                + Course.KEY_applyDate + " TEXT, "
                + Course.KEY_startApplyDate + " TEXT, "
                + Course.KEY_startEnd + " TEXT, "
                + Course.KEY_classDay + " TEXT, "
                + Course.KEY_people + " TEXT, "
                + Course.KEY_totalHr + " TEXT, "
                + Course.KEY_kAdd + " TEXT, "
                + Course.KEY_sAdd + " TEXT, "
                + Course.KEY_labor + " TEXT, "
                + Course.KEY_contact + " TEXT, "
                + Course.KEY_phone + " TEXT, "
                + Course.KEY_aYear + " Integer, "
                + Course.KEY_aMonth + " Integer, "
                + Course.KEY_aDay + " Integer, "
                + Course.KEY_eYear + " Integer, "
                + Course.KEY_eMonth + " Integer, "
                + Course.KEY_eDay + " Integer)  ";

        db.execSQL(CREATE_TABLE_COURSE);
    }
}