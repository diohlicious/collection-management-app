package com.studioh.cma.fav;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.naa.data.Dson;
import com.naa.data.Utility;
import com.naa.utils.InternetX;
import com.naa.utils.Messagebox;
import com.studioh.cma.AppActivity;
import com.studioh.cma.R;
import com.studioh.cma.main.SrcFilter;
import com.studioh.cma.main.TaskActivity;
import com.studioh.srv.StudiohRecyclerAdapter;
import com.studioh.srv.StudiohViewHolder;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddActivity extends AppActivity implements Runnable{
    @SuppressLint("ResourceAsColor")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        setTitle("ADD ACCOUNT");
        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        Toolbar topToolBar = findViewById(R.id.toolbar);
        setSupportActionBar(topToolBar);
        ActionBar actionBar = getSupportActionBar();;
        actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.green_A900)));
        actionBar.setDisplayHomeAsUpEnabled(true);

        findView( R.id.h_bahan, R.id.txtTitle, TextView.class).setText("WO In");
        findView( R.id.h_take, R.id.txtTitle, TextView.class).setText("Kuota");
        findView( R.id.h_add, R.id.txtTitle, TextView.class).setText("Take In");
        findView( R.id.h_os, R.id.txtTitle, TextView.class).setText("OS");
        find(R.id.main_toolbar_send).setVisibility(View.VISIBLE);
        find(R.id.main_toolbar_send).setVisibility(View.VISIBLE);

        find(R.id.txtSearch, EditText.class).setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch();
                    return true;
                }
                return false;
            }
        });

        final RecyclerView recyclerView = findViewById(R.id.rview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        recyclerView.setAdapter(new StudiohRecyclerAdapter(nListArray, R.layout.list_item_all) {
            @Override
            public void onBindViewHolder(@NonNull StudiohViewHolder viewHolder, int position) {

                viewHolder.find(R.id.checkBoxA, SwitchCompat.class).setText("INT");
                viewHolder.find(R.id.checkBoxB, SwitchCompat.class).setText("EXT");

                viewHolder.find(R.id.checkBoxA, SwitchCompat.class).setTag(String.valueOf(position));
                viewHolder.find(R.id.checkBoxA, SwitchCompat.class).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        String spos = String.valueOf(buttonView.getTag());
                        nListArray.get(Utility.getInt(spos)).set("_A",isChecked?"1":"");

                        if (isChecked){
                            if ( buttonView.getParent() instanceof ViewGroup){
                                findView( ((ViewGroup)(buttonView.getParent())) , R.id.checkBoxB, SwitchCompat.class).setChecked(false);
                            }
                        }
                        invalidateX();
                    }
                });
                viewHolder.find(R.id.checkBoxB, SwitchCompat.class).setTag(String.valueOf(position));
                viewHolder.find(R.id.checkBoxB, SwitchCompat.class).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        String spos = String.valueOf(buttonView.getTag());
                        nListArray.get(Utility.getInt(spos)).set("_B",isChecked?"1":"");

                        if (isChecked){
                            if ( buttonView.getParent() instanceof ViewGroup){
                                findView( ((ViewGroup)(buttonView.getParent())) , R.id.checkBoxA, SwitchCompat.class).setChecked(false);
                            }
                        }
                        invalidateX();
                    }
                });

                viewHolder.find(R.id.txtTitle, TextView.class).setText(nListArray.get(position).get("CustomerFullName").asString());//CustomerFullName
                viewHolder.find(R.id.txtMsg1, TextView.class).setText(nListArray.get(position).get("AgreementNo").asString());//CustomerFullName
                viewHolder.find(R.id.txtMsg3, TextView.class).setText(nListArray.get(position).get("LicensePlate").asString());//CustomerFullName
                viewHolder.find(R.id.txtMsg4, TextView.class).setText(nListArray.get(position).get("OSPrincipalAmount").asString());
                if (nListArray.get(position).get("AssetType").asString().equalsIgnoreCase("MOTOR")) {
                    viewHolder.find(R.id.imgAvatar, ImageView.class).setImageResource(R.drawable.ic_motorcycle);
                }else if (nListArray.get(position).get("AssetType").asString().equalsIgnoreCase("MOBIL")) {
                    viewHolder.find(R.id.imgAvatar, ImageView.class).setImageResource(R.drawable.ic_car);
                }else{

                }
                try {
                    SimpleDateFormat fdt = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
                    DateFormat inputfdt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                    viewHolder.find(R.id.txtMsg5, TextView.class).setText((fdt.format(inputfdt.parse(nListArray.get(position).get("WODate").asString()))));
                } catch (ParseException e) {
                    System.out.println("printing date Exception ==> "+e.toString());
                    e.printStackTrace();
                }
                for (int i = 0; i < nListArray.get(position).get("Address").size(); i++) {
                    if (nListArray.get(position).get("Address").get(i).get("Priority").asString().equalsIgnoreCase("1")) {
                        viewHolder.find(R.id.txtMsg2, TextView.class).setText(nListArray.get(position).get("Address").get(i).get("Address").asString());
                    }
                }
