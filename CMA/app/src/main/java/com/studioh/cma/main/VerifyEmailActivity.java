package com.studioh.cma.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Spinner;

import com.naa.data.Dson;
import com.naa.utils.InternetX;
import com.naa.utils.Messagebox;
import com.studioh.cma.AppActivity;
import com.studioh.cma.main.MainActivity;
import com.studioh.cma.R;
import com.studioh.cma.main.VerifyActivity;

public class VerifyEmailActivity extends AppActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_email);

        setTitle("Forgot Password");

        find(R.id.txtPhone, EditText.class).setInputType(InputType.TYPE_CLASS_PHONE);
        //String toNumber = validateNomorWA(dson.get("Direct_Advisor").get("MobilePhoneAdvisor").asString());

//txtPhone
        find(R.id.getToken).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(find(R.id.txtEmail, EditText.class).getText().toString().length()==0) {
                    find(R.id.txtEmail, EditText.class).setError("Email Diperlukan!");
                }else if(find(R.id.txtPhone, EditText.class).getText().toString().length()==0) {
                    find(R.id.txtPhone, EditText.class).setError("Nomor HP Diperlukan!");
                }else{
                    kirim(find(R.id.txtEmail, EditText.class).getText().toString(),
                            find(R.id.txtPhone, EditText.class).getText().toString()
                    );}
            }

        });
    }

    protected void kirim(final String email,
                         final String phone
    ){
        newProcess(new Messagebox.DoubleRunnable() {
            String result;
            public void run() {
                autoToken();
                Dson dson = Dson.newObject();
                String toNumber = validateNomorWA(phone);
                dson.set("LoginID", email);
                dson.set("MobilePhone", toNumber);
                setSetting("DtVerify", dson.toJson());

                result =  InternetX.postHttpConnectionRaw(getBaseUrl("RPM/RPM_getToken_Forgotpassword"), getDefaultHeader(), dson);

                //
            }
            public void runUI() {
                final Dson dson = Dson.readJson(result);
                if (dson.get("ResponseCode").asString().equalsIgnoreCase("00")){
                    showInfoDialog(dson.get("ResponseDescription").asString(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i = new Intent(getActivity(), VerifyActivity.class);
                            getActivity().startActivity(i);
                            dialog.dismiss();
                            //showInfo(dson.get("TokenNo").asString());
                        }
                    });
                }else{
                    showInfo(result);
                }
            }
        });
    }
}
