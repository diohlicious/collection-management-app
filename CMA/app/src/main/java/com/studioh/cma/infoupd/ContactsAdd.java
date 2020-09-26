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

public class ContactsAdd extends AppActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_add);

        setTitle("Tambah Contact Info");

        Spinner dropdown = findViewById(R.id.prioSpin);
        String[] items = new String[]{"Additional", "Main"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, items);
        dropdown.setAdapter(adapter);

        //addrSpin
        Spinner dropdownaddr = findViewById(R.id.tipeSpin);
        String[] itemsaddr = new String[]{"MobilePhone", "PhoneNo","CompanyPhoneNo", "EmergencyContactPhoneNo", "Spouse_MobilePhoneNo"};
        ArrayAdapter<String> adapteraddr = new ArrayAdapter<>(this, R.layout.spinner_item, itemsaddr);
        dropdownaddr.setAdapter(adapteraddr);


        find(R.id.saveColPoin).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(find(R.id.infoInput, EditText.class).getText().toString().length()==0) {
                    find(R.id.infoInput, EditText.class).setError("Nomor Telepon Diperlukan!");
                }else {
                    simpan(find(R.id.infoInput, EditText.class).getText().toString(),
                            find(R.id.tipeSpin, Spinner.class).getSelectedItem().toString(),
                            find(R.id.prioSpin, Spinner.class).getSelectedItem().toString()
                    );
                }
            }

        });

        find(R.id.cancelConInf).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), InfoUpd.class);
                startActivity(intent);
            }
        });
        
    }

    protected void simpan(final String info,
                          final String tipe,
                          final String prio
    ){
        newProcess(new Messagebox.DoubleRunnable() {
            String result;
            String sagrno = getSetting("agrno");
            public void run() {
                //if (info != null && !info.isEmpty() && !info.equals("null")) {
                autoToken();
                Dson dson = getDefaultDataRaw();
                Dson nCtact = Dson.newObject();
                dson.set("AgreementNo", sagrno);
                if(prio.equalsIgnoreCase("Main")){
                    String strprio= "1";
                    dson.set("Priority", strprio);
                    nCtact.set("Priority", strprio);
                }else{
                    String strprio= "0";
                    dson.set("Priority", strprio);
                    nCtact.set("Priority", strprio);
                }
                //format telepon 08...
                dson.set("Information", viewNomorPh(info));
                dson.set("Category", tipe);
                nCtact.set("Information", info);
                nCtact.set("Category", tipe);

                dson.set("AgreementNo", sagrno);
                setSetting("nCtact",nCtact.toJson());


                result =  InternetX.postHttpConnectionRaw(getBaseUrl("RPM/RPM_ContactInformation"), getDefaultHeader(), dson);
            }
            public void runUI() {
                Dson dson = Dson.readJson(result);
                if (dson.get("ResponseCode").asString().equalsIgnoreCase("00")){
                    showInfoDialog(dson.get("ResponseDescription").asString(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            setResult(RESULT_OK);
                            Dson addCtct = Dson.readJson(getSetting("DtlDetail"));
                            Dson nCtact = Dson.readJson(getSetting("nCtact"));
                            addCtct.get("ContactInformation").add(nCtact);
                            setSetting("DtlDetail",addCtct.toJson());
                            ContactsAdd.this.finish();
                            dialog.dismiss();
                        }
                    });
                }else{
                    showInfo(dson.get("ResponseDescription").asString());
                }
            }
        });
    }
}
