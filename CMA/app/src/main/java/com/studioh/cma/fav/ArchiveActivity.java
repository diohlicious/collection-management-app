package com.studioh.cma.fav;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.naa.data.Dson;
import com.naa.utils.InternetX;
import com.naa.utils.Messagebox;
import com.studioh.cma.AppActivity;
import com.studioh.cma.R;
import com.studioh.cma.act.Detail;
import com.studioh.srv.StudiohRecyclerAdapter;
import com.studioh.srv.StudiohViewHolder;

import java.text.NumberFormat;
import java.util.Locale;

public class ArchiveActivity extends AppActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backlog);

        setTitle("BACK LOG");

        findView(find(R.id.xRpk), R.id.txtName, TextView.class).setText("NA");
        find(R.id.xAdd).setVisibility(View.GONE);
        findView(find(R.id.xVisited), R.id.txtName, TextView.class).setText("VST");
        findView(find(R.id.xRal), R.id.txtName, TextView.class).setText("IDENT");
        findView(find(R.id.xF410), R.id.txtName, TextView.class).setText("APV");
        findView(find(R.id.xDone), R.id.txtName, TextView.class).setText("DONE");

        final RecyclerView recyclerView = findViewById(R.id.rview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        newProcess( new Messagebox.DoubleRunnable() {
            String res;

            public void run() {
                Dson dson = getDefaultDataRaw();
                dson.set("LicensePlate", "ALL");
                res = postHttpRaw("RPM/RPM_GetBacklogView", dson);
                autoToken(res);

            }

            public void runUI() {
                Dson dson = Dson.readJson(res);
                final Locale localeID = new Locale("in", "ID");
                final NumberFormat separator = NumberFormat.getInstance(localeID);
                if (dson.get("ResponseCode").asString().equalsIgnoreCase("00")) {

                    findView(R.id.xRpk, R.id.txtNumber, TextView.class).setText(dson.get("summary").get(0).get("NA").asString());
                    //findView( R.id.xAdd, R.id.txtNumber, TextView.class).setText(dson.get("VST").asString());
                    findView(R.id.xVisited, R.id.txtNumber, TextView.class).setText(dson.get("summary").get(0).get("VST").asString());
                    findView(R.id.xRal, R.id.txtNumber, TextView.class).setText(dson.get("summary").get(0).get("IDENT").asString());
                    findView(R.id.xF410, R.id.txtNumber, TextView.class).setText(dson.get("summary").get(0).get("APV").asString());
                    findView(R.id.xDone, R.id.txtNumber, TextView.class).setText(dson.get("summary").get(0).get("DONE").asString());

                    //recycle view


                    for (int i = 0; i < dson.get("view").size(); i++) {
                        nListArray.add(dson.get("view").get(i));
                    }

                    recyclerView.setAdapter(new StudiohRecyclerAdapter(nListArray, R.layout.list_item_archive) {
                        @Override
                        public void onBindViewHolder(@NonNull StudiohViewHolder viewHolder, int position) {
                            if (nListArray.get(position).get("AssetType").asString().equalsIgnoreCase("MOTOR")) {
                                //imgScore
                                viewHolder.find(R.id.imgScore, ImageView.class).setImageResource(R.drawable.ic_motorcycle);
                            } else if (nListArray.get(position).get("AssetType").asString().equalsIgnoreCase("MOBIL")) {
                                viewHolder.find(R.id.imgScore, ImageView.class).setImageResource(R.drawable.ic_car);
                            } else {

                            }

                            viewHolder.find(R.id.nama, TextView.class).setText(nListArray.get(position).get("CustomerName").asString());
                            viewHolder.find(R.id.year, TextView.class).setText(nListArray.get(position).get("WO_Year").asString());
                            viewHolder.find(R.id.agreementNo, TextView.class).setText(nListArray.get(position).get("AgreementNo").asString());
                            viewHolder.find(R.id.woAmt, TextView.class).setText(
                                    String.format("WO. Amount: %s", separator.format(nListArray.get(position).get("WOAmount").asNumber())));
                            viewHolder.find(R.id.txt0, TextView.class).setText(
                                    String.format("Fee. %s. Last %s. Next %s", separator.format(nListArray.get(position).get("FeeAmount").asNumber()), nListArray.get(position).get("FeeApproval").asString(), nListArray.get(position).get("FeeNextApproval").asString())
                            );
                            viewHolder.find(R.id.txt1, TextView.class).setText(
                                    String.format("Waived. %s. Last %s. Next %s", separator.format(nListArray.get(position).get("PayAmount").asNumber()), nListArray.get(position).get("PaymentApproval").asString(), nListArray.get(position).get("PaymentNextApproval").asString())
                            );
                            viewHolder.find(R.id.txt2, TextView.class).setText(nListArray.get(position).get("BacklogStatus").asString());
                        }
                    }/*.setOnitemClickListener(new StudiohRecyclerAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(Dson parent, View view, int position) {
                            //Toast.makeText(getActivity(),"HHHHH "+position, Toast.LENGTH_SHORT).show();
                *//*Intent intent =  new Intent(getActivity(), ControlLayanan.class);
                intent.putExtra("ID", nListArray.get(position).get("MEKANIK").asInteger());
                intent.putExtra("DATA", nListArray.get(position).toJson());
                startActivityForResult(intent, REQUEST_CONTROL);*//*
                            Intent intent = new Intent(getActivity(), Detail.class);
                            startActivity(intent);
                        }
                    })*/);

                } else {
                    showError(dson.get("error").asString());
                }
            }
        });
    }
}
