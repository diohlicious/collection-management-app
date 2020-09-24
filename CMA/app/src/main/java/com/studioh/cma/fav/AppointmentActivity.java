package com.studioh.cma.fav;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.naa.data.Dson;
import com.naa.data.Utility;
import com.naa.utils.InternetX;
import com.naa.utils.Messagebox;
import com.studioh.cma.AppActivity;
import com.studioh.cma.R;
import com.studioh.cma.act.Detail;
import com.studioh.srv.StudiohRecyclerAdapter;
import com.studioh.srv.StudiohViewHolder;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;


public class AppointmentActivity extends AppActivity {
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);

        setTitle("Appointment");

        final Calendar newCalendar = Calendar.getInstance();

        final SimpleDateFormat fdt = new SimpleDateFormat("EEE, d MMM", Locale.US);
        final SimpleDateFormat fyr = new SimpleDateFormat("yyyy", Locale.US);
        final DateFormat inputfdt = new SimpleDateFormat("d/M/yyyy", Locale.US);
        final DateFormat inputgdt = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        final Date c = Calendar.getInstance().getTime();

        setSetting("dateap", "");
        setSetting("dtAppointment", "");

        reload();

        final RecyclerView recyclerView = findViewById(R.id.rview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        //setSetting("dateap", inputfdt.format(inputfdt.format(c)));
        find(R.id.yrAppointment, TextView.class).setText(fyr.format(c));
        find(R.id.dateAppointment, TextView.class).setText(fdt.format(c));
        find(R.id.date, TextView.class).setText(inputgdt.format(c));


        final DatePickerDialog  StartTime = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                final Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                find(R.id.yrAppointment, TextView.class).setText(fyr.format(newDate.getTime()));
                find(R.id.dateAppointment, TextView.class).setText(fdt.format(newDate.getTime()));
                find(R.id.date, TextView.class).setText(inputgdt.format(newDate.getTime()));

                Dson dson = Dson.readJson(getSetting("apDate"));
                nListArray = Dson.newArray();
                if (dson.size()>=1) {
                    for (int i = 0; i < dson.size(); i++) {
                        if (dson.get(i).get("Date").asString().equalsIgnoreCase(inputgdt.format(newDate.getTime()))){
                            //array.add();
                            nListArray.add(dson.get(i));
                            //showInfo(nListArray.toJson());

                        }
                    }
                    recyclerView.setAdapter(new StudiohRecyclerAdapter(nListArray, R.layout.list_item_appointment) {
                                @Override
                                public void onBindViewHolder(@NonNull StudiohViewHolder viewHolder, int position) {

                                    viewHolder.find(R.id.txtTitle, TextView.class).setText(nListArray.get(position).get("Title").asString());//CustomerFullName
                                    viewHolder.find(R.id.txtMsg1, TextView.class).setText(nListArray.get(position).get("StartTime").asString());//CustomerFullName
                                    viewHolder.find(R.id.txtMsg2, TextView.class).setText(nListArray.get(position).get("EndTime").asString());//CustomerFullName
                                }
                            }.setOnitemClickListener(new StudiohRecyclerAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(Dson parent, View view, int position) {
                                    setSetting("dtAppointment", nListArray.get(position).toJson());
                                    Intent intent = new Intent(getActivity(), AppointmentAdd.class);
                                    startActivity(intent);
                                }
                            })
                    );

                }
                //setSetting("dateap", inputgdt.format(newDate.getTime()));
                //setSetting("dateap", find(R.id.date, TextView.class).getText().toString());
                //find(R.id.date, TextView.class).setText(inputgdt.format(c));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        find(R.id.btnDate).setOnClickListener(new View.OnClickListener() {
            @Override   public void onClick(View v) {
                StartTime .show();
            }
        });

        findViewById(R.id.addAppointment).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setSetting("dateap", find(R.id.date, TextView.class).getText().toString());
                setSetting("dtAppointment", "");
                Intent intent = new Intent(getActivity(), AppointmentAdd.class);
                startActivityForResult(intent, 12);
                //intent.putExtra("date", date);//usahakan string
            }
        });

    }
    public void reload() {
        newProcess(new Messagebox.DoubleRunnable() {
            String res;

            public void run() {
                autoToken();
                Dson dson = getDefaultDataRaw();
                res = InternetX.postHttpConnectionRaw(getBaseUrl("RPM/RPM_GetDataAppointment"), getDefaultHeader(), dson);
            }

            public void runUI() {
                Dson dson = Dson.readJson(res);
                DateFormat inputgdt = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                Date c = Calendar.getInstance().getTime();
                RecyclerView recyclerView = findViewById(R.id.rview);
                if (dson.get("ResponseCode").asString().equalsIgnoreCase("00")) {

                    for (int i = 0; i < dson.get("data").size(); i++) {
                        if (dson.get("data").get(i).get("Date").asString().equalsIgnoreCase(inputgdt.format(c))) {
                            nListArray.add(dson.get("data").get(i));
                        }
                    }

                    recyclerView.setAdapter(new StudiohRecyclerAdapter(nListArray, R.layout.list_item_appointment) {
                        @Override
                        public void onBindViewHolder(@NonNull StudiohViewHolder viewHolder, int position) {
                            viewHolder.find(R.id.txtTitle, TextView.class).setText(nListArray.get(position).get("Title").asString());//CustomerFullName
                            viewHolder.find(R.id.txtMsg1, TextView.class).setText(nListArray.get(position).get("StartTime").asString());//CustomerFullName
                            viewHolder.find(R.id.txtMsg2, TextView.class).setText(nListArray.get(position).get("EndTime").asString());//CustomerFullName

                        }
                    }.setOnitemClickListener(new StudiohRecyclerAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(Dson parent, View view, int position) {
                            setSetting("dtAppointment", nListArray.get(position).toJson());
                            Intent intent = new Intent(getActivity(), AppointmentAdd.class);
                            startActivity(intent);
                        }
                    }));

                    setSetting("apDate", dson.get("data").toJson());
                    //showInfo(dson.get("data").asString());

                } else {
                    showError(res);
                }
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode,   Intent data) {
        if (requestCode==12 && resultCode == RESULT_OK){
            reload();
        }
    }


}
