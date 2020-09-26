package com.studioh.cma.recovery;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.naa.data.Dson;
import com.naa.data.Utility;
import com.naa.data.UtilityAndroid;
import com.naa.utils.InternetX;
import com.naa.utils.Messagebox;
import com.studioh.cma.AppActivity;
import com.studioh.cma.R;
import com.studioh.cma.main.MainActivity;
import com.studioh.srv.DsonAutoCompleteAdapter;
import com.studioh.srv.StudiohAutoComplete;

import java.io.File;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Partial extends AppActivity {
    Locale localeID = new Locale("in", "ID");
    NumberFormat separator = NumberFormat.getInstance(localeID);
    SimpleDateFormat fdt = new SimpleDateFormat("d MMM yyyy", Locale.US);

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partial_payment);
        setTitle("PARTIAL PAYMENT");

        currTextFormat(find(R.id.thrdPartyInput, EditText.class));

        final Dson ndtl = Dson.readJson(getSetting("LV"));
        final Dson ndet = Dson.readJson(getSetting("DtlDetail"));

        DateFormat inputfdt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        final SimpleDateFormat fdy = new SimpleDateFormat("yyyy", Locale.US);

        String paramYear = null;
        try {
            paramYear = fdy.format(inputfdt.parse(ndet.get("NPLDate").asString()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        final Spinner dropdown = findViewById(R.id.tipeMitra);
        String[] items = new String[]{"Internal", "External"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, items);
        dropdown.setAdapter(adapter);

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                if (dropdown.getSelectedItem().equals("External")) {
                    find(R.id.txtPIC, TextView.class).setEnabled(true);
                    find(R.id.txtPIC, TextView.class).setText("");
                    find(R.id.thrdPartyInput, TextView.class).setEnabled(true);
                    find(R.id.thrdPartyInput, TextView.class).setText("");

                } else {
                    find(R.id.txtPIC, TextView.class).setText(ndtl.get("Name").asString());
                    find(R.id.txtPIC, TextView.class).setEnabled(false);
                    find(R.id.thrdPartyInput, TextView.class).setEnabled(false);
                    find(R.id.thrdPartyInput, TextView.class).setText("0");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
                find(R.id.txtPIC, TextView.class).setText(ndtl.get("Name").asString());
                find(R.id.txtPIC, TextView.class).setEnabled(false);
            }
        });

        find(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final int tot = ndet.get("OSPrincipalAmount").asInteger()
                + ndet.get("OSInterestAmount").asInteger()
                + ndet.get("installmentDue").asInteger()
                + ndet.get("AccruedInterest").asInteger()
                + ndet.get("OSLCAmount").asInteger()
                + ndet.get("AROthers").asInteger()
                + ndet.get("PrepaymentPenalty").asInteger();
        final int totPokok = ndet.get("OSPrincipalAmount").asInteger()
                + ndet.get("OSInterestAmount").asInteger();

        find(R.id.amtPaid, TextView.class).setText(separator.format(tot));

        find(R.id.okInstallment).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                invalidateAdd(Utility.getInt(find(R.id.txtNumber, EditText.class).getText().toString()));
            }
        });

        StudiohAutoComplete bookTitle = (StudiohAutoComplete) findViewById(R.id.txtPIC);
        bookTitle.setThreshold(2);
        bookTitle.setAdapter(new DsonAutoCompleteAdapter(this) {
            public Dson onFindDson(Context context, String bookTitle) {
                Dson dson = getDefaultDataRaw();
                String res = postHttpRaw("RPM/RPM_GetDataMitra", dson);
                autoToken(res);
                return Dson.readJson(res).get("data");
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = inflater.inflate(R.layout.find_autocomplete, parent, false);
                }
                findView(convertView, R.id.txtText, TextView.class).setText(getItem(position).get("MitraName").asString());

                return convertView;
            }

        }); // 'this' is Activity instance
        bookTitle.setLoadingIndicator((android.widget.ProgressBar) findViewById(R.id.pb_txt));
        bookTitle.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Dson n = Dson.readJson(String.valueOf(adapterView.getItemAtPosition(position)));
                find(R.id.txtPIC, StudiohAutoComplete.class).setText((n.get("MitraName").asString()));
            }
        });

        find(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        final Dson n = Dson.newArray();
        final TableLayout linearLayout = findViewById(R.id.table_main);
        // final View v = UtilityAndroid.getInflater(getActivity(), R.layout.activity_partial_payment_row);
        final String finalParamYear = paramYear;
        find(R.id.submit).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (find(R.id.thrdPartyInput, EditText.class).getText().toString().length() == 0) {
                    find(R.id.thrdPartyInput, EditText.class).setError("Informasi Diperlukan!");
                } else if (find(R.id.txtPIC, EditText.class).getText().toString().length() == 0) {
                    find(R.id.txtPIC, EditText.class).setError("Informasi Diperlukan!");
                } else if (find(R.id.notesInput, EditText.class).getText().toString().length() == 0) {
                    find(R.id.notesInput, EditText.class).setError("Informasi Diperlukan!");
                } else if (find(R.id.txtNumber, EditText.class).getText().toString().length() == 0) {
                    find(R.id.txtNumber, EditText.class).setError("Informasi Diperlukan!");
                } else if (String.valueOf(find(R.id.imgKtp, ImageView.class).getTag()).equals("null")) {
                    showInfoDialog("Foto KTP!", null);
                } else if (String.valueOf(find(R.id.imgPermohonan, ImageView.class).getTag()).equals("null")) {
                    showInfoDialog("Foto Surat Permohonan!", null);
                } else {
                    int payment = 0;
                    for (int i = 1; i < linearLayout.getChildCount(); i++) { //(+header)
                        if (findRow(i, R.id.txtInstall, EditText.class).getText().toString().replace(".", "").length() == 0) {
                            findRow(i, R.id.txtInstall, EditText.class).setError("Tidak Boleh Kosong!");
                        } else if (findRow(i, R.id.txtDate, EditText.class).getText().toString().replace(".", "").length() == 0) {
                            findRow(i, R.id.txtDate, EditText.class).setError("Tidak Boleh Kosong!");
                        } else {
                            DateFormat gdt = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                            Dson row = Dson.newObject();
                            row.set("no", findRow(i, R.id.noSeq, TextView.class).getText().toString());

                            row.set("amountinstallment", findRow(i, R.id.txtInstall, EditText.class).getText().toString().replace(".", ""));
                            try {
                                row.set("ptpdate", gdt.format(fdt.parse(findRow(i, R.id.txtDate, EditText.class).getText().toString())));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            n.add(row);
                            payment = payment + Integer.parseInt(findRow(i, R.id.txtInstall, EditText.class).getText().toString().replace(".", ""));
                        }
                    }
                    if (payment == tot) {
                        simpan(dropdown.getSelectedItem().toString(),
                                find(R.id.thrdPartyInput, EditText.class).getText().toString().replace(".", ""),
                                find(R.id.txtPIC, EditText.class).getText().toString(),
                                find(R.id.notesInput, EditText.class).getText().toString(),
                                find(R.id.txtNumber, EditText.class).getText().toString(),
                                //tobepaid
                                String.valueOf(tot),
                                //pokok
                                String.valueOf(totPokok),
                                ndet.get("AssetType").asString(),
                                finalParamYear,
                                String.valueOf(find(R.id.imgKtp, ImageView.class).getTag()),
                                String.valueOf(find(R.id.imgPermohonan, ImageView.class).getTag()),
                                n
                        );
                    } else {
                        showInfoDialog("Total Partial Payment harus sama dengan Amount To Be Paid!", null);
                    }

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

    }


    protected void simpan(final String handling,
                          final String tparty,
                          final String pic,
                          final String notes,
                          final String tenor,
                          final String tobepaid,
                          final String pokok,
                          final String prodtype,
                          final String npldate,
                          final String ktp,
                          final String permohonan,
                          final Dson n
    ) {
        newProcess(new Messagebox.DoubleRunnable() {
            Dson dson = getDefaultDataRaw();
            String result;
            String res;
            String sagrno = getSetting("agrno");
            String splate = getSetting("plate");

            public void run() {
                autoToken();
                int prepayment = 0;
                dson.set("AgreementNo", sagrno);
                dson.set("LicensePlate", splate);
                dson.set("HandlingBy", handling);
                dson.set("ThirdParty", tparty);
                dson.set("PIC", pic);
                dson.set("Notes", notes);
                dson.set("HowManyInstalled", tenor);
                for (int i = 0; i < n.size(); i++) {
                    dson.set("amount[" + n.get(i).get("no") + "][amountinstallment]", n.get(i).get("amountinstallment").asString());
                    dson.set("amount[" + n.get(i).get("no") + "][ptpdate]", n.get(i).get("ptpdate").asString());
                    prepayment = prepayment + Integer.parseInt(n.get(i).get("amountinstallment").asString());
                }
                /*if(prepayment<Integer.parseInt(tobepaid)){
                    dson.set("_ApprovalTypeID", "PAYMENTWAIVED");
                    if(prepayment<Integer.parseInt(pokok)){
                        dson.set("_RecoveryType", "FP_SPECIAL");
                    } else {
                        dson.set("_RecoveryType", "FP_NORMAL");
                    }
                }else{
                    dson.set("_ApprovalTypeID", "COLLECTIONFEE");
                }*/
                dson.set("_ProductType", prodtype);
                dson.set("_WO_NPL_Date", npldate);
                /*
                * dson.set("ApprovalTypeID", apptype);
                dson.set("RecoveryType", rectype);
                dson.set("ProductType", prodtype);
                dson.set("WO_NPL_Date", npldate);
                * */
                //dson.set("amount", n);
                //setSetting("ppp", dson.toJson());
                //result = InternetX.postHttpConnectionRaw(getBaseUrl("RPM/RPM_Partial"), getDefaultHeader(), dson);

                Dson file = Dson.newObject();
                file.set("KTP", ktp);
                file.set("SuratPermohonan", permohonan);
                res = dson.toJson();
                result =
                        postHttpRaw("RPM/RPM_Partial", dson, file);
                autoToken(result);
            }

            public void runUI() {
                Dson dson = Dson.readJson(result);
                GoToURL("http://api.whatsapp.com/send?phone=" + "6287886722267" + "&text=" + res);
                if (dson.get("ResponseCode").asString().equalsIgnoreCase("00")) {
                    AddDataLog("Partial Payment", dson.get("ResponseDescription").asString());
                    showInfoDialog(dson.get("ResponseDescription").asString(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //showInfo(getSetting("nnn"));
                            /*Partial.this.finish();
                            dialog.dismiss();*/
                            Dson n = Dson.newObject();
                            n.set("flag", "1");
                            n.set("aggrNo", sagrno);
                            n.set("f410", "PartialPayment");
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            intent.putExtra("flag", n.toString());
                            startActivity(intent);
                            finish();
                        }
                    });
                } else if (dson.get("ResponseCode").asString().equalsIgnoreCase("99")) {
                    AddDataLog("Partial Payment", dson.get("ResponseDescription").asString());
                    showInfo(dson.get("ResponseDescription").asString());
                } else {

                    AddDataLog("Partial Payment", result);
                    showInfo(result);
                }
            }
        });
    }

    public void invalidateAdd(int rows) {
        //LinearLayout linearLayout = find(R.id.lnrAdd);
        TableLayout linearLayout = findViewById(R.id.table_main);


        //remove + header
        if (linearLayout.getChildCount() > rows + 1) {
            for (int i = linearLayout.getChildCount() - 1; i >= rows + 1; i--) {
                linearLayout.removeViewAt(i);
            }
        }


        //add + header
        for (int i = linearLayout.getChildCount(); i < rows + 1; i++) {
            if (i % 2 == 0) {
                linearLayout.setBackgroundResource(R.color.grey_100);
            }
            View v = UtilityAndroid.getInflater(getActivity(), R.layout.activity_partial_payment_row);
            linearLayout.addView(v);

            findView(v, R.id.noSeq, TextView.class).setText(String.valueOf(i));
            currTextFormat(findView(v, R.id.txtInstall, EditText.class));
            findView(v, R.id.btnDate, View.class).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    final int row = Utility.getInt(String.valueOf(v.getTag()));
                    final Calendar newCalendar = Calendar.getInstance();

                    final DatePickerDialog StartTime = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            Calendar newDate = Calendar.getInstance();
                            newDate.set(year, monthOfYear, dayOfMonth);
                            findRow(row, R.id.txtDate, EditText.class).setText(fdt.format(newDate.getTime()));
                        }

                    }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
                    StartTime.show();
                }
            });
            findView(v, R.id.btnDate, View.class).setTag(String.valueOf(i));
        }

    }

    public <T extends View> T findRow(int row, int id, Class<? super T> s) {
        //LinearLayout linearLayout = find(R.id.lnrAdd);
        TableLayout linearLayout = findViewById(R.id.table_main);

        if (linearLayout.getChildCount() > row) {
            return findView(linearLayout.getChildAt(row), id, s);
        }
        return null;
    }

    public void showCamera(int request) {
        Log.i("showCamera", Thread.currentThread().getName());
        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ||
                ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED ||
                ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
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
        if (requestCode == REQUEST_KTP && resultCode == RESULT_OK) {
            getCamera("REQUEST_KTP", data, find(R.id.imgKtp, ImageView.class));
        } else if (requestCode == REQUEST_PERMOHONAN && resultCode == RESULT_OK) {
            getCamera("REQUEST_PERMOHONAN", data, find(R.id.imgPermohonan, ImageView.class));
        }
    }

    final int REQUEST_KTP = 203;
    final int REQUEST_PERMOHONAN = 204;
}


