package com.studioh.cma.fav;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.media.ExifInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.naa.data.Dson;
import com.naa.data.Utility;
import com.studioh.cma.AppActivity;
import com.studioh.cma.R;
import com.studioh.cma.act.Detail;
import com.studioh.srv.StudiohRecyclerAdapter;
import com.studioh.srv.StudiohViewHolder;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.DateFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Currency;
import java.util.Locale;

public class RouteActivity extends AppActivity implements OnMapReadyCallback {
    private GoogleMap mMap;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        setTitle("ROUTE");
        /*final Intent intent = getIntent();
        String sn = intent.getStringExtra("flag");*/

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Locale localeID = new Locale("in", "ID");
        final NumberFormat separator = NumberFormat.getInstance(localeID);
        //separator.setCurrency(Currency.getInstance(""));

        RecyclerView recyclerView = findViewById(R.id.rview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        //production
        //Dson n = Dson.readJson(getSetting("Monthly"));
        //demo start
        Dson n = null;
        AssetManager assetManager = getAssets();
        try {
            InputStream input = assetManager.open("account.json");
            n = Dson.readJson(isToString(input));
            for (int i = 0; i < n.size(); i++) {
                n.get(i).set("_DAYLY",Utility.Now().substring(0, 10));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        //demo end

        for (int i = 0; i < n.size(); i++) {
            if (n.get(i).get("_DAYLY").asString().equalsIgnoreCase(Utility.Now().substring(0, 10))
                    && !n.get(i).containsKey("flag")
            ) {
                nListArray.add(n.get(i));
            }
        }

        recyclerView.setAdapter(new StudiohRecyclerAdapter(nListArray, R.layout.list_item_all) {
            @Override
            public void onBindViewHolder(@NonNull StudiohViewHolder viewHolder, int position) {

                    /*viewHolder.find(R.id.txtTitle, TextView.class).setText(nListArray.get(position).get("NAMA").asString());
                    viewHolder.find(R.id.txtMsg1, TextView.class).setText(nListArray.get(position).get("").asString());
                    viewHolder.find(R.id.txtMsg1, TextView.class).setText(nListArray.get(position).get("").asString());*/
                viewHolder.find(R.id.checkBoxA).setVisibility(View.GONE);
                viewHolder.find(R.id.checkBoxB).setVisibility(View.GONE);
                viewHolder.find(R.id.cardDod).setVisibility(View.VISIBLE);

                String priority = nListArray.get(position).get("AccountGroup").asString();
                /*
                 * 1 Green
                 * 2 Yellow
                 * 3 Red
                 * 0 Grey*/
                viewHolder.find(R.id.prio, TextView.class).setText(nListArray.get(position).get("is_priority").asString());
                switch (priority) {
                    case "Green":
                        viewHolder.find(R.id.cardPrio, CardView.class).setCardBackgroundColor(Color.parseColor("#00C853"));
                        break;
                    case "Yellow":
                        viewHolder.find(R.id.cardPrio, CardView.class).setCardBackgroundColor(Color.parseColor("#FFD600"));
                        break;
                    case "Red":
                        viewHolder.find(R.id.cardPrio, CardView.class).setCardBackgroundColor(Color.parseColor("#B71C1C"));
                        break;
                    default:
                        viewHolder.find(R.id.cardPrio, CardView.class).setCardBackgroundColor(Color.parseColor("#546E7A"));
                        viewHolder.find(R.id.cardZero, CardView.class).setVisibility(View.VISIBLE);
                        break;
                }

                viewHolder.find(R.id.dod, TextView.class).setText(nListArray.get(position).get("daysOverDue").asString());
                viewHolder.find(R.id.txtTitle, TextView.class).setText(nListArray.get(position).get("CustomerFullName").asString());//CustomerFullName
                viewHolder.find(R.id.txtMsg1, TextView.class).setText(nListArray.get(position).get("AgreementNo").asString());//CustomerFullName
                viewHolder.find(R.id.txtMsg3, TextView.class).setText(nListArray.get(position).get("LicensePlate").asString());//CustomerFullName
                viewHolder.find(R.id.txtMsg4, TextView.class).setText(separator.format(nListArray.get(position).get("OSPrincipalAmount").asNumber()));

                try {
                    SimpleDateFormat fdt = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
                    DateFormat inputfdt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                    //String wotext = fdt.format(inputfdt.parse(nListArray.get(position).get("WODate").asString()))+" (WO)";
                    viewHolder.find(R.id.txtMsg5, TextView.class).setText(fdt.format(inputfdt.parse(nListArray.get(position).get("WODate").asString())));
                } catch (ParseException e) {
                    System.out.println("printing date Exception ==> "+e.toString());
                    e.printStackTrace();
                }
                //SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                //tvDisplayDate.setText(sdf.format(c.getTime()));
                viewHolder.find(R.id.txttime, TextView.class).setText( nListArray.get(position).get("visit_time").asString());
                viewHolder.find(R.id.txttime, TextView.class).setVisibility(View.VISIBLE);


                for (int i = 0; i < nListArray.get(position).get("Address").size(); i++) {
                    if (nListArray.get(position).get("Address").get(i).get("Priority").asString().equalsIgnoreCase("1")) {
                        viewHolder.find(R.id.txtMsg2, TextView.class).setText(nListArray.get(position).get("Address").get(i).get("Address").asString());
                    }
                }

                if (nListArray.get(position).get("AssetType").asString().equalsIgnoreCase("MOTOR")) {
                    viewHolder.find(R.id.imgAvatar, ImageView.class).setImageResource(R.drawable.ic_motorcycle);
                }else if (nListArray.get(position).get("AssetType").asString().equalsIgnoreCase("MOBIL")) {
                    viewHolder.find(R.id.imgAvatar, ImageView.class).setImageResource(R.drawable.ic_car);
                }else{
                }

            }
        }.setOnitemClickListener(new StudiohRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Dson parent, View view, int position) {
                setSetting("DtlDetail", nListArray.get(position).toJson());
                setSetting("agrno", nListArray.get(position).get("AgreementNo").toString());
                setSetting("plate", nListArray.get(position).get("LicensePlate").toString());
                Intent intent = new Intent(getActivity(), Detail.class);
                intent.putExtra("agrno", nListArray.get(position).get("AgreementNo").toString());//usahakan string
                startActivity(intent);
            }
        }));
    }

