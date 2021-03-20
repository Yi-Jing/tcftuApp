package com.tcftu.user.tcftuApp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.List;


public class userNavigation extends FragmentActivity {
    private ViewPager viewPager; //宣告 ViewPager 元件
    MyViewPagerAdapter mAdapter;
    RadioGroup radioGroup;
    RadioButton radioButton0,
                radioButton1,
                radioButton2,
                radioButton3,
                radioButton4,
                radioButton5;

    private Button buttonStart;
    private Button buttonVideo;

    private static final int[] pictures = {
            R.mipmap.user0,
            R.mipmap.user1,
            R.mipmap.user2,
            R.mipmap.user3,
            R.mipmap.user4,
            R.mipmap.user5};

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_navigation);

        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioButton0 = (RadioButton) findViewById(R.id.radioButton0);
        radioButton1 = (RadioButton) findViewById(R.id.radioButton1);
        radioButton2 = (RadioButton) findViewById(R.id.radioButton2);
        radioButton3 = (RadioButton) findViewById(R.id.radioButton3);
        radioButton4 = (RadioButton) findViewById(R.id.radioButton3);
        radioButton5 = (RadioButton) findViewById(R.id.radioButton3);


        viewPager = (ViewPager) findViewById(R.id.viewpager);
/*
        final LayoutInflater mInflater = getLayoutInflater().from(this);

        //宣告四個 View 以儲存四種不同 layout。
        View viewPager0 = mInflater.inflate(R.layout.activity_main, null);
        View viewPager1 = mInflater.inflate(R.layout.cardview, null);
        View viewPager2 = mInflater.inflate(R.layout.activity_main2, null);
        View viewPager3 = mInflater.inflate(R.layout.activity_main3, null);

        viewPager_List = new ArrayList<View>();
        viewPager_List.add(viewPager0);
        viewPager_List.add(viewPager1);
        viewPager_List.add(viewPager2);
        viewPager_List.add(viewPager3); //將四種不同 layout 加入 Arraylist 中*/

        List<Integer> list = new ArrayList<Integer>();
        for(int i : pictures){
            list.add(i);
        }

        mAdapter = new MyViewPagerAdapter(this, list);

        viewPager.setAdapter(mAdapter); //將 Arratlist 設定給 viewPager
        viewPager.setCurrentItem(0);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() //ViewPager 頁面滑動監聽器
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) //當頁面滑動到其中一頁時，觸發該頁對應的 RadioButton 按鈕
            {
                switch (position)
                {
                    case 0:
                        radioGroup.check(R.id.radioButton0);
                        buttonStart.setVisibility(View.INVISIBLE);
                        buttonVideo.setVisibility(View.INVISIBLE);
                        break;

                    case 1:
                        radioGroup.check(R.id.radioButton1);
                        buttonStart.setVisibility(View.INVISIBLE);
                        buttonVideo.setVisibility(View.INVISIBLE);
                        break;

                    case 2:
                        radioGroup.check(R.id.radioButton2);
                        buttonStart.setVisibility(View.INVISIBLE);
                        buttonVideo.setVisibility(View.INVISIBLE);
                        break;

                    case 3:
                        radioGroup.check(R.id.radioButton3);
                        buttonStart.setVisibility(View.INVISIBLE);
                        buttonVideo.setVisibility(View.INVISIBLE);
                        break;
                    case 4:
                        radioGroup.check(R.id.radioButton4);
                        buttonStart.setVisibility(View.INVISIBLE);
                        buttonVideo.setVisibility(View.INVISIBLE);
                        break;
                    case 5:
                        radioGroup.check(R.id.radioButton5);
                        buttonStart.setVisibility(View.VISIBLE);
                        buttonVideo.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state)
            {}
        });
        buttonStart = (Button) findViewById(R.id.button1);
        buttonStart.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        buttonVideo = (Button) findViewById(R.id.button2);
        buttonVideo.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View v) {
                String url = "https://www.youtube.com/watch?v=ndW0vyZTfZc&feature=youtu.be";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });
    }
    public class MyViewPagerAdapter extends PagerAdapter
    {
        private List<Integer> mListViews;
        private Context context;
        private LayoutInflater inflater;

        public MyViewPagerAdapter(Context context, List<Integer> mListViews)
        {
            this.context = context;
            this.mListViews = mListViews;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object)
        {
            container.removeView((LinearLayout) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position)
        {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            // 佈局
            View itemView = inflater.inflate(R.layout.cell_pager_image, container, false);

            // 佈局元件內容
            ImageView imageView = (ImageView)itemView.findViewById(R.id.imageView);
            imageView.setImageResource(mListViews.get(position));

            // 加載
            (container).addView(itemView);

            return itemView;
        }

        @Override
        public int getCount()
        {
            return  mListViews.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1)
        {
            return arg0 == arg1;
        }
    }
}

