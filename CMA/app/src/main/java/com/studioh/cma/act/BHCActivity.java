package com.studioh.cma.act;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.naa.data.Dson;
import com.naa.data.Utility;
import com.naa.data.UtilityAndroid;
import com.naa.utils.Expression;
import com.naa.utils.Messagebox;
import com.studioh.cma.AppActivity;
import com.studioh.cma.R;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

public class BHCActivity extends AppActivity {
    LinearLayout content ;
    public static boolean existBHC(Dson nbhc){
        return false;
    }
    private String getDefault(Dson data, String s){
        if (s.startsWith("@")){
            s = s.substring(1);
            if (data.containsKey(s)){
                return data.get(s).asString();
            }
            return "";
        }
        return s;
    }
    public static String getStatusBHC(String statusbayar){
        /*if (statusbayar.equals("KUNJUNGAN")) {
            statusbayar =("Gagal_Bertemu");
        }else if (statusbayar.equals("BAYAR")) {
            statusbayar = ("Bayar");
        }else if (statusbayar.equals("BELUM")) {
            statusbayar =("JB");//Janji Bayar
        }else{
            //sama
        }*/
        //statusbayar =("Gagal_Bayar");//bypass
        return statusbayar;
    }
    public static String getTglKirimBHC(String tanggalkirim){
        /*if (statusbayar.equals("KUNJUNGAN")) {
            statusbayar =("Gagal_Bertemu");
        }else if (statusbayar.equals("BAYAR")) {
            statusbayar = ("Bayar");
        }else if (statusbayar.equals("BELUM")) {
            statusbayar =("JB");//Janji Bayar
        }else{
            //sama
        }*/
        //statusbayar =("Gagal_Bayar");//bypass
        if (tanggalkirim!=null){
            if (tanggalkirim.length()>=10){
                //yyyy-mm-dd
                return tanggalkirim.substring(0,10);
            }
        }else{
            return "0000-00-00";
        }
        return tanggalkirim;
    }
    private void reqNexFocus(View vc){
        int icurr = 0;
        if (content==null){
            return;
        }
        for (int i = 0; i < content.getChildCount(); i++) {
            View v = content.getChildAt(i);
            if (v == vc){
                icurr = i+1;
            }
        }
        for (int i = icurr; i < content.getChildCount(); i++) {
            View v = content.getChildAt(i);
            TextView textView = v.findViewById(R.id.lblLabel);
            EditText editText = v.findViewById(R.id.txtText);
            String type =  String.valueOf(editText.getTag());


            if (v.getVisibility()==View.VISIBLE && editText.isEnabled()){
                if (type.equalsIgnoreCase("combo")) {
                    v.findViewById(R.id.spnText).requestFocus();
                }else if (type.equalsIgnoreCase("date")){
                   v.findViewById(R.id.datText).requestFocus();
                }else if (type.equalsIgnoreCase("view")){
                    //none
                    continue;
                }else if (type.equalsIgnoreCase("title")){
                    //none
                    continue;
                }else if (type.equalsIgnoreCase("button")){
                    v.findViewById(R.id.tblText).requestFocus();
                }else{
                    editText.requestFocus();
                }
                break;
            }
        }
    }
    private boolean exNow = false;

