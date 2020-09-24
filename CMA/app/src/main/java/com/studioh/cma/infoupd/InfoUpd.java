package com.studioh.cma.infoupd;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.naa.data.Dson;
import com.naa.data.Utility;
import com.naa.data.UtilityAndroid;
import com.naa.utils.Messagebox;
import com.studioh.cma.AppActivity;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.studioh.cma.R;
import com.studioh.srv.StudiohRecyclerAdapter;
import com.studioh.srv.StudiohViewHolder;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class InfoUpd extends AppActivity {

    private FloatingActionButton mMainFab, mLocateFab, mReportFab, mInfoUpdFab, mRecoveryFab;
    private TextView mLocateTxt, mReportTxt, mInfoUpdTxt, mRecoveryTxt;
    private ConstraintLayout mContainerAll;
    private boolean isOpen;
    private Animation mFabOpenAnim, mFabCloseAnim;
    private String res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_update);

        setTitle("Info Update");

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        TabItem tabAddresses = findViewById(R.id.adresses);
        TabItem tabContacts = findViewById(R.id.contacts);
        TabItem tabAsset = findViewById(R.id.asset);
        TabItem tabMitra = findViewById(R.id.mitra);

        fab();

        final ViewPager viewPager = findViewById(R.id.view_pager);

        PagerAdapter infoUpdAdapter = new InfoUpdAdapter(getSupportFragmentManager(),
                tabLayout.getTabCount());

        viewPager.setAdapter(infoUpdAdapter);

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
        loadAddresses();
        loadContacts();
        //loadOverAsset();


    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 12 && resultCode == RESULT_OK) {
            loadAddresses();
            loadContacts();
            //loadOverAsset();
        }
    }

    public void fab() {
        mContainerAll = findView(R.id.action, R.id.container_all, ConstraintLayout.class);
        FloatingActionButton mMainFab = findView(R.id.action, R.id.main_fab, FloatingActionButton.class);
        FloatingActionButton mLocateFab = findView(R.id.action, R.id.locate, FloatingActionButton.class);
        FloatingActionButton mReportFab = findView(R.id.action, R.id.report, FloatingActionButton.class);
        FloatingActionButton mInfoUpdFab = findView(R.id.action, R.id.updinfo, FloatingActionButton.class);
        FloatingActionButton mRecoveryFab = findView(R.id.action, R.id.reco, FloatingActionButton.class);

        TextView mLocateTxt = findView(R.id.action, R.id.locateTxt, TextView.class);
        TextView mReportTxt = findView(R.id.action, R.id.reportTxt, TextView.class);
        TextView mInfoUpdTxt = findView(R.id.action, R.id.infoUpdTxt, TextView.class);
        TextView mRecoveryTxt = findView(R.id.action, R.id.recoTxt, TextView.class);

        mFabOpenAnim = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        mFabCloseAnim = AnimationUtils.loadAnimation(this, R.anim.fab_close);

        mContainerAll.setVisibility(View.INVISIBLE);
        isOpen = false;

        mMainFab.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {
                if (isOpen) {
                    mContainerAll.startAnimation(mFabCloseAnim);
                    isOpen = false;
                } else {
                    mContainerAll.startAnimation(mFabOpenAnim);
                    isOpen = true;
                }
            }
        });
    }

    public void loadInfoMitra(){
        newProcess(new Messagebox.DoubleRunnable() {
            String res;

            public void run() {
                String sagrno = getSetting("agrno");
                Dson dson = getDefaultDataRaw();
                dson.set("AgreementNo", sagrno);//replace string dengan param sagrno
                res = postHttpRaw("RPM/RPM_GetDataInfoMitra", dson);
                autoToken(res);
            }

            public void runUI() {
                //Dson dson = Dson.readJson(res);
                //if (dson.get("ResponseCode").asString().equalsIgnoreCase("00")) {
                    //setSetting("dtData", dson.get("data").toJson());
                    //Dson n = Dson.readJson(getSetting("dtData"));
                //Dson n = dson.get("data");
                //demo start
                nListArray.asArray().clear();
                Dson dson = new Dson();
                AssetManager assetManager = getAssets();
                try {
                    InputStream input = assetManager.open("mitra.json");
                    dson = Dson.readDson(isToString(input));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Dson n = dson.get("data");
                //demo end
                for (int i = 0; i < n.size(); i++) {
                    if (n.size() >= 1){
                        nListArray.add(n.get(i));
                    }
                }

                Comparator objComparator = new Comparator() {
                    public int compare(Object o1, Object o2) {
                        DateFormat inputfdt = new SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.US);
                        try {
                            long time1 = inputfdt.parse(new Dson(o1).get("Tanggal").asString()).getTime();
                            long time2 = inputfdt.parse(new Dson(o2).get("Tanggal").asString()).getTime();
                            return  time1 > time2 ? -1 : time1 == time2 ? 0 : 1;
                        }catch (Exception e){}
                        return  0;
                    }
                };
                Collections.sort(nListArray.asArray(), objComparator);

                RecyclerView recyclerVi = findViewById(R.id.rview2);
                recyclerVi.setLayoutManager(new LinearLayoutManager(getActivity()));
                recyclerVi.setHasFixedSize(true);

                recyclerVi.setAdapter(new StudiohRecyclerAdapter(nListArray, R.layout.list_item_infomitra) {
                    @Override
                    public void onBindViewHolder(@NonNull StudiohViewHolder viewHolder, int position) {
                        //viewHolder.find(R.id.dateInfMit, TextView.class).setText(nListArray.get(position).get("Tanggal").asString());
                        try {
                            SimpleDateFormat fdt = new SimpleDateFormat("hh:mm d MMM yyyy", Locale.US);
                            DateFormat inputfdt = new SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.US);
                            viewHolder.find(R.id.dateInfMit, TextView.class).setText(fdt.format(inputfdt.parse(nListArray.get(position).get("Tanggal").asString())));
                        } catch (ParseException e) {
                            System.out.println("printing date Exception ==> "+e.toString());
                            e.printStackTrace();

                        }
                        viewHolder.find(R.id.typeInfMit, TextView.class).setText(nListArray.get(position).get("Type").asString());
                        viewHolder.find(R.id.dateNotesMit, TextView.class).setText(nListArray.get(position).get("Notes").asString());
                    }
                });
                //}else{
                //    //showError(dson.get("ResponseDescription").asString());
                //    showError(dson.asString());
                //}
            }
        });
    }
    public void loadOverAsset() {
        newProcess(new Messagebox.DoubleRunnable() {
            String res;

            public void run() {
                Dson dson = getDefaultDataRaw();
                String sagrno = getSetting("agrno");
                dson.set("AgreementNo", sagrno);
                res = postHttpRaw("RPM/RPM_GetDataOverAsset", dson);
                //setSetting("pp", dson.asJson().toString());
                autoToken(res);
            }

            public void runUI() {
                Dson n = Dson.readJson(res);
                nListArray.asArray().clear();
                if (n.get("ResponseCode").asString().equalsIgnoreCase("00")) {
                    //showError(getSetting("agrno"));

                    for (int i = 0; i < n.get("data").get(i).size(); i++) {
                        nListArray.add(n.get("data").get(i));
                    }

                    RecyclerView recyclerView = findViewById(R.id.rview1);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    recyclerView.setHasFixedSize(true);

                    recyclerView.setAdapter(new StudiohRecyclerAdapter(nListArray, R.layout.list_item_overasset) {
                                                @Override
                                                public void onBindViewHolder(@NonNull StudiohViewHolder viewHolder, int position) {
                                                    viewHolder.find(R.id.ThirdParty, TextView.class).setText(nListArray.get(position).get("ThirdParty").asString());//CustomerFullName
                                                    viewHolder.find(R.id.Email, TextView.class).setText(nListArray.get(position).get("Email").asString());//CustomerFullName
                                                    viewHolder.find(R.id.Reason, TextView.class).setText(
                                                            String.format("Reason: %s", nListArray.get(position).get("Reason").asString()));//CustomerFullName
                                                    viewHolder.find(R.id.Notes, TextView.class).setText(
                                                            String.format("Notes: %s", nListArray.get(position).get("Notes").asString()));
                                                    viewHolder.find(R.id.MobilePhone, TextView.class).setText(nListArray.get(position).get("MobilePhone").asString());
                                                    viewHolder.find(R.id.Phone, TextView.class).setText(nListArray.get(position).get("Phone").asString());
                                                    viewHolder.find(R.id.Address, TextView.class).setText(nListArray.get(position).get("Address").asString());
                                                    viewHolder.find(R.id.Profile, TextView.class).setText(nListArray.get(position).get("Profile").asString());

                                                }
                                            }/*.setOnitemClickListener(new NikitaRecyclerAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(Dson parent, View view, int position) {
                            setSetting("OverAst", nListArray.get(position).toJson());
                            Intent intent = new Intent(getActivity(), OverAsset.class);
                            startActivity(intent);
                        }
                    })*/
                            //addOverAsset
                    );

                } else {
                    showError(getIntentStringExtra("agrno"));
                }

            }

        });
    }


    public void loadAddresses() {
        newProcess(new Messagebox.DoubleRunnable() {
            public void run() {
                Dson dson = getDefaultDataRaw();
                String sagrno = getSetting("agrno");
                dson.set("AgreementNo", sagrno);
                res = postHttpRaw("RPM/RPM_GetDataCollectionPoint", dson);
                autoToken(res);
            }

            public void runUI() {
                //production
                /*Dson n = Dson.readJson(res);
                nListArray.asArray().clear();//reset
                if (n.get("ResponseCode").asString().equalsIgnoreCase("00") ) {
                    res=n.asString();
                }else{
                    showError(n.get("error").asString());
                }*/
                //demo start
                Dson n = new Dson();
                AssetManager assetManager = getAssets();
                try {
                    InputStream input = assetManager.open("addresses.json");
                    n = Dson.readDson(isToString(input));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                initAddresses(n);
                //demo end
            }
        });
    }

    public void loadContacts() {
        newProcess(new Messagebox.DoubleRunnable() {
            String res;

            public void run() {
                Dson dson = getDefaultDataRaw();
                String sagrno = getSetting("agrno");
                dson.set("AgreementNo", sagrno);
                res = postHttpRaw("RPM/RPM_GetDataContactInformation", dson);
                autoToken(res);
            }

            public void runUI() {
                //production
                /*Dson n = Dson.readJson(res);
                nListArray.asArray().clear();//reset
                if (n.get("ResponseCode").asString().equalsIgnoreCase("00") ) {
                    res=n.asString();
                }else{
                    showError(n.get("error").asString());
                }*/
                //demo start
                Dson n = new Dson();
                AssetManager assetManager = getAssets();
                try {
                    InputStream input = assetManager.open("contacts.json");
                    n = Dson.readDson(isToString(input));
                    //
                } catch (IOException e) {
                    e.printStackTrace();
                }
                initContacts(n);

                //demo end
            }
        });
    }

    private List<RadioButton> list = new ArrayList<>();

    public void initAddresses(final Dson dcoll) {
        list.clear();//clear
        //TableLayout stk = (TableLayout) findViewById(R.id.tableLayout1);
        TableLayout stk = findViewById(R.id.table_main);
        stk.removeAllViews();
        TableRow tbrow0 = new TableRow(this);
        tbrow0.setBackgroundColor(R.drawable.gradient_table);
        TextView tv0 = new TextView(this);
        tv0.setText("Priority");
        tv0.setTextColor(Color.WHITE);
        tbrow0.addView(tv0);
        TextView tv1 = new TextView(this);
        tv1.setText(" Address ");
        tv1.setTextColor(Color.WHITE);
        tbrow0.addView(tv1);
        TextView tv2 = new TextView(this);
        tv2.setText(" Type ");
        tv2.setTextColor(Color.WHITE);
        tbrow0.addView(tv2);
        stk.addView(tbrow0);
        for (int i = 0; i < dcoll.get("data").size(); i++) {
            if (dcoll.get("data").get(i).size() >= 1) {

                TableRow tbrow = (TableRow) UtilityAndroid.getInflater(getActivity(), R.layout.row_addresses);

                if (dcoll.get("data").get(i).get("Priority").asString().equalsIgnoreCase("1")) {
                    findView(tbrow, R.id.radioPriority, RadioButton.class).setChecked(true);
                } else {
                    findView(tbrow, R.id.radioPriority, RadioButton.class).setChecked(false);
                }
                list.add(findView(tbrow, R.id.radioPriority, RadioButton.class));
                findView(tbrow, R.id.radioPriority, RadioButton.class).setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        RadioButton button = (RadioButton) v;

                        for (int j = 0; j < list.size(); j++) {
                            RadioButton radioButton = list.get(j);
                            if (button.isChecked()) {
                                if (!radioButton.equals(v)) {
                                    radioButton.setChecked(false);
                                }
                            }
                        }
                    }
                });

                findView(tbrow, R.id.txtAddress, TextView.class).setText(dcoll.get("data").get(i).get("Address").asString());
                findView(tbrow, R.id.txtType, TextView.class).setText(dcoll.get("data").get(i).get("Type").asString());
                if (i % 2 == 0) {
                    tbrow.setBackgroundResource(R.color.grey_100);
                }
                stk.addView(tbrow, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            } /*else {
                showError(nListArray.get("Address").asString());
                }*/
        }
    }

    public void initContacts(final Dson dcoll) {
        //TableLayout stk = (TableLayout) findViewById(R.id.tableLayout1);
        TableLayout stk = findViewById(R.id.table_main1);
        stk.removeAllViews();
        TableRow tbrow0 = new TableRow(this);
        tbrow0.setBackgroundColor(R.drawable.gradient_table);
        TextView tv0 = new TextView(this);
        tv0.setText("Priority");
        tv0.setTextColor(Color.WHITE);
        tbrow0.addView(tv0);
        TextView tv1 = new TextView(this);
        tv1.setText(" Information ");
        tv1.setTextColor(Color.WHITE);
        tbrow0.addView(tv1);
        TextView tv2 = new TextView(this);
        tv2.setText(" Category ");
        tv2.setTextColor(Color.WHITE);
        tbrow0.addView(tv2);
        stk.addView(tbrow0);

        for (int i = 0; i < dcoll.get("data").size(); i++) {
            if (dcoll.get("data").get(i).size() >= 1) {
                //nListArray.add(dcoll.get("Address").get(i).asJson());
                if (i % 2 == 0) {
                    float scalingFactor = 0.7f;
                    //TableRow tbrow =  (TableRow)UtilityAndroid.getInflater(getActivity(), R.layout.fragment_contacts);
                    TableRow tbrow = new TableRow(getActivity());
                    tbrow.setBackgroundResource(R.color.grey_100);
                    //tbrow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                    RadioButton t1v = new RadioButton(this);
                    if (dcoll.get("data").get(i).get("Priority").asString().equalsIgnoreCase("1")) {
                        t1v.setChecked(true);
                    } else {
                        t1v.setChecked(false);
                    }
                    t1v.setScaleX(scalingFactor);
                    t1v.setScaleY(scalingFactor);
                    t1v.setGravity(Gravity.CENTER);
                    //t1v.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                    tbrow.addView(t1v);
                    TextView t2v = new TextView(this);
                    t2v.setText(dcoll.get("data").get(i).get("Information").asString());
                    final int finalI = i;
                    t2v.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            call(dcoll.get("data").get(finalI).get("Information").asString());
                        }
                    });
                    t2v.setTextColor(Color.GRAY);
                    //t2v.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
                    tbrow.addView(t2v);
                    TextView t3v = new TextView(this);
                    t3v.setText(dcoll.get("data").get(i).get("Category").asString());
                    t3v.setTextColor(Color.GRAY);
                    tbrow.addView(t3v);
                    stk.addView(tbrow);
                } else {
                    float scalingFactor = 0.7f;
                    TableRow tbrow = new TableRow(this);
                    RadioButton t1v = new RadioButton(this);
                    if (dcoll.get("data").get(i).get("Priority").asString().equalsIgnoreCase("1")) {
                        t1v.setChecked(true);
                    } else {
                        t1v.setChecked(false);
                    }
                    t1v.setScaleX(scalingFactor);
                    t1v.setScaleY(scalingFactor);
                    t1v.setGravity(Gravity.CENTER);
                    tbrow.addView(t1v);
                    TextView t2v = new TextView(this);
                    t2v.setText(dcoll.get("data").get(i).get("Information").asString());
                    final int finalI = i;
                    t2v.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            call(dcoll.get("data").get(finalI).get("Information").asString());
                        }
                    });
                    t2v.setTextColor(Color.GRAY);
                    tbrow.addView(t2v);
                    TextView t3v = new TextView(this);
                    t3v.setText(dcoll.get("data").get(i).get("Category").asString());
                    tbrow.addView(t3v);
                    stk.addView(tbrow);
                }

            } /*else {
                showError(nListArray.get("ContactInformation").asString());
            }*/
        }
    }
}
