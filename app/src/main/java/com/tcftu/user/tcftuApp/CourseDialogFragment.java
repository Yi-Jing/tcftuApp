/*********************************************出現課程資訊框*******************************************************/
package com.tcftu.user.tcftuApp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

/**
 * Created by user on 2017/7/18.
 */
public class CourseDialogFragment extends DialogFragment {
    Cursor cursor;
    CourseRepo courseRepo;
    Cursor cursor1;
    FollowedRepo followedRepo;
    String courseID;
    String phone;
    @RequiresApi(api = Build.VERSION_CODES.N)
    public Dialog onCreateDialog(Bundle savedlnstanceState){

        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        LayoutInflater inflater= getActivity().getLayoutInflater();

        /*連接資料庫*/
        courseRepo = new CourseRepo(getActivity());
        cursor= courseRepo.getStudentList();
        followedRepo = new FollowedRepo(getActivity());
        cursor1= followedRepo.getStudentList();

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
        //((TextView) tv1).setText(position); //測試（呈現結果每個 position 都不同）
        /*以課程ID來顯示被點選到的所有課程詳細資訊*/
        for (int i=0;i<cursor.getCount();i++) {
            //String courseID;
            cursor.moveToPosition(i); //從頭掃描到資料尾端
            //若該行課程ID等於被點選到的課程ID(position)
            if (cursor.getString(cursor.getColumnIndex(Course.KEY_courseID)).equals(position)) {
                //則顯示該課程所有詳細資訊
                ((TextView) tv1).setText(
                        "課程：" + cursor.getString(cursor.getColumnIndex(Course.KEY_name)) +
                                "\n課程代碼：" + cursor.getString(cursor.getColumnIndex(Course.KEY_courseID)) +
                                "\n報名日期：" + cursor.getString(cursor.getColumnIndex(Course.KEY_applyDate)));
                ((TextView) tv2).setText(
                        "\n訓練期間：" + cursor.getString(cursor.getColumnIndex(Course.KEY_startEnd)) +
                                "\n訓練人數：" + cursor.getString(cursor.getColumnIndex(Course.KEY_people)) +
                                "\n訓練時數：" + cursor.getString(cursor.getColumnIndex(Course.KEY_totalHr)) +
                                 "\n上課時間：\n" + cursor.getString(cursor.getColumnIndex(Course.KEY_classDay)));
                ((TextView) tv3).setText(
                        "\n老師：" + cursor.getString(cursor.getColumnIndex(Course.KEY_teacher)) +
                                "\n學科場地址1：\n" + cursor.getString(cursor.getColumnIndex(Course.KEY_kAdd)) +
                                "\n術科場地址1：\n" + cursor.getString(cursor.getColumnIndex(Course.KEY_sAdd)));
                ((TextView) tv4).setText(
                        "\n訓練單位名稱：\n" + cursor.getString(cursor.getColumnIndex(Course.KEY_labor)) +
                                "\n聯絡人：" + cursor.getString(cursor.getColumnIndex(Course.KEY_contact)));

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

        /*日曆宣告*/
        final Calendar cal = Calendar.getInstance(); //宣告Calendar來設定推播時間
        cal.setTimeInMillis(System.currentTimeMillis());

        /*Dialog視窗*/
        builder.setTitle("▼ 課程詳細內容")
        .setView(v)
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