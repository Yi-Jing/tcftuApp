package com.tcftu.user.tcftuApp;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.MODE_PRIVATE;
import static com.tcftu.user.tcftuApp.R.id.imageStar;

public class CustomAdapter extends CursorAdapter {
    private LayoutInflater mInflater;

    public CustomAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View newView(final Context context, final Cursor cursor, final ViewGroup parent) {

        View view;
        final ViewHolder holder;

        view = mInflater.inflate(R.layout.item, parent, false);
        holder = new ViewHolder();
        holder.txtName = (TextView) view.findViewById(R.id.txtName);
        holder.txtTe = (TextView) view.findViewById(R.id.txtTe);
        holder.txtApDate = (TextView) view.findViewById(R.id.txtApDate);
        holder.imageStar = (ImageView) view.findViewById(R.id.imageStar);
        view.setTag(holder);

        holder.imageStar.setTag(cursor.getString(1));

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void bindView(View view,final Context context,Cursor cursor) {

        final ViewHolder holder = (ViewHolder) view.getTag();
        holder.txtName.setText(cursor.getString(cursor.getColumnIndex(Course.KEY_courseID)) + " " + cursor.getString(cursor.getColumnIndex(Course.KEY_name)));
        holder.txtTe.setText(cursor.getString(cursor.getColumnIndex(Course.KEY_teacher)) + " ??????");
        holder.txtApDate.setText(cursor.getString(cursor.getColumnIndex(Course.KEY_startApplyDate)) + " ???????????????");

        /*************????????????????????????*************/
        final String courseID = cursor.getString(1);
        final String name = cursor.getString(2);
        final String teacher = cursor.getString(3);
        final String applyDate = cursor.getString(4);
        final String startApplyDate = cursor.getString(5);
        final String startEnd = cursor.getString(6);
        final String classDay = cursor.getString(7);
        final String people = cursor.getString(8);
        final String totalHr = cursor.getString(9);
        final String kAdd = cursor.getString(10);
        final String sAdd = cursor.getString(11);
        final String labor = cursor.getString(12);
        final String contact = cursor.getString(13);
        final String phone = cursor.getString(14);
        final int aYear = cursor.getInt(15);
        final int aMonth = cursor.getInt(16);
        final int aDay = cursor.getInt(17);
        final int eYear = cursor.getInt(18);
        final int eMonth = cursor.getInt(19);
        final int eDay = cursor.getInt(20);
        final int position = cursor.getPosition();

        /********?????? ????????????????????? ???????????????********/
        final SharedPreferences preferences = context.getSharedPreferences("data", 0); //??????SharedPreferences ??? ??????????????????("??????" , ????????????)
        final SharedPreferences preferences1 = context.getSharedPreferences("star", MODE_PRIVATE); //??????SharedPreferences ??? ??????????????????("??????" , ????????????)

        /********************************???????????????????????????????????????********************************/
        final boolean Checked = preferences1.getBoolean(courseID, false);  //??????????????? ??????????????????1(key , ????????????????????????false)
        if (Checked == true) {
            holder.imageStar.setImageResource(R.mipmap.ic_srar_yellow);//???????????????????????????
        }
        else
        {
            holder.imageStar.setImageResource(R.mipmap.ic_srar_gray);
        }
        /***************************************???????????????????????????***************************************/
        holder.imageStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Checked == false) {
                    holder.imageStar.setImageResource(R.mipmap.ic_srar_yellow); //????????????????????????
                    preferences1.edit().putBoolean(courseID, true).apply();  //???????????????????????????????????????(key , value)

                    /*??????????????????????????? ??????????????????*/
                    sendFollowCourse(context, courseID, name, teacher, applyDate, startApplyDate, startEnd,
                            classDay, people, totalHr, kAdd, sAdd, labor, contact, phone, aYear, aMonth, aDay,
                            eYear, eMonth, eDay,position);

                    preferences.edit().putBoolean(courseID, true).apply();  //???????????????????????????????????????????????????(key , value)
                }
                else {
                    Toast.makeText(context, "????????????????????????", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void sendFollowCourse(Context context, String courseID, String name, String teacher, String applyDate, String startApplyDate, String startEnd,
                                  String classDay, String people, String totalHr, String kAdd, String sAdd, String labor, String contact,
                                  String phone, int aYear, int aMonth, int aDay, int eYear, int eMonth, int eDay,int position) {

        /*??????????????????????????????*/
        //?????? Intent ??????
        Intent intent = new Intent(context, Main2Activity.class);
        //?????? Bundle ??????
        Bundle FollowedCourse = new Bundle();
        // ??????????????? Bundle ???
        FollowedCourse.putString("courseID", courseID);
        FollowedCourse.putString("name", name);
        FollowedCourse.putString("teacher", teacher);
        FollowedCourse.putString("applyDate",applyDate);
        FollowedCourse.putString("startApplyDate",startApplyDate);
        FollowedCourse.putString("startEnd", startEnd);
        FollowedCourse.putString("classDay", classDay);
        FollowedCourse.putString("people", people);
        FollowedCourse.putString("totalHr", totalHr);
        FollowedCourse.putString("kAdd",kAdd);
        FollowedCourse.putString("sAdd",sAdd);
        FollowedCourse.putString("labor",labor);
        FollowedCourse.putString("contact",contact);
        FollowedCourse.putString("phone", phone);
        FollowedCourse.putInt("aYear", aYear);
        FollowedCourse.putInt("aMonth", aMonth);
        FollowedCourse.putInt("aDay", aDay);
        FollowedCourse.putInt("eYear", eYear);
        FollowedCourse.putInt("eMonth", eMonth);
        FollowedCourse.putInt("eDay", eDay);

        intent.putExtras(FollowedCourse); //??????????????? Main2Activity

        Calendar cal = Calendar.getInstance(); //??????Calendar?????????????????????
        cal.clear(); //????????????
        cal.set(aYear, aMonth - 1, aDay - 1, 11, 00); //?????????????????????
        startAlarm(cal,position,context, name,courseID); //?????? ?????? ????????????

        context.startActivity(intent); //????????????????????????
        ((Activity) context).finish(); //????????????
        Toast.makeText(context, "????????????", Toast.LENGTH_SHORT).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void startAlarm(Calendar cal,int position,Context context,String courseName,String courseID) {

        Intent myIntent;
        PendingIntent pendingIntent;

        AlarmManager manager = (AlarmManager)context.getSystemService(ALARM_SERVICE);
        myIntent = new Intent(context,AlarmNotificationReceiver.class);
        // ??????????????? myIntent ??? ???????????????????????????
        myIntent.putExtra("courseName",courseName);
        myIntent.putExtra("courseID",courseID);
        myIntent.putExtra("position",position);

        pendingIntent = PendingIntent.getBroadcast(context,position,myIntent,PendingIntent.FLAG_CANCEL_CURRENT);
        manager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),pendingIntent); //?????? ????????????
    }
    static class ViewHolder {
        TextView txtName;
        TextView txtTe;
        TextView txtApDate;
        ImageView imageStar;
    }
}