    private void getReport(final Runnable runnable){
        newProcess(new Messagebox.DoubleRunnable() {
            String res;
            public void run() {
                Dson dson = getDefaultDataRaw();
                dson.set("LicensePlate","ALL");
                res =  postHttpRaw("RPM/RPM_GetDataReport", dson );
                autoToken(res);
            }

            public void runUI() {
                Dson dson ;
                //production
                /*if (dson.get("ResponseCode").asString().equalsIgnoreCase("00")) {
                     setSetting("REPORT", dson.get("data").toJson());
                     runnable.run();

                }else{
                    showError(dson.get("error").asString());
                }*/
                //demo start
                AssetManager assetManager = getAssets();
                try {
                    InputStream input = assetManager.open("report.json");
                    dson = Dson.readJson(isToString(input));
                    setSetting("REPORT", dson.get("data").toJson());

                } catch (IOException e) {
                    e.printStackTrace();
                }
                runnable.run();
                //demo end


            }
        });
    }
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bhcgenerator);

      /*  Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }*/
        setTitle("Collection Activity Report");
        getReport(new Runnable() {
            @Override
            public void run() {
                Dson dson = Dson.newArray();
                dson = Dson.readJson(getSetting("REPORT"));
                String statusbayar = getStatusBHC(getIntentStringExtra("statusbayar").trim()) ;
                Dson dataa = Dson.readJson(getIntentStringExtra("order"));
                TextView textView = null;

                content = findViewById(R.id.lnrContent);
                //Toast.makeText(this,dson.toJson(),Toast.LENGTH_SHORT).show();
                for (int i = 0; i < dson.size(); i++) {
                    if (dson.get(i).get("Field_id").asString().trim().equalsIgnoreCase(statusbayar+getIntentStringExtra("index").trim())){
                        String type =  dson.get(i).get("Type_Tanya").asString().trim();
                        String text =  getDefault (dataa, dson.get(i).get("Isian").asString().trim());//default
                        String combo = dson.get(i).get("Tanya").asString().trim();
                        String label = dson.get(i).get("Tanya_detail").asString().trim();
                        String eval =  dson.get(i).get("Terlihat").asString().trim();
                        String name =  dson.get(i).get("Name_id").asString().trim();
                        String expr =  dson.get(i).toJson();

                        View v = null;
                        if (type.equalsIgnoreCase("combo")){
                            v = UtilityAndroid.inflate(getActivity(), R.layout.bhc_item_combo);
                            Vector array = splitVectorTrim(combo, ",");
                            Spinner spinner =  v.findViewById(R.id.spnText);
                            if (text.trim().equalsIgnoreCase("")){
                                array.insertElementAt("",0);
                            }

                            ArrayAdapter aa = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, array) {
                                 public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                                     View v =super.getDropDownView(position, convertView, parent);

                                     if (position == 0){
                                        /* v = new TextView(getActivity());
                                         v.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
                                         ((TextView)v).setText("WAKAKA");*/
                                        //v.setVisibility(View.GONE);
                                     }
                                     return v;
                                }
                                public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                                    /* View v = super.getView(position, convertView, parent);
                                     if (position == 0){
                                         //v = new TextView(getActivity());
                                         //v.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
                                         ((TextView)v).setText("WAKAKA");
                                     }*/
                                    return super.getView(position, convertView, parent);
                                }
                            };
                            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinner.setAdapter(aa);
                            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                private View v;
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    validateVisible();
                                    if (exNow) {
                                        reqNexFocus(v);
                                    }
                                }
                                public void onNothingSelected(AdapterView<?> parent) {}
                                public AdapterView.OnItemSelectedListener get(View v ){
                                    this.v=v;
                                    return  this;
                                }
                            }.get(v));



                        }else if (type.equalsIgnoreCase("text")){
                            v = UtilityAndroid.inflate(getActivity(), R.layout.bhc_item_text);
                            EditText editText = v.findViewById(R.id.txtText);
                            editText.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
                        }else if (type.equalsIgnoreCase("area")){
                            v = UtilityAndroid.inflate(getActivity(), R.layout.bhc_item_area);
                            EditText editText = v.findViewById(R.id.txtText);
                            editText.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
                        }else if (type.equalsIgnoreCase("phone")){
                            v = UtilityAndroid.inflate(getActivity(), R.layout.bhc_item_phone);
                        }else if (type.equalsIgnoreCase("number")){
                            v = UtilityAndroid.inflate(getActivity(), R.layout.bhc_item_number);
                        }else if (type.equalsIgnoreCase("view")){
                            v = UtilityAndroid.inflate(getActivity(), R.layout.bhc_item_view);
                        }else if (type.equalsIgnoreCase("title")){
                            v = UtilityAndroid.inflate(getActivity(), R.layout.bhc_item_title);
                        }else if (type.equalsIgnoreCase("button")){
                            v = UtilityAndroid.inflate(getActivity(), R.layout.bhc_item_button);
                            v.findViewById(R.id.tblText).setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                   /* String text = ((Button)v).getText().toString();
                                    //save
                                    if (text.equalsIgnoreCase("save")||text.equalsIgnoreCase("simpan")){
                                        save();
                                    }
                                    //lanjut
                                    if (text.equalsIgnoreCase("lanjut")||text.equalsIgnoreCase("next")){
                                        lanjut();
                                    }*/
                                    if (String.valueOf(find(R.id.selfie, ImageView.class).getTag()).equals("null")){
                                        /*find(R.id.selfietxt, TextView.class).setError("Foto Diperlukan!");*/
                                        showInfoDialog("Foto Diperlukan!", null);
                                    }else {
                                        save(String.valueOf(find(R.id.selfie, ImageView.class).getTag()));
                                    }
                                }
                            });
                            ((Button)v.findViewById(R.id.tblText)).setText("SAVE");//combo
                        }else if (type.equalsIgnoreCase("date")){
                            v = UtilityAndroid.inflate(getActivity(), R.layout.bhc_item_date);
                            //file hire
                            DatePicker datePicker = ((DatePicker)v.findViewById(R.id.datText));
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                            Date date = null;
                            try {
                                date = sdf.parse(text);
                            } catch (Exception e) {
                                date = new Date();
                            }

                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(date);

                            int year = calendar.get(Calendar.YEAR);
                            int month = calendar.get(Calendar.MONTH);
                            int day = calendar.get(Calendar.DAY_OF_MONTH);

                            datePicker.updateDate(year, month, day);

                            long now = System.currentTimeMillis() - 1000;
                            datePicker.setMinDate(now + (1000 * 60 * 60 * 24 * 0));
                            datePicker.setMaxDate(now + (1000 * 60 * 60 * 24 * 4));


                        }else{
                            v = UtilityAndroid.inflate(getActivity(), R.layout.bhc_item_text);
                        }

                        textView = v.findViewById(R.id.lblLabel);
                        textView.setText(label);
                        if (label.equalsIgnoreCase("")){
                            textView.setVisibility(View.GONE);

                        }
                        textView.setTag(name);

                        if ( v.findViewById(R.id.txtText)instanceof EditText){
                            EditText editText = v.findViewById(R.id.txtText);
                            editText.setText(text);
                            editText.setTag(type);
                        }
                        v.findViewById(R.id.frmExpression).setTag(expr);
                        //v.setTag(eval);

                        View card = UtilityAndroid.inflate(getActivity(), R.layout.bhc_item_card);
                        findView(card, R.id.lnrContent, LinearLayout.class).addView(v);
                        card.setTag(eval);
                        content.addView(card);
                    }
                }
                validateVisible();
                showCamera(REQUEST_SELFIE);
            }
        });

        findViewById(R.id.selfie).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCamera(REQUEST_SELFIE);
            }
        });

        /*findViewById(R.id.tblSimpan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                save();

            }
        });*/

        /*new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }
        }).start();*/
        findViewById(R.id.tblSimpan).postDelayed(new Runnable() {
            @Override
            public void run() {
                reqNexFocus(findViewById(R.id.tblSimpan));
            }
        },300);

    }

    @Override
    protected void onResume() {
        super.onResume();
        exNow = true;
    }

    @Override
    protected void onStart() {
        super.onStart();

    }
    public static Vector<String> splitVectorTrim(String original, String separator) {
        Vector<String> nodes = new Vector<String>();
        int index = original.indexOf(separator);
        while (index >= 0) {
            nodes.addElement(original.substring(0, index).trim());
            original = original.substring(index + separator.length());
            index = original.indexOf(separator);
        }
        nodes.addElement(original.trim());
        return nodes;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==12345 && resultCode==RESULT_OK){
            finish();
        }else if (requestCode == REQUEST_SELFIE && resultCode == RESULT_OK){
            //getCamera("REQUEST_SELIE", data, find(R.id.selfie, ImageView.class));
            //file.set("RAL", String.valueOf(find(R.id.imgRal, ImageView.class).getTag()));
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            find(R.id.selfie, ImageView.class).setImageBitmap(photo);
        }
    }


    /*
    protected void onActivityResult(int requestCode, int resultCode,   Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SELIE && resultCode == RESULT_OK){
            getCamera("REQUEST_RAL", data, find(R.id.imgRal, ImageView.class));
        }
    }
    * */
    public static void resetBHC(Activity activity){
        Dson nbhc = Dson.readDson(UtilityAndroid.getSetting(activity, "BHC","{}"));
        /*if (nbhc.isDsonObject()){
            if (nbhc.get("date").asString().equalsIgnoreCase(com.naa.data.Utility.Now().substring(0,10))){
            }else{
                //reset
                //**********************************************BHC***********************************
                nbhc.set("date", com.naa.data.Utility.Now().substring(0,10));
                nbhc.set("data", Dson.newObject());
                UtilityAndroid.setSetting(activity, "BHC",nbhc.toJson());
            }
        }*/
        Dson key = nbhc.get("data").getObjectKeys();

        if (nbhc.get("version").asInteger()<=1){
            //reset
            UtilityAndroid.setSetting(activity, "BHC","");
        }else{
            for (int i = 0; i < key.size(); i++) {
                if (key.get(i).asString().length()>=10){
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = null;
                    try {
                        date = sdf.parse(key.get(i).asString().substring(0,10));
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(new Date());
                        calendar.add(Calendar.DAY_OF_MONTH, -5);

                        if (date.getTime()<=calendar.getTimeInMillis()){
                            //delete
                            nbhc.get("data").remove(key.get(i).asString());
                        }
                    } catch (ParseException e) {
                        //delete
                        nbhc.get("data").remove(key.get(i).asString());
                    }
                }//end date 10
            }//end if

            if (key.size()!=nbhc.get("data").getObjectKeys().size()){
                UtilityAndroid.setSetting(activity, "BHC",nbhc.toJson());
            }
        }
    }


    public static void startBHC(Activity activity, Dson myOrder, int req){
        Intent intent =  new Intent(activity, BHCActivity.class);
       /* intent.putExtra("nopsb", myOrder.getNoPsb());
        intent.putExtra("nama", myOrder.getNama());
        intent.putExtra("statusbayar", myOrder.getStatusBayar());
        intent.putExtra("order",new Gson().toJson(myOrder, OrderItem.class));
        intent.putExtra("kode_ao", myOrder.getKodeAo());
        intent.putExtra("ke_awal", myOrder.getKeAwal());
        intent.putExtra("ke_akhir", myOrder.getKeAkhir());
        intent.putExtra("posko", myOrder.getPosko());
        intent.putExtra("orderX", (Parcelable) myOrder);
        intent.putExtra("tgl_update", com.icollection.util.Utility.Now().substring(0,10));
        intent.putExtra("jam_update",  com.icollection.util.Utility.Now().substring(11).trim());
        intent.putExtra("tgl_kirim", myOrder.getData1());
        intent.putExtra("jam_kirim", "");
        intent.putExtra("id", myOrder.getId());*/
        if (req == -1 ){
            activity.startActivity(intent);
        }else{
            activity.startActivityForResult(intent, req);
        }
    }
    private void lanjut(){
        if (validateMandatory()){
            Intent intent =  new Intent(BHCActivity.this, BHCActivity.class);
            intent.putExtra("id",           getIntentStringExtra("id"));

            intent.putExtra("statusbayar",  getIntentStringExtra("statusbayar"));


            intent.putExtra("index", getIntentStringExtra("index") +"2");
            intent.putExtra("stream",       getData().toJson());
            startActivityForResult(intent,12345);
        }else{
            showInfoDialog("Semua pertanyaan harus diisi", null);
        }

    }

    private void save(final String tag){
        String statusbayar = getStatusBHC(getIntentStringExtra("statusbayar").trim()) ;

        if (!validateMandatory())
        {
            showInfoDialog("Semua pertanyaan harus diisi", null);
        }else{

            newProcess(new Messagebox.DoubleRunnable() {
                String res;
                String result;
                public void run() {
                    Dson dson = getDefaultDataRaw();
                    dson.set("data", getData());
                    dson.set("AgreementNo", getSetting("AgreementNo"));

                    Dson file = Dson.newObject();
                    file.set("Photo", tag);

                    res = //dson.toJson();
                            postHttpRaw("RPM/RPM_AddReport", dson);
                    autoToken(res);
                    result = postHttpRaw("RPM/RPM_AddReportPhoto", dson, file);
                    autoToken(result);
                }

                public void runUI() {
                    final Dson dson = Dson.readJson(res);
                    if (dson.get("ResponseCode").asString().equalsIgnoreCase("00")) {
                        AddDataLog("Save Report", dson.get("ResponseDescription").asString());
                        showInfoDialog(dson.get("ResponseDescription").asString(), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                AddDataLog("Save Report", dson.get("ResponseDescription").asString());
                                dialog.dismiss();
                                setResult(Activity.RESULT_OK);
                                finish();
                            }
                        });
                    } else if (dson.get("ResponseCode").asString().equalsIgnoreCase("99")) {
                        AddDataLog("Save Report", dson.get("ResponseDescription").asString());
                        showInfo(dson.get("ResponseDescription").asString());
                    } else {
                        //change header
                        //add to response code 00
                        AddDataLog("Save Report", res);
                        showInfo(res);
                        //mailMe("noe.ttr@gmail.com", res);
                    }
                }
            });
        }
    }

    public void showInfoDialog(String message, DialogInterface.OnClickListener onClickListener){
        if (onClickListener == null){
            onClickListener = onClickListenerDismiss;
        }
        Messagebox.showDialog(this, "", message, "OK", "", onClickListener, null);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home ) {

            onBackPressed();
        }
        return true;
    }

    private final DialogInterface.OnClickListener onClickListenerDismiss = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
        }
    };
    private void validateVisible(){
        Expression expression = new Expression();
        final Dson dson = Dson.newObject();
        //Dson data = Dson.readJson(getIntentStringExtra("order"));
        //prepare data
        for (int i = 0; i < content.getChildCount(); i++) {
            View v = content.getChildAt(i);
            TextView textView = v.findViewById(R.id.lblLabel);
            EditText editText = v.findViewById(R.id.txtText);
            String type =  String.valueOf(editText.getTag());
            String kname = "$"+String.valueOf(textView.getTag());
            String value = "";


            if (type.equalsIgnoreCase("combo")) {
                Spinner spinner = v.findViewById(R.id.spnText);
                value =  String.valueOf(spinner.getSelectedItem()).trim();

            }else if (type.equalsIgnoreCase("date")){
                String[] arrMonth = { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12" };
                int mYear =  ((DatePicker)v.findViewById(R.id.datText)).getYear();
                int mMonth = ((DatePicker)v.findViewById(R.id.datText)).getMonth();
                int mDay =   ((DatePicker)v.findViewById(R.id.datText)).getDayOfMonth();

                value = mYear  + "-" + arrMonth[mMonth] + "-" + LPad(mDay + "", "0", 2) ;
            }else{
                value = editText.getText().toString();
            }
            if (v.getVisibility()==View.VISIBLE){
                dson.set(kname.toLowerCase().trim(), value);
                Log.i("Exps",kname.toLowerCase().trim() +":"+value);
            }else{
                dson.set(kname.toLowerCase().trim(), value);
                Log.i("Expsx",kname.toLowerCase().trim() +":"+value);
            }


        }

        //evaluate
        for (int i = 0; i < content.getChildCount(); i++) {
            View v = content.getChildAt(i);
            String eval = String.valueOf(v.getTag());
            //evaluate expression
            Log.i("Exp",eval);
            boolean visible = false;
            if (eval.equalsIgnoreCase("")||eval.equalsIgnoreCase("Ya")||eval.equalsIgnoreCase("null")||eval.equalsIgnoreCase("true")){
                visible = true;
                Log.i("Exp0",String.valueOf(visible));
            }else{
                try {
                    Object out =  expression.evaluate(eval, new Expression.OnVariableListener() {
                        public Object get(String name) {
                            Log.i("Expg", name+":"+dson.get(name.toLowerCase().trim()));
                            return dson.get(name.toLowerCase().trim());
                        }
                        public void set(String name, Object value) {  }
                    });
                    Log.i("Exp1",String.valueOf(out));
                    visible = String.valueOf(out).equalsIgnoreCase("true");
                    Log.i("Exp2",String.valueOf(String.valueOf(out).equalsIgnoreCase("true")));
                }catch (Exception e){}
            }

            if (visible){
                v.setVisibility(View.VISIBLE);
            }else{
                v.setVisibility(View.GONE);//Gone
            }
        }
    }
    private boolean validateMandatory(){
        for (int i = 0; i < content.getChildCount(); i++) {
            Dson object = Dson.newObject();
            View v = content.getChildAt(i);
            TextView textView = v.findViewById(R.id.lblLabel);
            EditText editText = v.findViewById(R.id.txtText);
            String type =  String.valueOf(editText.getTag());
            if (v.getVisibility()==View.VISIBLE && editText.isEnabled()){
                if (type.equalsIgnoreCase("combo")){
                    Spinner spinner =  v.findViewById(R.id.spnText);
                    String text = String.valueOf(spinner.getSelectedItem()).trim() ;
                    if ( text.equalsIgnoreCase("")||text.equalsIgnoreCase("null")  ){
                        return false;
                    }
                }else if (type.equalsIgnoreCase("date")){
                    String[] arrMonth = { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12" };
                    int mYear =  ((DatePicker)v.findViewById(R.id.datText)).getYear();
                    int mMonth = ((DatePicker)v.findViewById(R.id.datText)).getMonth();
                    int mDay =   ((DatePicker)v.findViewById(R.id.datText)).getDayOfMonth();

                    String sdate = mYear  + "-" + arrMonth[mMonth] + "-" + LPad(mDay + "", "0", 2) ;
                    String text = sdate;
                    if ( text.equalsIgnoreCase("")||text.equalsIgnoreCase("null")  ){
                        return false;
                    }
                }else{
                    String text = editText.getText().toString();
                    if ( text.equalsIgnoreCase("")||text.equalsIgnoreCase("null")  ){
                        return false;
                    }
                }
            }

        }
        return true;
    }
    private Dson getData(){
        Dson dson = Dson.newArray();
        for (int i = 0; i < content.getChildCount(); i++) {
            Dson object = Dson.newObject();
            View v = content.getChildAt(i);
            TextView textView = v.findViewById(R.id.lblLabel);
            EditText editText = v.findViewById(R.id.txtText);
            String type =  String.valueOf(editText.getTag());
            Dson param = Dson.readJson(String.valueOf( v.findViewById(R.id.frmExpression).getTag()));
            if (param.isDsonObject()){
                object.asObject().putAll(param.asObject());
            }

            //String.valueOf(textView.getText())
            if (type.equalsIgnoreCase("combo")){
                Spinner spinner =  v.findViewById(R.id.spnText);
                object.set("Hasil", String.valueOf(spinner.getSelectedItem()).trim());
            }else if (type.equalsIgnoreCase("date")){
                String[] arrMonth = { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12" };
                int mYear =  ((DatePicker)v.findViewById(R.id.datText)).getYear();
                int mMonth = ((DatePicker)v.findViewById(R.id.datText)).getMonth();
                int mDay =   ((DatePicker)v.findViewById(R.id.datText)).getDayOfMonth();

                String sdate = mYear  + "-" + arrMonth[mMonth] + "-" + LPad(mDay + "", "0", 2) ;
                object.set("Hasil", sdate);
            }else{
                object.set("Hasil", editText.getText().toString());
            }
            String seq = object.get("seq").asString();
            object.remove("seq");
            object.set("Seq", seq);
            dson.add(object);
        }
        Log.i("Exp_da", dson.toJson());


        return dson;
    }

    public String getIntentStringExtra(String key) {
        return getIntentStringExtra(getIntent(), key);
    }
    public String getIntentStringExtra(Intent intent, String key) {
        if (intent!=null && intent.getStringExtra(key)!=null){
            return intent.getStringExtra(key);
        }
        return "";
    }

    public static String LPad(String schar, String spad, int len) {
        String sret = schar;
        for (int i = sret.length(); i < len; i++) {
            sret = spad + sret;
        }
        return new String(sret);
    }
    final int REQUEST_SELFIE    = 204;


}