/*                if (nListArray.get(position).get("Address").get(0).get("Priority").asString().equalsIgnoreCase("1")) {
                    viewHolder.find(R.id.txtMsg2, TextView.class).setText(nListArray.get(position).get("Address").get(0).get("Address").asString());
                }*/

            }
        });
        final Dson n = Dson.readJson(getSetting("Monthly"));
        final Dson nfiltertmp = Dson.newArray();
        for (int i = 0; i < n.size(); i++) {
            Dson one = n.get(i);
            Dson two = one.get("Address");
            for (int j = 0; j < two.size(); j++) {
                if (two.get(j).get("Priority").asString().equals("1")) {
                    Dson three = two.get(j);
                    Dson four = Dson.newObject();
                    four.set("kota", three.get("City"));
                    four.set("kelurahan", three.get("Kelurahan"));
                    four.set("kecamatan", three.get("Kecamatan"));
                    nfiltertmp.add(four);
                }
            }
        }
        //setSetting("Monthly", nListArray.toJson());
        find(R.id.imgList, ImageView.class).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SrcFilter.class);
                //nfilter
                intent.putExtra("filter", nfiltertmp.toString());
                startActivityForResult(intent, 12);
            }
        });

        find(R.id.main_toolbar_send).setVisibility(View.VISIBLE);

        invalidateX();
        find(R.id.main_toolbar_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newProcess(new Messagebox.DoubleRunnable() {
                    String res;
                    public void run() {
                        Dson dson = getDefaultDataRaw();

                        Dson darr = Dson.newArray();
                        for (int i = 0; i < nListArray.size(); i++) {
                            Dson dv = Dson.newObject();
                            dv.set("AgreementNo",  nListArray.get(i).get("AgreementNo").asString());
                            dv.set("is_rpk",nListArray.get(i).get("_A").asString());
                            dv.set("is_ral",nListArray.get(i).get("_B").asString());
                            dv.set("flag","AddAccount");
                            if (nListArray.get(i).get("_A").asString().equalsIgnoreCase("1") ||
                                    nListArray.get(i).get("_B").asString().equalsIgnoreCase("1")){
                                darr.add(dv);
                            }
                        }
                        dson.set("data",darr);

                        Dson hdrs = getDefaultHeader();
                        res =  InternetX.postHttpConnectionRaw(getBaseUrl("RPM/RPM_FlaggingRPK_Monthly"), hdrs, dson);
                    }
                    public void runUI() {

                        Dson dson = Dson.readJson(res);
                        //dson = Dson.newObject();
                        //dson.set("ResponseCode", "00");//bypass
                        if (dson.get("ResponseCode").asString().equalsIgnoreCase("00")) {
                            AddDataLog("Add Account", dson.get("ResponseDescription").asString());
                            setSetting("dailyRPKadd", findView( R.id.h_add, R.id.txtMessage, TextView.class).getText().toString());
                            setSetting("MONTHadd",Utility.Now().substring(0,5));
                            setSetting("MonthlyAdd", nListArray.toJson());
                            setSetting("approveAdd", "");
                            finish();
                        }else if (dson.get("ResponseCode").asString().equalsIgnoreCase("99")) {
                            AddDataLog("Add Account", dson.get("ResponseDescription").asString());
                            showInfo(dson.get("ResponseDescription").asString());
                        } else {
                            AddDataLog("Add Account", res);
                            showInfo(res);
                        }
                    }
                });
            }
        });

        TaskActivity.checkApprove(this,this, this);
    }

    public void run() {
        performSearch();
    }

    protected void onActivityResult(int requestCode, int resultCode,   Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 12 && resultCode == RESULT_OK) {
            try {
                performFilter(getIntentStringExtra(data, "nfilter"));
                //GoToURL("http://api.whatsapp.com/send?phone=" + "6287886722267" + "&text=" + getIntentStringExtra(data, "nfilter"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void performSearch(){
        newProcess(new Messagebox.DoubleRunnable() {
            String res;
            String result;
            public void run() {
                String txt = find(R.id.txtSearch, EditText.class).getText().toString().toLowerCase();
                Dson dson = getDefaultDataRaw();
                dson.set("LicensePlate","ALL");
                dson.set("search",txt);
                dson.set("LoginID",getSetting("LoginID"));
                dson.set("MobilePhone", getSetting("MobilePhoneNo"));
                /*res =  postHttpRaw("RPM/RPM_GetDataRPK_AddAccount", dson );
                autoToken(res);*/
                Dson hdrs = getDefaultHeader();
                result = dson.toJson();
                res = InternetX.postHttpConnectionRaw(getBaseUrl("RPM/RPM_GetDataRPK_AddAccount"), hdrs, dson);
            }

            public void runUI() {
                Dson n = Dson.readJson(res);
                if (n.get(0).get("ResponseCode").asString().equalsIgnoreCase("00") ) {
                    AddDataLog("Inquiry Account", n.get(0).get("ResponseDescription").asString());
                    nListArray.asArray().clear();
                    for (int i = 0; i < 10; i++) {
                        nListArray.add(n.get(i));
                    }
                    setSetting("addRpk", nListArray.toJson());
                    RecyclerView recyclerView = findViewById(R.id.rview);
                    recyclerView.getAdapter().notifyDataSetChanged();

                }else{
                    nListArray.asArray().clear();
                    RecyclerView recyclerView = findViewById(R.id.rview);
                    recyclerView.getAdapter().notifyDataSetChanged();
                    //showError(n.get("error").asString());
                    //GoToURL("http://api.whatsapp.com/send?phone=" + "6287886722267" + "&text=" + result);
                    AddDataLog("Inquiry Account", res);
                    showError(res);
                }

            }

        });
    }

    private void invalidateX(){
        Dson dataH = Dson.newObject();
        Dson dson = Dson.readDson(getSetting("Personal"));

        int quota = dson.get("Monthly_RPK").asInteger();
        double osp =  dson.get("Monthly_Recovery_OSP").asDouble();
        int take = 0;
        for (int i = 0; i < nListArray.size(); i++) {
            Dson n = nListArray;
            if (n.get(i).get("_A").asString().equalsIgnoreCase("1")||n.get(i).get("_B").asString().equalsIgnoreCase("1")) {
                osp = osp - nListArray.get(i).get("OSPrincipalAmount").asDouble();
                take++;
            }
        }
      /*  String os = String.format("%.2f", osp/1000000);
        String tg = String.format("%.2f", dson.get("Monthly_Target").asDouble()/1000000);
        String nc = String.format("%.2f", dson.get("Monthly_Recovery_NCI").asDouble()/1000000);*/


        dataH.set("wo",nListArray.size());
        dataH.set("quota",quota);
        dataH.set("take",take);
        dataH.set("os", nListArray.size()-take);

        findView( R.id.h_bahan, R.id.txtMessage, TextView.class).setText(String.valueOf(dataH.get("wo").asInteger()));//WO In
        findView( R.id.h_take, R.id.txtMessage, TextView.class).setText(String.valueOf(quota));//Kuota Monthly_RPK
        findView( R.id.h_add, R.id.txtMessage, TextView.class).setText(String.valueOf(take));//Take In
        findView( R.id.h_os, R.id.txtMessage, TextView.class).setText(String.valueOf(dataH.get("os").asInteger()));//OS

        setSetting("dataH", dataH.toJson());

    }
    public void performFilter(String filter) throws ParseException {
        Dson nfilter = Dson.readJson(filter);
        if (nfilter.containsKey("kendaraan")
                ||nfilter.containsKey("os0")
                ||nfilter.containsKey("kota")
                ||nfilter.containsKey("yfrom")
                ||nfilter.containsKey("yto")) {
            nListArray.asArray().clear();
            Dson n = Dson.readJson(getSetting("addRpk"));
            Calendar newDate = Calendar.getInstance();
            DateFormat ydt = new SimpleDateFormat("yyyy", Locale.US);
            int os0 = Integer.parseInt(nfilter.get("os0").asString().equals("") ? "0" : nfilter.get("os0").asString());
            int os1 = Integer.parseInt(nfilter.get("os1").asString().equals("") ? "999999999" : nfilter.get("os1").asString());
            int yfrom = Integer.parseInt(nfilter.get("yfrom").asString().equals("") ?
                    String.valueOf(Calendar.getInstance().get(Calendar.YEAR) - 10) :
                    nfilter.get("yfrom").asString()
            );
            int yto = Integer.parseInt(nfilter.get("yto").asString().equals("") ?
                    String.valueOf(Calendar.getInstance().get(Calendar.YEAR)) :
                    nfilter.get("yto").asString()
            );
            for (int i = 0; i < n.size(); i++) {
                if (n.get(i).get("AssetType").asString().contains(nfilter.get("kendaraan").asString())
                        && isWithinRangeOs(n.get(i).get("OSPrincipalAmount").asInteger(), os0, os1)
                        //&& n.get(i).get("OSInterestAmount").asInteger() >= os0
                        //&& n.get(i).get("OSInterestAmount").asInteger() <= os1
                        && isWithinRange(n.get(i).get("WODate").asString(),
                        yfrom,
                        yto)
                        && filterAddress(n.get(i).get("Address"),
                        nfilter.get("kota").asString(),
                        nfilter.get("kecamatan").asString(),
                        nfilter.get("kelurahan").asString())
                ) {
                    nListArray.add(n.get(i));
                }
            }
            RecyclerView recyclerView = findViewById(R.id.rview);
            recyclerView.getAdapter().notifyDataSetChanged();
        }else{
            nListArray.asArray().clear();
            Dson n =Dson.readJson(getSetting("addRpk"));
            for (int i = 0; i < n.size(); i++) {
                nListArray.add(n.get(i));
            }
            RecyclerView recyclerView = findViewById(R.id.rview);
            recyclerView.getAdapter().notifyDataSetChanged();
        }
    }
    boolean isWithinRange(String testDate, int fromDate, int toDate) throws ParseException {
        DateFormat ydt = new SimpleDateFormat("yyyy", Locale.US);
        DateFormat ydtinput = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        int date = Integer.parseInt(ydt.format(ydtinput.parse(testDate)));
        return !(date < fromDate || date > toDate);
    }

    boolean isWithinRangeOs(int os, int os0, int os1) {
        return !(os < os0 || os > os1);
    }

    private boolean filterAddress(Dson naddr, String kota, String kecamatan, String kelurahan) {
        for (int i = 0; i < naddr.size(); i++) {
            if (naddr.get(i).get("Priority").asString().equals("1")
                    && naddr.get(i).get("City").asString().contains(kota)
                    && naddr.get(i).get("Kecamatan").asString().contains(kecamatan)
                    && naddr.get(i).get("Kelurahan").asString().contains(kelurahan)) {
                return true;
            }
        }
        return false;
    }

}
