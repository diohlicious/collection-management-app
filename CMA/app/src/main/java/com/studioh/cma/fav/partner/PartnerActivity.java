package com.studioh.cma.fav.partner;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.studioh.cma.AppActivity;
import com.studioh.cma.R;
import com.studioh.cma.fav.partner.PartnerAdapter;

public class PartnerActivity extends AppActivity {
    private FloatingActionButton mMainFab, mLocateFab, mReportFab, mInfoUpdFab, mRecoveryFab;
    private TextView mLocateTxt, mReportTxt, mInfoUpdTxt, mRecoveryTxt;
    private ConstraintLayout mContainerAll;
    private boolean isOpen;
    private Animation mFabOpenAnim, mFabCloseAnim;
    private String res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partner);

        setTitle("Partner");

        TabLayout tabLayout = findViewById(R.id.tab_layout);

        final ViewPager viewPager = findViewById(R.id.view_pager);

        PagerAdapter partnerAdapter = new PartnerAdapter(getSupportFragmentManager(),
                tabLayout.getTabCount());

        viewPager.setAdapter(partnerAdapter);

        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        viewPager.setOffscreenPageLimit(4);

        tabLayout.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

}
