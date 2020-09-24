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
import com.naa.utils.Messagebox;
import com.studioh.cma.AppActivity;
import com.studioh.cma.R;
import com.studioh.srv.StudiohRecyclerAdapter;
import com.studioh.srv.StudiohViewHolder;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class RecoveryListActivity extends AppActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recovery_list);

        setTitle("Recovery List");
/*
        RecyclerView recyclerView = findViewById(R.id.rviewmitra);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        for (int i = 0; i < 0; i++) {
            nListArray.add(Dson.newObject());
        }

        recyclerView.setAdapter(new StudiohRecyclerAdapter(nListArray, R.layout.list_item_f410) {
            @Override
            public void onBindViewHolder(@NonNull StudiohViewHolder viewHolder, int position) {



            }
        }.setOnitemClickListener(new StudiohRecyclerAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Dson parent, View view, int position) {
                        Intent intent = new Intent(getActivity(), F410.class);
                        startActivity(intent);
                    }
                })
        );*/
        reload ();
    }

    public void reload(){
        final DateFormat inputfdt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        final SimpleDateFormat fdy = new SimpleDateFormat("yyyy", Locale.US);
        final RecyclerView recyclerView = findViewById(R.id.rviewmitra);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        newProcess(new Messagebox.DoubleRunnable() {
            String res;
            public void run() {
                Dson dson = getDefaultDataRaw();
                //dson.set("LicensePlate","ALL");
                res =  postHttpRaw("RPM/RPM_GetF410List", dson );
                autoToken(res);
            }

            public void runUI() {
                Dson n = Dson.readJson(res);
                nListArray.asArray().clear();//reset
                final Locale localeID = new Locale("in", "ID");
                final NumberFormat separator = NumberFormat.getInstance(localeID);
                if (n.get(0).get("ResponseCode").asString().equalsIgnoreCase("00") ) {
                    for (int i = 0; i < n.get(i).size(); i++) {
                        if (n.get(i).get("ApprovalStatus").toString().equalsIgnoreCase("APV")){
                            nListArray.add(n.get(i));
                        }
                    }
                    recyclerView.setAdapter(new StudiohRecyclerAdapter(nListArray, R.layout.list_item_recovery) {
                                @Override
                                public void onBindViewHolder(@NonNull StudiohViewHolder viewHolder, int position) {
                                    if (nListArray.get(position).get("AssetType").asString().equalsIgnoreCase("MOTOR")) {
                                        //imgScore
                                        viewHolder.find(R.id.imgScore, ImageView.class).setImageResource(R.drawable.ic_motorcycle);
                                    } else if (nListArray.get(position).get("AssetType").asString().equalsIgnoreCase("MOBIL")) {
                                        viewHolder.find(R.id.imgScore, ImageView.class).setImageResource(R.drawable.ic_car);
                                    } else {

                                    }
                                    final int tot = nListArray.get(position).get("OSPrincipalAmount").asInteger()
                                            + nListArray.get(position).get("OSInterestAmount").asInteger()
                                            + nListArray.get(position).get("installmentDue").asInteger()
                                            + nListArray.get(position).get("AccruedInterest").asInteger()
                                            + nListArray.get(position).get("OSLCAmount").asInteger()
                                            + nListArray.get(position).get("AROthers").asInteger()
                                            + nListArray.get(position).get("PrepaymentPenalty").asInteger();
                                    //CustomerName
                                    //TransactionType
                                    viewHolder.find(R.id.TransactionType, TextView.class).setText(nListArray.get(position).get("HeaderF410List").asString());
                                    viewHolder.find(R.id.namef410, TextView.class).setText(nListArray.get(position).get("CustomerFullName").asString());//CustomerFullName
                                    viewHolder.find(R.id.amtPaid, TextView.class).setText("Amount to be paid: " + separator.format(nListArray.get(position).get("amounttobepaid").asNumber()));
                                    viewHolder.find(R.id.year, TextView.class).setText(nListArray.get(position).get("THWO").asString());

                                    //Branch
                                    viewHolder.find(R.id.agreementNo, TextView.class).setText(
                                            nListArray.get(position).get("AgreementNo").asString()
                                                    + " ("
                                                    + nListArray.get(position).get("Branch").asString()
                                                    + ")");
                                    //viewHolder.find(R.id.vehNo, TextView.class).setText(nListArray.get(position).get("LicensePlate").asString());//CustomerFullName
                                    viewHolder.find(R.id.CollectorName, TextView.class).setText("req: " + nListArray.get(position).get("RequestBy").asString());
                                    viewHolder.find(R.id.ApprovalBy, TextView.class).setText("Last Apv: " + nListArray.get(position).get("ApprovalBy").asString());
                                    viewHolder.find(R.id.payment, TextView.class).setText("Full Payment: " + separator.format(nListArray.get(position).get("Paymentf410").asNumber()));
                                    Double wpct = nListArray.get(position).get("amountf410").asDouble() * 100 / nListArray.get(position).get("amounttobepaid").asDouble();
                                    String sPaymentType = null;
                                    String sWaivedPct = null;
                                    if (nListArray.get(position).get("ApprovalTypeID").asString().equalsIgnoreCase("COLLECTIONFEE")) {
                                        sPaymentType = "Collection Fee: ";
                                        sWaivedPct = "";
                                    }
                                    else {
                                        sPaymentType = "Waived: ";
                                        sWaivedPct = " | " + String.format(localeID, "%.0f", wpct) + " %";
                                    }

                                    viewHolder.find(R.id.waived, TextView.class).setText( sPaymentType
                                            + separator.format(nListArray.get(position).get("amountf410").asNumber())
                                            + sWaivedPct);
                                    //Picasso.get().load(nListArray.get(position).get("Photo").asString()).into( find(R.id.phtMitra, ImageView.class)  );

                                }
                            }.setOnitemClickListener(new StudiohRecyclerAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(Dson parent, View view, int position) {
                                    if(nListArray.get(position).get("ApprovalStatus").toString().equalsIgnoreCase("APV")){
                                        Intent intent = new Intent(getActivity(), RecoveryDtlActivity.class);
                                        setSetting("DtlRecovery", nListArray.get(position).toJson());
                                        //TransactionType
                                        intent.putExtra("RecoveryType", nListArray.get(position).get("TransactionType").asString());
                                        startActivity(intent);
                                    } else {

                                    }
                                }
                            })
                    );

                }else{
                    showError(n.get("error").asString());
                }

            }

        });
    }
}