    protected void onActivityResult(int requestCode, int resultCode,   Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            try {
                performFlag(getIntentStringExtra(data, "flag"));
                //GoToURL("http://api.whatsapp.com/send?phone=" + "6287886722267" + "&text=" + getIntentStringExtra(data, "nfilter"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void performFlag(String flag) {
        Dson nflag = Dson.readJson(flag);
        Dson n = Dson.readJson(getSetting("Monthly"));
        for (int i = 0; i < n.size(); i++) {
            if(n.get(i).get("AgreementNo").asString().equalsIgnoreCase(nflag.get("aggrNo").asString())){
                n.get(i).set("flag","1");
                n.get(i).set("f410",nflag.get("f410").asString());
            }
        }
        setSetting("Monthly", n.toJson());
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            /*LatLng ltlg  = new LatLng(-6.2068935,106.8102459);
            mMap.addMarker(new MarkerOptions().position(ltlg).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(ltlg.latitude, ltlg.longitude), 17));

*/
            mMap.setMyLocationEnabled(true);

        }


        for (int i = 0; i < nListArray.size(); i++) {
            if (nListArray.get(i).get("Address").size() >= 1) {
                String[] latlong = (nListArray.get(i).get("Address").get(0).get("LatLong").asString() + ",").split(",");
                double latitude = Double.parseDouble(latlong[0]);
                double longitude = Double.parseDouble(latlong[1]);
                LatLng latLng = new LatLng(latitude, longitude);
                mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));

            }else{

            }
        }
    }
}
