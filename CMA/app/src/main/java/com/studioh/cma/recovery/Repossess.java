package com.studioh.cma.recovery;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;

public class Repossess extends AppActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_responses);

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

        final Spinner dropdown = findViewById(R.id.spinner);
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

        setTitle("REPOSSESS");

        currTextFormat(find(R.id.thrdPartyInput, EditText.class));

        findViewById(R.id.imgRal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCamera(REQUEST_RAL);
            }
        });
        /*findViewById(R.id.imgBastk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCamera(REQUEST_BASTIK);
            }
        });
        findViewById(R.id.imgKunci).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCamera(REQUEST_KUNCI);
            }
        });
        findViewById(R.id.imgMou).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCamera(REQUEST_MOU);
            }
        });*/
        findViewById(R.id.imgPermohonan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCamera(REQUEST_SURAT);
            }
        });
        findViewById(R.id.add0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCamera(REQUEST_ADD0);
            }
        });
        findViewById(R.id.add1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCamera(REQUEST_ADD1);
            }
        });
        findViewById(R.id.add2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCamera(REQUEST_ADD2);
            }
        });
        findViewById(R.id.add3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCamera(REQUEST_ADD3);
            }
        });
        findViewById(R.id.add4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCamera(REQUEST_ADD4);
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

        currTextFormat(find(R.id.hargaInput, EditText.class));
        find(R.id.cancelReposses).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        final String finalParamYear = paramYear;
        find(R.id.saveReposses).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (find(R.id.thrdPartyInput, EditText.class).getText().toString().length() == 0) {
                    find(R.id.thrdPartyInput, EditText.class).setError("Informasi Diperlukan!");
                } else if (find(R.id.txtPIC, EditText.class).getText().toString().length() == 0) {
                    find(R.id.txtPIC, EditText.class).setError("Informasi Diperlukan!");
                } else if (find(R.id.editText6, EditText.class).getText().toString().length() == 0) {
                    find(R.id.editText6, EditText.class).setError("Informasi Diperlukan!");
                } else if (find(R.id.hargaInput, EditText.class).getText().toString().length() == 0) {
                    find(R.id.hargaInput, EditText.class).setError("Informasi Diperlukan!");
                    //Image Mandatory
                } else if (String.valueOf(find(R.id.imgRal, ImageView.class).getTag()).equals("null")) {
                    showInfoDialog("Silahkan Take Photo RAL", null);
                } else if (String.valueOf(find(R.id.imgPermohonan, ImageView.class).getTag()).equals("null")) {
                    showInfoDialog("Silahkan Take Photo Surat Permohonan", null);
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
                    simpan(String.valueOf(find(R.id.spinner, Spinner.class).getSelectedItem()),
                            find(R.id.thrdPartyInput, EditText.class).getText().toString().replace(".", ""),
                            find(R.id.txtPIC, EditText.class).getText().toString(),
                            find(R.id.editText6, EditText.class).getText().toString(),
                            find(R.id.hargaInput, EditText.class).getText().toString().replace(".", ""),
                            "COLLECTIONFEE",
                            "",
                            ndet.get("AssetType").asString(),
                            finalParamYear,
                            String.valueOf(find(R.id.imgRal, ImageView.class).getTag()),
                            String.valueOf(find(R.id.imgPermohonan, ImageView.class).getTag()),
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

    public void simpan(final String handling,
                       final String thrdparty,
                       final String pic,
                       final String notes,
                       final String harga,
                       final String apptype,
                       final String rectype,
                       final String prodtype,
                       final String npldate,
                       final String imgral,
                       final String imgsurat,
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
            String param;
            String sagrno = getSetting("agrno");

            //String result;
            public void run() {
                Dson dson = getDefaultDataRaw();
                dson.set("AgreementNo", sagrno);
                dson.set("LicensePlate", getSetting("plate"));
                dson.set("HandlingBy", handling);
                dson.set("ThirdParty", thrdparty);
                dson.set("PIC", pic);
                dson.set("Notes", notes);
                dson.set("HargaJual", harga);
                /*dson.set("_ApprovalTypeID", apptype);
                dson.set("_RecoveryType", rectype);*/
                dson.set("_ProductType", prodtype);
                dson.set("_WO_NPL_Date", npldate);
                dson.set("Image[0][description]", txt0);
                dson.set("Image[1][description]", txt1);
                dson.set("Image[2][description]", txt2);
                dson.set("Image[3][description]", txt3);
                dson.set("Image[4][description]", txt4);

                Dson file = Dson.newObject();
                file.set("RAL", imgral);
                        /*file.set("BASTK",         String.valueOf(find(R.id.imgBastk, ImageView.class).getTag()));
                        file.set("Kunci",        String.valueOf(find(R.id.imgKunci, ImageView.class).getTag()));
                        file.set("MOU",String.valueOf(find(R.id.imgMou, ImageView.class).getTag()));*/
                file.set("SuratPermohonan", imgsurat);
                file.set("Image[0][path]", add0);
                file.set("Image[1][path]", add1);
                file.set("Image[2][path]", add2);
                file.set("Image[3][path]", add3);
                file.set("Image[4][path]", add4);

                //result = dson.toJson() +"+"+file.toJson();
                param = dson.toJson();
                res = postHttpRaw("RPM/RPM_Reposses", dson, file);
                autoToken(res);
            }

            public void runUI() {

                Dson dson = Dson.readJson(res);
                if (dson.get("ResponseCode").asString().equalsIgnoreCase("00")) {
                    AddDataLog("Repossess", dson.get("ResponseDescription").asString());
                    showInfoDialog(dson.get("ResponseDescription").asString(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                                    /*Responses.this.finish();
                                    dialog.dismiss();*/
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
                    //setResult(RESULT_OK);
                } else if (dson.get("ResponseCode").asString().equalsIgnoreCase("99")) {
                    AddDataLog("Repossess", dson.get("ResponseDescription").asString());
                    showInfo(dson.get("ResponseDescription").asString());
                } else {
                    AddDataLog("Repossess", res);
                    //GoToURL("http://api.whatsapp.com/send?phone=" + "6287886722267" + "&text=" + param);
                    showInfo(res);
                }
            }
        });
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
        if (requestCode == REQUEST_RAL && resultCode == RESULT_OK) {
            getCamera("REQUEST_RAL", data, find(R.id.imgRal, ImageView.class));
        }/*else if (requestCode == REQUEST_BASTIK && resultCode == RESULT_OK){
            getCamera("REQUEST_BASTIK", data, find(R.id.imgBastk, ImageView.class));
        }else if (requestCode == REQUEST_KUNCI && resultCode == RESULT_OK){
            getCamera("REQUEST_KUNCI", data, find(R.id.imgKunci, ImageView.class));
        }else if (requestCode == REQUEST_MOU && resultCode == RESULT_OK){
            getCamera("REQUEST_MOU", data, find(R.id.imgMou, ImageView.class));
        }*/ else if (requestCode == REQUEST_SURAT && resultCode == RESULT_OK) {
            getCamera("REQUEST_SURAT", data, find(R.id.imgPermohonan, ImageView.class));
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
    /*  final int REQUEST_BASTIK    = 204;
        final int REQUEST_KUNCI     = 205;
        final int REQUEST_MOU       = 206;*/
    final int REQUEST_SURAT = 207;
    final int REQUEST_ADD0 = 300;
    final int REQUEST_ADD1 = 301;
    final int REQUEST_ADD2 = 302;
    final int REQUEST_ADD3 = 303;
    final int REQUEST_ADD4 = 304;

}
