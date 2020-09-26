package com.studioh.cma.fav;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.naa.data.Dson;
import com.naa.data.Utility;
import com.naa.utils.Messagebox;
import com.studioh.cma.AppActivity;
import com.studioh.cma.R;
import com.studioh.cma.main.MainActivity;
import com.studioh.cma.recovery.Full;
import com.studioh.cma.recovery.Partial;
import com.studioh.cma.recovery.Repossess;
import com.studioh.cma.recovery.Waived;

import java.io.File;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class RecoveryDtlActivity extends AppActivity {
    private GoogleMap mMap;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recovery);

        setTitle("Recovery");

        find(R.id.footer).setVisibility(View.GONE);
        find(R.id.recoverySection).setVisibility((View.VISIBLE));
        find(R.id.personalSection).setVisibility((View.GONE));
        find(R.id.creditSection).setVisibility((View.GONE));
        find(R.id.outstandingSection).setVisibility((View.GONE));

        final String transactionType = getIntentStringExtra("RecoveryType");
        SimpleDateFormat fdt = new SimpleDateFormat("d MMM yyyy", Locale.US);
        SimpleDateFormat fdtime = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.US);
        DateFormat inputfdt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

        Locale localeID = new Locale("in", "ID");
        final NumberFormat separator = NumberFormat.getInstance(localeID);
        //separator.setMaximumFractionDigits(0);

        final Dson ndet = Dson.readJson(getSetting("DtlRecovery"));

        findView(find(R.id.personalSection), R.id.namaCust, TextView.class).setText(ndet.get("CustomerFullName").asString());
        findView(find(R.id.personalSection), R.id.idCust, TextView.class).setText(ndet.get("Idno").asString());
        findView(find(R.id.personalSection), R.id.agama, TextView.class).setText(ndet.get("Religion").asString());
        findView(find(R.id.personalSection), R.id.spouse, TextView.class).setText(ndet.get("Spouse_Name").asString());
        findView(find(R.id.personalSection), R.id.companyName, TextView.class).setText(ndet.get("CompanyName").asString());
        findView(find(R.id.personalSection), R.id.emergencyCtc, TextView.class).setText(ndet.get("EmergencyContactName").asString());
        if (ndet.get("address").get(0).get("Priority").asString().equalsIgnoreCase("1")) {
            //setSetting("tagMap", ndet.get("Address").get(0).toJson());
            setSetting("tagMap", ndet.get("Address").get(0).get("LatLong").toString());
            findView(find(R.id.personalSection), R.id.address, TextView.class).setText(ndet.get("Address").get(0).get("Address").asString());
        }
        ;

        findView(find(R.id.creditSection), R.id.noAggr, TextView.class).setText(ndet.get("AgreementNo").asString());
        findView(find(R.id.creditSection), R.id.licensePlate, TextView.class).setText(ndet.get("LicensePlate").asString());
        findView(find(R.id.creditSection), R.id.tenor, TextView.class).setText(ndet.get("Tenor").asString());
        findView(find(R.id.creditSection), R.id.model, TextView.class).setText(ndet.get("AssetDescription").asString());
        findView(find(R.id.creditSection), R.id.chassisNo, TextView.class).setText(ndet.get("SerialNo1").asString());
        findView(find(R.id.creditSection), R.id.engineNo, TextView.class).setText(ndet.get("SerialNo2").asString());
        findView(find(R.id.creditSection), R.id.colour, TextView.class).setText(ndet.get("Colour").asString());
        findView(find(R.id.creditSection), R.id.ntf, TextView.class).setText(separator.format(ndet.get("NTFAmount").asNumber()));
        findView(find(R.id.creditSection), R.id.marketPrice, TextView.class).setText(separator.format(ndet.get("MarketPrice").asNumber()));


        findView(find(R.id.outstandingSection), R.id.osp, TextView.class).setText(separator.format(ndet.get("OSPrincipalAmount").asNumber()));
        findView(find(R.id.outstandingSection), R.id.osi, TextView.class).setText(separator.format(ndet.get("OSInterestAmount").asNumber()));
        findView(find(R.id.outstandingSection), R.id.lc, TextView.class).setText(separator.format(ndet.get("OSLCAmount").asNumber()));
        findView(find(R.id.outstandingSection), R.id.others, TextView.class).setText(ndet.get("AROthers").asString());
        findView(find(R.id.outstandingSection), R.id.pinalty, TextView.class).setText(ndet.get("PrepaymentPenalty").asString());
        Number tot = (ndet.get("OSPrincipalAmount").asInteger()
                + ndet.get("OSInterestAmount").asInteger()
                + ndet.get("OSLCAmount").asInteger()
                + ndet.get("AROthers").asInteger()
                + ndet.get("PrepaymentPenalty").asInteger());
        //String stot = separator.format(tot) +",-";
        findView(find(R.id.outstandingSection), R.id.total, TextView.class).setText(separator.format(tot));

        try {
            String bdob = fdt.format(inputfdt.parse(ndet.get("BirthofDate").asString()));
            String pdob = String.format(ndet.get("BirthPlace").asString() + ", " + bdob);
            findView(find(R.id.personalSection), R.id.bod, TextView.class).setText(pdob);
            findView(find(R.id.creditSection), R.id.nplDate, TextView.class).setText(fdt.format(inputfdt.parse(ndet.get("NPLDate").asString())));
        } catch (ParseException e) {
            System.out.println("printing date Exception ==> " + e.toString());
            e.printStackTrace();

        }

        //Visible to Approver status != Approved
        findView(find(R.id.recoApprSection), R.id.TransactionType, TextView.class).setText(ndet.get("HeaderF410List").asString());
        /*findView(find(R.id.f410Section), R.id.ApprovalTypeID, TextView.class).setText(ndet.get("ApprovalTypeID").asString());
        findView(find(R.id.f410Section), R.id.RecoveryType, TextView.class).setText(ndet.get("RecoveryType").asString());*/
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

        //Visible to Collector status = Approved
        findView(find(R.id.recoverySection), R.id.customerFullName, TextView.class).setText(ndet.get("CustomerName").asString());
        findView(find(R.id.recoverySection), R.id.agreementNo, TextView.class).setText(ndet.get("AgreementNo").asString());
        findView(find(R.id.recoverySection), R.id.LicensePlate, TextView.class).setText(ndet.get("LicensePlate").asString());
        findView(find(R.id.recoverySection), R.id.PIC, TextView.class).setText(ndet.get("PIC").asString());
        findView(find(R.id.recoverySection), R.id.HandlingBy, TextView.class).setText(ndet.get("HandlingBy").asString());
        findView(find(R.id.recoverySection), R.id.TransactionType, TextView.class).setText(ndet.get("ThirdPartyFee").asString());
        findView(find(R.id.recoverySection), R.id.ApprovalBy, TextView.class).setText(ndet.get("ApprovalBy").asString());
        try {
            findView(find(R.id.recoverySection), R.id.ApprovalDate, TextView.class).setText(fdtime.format(inputfdt.parse(ndet.get("ApprovalDate").asString())));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        findView(find(R.id.recoverySection), R.id.Notes, TextView.class).setText(ndet.get("Notes").asString());

        findView(find(R.id.recoverySection), R.id.imgRal, ImageView.class).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCamera(REQUEST_RAL);
            }
        });
        findView(find(R.id.recoverySection), R.id.imgPermohonan, ImageView.class).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCamera(REQUEST_SURAT);
            }
        });
        findView(find(R.id.recoverySection), R.id.imgAsset, ImageView.class).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCamera(REQUEST_ASSET);
            }
        });
        findView(find(R.id.recoverySection), R.id.imgPelunasan, ImageView.class).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCamera(REQUEST_PELUNASAN);
            }
        });
        findView(find(R.id.recoverySection), R.id.imgStnk, ImageView.class).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCamera(REQUEST_STNK);
            }
        });
        findView(find(R.id.recoverySection), R.id.add0, ImageView.class).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCamera(REQUEST_ADD0);
            }
        });
        findView(find(R.id.recoverySection), R.id.add1, ImageView.class).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCamera(REQUEST_ADD1);
            }
        });
        findView(find(R.id.recoverySection), R.id.add2, ImageView.class).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCamera(REQUEST_ADD2);
            }
        });
        findView(find(R.id.recoverySection), R.id.add3, ImageView.class).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCamera(REQUEST_ADD3);
            }
        });
        findView(find(R.id.recoverySection), R.id.add4, ImageView.class).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCamera(REQUEST_ADD4);
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

        findView(find(R.id.recoverySection), R.id.save, Button.class).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (String.valueOf(findView(find(R.id.recoverySection), R.id.imgRal, ImageView.class).getTag()).equals("null")) {
                    showInfoDialog("Silahkan Take Photo RAL", null);
                } else if (String.valueOf(findView(find(R.id.recoverySection), R.id.imgPermohonan, ImageView.class).getTag()).equals("null")) {
                    showInfoDialog("Silahkan Take Photo Surat Permohonan", null);
                } else if (String.valueOf(findView(find(R.id.recoverySection), R.id.imgAsset, ImageView.class).getTag()).equals("null")) {
                    showInfoDialog("Silahkan Take Photo Asset", null);
                } else if (String.valueOf(findView(find(R.id.recoverySection), R.id.imgPelunasan, ImageView.class).getTag()).equals("null")) {
                    showInfoDialog("Silahkan Take Photo Bukti Pelunasan", null);
                } else if (String.valueOf(findView(find(R.id.recoverySection), R.id.imgStnk, ImageView.class).getTag()).equals("null")) {
                    showInfoDialog("Silahkan Take Photo STNK", null);
                    //Image Optional
                } else if (!String.valueOf(find(R.id.add0, ImageView.class).getTag()).equals("null")
                        && find(R.id.input0, EditText.class).getText().toString().length() == 0) {
                    find(R.id.input0, EditText.class).setError("Informasi Diperlukan!");
                } else if (!String.valueOf(find(R.id.add1, ImageView.class).getTag()).equals("null")
                        && find(R.id.input1, EditText.class).getText().toString().length() == 0) {
                    find(R.id.input1, EditText.class).setError("Informasi Diperlukan!");
                } else if (!String.valueOf(find(R.id.add2, ImageView.class).getTag()).equals("null")
                        && find(R.id.input2, EditText.class).getText().toString().length() == 0) {
                    find(R.id.input2, EditText.class).setError("Informasi Diperlukan!");
                } else if (!String.valueOf(find(R.id.add3, ImageView.class).getTag()).equals("null")
                        && find(R.id.input3, EditText.class).getText().toString().length() == 0) {
                    find(R.id.input3, EditText.class).setError("Informasi Diperlukan!");
                } else {
                    simpan(
                            transactionType,
                            String.valueOf(findView(find(R.id.recoverySection), R.id.imgRal, ImageView.class).getTag()),
                            String.valueOf(findView(find(R.id.recoverySection), R.id.imgPermohonan, ImageView.class).getTag()),
                            String.valueOf(findView(find(R.id.recoverySection), R.id.imgAsset, ImageView.class).getTag()),
                            String.valueOf(findView(find(R.id.recoverySection), R.id.imgPelunasan, ImageView.class).getTag()),
                            String.valueOf(findView(find(R.id.recoverySection), R.id.imgStnk, ImageView.class).getTag()),
                            find(R.id.input0, EditText.class).getText().toString(),
                            String.valueOf(find(R.id.add0, ImageView.class).getTag()),
                            find(R.id.input1, EditText.class).getText().toString(),
                            String.valueOf(find(R.id.add1, ImageView.class).getTag()),
                            find(R.id.input2, EditText.class).getText().toString(),
                            String.valueOf(find(R.id.add2, ImageView.class).getTag()),
                            find(R.id.input3, EditText.class).getText().toString(),
                            String.valueOf(find(R.id.add3, ImageView.class).getTag()),
                            find(R.id.input4, EditText.class).getText().toString(),
                            String.valueOf(find(R.id.add4, ImageView.class).getTag())
                    );
                }
            }
        });

    }

    public void simpan(final String recoverytype,
                       final String imgral,
                       final String imgsurat,
                       final String imgasset,
                       final String imgpelunasan,
                       final String imgstnk,
                       final String txt0,
                       final String add0,
                       final String txt1,
                       final String add1,
                       final String txt2,
                       final String add2,
                       final String txt3,
                       final String add3,
                       final String txt4,
                       final String add4) {
        newProcess(new Messagebox.DoubleRunnable() {
            String res;
            String sagrno = getSetting("agrno");

            //String result;
            public void run() {
                Dson dson = getDefaultDataRaw();
                dson.set("AgreementNo", sagrno);
                dson.set("LicensePlate", getSetting("plate"));
                dson.set("RecoveryType", recoverytype);
                dson.set("Image[0][description]", txt0);
                dson.set("Image[1][description]", txt1);
                dson.set("Image[2][description]", txt2);
                dson.set("Image[3][description]", txt3);
                dson.set("Image[4][description]", txt4);

                Dson file = Dson.newObject();
                file.set("RAL", imgral);
                file.set("SuratPermohonan", imgsurat);
                file.set("FotoAsset", imgasset);
                file.set("BuktiPelunasan", imgpelunasan);
                file.set("STNK", imgstnk);
                file.set("Image[0][path]", add0);
                file.set("Image[1][path]", add1);
                file.set("Image[2][path]", add2);
                file.set("Image[3][path]", add3);
                file.set("Image[4][path]", add4);

                //result = dson.toJson() +"+"+file.toJson();
                res = postHttpRaw("RPM/RPM_Recovery", dson, file);
                autoToken(res);
            }

            public void runUI() {

                Dson dson = Dson.readJson(res);
                if (dson.get("ResponseCode").asString().equalsIgnoreCase("00")) {
                    AddDataLog("Recovery", dson.get("ResponseDescription").asString());
                    showInfoDialog(dson.get("ResponseDescription").asString(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                                    /*Responses.this.finish();
                                    dialog.dismiss();*/
                            Dson n = Dson.newObject();
                            n.set("flagRecovery", "1");
                            n.set("aggrNo", sagrno);
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            intent.putExtra("flag", n.toString());
                            startActivity(intent);
                            finish();
                        }
                    });
                    //setResult(RESULT_OK);
                } else if (dson.get("ResponseCode").asString().equalsIgnoreCase("99")) {
                    AddDataLog("Recovery", dson.get("ResponseDescription").asString());
                    showInfo(dson.get("ResponseDescription").asString());
                } else {
                    AddDataLog("Recovery", res);
                    showInfo(res);
                }
            }
        });
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
                            RecoveryDtlActivity.this.finish();
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
                            RecoveryDtlActivity.this.finish();
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
                            RecoveryDtlActivity.this.finish();
                            dialog.dismiss();
                        }
                    });
                } else {
                    AddDataLog("InquiryF410", res);
                    showInfoDialog(res, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            RecoveryDtlActivity.this.finish();
                            dialog.dismiss();
                        }
                    });
                }
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

    public void showCamera(int request) {
        Log.i("showCamera", Thread.currentThread().getName());
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ||
                ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED ||
                ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            return;
        }

        try {
            File photo = new File(Environment.getExternalStorageDirectory(), "image");
            photo.delete();
            photo.createNewFile();

            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempFile);
            cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
            getActivity().startActivityForResult(cameraIntent, request);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void getCamera(String name, Intent data, ImageView imageView) {
        File photo = new File(Environment.getExternalStorageDirectory(), "image");
        String fname = Long.toHexString(System.currentTimeMillis()) + "." + Long.toHexString(System.nanoTime()) + "." + System.currentTimeMillis() + ".tmp";


        String pfile = Utility.getCacheDir(name + "_AM");
        Utility.copyFile(photo.getAbsolutePath(), pfile);
        onCompressImage(pfile, 80, 1366, 1366);
        if (isRotate_90(photo.getAbsolutePath())) {
            rotate(pfile, 90);
        }

        //imageView.setImageURI(Uri.fromFile(photo));
        viewImage(imageView, pfile, 256);//photo.getAbsolutePath()
        imageView.setTag(pfile);
        //camera
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_RAL && resultCode == RESULT_OK) {
            getCamera("REQUEST_RAL", data, findView(find(R.id.recoverySection), R.id.imgRal, ImageView.class));
        } else if (requestCode == REQUEST_ASSET && resultCode == RESULT_OK) {
            getCamera("REQUEST_ASSET", data, findView(find(R.id.recoverySection), R.id.imgAsset, ImageView.class));
        } else if (requestCode == REQUEST_PELUNASAN && resultCode == RESULT_OK) {
            getCamera("REQUEST_PELUNASAN", data, findView(find(R.id.recoverySection), R.id.imgPelunasan, ImageView.class));
        } else if (requestCode == REQUEST_STNK && resultCode == RESULT_OK) {
            getCamera("REQUEST_STNK", data, findView(find(R.id.recoverySection), R.id.imgStnk, ImageView.class));
        } else if (requestCode == REQUEST_SURAT && resultCode == RESULT_OK) {
            getCamera("REQUEST_SURAT", data, findView(find(R.id.recoverySection), R.id.imgPermohonan, ImageView.class));
        } else if (requestCode == REQUEST_ADD0 && resultCode == RESULT_OK) {
            getCamera("REQUEST_ADD0", data, find(R.id.add0, ImageView.class));
            find(R.id.txt0).setVisibility(View.GONE);
            find(R.id.input0).setVisibility(View.VISIBLE);
            find(R.id.layout1).setVisibility(View.VISIBLE);
        } else if (requestCode == REQUEST_ADD1 && resultCode == RESULT_OK) {
            getCamera("REQUEST_ADD1", data, find(R.id.add1, ImageView.class));
            find(R.id.txt1).setVisibility(View.GONE);
            find(R.id.input1).setVisibility(View.VISIBLE);
            find(R.id.layout2).setVisibility(View.VISIBLE);
        } else if (requestCode == REQUEST_ADD2 && resultCode == RESULT_OK) {
            getCamera("REQUEST_ADD2", data, find(R.id.add2, ImageView.class));
            find(R.id.txt2, TextView.class).setVisibility(View.GONE);
            find(R.id.input2, EditText.class).setVisibility(View.VISIBLE);
            find(R.id.layout3).setVisibility(View.VISIBLE);
        } else if (requestCode == REQUEST_ADD3 && resultCode == RESULT_OK) {
            getCamera("REQUEST_ADD3", data, find(R.id.add3, ImageView.class));
            find(R.id.txt3).setVisibility(View.GONE);
            find(R.id.input3).setVisibility(View.VISIBLE);
            find(R.id.layout4).setVisibility(View.VISIBLE);
        } else if (requestCode == REQUEST_ADD4 && resultCode == RESULT_OK) {
            getCamera("REQUEST_ADD4", data, find(R.id.add4, ImageView.class));
            find(R.id.txt4).setVisibility(View.GONE);
            find(R.id.input4).setVisibility(View.VISIBLE);
        }
    }

    final int REQUEST_RAL = 203;
    final int REQUEST_SURAT = 207;
    final int REQUEST_ASSET = 208;
    final int REQUEST_PELUNASAN = 209;
    final int REQUEST_STNK = 210;

    final int REQUEST_ADD0 = 300;
    final int REQUEST_ADD1 = 301;
    final int REQUEST_ADD2 = 302;
    final int REQUEST_ADD3 = 303;
    final int REQUEST_ADD4 = 304;
}
