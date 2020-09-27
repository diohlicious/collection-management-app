package com.studioh.cma.main;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.navigation.NavigationView;
import com.naa.data.Dson;
import com.studioh.cma.AppActivity;
import com.studioh.cma.R;
import com.studioh.cma.main.homeadapter.AdvHome;
import com.studioh.cma.main.homeadapter.PageAdapter;
import com.studioh.cma.main.homeadapter.pageindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.List;

import static com.naa.utils.InternetX.getSetting;

public class MainActivity extends AppActivity{

    private DrawerLayout mDrawerlayout;
    private ActionBarDrawerToggle mToggle;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar mtoolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mtoolbar);

        mDrawerlayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerlayout, mtoolbar, R.string.open, R.string.close);
        mDrawerlayout.addDrawerListener(mToggle);
        mToggle.syncState();
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        loadFragment(new HomeFragment(), "id_home_fr");

        NavigationView navigationView = findViewById(R.id.nav_view);
        final View headerview = navigationView.getHeaderView(0);
        headerview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new ProfileFragment(), "id_profile_fr");
            }
        });
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id=menuItem.getItemId();
                switch (id){
                    case R.id.nav_home:
                        loadFragment(new HomeFragment(),"id_home_fr");
                        break;
                    case R.id.nav_task:
                        loadFragment(new TaskFragment(), "id_task_fr");
                        break;
                    case R.id.nav_inbox:
                        loadFragment(new InboxFragment(), "id_inbox_fr");
                        break;
                    default:
                        return true;
                }
                return true;
            }
        });
        /*HomeFragment homeFragment = (HomeFragment)getSupportFragmentManager().
                findFragmentByTag("id_home_fr");
        homeFragment.advToday();*/
    }

    private void loadFragment(Fragment fragment, String tag){
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment, tag).commit();
        closeDrawer(mDrawerlayout);
        fragmentTransaction.addToBackStack(null);
    }
    public static void closeDrawer(DrawerLayout drawerLayout) {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeDrawer(mDrawerlayout);
    }

}