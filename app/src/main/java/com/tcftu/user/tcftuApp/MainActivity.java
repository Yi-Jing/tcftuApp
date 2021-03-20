package com.tcftu.user.tcftuApp;

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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    Cursor cursor;
    CourseRepo courseRepo;
    Cursor cursor1;
    FollowedRepo followedRepo;
    private AlertDialog newVersionDialog; //新版本對話框
    private AlertDialog newDialog; //偵測網路對話框

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences oneStart = getSharedPreferences("oneStart", 0);
        boolean ifOne = oneStart.getBoolean("ifOne", false);

        final SharedPreferences updateData = getSharedPreferences("updateData", 0);
        boolean ifUpdate = updateData.getBoolean("ifUpdate", false);

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

        /***************若未新增資料過(代表APP第一次下載)，就新增以下資料並執行使用者導覽頁面***************/
        if (ifOne == false) {
            insertDummy(); //呼叫新增函數來新增
            oneStart.edit().putBoolean("ifOne", true).apply();

            Intent intent1 = new Intent();
            intent1.setClass(MainActivity.this, userNavigation.class);
            //開啟Activity
            startActivity(intent1);

            final ProgressDialog dialog = ProgressDialog.show(MainActivity.this,
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
        }
        else if (cursor == null && ifUpdate == true) {
            insertDummy(); //呼叫新增函數來新增
            updateData.edit().putBoolean("ifUpdate", false).apply();
        }
        /***************************************Menu按鈕的取得和觸發*****************************************/
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation); //取得按鈕的實體
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        /***************************************卡片表格資訊***************************************/
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list_view);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new MemberAdapter(this));
    }
    Course course= new Course();
    /******** 新增資料 ********/
    private void insertDummy()
    {
        courseRepo=new CourseRepo(this);
        course.courseID="107355";
        course.name="EXCEL統計分析商務應用班 ";
        course.teacher="王意順 ";
        course.applyDate="2019/10/23 12:00  ~   2019/10/30 18:00";
        course.startApplyDate="2019/10/23 12:00";
        course.startEnd="2019/11/05 至  2017/11/11";
        course.classDay="星期二18:30~21:30;星期四18:30~21:30 ";
        course.people="25";
        course.totalHr="54";
        course.kAdd="";
        course.sAdd="701-68臺南市東區中華東路三段18號3樓 ";
        course.labor="大台南勞工團體會務工作人員職業工會 ";
        course.contact="陳柏勳 ";
        course.phone="06-6322176";
        course.aYear=2019;
        course.aMonth=10;
        course.aDay=23;
        course.eYear=2019;
        course.eMonth=10;
        course.eDay=30;
        courseRepo.insert(course);
        courseRepo=new CourseRepo(this);
        course.courseID="107590";
        course.name="蝶谷巴特家飾班 ";
        course.teacher="陳月鳳 ";
        course.applyDate="2019/07/27 12:00  ~   2019/07/30 18:00";
        course.startApplyDate="2019/07/27 12:00";
        course.startEnd="2019/08/01 至 2019/09/02";
        course.classDay="星期一18:30~21:30;星期三18:30~21:30 ";
        course.people="20";
        course.totalHr="54";
        course.kAdd="730-64臺南市新營區文昌街26之7號";
        course.sAdd="730-64臺南市新營區文昌街26之7號 ";
        course.labor="大台南總工會 ";
        course.contact="沈暄晨 ";
        course.phone="06-6322176";
        course.aYear=2019;
        course.aMonth=7;
        course.aDay=27;
        course.eYear=2019;
        course.eMonth=7;
        course.eDay=30;
        courseRepo.insert(course);
        courseRepo=new CourseRepo(this);
        course.courseID="107359";
        course.name="商業行銷廣告平面設計班 ";
        course.teacher="何若齊 ";
        course.applyDate="2017/07/24 12:00  ~   2017/08/13 18:00";
        course.startApplyDate="2017/07/24 12:00";
        course.startEnd="2017/08/16 至2017/10/18";
        course.classDay="星期三18:30~21:30;星期五18:30~21:30 ";
        course.people="25";
        course.totalHr="54";
        course.kAdd="701-68臺南市東區中華東路三段18號3樓";
        course.sAdd="701-68臺南市東區中華東路三段18號3樓 ";
        course.labor="大台南勞工團體會務工作人員職業工會 ";
        course.contact="陳柏勳 ";
        course.phone="06-6322176";
        course.aYear=2017;
        course.aMonth=7;
        course.aDay=28;
        course.eYear=2017;
        course.eMonth=8;
        course.eDay=13;
        courseRepo.insert(course);
        courseRepo=new CourseRepo(this);
        course.courseID="107621";
        course.name="數位商務網路行銷實戰班 ";
        course.teacher="黃嘉偉 ";
        course.applyDate="2017/07/26 13:00  ~   2017/08/16 18:00";
        course.startApplyDate="2017/07/26 13:00";
        course.startEnd="2017/08/10至2017/09/28";
        course.classDay="星期二18:30-21:30;星期四18:30-21:30 ";
        course.people="25";
        course.totalHr="45";
        course.kAdd="701-68臺南市東區中華東路三段18號3樓";
        course.sAdd="701-68臺南市東區中華東路三段18號3樓 ";
        course.labor="大台南花藝設計製作職業工會 ";
        course.contact="王安佳 ";
        course.phone="06-6371890";
        course.aYear=2017;
        course.aMonth=7;
        course.aDay=26;
        course.eYear=2017;
        course.eMonth=8;
        course.eDay=7;
        courseRepo.insert(course);
        courseRepo=new CourseRepo(this);
        course.courseID="106174";
        course.name="勞動法令解析與應用實務班 ";
        course.teacher="林管洛、蔡建忠、賴威志、翁偉哲、翁政樺 ";
        course.applyDate="2017/07/27 13:00 ~2017/08/19 18:00";
        course.startApplyDate="2017/7/27 13:00";
        course.startEnd="2017/08/12至  2017/12/09";
        course.classDay="星期六08:00~11:00;星期日08:00~11:00 ";
        course.people="20";
        course.totalHr="54";
        course.kAdd="741-71臺南市善化區成功路2號";
        course.sAdd=" ";
        course.labor="社團法人臺南市勞工領袖協進會 ";
        course.contact="王永木 ";
        course.phone="06-6354835";
        course.aYear=2017;
        course.aMonth=7;
        course.aDay=27;
        course.eYear=2017;
        course.eMonth=8;
        course.eDay=9;
        courseRepo.insert(course);
        courseRepo=new CourseRepo(this);
        course.courseID="107379";
        course.name="西式糕點烘焙高階班 ";
        course.teacher="謝仁宗 ";
        course.applyDate="2017/07/27 13:00  ~   2017/08/11 18:00";
        course.startApplyDate="2017/07/27 13:00";
        course.startEnd="2017/08/13 至2017/11/12";
        course.classDay="星期日18:00~22:00 ";
        course.people="20";
        course.totalHr="52";
        course.kAdd="741-71臺南市善化區成功路2號";
        course.sAdd="741-71臺南市善化區成功路2號 ";
        course.labor="大台南勞動力服務人員職業工會 ";
        course.contact="沈佳蓉 ";
        course.phone="06-6371890";
        course.aYear=2017;
        course.aMonth=7;
        course.aDay=27;
        course.eYear=2017;
        course.eMonth=8;
        course.eDay=10;
        courseRepo.insert(course);
        courseRepo=new CourseRepo(this);
        course.courseID="107361";
        course.name="餅乾甜點烘焙進階班 ";
        course.teacher="謝仁宗、蔣寶儀 ";
        course.applyDate="2017/07/27 13:00 ~2017/08/21 18:00";
        course.startApplyDate="2017/7/27 13:00";
        course.startEnd="2017/08/14至2017/10/02";
        course.classDay="星期一18:00~22:00;星期四18:00~22:00 ";
        course.people="20";
        course.totalHr="60";
        course.kAdd="741-71臺南市善化區成功路2號";
        course.sAdd="741-71臺南市善化區成功路2號 ";
        course.labor="大台南勞動力服務人員職業工會 ";
        course.contact="沈佳蓉 ";
        course.phone="06-6371890";
        course.aYear=2017;
        course.aMonth=7;
        course.aDay=27;
        course.eYear=2017;
        course.eMonth=8;
        course.eDay=11;
        courseRepo.insert(course);
        courseRepo=new CourseRepo(this);
        course.courseID="107600";
        course.name="天然草本手工皂班 ";
        course.teacher="胡秀靜 ";
        course.applyDate="2017/07/27 13:00 ~2017/08/16 18:00";
        course.startApplyDate="2017/7/27 13:00";
        course.startEnd="2017/08/13 至2017/10/15";
        course.classDay="星期日09:00~12:00;星期日13:00~16:00 ";
        course.people="30";
        course.totalHr="54";
        course.kAdd="";
        course.sAdd="730-64臺南市新營區文昌街26之7號 ";
        course.labor="大台南總工會 ";
        course.contact="沈暄晨 ";
        course.phone="06-6322176";
        course.aYear=2017;
        course.aMonth=7;
        course.aDay=27;
        course.eYear=2017;
        course.eMonth=8;
        course.eDay=10;
        courseRepo.insert(course);
        courseRepo=new CourseRepo(this);
        course.courseID="107598";
        course.name="服裝設計打版縫製與修改班 ";
        course.teacher="邱明歆 ";
        course.applyDate="2017/07/28 13:00  ~   2017/08/18 18:00";
        course.startApplyDate="2017/07/28 13:00";
        course.startEnd="2017/08/15 至  2017/12/12";
        course.classDay="星期二18:30~21:30 ";
        course.people="25";
        course.totalHr="54";
        course.kAdd="";
        course.sAdd="730-64臺南市新營區文昌街26之7號 ";
        course.labor="大台南總工會 ";
        course.contact="沈暄晨 ";
        course.phone="06-6322176";
        course.aYear=2017;
        course.aMonth=7;
        course.aDay=28;
        course.eYear=2017;
        course.eMonth=8;
        course.eDay=12;
        courseRepo.insert(course);
        courseRepo=new CourseRepo(this);
        course.courseID="107584";
        course.name="流行編織飾品設計班 ";
        course.teacher="謝麗錚 ";
        course.applyDate="2017/07/28 13:00  ~   2017/08/18 18:00";
        course.startApplyDate="2017/07/28 13:00";
        course.startEnd="2017/08/16至2017/10/23";
        course.classDay="星期一18:30-21:30;星期三18:30-21:30 ";
        course.people="30";
        course.totalHr="54";
        course.kAdd="";
        course.sAdd="730-64臺南市新營區文昌街26之7號 ";
        course.labor="大台南總工會 ";
        course.contact="沈暄晨 ";
        course.phone="06-6322176";
        course.aYear=2017;
        course.aMonth=7;
        course.aDay=28;
        course.eYear=2017;
        course.eMonth=8;
        course.eDay=13;
        courseRepo.insert(course);
        courseRepo=new CourseRepo(this);
        course.courseID="107315";
        course.name="勞動法令解析與應用實務A班 ";
        course.teacher="林管洛、蔡建忠、賴威志、翁偉哲、翁政樺 ";
        course.applyDate="2017/07/28 13:00  ~   2017/08/29 18:00";
        course.startApplyDate="2017/07/28 13:00";
        course.startEnd="2017/08/16 至2017/12/13";
        course.classDay="星期一18:30~21:30;星期三18:30~21:30 ";
        course.people="20";
        course.totalHr="54";
        course.kAdd="741-71臺南市善化區成功路2號";
        course.sAdd=" ";
        course.labor="大台南勞工團體會務工作人員職業工會 ";
        course.contact="陳柏勳 ";
        course.phone="06-6322176";
        course.aYear=2017;
        course.aMonth=7;
        course.aDay=28;
        course.eYear=2017;
        course.eMonth=8;
        course.eDay=13;
        courseRepo.insert(course);
        courseRepo=new CourseRepo(this);
        course.courseID="107285";
        course.name="內部講師簡報班 ";
        course.teacher="韓瑞信 ";
        course.applyDate="2017/07/29 13:00  ~   2017/08/17 18:00";
        course.startApplyDate="2017/07/29 13:00";
        course.startEnd="2017/08/20 至2017/10/01";
        course.classDay="星期日09:00~12:00;星期日13:00~16:00 ";
        course.people="20";
        course.totalHr="42";
        course.kAdd="";
        course.sAdd="741-71臺南市善化區成功路2號 ";
        course.labor="大台南勞工團體會務工作人員職業工會 ";
        course.contact="陳柏勳 ";
        course.phone="06-6322176";
        course.aYear=2017;
        course.aMonth=7;
        course.aDay=29;
        course.eYear=2017;
        course.eMonth=8;
        course.eDay=17;
        courseRepo.insert(course);
        courseRepo=new CourseRepo(this);
        course.courseID="107565";
        course.name="花藝會場佈置與規劃班 ";
        course.teacher="謝麗錚 ";
        course.applyDate="2017/07/29 13:00  ~   2017/08/19 18:00";
        course.startApplyDate="2017/07/29 13:00";
        course.startEnd="2017/08/22 至  2017/11/28";
        course.classDay="星期二18:30~21:30 ";
        course.people="24";
        course.totalHr="42";
        course.kAdd="";
        course.sAdd="741-71臺南市善化區成功路2號 ";
        course.labor="大台南園藝花卉業職業工會 ";
        course.contact="謝芬蘭 ";
        course.phone="06-6354835";
        course.aYear=2017;
        course.aMonth=7;
        course.aDay=29;
        course.eYear=2017;
        course.eMonth=8;
        course.eDay=19;
        courseRepo.insert(course);
        courseRepo=new CourseRepo(this);
        course.courseID="107324";
        course.name="勞動法令解析與應用實務B班 ";
        course.teacher="林管洛、蔡建忠、賴威志、翁偉哲、翁政樺 ";
        course.applyDate="2017/07/29 13:00  ~   2017/08/28 18:00";
        course.startApplyDate="2017/07/29 13:00";
        course.startEnd="2017/08/22 至  2017/10/24";
        course.classDay="星期二09:00~12:00;星期二13:00~16:00 ";
        course.people="20";
        course.totalHr="54";
        course.kAdd="741-71臺南市善化區成功路2號";
        course.sAdd=" ";
        course.labor="大台南勞工團體會務工作人員職業工會 ";
        course.contact="陳柏勳 ";
        course.phone="06-6322176";
        course.aYear=2017;
        course.aMonth=7;
        course.aDay=29;
        course.eYear=2017;
        course.eMonth=8;
        course.eDay=19;
        courseRepo.insert(course);
        courseRepo=new CourseRepo(this);
        course.courseID="107350";
        course.name="文書與試算表應用實務班 ";
        course.teacher="李文雄 ";
        course.applyDate="2017/07/29 13:00  ~   2017/08/29 18:00";
        course.startApplyDate="2017/07/29 13:00";
        course.startEnd="2017/08/22 至2017/10/24";
        course.classDay="星期二18:30~21:30;星期四18:30~21:30 ";
        course.people="25";
        course.totalHr="54";
        course.kAdd="";
        course.sAdd="741-54臺南市善化區中山路152號 ";
        course.labor="大台南勞工團體會務工作人員職業工會 ";
        course.contact="陳柏勳 ";
        course.phone="06-6322176";
        course.aYear=2017;
        course.aMonth=7;
        course.aDay=29;
        course.eYear=2017;
        course.eMonth=8;
        course.eDay=19;
        courseRepo.insert(course);
        courseRepo=new CourseRepo(this);
        course.courseID="104499";
        course.name="創意造型氣球佈置班 ";
        course.teacher="陳登在 ";
        course.applyDate="2017/07/30 13:00  ~   2017/09/01 18:00";
        course.startApplyDate="2017/07/30 13:00";
        course.startEnd="2017/08/26至2017/10/21";
        course.classDay="星期六09:00~12:00;星期六13:00~16:00 ";
        course.people="25";
        course.totalHr="42";
        course.kAdd="";
        course.sAdd="741-71臺南市善化區成功路2號 ";
        course.labor="社團法人中華民國勞工志願服務協會 ";
        course.contact="陳美婷 ";
        course.phone="06-6371890";
        course.aYear=2017;
        course.aMonth=7;
        course.aDay=30;
        course.eYear=2017;
        course.eMonth=8;
        course.eDay=23;
        courseRepo.insert(course);
        courseRepo=new CourseRepo(this);
        course.courseID="104451";
        course.name="花藝會場佈置設計與規劃班 ";
        course.teacher="謝麗錚 ";
        course.applyDate="2017/07/30 13:00  ~   2017/09/06 18:00";
        course.startApplyDate="2017/07/30 13:00";
        course.startEnd="2017/08/25 至  2017/12/08";
        course.classDay="星期五18:30~21:30 ";
        course.people="30";
        course.totalHr="48";
        course.kAdd="";
        course.sAdd="702-42臺南市南區南門路261號 ";
        course.labor="社團法人台南市勞工志願服務協會 ";
        course.contact="王永木 ";
        course.phone="06-6322176";
        course.aYear=2017;
        course.aMonth=7;
        course.aDay=30;
        course.eYear=2017;
        course.eMonth=8;
        course.eDay=22;
        courseRepo.insert(course);
        courseRepo=new CourseRepo(this);
        course.courseID="107382";
        course.name="手作麵包烘焙實務班 ";
        course.teacher="謝仁宗、蔣寶儀 ";
        course.applyDate="2017/07/31 13:00  ~   2017/08/12 18:00";
        course.startApplyDate="2017/07/31 13:00";
        course.startEnd="2017/08/15 至2017/10/03";
        course.classDay="星期二18:00~22:00;星期五18:00~22:00 ";
        course.people="20";
        course.totalHr="60";
        course.kAdd="741-71臺南市善化區成功路2號";
        course.sAdd="741-71臺南市善化區成功路2號 ";
        course.labor="大台南勞動力服務人員職業工會 ";
        course.contact="沈佳蓉 ";
        course.phone="06-6371890";
        course.aYear=2017;
        course.aMonth=7;
        course.aDay=31;
        course.eYear=2017;
        course.eMonth=8;
        course.eDay=12;
        courseRepo.insert(course);
        courseRepo=new CourseRepo(this);
        course.courseID="107299";
        course.name="圖解思考-創新工作思維實務班 ";
        course.teacher="李珮瑜、孫中平、蔡興正 ";
        course.applyDate="2017/08/02 13:00  ~   2017/08/30 18:00";
        course.startApplyDate="2017/08/02 13:00";
        course.startEnd="2017/09/02 至  2017/11/11";
        course.classDay="星期六09:00~12:00;星期六13:00~16:00 ";
        course.people="20";
        course.totalHr="54";
        course.kAdd="741-71臺南市善化區成功路2號";
        course.sAdd=" ";
        course.labor="大台南勞工團體會務工作人員職業工會 ";
        course.contact="陳柏勳 ";
        course.phone="06-6322176";
        course.aYear=2017;
        course.aMonth=8;
        course.aDay=2;
        course.eYear=2017;
        course.eMonth=8;
        course.eDay=30;
        courseRepo.insert(course);
        courseRepo=new CourseRepo(this);
        course.courseID="107579";
        course.name="韓系與日系職場整體造型時尚彩妝班 ";
        course.teacher="洪瑞霞 ";
        course.applyDate="2017/08/02 13:00  ~   2017/08/30 18:00";
        course.startApplyDate="2017/08/02 13:00";
        course.startEnd="2017/09/02至  2017/10/28";
        course.classDay="星期六09:00~12:00;星期六13:00~16:00 ";
        course.people="30";
        course.totalHr="42";
        course.kAdd="";
        course.sAdd="702-42臺南市南區南門路261號 ";
        course.labor="大台南總工會 ";
        course.contact="沈暄晨 ";
        course.phone="06-6322176";
        course.aYear=2017;
        course.aMonth=8;
        course.aDay=2;
        course.eYear=2017;
        course.eMonth=8;
        course.eDay=30;
        courseRepo.insert(course);
        courseRepo=new CourseRepo(this);
        course.courseID="104419";
        course.name="花藝會場佈置實務班 ";
        course.teacher="謝麗錚 ";
        course.applyDate="2017/08/09 12:00  ~   2017/09/06 18:00";
        course.startApplyDate="2017/08/09 12:00";
        course.startEnd="2017/09/09 至  2018/01/06";
        course.classDay="星期六08:00~11:00 ";
        course.people="30";
        course.totalHr="48";
        course.kAdd="";
        course.sAdd="741-71臺南市善化區成功路2號 ";
        course.labor="社團法人台南市勞工志願服務協會 ";
        course.contact="王永木 ";
        course.phone="06-6322176";
        course.aYear=2017;
        course.aMonth=8;
        course.aDay=9;
        course.eYear=2017;
        course.eMonth=9;
        course.eDay=6;
        courseRepo.insert(course);
        courseRepo=new CourseRepo(this);
        course.courseID="104506";
        course.name="電腦文書處理應用班 ";
        course.teacher="賴騏濬 ";
        course.applyDate="2017/08/12 12:00  ~   2017/09/09 18:00";
        course.startApplyDate="2017/08/12 12:00";
        course.startEnd="2017/09/12至2017/11/09";
        course.classDay="星期二18:30~21:30;星期四18:30~21:30 ";
        course.people="24";
        course.totalHr="48";
        course.kAdd="730-43臺南市新營區復興路251之1號";
        course.sAdd="730-43臺南市新營區復興路251之2號 ";
        course.labor="社團法人中華民國勞工志願服務協會 ";
        course.contact="陳美婷 ";
        course.phone="06-6371890";
        course.aYear=2017;
        course.aMonth=8;
        course.aDay=12;
        course.eYear=2017;
        course.eMonth=9;
        course.eDay=9;
        courseRepo.insert(course);
        courseRepo=new CourseRepo(this);
        course.courseID="104485";
        course.name="助人者的訪視會談技巧進階班 ";
        course.teacher="鍾美智 ";
        course.applyDate="2017/08/15 12:00  ~   2017/09/12 18:00";
        course.startApplyDate="2017/08/15 12:00";
        course.startEnd="2017/09/15至2018/01/12";
        course.classDay="星期五09:00~12:00;星期五13:00~16:00 ";
        course.people="20";
        course.totalHr="54";
        course.kAdd="741-71臺南市善化區成功路2號";
        course.sAdd="741-71臺南市善化區成功路2號 ";
        course.labor="社團法人中華民國勞工志願服務協會 ";
        course.contact="陳美婷 ";
        course.phone="06-6371890";
        course.aYear=2017;
        course.aMonth=8;
        course.aDay=15;
        course.eYear=2017;
        course.eMonth=9;
        course.eDay=12;
        courseRepo.insert(course);
        courseRepo=new CourseRepo(this);
        course.courseID="107620";
        course.name="創意押花禮品設計班 ";
        course.teacher="闕玟玉 ";
        course.applyDate="2017/08/15 12:00  ~   2017/09/12 18:00";
        course.startApplyDate="2017/08/15 12:00";
        course.startEnd="2017/09/15至  2017/11/24";
        course.classDay="星期四18:30~21:30;星期五18:30~21:30 ";
        course.people="25";
        course.totalHr="48";
        course.kAdd="";
        course.sAdd="730-64臺南市新營區文昌街26之7號 ";
        course.labor="大台南花藝設計製作職業工會 ";
        course.contact="王安佳 ";
        course.phone="06-6371890";
        course.aYear=2017;
        course.aMonth=8;
        course.aDay=15;
        course.eYear=2017;
        course.eMonth=9;
        course.eDay=12;
        courseRepo.insert(course);
        courseRepo=new CourseRepo(this);
        course.courseID="102815";
        course.name="池坊自由花班 ";
        course.teacher="謝麗錚 ";
        course.applyDate="2017/08/16 12:00  ~   2017/09/13 18:00";
        course.startApplyDate="2017/08/16 12:00";
        course.startEnd="2017/09/16 至  2017/12/23";
        course.classDay="星期六14:00-17:00 ";
        course.people="30";
        course.totalHr="42";
        course.kAdd="";
        course.sAdd="702-42臺南市南區南門路261號 ";
        course.labor="臺南市園藝花卉產業工會 ";
        course.contact="林雅慧 ";
        course.phone="06-6322176";
        course.aYear=2017;
        course.aMonth=8;
        course.aDay=16;
        course.eYear=2017;
        course.eMonth=9;
        course.eDay=13;
        courseRepo.insert(course);
        courseRepo=new CourseRepo(this);
        course.courseID="107349";
        course.name="辦公室軟體應用進階班 ";
        course.teacher="賴騏濬 ";
        course.applyDate="2017/08/23 13:00 ~2017/09/20 18:00";
        course.startApplyDate="2017/8/23 13:00";
        course.startEnd="2017/08/26 至  2017/10/28";
        course.classDay="星期六09:00~12:00;星期六13:00~16:00 ";
        course.people="24";
        course.totalHr="48";
        course.kAdd="730-43臺南市新營區復興路251之1號";
        course.sAdd="730-43臺南市新營區復興路251之1號 ";
        course.labor="大台南勞工團體會務工作人員職業工會 ";
        course.contact="陳柏勳 ";
        course.phone="06-6322176";
        course.aYear=2017;
        course.aMonth=7;
        course.aDay=30;
        course.eYear=2017;
        course.eMonth=8;
        course.eDay=23;
        courseRepo.insert(course);
        courseRepo=new CourseRepo(this);
        course.courseID="107568";
        course.name="花藝空間設計班 ";
        course.teacher="謝麗錚 ";
        course.applyDate="2017/08/28 12:00  ~   2017/09/25 18:00";
        course.startApplyDate="2017/08/28 12:00";
        course.startEnd="2017/09/28 至  2018/01/25";
        course.classDay="星期四18:30~21:30 ";
        course.people="30";
        course.totalHr="54";
        course.kAdd="";
        course.sAdd="730-64臺南市新營區文昌街26之7號 ";
        course.labor="大台南園藝花卉業職業工會 ";
        course.contact="謝芬蘭 ";
        course.phone="06-6354835";
        course.aYear=2017;
        course.aMonth=8;
        course.aDay=28;
        course.eYear=2017;
        course.eMonth=9;
        course.eDay=25;
        courseRepo.insert(course);
        courseRepo=new CourseRepo(this);
        course.courseID="104375";
        course.name="Office軟體職場實務應用班 ";
        course.teacher="王意順 ";
        course.applyDate="2017/09/05 12:00  ~   2017/10/02 18:00";
        course.startApplyDate="2017/09/05 12:00";
        course.startEnd="2017/10/05 至  2017/12/07";
        course.classDay="星期二18:30~21:30;星期四18:30~21:30 ";
        course.people="25";
        course.totalHr="54";
        course.kAdd="";
        course.sAdd="701-68臺南市東區中華東路三段18號3樓 ";
        course.labor="社團法人台南市勞工志願服務協會 ";
        course.contact="王永木 ";
        course.phone="06-6322176";
        course.aYear=2017;
        course.aMonth=9;
        course.aDay=5;
        course.eYear=2017;
        course.eMonth=10;
        course.eDay=2;
        courseRepo.insert(course);
        courseRepo=new CourseRepo(this);
        course.courseID="107570";
        course.name="園藝景觀竹藝設計高級班 ";
        course.teacher="賴進益 ";
        course.applyDate="2017/09/15 12:00  ~   2017/10/12 18:00";
        course.startApplyDate="2017/09/15 12:00";
        course.startEnd="2017/10/15至  2017/12/17";
        course.classDay="星期日09:00~12:00;星期日13:00~16:00 ";
        course.people="20";
        course.totalHr="54";
        course.kAdd="741-71臺南市善化區成功路2號";
        course.sAdd="741-71臺南市善化區成功路2號 ";
        course.labor="大台南園藝花卉業職業工會 ";
        course.contact="謝芬蘭 ";
        course.phone="06-6354835";
        course.aYear=2017;
        course.aMonth=9;
        course.aDay=15;
        course.eYear=2017;
        course.eMonth=10;
        course.eDay=12;
        courseRepo.insert(course);
        courseRepo=new CourseRepo(this);
        course.courseID="107287";
        course.name="內部講師實務班 ";
        course.teacher="韓瑞信 ";
        course.applyDate="2017/09/22 12:00  ~   2017/10/19 18:00";
        course.startApplyDate="2017/09/22 12:00";
        course.startEnd="2017/10/22 至  2017/12/03";
        course.classDay="星期日09:00~12:00;星期日13:00~16:00 ";
        course.people="20";
        course.totalHr="42";
        course.kAdd="";
        course.sAdd="741-71臺南市善化區成功路2號 ";
        course.labor="大台南勞工團體會務工作人員職業工會 ";
        course.contact="陳柏勳 ";
        course.phone="06-6322176";
        course.aYear=2017;
        course.aMonth=9;
        course.aDay=22;
        course.eYear=2017;
        course.eMonth=10;
        course.eDay=19;
        courseRepo.insert(course);
        courseRepo=new CourseRepo(this);
        course.courseID="104481";
        course.name="百變造型氣球展場佈置班 ";
        course.teacher="陳登在 ";
        course.applyDate="2017/10/11 12:00  ~   2017/11/08 18:00";
        course.startApplyDate="2017/10/11 12:00";
        course.startEnd="2017/11/11至2017/12/30";
        course.classDay="星期六09:00~12:00;星期六13:00~16:00 ";
        course.people="25";
        course.totalHr="48";
        course.kAdd="";
        course.sAdd="741-71臺南市善化區成功路2號 ";
        course.labor="社團法人中華民國勞工志願服務協會 ";
        course.contact="陳美婷 ";
        course.phone="06-6371890";
        course.aYear=2017;
        course.aMonth=10;
        course.aDay=11;
        course.eYear=2017;
        course.eMonth=11;
        course.eDay=8;
        courseRepo.insert(course);
        courseRepo=new CourseRepo(this);
        course.courseID="107296";
        course.name="訓練品質系統規劃與建置班 ";
        course.teacher="王思傑 ";
        course.applyDate="2017/10/15 12:00  ~   2017/11/12 18:00";
        course.startApplyDate="2017/10/15 12:00";
        course.startEnd="2017/11/15 至  2018/01/10";
        course.classDay="星期三09:00~12:00;星期三13:00~16:00 ";
        course.people="20";
        course.totalHr="54";
        course.kAdd="741-71臺南市善化區成功路2號";
        course.sAdd=" ";
        course.labor="大台南勞工團體會務工作人員職業工會 ";
        course.contact="陳柏勳 ";
        course.phone="06-6322176";
        course.aYear=2017;
        course.aMonth=10;
        course.aDay=15;
        course.eYear=2017;
        course.eMonth=11;
        course.eDay=12;
        courseRepo.insert(course);
        courseRepo=new CourseRepo(this);
        course.courseID="107622";
        course.name="手工皂蜜蠟花創作班 ";
        course.teacher="胡秀靜 ";
        course.applyDate="2017/10/18 12:00  ~   2017/11/15 18:00";
        course.startApplyDate="2017/10/18 12:00";
        course.startEnd="2017/11/18至2018/01/13";
        course.classDay="星期六09:00~12:00;星期六13:00~16:00 ";
        course.people="30";
        course.totalHr="54";
        course.kAdd="";
        course.sAdd="741-71臺南市善化區成功路2號 ";
        course.labor="大台南花藝設計製作職業工會 ";
        course.contact="王安佳 ";
        course.phone="06-6371890";
        course.aYear=2017;
        course.aMonth=10;
        course.aDay=18;
        course.eYear=2017;
        course.eMonth=11;
        course.eDay=15;
        courseRepo.insert(course);
        courseRepo=new CourseRepo(this);
        course.courseID="107595";
        course.name="內部講師班 ";
        course.teacher="韓瑞信 ";
        course.applyDate="2017/11/24 12:00 ~2017/12/21 18:00";
        course.startApplyDate="2017/11/24 12:00";
        course.startEnd="2017/12/24至2018/02/11";
        course.classDay="星期日09:00~12:00;星期日13:00~16:00 ";
        course.people="20";
        course.totalHr="42";
        course.kAdd="741-71臺南市善化區成功路2號";
        course.sAdd="741-71臺南市善化區成功路2號 ";
        course.labor="大台南總工會 ";
        course.contact="沈暄晨 ";
        course.phone="06-6322176";
        course.aYear=2017;
        course.aMonth=11;
        course.aDay=24;
        course.eYear=2017;
        course.eMonth=12;
        course.eDay=21;
        courseRepo.insert(course);
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
    private class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.ViewHolder> {
        private LayoutInflater layoutInflater;
        private List<home> memberList;

        public MemberAdapter(Context context) {
            layoutInflater = LayoutInflater.from(context);

            memberList = new ArrayList<>();
            memberList.add(new home(R.mipmap.home1, "工會網站"));
            memberList.add(new home(R.mipmap.home2, "招生訊息"));
            memberList.add(new home(R.mipmap.home3, "產投網站"));
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private ImageView ivImage;
            private TextView tvName;

            public ViewHolder(final View itemView) {
                super(itemView);
                ivImage = (ImageView) itemView.findViewById(R.id.coverImageView);
                tvName = (TextView) itemView.findViewById(R.id.titleTextView);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        home member = memberList.get(getAdapterPosition());
                        switch (member.getName()){
                            /*點選每個卡片的動畫效果 + 跳到網頁*/
                            case "工會網站":
                                ViewAnimationUtils.createCircularReveal(ivImage,1, ivImage.getHeight(), ivImage.getWidth() / 4, ivImage.getWidth() * 2).setDuration(500).start();
                                String url1 = "http://www.tcftu.com/";
                                Intent i1 = new Intent(Intent.ACTION_VIEW);
                                i1.setData(Uri.parse(url1));
                                startActivity(i1);
                                break;
                            case "招生訊息":
                                ViewAnimationUtils.createCircularReveal(ivImage,1, ivImage.getHeight(), ivImage.getWidth() / 4, ivImage.getWidth() * 2).setDuration(500).start();
                                String url2 = "http://www.tcftu.com/news.asp?readclass=2";
                                Intent i2 = new Intent(Intent.ACTION_VIEW);
                                i2.setData(Uri.parse(url2));
                                startActivity(i2);
                                break;
                            case "產投網站":
                                ViewAnimationUtils.createCircularReveal(ivImage,1, ivImage.getHeight(), ivImage.getWidth() / 4, ivImage.getWidth() * 2).setDuration(500).start();
                                String url3 = "https://tims.etraining.gov.tw/timsonline/index.aspx";
                                Intent i3 = new Intent(Intent.ACTION_VIEW);
                                i3.setData(Uri.parse(url3));
                                startActivity(i3);
                                break;
                        }
                    }
                });

            }

            public ImageView getIvImage() {
                return ivImage;
            }

            public TextView getTvName() {
                return tvName;
            }
        }

        @Override
        public int getItemCount() {
            return memberList.size();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View itemView = layoutInflater.inflate(
                    R.layout.cardview, viewGroup, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {
            home member = memberList.get(position);
            viewHolder.getIvImage().setImageResource(member.getImage());
            viewHolder.getTvName().setText(member.getName());
        }
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
                case R.id.navigation_follow:
                    // 設定狀態
                    states = new int[][]{
                            new int[]{-android.R.attr.state_checked},  // 沒被點擊
                            new int[]{android.R.attr.state_checked}};   // 被點擊
                    // 設定顏色
                    colors = new int[]{
                            Color.parseColor("#6C6C6C"),
                            Color.parseColor("#F2AF29")};
                    //實現
                    navigationViewColorStateList = new ColorStateList(states, colors);
                    navigation.setItemTextColor(navigationViewColorStateList);
                    navigation.setItemIconTintList(navigationViewColorStateList);

                    //從MainActivity 到Main2Activity
                    intent.setClass(MainActivity.this , Main2Activity.class);
                    //開啟Activity
                    startActivity(intent);
                    MainActivity.this.finish(); //關閉本頁
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
                    //實現
                    navigationViewColorStateList = new ColorStateList(states, colors);
                    navigation.setItemTextColor(navigationViewColorStateList);
                    navigation.setItemIconTintList(navigationViewColorStateList);

                    //從MainActivity 到Main3Activity
                    intent.setClass(MainActivity.this , Main3Activity.class);
                    //開啟Activity
                    startActivity(intent);
                    MainActivity.this.finish(); //關閉本頁
                    return true;
            }
            return false;
        }
    };
    /****************************************教學*************************************/
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.teach, menu);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.teach)
        {
            Intent intent1 = new Intent();
            intent1.setClass(MainActivity.this , userNavigation.class);
            //開啟Activity
            startActivity(intent1);
        }
        return true;
    }
}
