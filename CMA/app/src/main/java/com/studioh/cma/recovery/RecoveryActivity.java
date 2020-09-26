package com.studioh.cma.recovery;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.naa.data.Dson;
import com.naa.utils.Messagebox;
import com.studioh.cma.AppActivity;
import com.studioh.cma.R;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class RecoveryActivity extends AppActivity {
    private GoogleMap mMap;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recovery);

        setTitle("F410");
        final Dson ndet = Dson.readJson(getSetting("DtlDetail"));

        String role = getSetting("Access_Level");
        //Strategic and Tactical
        if (!role.equalsIgnoreCase("Operasional")) {
            inquiry(ndet.get("AgreementNo").asString(),
                    ndet.get("ApprovalTypeID").asString());
            //final Dson ninquiry = Dson.readJson(getSetting("setInquiry"));

        }

        //Operasional

        SimpleDateFormat fdt = new SimpleDateFormat("d MMM yyyy", Locale.US);
        SimpleDateFormat fdtime = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.US);
        DateFormat inputfdt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

        Locale localeID = new Locale("in", "ID");
        final NumberFormat separator = NumberFormat.getInstance(localeID);
        //separator.setMaximumFractionDigits(0);

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
        findView(find(R.id.creditSection), R.id.chassisNo, TextView.class).setText(ndet.get("SerialNo2").asString());
        findView(find(R.id.creditSection), R.id.colour, TextView.class).setText(ndet.get("Colour").asString());
        findView(find(R.id.creditSection), R.id.ntf, TextView.class).setText(separator.format(ndet.get("NTFAmount").asNumber()));
        findView(find(R.id.creditSection), R.id.marketPrice, TextView.class).setText(separator.format(ndet.get("MarketPrice").asNumber()));
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
        //String stot = separator.format(tot) +",-";
        findView(find(R.id.outstandingSection), R.id.total, TextView.class).setText(separator.format(ndet.get("amounttobepaid").asNumber()));

        //aproval section
        findView(find(R.id.recoApprSection), R.id.TransactionType, TextView.class).setText(ndet.get("HeaderF410List").asString());
        findView(find(R.id.recoApprSection), R.id.payment, TextView.class).setText(separator.format(ndet.get("Paymentf410").asNumber()));
        Double wpct = ndet.get("amountf410").asDouble() * 100 / ndet.get("amounttobepaid").asDouble();
        String sPaymentType = null;
        String sWaivedPct;
        if (ndet.get("ApprovalTypeID").asString().equalsIgnoreCase("COLLECTIONFEE")) {
            //paymentType
            sPaymentType = "Collection Fee";
            sWaivedPct = "";
        } else {
            sPaymentType = "Waived";
            sWaivedPct = " (" + String.format(localeID, "%.0f", wpct) + " %)";
        }
        findView(find(R.id.recoApprSection), R.id.paymentType, TextView.class).setText(sPaymentType);
        findView(find(R.id.recoApprSection), R.id.waived, TextView.class).setText(separator.format(ndet.get("amountf410").asNumber())
                + sWaivedPct);
        findView(find(R.id.recoApprSection), R.id.ApprovalBy, TextView.class).setText(ndet.get("ApprovalBy").asString());

        try {
            findView(find(R.id.recoApprSection), R.id.ApprovalDate, TextView.class).setText(fdtime.format(inputfdt.parse(ndet.get("ApprovalDate").asString())));
        } catch (Exception e) {
            e.printStackTrace();
        }
        //findView(find(R.id.f410Section), R.id.ApprovalDate, TextView.class).setText(ndet.get("ApprovalDate").asString());

        if (!TextUtils.isEmpty(ndet.get("AgreementDate").asString())) {
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

        try {
            String bdob = fdt.format(inputfdt.parse(ndet.get("BirthofDate").asString()));
            String pdob = String.format(ndet.get("BirthPlace").asString() + ", " + bdob);
            findView(find(R.id.personalSection), R.id.dob, TextView.class).setText(pdob);
            /*findView(find(R.id.creditSection), R.id.nplDate, TextView.class).setText(fdt.format(inputfdt.parse(ndet.get("NPLDate").asString())))*/
            ;
        } catch (ParseException e) {
            System.out.println("printing date Exception ==> " + e.toString());
            e.printStackTrace();

        }

        find(R.id.cardResponses).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Repossess.class);
                startActivityForResult(intent, 1);
            }
        });
        find(R.id.cardFullPaiment).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Full.class);
                startActivityForResult(intent, 1);
            }
        });
        find(R.id.cardPartialPayment).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Partial.class);
                startActivityForResult(intent, 1);
            }
        });
        find(R.id.cardWaived).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Waived.class);
                startActivityForResult(intent, 1);
            }
        });

        findView(find(R.id.personalSection), R.id.address1, TextView.class).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
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

    public void approve(final String sagrno,
                        final String stypeid,
                        final String srectype,
                        final String sprodtype,
                        final String swonpl,
                        final String samt,
                        final String sreqdate,
                        final String sreqby,
                        final String sapproveby,
                        final String sapprovestat,
                        final String sseq,
                        final String snotes) {
        newProcess(new Messagebox.DoubleRunnable() {
            String res;

            public void run() {
                Dson dson = getDefaultDataRaw();
                dson.set("_AgreementNo", sagrno);
                dson.set("_ApprovalTypeID", stypeid);
                dson.set("_RecoveryType", srectype);
                dson.set("_ProductType", sprodtype);
                dson.set("_WO_NPL_Date", swonpl);
                dson.set("_TransactionAmount", samt);
                dson.set("_RequestDate", sreqdate);
                dson.set("_RequestBy", sreqby);
                dson.set("_ApprovalBy", sapproveby);
                dson.set("_ApprovalStatus", sapprovestat);
                dson.set("_Seqno", sseq);
                dson.set("_Notes", snotes);

                res = postHttpRaw("RPM/RPM_F410Approval", dson);
                autoToken(res);
            }

            public void runUI() {
                final Dson dson = Dson.readJson(res);
                if (dson.get("ResponseCode").asString().equalsIgnoreCase("00")) {
                    AddDataLog("ApprovalF410", dson.get("ResponseDescription").asString());
                    showInfoDialog(dson.get("ResponseDescription").asString(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            setResult(RESULT_OK);
                            RecoveryActivity.this.finish();
                            dialog.dismiss();
                        }
                    });
                    //setResult(RESULT_OK);
                } else if (dson.get("ResponseCode").asString().equalsIgnoreCase("99")) {
                    AddDataLog("ApprovalF410", dson.get("ResponseDescription").asString());
                    showInfo(dson.get("ResponseDescription").asString());
                } else {
                    AddDataLog("ApprovalF410", res);
                    showInfo(res);
                }
            }
        });
    }

    public void reject(final String sagrno,
                       final String stypeid,
                       final String srectype,
                       final String sprodtype,
                       final String swonpl,
                       final String samt,
                       final String sreqdate,
                       final String sreqby,
                       final String sapproveby,
                       final String sapprovestat,
                       final String sseq,
                       final String snotes) {
        newProcess(new Messagebox.DoubleRunnable() {
            String res;

            public void run() {
                Dson dson = getDefaultDataRaw();
                dson.set("_AgreementNo", sagrno);
                dson.set("_ApprovalTypeID", stypeid);
                dson.set("_RecoveryType", srectype);
                dson.set("_ProductType", sprodtype);
                dson.set("_WO_NPL_Date", swonpl);
                dson.set("_TransactionAmount", samt);
                dson.set("_RequestDate", sreqdate);
                dson.set("_RequestBy", sreqby);
                dson.set("_ApprovalBy", sapproveby);
                dson.set("_ApprovalStatus", sapprovestat);
                dson.set("_Seqno", sseq);
                dson.set("_Notes", snotes);

                res = postHttpRaw("RPM/RPM_F410Reject", dson);
                autoToken(res);
            }

            public void runUI() {
                final Dson dson = Dson.readJson(res);
                if (dson.get("ResponseCode").asString().equalsIgnoreCase("00")) {
                    AddDataLog("RejectF410", dson.get("ResponseDescription").asString());
                    showInfoDialog(dson.get("ResponseDescription").asString(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            setResult(RESULT_OK);
                            RecoveryActivity.this.finish();
                            dialog.dismiss();
                        }
                    });
                    //setResult(RESULT_OK);
                } else if (dson.get("ResponseCode").asString().equalsIgnoreCase("99")) {
                    AddDataLog("RejectF410", dson.get("ResponseDescription").asString());
                    showInfo(dson.get("ResponseDescription").asString());
                } else {
                    AddDataLog("RejectF410", res);
                    showInfo(res);
                }
            }
        });
    }

    public void inquiry(
            final String sagrno,
            final String stipeid) {
        newProcess(new Messagebox.DoubleRunnable() {
            String res;
            String sinquiry;
            //Dson ninq = Dson.newArray();

            public void run() {
                Dson dson = getDefaultDataRaw();
                dson.set("_AgreementNo", sagrno);
                //result = dson.toJson() +"+"+file.toJson();
                res = postHttpRaw("RPM/RPM_F410Inquiry", dson);
                autoToken(res);
            }

            public void runUI() {
                final Dson dson = Dson.readJson(res);
                if (dson.get("inquiry").size() > 0) {
                    DateFormat inputfdt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                    final SimpleDateFormat fdt = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.US);
                    //ApprovalTypeID

                    for (int i = 0; i < dson.get("inquiry").size(); i++) {
                        if (dson.get("inquiry").get(i).get("ApprovalTypeID").asString().equalsIgnoreCase(stipeid)) {
                            sinquiry = dson.get("inquiry").get(i).toJson() ;
                        }
                    }
                    final Dson ninquiry = Dson.readJson(sinquiry);
                    // vGoToURL("http://api.whatsapp.com/send?phone=" + "6287886722267" + "&text=" + ninquiry.toJson());
                    find(R.id.footer).setVisibility(View.GONE);
                    find(R.id.recoApprSection).setVisibility((View.VISIBLE));
                    //find(R.id.f410HistorySection).setVisibility((View.VISIBLE));
                    findView(find(R.id.recoApprSection), R.id.picRequest, TextView.class).setText(ninquiry.get("RequestBy").asString());
                    try {
                        findView(find(R.id.recoApprSection), R.id.picDate, TextView.class).setText(
                                fdt.format(inputfdt.parse(ninquiry.get("RequestDate").asString()))
                        );
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    findView(find(R.id.recoApprSection), R.id.approve, Button.class).setOnClickListener(new View.OnClickListener() {
                                                                                                        @Override
                                                                                                        public void onClick(View v) {
                                                                                                            if (findView(find(R.id.recoApprSection), R.id.infoInput, EditText.class).getText().toString().length() > 0) {
                                                                                                                approve(sagrno,
                                                                                                                        ninquiry.get("ApprovalTypeID").asString(),
                                                                                                                        ninquiry.get("RecoveryType").asString(),
                                                                                                                        ninquiry.get("ProductType").asString(),
                                                                                                                        ninquiry.get("WO_NPL_Date").asString(),
                                                                                                                        ninquiry.get("TransactionAmount").asString(),
                                                                                                                        ninquiry.get("RequestDate").asString(),
                                                                                                                        ninquiry.get("RequestBy").asString(),
                                                                                                                        ninquiry.get("ApprovalBy").asString(),
                                                                                                                        ninquiry.get("ApprovalStatus").asString(),
                                                                                                                        ninquiry.get("Seqno").asString(),
                                                                                                                        findView(find(R.id.recoApprSection), R.id.infoInput, EditText.class).getText().toString()
                                                                                                                );
                                                                                                            } else {
                                                                                                                findView(find(R.id.recoApprSection), R.id.infoInput, EditText.class).setError("Informasi Diperlukan!");
                                                                                                            }
                                                                                                        }
                                                                                                    }
                    );
                    findView(find(R.id.recoApprSection), R.id.reject, Button.class).setOnClickListener(new View.OnClickListener() {
                                                                                                       @Override
                                                                                                       public void onClick(View v) {
                                                                                                           if (findView(find(R.id.recoApprSection), R.id.infoInput, EditText.class).getText().toString().length() > 0) {
                                                                                                               reject(sagrno,
                                                                                                                       ninquiry.get("ApprovalTypeID").asString(),
                                                                                                                       ninquiry.get("RecoveryType").asString(),
                                                                                                                       ninquiry.get("ProductType").asString(),
                                                                                                                       ninquiry.get("WO_NPL_Date").asString(),
                                                                                                                       ninquiry.get("TransactionAmount").asString(),
                                                                                                                       ninquiry.get("RequestDate").asString(),
                                                                                                                       ninquiry.get("RequestBy").asString(),
                                                                                                                       ninquiry.get("ApprovalBy").asString(),
                                                                                                                       ninquiry.get("ApprovalStatus").asString(),
                                                                                                                       ninquiry.get("Seqno").asString(),
                                                                                                                       findView(find(R.id.recoApprSection), R.id.infoInput, EditText.class).getText().toString()
                                                                                                               );
                                                                                                           } else {
                                                                                                               findView(find(R.id.recoApprSection), R.id.infoInput, EditText.class).setError("Informasi Diperlukan!");
                                                                                                           }
                                                                                                       }
                                                                                                   }
                    );
                } else if (dson.get("ResponseCode").asString().equalsIgnoreCase("99")) {
                    AddDataLog("InquiryF410", dson.get("ResponseDescription").asString());
                    showInfoDialog(dson.get("ResponseDescription").asString(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            RecoveryActivity.this.finish();
                            dialog.dismiss();
                        }
                    });
                } else {
                    AddDataLog("InquiryF410", res);
                    showInfoDialog(res, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            RecoveryActivity.this.finish();
                            dialog.dismiss();
                        }
                    });
                }
            }
        });

    }
}
