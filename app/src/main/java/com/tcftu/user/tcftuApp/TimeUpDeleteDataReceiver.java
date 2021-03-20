package com.tcftu.user.tcftuApp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

/**
 * Created by user on 2017/8/10.
 */

public class TimeUpDeleteDataReceiver extends BroadcastReceiver {
    Cursor cursor;
    CourseRepo courseRepo;

    @Override
    public void onReceive(Context context, Intent intent) {
        /*連接資料庫*/
        courseRepo = new CourseRepo(context);
        cursor= courseRepo.getStudentList();
        // 接受 intent 中的資料
        String courseID = (String) intent.getExtras().get("courseID");
        courseRepo.delete(courseID);
    }
}
