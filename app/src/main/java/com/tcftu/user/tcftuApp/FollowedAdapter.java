package com.tcftu.user.tcftuApp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.icu.util.Calendar;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import static android.content.Context.ALARM_SERVICE;
import static com.tcftu.user.tcftuApp.R.id.switch1;

/**
 * Created by user on 2017/7/24.
 */

public class FollowedAdapter extends CursorAdapter {
    private LayoutInflater mInflater;

    public FollowedAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = mInflater.inflate(R.layout.item2, parent, false);
        FollowedAdapter.ViewHolder holder = new FollowedAdapter.ViewHolder();
        holder.txtName = (TextView) view.findViewById(R.id.txtName);
        holder.txtTe = (TextView) view.findViewById(R.id.txtTe);
        holder.txtApDate = (TextView) view.findViewById(R.id.txtApDate);
        holder.switch1 = (Switch) view.findViewById(switch1);
        holder.switch1.setTag(cursor.getPosition()); //紀錄被按下的位置

        view.setTag(holder);
        return view;
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void bindView(final View view, final Context context, final Cursor cursor) {

        final FollowedAdapter.ViewHolder holder  =   (FollowedAdapter.ViewHolder)view.getTag();

        holder.txtName.setText(cursor.getString(cursor.getColumnIndex(Followed.KEY_courseID))+" "+cursor.getString(cursor.getColumnIndex(Followed.KEY_name)));
        holder.txtTe.setText(cursor.getString(cursor.getColumnIndex(Followed.KEY_teacher))+" 老師");
        holder.txtApDate.setText(cursor.getString(cursor.getColumnIndex(Followed.KEY_startApplyDate))+" 起開始報名");
        /**********************************************保持通知開關狀態**********************************************/
        final int position = (int)holder.switch1.getTag(); //存入被按的位置
        cursor.moveToPosition(position); //移到此位置
        final String courseID = cursor.getString(1); //抓取課程ID

        final SharedPreferences preferences = context.getSharedPreferences("data",0); //取得SharedPreferences ， 丟入的參數為("名稱" , 存取權限)
        final boolean Checked = preferences.getBoolean(courseID, false);  //取出資料， 丟入的參數為(key , 若是沒值，預設為false)
        holder.switch1.setChecked(Checked); //設定當初按下的狀態

        /*日曆宣告*/
        final Calendar cal = Calendar.getInstance(); //宣告Calendar來設定推播時間
        cal.setTimeInMillis(System.currentTimeMillis());
        /**********************************************開關按下去事件**********************************************/
        holder.switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {

                if (isChecked) {
                    cursor.moveToPosition(position); //移到被按的位置
                    cal.clear(); //清除時間
                    cal.set(cursor.getInt(15), cursor.getInt(16)-1, cursor.getInt(17)-1, 11, 00); //設定要推播時間
                    String courseName = cursor.getString(2); //抓取課程名稱
                    String courseID = cursor.getString(1); //抓取課程ID
                    startAlarm(cal,position,context, courseName,courseID); //呼叫 開啟 推播方法

                    preferences.edit().putBoolean(courseID, true).apply();  //存入開關為開，丟入的參數為(key , value)
                    Toast.makeText(context,"開啟通知", Toast.LENGTH_SHORT).show();

                } else {
                    closeAlarm(position,context); //呼叫 關閉 推播方法

                    preferences.edit().putBoolean(courseID,false).apply();  //存入開關為關，丟入的參數為(key , value)
                    Toast.makeText(context,"關閉通知", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    /*******************************************推播通知*******************************************/
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void startAlarm(Calendar cal,int position,Context context,String courseName,String courseID) {

        Intent myIntent;
        PendingIntent pendingIntent;

        AlarmManager manager = (AlarmManager)context.getSystemService(ALARM_SERVICE);
        myIntent = new Intent(context,AlarmNotificationReceiver.class);
        // 寫入資料到 myIntent 中 ，並傳送到通知欄裡
        myIntent.putExtra("courseName",courseName);
        myIntent.putExtra("courseID",courseID);
        myIntent.putExtra("position",position);

        pendingIntent = PendingIntent.getBroadcast(context,position,myIntent,PendingIntent.FLAG_CANCEL_CURRENT);
        manager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),pendingIntent); //開啟 推播功能
    }
    private void closeAlarm(int position,Context context) {

        AlarmManager manager = (AlarmManager)context.getSystemService(ALARM_SERVICE);
        Intent myIntent;
        PendingIntent pendingIntent;

        myIntent = new Intent(context,AlarmNotificationReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(context,position,myIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        manager.cancel(pendingIntent); //關閉 推播功能
    }

    static class ViewHolder {
        TextView txtName;
        TextView txtTe;
        TextView txtApDate;
        Switch switch1;
    }
}
