
/*

************************************** 　查　詢　課　程　主　頁　面　**************************************

 */

package com.tcftu.user.tcftuApp;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.icu.util.Calendar;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import android.database.Cursor;
import android.support.v7.widget.SearchView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Main3Activity extends AppCompatActivity {

    CustomAdapter customAdapter;
    ListView listView;
    Cursor cursor;
    CourseRepo courseRepo;
    Cursor cursor1;
    FollowedRepo followedRepo;
    private AlertDialog newVersionDialog; //新版本對話框
    private AlertDialog newDialog; //偵測網路對話框
    private final static String TAG = Main3Activity.class.getName().toString();


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        SharedPreferences oneStart2 = getSharedPreferences("oneStart2", 0);
        boolean ifOne2 = oneStart2.getBoolean("ifOne2", false);

        SharedPreferences updateData2 = getSharedPreferences("updateData2", 0);
        boolean ifUpdate2 = updateData2.getBoolean("ifUpdate2", false);

        SharedPreferences updateData3 = getSharedPreferences("updateData3", 0);
        final SharedPreferences preferences = Main3Activity.this.getSharedPreferences("data",0); //取得SharedPreferences ， 丟入的參數為("名稱" , 存取權限)

        followedRepo = new FollowedRepo(this); //建立 正在追蹤 所要新增的欄位
        cursor1 = followedRepo.getStudentList(); //加載 正在追蹤 資料表

        courseRepo = new CourseRepo(this); //建立 追蹤課程 所要新增的欄位
        cursor = courseRepo.getStudentList(); //加載 追蹤課程 資料表

        ConnectivityManager mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();

        newDialog = alertDialog1("請檢查網路連線");

        //如果未連線的話，mNetworkInfo會等於null
        if (mNetworkInfo == null) {
            newDialog.show();
        }
        /******************偵測App版本跟Google Play商店App版本有沒有一致******************/

        /*偵測本版本*/
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        final String myVerName = packageInfo.versionName; //此App版本

        /*抓取Google Play商店APP版本號*/
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        final String url = "https://play.google.com/store/apps/details?id=com.tcftu.user.tcftuApp";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Pattern pattern = Pattern.compile("\"softwareVersion\"\\W*([\\d\\.]+)");
                        Matcher matcher = pattern.matcher(response);
                        if (matcher.find()) {
                            if (!myVerName.equals(matcher.group(1))) {
                                newVersionDialog.show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(MainActivity.this, "無法偵測目前版本", Toast.LENGTH_SHORT).show();
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
        newVersionDialog = alertDialog("有新版本\n請立即更新！");

        /******************若第一次進來(代表APP第一次下載)刪除資料，並設定時間在以後過期時刪除******************/
        if (ifOne2 == false | ifUpdate2 == true) {

            /*****************設定時間已刪除過期資料*****************/
            Calendar cal = Calendar.getInstance(); //宣告Calendar來設定刪除時間
            cal.setTimeInMillis(System.currentTimeMillis());

            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i); //移到位置
                cal.clear(); //清除時間
                cal.set(cursor.getInt(18), cursor.getInt(19) - 1, cursor.getInt(20), 0, 0); //設定要刪除時間
                String courseID = cursor.getString(1); //抓取課程ID
                timeDelete(cal, courseID, i); //呼叫 刪除資料方法
            }
            /*若正在追蹤有課程，當資料庫修改時，跟著修改被更新的資料*/
            if (cursor1 != null) {
                for (int i = 0; i < cursor.getCount(); i++) {
                    cursor.moveToPosition(i); //移到位置
                    for (int m = 0; m < cursor1.getCount(); m++) {
                        cursor1.moveToPosition(m); //移到位置
                        if (cursor.getString(1).equals(cursor1.getString(1))) {
                            String courseID = cursor1.getString(1); //抓取課程ID
                            String courseName = cursor1.getString(2); //抓取課程名稱

                            boolean Checked = preferences.getBoolean(courseID, false);  //取出資料， 丟入的參數為(key , 若是沒值，預設為false)
                            closeAlarm(m,courseID); //清除 更新前 推播時間
                            cleartimeDelete1(courseID,m); //清除 更新前 刪除時間

                            Followed followed = new Followed();
                            followedRepo = new FollowedRepo(this);

                            followed.applyDate = cursor.getString(4);
                            followed.startApplyDate = cursor.getString(5);
                            followed.startEnd = cursor.getString(6);
                            followed.classDay = cursor.getString(7);
                            followed.aYear = cursor.getInt(15);
                            followed.aMonth = cursor.getInt(16);
                            followed.aDay = cursor.getInt(17);
                            followed.eYear = cursor.getInt(18);
                            followed.eMonth = cursor.getInt(19);
                            followed.eDay = cursor.getInt(20);
                            followedRepo.update(cursor1.getString(1), followed);

                            /*把更改過的資訊，重新設定推播時間*/
                            if (Checked == true) {
                                Calendar cal1 = Calendar.getInstance(); //宣告Calendar來設定推播時間
                                cal1.clear(); //清除時間
                                cal1.set(cursor1.getInt(15), cursor1.getInt(16) - 1, cursor1.getInt(17) - 1, 11, 00); //設定要推播時間

                                startAlarm(cal1, m, courseName, courseID); //呼叫 開啟 推播方法
                                preferences.edit().putBoolean(courseID, true).apply();  //存入開關為開，丟入的參數為(key , value)
                            }
                        }
                    }
                }
            }
            final ProgressDialog dialog = ProgressDialog.show(Main3Activity.this,
                    "資料載入中", "請稍後....", true);
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
                    onRefresh();
                }
            }, 10000);
            oneStart2.edit().putBoolean("ifOne2", true).apply();
            updateData2.edit().putBoolean("ifUpdate2", false).apply();
            updateData3.edit().putBoolean("ifUpdate3", false).apply();
        }
        /*******************************查詢課程為空，顯示訊息*******************************/
        else if (cursor == null) {
            Toast.makeText(Main3Activity.this, "目前尚未公告課程！", Toast.LENGTH_LONG).show();
        }
        /**********************並非第一次進來且資料庫不為空，就照常加載資料到表單**********************/
        else {
            customAdapter = new CustomAdapter(Main3Activity.this, cursor, 0); //加載資料表到此已設定Layout listView
            listView = (ListView) findViewById(R.id.lstStudent); //決定listView加載到哪個Layout file
            listView.setAdapter(customAdapter); //寫入listView為之前設定的資料表

            /*每個清單的點擊事件*/
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // 建立 Bundle 物件
                    Bundle listPosition = new Bundle();
                    // 寫入資料到 Bundle 中
                    listPosition.putString("num", cursor.getString(1)); /*取得該點選值的課程ID，並存入listPosition，已提供給資訊視窗*/
                    //建立CourseDialogFragment物件
                    CourseDialogFragment dialog = new CourseDialogFragment();
                    // 將 Bundle 指定到 DialogFragment
                    dialog.setArguments(listPosition);
                    /*把資料帶到DialogFragment已出現資訊視窗*/
                    dialog.show(getFragmentManager(), "TAG");
                }
            });
        }
    }
    /*** 產生「新版本更新」對話框 ***/
    private AlertDialog alertDialog(String title) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(this)
                        .setTitle(title)
                        .setIcon(R.drawable.ic_sync_problem_black_24dp)
                        .setPositiveButton("更新", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                go2GooglePlay();
                                finish();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
        return builder.create();
    }

    /*** 前往Google play ***/
    private void go2GooglePlay() {
        final String appPackageName = this.getPackageName(); // getPackageName() from Context or Activity object
        try {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }
    /*** 產生「偵測網路」對話框 ***/
    private AlertDialog alertDialog1(String title){
        AlertDialog.Builder builder =
                new AlertDialog.Builder(this)
                        .setTitle(title)
                        .setIcon(R.drawable.ic_signal_cellular_connected_no_internet_0_bar_black_24dp)
                        .setPositiveButton("確定開啟", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                ConnectivityManager ConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                                NetworkInfo NetworkInfo = ConnectivityManager.getActiveNetworkInfo();

                                if (NetworkInfo != null) {
                                    dialog.dismiss();
                                }
                                else {
                                    newDialog = alertDialog1("網路連線失敗，請再檢查");
                                    newDialog.show();
                                    onResume();
                                }
                            }
                        });
        return builder.create();
    }
    @Override
    protected void onResume() {
        super.onResume();

        //對話框按下back不能取消的監聽
        newVersionDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

                if ((keyCode == KeyEvent.KEYCODE_BACK)) {
                    return true;
                } else {
                    return false;
                }
            }
        });
        //對話框按下back不能取消的監聽
        newDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

                if ((keyCode == KeyEvent.KEYCODE_BACK)) {
                    return true;
                } else {
                    return false;
                }
            }
        });
        //對話框碰到外邊不會被取消
        newVersionDialog.setCanceledOnTouchOutside(false);
        newDialog.setCanceledOnTouchOutside(false);
    }
    /*******************************************推播通知*******************************************/
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void startAlarm(Calendar cal,int position,String courseName,String courseID) {

        Intent myIntent;
        PendingIntent pendingIntent;

        AlarmManager manager = (AlarmManager)Main3Activity.this.getSystemService(ALARM_SERVICE);
        myIntent = new Intent(Main3Activity.this,AlarmNotificationReceiver.class);
        // 寫入資料到 myIntent 中 ，並傳送到通知欄裡
        myIntent.putExtra("courseName",courseName);
        myIntent.putExtra("courseID",courseID);
        myIntent.putExtra("position",position);

        pendingIntent = PendingIntent.getBroadcast(Main3Activity.this,position,myIntent,PendingIntent.FLAG_CANCEL_CURRENT);
        manager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),pendingIntent); //開啟 推播功能
    }
    private void closeAlarm(int i,String courseID) {

        AlarmManager manager = (AlarmManager)Main3Activity.this.getSystemService(ALARM_SERVICE);
        Intent myIntent;
        PendingIntent pendingIntent;

        myIntent = new Intent(Main3Activity.this,AlarmNotificationReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(Main3Activity.this,i,myIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        manager.cancel(pendingIntent); //關閉 推播功能
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void timeDelete(Calendar cal, String courseID,int i) {

        Intent myIntent;
        PendingIntent pendingIntent;

        AlarmManager manager = (AlarmManager)Main3Activity.this.getSystemService(ALARM_SERVICE);
        myIntent = new Intent(Main3Activity.this,TimeUpDeleteDataReceiver.class);
        // 寫入資料到 myIntent 中 ，利用ID去刪除資料
        myIntent.putExtra("courseID",courseID);

        pendingIntent = PendingIntent.getBroadcast(Main3Activity.this,i,myIntent,PendingIntent.FLAG_CANCEL_CURRENT);
        manager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),pendingIntent); //呼叫刪除資料的方法
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void timeDelete1(Calendar cal, String courseID, int i) {

        Intent myIntent;
        PendingIntent pendingIntent;

        AlarmManager manager = (AlarmManager)Main3Activity.this.getSystemService(ALARM_SERVICE);
        myIntent = new Intent(Main3Activity.this,TimeUpDeleteDataReceiver1.class);
        // 寫入資料到 myIntent 中 ，利用ID去刪除資料
        myIntent.putExtra("courseID",courseID);

        pendingIntent = PendingIntent.getBroadcast(Main3Activity.this,i,myIntent,PendingIntent.FLAG_CANCEL_CURRENT);
        manager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),pendingIntent); //呼叫刪除資料的方法
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void cleartimeDelete1(String courseID, int m) {
        Intent myIntent;
        PendingIntent pendingIntent;

        AlarmManager manager = (AlarmManager)Main3Activity.this.getSystemService(ALARM_SERVICE);
        myIntent = new Intent(Main3Activity.this,AlarmNotificationReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(Main3Activity.this,m,myIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        final SharedPreferences preferences = Main3Activity.this.getSharedPreferences("data",0); //取得SharedPreferences ， 丟入的參數為("名稱" , 存取權限)
        preferences.edit().remove(courseID).commit();

        manager.cancel(pendingIntent); //關閉 推播功能
    }
    public void onRefresh() {
        cursor = courseRepo.getStudentList(); //加載 追蹤課程 資料表

        customAdapter = new CustomAdapter(Main3Activity.this, cursor, 0); //加載資料表到此已設定Layout listView
        listView = (ListView) findViewById(R.id.lstStudent); //決定listView加載到哪個Layout file
        listView.setAdapter(customAdapter); //寫入listView為之前設定的資料表

        /*每個清單的點擊事件*/
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 建立 Bundle 物件
                Bundle listPosition = new Bundle();
                // 寫入資料到 Bundle 中
                listPosition.putString("num", cursor.getString(1)); /*取得該點選值的課程ID，並存入listPosition，已提供給資訊視窗*/
                //建立CourseDialogFragment物件
                CourseDialogFragment dialog = new CourseDialogFragment();
                // 將 Bundle 指定到 DialogFragment
                dialog.setArguments(listPosition);
                /*把資料帶到DialogFragment已出現資訊視窗*/
                dialog.show(getFragmentManager(), "TAG");
            }
        });
    }
    /*按下返回鍵一律回到首頁*/
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        //從Main3Activity 到MainActivity
        intent.setClass(Main3Activity.this , MainActivity.class);
        //開啟Activity
        startActivity(intent);
        Main3Activity.this.finish(); //關閉本頁
    }
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.search, menu);


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            SearchView search = (SearchView) menu.findItem(R.id.search).getActionView();
            search.setSearchableInfo(manager.getSearchableInfo(getComponentName()));

            search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                @Override
                public boolean onQueryTextSubmit(String s) {
                    Log.d(TAG, "onQueryTextSubmit ");
                    cursor= courseRepo.getStudentListByKeyword(s);
                    if (cursor==null){
                        Toast.makeText(Main3Activity.this,"查無此課程",Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(Main3Activity.this,"搜尋到 "+ cursor.getCount() + " 筆課程",Toast.LENGTH_LONG).show();
                    }
                    customAdapter.swapCursor(cursor);

                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    Log.d(TAG, "onQueryTextChange ");
                    cursor= courseRepo.getStudentListByKeyword(s);
                    if (cursor!=null){
                        customAdapter.swapCursor(cursor);
                    }
                    return false;
                }

            });
        }
        return true;
    }
}
