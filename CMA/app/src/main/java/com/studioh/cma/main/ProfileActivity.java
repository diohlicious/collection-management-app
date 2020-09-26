package com.studioh.cma.main;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;

import com.naa.data.Dson;
import com.naa.utils.Messagebox;
import com.squareup.picasso.Picasso;
import com.studioh.cma.AppActivity;
import com.studioh.cma.R;

public class ProfileActivity extends AppActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //drawerLayout = findViewById(R.id.drawer_layout);
        /*TextView mHdr = findView(R.id.main_toolbar, R.id.main_toolbar_txt, TextView.class);
        mHdr.setText("Profile");
        ImageView mSubmit = findView(R.id.main_toolbar, R.id.more, ImageView.class);
        mSubmit.setVisibility(View.INVISIBLE);*/

        newProcess(new Messagebox.DoubleRunnable() {
            String res;

            public void run() {
                res = getSetting("LV");
            }

            public void runUI() {
                Dson dson = Dson.readJson(res);
                if (dson.containsKey("NIK")) {

                    Picasso.get().load(dson.get("Personal_Images").asString()).into(find(R.id.imgAvatar, ImageView.class));

                    TextView nik = findViewById(R.id.nik);
                    nik.setText(dson.get("NIK").asString());

                    TextView ktpTextView = findViewById(R.id.ktp);
                    ktpTextView.setText(dson.get("IDNo").asString());
                    TextView nophoneTextView = findViewById(R.id.nophone);
                    nophoneTextView.setText(dson.get("MobilePhoneNo").asString());
                    TextView emailTextView = findViewById(R.id.email);
                    emailTextView.setText(dson.get("Email_Account").asString());
                    TextView bodTextView = findViewById(R.id.bod);
                    bodTextView.setText(dson.get("BirthDate").asString());

                    find(R.id.alamat, TextView.class).setText(dson.get("PersonalAddress").asString());

                } else {
                    //showError(dson.get("error").asString());
                }

            }

        });
    }
}
