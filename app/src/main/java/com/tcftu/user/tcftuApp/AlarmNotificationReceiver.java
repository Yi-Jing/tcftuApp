package com.tcftu.user.tcftuApp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by user on 2017/8/2.
 */

public class AlarmNotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //Step1. 初始化NotificationManager，取得Notification服務
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(NOTIFICATION_SERVICE);
        // 接受 intent 中的資料
        String courseName = (String) intent.getExtras().get("courseName");
        String courseID = (String) intent.getExtras().get("courseID");
        int position = (int) intent.getExtras().get("position");

        /*新建工會課程編號*/
        String tcftuCID = courseID.substring(4,courseID.length());
        /*取出字頭*/
        char title = courseID.charAt(0);

        //Step2. 設定當按下這個通知之後要執行的activity
        Intent notifyIntent = new Intent(Intent.ACTION_VIEW);
        /*判斷是產投網頁 還是 工會網頁*/
        String url;
        // 字頭為A代表工會課程編號
        if (String.valueOf(title).equals("A")){
            url = "http://www.tcftu.com/news_show.asp?readclass=2&page=1&readno=" + tcftuCID;
        }
        else {
            url = "https://tims.etraining.gov.tw/timsonline/index3.aspx?OCID=" + courseID;
        }
        notifyIntent.setData(Uri.parse(url));

        notifyIntent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pending = PendingIntent.getActivity(context, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //Step3. 透過 Notification.Builder 來建構 notification，
        //並直接使用其.build() 的方法將設定好屬性的 Builder 轉換
        //成 notification，最後開始將顯示通知訊息發送至狀態列上。
        int color = Color.argb(255, 49, 185, 110);
        Notification notification = new Notification.Builder(context)
                .setContentIntent(pending)
                .setWhen(System.currentTimeMillis())
                .setShowWhen(true) //顯示發出的時間
                .setColor(color)
                .setSmallIcon(R.mipmap.icon_1) // 設置狀態列裡面的圖示（小圖示）
                //.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_info_black_24dp)) // 下拉下拉清單裡面的圖示（大圖示）
                .setContentTitle("您有 " + courseName + " 將要/正在開始報名")
                .setContentText("輕觸前往報名網頁")
                //.setSubText("1門課程")
                .setAutoCancel(true) // 設置通知被使用者點擊後是否清除  //notification.flags = Notification.FLAG_AUTO_CANCEL;
                .setOngoing(false)   //true使notification變為ongoing，用戶不能手動清除// notification.flags = Notification.FLAG_ONGOING_EVENT; notification.flags = Notification.FLAG_NO_CLEAR;
                .setDefaults(Notification.DEFAULT_ALL) //使用所有默認值，比如聲音，震動，閃屏等等
                .build();
        notificationManager.notify(position,notification);
    }
}
