package com.studioh.cma.fav;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.TimePicker;

import com.naa.data.Dson;
import com.naa.utils.InternetX;
import com.naa.utils.Messagebox;
import com.studioh.cma.AppActivity;
import com.studioh.cma.main.MainActivity;
import com.studioh.cma.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AppointmentAdd extends AppActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_add);

        SimpleDateFormat df = new SimpleDateFormat("dd", Locale.US);
        SimpleDateFormat dy = new SimpleDateFormat("EEEE", Locale.US);
        DateFormat inputfdt = new SimpleDateFormat("d/M/yyyy", Locale.US);
        DateFormat inputgdt = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        Spinner dropdown = findViewById(R.id.tipeSpin);
        String[] items = new String[]{"15 Menit", "1 Jam", "2 Jam", "1 Hari"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, items);
        dropdown.setAdapter(adapter);

        Dson dtAppointment = Dson.readJson(getSetting("dtAppointment"));

        if (dtAppointment.containsKey("Date")){
            setTitle("Detail Janji");
            try {
                find(R.id.dayAppointment, TextView.class).setText(dy.format(inputgdt.parse(dtAppointment.get("Date").asString())));
                find(R.id.dateAppointment, TextView.class).setText(df.format(inputgdt.parse(dtAppointment.get("Date").asString())));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            //find(R.id.dateAppointment, TextView.class).setText(("Date"));
            //find(R.id.dayAppointment, TextView.class).setText(df.format(inputgdt.parse(dtAppointment.get("Date"))));
            find(R.id.titleInput, EditText.class).setText(dtAppointment.get("Title").asString());
            find(R.id.timeStartInput, EditText.class).setText(dtAppointment.get("StartTime").asString());
            find(R.id.timeEndInput, EditText.class).setText(dtAppointment.get("EndTime").asString());
            find(R.id.locationInput, EditText.class).setText(dtAppointment.get("Location").asString());
            find(R.id.attendeeInput, EditText.class).setText(dtAppointment.get("Attendee").asString());
            find(R.id.notesInput, EditText.class).setText(dtAppointment.get("Notes").asString());
            find(R.id.tipeSpin, Spinner.class).setSelection(0);
            //notesInput
            find(R.id.titleInput, EditText.class).setEnabled(false);
            find(R.id.timeStartInput, EditText.class).setEnabled(false);
            find(R.id.timeEndInput, EditText.class).setEnabled(false);
            find(R.id.locationInput, EditText.class).setEnabled(false);
            find(R.id.attendeeInput, EditText.class).setEnabled(false);
            find(R.id.notesInput, EditText.class).setEnabled(false);
            find(R.id.saveAppointment, Button.class).setEnabled(false);
            find(R.id.cancelAppointment, Button.class).setEnabled(false);
        }else {

            setTitle("Tambah Janji");
            String c = getSetting("dateap");
            if (c == "") {
                Date cl = Calendar.getInstance().getTime();
                //setSetting("dateap", inputfdt.format(inputfdt.format(c)));
                find(R.id.dateAppointment, TextView.class).setText(df.format((cl)));
                find(R.id.dayAppointment, TextView.class).setText(dy.format((cl)));
                setSetting("dateap", inputgdt.format(cl));
            } else {
                try {
                    find(R.id.dateAppointment, TextView.class).setText(df.format(inputgdt.parse(c)));
                    find(R.id.dayAppointment, TextView.class).setText(dy.format(inputgdt.parse(c)));
                    setSetting("dateap", c);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            findViewById(R.id.timeStartInput).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TimePickerDialog timePickerDialog = new TimePickerDialog(AppointmentAdd.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            find(R.id.timeStartInput, EditText.class).setText(selectedHour + ":" + selectedMinute);
                        }
                    }, 0, 0, true);
                    timePickerDialog.show();
                }
            });
            findViewById(R.id.timeEndInput).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TimePickerDialog timePickerDialog = new TimePickerDialog(AppointmentAdd.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            find(R.id.timeEndInput, EditText.class).setText(selectedHour + ":" + selectedMinute);
                        }
                    }, 0, 0, true);
                    timePickerDialog.show();
                }
            });

            //

            find(R.id.saveAppointment).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (find(R.id.titleInput, EditText.class).getText().toString().length() == 0) {
                        find(R.id.titleInput, EditText.class).setError("Informasi Diperlukan!");
                    } else if (find(R.id.timeStartInput, EditText.class).getText().toString().length() == 0) {
                        find(R.id.timeStartInput, EditText.class).setError("Informasi Diperlukan!");
                    } else if (find(R.id.timeEndInput, EditText.class).getText().toString().length() == 0) {
                        find(R.id.timeEndInput, EditText.class).setError("Informasi Diperlukan!");
                    } else if (find(R.id.locationInput, EditText.class).getText().toString().length() == 0) {
                        find(R.id.locationInput, EditText.class).setError("Informasi Diperlukan!");
                    } else if (find(R.id.attendeeInput, EditText.class).getText().toString().length() == 0) {
                        find(R.id.attendeeInput, EditText.class).setError("Informasi Diperlukan!");
                    } else if (find(R.id.notesInput, EditText.class).getText().toString().length() == 0) {
                        find(R.id.notesInput, EditText.class).setError("Informasi Diperlukan!");
                    } else {
                        simpan(find(R.id.titleInput, EditText.class).getText().toString(),
                                find(R.id.timeStartInput, EditText.class).getText().toString(),
                                find(R.id.timeEndInput, EditText.class).getText().toString(),
                                find(R.id.locationInput, EditText.class).getText().toString(),
                                find(R.id.attendeeInput, EditText.class).getText().toString(),
                                find(R.id.notesInput, EditText.class).getText().toString(),
                                find(R.id.tipeSpin, Spinner.class).getSelectedItem().toString()
                        );
                    }
                }

            });
        }

    }

    protected void simpan(final String titl,
                          final String timest,
                          final String timend,
                          final String loc,
                          final String attd,
                          final String notes,
                          final String rmdr
    ) {
        newProcess(new Messagebox.DoubleRunnable() {
            String result;

            public void run() {
                autoToken();
                Dson dson = getDefaultDataRaw();
                dson.set("Date", getSetting("dateap"));
                dson.set("Title", titl);
                dson.set("StartTime", timest);
                dson.set("EndTime", timend);
                dson.set("Location", loc);
                dson.set("Attendee", attd);
                dson.set("Notes", notes);
                dson.set("Remainder", rmdr);
                result = InternetX.postHttpConnectionRaw(getBaseUrl("RPM/RPM_Appointment"), getDefaultHeader(), dson);
            }

            public void runUI() {
                Dson dson = Dson.readJson(result);
                if (dson.get("ResponseCode").asString().equalsIgnoreCase("00")) {
                    showInfoDialog(dson.get("ResponseDescription").asString(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            setResult(RESULT_OK);
                            AppointmentAdd.this.finish();
                            dialog.dismiss();
                        }
                    });
                } else {
                    showInfo(result);
                }
            }
        });
    }

}