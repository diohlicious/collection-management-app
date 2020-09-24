package com.studioh.cma.infoupd;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.naa.data.Dson;
import com.naa.utils.InternetX;
import com.naa.utils.Messagebox;
import com.studioh.cma.AppActivity;
import com.studioh.cma.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MitraAdd extends AppActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_mitra_add);

        setTitle("Tambah Info Mitra");

        Spinner dropdown = findViewById(R.id.tipeSpin);
        String[] items = new String[]{"Matel", "Tetangga", "Keluarga", "Pihak Lain"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, items);
        dropdown.setAdapter(adapter);

        find(R.id.saveInfoMit).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                simpan(find(R.id.tipeSpin, Spinner.class).getSelectedItem().toString(),
                        find(R.id.notesInput, EditText.class).getText().toString()
                );
            }

        });

        find(R.id.cancelInfMit).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), InfoUpd.class);
                startActivity(intent);
            }
        });
    }

    protected void simpan(final String tipe,
                          final String notes
    ){
        newProcess(new Messagebox.DoubleRunnable() {
            String result;
            String sagrno = getSetting("agrno");

            public void run() {
                autoToken();
                Dson dson = getDefaultDataRaw();
                dson.set("AgreementNo", sagrno);
                dson.set("Type", tipe);
                dson.set("Notes", notes);

                result =  InternetX.postHttpConnectionRaw(getBaseUrl("RPM/RPM_InfoMitra"), getDefaultHeader(), dson);
            }
            public void runUI() {
                final Dson dson = Dson.readJson(result);
                if (dson.get("ResponseCode").asString().equalsIgnoreCase("00")){
                    showInfoDialog(dson.get("ResponseDescription").asString(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            setResult(RESULT_OK);
                            MitraAdd.this.finish();
                            dialog.dismiss();
                        }
                    });
                }else{
                    showInfo(result);
                    //showInfo(dson.get("ResponseDescription").asString());
                }
            }
        });
    }


}
