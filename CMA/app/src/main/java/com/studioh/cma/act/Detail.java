package com.studioh.cma.act;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.naa.data.Dson;
import com.naa.data.Utility;
import com.studioh.cma.AppActivity;
import com.studioh.cma.R;
import com.studioh.cma.infoupd.InfoUpd;
import com.studioh.cma.recovery.RecoveryActivity;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class Detail extends AppActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private ConstraintLayout mContainerAll;
    private boolean isOpen;
    private Animation mFabOpenAnim, mFabCloseAnim;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_detail);

        setTitle("Account Detail");

        //CompanyName

        Locale localeID = new Locale("in", "ID");
        NumberFormat separator = NumberFormat.getInstance(localeID);
        //separator.setMaximumFractionDigits(0);
        //NumberFormat separator = new DecimalFormat("###.###.###");
        //separator.setMaximumFractionDigits(0);
        //double myNumber = 1000000;
        //String formattedNumber = separator.format(myNumber);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        final Dson ndet = Dson.readJson(getSetting("DtlDetail"));

        SimpleDateFormat fdt = new SimpleDateFormat("d MMM yyyy", Locale.US);
        DateFormat inputfdt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

        if(!TextUtils.isEmpty(ndet.get("AgreementDate").asString())){
            try {
                findView(find(R.id.creditSection), R.id.nplDate, TextView.class).setText(fdt.format(inputfdt.parse(ndet.get("AgreementDate").asString())));

            } catch (Exception e) {
                e.printStackTrace();

            }
        } else {
            try {
                findView(find(R.id.creditSection), R.id.nplDate, TextView.class).setText(fdt.format(inputfdt.parse(ndet.get("GoliveDate").asString())));

            } catch (Exception e) {
                e.printStackTrace();

            }
        }
        fab();

        try {
            String bdob = fdt.format(inputfdt.parse(ndet.get("BirthofDate").asString()));
            String pdob = String.format(ndet.get("BirthPlace").asString() + ", " + bdob);
            findView(find(R.id.personalSection), R.id.dob, TextView.class).setText(pdob);
            findView(find(R.id.collectionSection), R.id.nplDate, TextView.class).setText(fdt.format(inputfdt.parse(ndet.get("NPLDate").asString())));
            findView(find(R.id.collectionSection), R.id.woDate, TextView.class).setText(fdt.format(inputfdt.parse(ndet.get("WODate").asString())));
        } catch (ParseException e) {
            System.out.println("printing date Exception ==> " + e.toString());
            e.printStackTrace();
        }

        findView(find(R.id.personalSection), R.id.namaCust, TextView.class).setText(ndet.get("CustomerFullName").asString());
        findView(find(R.id.personalSection), R.id.idCust, TextView.class).setText(ndet.get("Idno").asString());
        findView(find(R.id.personalSection), R.id.agama, TextView.class).setText(ndet.get("Religion").asString());
        findView(find(R.id.personalSection), R.id.spouse, TextView.class).setText(ndet.get("Spouse_Name").asString());
        findView(find(R.id.personalSection), R.id.companyName, TextView.class).setText(ndet.get("CompanyName").asString());
        findView(find(R.id.personalSection), R.id.emergencyCtc, TextView.class).setText(ndet.get("EmergencyContactName").asString());
        for (int i = 0; i < ndet.get("Address").size(); i++) {
            if (ndet.get("Address").get(i).get("Priority").asString().equalsIgnoreCase("1")) {
                setSetting("tagMap", ndet.get("Address").get(i).get("LatLong").toString());
                findView(find(R.id.personalSection), R.id.address, TextView.class).setText(ndet.get("Address").get(i).get("Address").asString());
            }
        };

        findView(find(R.id.creditSection), R.id.noAggr, TextView.class).setText(ndet.get("AgreementNo").asString());
        findView(find(R.id.creditSection), R.id.licensePlate, TextView.class).setText(ndet.get("LicensePlate").asString());
        findView(find(R.id.creditSection), R.id.tenor, TextView.class).setText(ndet.get("Tenor").asString());
        findView(find(R.id.creditSection), R.id.model, TextView.class).setText(ndet.get("AssetDescription").asString());
        findView(find(R.id.creditSection), R.id.chassisNo, TextView.class).setText(ndet.get("SerialNo1").asString());
        findView(find(R.id.creditSection), R.id.engineNo, TextView.class).setText(ndet.get("SerialNo2").asString());
        String priority = ndet.get("AccountGroup").asString();
        /*
         * 1 Green
         * 2 Yellow
         * 3 Red
         * 0 Grey*/
        findView(find(R.id.creditSection), R.id.prio, TextView.class).setText(ndet.get("is_priority").asString());
        switch (priority) {
            case "Green":
                findView(find(R.id.creditSection), R.id.cardPrio, CardView.class).setCardBackgroundColor(Color.parseColor("#00C853"));
                break;
            case "Yellow":
                findView(find(R.id.creditSection), R.id.cardPrio, CardView.class).setCardBackgroundColor(Color.parseColor("#FFD600"));
                //#daysOverDue
               break;
            case "Red":
                findView(find(R.id.creditSection), R.id.cardPrio, CardView.class).setCardBackgroundColor(Color.parseColor("#B71C1C"));
                break;
            default:
                findView(find(R.id.creditSection), R.id.cardPrio, CardView.class).setCardBackgroundColor(Color.parseColor("#546E7A"));
                findView(find(R.id.creditSection), R.id.cardZero, CardView.class).setVisibility(View.VISIBLE);
                break;
        }
        /*if (ndet.get("daysOverDue").asInteger() >= 200) {
            findView(find(R.id.creditSection), R.id.scoreColorCS, CardView.class).setCardBackgroundColor(Color.parseColor("#D32F2F"));
            //"#D32F2F"
        }*/
        ;
        findView(find(R.id.creditSection), R.id.colour, TextView.class).setText(ndet.get("Colour").asString());
        findView(find(R.id.creditSection), R.id.ntf, TextView.class).setText(separator.format(ndet.get("NTFAmount").asNumber()));
        findView(find(R.id.creditSection), R.id.marketPrice, TextView.class).setText(separator.format(ndet.get("MarketPrice").asNumber()));


  /*      if (ndet.get("ContactInformation").get(0).get("Category").asString().equalsIgnoreCase("MobilePhone")
                && ndet.get("ContactInformation").get(0).get("Priority").asString().equalsIgnoreCase("1")
        ) {
            findView(find(R.id.contactSection), R.id.contactInformation, TextView.class).setText(ndet.get("ContactInformation").get(0).get("Information").asString());
        }else if (ndet.get("Contac tInformation").get(0).get("Category").asString().equalsIgnoreCase("MobilePhone")){
            findView(find(R.id.contactSection), R.id.contactInformation, TextView.class).setText(ndet.get("ContactInformation").get(0).get("Information").asString());
        }*/

        //findAddress(ndet.get("Address"), "Company");
        //phone1

        final int mphone = findContact(ndet.get("ContactInformation"), "MobilePhone");
        findView(find(R.id.contactSection), R.id.phone, TextView.class).setText(ndet.get("ContactInformation").get(mphone).get("Information").asString());
        findView(find(R.id.contactSection), R.id.phone, TextView.class).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                call(ndet.get("ContactInformation").get(mphone).get("Information").asString());
            }
        });

        final int dtmail = findContact(ndet.get("ContactInformation"), "Email");
        findView(find(R.id.contactSection), R.id.mail, TextView.class).setText(ndet.get("ContactInformation").get(dtmail).get("Information").asString());
        findView(find(R.id.contactSection), R.id.mail, TextView.class).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mailMe(ndet.get("ContactInformation").get(dtmail).get("Information").asString(), getSetting("agrno"));
            }
        });

        final int comp = findContact(ndet.get("ContactInformation"), "CompanyPhoneNo");
        findView(find(R.id.contactSection), R.id.companyCtc, TextView.class).setText(ndet.get("ContactInformation").get(comp).get("Information").asString());
        findView(find(R.id.contactSection), R.id.companyCtc, TextView.class).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call(ndet.get("ContactInformation").get(comp).get("Information").asString());

            }
        });


        final int emerg = findContact(ndet.get("ContactInformation"), "EmergencyContactPhoneNo");
        findView(find(R.id.contactSection), R.id.emergencyCtc, TextView.class).setText(ndet.get("ContactInformation").get(emerg).get("Information").asString());
        findView(find(R.id.contactSection), R.id.emergencyCtc, TextView.class).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call(ndet.get("ContactInformation").get(emerg).get("Information").asString());
            }
        });

        final int spouse = findContact(ndet.get("ContactInformation"), "Spouse_MobilePhoneNo");
        findView(find(R.id.contactSection), R.id.spouseCtc, TextView.class).setText(ndet.get("ContactInformation").get(spouse).get("Information").asString());
        findView(find(R.id.contactSection), R.id.spouseCtc, TextView.class).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call(ndet.get("ContactInformation").get(spouse).get("Information").asString());
            }
        });

        int fb = findContact(ndet.get("ContactInformation"), "Facebook");
        //https://id-id.facebook.com/cut.syfa.33848
        final String sfb = ndet.get("ContactInformation").get(fb).get("Information").asString();
        Dson n  = new Dson(Utility.splitVector(sfb.endsWith("/")?sfb.substring(0,sfb.length()-1):sfb,"/"));
        findView(find(R.id.contactSection), R.id.fblink, TextView.class).setText("/"+n.get(n.size()-1).asString());
        findView(find(R.id.contactSection), R.id.fblink, TextView.class).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoToURL(sfb);
            }
        });

        int twitter = findContact(ndet.get("ContactInformation"), "Twitter");
        final String stwitter = ndet.get("ContactInformation").get(twitter).get("Information").asString();
        Dson o  = new Dson(Utility.splitVector(stwitter.endsWith("/")?stwitter.substring(0,stwitter.length()-1):stwitter,"/"));
        findView(find(R.id.contactSection), R.id.twitterlink, TextView.class).setText("@"+o.get(n.size()-1).asString());
        findView(find(R.id.contactSection), R.id.twitterlink, TextView.class).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoToURL(stwitter);
            }
        });

        int insta = findContact(ndet.get("ContactInformation"), "Instagram");
        final String sinsta = ndet.get("ContactInformation").get(insta).get("Information").asString();
        Dson p  = new Dson(Utility.splitVector(sinsta.endsWith("/")?sinsta.substring(0,sinsta.length()-1):sinsta,"/"));
        findView(find(R.id.contactSection), R.id.instalink, TextView.class).setText("@"+p.get(n.size()-1).asString());
        findView(find(R.id.contactSection), R.id.instalink, TextView.class).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoToURL(sinsta);
            }
        });

        //OSPrincipalAmount
        findView(find(R.id.outstandingSection), R.id.osp, TextView.class).setText(separator.format(ndet.get("OSPrincipalAmount").asNumber()));
        findView(find(R.id.outstandingSection), R.id.osi, TextView.class).setText(separator.format(ndet.get("OSInterestAmount").asNumber()));
        findView(find(R.id.outstandingSection), R.id.installmentDue, TextView.class).setText(separator.format(ndet.get("installmentDue").asNumber()));
        findView(find(R.id.outstandingSection), R.id.accruedInterest, TextView.class).setText(separator.format(ndet.get("AccruedInterest").asNumber()));
        findView(find(R.id.outstandingSection), R.id.lc, TextView.class).setText(separator.format(ndet.get("OSLCAmount").asNumber()));
        findView(find(R.id.outstandingSection), R.id.others, TextView.class).setText(separator.format(ndet.get("AROthers").asNumber()));
        findView(find(R.id.outstandingSection), R.id.pinalty, TextView.class).setText(separator.format(ndet.get("PrepaymentPenalty").asNumber()));
        Number tot = (ndet.get("OSPrincipalAmount").asInteger()
                + ndet.get("OSInterestAmount").asInteger()
                + ndet.get("installmentDue").asInteger()
                + ndet.get("AccruedInterest").asInteger()
                + ndet.get("OSLCAmount").asInteger()
                + ndet.get("AROthers").asInteger()
                + ndet.get("PrepaymentPenalty").asInteger());
        findView(find(R.id.outstandingSection), R.id.total, TextView.class).setText(separator.format(tot));

        findView(find(R.id.collectionSection), R.id.collector, TextView.class).setText(ndet.get("CollectorName").asString());
        findView(find(R.id.collectionSection), R.id.caseCategory, TextView.class).setText(ndet.get("CaseCategory").asString());
        findView(find(R.id.collectionSection), R.id.notes, TextView.class).setText(ndet.get("Notes").asString());
        findView(find(R.id.collectionSection), R.id.ralNo, TextView.class).setText(ndet.get("RalNo").asString());

        find(R.id.locate).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String ggl = new String("google.navigation:q=");
                String tujuan = new String(ggl + getSetting("tagMap"));
                Uri gmmIntentUri = Uri.parse(tujuan);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });
        find(R.id.report).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Dson ndet = Dson.readJson(getSetting("DtlDetail"));
                setSetting("AgreementNo",ndet.get("AgreementNo").asString() );
                BHCActivity.startBHC(getActivity(), Dson.newObject(), 1);
            }
        });
        find(R.id.updinfo).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), InfoUpd.class);
                startActivity(intent);
            }
        });
        find(R.id.reco).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RecoveryActivity.class);
                startActivity(intent);
            }
        });

        findView(find(R.id.personalSection), R.id.address1, TextView.class).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Dson ndet = Dson.readJson(getSetting("DtlDetail"));
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
    }

    private int findAddress(Dson ndet, String type) {
        for (int i = 0; i < ndet.size(); i++) {
            if (ndet.get(i).get("Type").asString().equalsIgnoreCase(type)) {
                return i;
            }
        }
        return 0;//def
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
        } else {
            showError(getSetting("tagMap"));
        }
    }
    public void fab() {
        mContainerAll = findView(R.id.action, R.id.container_all, ConstraintLayout.class);
        FloatingActionButton mMainFab = findView(R.id.action, R.id.main_fab, FloatingActionButton.class);
        FloatingActionButton mLocateFab = findView(R.id.action, R.id.locate, FloatingActionButton.class);
        FloatingActionButton mReportFab = findView(R.id.action, R.id.report, FloatingActionButton.class);
        FloatingActionButton mInfoUpdFab = findView(R.id.action, R.id.updinfo, FloatingActionButton.class);
        FloatingActionButton mRecoveryFab = findView(R.id.action, R.id.reco, FloatingActionButton.class);

        TextView mLocateTxt = findView(R.id.action, R.id.locateTxt, TextView.class);
        TextView mReportTxt = findView(R.id.action, R.id.reportTxt, TextView.class);
        TextView mInfoUpdTxt = findView(R.id.action, R.id.infoUpdTxt, TextView.class);
        TextView mRecoveryTxt = findView(R.id.action, R.id.recoTxt, TextView.class);

        mFabOpenAnim = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        mFabCloseAnim = AnimationUtils.loadAnimation(this, R.anim.fab_close);

        mContainerAll.setVisibility(View.INVISIBLE);
        isOpen = false;

        mMainFab.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {
                if(isOpen){
                    mContainerAll.startAnimation(mFabCloseAnim);
                    isOpen = false;
                } else {
                    mContainerAll.startAnimation(mFabOpenAnim);
                    isOpen = true;
                }
            }
        });
    }

}
