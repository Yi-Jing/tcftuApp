
/*

************************************** 　正　在　追　蹤　主　頁　面　**************************************

 */

package com.tcftu.user.tcftuApp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main2Activity extends AppCompatActivity {

    private FollowedAdapter followedAdapter;
    ListView listView;
    Cursor cursor;
    FollowedRepo followedRepo;
    Cursor cursor1;
    CourseRepo courseRepo;
    private AlertDialog newVersionDialog; //新版本對話框
    private AlertDialog newDialog; //偵測網路對話框
    private AlertDialog newUpdateDialog; //提醒用戶更新課程對話框
    private final static String TAG= Main2Activity.class.getName().toString();
    
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        final SharedPreferences updateData3 = getSharedPreferences("updateData3", 0);
        boolean ifUpdate3 = updateData3.getBoolean("ifUpdate3", false);

        followedRepo = new FollowedRepo(this);  //建立 正在追蹤 所要新增的欄位
        cursor = followedRepo.getStudentList(); //加載 正在追蹤 資料表

        courseRepo = new CourseRepo(this); //建立 追蹤課程 所要新增的欄位
        cursor1 = courseRepo.getStudentList(); //加載 追蹤課程 資料表

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

        // 如果沒有勾選「不再顯示」，則顯示對話框
        if (!isCheckboxStateEnabled()) {
            FollowHelpDialogFragment1 dialog = new FollowHelpDialogFragment1();
            dialog.show(getFragmentManager(), "TAG");
        }
        /*更新後，沒有先執行查詢頁面，就先進到追蹤清單，提醒用戶先去查詢頁面*/
        newUpdateDialog = alertDialog2("課程尚未載入");
        if (ifUpdate3 == true){
            newUpdateDialog.show();
        }
        else {
            /****************************************顯示正在追蹤課程****************************************/
            followedAdapter = new FollowedAdapter(Main2Activity.this, cursor, 0);
            listView = (ListView) findViewById(R.id.lstCourse);
            listView.setAdapter(followedAdapter);
            /*接受被追蹤的課程資訊*/
            Bundle bundle = this.getIntent().getExtras();
            if (bundle != null) {
                insertDummy(); //新增被追蹤的課程資訊
                /*重新讀取ListView，使新增進去的資料顯示在頁面上*/
                cursor = followedRepo.getStudentList();
                followedAdapter = new FollowedAdapter(Main2Activity.this, cursor, 0);
                listView = (ListView) findViewById(R.id.lstCourse);
                listView.setAdapter(followedAdapter);
            }
            else if (cursor == null) {
                Toast.makeText(Main2Activity.this, "請追蹤課程！", Toast.LENGTH_SHORT).show();
            }
            /*每個清單的點擊事件*/
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // 建立 Bundle 物件
                    Bundle listPosition = new Bundle();
                    // 寫入資料到 Bundle 中
                    listPosition.putString("num", cursor.getString(1)); /*取得該點選值的課程ID，並存入listPosition，已提供給資訊視窗*/
                    //建立CourseDialogFragment物件
                    FollowedDialogFragment dialog = new FollowedDialogFragment();
                    // 將 Bundle 指定到 DialogFragment
                    dialog.setArguments(listPosition);
                    /*把資料帶到DialogFragment已出現資訊視窗*/
                    dialog.show(getFragmentManager(), "TAG");
                    /*重新讀取ListView，使新增進去的資料顯示在頁面上*/
                }
            });
        }
        /***************************************取得Menu按鈕實體***************************************/
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setSelectedItemId(R.id.navigation_follow); //標記通知圖示
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        /********************************時間到刪除資料********************************/
        if (cursor != null) {
            Calendar cal3 = Calendar.getInstance(); //宣告Calendar來設定刪除時間
            cal3.setTimeInMillis(System.currentTimeMillis());

            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i); //移到位置
                cal3.clear(); //清除時間
                cal3.set(cursor.getInt(18), cursor.getInt(19) - 1, cursor.getInt(20), 0, 0); //設定要刪除時間
                String courseID = cursor.getString(1); //抓取課程ID
                timeDelete(cal3, courseID, i); //呼叫 刪除資料方法
            }
        }
        else {
            Toast.makeText(Main2Activity.this, "請追蹤課程！", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isCheckboxStateEnabled() {
        // 讀取勾選方塊是否被打勾，預設值是未打勾（fasle）
        SharedPreferences settings = getSharedPreferences("showit",0);
        boolean skipMessage = settings.getBoolean("skipMessage", false);  //取出資料， 丟入的參數為(key , 若是沒值，預設為false)

        return skipMessage;
    }
    private void insertDummy(){
         /*接受被追蹤的課程資訊*/
        Bundle bundle = this.getIntent().getExtras();
        String a =  bundle.getString("courseID");
        String b =  bundle.getString("name");
        String c =  bundle.getString("teacher");
        String d =  bundle.getString("applyDate");
        String x =  bundle.getString("startApplyDate");
        String e =  bundle.getString("startEnd");
        String f =  bundle.getString("classDay");
        String g =  bundle.getString("people");
        String h =  bundle.getString("totalHr");
        String i =  bundle.getString("kAdd");
        String j =  bundle.getString("sAdd");
        String k =  bundle.getString("labor");
        String l =  bundle.getString("contact");
        String m =  bundle.getString("phone");
        int n =  bundle.getInt("aYear");
        int o =  bundle.getInt("aMonth");
        int p =  bundle.getInt("aDay");
        int r =  bundle.getInt("eYear");
        int s =  bundle.getInt("eMonth");
        int t =  bundle.getInt("eDay");

        Followed followed= new Followed();

        followedRepo = new FollowedRepo(this);
        followed.courseID = a ;
        followed.name = b ;
        followed.teacher = c ;
        followed.applyDate = d ;
        followed.startApplyDate = x;
        followed.startEnd = e ;
        followed.classDay = f ;
        followed.people = g ;
        followed.totalHr = h ;
        followed.kAdd = i ;
        followed.sAdd = j ;
        followed.labor = k ;
        followed.contact = l ;
        followed.phone = m ;
        followed.aYear = n ;
        followed.aMonth = o ;
        followed.aDay = p ;
        followed.eYear = r ;
        followed.eMonth = s ;
        followed.eDay = t ;
        followedRepo.insert(followed);
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
    /*** 產生「課程更新」對話框 ***/
    private AlertDialog alertDialog2(String title) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(this)
                        .setTitle(title)
                        .setIcon(R.drawable.ic_sync_problem_black_24dp)
                        .setPositiveButton("前往載入", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent();
                                //從Main2Activity 到Main3Activity
                                intent.setClass(Main2Activity.this , Main3Activity.class);
                                //開啟Activity
                                startActivity(intent);
                                Main2Activity.this.finish(); //關閉本頁
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
        //對話框按下back不能取消的監聽
        newUpdateDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
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
        newUpdateDialog.setCanceledOnTouchOutside(false);
    }
    /***********************************設定刪除資料方法***********************************/
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void timeDelete(Calendar cal, String courseID, int i) {

        Intent myIntent;
        PendingIntent pendingIntent;

        AlarmManager manager = (AlarmManager)Main2Activity.this.getSystemService(ALARM_SERVICE);
        myIntent = new Intent(Main2Activity.this,TimeUpDeleteDataReceiver1.class);
        // 寫入資料到 myIntent 中 ，利用ID去刪除資料
        myIntent.putExtra("courseID",courseID);

        pendingIntent = PendingIntent.getBroadcast(Main2Activity.this,i,myIntent,PendingIntent.FLAG_CANCEL_CURRENT);
        manager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),pendingIntent); //呼叫刪除資料的方法
    }
    /****************************按下返回鍵一律回到首頁****************************/
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        //從Main2Activity 到MainActivity
        intent.setClass(Main2Activity.this , MainActivity.class);
        //開啟Activity
        startActivity(intent);
        Main2Activity.this.finish(); //關閉本頁
    }

    /****************************************按下Menu按鈕的事件*************************************/
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation); //取得按鈕的實體
            int[][] states;
            int [] colors;
            ColorStateList navigationViewColorStateList;
            //初始化Intent物件
            Intent intent = new Intent();
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    // 設定狀態
                    states = new int[][]{
                            new int[]{-android.R.attr.state_checked},  // 沒被點擊
                            new int[]{android.R.attr.state_checked}};   // 被點擊
                    // 設定顏色
                    colors = new int[]{
                            Color.parseColor("#6C6C6C"),
                            Color.parseColor("#31B96E")};
                    /*實現*/
                    navigationViewColorStateList = new ColorStateList(states, colors);
                    navigation.setItemTextColor(navigationViewColorStateList);
                    navigation.setItemIconTintList(navigationViewColorStateList);

                    //從Main2Activity 到MainActivity
                    intent.setClass(Main2Activity.this , MainActivity.class);
                    //開啟Activity
                    startActivity(intent);
                    Main2Activity.this.finish(); //關閉本頁
                    return true;
                case R.id.navigation_dashboard:
                    // 設定狀態
                    states = new int[][]{
                            new int[]{-android.R.attr.state_checked},  // 沒被點擊
                            new int[]{android.R.attr.state_checked}};   // 被點擊
                    // 設定顏色
                    colors = new int[]{
                            Color.parseColor("#6C6C6C"),
                            Color.parseColor("#D74B4B")};
                    /*實現*/
                    navigationViewColorStateList = new ColorStateList(states, colors);
                    navigation.setItemTextColor(navigationViewColorStateList);
                    navigation.setItemIconTintList(navigationViewColorStateList);

                    //從Main2Activity 到Main3Activity
                    intent.setClass(Main2Activity.this , Main3Activity.class);
                    //開啟Activity
                    startActivity(intent);
                    Main2Activity.this.finish(); //關閉本頁
                    return true;
            }
            return false;
        }

    };
    /****************************************問號*************************************/
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.help, menu);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.help)
        {
            FollowHelpDialogFragment dialog = new FollowHelpDialogFragment();
            dialog.show(getFragmentManager(), "TAG");
        }
        return true;
    }
}
