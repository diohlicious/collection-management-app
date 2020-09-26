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

public class AssetAdd extends AppActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overasset_add);

        setTitle("Over Asset");

        Spinner dropdown = findViewById(R.id.profileSelect);
        String[] items = new String[]{"LSM", "Aparat Negara", "Masyarakat", "Ormas", "Lain-lain"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, items);
        dropdown.setAdapter(adapter);
        //

        find(R.id.saveInfoMit).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (find(R.id.thrdPartyInput, EditText.class).getText().toString().length() == 0) {
                    find(R.id.thrdPartyInput, EditText.class).setError("Informasi Diperlukan!");
                } else if (find(R.id.addrInput, EditText.class).getText().toString().length() == 0) {
                    find(R.id.addrInput, EditText.class).setError("Informasi Diperlukan!");
                } else if (find(R.id.rtInput, EditText.class).getText().toString().length() == 0) {
                    find(R.id.rtInput, EditText.class).setError("Informasi Diperlukan!");
                }else if (find(R.id.rwInput, EditText.class).getText().toString().length() == 0) {
                    find(R.id.rwInput, EditText.class).setError("Informasi Diperlukan!");
                }else if (find(R.id.kecamatanInput, EditText.class).getText().toString().length() == 0) {
                    find(R.id.kecamatanInput, EditText.class).setError("Informasi Diperlukan!");
                }else if (find(R.id.kelurahanInput, EditText.class).getText().toString().length() == 0) {
                    find(R.id.kelurahanInput, EditText.class).setError("Informasi Diperlukan!");
                }else if (find(R.id.kotaInput, EditText.class).getText().toString().length() == 0) {
                    find(R.id.kotaInput, EditText.class).setError("Informasi Diperlukan!");
                }else if (find(R.id.kodeposInput, EditText.class).getText().toString().length() == 0) {
                    find(R.id.kodeposInput, EditText.class).setError("Informasi Diperlukan!");
                }
                else if (find(R.id.phoneInput, EditText.class).getText().toString().length() == 0) {
                    find(R.id.phoneInput, EditText.class).setError("Informasi Diperlukan!");
                } else if (find(R.id.phone2Input, EditText.class).getText().toString().length() == 0) {
                    find(R.id.phone2Input, EditText.class).setError("Informasi Diperlukan!");
                } else if (find(R.id.emailInput, EditText.class).getText().toString().length() == 0) {
                    find(R.id.emailInput, EditText.class).setError("Informasi Diperlukan!");
                } else if (find(R.id.reasonInput, EditText.class).getText().toString().length() == 0) {
                    find(R.id.reasonInput, EditText.class).setError("Informasi Diperlukan!");
                } else if (find(R.id.notesInput, EditText.class).getText().toString().length() == 0) {
                    find(R.id.notesInput, EditText.class).setError("Informasi Diperlukan!");
                } else {
                    simpan(find(R.id.phoneInput, EditText.class).getText().toString(),
                            find(R.id.thrdPartyInput, EditText.class).getText().toString(),
                            find(R.id.addrInput, EditText.class).getText().toString(),
                            find(R.id.rtInput, EditText.class).getText().toString(),
                            find(R.id.rwInput, EditText.class).getText().toString(),
                            find(R.id.kecamatanInput, EditText.class).getText().toString(),
                            find(R.id.kelurahanInput, EditText.class).getText().toString(),
                            find(R.id.kotaInput, EditText.class).getText().toString(),
                            find(R.id.kodeposInput, EditText.class).getText().toString(),
                            find(R.id.phone2Input, EditText.class).getText().toString(),
                            find(R.id.emailInput, EditText.class).getText().toString(),
                            find(R.id.profileSelect, Spinner.class).getSelectedItem().toString(),
                            find(R.id.reasonInput, EditText.class).getText().toString(),
                            find(R.id.notesInput, EditText.class).getText().toString()
                    );
                }
            }

        });
        find(R.id.cancelOvrAsset).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), InfoUpd.class);
                startActivity(intent);
            }
        });
    }


    final int FORGOT = 89;

    protected void simpan(final String mphone,
                          final String tparty,
                          final String addrs,
                          final String rt,
                          final String rw,
                          final String kec,
                          final String kel,
                          final String kota,
                          final String poscode,
                          final String phone,
                          final String email,
                          final String prof,
                          final String resn,
                          final String notes
    ) {
        newProcess(new Messagebox.DoubleRunnable() {
            String result;
            String sagrno = getSetting("agrno");

            public void run() {
                autoToken();
                Dson dson = getDefaultDataRaw();
                dson.set("AgreementNo", sagrno);
                dson.set("MobilePhone", mphone);
                dson.set("ThirdParty", tparty);
                dson.set("Address", addrs);
                dson.set("RT", rt);
                dson.set("RW", rw);
                dson.set("Kecamatan", kec);
                dson.set("Kelurahan", kel);
                dson.set("Kota", kota);
                dson.set("Kodepos", poscode);
                dson.set("Phone", phone);
                dson.set("Email", email);
                dson.set("Profile", prof);
                dson.set("Reason", resn);
                dson.set("Notes", notes);
                result = InternetX.postHttpConnectionRaw(getBaseUrl("RPM/RPM_OverAsset"), getDefaultHeader(), dson);
            }

            public void runUI() {
                Dson dson = Dson.readJson(result);
                if (dson.get("ResponseCode").asString().equalsIgnoreCase("00")) {
                    showInfoDialog(dson.get("ResponseDescription").asString(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            setResult(RESULT_OK);
                            dialog.dismiss();
                            AssetAdd.this.setResult(RESULT_OK);
                            AssetAdd.this.finish();
                        }
                    });
                } else {
                    showInfo(dson.get("ResponseDescription").asString());
                }
            }
        });
    }


}
