package com.studioh.cma.fav;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

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
import com.naa.data.UtilityAndroid;
import com.naa.utils.Messagebox;
import com.studioh.cma.AppActivity;
import com.studioh.cma.R;
import com.studioh.srv.StudiohRecyclerAdapter;
import com.studioh.srv.StudiohViewHolder;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class FindActivity extends AppActivity implements OnMapReadyCallback {
    private GoogleMap mMap;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);

        setTitle("ONLINE SEARCH");

        String menu = getSetting("Access_Level");
        if (!menu.equalsIgnoreCase("operasional")) {
            findViewById(R.id.footer).setVisibility(View.GONE);
        }

        RecyclerView recyclerView = findViewById(R.id.rview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync( this);


        find(R.id.txtSearch, EditText.class).setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch();
                    return true;
                }
                return false;
            }
        });
        setFilterUcase(find(R.id.txtSearch, EditText.class));
    }

    public void performSearch(){
        findViewById(R.id.detail).setVisibility(View.GONE);
        newProcess(new Messagebox.DoubleRunnable() {
            String res;
            public void run() {
                Dson dson = getDefaultDataRaw();
                dson.set("LicensePlate",find(R.id.txtSearch, EditText.class).getText().toString());
                res =  postHttpRaw("RPM/RPM_OnlineSearch", dson );
                autoToken(res);
            }

            public void runUI() {

                Dson dson = Dson.readJson(res);
                if (dson.size()>=1 && dson.get(0).get("ResponseCode").asString().equalsIgnoreCase("00")) {
                    nListArray.asArray().clear();
                    for (int i = 0; i < dson.size(); i++) {
                        nListArray.add(dson.get(i));
                    }
                    //find(R.id.rview, RecyclerView.class).getAdapter().notifyDataSetChanged();
                    final int position = 0;

                    findViewById(R.id.detail).setVisibility(View.VISIBLE);

                    SimpleDateFormat fdt = new SimpleDateFormat("d MMM yyyy", Locale.US);
                    DateFormat inputfdt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                    Locale localeID = new Locale("in", "ID");
                    NumberFormat separator = NumberFormat.getInstance(localeID);

                    //setSetting("tagMap", ndet.get("Address").get(0).toJson());
                    setSetting("almt", nListArray.get(position).toJson());

                    findView(find(R.id.personalSection), R.id.address1, TextView.class).setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            Dson ndet = Dson.readJson(getSetting("almt"));
                            int pos = findAddress(ndet.get("Address"), "Residence");
                            showInfoDialog(ndet.get("Address").get(pos).get("Address").asString(), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                        }
                    });

                    findView(find(R.id.personalSection), R.id.address2, TextView.class).setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            Dson ndet = Dson.readJson(getSetting("DtlDetail"));
                            int pos = findAddress(ndet.get("Address"), "Legal");
                            showInfoDialog(ndet.get("Address").get(pos).get("Address").asString(), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                        }
                    });

                    findView(find(R.id.personalSection), R.id.address3, TextView.class).setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            Dson ndet = Dson.readJson(getSetting("DtlDetail"));
                            int pos = findAddress(ndet.get("Address"), "Company");
                            showInfoDialog(ndet.get("Address").get(pos).get("Address").asString(), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                        }
                    });

                    try {
                        String bdob = fdt.format(inputfdt.parse(nListArray.get(position).get("BirthofDate").asString()));
                        String pdob = String.format(nListArray.get(position).get("BirthPlace").asString()+", "+bdob);
                        findView(find(R.id.personalSection), R.id.dob, TextView.class).setText(pdob);
                        findView(find(R.id.creditSection), R.id.nplDate, TextView.class).setText(fdt.format(inputfdt.parse(nListArray.get(position).get("NPLDate").asString())));
                        findView(find(R.id.collectionSection), R.id.nplDate, TextView.class).setText(fdt.format(inputfdt.parse(nListArray.get(position).get("NPLDate").asString())));
                        findView(find(R.id.collectionSection), R.id.woDate, TextView.class).setText(fdt.format(inputfdt.parse(nListArray.get(position).get("WODate").asString())));
                    } catch (ParseException e) {
                        System.out.println("printing date Exception ==> "+e.toString());
                        e.printStackTrace();

                    }

                    findView(find(R.id.personalSection), R.id.namaCust, TextView.class).setText(nListArray.get(position).get("CustomerFullName").asString());
                    findView(find(R.id.personalSection), R.id.idCust, TextView.class).setText(nListArray.get(position).get("Idno").asString());
                    findView(find(R.id.personalSection), R.id.agama, TextView.class).setText(nListArray.get(position).get("Religion").asString());
                    findView(find(R.id.personalSection), R.id.spouse, TextView.class).setText(nListArray.get(position).get("Spouse_Name").asString());
                    findView(find(R.id.personalSection), R.id.companyName, TextView.class).setText(nListArray.get("CompanyName").asString());
                    findView(find(R.id.personalSection), R.id.emergencyCtc, TextView.class).setText(nListArray.get(position).get("EmergencyContactName").asString());
                    if (nListArray.get(position).get("address").get(0).get("Priority").asString().equalsIgnoreCase("1")) {
                        //setSetting("tagMap", nListArray.get(position).get("Address").get(0).toJson());
                        setSetting("tagMap", nListArray.get(position).get("Address").get(0).get("LatLong").toString());
                        findView(find(R.id.personalSection), R.id.address, TextView.class).setText(nListArray.get(position).get("Address").get(0).get("Address").asString());
                    };

                    findView(find(R.id.creditSection), R.id.noAggr, TextView.class).setText(nListArray.get(position).get("AgreementNo").asString());
                    findView(find(R.id.creditSection), R.id.licensePlate, TextView.class).setText(nListArray.get(position).get("LicensePlate").asString());
                    findView(find(R.id.creditSection), R.id.tenor, TextView.class).setText(nListArray.get(position).get("Tenor").asString());
                    findView(find(R.id.creditSection), R.id.model, TextView.class).setText(nListArray.get(position).get("AssetDescription").asString());
                    findView(find(R.id.creditSection), R.id.chassisNo, TextView.class).setText(nListArray.get(position).get("SerialNo1").asString());
                    findView(find(R.id.creditSection), R.id.engineNo, TextView.class).setText(nListArray.get(position).get("SerialNo2").asString());
                    findView(find(R.id.creditSection), R.id.colour, TextView.class).setText(nListArray.get(position).get("Colour").asString());
                    findView(find(R.id.creditSection), R.id.ntf, TextView.class).setText(nListArray.get(position).get("ntfCS").asString());
                    findView(find(R.id.creditSection), R.id.marketPrice, TextView.class).setText(nListArray.get(position).get("priceCS").asString());

                    /*if (nListArray.get(position).get("ContactInformation").get(0).get("Priority").asString().equalsIgnoreCase("1")
                    ) {
                        findView(find(R.id.contactSection), R.id.contactInformation, TextView.class).setText(nListArray.get(position).get("ContactInformation").get(0).get("Information").asString());
                    };
                    if (nListArray.get(position).get("ContactInformation").get(0).get("Category").asString().equalsIgnoreCase("Phone")
                    ) {
                        findView(find(R.id.contactSection), R.id.phoneprimary2, TextView.class).setText(nListArray.get(position).get("ContactInformation").get(0).get("Information").asString());
                    };*/
                    final int mphone = findContact(nListArray.get(position).get("ContactInformation"), "MobilePhone");
                    findView(find(R.id.contactSection), R.id.phone, TextView.class).setText(nListArray.get(position).get("ContactInformation").get(mphone).get("Information").asString());
                    findView(find(R.id.contactSection), R.id.phone, TextView.class).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            call(nListArray.get(position).get("ContactInformation").get(mphone).get("Information").asString());
                        }
                    });

                    final int dtmail = findContact(nListArray.get(position).get("ContactInformation"), "Email");
                    findView(find(R.id.contactSection), R.id.mail, TextView.class).setText(nListArray.get(position).get("ContactInformation").get(dtmail).get("Information").asString());
                    findView(find(R.id.contactSection), R.id.mail, TextView.class).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mailMe(nListArray.get(position).get("ContactInformation").get(dtmail).get("Information").asString(), getSetting("agrno"));
                        }
                    });

                    final int comp = findContact(nListArray.get(position).get("ContactInformation"), "CompanyPhoneNo");
                    findView(find(R.id.contactSection), R.id.companyCtc, TextView.class).setText(nListArray.get(position).get("ContactInformation").get(comp).get("Information").asString());
                    findView(find(R.id.contactSection), R.id.companyCtc, TextView.class).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            call(nListArray.get(position).get("ContactInformation").get(comp).get("Information").asString());

                        }
                    });


                    final int emerg = findContact(nListArray.get(position).get("ContactInformation"), "EmergencyContactPhoneNo");
                    findView(find(R.id.contactSection), R.id.emergencyCtc, TextView.class).setText(nListArray.get(position).get("ContactInformation").get(emerg).get("Information").asString());
                    findView(find(R.id.contactSection), R.id.emergencyCtc, TextView.class).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            call(nListArray.get(position).get("ContactInformation").get(emerg).get("Information").asString());
                        }
                    });

                    final int spouse = findContact(nListArray.get(position).get("ContactInformation"), "Spouse_MobilePhoneNo");
                    findView(find(R.id.contactSection), R.id.spouseCtc, TextView.class).setText(nListArray.get(position).get("ContactInformation").get(spouse).get("Information").asString());
                    findView(find(R.id.contactSection), R.id.spouseCtc, TextView.class).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            call(nListArray.get(position).get("ContactInformation").get(spouse).get("Information").asString());
                        }
                    });

                    int fb = findContact(nListArray.get(position).get("ContactInformation"), "Facebook");
                    //https://id-id.facebook.com/cut.syfa.33848
                    final String sfb = nListArray.get(position).get("ContactInformation").get(fb).get("Information").asString();
                    Dson nfb  = new Dson(Utility.splitVector(sfb.endsWith("/")?sfb.substring(0,sfb.length()-1):sfb,"/"));
                    findView(find(R.id.contactSection), R.id.fblink, TextView.class).setText(nfb.get(nfb.size()-1).asString());
                    findView(find(R.id.contactSection), R.id.fblink, TextView.class).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            GoToURL(sfb);
                        }
                    });

                    int twitter = findContact(nListArray.get(position).get("ContactInformation"), "Twitter");
                    final String stwitter = nListArray.get(position).get("ContactInformation").get(twitter).get("Information").asString();
                    Dson ntwitter  = new Dson(Utility.splitVector(stwitter.endsWith("/")?stwitter.substring(0,stwitter.length()-1):stwitter,"/"));
                    findView(find(R.id.contactSection), R.id.twitterlink, TextView.class).setText(ntwitter.get(ntwitter.size()-1).asString());
                    findView(find(R.id.contactSection), R.id.twitterlink, TextView.class).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            GoToURL(stwitter);
                        }
                    });

                    int insta = findContact(nListArray.get(position).get("ContactInformation"), "Instagram");
                    final String sinsta = nListArray.get(position).get("ContactInformation").get(insta).get("Information").asString();
                    Dson ninsta  = new Dson(Utility.splitVector(sinsta.endsWith("/")?sinsta.substring(0,sinsta.length()-1):sinsta,"/"));
                    findView(find(R.id.contactSection), R.id.instalink, TextView.class).setText(ninsta.get(ninsta.size()-1).asString());
                    findView(find(R.id.contactSection), R.id.instalink, TextView.class).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            GoToURL(sinsta);
                        }
                    });

                    //OSPrincipalAmount
                    findView(find(R.id.outstandingSection), R.id.osp, TextView.class).setText(separator.format(nListArray.get(position).get("OSPrincipalAmount").asNumber()));
                    findView(find(R.id.outstandingSection), R.id.osi, TextView.class).setText(separator.format(nListArray.get(position).get("OSInterestAmount").asNumber()));
                    findView(find(R.id.outstandingSection), R.id.lc, TextView.class).setText(separator.format(nListArray.get(position).get("OSLCAmount").asNumber()));
                    findView(find(R.id.outstandingSection), R.id.others, TextView.class).setText(nListArray.get(position).get("AROthers").asString());
                    findView(find(R.id.outstandingSection), R.id.pinalty, TextView.class).setText(nListArray.get(position).get("PrepaymentPenalty").asString());
                    Number tot = (nListArray.get(position).get("OSPrincipalAmount").asInteger()
                            + nListArray.get(position).get("OSInterestAmount").asInteger()
                            + nListArray.get(position).get("OSLCAmount").asInteger()
                            + nListArray.get(position).get("AROthers").asInteger()
                            + nListArray.get(position).get("PrepaymentPenalty").asInteger());
                    findView(find(R.id.outstandingSection), R.id.total, TextView.class).setText(separator.format(tot));

                    findView(find(R.id.collectionSection), R.id.collector, TextView.class).setText(nListArray.get(position).get("CollectorName").asString());
                    findView(find(R.id.collectionSection), R.id.caseCategory, TextView.class).setText(nListArray.get(position).get("CaseCategory").asString());
                    findView(find(R.id.collectionSection), R.id.notes, TextView.class).setText(nListArray.get(position).get("Notes").asString());
                    findView(find(R.id.collectionSection), R.id.ralNo, TextView.class).setText(nListArray.get(position).get("RalNo").asString());

                }else{
                    showError(dson.get("error").asString());
                }

            }
        });
    }

    private int findAddress (Dson ndet , String type){
        for (int i = 0; i < ndet.size(); i++) {
            if (ndet.get(i).get("Type").asString().equalsIgnoreCase(type)){
                return  i;
            }
        }
        return 0;//def
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //Dson ntag = Dson.readJson(getSetting("tagMap"));
        String ntag = new String(getSetting("tagMap"));
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }
        if (!ntag.isEmpty()) {

            String[] latlong = (ntag + ",").split(",");
            double latitude = Double.parseDouble(latlong[0]);
            double longitude = Double.parseDouble(latlong[1]);
            LatLng latLng = new LatLng(latitude, longitude);
            mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
        }else{
            showError(getSetting("tagMap"));
        }

    }
    private int findContact(Dson ndet, String category) {
        for (int i = 0; i < ndet.size(); i++) {
            if (ndet.get(i).get("Category").asString().equalsIgnoreCase(category)
                    && ndet.get(i).get("Priority").asString().equalsIgnoreCase("1")) {
                return i;
            }
        }
        for (int i = 0; i < ndet.size(); i++) {
            if (ndet.get(i).get("Category").asString().equalsIgnoreCase(category)
                    && ndet.get(i).get("Priority").asString().equalsIgnoreCase("0")) {
                return i;
            }
        }
        return -1;//def
    }
}
