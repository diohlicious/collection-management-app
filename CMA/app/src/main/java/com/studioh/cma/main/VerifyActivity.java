package com.studioh.cma.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Spinner;

import com.naa.data.Dson;
import com.naa.utils.InternetX;
import com.naa.utils.Messagebox;
import com.studioh.cma.AppActivity;
import com.studioh.cma.R;

public class VerifyActivity extends AppActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);

        setTitle("VERIFY OTP");

        View.OnKeyListener key = new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (!v.toString().isEmpty())
                    v.focusSearch(View.FOCUS_RIGHT).requestFocus();

                return false;
            }
        };
        find(R.id.txtOtp1, EditText.class).setOnKeyListener(key);
        find(R.id.txtOtp2, EditText.class).setOnKeyListener(key);
        find(R.id.txtOtp3, EditText.class).setOnKeyListener(key);

        /*code1.setOnKeyListener(key);
        code2.setOnKeyListener(key);
        code3.setOnKeyListener(key);*/

        find(R.id.tblVerify).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                verify(find(R.id.txtOtp1, EditText.class).getText().toString(),
                        find(R.id.txtOtp2, EditText.class).getText().toString(),
                        find(R.id.txtOtp3, EditText.class).getText().toString(),
                        find(R.id.txtOtp4, EditText.class).getText().toString());
            }

        });
    }

    protected void verify(final String otp1,
                          final String otp2,
                          final String otp3,
                          final String otp4
    ) {
        newProcess(new Messagebox.DoubleRunnable() {
            String result;

            public void run() {
                if (otp1.length() == 0 && otp2.length() == 0 && otp3.length() == 0 && otp4.length() == 0) {
                    result = "Masukan OTP Dengan Benar";
                } else {
                    String otp = otp1 + otp2 + otp3 + otp4;
                    Dson nver = Dson.readJson(getSetting("DtVerify"));
                    autoToken();
                    nver.set("TokenNo", otp);

                    result = InternetX.postHttpConnectionRaw(getBaseUrl("RPM/RPM_ValidateToken_Forgotpassword"), getDefaultHeader(), nver);
                }
                //
            }

            public void runUI() {
                Dson nson = Dson.readJson(result);
                if (nson.get("ResponseCode").asString().equalsIgnoreCase("00")) {
                    showInfoDialog(nson.get("ResponseDescription").asString(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i = new Intent(getActivity(), ChangePassActivity.class);
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
}
