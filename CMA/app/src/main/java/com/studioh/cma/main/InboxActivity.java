package com.studioh.cma.main;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.naa.data.Dson;
import com.naa.utils.Messagebox;
import com.studioh.cma.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

import com.studioh.cma.AppActivity;
import com.studioh.srv.StudiohRecyclerAdapter;
import com.studioh.srv.StudiohViewHolder;

public class InboxActivity extends AppActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        //drawerLayout = findViewById(R.id.drawer_layout);

        final RecyclerView recyclerView = findViewById(R.id.rview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        newProcess(new Messagebox.DoubleRunnable() {
            String res;

            public void run() {
                Dson dson = getDefaultDataRaw();
                dson.set("LicensePlate", "ALL");
                res = postHttpRaw("RPM/RPM_GetDataLog", dson);
                autoToken(res);
            }

            public void runUI() {
                Dson n = Dson.readJson(res);
                if (n.get("ResponseCode").asString().equalsIgnoreCase("00")) {

                    for (int i = 0; i < n.get("data").size(); i++) {
                        nListArray.add(n.get("data").get(i));
                    }

                    Comparator objComparator = new Comparator() {
                        public int compare(Object o1, Object o2) {
                            DateFormat inputfdt = new SimpleDateFormat("dd MMMM yyyy hh:mm:ss", Locale.US);
                            try {
                                long time1 = inputfdt.parse(new Dson(o1).get("LogDate").asString()).getTime();
                                long time2 = inputfdt.parse(new Dson(o2).get("LogDate").asString()).getTime();
                                return time1 > time2 ? -1 : time1 == time2 ? 0 : 1;
                            } catch (Exception e) {
                            }
                            return 0;
                        }
                    };
                    Collections.sort(nListArray.asArray(), objComparator);

                    recyclerView.setAdapter(new StudiohRecyclerAdapter(nListArray, R.layout.list_item_inbox) {
                        @Override
                        public void onBindViewHolder(@NonNull StudiohViewHolder viewHolder, int position) {

                            viewHolder.find(R.id.txtTitle, TextView.class).setText(nListArray.get(position).get("LogHeader").asString());//CustomerFullName
                            viewHolder.find(R.id.txtMsg1, TextView.class).setText(nListArray.get(position).get("LogDesc").asString());//CustomerFullName
                            viewHolder.find(R.id.txtMsg2, TextView.class).setText(nListArray.get(position).get("LogDate").asString());//CustomerFullName
                        }

                    });
                    //showInfo(res);

                } else {
                    showError(n.get("error").asString());
                }
            }
        });
    }
}
