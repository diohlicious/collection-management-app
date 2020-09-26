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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AddressesAdd extends AppActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addresses_add);

        setTitle("Tambah Collection Poin");

        Spinner dropdown = findViewById(R.id.prioSpin);
        String[] items = new String[]{"Additional", "Main"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, items);
        dropdown.setAdapter(adapter);

        //addrSpin
        Spinner dropdownaddr = findViewById(R.id.tipeSpin);
        String[] itemsaddr = new String[]{"Residence", "Legal", "Company"};
        ArrayAdapter<String> adapteraddr = new ArrayAdapter<>(this, R.layout.spinner_item, itemsaddr);
        dropdownaddr.setAdapter(adapteraddr);

        find(R.id.saveColPoin).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (find(R.id.addrInput, EditText.class).getText().toString().length() == 0) {
                    find(R.id.addrInput, EditText.class).setError("Alamat Diperlukan!");
                } else if (find(R.id.rtInput, EditText.class).getText().toString().length() == 0) {
                    find(R.id.rtInput, EditText.class).setError("Alamat Diperlukan!");
                } else if (find(R.id.rwInput, EditText.class).getText().toString().length() == 0) {
                    find(R.id.rwInput, EditText.class).setError("Alamat Diperlukan!");
                }else if (find(R.id.kecamatanInput, EditText.class).getText().toString().length() == 0) {
                    find(R.id.kecamatanInput, EditText.class).setError("Alamat Diperlukan!");
                }else if (find(R.id.kelurahanInput, EditText.class).getText().toString().length() == 0) {
                    find(R.id.kelurahanInput, EditText.class).setError("Alamat Diperlukan!");
                }else if (find(R.id.kotaInput, EditText.class).getText().toString().length() == 0) {
                    find(R.id.kotaInput, EditText.class).setError("Alamat Diperlukan!");
                }else if (find(R.id.kodeposInput, EditText.class).getText().toString().length() == 0) {
                    find(R.id.kodeposInput, EditText.class).setError("Alamat Diperlukan!");
                }
                else {
                    simpan(find(R.id.addrInput, EditText.class).getText().toString(),
                            find(R.id.rtInput, EditText.class).getText().toString(),
                            find(R.id.rwInput, EditText.class).getText().toString(),
                            find(R.id.kecamatanInput, EditText.class).getText().toString(),
                            find(R.id.kelurahanInput, EditText.class).getText().toString(),
                            find(R.id.kotaInput, EditText.class).getText().toString(),
                            find(R.id.kodeposInput, EditText.class).getText().toString(),
                            find(R.id.tipeSpin, Spinner.class).getSelectedItem().toString(),
                            find(R.id.prioSpin, Spinner.class).getSelectedItem().toString()
                    );
                }
            }

        });


        find(R.id.cancelColPoin).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), InfoUpd.class);
                startActivity(intent);
            }
        });
    }

    protected void simpan(final String addrs,
                          final String rt,
                          final String rw,
                          final String kec,
                          final String kel,
                          final String kota,
                          final String poscode,
                          final String tipe,
                          final String prio
    ) {
        newProcess(new Messagebox.DoubleRunnable() {
            String result;
            //String sagrno = getIntentStringExtra("agrno");
            String sagrno = getSetting("agrno");

            public void run() {
                //if (addrs != null && !addrs.isEmpty() && !addrs.equals("null")) {
                autoToken();
                Dson dson = getDefaultDataRaw();
                Dson nAddr = Dson.newObject();
                if(prio.equalsIgnoreCase("Main")){
                    String strprio= "1";
                    dson.set("Priority", strprio);
                    nAddr.set("Priority", strprio);
                }else{
                    String strprio= "0";
                    dson.set("Priority", strprio);
                    nAddr.set("Priority", strprio);
                }
                nAddr.set("Address", addrs);
                nAddr.set("Type", tipe);
                dson.set("Address", addrs);
                dson.set("RT", rt);
                dson.set("RW", rw);
                dson.set("Kecamatan", kec);
                dson.set("Kelurahan", kel);
                dson.set("Kota", kota);
                dson.set("Kodepos", poscode);
                dson.set("Type", tipe);

                //Dson list = Dson.newArray();

                dson.set("AgreementNo", sagrno);
                setSetting("nAddr",nAddr.toJson());

                result = InternetX.postHttpConnectionRaw(getBaseUrl("RPM/RPM_CollectionPoint"), getDefaultHeader(), dson);

                //
            }

            public void runUI() {
                Dson dson = Dson.readJson(result);
                if (dson.get("ResponseCode").asString().equalsIgnoreCase("00")) {
                    showInfoDialog(dson.get("ResponseDescription").asString(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            setResult(RESULT_OK);
                            Dson addAddrs = Dson.readJson(getSetting("DtlDetail"));
                            Dson nAddr = Dson.readJson(getSetting("nAddr"));
                            addAddrs.get("Address").add(nAddr);
                            setSetting("DtlDetail",addAddrs.toJson());
                            AddressesAdd.this.finish();
                            dialog.dismiss();
                        }
                    });
                } else {
                    //showInfo(result);
                    showInfo(getSetting("nAddr"));
                }
            }
        });
    }
}
