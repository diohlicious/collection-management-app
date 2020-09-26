package com.studioh.cma.main;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.naa.data.Dson;
import com.naa.data.Utility;
import com.naa.utils.InternetX;
import com.naa.utils.Messagebox;
import com.studioh.cma.AppActivity;
import com.studioh.cma.R;
import com.studioh.srv.StudiohRecyclerAdapter;
import com.studioh.srv.StudiohViewHolder;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class TaskActivity extends AppActivity  implements Runnable{
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        //drawerLayout = findViewById(R.id.drawer_layout);
/*        TextView mHdr = findView(R.id.main_toolbar, R.id.main_toolbar_txt, TextView.class);
        mHdr.setText("Task");
        ImageView mSubmit = findView(R.id.main_toolbar, R.id.more, ImageView.class);
        mSubmit.setImageResource(R.drawable.ic_submit);*/

        findView(R.id.h_bahan, R.id.txtTitle, TextView.class).setText("RPK");
        findView(R.id.h_take, R.id.txtTitle, TextView.class).setText("Visited");
        findView(R.id.h_add, R.id.txtTitle, TextView.class).setText("Today");
        findView(R.id.h_os, R.id.txtTitle, TextView.class).setText("Outstanding");

        invalidateX();

        RecyclerView recyclerView = findViewById(R.id.rview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        Dson n = Dson.readJson(getSetting("Monthly"));
        //dson filter
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

        textChangeListener(find(R.id.txtSearch, EditText.class), new Runnable() {
            @Override
            public void run() {
                performSearch();
            }
        });
        find(R.id.imgList, ImageView.class).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SrcFilter.class);
                //nfilter
                intent.putExtra("filter", nfiltertmp.toString());
                startActivityForResult(intent, 12);
            }
        });

        recyclerView.setAdapter(new StudiohRecyclerAdapter(nListArray, R.layout.list_item_all) {
                    @Override
                    public void onBindViewHolder(@NonNull final StudiohViewHolder viewHolder, final int position) {

                        Locale localeID = new Locale("in", "ID");
                        NumberFormat separator = NumberFormat.getInstance(localeID);

                        viewHolder.find(R.id.checkBoxA).setVisibility(View.GONE);
                        viewHolder.find(R.id.checkBoxB).setEnabled(false);

                        viewHolder.find(R.id.checkBoxB, SwitchCompat.class).setTag(String.valueOf(position));
                        viewHolder.find(R.id.checkBoxB, SwitchCompat.class).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                String spos = String.valueOf(buttonView.getTag());
                                viewHolder.find(R.id.checkBoxB, SwitchCompat.class).setEnabled(false);
                                nListArray.get(Utility.getInt(spos)).set("_DAYLY", isChecked ? Utility.Now().substring(0, 10) : "");
                                nListArray.get(Utility.getInt(spos)).set("is_Daily", isChecked ? Utility.Now().substring(0, 10) : "");
                                nListArray.get(Utility.getInt(spos)).set("_C", isChecked ? "1" : "");
                                invalidateX();
                            }
                        });


                        invalidateX();
                        if (nListArray.get(position).get("_DAYLY").asString().equalsIgnoreCase(Utility.Now().substring(0, 10))) {
                            viewHolder.find(R.id.checkBoxB, SwitchCompat.class).setEnabled(false);
                            viewHolder.find(R.id.checkBoxB, SwitchCompat.class).setChecked(true);
                        }

                        viewHolder.find(R.id.txtTitle, TextView.class).setText(nListArray.get(position).get("CustomerFullName").asString());//CustomerFullName
                        viewHolder.find(R.id.txtMsg1, TextView.class).setText(nListArray.get(position).get("AgreementNo").asString());//CustomerFullName
                        viewHolder.find(R.id.txtMsg3, TextView.class).setText(nListArray.get(position).get("LicensePlate").asString());//CustomerFullName
                        viewHolder.find(R.id.txtMsg4, TextView.class).setText(separator.format(nListArray.get(position).get("OSPrincipalAmount").asNumber()));
                        try {
                            SimpleDateFormat fdt = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
                            DateFormat inputfdt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                            viewHolder.find(R.id.txtMsg5, TextView.class).setText((fdt.format(inputfdt.parse(nListArray.get(position).get("WODate").asString()))));
                        } catch (ParseException e) {
                            System.out.println("printing date Exception ==> " + e.toString());
                            e.printStackTrace();
                        }

                        for (int i = 0; i < nListArray.get(position).get("Address").size(); i++) {
                            if (nListArray.get(position).get("Address").get(i).get("Priority").asString().equalsIgnoreCase("1")) {
                                viewHolder.find(R.id.txtMsg2, TextView.class).setText(nListArray.get(position).get("Address").get(i).get("Address").asString());
                            }
                        }

                        if (nListArray.get(position).get("AssetType").asString().equalsIgnoreCase("MOTOR")) {
                            viewHolder.find(R.id.imgAvatar, ImageView.class).setImageResource(R.drawable.ic_motorcycle);
                        } else if (nListArray.get(position).get("AssetType").asString().equalsIgnoreCase("MOBIL")) {
                            viewHolder.find(R.id.imgAvatar, ImageView.class).setImageResource(R.drawable.ic_car);
                        } else {

                        }
                    }

                }.setOnitemClickListener(new StudiohRecyclerAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(final Dson parent, final View view, final int position) {
                        if (!parent.get(position).get("_C").asString().equals("1")) {
                            TimePickerDialog timePickerDialog = new TimePickerDialog(TaskActivity.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                    SimpleDateFormat oldtime = new SimpleDateFormat("k:m");
                                    SimpleDateFormat newtime = new SimpleDateFormat("kk:mm");
                                    String stime = null;
                                    try {
                                        stime = newtime.format(oldtime.parse(selectedHour + ":" + selectedMinute));
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    nListArray.get(position).set("visit_time", stime);
                                    findView(view, R.id.checkBoxB, SwitchCompat.class).setChecked(true);
                                    findView(view, R.id.checkBoxB, SwitchCompat.class).setEnabled(true);
                                    //showInfo(selectedHour + ":" + selectedMinute);
                                    invalidateX();
                                }

                            }, 0, 0, true);
                            timePickerDialog.show();
                        }
                    }
                })
        );

        /*mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newProcess(new Messagebox.DoubleRunnable() {
                    String res;

                    public void run() {
                        Dson dson = getDefaultDataRaw();

                        Dson darr = Dson.newArray();
                        for (int i = 0; i < nListArray.size(); i++) {
                            Dson dv = Dson.newObject();
                            dv.set("AgreementNo", nListArray.get(i).get("AgreementNo").asString());
                            dv.set("is_Daily", nListArray.get(i).get("is_Daily").asString());
                            dv.set("visit_time", nListArray.get(i).get("visit_time").asString());

                            darr.add(dv);
                        }
                        dson.set("data", darr);

                        Dson hdrs = getDefaultHeader();
                        res = InternetX.postHttpConnectionRaw(getBaseUrl("RPM/RPM_FlaggingRPK_Daily"), hdrs, dson);
                    }

                    public void runUI() {
                        Dson dson = Dson.readJson(res);
                        if (dson.get("ResponseCode").asString().equalsIgnoreCase("00")) {
                            setSetting("Monthly", nListArray.toJson());

                            setSetting("MONTH", Utility.Now().substring(0, 5));
                            finish();
                        } else {
                            showError(res);
                        }
                    }
                });
            }
        });*/

        /*checkApprove(this, new Runnable() {
            public void run() {
                reload();
            }
        }, null);
        reload(n);*/
        //Task.checkApprove(this,this, this);
    }

    public void run() {
        reload();
    }

    public static void checkApprove(final AppActivity appActivity, final Runnable finishUI, Runnable approved){
        if (appActivity.getSetting("approve").equalsIgnoreCase("true")){
            if (approved!=null) {
                approved.run();
            }
        }else{
            appActivity.newProcess(new Messagebox.DoubleRunnable() {
                String res;
                public void run() {
                    Dson dson = appActivity.getDefaultDataRaw();
                    Dson hdrs = appActivity.getDefaultHeader();
                    res = InternetX.postHttpConnectionRaw(getBaseUrl("RPM/RPM_ApproveRPK"), hdrs, dson);
                }

                public void runUI() {
                    Dson dson = Dson.readJson(res);
                    //if (dson.get("ResponseCode").asString().equalsIgnoreCase("00")) {
                    //AgreementNo
                    dson = dson.get("data");
                    Dson x = Dson.newObject();
                    for (int i = 0; i < dson.size(); i++) {
                        String dt = dson.get(i).get("is_rpk").asString()+ dson.get(i).get("is_ral").asString();//abaikan flag
                        x.set(dson.get(i).get("AgreementNo").asString(),dt);
                    }
                    dson = Dson.readJson(appActivity.getSetting("Monthly"));
                    for (int i = 0; i < dson.size(); i++) {
                        if (x.containsKey(dson.get(i).get("AgreementNo").asString())){
                            boolean b = false;
                            if (dson.get(i).get("_A").asInteger()==1){
                                b = true;
                            }else{
                                dson.get(i).set("_A", "1");
                            }
                            if (dson.get(i).get("is_rpk").asInteger()==1){
                                b = true;
                                dson.get(i).set("_A", "1");
                            }
                            if (dson.get(i).get("_B").asInteger()==1){
                                b = true;
                            }else{
                                dson.get(i).set("_B", "1");
                            }
                            if (dson.get(i).get("is_ral").asInteger()==1){
                                b = true;
                                dson.get(i).set("_B", "1");
                            }

                            if (b == false){
                                dson.get(i).set("_A", "1");//RANDOM
                            }

                        }else{
                            dson.get(i).set("_A", "");
                            dson.get(i).set("_B", "");
                        }
                    }
                    for (int i = 0; i < dson.size(); i++) {
                        if (x.containsKey(dson.get(i).get("AgreementNo").asString())){
                            if (x.get(dson.get(i).get("AgreementNo").asString()).asString().trim().equalsIgnoreCase("") ){
                                dson.get(i).set("_A", "");//ditolak
                                dson.get(i).set("_B", "");
                            }else{
                                if (dson.get(i).get("is_ral").asInteger()==1){
                                    //sudah diapprove
                                }else if (dson.get(i).get("is_rpk").asInteger()==1){
                                    //sudah diapprove
                                }else{
                                    dson.get(i).set("_A", "1");//tambahan dari atas
                                }
                            }
                        }
                    }
                    appActivity.setSetting("Monthly", dson.toJson());
                    appActivity.setSetting("approve", "true");//bypas
                    appActivity.showInfo("RPK Sudah diapproved");
                    if (finishUI!=null) {
                        finishUI.run();
                    }
                    /*} else {
                        appActivity.showInfoDialog("RPK Belum diapproved", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                appActivity.finish();
                            }
                        });
                    }*/
                }
            });
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 12 && resultCode == RESULT_OK) {
            //Dson n = Dson.readJson(getIntentStringExtra(data,"nfilter"));
            //Dson n = Dson.readJson(getIntent().getStringExtra("nfilter"));
            //getIntentStringExtra(data,"nfilter")
            //Dson n = Dson.readJson(getSetting("Monthly"));
            //GoToURL("http://api.whatsapp.com/send?phone=" + "6287886722267" + "&text=" + getIntentStringExtra(data, "nfilter"));
            //showInfo(getIntent().getStringExtra("filter"));
            try {
                performFilter(getIntentStringExtra(data, "nfilter"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void performFilter(String filter) throws ParseException {
        nListArray.asArray().clear();
        final Dson nfilter = Dson.readJson(filter);
        Dson n = Dson.readJson(getSetting("Monthly"));
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
            if (n.get(i).get("_A").asString().equalsIgnoreCase("1") ||
                    n.get(i).get("_B").asString().equalsIgnoreCase("1")) {
                if (n.get(i).get("AssetType").asString().contains(nfilter.get("kendaraan").asString())
                        && isWithinRangeOs(n.get(i).get("OSPrincipalAmount").asInteger(), os0, os1)
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
        }

        RecyclerView recyclerView = findViewById(R.id.rview);
        recyclerView.getAdapter().notifyDataSetChanged();

    }
    public void reload() {
        Dson n = Dson.readJson(getSetting("Monthly"));
        reload(n);
    }
    public void reload(Dson n) {
        nListArray.asArray().clear();
        for (int i = 0; i < n.size(); i++) {
            if (n.get(i).get("_A").asString().equalsIgnoreCase("1")
                    || n.get(i).get("_B").asString().equalsIgnoreCase("1")) {
                nListArray.add(n.get(i));
            }
        }
        RecyclerView recyclerView = findViewById(R.id.rview);
        recyclerView.getAdapter().notifyDataSetChanged();
    }
    public void performSearch() {
        nListArray.asArray().clear();
        String txt = find(R.id.txtSearch, EditText.class).getText().toString().toLowerCase();
        Dson n = Dson.readJson(getSetting("Monthly"));
        for (int i = 0; i < n.size(); i++) {
            if (n.get(i).get("_A").asString().equalsIgnoreCase("1")
                    || n.get(i).get("_B").asString().equalsIgnoreCase("1")) {
                if (n.get(i).get("CustomerFullName").asString().toLowerCase().contains(txt) ||
                        n.get(i).get("LicensePlate").asString().toLowerCase().contains(txt) ||
                        n.get(i).get("AgreementNo").asString().toLowerCase().contains(txt)) {
                    nListArray.add(n.get(i));
                }
            }
        }
        RecyclerView recyclerView = findViewById(R.id.rview);
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    private void invalidateX() {
        Dson dataH = Dson.newObject();
        Dson dson = Dson.readDson(getSetting("Personal"));

        int quota = dson.get("Monthly_RPK").asInteger();
        double osp = dson.get("Monthly_Recovery_OSP").asDouble();
        int take = 0;
        for (int i = 0; i < nListArray.size(); i++) {
            Dson n = nListArray;
            if (n.get(i).get("_C").asString().equalsIgnoreCase("1")) {
                osp = osp - nListArray.get(i).get("OSPrincipalAmount").asDouble();
                take++;
            }
        }

        //n.get(i).get("_A").asString().equalsIgnoreCase("1") || n.get(i).get("_B").asString().equalsIgnoreCase("1")


        Dson n = Dson.readJson(getSetting("Monthly"));
        int visit = 0;
        for (int i = 0; i < n.size(); i++) {
            if (n.get(i).get("is_visited").asString().equalsIgnoreCase("1")) {
                visit++;
            }
        }
       /* dataH.set("wo",n.size());
        dataH.set("quota",quota);
        dataH.set("take",take);
        dataH.set("os", nListArray.size()-take);*/
        //Integer dailyRPK = Integer.parseInt(getSetting("dailyRPK"))+Integer.parseInt(getSetting("dailyRPKadd"));

        findView(R.id.h_bahan, R.id.txtMessage, TextView.class).setText(getSetting("dailyRPK"));//Kuota Monthly_RPK
        findView(R.id.h_take, R.id.txtMessage, TextView.class).setText(String.valueOf(visit));//visited
        findView(R.id.h_add, R.id.txtMessage, TextView.class).setText(String.valueOf(take));//Take In
        findView(R.id.h_os, R.id.txtMessage, TextView.class).setText(String.valueOf(dataH.get("os").asInteger()));//OS

        setSetting("dataH", dataH.toJson());
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
