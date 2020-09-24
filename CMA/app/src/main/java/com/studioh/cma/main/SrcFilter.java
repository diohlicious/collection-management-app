package com.studioh.cma.main;

import android.content.Context;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.naa.data.Dson;
import com.studioh.cma.AppActivity;
import com.studioh.cma.R;
import com.studioh.srv.StudiohAutoComplete;
import com.studioh.srv.DsonAutoCompleteAdapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SrcFilter extends AppActivity {
    private GoogleMap mMap;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_filter);
        setTitle("Filter Pencarian");

        final Calendar newCalendar = Calendar.getInstance();
        final SimpleDateFormat ydt = new SimpleDateFormat("yyyy", Locale.US);
        final String[] kendaraan = new String[1];
        final String[] os = new String[1];
        final Intent intent = getIntent();
        final String res = intent.getStringExtra("filter");
        final Dson skec = Dson.newArray();
        final Dson skel = Dson.newArray();
        final Dson nfilter = Dson.newObject();
        //GoToURL("http://api.whatsapp.com/send?phone=" + "6287886722267" + "&text=" + res);

        StudiohAutoComplete kotaTitle = (StudiohAutoComplete) findViewById(R.id.kotaInput);
        kotaTitle.setThreshold(2);
        kotaTitle.setAdapter(new DsonAutoCompleteAdapter(this) {
            public Dson onFindDson(Context context, String kotaTitle) {

                return distinct(Dson.readJson(res), "kota");
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = inflater.inflate(R.layout.find_autocomplete, parent, false);
                }
                findView(convertView, R.id.txtText, TextView.class).setText(getItem(position).get("kota").asString());
                return convertView;
            }

        }); // 'this' is Activity instance
        kotaTitle.setLoadingIndicator((android.widget.ProgressBar) findViewById(R.id.pb_txt));
        kotaTitle.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Dson n = Dson.readJson(String.valueOf(adapterView.getItemAtPosition(position)));
                find(R.id.kotaInput, StudiohAutoComplete.class).setText((n.get("kota").asString()));
                find(R.id.section1, LinearLayout.class).setVisibility(View.VISIBLE);
                Dson nkec = Dson.readJson(res);
                nfilter.set("kota",n.get("kota").asString());
                for (int i = 0; i < nkec.get(i).size(); i++) {
                    if (nkec.get(i).get("kota").asString().equalsIgnoreCase(
                            //find(R.id.kotaInput, StudiohAutoComplete.class).getText().toString()
                            n.get("kota").asString()
                    )){
                        skec.add(nkec.get(i));
                    }
                }
                //GoToURL("http://api.whatsapp.com/send?phone=" + "6287886722267" + "&text=" + skec);
            }
        });

        StudiohAutoComplete kecTitle = (StudiohAutoComplete) findViewById(R.id.kecamatanInput);
        kecTitle.setThreshold(2);
        kecTitle.setAdapter(new DsonAutoCompleteAdapter(this) {
            public Dson onFindDson(Context context, String kecTitle) {
                return distinct(skec, "kecamatan");
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = inflater.inflate(R.layout.find_autocomplete, parent, false);
                }
                findView(convertView, R.id.txtText, TextView.class).setText(getItem(position).get("kecamatan").asString());
                return convertView;
            }

        }); // 'this' is Activity instance
        kecTitle.setLoadingIndicator((android.widget.ProgressBar) findViewById(R.id.pb_txt1));
        kecTitle.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Dson n = Dson.readJson(String.valueOf(adapterView.getItemAtPosition(position)));
                find(R.id.kecamatanInput, StudiohAutoComplete.class).setText((n.get("kecamatan").asString()));
                find(R.id.section2, LinearLayout.class).setVisibility(View.VISIBLE);
                nfilter.set("kecamatan",n.get("kecamatan").asString());
                Dson nkel = Dson.readJson(res);
                for (int i = 0; i < nkel.get(i).size(); i++) {
                    if (nkel.get(i).get("kota").asString().equalsIgnoreCase(
                            n.get("kota").asString()) &&
                            nkel.get(i).get("kecamatan").asString().equalsIgnoreCase(
                                    n.get("kecamatan").asString()
                            )
                    ){
                        skel.add(nkel.get(i));
                    }
                }

            }
        });

        StudiohAutoComplete kelTitle = (StudiohAutoComplete) findViewById(R.id.kelurahanInput);
        kelTitle.setThreshold(2);
        kelTitle.setAdapter(new DsonAutoCompleteAdapter(this) {
            public Dson onFindDson(Context context, String kelTitle) {
                //String res = intent.getStringExtra("filter");
                return distinct(skel, "kelurahan");
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = inflater.inflate(R.layout.find_autocomplete, parent, false);
                }
                findView(convertView, R.id.txtText, TextView.class).setText(getItem(position).get("kelurahan").asString());
                return convertView;
            }

        }); // 'this' is Activity instance
        kelTitle.setLoadingIndicator((android.widget.ProgressBar) findViewById(R.id.pb_txt2));
        kelTitle.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Dson n = Dson.readJson(String.valueOf(adapterView.getItemAtPosition(position)));
                find(R.id.kelurahanInput, StudiohAutoComplete.class).setText((n.get("kelurahan").asString()));
                nfilter.set("kelurahan",n.get("kelurahan").asString());

            }
        });


        find(R.id.motor, CheckBox.class).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    if ( buttonView.getParent() instanceof ViewGroup){
                        find(R.id.mobil, CheckBox.class).setChecked(false);
                        find(R.id.os, LinearLayout.class).setVisibility(View.VISIBLE);
                        find(R.id.os1, CheckBox.class).setText("<5.000.000");
                        find(R.id.os2, CheckBox.class).setText("5.000.000 - 9.999.999");
                        find(R.id.os3, CheckBox.class).setText("10.000.000 - 20.000.000");
                        find(R.id.os4, CheckBox.class).setText(">20.000.000");
                        //kendaraan[0] = "motor";
                        nfilter.set("kendaraan","MOTOR");
                    }
                }
            }
        });
        find(R.id.mobil, CheckBox.class).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    if ( buttonView.getParent() instanceof ViewGroup){
                        find(R.id.motor, CheckBox.class).setChecked(false);
                        find(R.id.os, LinearLayout.class).setVisibility(View.VISIBLE);
                        find(R.id.os1, CheckBox.class).setText("<50.000.000");
                        find(R.id.os2, CheckBox.class).setText("50.000.000 - 99.999.999");
                        find(R.id.os3, CheckBox.class).setText("100.000.000 - 200.000.000");
                        find(R.id.os4, CheckBox.class).setText(">200.000.000");
                        //kendaraan[0] = "mobil";
                        nfilter.set("kendaraan","MOBIL");
                    }
                }
            }
        });
        //_________________________________________________________________________________________________________
        find(R.id.os1, CheckBox.class).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    if ( buttonView.getParent() instanceof ViewGroup){
                        find(R.id.os2, CheckBox.class).setChecked(false);
                        find(R.id.os3, CheckBox.class).setChecked(false);
                        find(R.id.os4, CheckBox.class).setChecked(false);
                        //os[0] = find(R.id.os1, CheckBox.class).getText().toString();
                        if(nfilter.get("kendaraan").asString().equalsIgnoreCase("MOTOR")){
                            nfilter.set("os0","0");
                            nfilter.set("os1","4999999");
                        }else if (nfilter.get("kendaraan").asString().equalsIgnoreCase("MOBIL")){
                            nfilter.set("os0","0");
                            nfilter.set("os1","49999999");
                        }
                    }
                }
            }
        });
        find(R.id.os2, CheckBox.class).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    if ( buttonView.getParent() instanceof ViewGroup){
                        find(R.id.os1, CheckBox.class).setChecked(false);
                        find(R.id.os3, CheckBox.class).setChecked(false);
                        find(R.id.os4, CheckBox.class).setChecked(false);
                        //os[0] = find(R.id.os2, CheckBox.class).getText().toString();
                        //nfilter.set("os","2");
                        if(nfilter.get("kendaraan").asString().equalsIgnoreCase("MOTOR")){
                            nfilter.set("os0","5000000");
                            nfilter.set("os1","9999999");
                        }else if (nfilter.get("kendaraan").asString().equalsIgnoreCase("MOBIL")){
                            nfilter.set("os0","5000000");
                            nfilter.set("os1","99999999");
                        }
                    }
                }
            }
        });
        find(R.id.os3, CheckBox.class).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    if ( buttonView.getParent() instanceof ViewGroup){
                        find(R.id.os2, CheckBox.class).setChecked(false);
                        find(R.id.os1, CheckBox.class).setChecked(false);
                        find(R.id.os4, CheckBox.class).setChecked(false);
                        //os[0] = find(R.id.os3, CheckBox.class).getText().toString();
                        //nfilter.set("os","3");
                        if(nfilter.get("kendaraan").asString().equalsIgnoreCase("MOTOR")){
                            nfilter.set("os0","10000000");
                            nfilter.set("os1","20000000");
                        }else if (nfilter.get("kendaraan").asString().equalsIgnoreCase("MOBIL")){
                            nfilter.set("os0","100000000");
                            nfilter.set("os1","200000000");
                        }
                    }
                }
            }
        });
        find(R.id.os4, CheckBox.class).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    if ( buttonView.getParent() instanceof ViewGroup){
                        find(R.id.os2, CheckBox.class).setChecked(false);
                        find(R.id.os3, CheckBox.class).setChecked(false);
                        find(R.id.os1, CheckBox.class).setChecked(false);
                        //os[0] = find(R.id.os4, CheckBox.class).getText().toString();
                        //nfilter.set("os","4");
                        if(nfilter.get("kendaraan").asString().equalsIgnoreCase("MOTOR")){
                            nfilter.set("os0","20000000");
                            nfilter.set("os1","999999999");
                        }else if (nfilter.get("kendaraan").asString().equalsIgnoreCase("MOBIL")){
                            nfilter.set("os0","200000000");
                            nfilter.set("os1","999999999");
                        }
                    }
                }
            }
        });


        final DatePickerDialog EndTime = new DatePickerDialog(this, android.R.style.Theme_Holo_Dialog, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                find(R.id.yearEndInput, EditText.class).setText(ydt.format(newDate.getTime()));
                nfilter.set("yto",ydt.format(newDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        EndTime.getDatePicker().findViewById(getResources().getIdentifier("day","id","android")).setVisibility(View.GONE);
        EndTime.getDatePicker().findViewById(getResources().getIdentifier("month","id","android")).setVisibility(View.GONE);


        final DatePickerDialog StartTime = new DatePickerDialog(this, android.R.style.Theme_Holo_Dialog, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                find(R.id.yearStartInput, EditText.class).setText(ydt.format(newDate.getTime()));
                find(R.id.yearEndInput, EditText.class).setText("");
                nfilter.set("yfrom",ydt.format(newDate.getTime()));
                EndTime.getDatePicker().setMinDate(newDate.getTimeInMillis());
                EndTime.getDatePicker().setMaxDate(Calendar.getInstance().getTimeInMillis());
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        StartTime.getDatePicker().findViewById(getResources().getIdentifier("day","id","android")).setVisibility(View.GONE);
        StartTime.getDatePicker().findViewById(getResources().getIdentifier("month","id","android")).setVisibility(View.GONE);
        Calendar mindt = Calendar.getInstance();
        mindt.add(Calendar.YEAR, -10);
        Calendar maxdt = Calendar.getInstance();
        maxdt.add(Calendar.DATE, -1);
        StartTime.getDatePicker().setMinDate(mindt.getTimeInMillis());
        StartTime.getDatePicker().setMaxDate(maxdt.getTimeInMillis());

        find(R.id.yearStartInput, EditText.class).setText(
                String.valueOf(Calendar.getInstance().get(Calendar.YEAR)-10)
        );

        find(R.id.yearStartInput).setOnClickListener(new View.OnClickListener() {
            @Override   public void onClick(View v) {
                StartTime .show();
            }
        });

        find(R.id.yearEndInput).setOnClickListener(new View.OnClickListener() {
            @Override   public void onClick(View v) {
                EndTime .show();
            }
        });
        find(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override   public void onClick(View v) {
                finish();
            }
        });

        find(R.id.btnSave).setOnClickListener(new View.OnClickListener() {
            @Override   public void onClick(View v) {
                setResult(RESULT_OK,
                        new Intent().putExtra("nfilter", nfilter.toString()));
                finish();
            }
        });

    }
}
