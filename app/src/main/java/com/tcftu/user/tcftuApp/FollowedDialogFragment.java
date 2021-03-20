package com.tcftu.user.tcftuApp;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.MODE_PRIVATE;

/**
 * Created by user on 2017/7/25.
 */

public class FollowedDialogFragment extends DialogFragment {
    Cursor cursor;
    FollowedRepo followedRepo;
    String courseID;
    String phone;

    public Dialog onCreateDialog(Bundle savedlnstanceState){

        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        LayoutInflater inflater= getActivity().getLayoutInflater();

         /*連接資料庫*/
        followedRepo = new FollowedRepo(getActivity());
        cursor= followedRepo.getStudentList();

         /*顯示課程資訊*/
        View v = inflater.inflate(R.layout.fragment_dialog,null);
        View tv1 = v.findViewById(R.id.Info1);
        View tv2 = v.findViewById(R.id.Info2);
        View tv3 = v.findViewById(R.id.Info3);
        View tv4 = v.findViewById(R.id.Info4);
        View tv5 = v.findViewById(R.id.Info5);

        // 取得 Bundle 中的資料
        Bundle num = getArguments();
        String position = num.getString("num");

        /*以課程ID來顯示被點選到的所有課程詳細資訊*/
        for (int i=0;i<cursor.getCount();i++) {
            cursor.moveToPosition(i); //從投掃描到資料尾端
            //若該行課程ID等於被點選到的課程ID(position)
            if (cursor.getString(cursor.getColumnIndex(Course.KEY_courseID)).equals(position)) {
                //則顯示該課程所有詳細資訊
                ((TextView) tv1).setText(
                        "課程：" + cursor.getString(cursor.getColumnIndex(Followed.KEY_name)) +
                                "\n課程代碼：" + cursor.getString(cursor.getColumnIndex(Followed.KEY_courseID)) +
                                "\n報名日期：" + cursor.getString(cursor.getColumnIndex(Followed.KEY_applyDate)));
                ((TextView) tv2).setText(
                        "\n訓練期間：" + cursor.getString(cursor.getColumnIndex(Followed.KEY_startEnd)) +
                                "\n訓練人數：" + cursor.getString(cursor.getColumnIndex(Followed.KEY_people)) +
                                "\n訓練時數：" + cursor.getString(cursor.getColumnIndex(Followed.KEY_totalHr)) +
                                "\n上課時間：\n" + cursor.getString(cursor.getColumnIndex(Followed.KEY_classDay)));
                ((TextView) tv3).setText(
                        "\n老師：" + cursor.getString(cursor.getColumnIndex(Followed.KEY_teacher)) +
                                "\n學科場地址1：\n" + cursor.getString(cursor.getColumnIndex(Followed.KEY_kAdd)) +
                                "\n術科場地址1：\n" + cursor.getString(cursor.getColumnIndex(Followed.KEY_sAdd)));
                ((TextView) tv4).setText(
                        "\n訓練單位名稱：\n" + cursor.getString(cursor.getColumnIndex(Followed.KEY_labor)) +
                                "\n聯絡人：" + cursor.getString(cursor.getColumnIndex(Followed.KEY_contact)));

                ((TextView) tv5).setText("電話：" + cursor.getString(cursor.getColumnIndex(Course.KEY_phone)));
                courseID  = cursor.getString(1);
                phone = cursor.getString(14);

            }
        }
        /*若點擊電話則呼叫確認是否撥話視窗*/
        tv5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.Info5)
                {
                    normalDialogEvent();
                }
            }
        });
        /*實體化追蹤星星按鈕*/
        final SharedPreferences preferences1 = getActivity().getSharedPreferences("star",MODE_PRIVATE); //取得SharedPreferences ， 丟入的參數為("名稱" , 存取權限)

        /*Dialog視窗*/
        builder
                .setTitle("▼ 課程詳細內容")
                .setView(v)
                .setNeutralButton("取消追蹤", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Bundle num = getArguments();
                        String position = num.getString("num");
                        /*以課程ID來顯示被點選到的所有課程詳細資訊*/
                        for (int i=0;i<cursor.getCount();i++) {
                            cursor.moveToPosition(i); //從投掃描到資料尾端
                            //若該行課程ID等於被點選到的課程ID(position)
                            if (cursor.getString(cursor.getColumnIndex(Course.KEY_courseID)).equals(position)) {

                                String courseID = cursor.getString(1); //紀錄課程ID
                                Intent intent = new Intent(getActivity(),Main3Activity.class);

                                closeAlarm(i,courseID); //呼叫 關閉 推播方法
                                preferences1.edit().remove(courseID).commit(); //移除星星按鈕

                                /*刪除追蹤課程裡該課程所有資訊*/
                                followedRepo.delete(courseID);

                                startActivity(intent);
                                getActivity().finish(); //關閉本頁
                                Toast.makeText(getActivity(),"取消追蹤",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                })
                .setPositiveButton("看更多", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        /*新建工會課程編號*/
                        String tcftuCID = courseID.substring(4,courseID.length());
                        /*取出字頭*/
                        char title = courseID.charAt(0);

                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        /*判斷是產投網頁 還是 工會網頁*/
                        String url;
                        // 字頭為A代表工會課程編號
                        if (String.valueOf(title).equals("A")){
                            url = "http://www.tcftu.com/news_show.asp?readclass=2&page=1&readno=" + tcftuCID;
                        }
                        else {
                            url = "https://tims.etraining.gov.tw/timsonline/index3.aspx?OCID=" + courseID;
                        }
                        intent.setData(Uri.parse(url));
                        startActivity(intent);
                    }
                })
                .setNegativeButton("關閉", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        return builder.create();
    }
    private void closeAlarm(int i,String courseID) {

        AlarmManager manager = (AlarmManager)getActivity().getSystemService(ALARM_SERVICE);
        Intent myIntent;
        PendingIntent pendingIntent;

        myIntent = new Intent(getActivity(),AlarmNotificationReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(getActivity(),i,myIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        final SharedPreferences preferences = getActivity().getSharedPreferences("data",0); //取得SharedPreferences ， 丟入的參數為("名稱" , 存取權限)
        preferences.edit().remove(courseID).commit();

        manager.cancel(pendingIntent); //關閉 推播功能
    }
    /*撥話視窗*/
    private void normalDialogEvent(){
        new AlertDialog.Builder(getActivity())
                .setTitle("您是否要撥打電話？")
                .setIcon(R.drawable.ic_phone_forwarded_blue_24dp)
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        StringBuffer sb = new StringBuffer(phone);
                        sb.deleteCharAt(2);//刪除字串的第3個字，從0開始
                        Intent myIntentDial = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+sb));

                        //改成ACTION_CALL則會直接撥出指定的電話號碼，不經由撥號程式
                        startActivity(myIntentDial);
                    }
                })
                .setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }
}

