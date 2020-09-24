package com.studioh.cma.recovery;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.naa.data.Dson;
import com.naa.data.Utility;
import com.naa.utils.InternetX;
import com.naa.utils.Messagebox;
import com.studioh.cma.AppActivity;
import com.studioh.cma.AppApplication;
import com.studioh.cma.R;
import com.studioh.cma.main.MainActivity;
import com.studioh.srv.StudiohAutoComplete;
import com.studioh.srv.DsonAutoCompleteAdapter;

import java.io.File;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;

public class Waived extends AppActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waived);

        setTitle("WAIVED");

        Dson n = Dson.readJson("DtlDetail");
        final Locale localeID = new Locale("in", "ID");
        final NumberFormat separator = NumberFormat.getInstance(localeID);

        final Dson ndet = Dson.readJson(getSetting("DtlDetail"));
        DateFormat inputfdt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        final SimpleDateFormat fdy = new SimpleDateFormat("yyyy", Locale.US);

        final String[] recoverytype = new String[1];

        String paramYear = null;
        try {
            paramYear = fdy.format(inputfdt.parse(ndet.get("NPLDate").asString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        //GoToURL("http://api.whatsapp.com/send?phone=" + "6287886722267" + "&text=" + ndet.get("NPLDate").asString()+" - "+ paramYear);
        final int tot = ndet.get("OSPrincipalAmount").asInteger()
                + ndet.get("OSInterestAmount").asInteger()
                + ndet.get("installmentDue").asInteger()
                + ndet.get("AccruedInterest").asInteger()
                + ndet.get("OSLCAmount").asInteger()
                + ndet.get("AROthers").asInteger()
                + ndet.get("PrepaymentPenalty").asInteger();
        final int[] iPrepayment = new int[1];

        currTextFormat(find(R.id.prepayment, EditText.class));

        find(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        final String finalParamYear = paramYear;
        find(R.id.submit).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String approvaltypeid;
                if (Integer.parseInt(find(R.id.ospw, EditText.class).getText().toString().replace(".","")) > ndet.get("OSPrincipalAmount").asInteger()) {
                    find(R.id.ospw, EditText.class).setError("Nilai Melebihi Pokok!");
                } else if (Integer.parseInt(find(R.id.osiw, EditText.class).getText().toString().replace(".","")) > ndet.get("OSInterestAmount").asInteger()) {
                    find(R.id.osiw, EditText.class).setError("Nilai Melebihi Pokok!");
                } else if (Integer.parseInt(find(R.id.lcw, EditText.class).getText().toString().replace(".","")) > ndet.get("OSLCAmount").asInteger()) {
                    find(R.id.lcw, EditText.class).setError("Nilai Melebihi Pokok!");
                } else if (Integer.parseInt(find(R.id.othersw, EditText.class).getText().toString().replace(".","")) > ndet.get("AROthers").asInteger()) {
                    find(R.id.othersw, EditText.class).setError("Nilai Melebihi Pokok!");
                } else if (Integer.parseInt(find(R.id.penaltyw, EditText.class).getText().toString().replace(".","")) > ndet.get("PrepaymentPenalty").asInteger()) {
                    find(R.id.penaltyw, EditText.class).setError("Nilai Melebihi Pokok!");
                } else if (Integer.parseInt(find(R.id.prepayment, EditText.class).getText().toString().replace(".", "")) == 0) {
                    find(R.id.prepayment, EditText.class).setError("Nilai Wajib Di Isi!");
                } else if (String.valueOf(find(R.id.imgKtp, ImageView.class).getTag()).equals("null")) {
                    showInfoDialog("Silahkan Take Photo KTP", null);
                } else if (String.valueOf(find(R.id.imgPermohonan, ImageView.class).getTag()).equals("null")) {
                    showInfoDialog("Silahkan Take Photo Surat Permohonan", null);
                } else{
                    if (tot< iPrepayment[0]){
                        approvaltypeid = "PAYMENTWAIVED";
                    } else {
                        approvaltypeid ="COLLECTIONFEE";
                    };
                    simpan(
                            approvaltypeid,
                            recoverytype[0],
                            //find(R.id.notesInput, EditText.class).getText().toString(),
                            ndet.get("AssetType").asString(),
                            finalParamYear,
                            //allocation
                            find(R.id.ospLoc, EditText.class).getText().toString().replace(".", ""),
                            find(R.id.osiLoc, EditText.class).getText().toString().replace(".", ""),
                            find(R.id.lcLoc, EditText.class).getText().toString().replace(".", ""),
                            find(R.id.othersLoc, EditText.class).getText().toString().replace(".", ""),
                            find(R.id.penaltyLoc, EditText.class).getText().toString().replace(".", ""),
                            find(R.id.totalLoc, EditText.class).getText().toString().replace(".", ""),
                            //waived
                            find(R.id.ospw, EditText.class).getText().toString().replace(".",""),
                            find(R.id.osiw, EditText.class).getText().toString().replace(".",""),
                            find(R.id.lcw, EditText.class).getText().toString().replace(".",""),
                            find(R.id.othersw, EditText.class).getText().toString().replace(".",""),
                            find(R.id.penaltyw, EditText.class).getText().toString().replace(".",""),
                            find(R.id.totalw, EditText.class).getText().toString().replace(".",""),
                            //percentage
                            find(R.id.ospPct, EditText.class).getText().toString().replace(".", ""),
                            find(R.id.osiPct, EditText.class).getText().toString().replace(".", ""),
                            find(R.id.lcPct, EditText.class).getText().toString().replace(".", ""),
                            find(R.id.othersPct, EditText.class).getText().toString().replace(".", ""),
                            find(R.id.penaltyPct, EditText.class).getText().toString().replace(".", ""),
                            find(R.id.totalPct, EditText.class).getText().toString().replace(".", ""),
                            //file
                            find(R.id.prepayment, EditText.class).getText().toString().replace(".",""),
                            String.valueOf(find(R.id.imgKtp, ImageView.class).getTag()),
                            String.valueOf(find(R.id.imgPermohonan, ImageView.class).getTag())
                            //PrepaymentAmount
                    );
                }
            }
        });

        Dson ndtl = Dson.readJson(getSetting("LV"));
        Dson npic = Dson.newObject();
        npic.set("data", ndtl.get("Name").asJson());
        for (int i = 0; i < npic.size(); i++) {
            if (npic.get("data").get(i).size() >= 1){
                nListArray.add(npic.get("data").get(i).asJson());
            }
        }
        //OSLCAmount
        find(R.id.osp, TextView.class).setText(separator.format(ndet.get("OSPrincipalAmount").asNumber()));
        find(R.id.osi, TextView.class).setText(separator.format(ndet.get("OSInterestAmount").asNumber()));
        find(R.id.iDue, TextView.class).setText(separator.format(ndet.get("installmentDue").asNumber()));
        find(R.id.accrInt, TextView.class).setText(separator.format(ndet.get("AccruedInterest").asNumber()));
        find(R.id.lc, TextView.class).setText(separator.format(ndet.get("OSLCAmount").asNumber()));
        find(R.id.others, TextView.class).setText(separator.format(ndet.get("AROthers").asNumber()));
        find(R.id.penalty, TextView.class).setText(separator.format(ndet.get("PrepaymentPenalty").asNumber()));
        //find(R.id.total, TextView.class).setText(separator.format(ndet.get("PrepaymentPenalty").asNumber()));


        /*+ ndet.get("AROthers").asInteger()
        + ndet.get("PenaltyAmount").asInteger()*/;

        find(R.id.total, TextView.class).setText(separator.format(tot));

        find(R.id.prepaymentAmt, TextView.class).setText("RP. " + separator.format(tot));

        //------------------------------------------------------------------------
        find(R.id.prepayment, EditText.class).addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                try {
                    find(R.id.totalLoc, EditText.class).setText(String.valueOf(s));

                    iPrepayment[0] = Integer.parseInt(s.toString().replace(".", ""));

                    Integer totw = tot
                            -
                            Integer.parseInt(s.toString().replace(".", ""));
                    find(R.id.totalw, EditText.class).setText(String.valueOf(separator.format(totw)));

                    double totpct = Double.parseDouble(s.toString().replace(".", "")) * 100 / tot;
                    find(R.id.totalPct, EditText.class).setText(String.format(localeID, "%.0f", totpct));


                    if (Integer.parseInt(s.toString().replace(".", "")) < ndet.get("OSPrincipalAmount").asInteger()) {
                        find(R.id.ospLoc, EditText.class).setText(String.valueOf(s));
                        find(R.id.osiLoc, TextView.class).setText("0");
                        find(R.id.iDueLoc, TextView.class).setText("0");
                        find(R.id.accrIntLoc, TextView.class).setText("0");
                        find(R.id.lcLoc, TextView.class).setText("0");
                        find(R.id.othersLoc, TextView.class).setText("0");
                        find(R.id.penaltyLoc, TextView.class).setText("0");
                        recoverytype[0] = "FP_SPECIAL";

                    } else {
                        if (Integer.parseInt(s.toString().replace(".", ""))
                                - ndet.get("OSPrincipalAmount").asInteger()
                                < ndet.get("OSInterestAmount").asInteger()) {
                            find(R.id.ospLoc, TextView.class).setText(separator.format(ndet.get("OSPrincipalAmount").asNumber()));

                            Integer b = Integer.parseInt(s.toString().replace(".", ""))
                                    - ndet.get("OSPrincipalAmount").asInteger();

                            find(R.id.osiLoc, EditText.class).setText(String.valueOf(separator.format(b)));

                            find(R.id.iDueLoc, TextView.class).setText("0");
                            find(R.id.accrIntLoc, TextView.class).setText("0");
                            find(R.id.lcLoc, TextView.class).setText("0");
                            find(R.id.othersLoc, TextView.class).setText("0");
                            find(R.id.penaltyLoc, TextView.class).setText("0");
                            recoverytype[0] = "FP_SPECIAL";

                        } else {
                            if (Integer.parseInt(s.toString().replace(".", ""))
                                    - ndet.get("OSPrincipalAmount").asInteger()
                                    - ndet.get("OSInterestAmount").asInteger()
                                    < ndet.get("installmentDue").asInteger()) {
                                find(R.id.ospLoc, TextView.class).setText(separator.format(ndet.get("OSPrincipalAmount").asNumber()));
                                find(R.id.osiLoc, TextView.class).setText(separator.format(ndet.get("OSInterestAmount").asNumber()));

                                Integer b = Integer.parseInt(s.toString().replace(".", ""))
                                        - ndet.get("OSPrincipalAmount").asInteger()
                                        - ndet.get("OSInterestAmount").asInteger();
                                find(R.id.iDueLoc, EditText.class).setText(String.valueOf(separator.format(b)));

                                find(R.id.accrIntLoc, TextView.class).setText("0");
                                find(R.id.lcLoc, TextView.class).setText("0");
                                find(R.id.othersLoc, TextView.class).setText("0");
                                find(R.id.penaltyLoc, TextView.class).setText("0");
                                recoverytype[0] = "FP_NORMAL";


                            } else {
                                if (Integer.parseInt(s.toString().replace(".", ""))
                                        - ndet.get("OSPrincipalAmount").asInteger()
                                        - ndet.get("OSInterestAmount").asInteger()
                                        - ndet.get("installmentDue").asInteger()
                                        < ndet.get("AccruedInterest").asInteger()) {
                                    find(R.id.ospLoc, TextView.class).setText(separator.format(ndet.get("OSPrincipalAmount").asNumber()));
                                    find(R.id.osiLoc, TextView.class).setText(separator.format(ndet.get("OSInterestAmount").asNumber()));
                                    find(R.id.iDueLoc, TextView.class).setText(separator.format(ndet.get("installmentDue").asNumber()));

                                    Integer b = Integer.parseInt(s.toString().replace(".", ""))
                                            - ndet.get("OSPrincipalAmount").asInteger()
                                            - ndet.get("OSInterestAmount").asInteger()
                                            - ndet.get("installmentDue").asInteger();
                                    find(R.id.accrIntLoc, EditText.class).setText(String.valueOf(separator.format(b)));

                                    find(R.id.lcLoc, TextView.class).setText("0");
                                    find(R.id.othersLoc, TextView.class).setText("0");
                                    find(R.id.penaltyLoc, TextView.class).setText("0");
                                    recoverytype[0] = "FP_NORMAL";
                                } else {
                                    if (Integer.parseInt(s.toString().replace(".", ""))
                                            - ndet.get("OSPrincipalAmount").asInteger()
                                            - ndet.get("OSInterestAmount").asInteger()
                                            - ndet.get("installmentDue").asInteger()
                                            - ndet.get("AccruedInterest").asInteger()
                                            < ndet.get("OSLCAmount").asInteger()) {
                                        find(R.id.ospLoc, TextView.class).setText(separator.format(ndet.get("OSPrincipalAmount").asNumber()));
                                        find(R.id.osiLoc, TextView.class).setText(separator.format(ndet.get("OSInterestAmount").asNumber()));
                                        find(R.id.iDueLoc, TextView.class).setText(separator.format(ndet.get("installmentDue").asNumber()));
                                        find(R.id.accrIntLoc, TextView.class).setText(separator.format(ndet.get("AccruedInterest").asNumber()));

                                        Integer b = Integer.parseInt(s.toString().replace(".", ""))
                                                - ndet.get("OSPrincipalAmount").asInteger()
                                                - ndet.get("OSInterestAmount").asInteger()
                                                - ndet.get("installmentDue").asInteger()
                                                - ndet.get("AccruedInterest").asInteger();
                                        find(R.id.lcLoc, EditText.class).setText(String.valueOf(separator.format(b)));

                                        find(R.id.othersLoc, TextView.class).setText("0");
                                        find(R.id.penaltyLoc, TextView.class).setText("0");
                                        recoverytype[0] = "FP_NORMAL";


                                    } else {
                                        if (Integer.parseInt(s.toString().replace(".", ""))
                                                - ndet.get("OSPrincipalAmount").asInteger()
                                                - ndet.get("OSInterestAmount").asInteger()
                                                - ndet.get("installmentDue").asInteger()
                                                - ndet.get("AccruedInterest").asInteger()
                                                - ndet.get("OSLCAmount").asInteger()
                                                < ndet.get("AROthers").asInteger()) {
                                            find(R.id.ospLoc, TextView.class).setText(separator.format(ndet.get("OSPrincipalAmount").asNumber()));
                                            find(R.id.osiLoc, TextView.class).setText(separator.format(ndet.get("OSInterestAmount").asNumber()));
                                            find(R.id.iDueLoc, TextView.class).setText(separator.format(ndet.get("installmentDue").asNumber()));
                                            find(R.id.accrIntLoc, TextView.class).setText(separator.format(ndet.get("AccruedInterest").asNumber()));
                                            find(R.id.lcLoc, TextView.class).setText(separator.format(ndet.get("OSLCAmount").asNumber()));

                                            Integer b = Integer.parseInt(s.toString().replace(".", ""))
                                                    - ndet.get("OSPrincipalAmount").asInteger()
                                                    - ndet.get("OSInterestAmount").asInteger()
                                                    - ndet.get("installmentDue").asInteger()
                                                    - ndet.get("AccruedInterest").asInteger()
                                                    - ndet.get("OSLCAmount").asInteger();
                                            find(R.id.othersLoc, EditText.class).setText(String.valueOf(separator.format(b)));

                                            find(R.id.penaltyLoc, TextView.class).setText("0");
                                            recoverytype[0] = "FP_NORMAL";
                                        } else {
                                            if (Integer.parseInt(s.toString().replace(".", ""))
                                                    - ndet.get("OSPrincipalAmount").asInteger()
                                                    - ndet.get("OSInterestAmount").asInteger()
                                                    - ndet.get("installmentDue").asInteger()
                                                    - ndet.get("AccruedInterest").asInteger()
                                                    - ndet.get("OSLCAmount").asInteger()
                                                    - ndet.get("AROthers").asInteger()
                                                    < ndet.get("PrepaymentPenalty").asInteger()) {
                                                find(R.id.ospLoc, TextView.class).setText(separator.format(ndet.get("OSPrincipalAmount").asNumber()));
                                                find(R.id.osiLoc, TextView.class).setText(separator.format(ndet.get("OSInterestAmount").asNumber()));
                                                find(R.id.iDueLoc, TextView.class).setText(separator.format(ndet.get("installmentDue").asNumber()));
                                                find(R.id.accrIntLoc, TextView.class).setText(separator.format(ndet.get("AccruedInterest").asNumber()));
                                                find(R.id.lcLoc, TextView.class).setText(separator.format(ndet.get("OSLCAmount").asNumber()));
                                                find(R.id.othersLoc, TextView.class).setText(separator.format(ndet.get("AROthers").asNumber()));

                                                Integer b = Integer.parseInt(s.toString().replace(".", ""))
                                                        - ndet.get("OSPrincipalAmount").asInteger()
                                                        - ndet.get("OSInterestAmount").asInteger()
                                                        - ndet.get("installmentDue").asInteger()
                                                        - ndet.get("AccruedInterest").asInteger()
                                                        - ndet.get("OSLCAmount").asInteger()
                                                        - ndet.get("AROthers").asInteger();
                                                find(R.id.penaltyLoc, EditText.class).setText(String.valueOf(separator.format(b)));
                                                recoverytype[0] = "FP_NORMAL";
                                            } else {
                                                find(R.id.ospLoc, TextView.class).setText(separator.format(ndet.get("OSPrincipalAmount").asNumber()));
                                                find(R.id.osiLoc, TextView.class).setText(separator.format(ndet.get("OSInterestAmount").asNumber()));
                                                find(R.id.iDueLoc, TextView.class).setText(separator.format(ndet.get("installmentDue").asNumber()));
                                                find(R.id.accrIntLoc, TextView.class).setText(separator.format(ndet.get("AccruedInterest").asNumber()));
                                                find(R.id.lcLoc, TextView.class).setText(separator.format(ndet.get("OSLCAmount").asNumber()));
                                                find(R.id.othersLoc, TextView.class).setText(separator.format(ndet.get("AROthers").asNumber()));
                                                find(R.id.penaltyLoc, TextView.class).setText(separator.format(ndet.get("PrepaymentPenalty").asNumber()));
                                                recoverytype[0] = "FP_NORMAL";
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    find(R.id.ospLoc, TextView.class).setText("0");
                    find(R.id.osiLoc, TextView.class).setText("0");
                    find(R.id.lcLoc, TextView.class).setText("0");
                    find(R.id.othersLoc, TextView.class).setText("0");
                    find(R.id.penaltyLoc, TextView.class).setText("0");
                    find(R.id.totalLoc, TextView.class).setText("0");
                }
            }
        });

//__________________________________________________
        find(R.id.ospLoc, EditText.class).addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                //int block_id = jObj.has("block_id") ? jObj.getInt("block_id") : 0;

                Integer totw = ndet.get("OSPrincipalAmount").asInteger() -
                        Integer.parseInt(s.toString().replace(".", ""));
                find(R.id.ospw, EditText.class).setText(String.valueOf(separator.format(totw)));

                Double totp = Double.parseDouble(s.toString().replace(".", "")) * 100 / ndet.get("OSPrincipalAmount").asDouble();
                find(R.id.ospPct, EditText.class).setText(String.format(localeID, "%.0f", totp));


            }
        });

        find(R.id.osiLoc, EditText.class).addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                //int block_id = jObj.has("block_id") ? jObj.getInt("block_id") : 0;

                Integer totw = ndet.get("OSInterestAmount").asInteger() -
                        Integer.parseInt(s.toString().replace(".", ""));
                find(R.id.osiw, EditText.class).setText(String.valueOf(separator.format(totw)));

                Double totp = Double.parseDouble(s.toString().replace(".", "")) * 100 / ndet.get("OSInterestAmount").asDouble();
                if (Double.isNaN(totp)) {
                    find(R.id.osiPct, EditText.class).setText("0");
                } else {
                    find(R.id.osiPct, EditText.class).setText(String.format(localeID, "%.0f", totp));
                }

            }
        });

        find(R.id.iDueLoc, EditText.class).addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                //int block_id = jObj.has("block_id") ? jObj.getInt("block_id") : 0;

                Integer totw = ndet.get("installmentDue").asInteger() -
                        Integer.parseInt(s.toString().replace(".", ""));
                find(R.id.iDuew, EditText.class).setText(String.valueOf(separator.format(totw)));

                Double totp = Double.parseDouble(s.toString().replace(".", "")) * 100 / ndet.get("installmentDue").asDouble();
                if (Double.isNaN(totp)) {
                    find(R.id.iDuePct, EditText.class).setText("0");
                } else {
                    find(R.id.iDuePct, EditText.class).setText(String.format(localeID, "%.0f", totp));
                }

            }
        });

        find(R.id.accrIntLoc, EditText.class).addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                //int block_id = jObj.has("block_id") ? jObj.getInt("block_id") : 0;

                Integer totw = ndet.get("AccruedInterest").asInteger() -
                        Integer.parseInt(s.toString().replace(".", ""));
                find(R.id.accrIntw, EditText.class).setText(String.valueOf(separator.format(totw)));

                Double totp = Double.parseDouble(s.toString().replace(".", "")) * 100 / ndet.get("AccruedInterest").asDouble();
                if (Double.isNaN(totp)) {
                    find(R.id.accrIntPct, EditText.class).setText("0");
                } else {
                    find(R.id.accrIntPct, EditText.class).setText(String.format(localeID, "%.0f", totp));
                }

            }
        });

        find(R.id.lcLoc, EditText.class).addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                Integer totw = ndet.get("OSLCAmount").asInteger() -
                        Integer.parseInt(s.toString().replace(".", ""));
                find(R.id.lcw, EditText.class).setText(String.valueOf(separator.format(totw)));

                Double totp = Double.parseDouble(s.toString().replace(".", "")) * 100 / ndet.get("OSLCAmount").asDouble();
                if (Double.isNaN(totp)) {
                    find(R.id.lcPct, EditText.class).setText("0");
                } else {
                    find(R.id.lcPct, EditText.class).setText(String.format(localeID, "%.0f", totp));
                }
            }
        });

        find(R.id.othersLoc, EditText.class).addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                Integer totw = ndet.get("AROthers").asInteger() -
                        Integer.parseInt(s.toString().replace(".", ""));
                find(R.id.othersw, EditText.class).setText(String.valueOf(separator.format(totw)));

                Double totp = Double.parseDouble(s.toString().replace(".", "")) * 100 / ndet.get("AROthers").asDouble();
                if (Double.isNaN(totp)) {
                    find(R.id.othersPct, EditText.class).setText("0");
                } else {
                    find(R.id.othersPct, EditText.class).setText(String.format(localeID, "%.0f", totp));
                }
            }
        });

        find(R.id.penaltyLoc, EditText.class).addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                //Integer sl = s=="" ? 0 : Integer.parseInt(s.toString());
                Integer totw = ndet.get("PrepaymentPenalty").asInteger() -
                        Integer.parseInt(s.toString().replace(".", ""));
                find(R.id.penaltyw, EditText.class).setText(String.valueOf(separator.format(totw)));

                Double totp = Double.parseDouble(s.toString().replace(".", "")) * 100 / ndet.get("PrepaymentPenalty").asDouble();
                if (Double.isNaN(totp)) {
                    find(R.id.penaltyPct, EditText.class).setText("0");
                } else {
                    find(R.id.penaltyPct, EditText.class).setText(String.format(localeID, "%.0f", totp));
                }
            }
        });
        findViewById(R.id.imgKtp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCamera(REQUEST_KTP);
            }
        });
        findViewById(R.id.imgPermohonan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCamera(REQUEST_PERMOHONAN);
            }
        });

        //------------------------------------------------------------------------
    }
    protected void simpan(final String apptype,
                          final String rectype,
                          final String prodtype,
                          final String npldate,
                          //allocation
                          final String osploc,
                          final String osiloc,
                          final String lcloc,
                          final String othersloc,
                          final String penaltyloc,
                          final String totalloc,
                          //waived
                          final String ospw,
                          final String osiw,
                          final String lcw,
                          final String othersw,
                          final String penaltyw,
                          final String totalw,
                          //percentage
                          final String osppct,
                          final String osipct,
                          final String lcpct,
                          final String otherspct,
                          final String penaltypct,
                          final String totalpct,
                          //file
                          final String prepayment,
                          final String ktp,
                          final String permohonan
    ) {
        newProcess(new Messagebox.DoubleRunnable() {
            String result;
            String res;
            String sagrno = getSetting("agrno");
            String splate = getSetting("plate");

            public void run() {
                autoToken();
                Dson dson = getDefaultDataRaw();
                dson.set("AgreementNo", sagrno);
                dson.set("LicensePlate", splate);
                /*dson.set("_ApprovalTypeID", apptype);
                dson.set("_RecoveryType", rectype);*/
                dson.set("_ProductType", prodtype);
                dson.set("_WO_NPL_Date", npldate);
                //Alocation
                /*dson.set("AllocationOSP", osploc);
                dson.set("AllocationOSI", osiloc);
                dson.set("AllocationLC", lcloc);
                dson.set("AllocationOther", othersloc);
                dson.set("AllocationPenalty", penaltyloc);
                dson.set("AllocationTotal", totalloc);*/
                //Waived
                dson.set("WaivedOSP", ospw);
                dson.set("WaivedOSI", osiw);
                dson.set("WaivedLC", lcw);
                dson.set("WaivedOther", othersw);
                dson.set("WaivedPenalty", penaltyw);
                dson.set("WaivedTotal", totalw);
                //Percentage
                /*dson.set("PercentageOSP", osppct);
                dson.set("PercentageOSI", osipct);
                dson.set("PercentageLC", lcpct);
                dson.set("PercentageOther", otherspct);
                dson.set("PercentagePenalty", penaltypct);
                dson.set("PercentageTotal", totalpct);*/
                //file
                dson.set("PrepaymentAmount", prepayment);
                //result = InternetX.postHttpConnectionRaw(getBaseUrl("RPM/RPM_Waived"), getDefaultHeader(), dson);
                res = dson.toJson();
                Dson file = Dson.newObject();
                file.set("KTP",       ktp);
                file.set("SuratPermohonan",         permohonan);
                result = postHttpRaw("RPM/RPM_Waived", dson, file);
                autoToken(result);
            }

            public void runUI() {
                Dson dson = Dson.readJson(result);
                Dson n = Dson.readJson(res);
                if (dson.get("ResponseCode").asString().equalsIgnoreCase("00")) {
                    AddDataLog("Waived", dson.get("ResponseDescription").asString());
                    showInfoDialog(dson.get("ResponseDescription").asString(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Waived.this.finish();
                            dialog.dismiss();
                            Dson n = Dson.newObject();
                            n.set("flag", "1");
                            n.set("aggrNo", sagrno);
                            n.set("f410", "full");
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            intent.putExtra("flag", n.toString());
                            startActivity(intent);
                            finish();
                        }
                    });
                } else if (dson.get("ResponseCode").asString().equalsIgnoreCase("99")) {
                    //change hdr
                    AddDataLog("Waived", dson.get("ResponseDescription").asString());
                    showInfo(dson.get("ResponseDescription").asString());
                } else {
                    //change hdr
                    //add line to responsecode = 00
                    //GoToURL("http://api.whatsapp.com/send?phone=" + "6287886722267" + "&text=" + n.toJson());
                    AddDataLog("Waived", result);
                    showInfo(result);
                }
            }
        });
    }

    public void showCamera(int request){
        Log.i("showCamera",Thread.currentThread().getName());
        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED||
                ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED||
                ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(getActivity(), new String[] {android.Manifest.permission.CAMERA,android.Manifest.permission.READ_EXTERNAL_STORAGE,android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
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
    public void getCamera(String name, Intent data, ImageView imageView){
        File photo = new File(Environment.getExternalStorageDirectory(), "image");
        String fname =   Long.toHexString(System.currentTimeMillis()) +"." + Long.toHexString(System.nanoTime()) +"." + System.currentTimeMillis() +".tmp";



        String pfile = Utility.getCacheDir(name+"_AM");
        Utility.copyFile( photo.getAbsolutePath(), pfile );
        onCompressImage( pfile , 60,1366,1366 );

        //imageView.setImageURI(Uri.fromFile(photo));
        viewImage(imageView, pfile, 256);//photo.getAbsolutePath()
        imageView.setTag(pfile);
        //camera


    }
    protected void onActivityResult(int requestCode, int resultCode,   Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_KTP && resultCode == RESULT_OK){
            getCamera("REQUEST_KTP", data, find(R.id.imgKtp, ImageView.class));
        }else if (requestCode == REQUEST_PERMOHONAN && resultCode == RESULT_OK){
            getCamera("REQUEST_PERMOHONAN", data, find(R.id.imgPermohonan, ImageView.class));
        }
    }

    final int REQUEST_KTP       = 203;
    final int REQUEST_PERMOHONAN    = 204;
}
