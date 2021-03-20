package com.tcftu.user.tcftuApp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

/**
 * Created by user on 2017/8/10.
 */

public class TimeUpDeleteDataReceiver1 extends BroadcastReceiver {
    Cursor cursor;
    FollowedRepo followedRepo;

    @Override
    public void onReceive(Context context, Intent intent) {
        /*連接資料庫*/
        followedRepo = new FollowedRepo(context);
        cursor= followedRepo.getStudentList();
        // 接受 intent 中的資料
        String courseID = (String) intent.getExtras().get("courseID");
        followedRepo.delete(courseID);
    }
}
