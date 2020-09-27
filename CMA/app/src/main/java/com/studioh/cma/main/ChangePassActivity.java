package com.studioh.cma.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.naa.data.Dson;
import com.naa.data.Utility;
import com.naa.utils.InternetX;
import com.naa.utils.Messagebox;
import com.studioh.cma.AppActivity;
import com.studioh.cma.R;
import com.studioh.cma.fav.AttendanceActivity;
import com.studioh.cma.main.LoginActivity;

import java.util.Calendar;

import static android.text.InputType.TYPE_CLASS_TEXT;
import static android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD;

public class ChangePassActivity extends AppActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        find(R.id.imageView, ImageView.class).setImageResource(R.drawable.repossess_kunci);

        find(R.id.txtUsername, EditText.class).setInputType(TYPE_TEXT_VARIATION_PASSWORD );
        find(R.id.txtPassword, EditText.class).setInputType(TYPE_TEXT_VARIATION_PASSWORD );


        find(R.id.tblSubmit).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                newProcess(new Messagebox.DoubleRunnable() {
                    String result;
                    public void run() {

                        Dson dson = Dson.readJson(getSetting("DtVerify"));
                        dson.set("Username", find(R.id.txtUsername, EditText.class).getText().toString());
                        dson.set("password", find(R.id.txtPassword, EditText.class).getText().toString());
                        setSetting("frgt", dson.toJson());
                        result =  InternetX.postHttpConnectionRaw(getBaseUrl("RPM/RPM_User_ChangePassword"), getDefaultHeader(), dson);

                    }
                    public void runUI() {
                        Dson dson = Dson.readJson(result);
                        if (dson.get("ResponseCode").asString().equalsIgnoreCase("00")) {
                            showInfoDialog(dson.get("ResponseDescription").asString(), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent i = new Intent(getActivity(), LoginActivity.class);
                                    getActivity().startActivity(i);
                                    dialog.dismiss();
                                }
                            });
                        } else {
                            showInfo(result);
                        }
                    }
                });
            }
        });
    }
}
