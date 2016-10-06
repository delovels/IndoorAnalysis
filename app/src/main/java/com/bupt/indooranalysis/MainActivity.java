package com.bupt.indooranalysis;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bupt.indooranalysis.fragment.DataFragment;
import com.bupt.indooranalysis.fragment.HistoryFragment;
import com.bupt.indooranalysis.fragment.InspectFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,InspectFragment.OnFragmentInteractionListener {

    private ViewPager mPager;
    private ArrayList<Fragment> mFragmentList = new ArrayList<Fragment>();
    private FragmentPagerAdapter fragmentPagerAdapter;

    private TextView mTabInspect, mTabHistory, mTabData;

    private InspectFragment inspectFragment;
    private HistoryFragment historyFragment;
    private DataFragment dataFragment;

    private int currentIndex;
    private int screenWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponent();
    }

    protected void initComponent(){

        //init tab
        mTabInspect = (TextView) findViewById(R.id.txt_tab_inspect);
        mTabHistory = (TextView) findViewById(R.id.txt_tab_history);
        mTabData = (TextView) findViewById(R.id.txt_tab_data);
        mPager = (ViewPager) findViewById(R.id.container);

        //init app toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        //init app navigation
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

//        inspectFragment = new InspectFragment();
//        historyFragment = new HistoryFragment();
//        dataFragment = new DataFragment();
//
//        mFragmentList.add(inspectFragment);
//        mFragmentList.add(historyFragment);
//        mFragmentList.add(dataFragment);

        mFragmentList.add(new InspectFragment());
        mFragmentList.add(new InspectFragment());
        mFragmentList.add(new InspectFragment());

        fragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(),mFragmentList);
        mPager.setAdapter(fragmentPagerAdapter);
        mPager.setCurrentItem(0);
        mTabInspect.setTextColor(Color.BLACK);
        mTabHistory.setTextColor(Color.GRAY);
        mTabData.setTextColor(Color.GRAY);
        Log.i("Init Component","add Fragment");

        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                Log.i("Init Component","onPageScrolled");
//                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mTabLine.getLayoutParams();
//
//                if (currentIndex == 0 && position == 0)// 0->1
//                {
//                    lp.leftMargin = (int) (positionOffset * (screenWidth * 1.0 / 3) + currentIndex
//                            * (screenWidth / 3));
//
//                } else if (currentIndex == 1 && position == 0) // 1->0
//                {
//                    lp.leftMargin = (int) (-(1 - positionOffset)
//                            * (screenWidth * 1.0 / 3) + currentIndex
//                            * (screenWidth / 3));
//
//                } else if (currentIndex == 1 && position == 1) // 1->2
//                {
//                    lp.leftMargin = (int) (positionOffset * (screenWidth * 1.0 / 3) + currentIndex
//                            * (screenWidth / 3));
//                } else if (currentIndex == 2 && position == 1) // 2->1
//                {
//                    lp.leftMargin = (int) (-(1 - positionOffset)
//                            * (screenWidth * 1.0 / 3) + currentIndex
//                            * (screenWidth / 3));
//                }
//                mTabLine.setLayoutParams(lp);
            }

            @Override
            public void onPageSelected(int position) {

                mTabInspect.setTextColor(Color.GRAY);
                mTabHistory.setTextColor(Color.GRAY);
                mTabData.setTextColor(Color.GRAY);

                mTabInspect.setBackground(null);
                mTabHistory.setBackground(null);
                mTabData.setBackground(null);

                switch (position){
                    case 0:
                        mTabInspect.setTextColor(Color.BLACK);
                        mTabInspect.setBackground(getDrawable(R.drawable.shape_rect_button));
                        break;
                    case 1:
                        mTabHistory.setTextColor(Color.BLACK);
                        mTabHistory.setBackground(getDrawable(R.drawable.shape_rect_button));
                        break;
                    case 2:
                        mTabData.setTextColor(Color.BLACK);
                        mTabData.setBackground(getDrawable(R.drawable.shape_rect_button));
                        break;
                    default:break;
                }

                currentIndex = position;

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

       // initTabLineWidth();

    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        return;
    }

    public class MyFragmentPagerAdapter extends FragmentPagerAdapter{

        ArrayList<Fragment> list;
        public MyFragmentPagerAdapter(FragmentManager fm, ArrayList<Fragment> list){
            super(fm);
            this.list = list;
        }

        @Override
        public Fragment getItem(int position) {
            return list.get(position);
        }

        @Override
        public int getCount() {
            return list.size();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_history) {
            // Handle the History request

            startActivity(new Intent(this,TabbedActivity.class));
        } else if (id == R.id.nav_data) {

        } else if (id == R.id.nav_setting) {

        } else if (id == R.id.nav_info) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
