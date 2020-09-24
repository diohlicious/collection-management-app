package com.studioh.cma.fav.partner;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TimePicker;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.naa.data.Dson;
import com.naa.data.Utility;
import com.naa.utils.InternetX;
import com.naa.utils.Messagebox;
import com.studioh.cma.AppActivity;
import com.studioh.cma.R;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class PartnerAddActivity extends AppActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partner_add);

        setTitle("Tambah Partner");
        //find(R.id.mitraLAddrInput).setFocusable(true);

        final Calendar newCalendar = Calendar.getInstance();

        final SimpleDateFormat fdt = new SimpleDateFormat("d MMM yyyy", Locale.US);
        final DateFormat outputfdt = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        String[] itemmitra = new String[]{"Personal", "Badan Hukum"};
        String[] itemedu = new String[]{"SD", "SMP", "SMA", "D3", "S1", "Others"};
        String[] itehandle = new String[]{"Mitra-Matel", "Mitra-Visit", "TNI", "Polri"};
        String[] itemspec = new String[]{"LSM", "Aparat", "Personal"};

        ArrayAdapter<String> adaptermitra = new ArrayAdapter<>(this, R.layout.spinner_item, itemmitra);
        ArrayAdapter<String> adapteredu = new ArrayAdapter<>(this, R.layout.spinner_item, itemedu);
        ArrayAdapter<String> adapterhandle = new ArrayAdapter<>(this, R.layout.spinner_item, itehandle);
        ArrayAdapter<String> adapterspec = new ArrayAdapter<>(this, R.layout.spinner_item, itemspec);

        find(R.id.tipeMitra, Spinner.class).setAdapter(adaptermitra);
        find(R.id.eduMitra, Spinner.class).setAdapter(adapteredu);
        find(R.id.handlingMitra, Spinner.class).setAdapter(adapterhandle);
        find(R.id.specMitra, Spinner.class).setAdapter(adapterspec);

        findViewById(R.id.mitraPhotoImg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCamera(REQUEST_PHT);
            }
        });
        findViewById(R.id.mitraKtpImg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCamera(REQUEST_KTP);
            }
        });
        findViewById(R.id.mitraNpwpImg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCamera(REQUEST_NPWP);
            }
        });
        findViewById(R.id.mitraJaminanImg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCamera(REQUEST_JAMINAN);
            }
        });
        findViewById(R.id.mitraSppiImg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCamera(REQUEST_SPPI);
            }
        });
        findViewById(R.id.mitraBukuImg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCamera(REQUEST_BUKU);
            }
        });
        final String[] dob = new String[1];
        //android.R.style.Theme_Holo_Light_Dialog_NoActionBar
        final DatePickerDialog  StartTime = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                find(R.id.dobMitra, EditText.class).setText(fdt.format(newDate.getTime()));
                dob[0] = outputfdt.format(newDate.getTime());
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        find(R.id.calendarMitra).setOnClickListener(new View.OnClickListener() {
            @Override   public void onClick(View v) {
                StartTime .show();
            }
        });

        /*findViewById(R.id.mitraLAddrInput).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInfoDialogAddr();
            }
        });*/
        findViewById(R.id.copy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInfoDialogAddr();
            }
        });

        find(R.id.cancelColPoin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        find(R.id.saveColPoin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newProcess(new Messagebox.DoubleRunnable() {
                    String res;

                    public void run() {
                        Dson dson = getDefaultDataRaw();
                        dson.set("MitraType", String.valueOf(find(R.id.tipeMitra, Spinner.class).getSelectedItem()));
                        dson.set("MitraName", String.valueOf(find(R.id.mitraNameInput, EditText.class).getText()));
                        dson.set("IDNo", String.valueOf(find(R.id.mitraIdInput, EditText.class).getText()));
                        dson.set("BirthPlace", String.valueOf(find(R.id.pob, EditText.class).getText()));
                        dson.set("DateofBirth", dob[0]);
                        dson.set("Education", String.valueOf(find(R.id.eduMitra, Spinner.class).getSelectedItem()));
                        dson.set("CollectionHandling", String.valueOf(find(R.id.handlingMitra, Spinner.class).getSelectedItem()));
                        dson.set("ResidenceAddress", String.valueOf(find(R.id.mitraAddrInput, EditText.class).getText()));
                        dson.set("LegalAddress", String.valueOf(find(R.id.mitraLAddrInput, EditText.class).getText()));
                        dson.set("Specialist", String.valueOf(find(R.id.specMitra, Spinner.class).getSelectedItem()));
                        dson.set("Notes", String.valueOf(find(R.id.mitraNotesInput, EditText.class).getText()));

                        Dson file = Dson.newObject();
                        file.set("Photo",       String.valueOf(find(R.id.mitraPhotoImg, ImageView.class).getTag()));
                        file.set("KTP",         String.valueOf(find(R.id.mitraKtpImg, ImageView.class).getTag()));
                        file.set("NPWP",        String.valueOf(find(R.id.mitraNpwpImg, ImageView.class).getTag()));
                        file.set("JaminanMitra",String.valueOf(find(R.id.mitraJaminanImg, ImageView.class).getTag()));
                        file.set("SPPI",        String.valueOf(find(R.id.mitraSppiImg, ImageView.class).getTag()));
                        file.set("BukuTabungan",String.valueOf(find(R.id.mitraBukuImg, ImageView.class).getTag()));
                        file.set("KK",          String.valueOf(find(R.id.mitraPhotoImg, ImageView.class).getTag()));
                        res = postHttpRaw("RPM/RPM_DataMitra", dson, file);
                        autoToken(res);
                    }

                    public void runUI() {

                        Dson dson = Dson.readJson(res);
                        if ( dson.get("ResponseCode").asString().equalsIgnoreCase("00")) {
                            AddDataLog("Add Partner", dson.get("ResponseDescription").asString());
                            showInfoDialog(dson.get("ResponseDescription").asString(), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    setResult(RESULT_OK);
                                    finish();
                                    dialog.dismiss();
                                }
                            });

                        }else if (!dson.containsKey("ResponseCode")) {
                            //change header
                            AddDataLog("Add Partner", res);
                            showInfo(res);
                        } else {
                            //change header
                            //add to response code 00
                            AddDataLog("Add Partner", dson.get("ResponseDescription").asString());
                            showInfo(dson.get("ResponseDescription").asString());
                        }
                    }
                });
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
        onCompressImage( pfile , 80,1366,1366 );
        if (isRotate_90(photo.getAbsolutePath())){
            rotate(pfile, 90);
        }

        //imageView.setImageURI(Uri.fromFile(photo));
        viewImage(imageView, pfile, 256);//photo.getAbsolutePath()
        imageView.setTag(pfile);
        //camera

    }
    protected void onActivityResult(int requestCode, int resultCode,   Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PHT && resultCode == RESULT_OK){
            getCamera("REQUEST_PHT", data, find(R.id.mitraPhotoImg, ImageView.class));
        }else if (requestCode == REQUEST_KTP && resultCode == RESULT_OK){
            getCamera("REQUEST_KTP", data, find(R.id.mitraKtpImg, ImageView.class));
        }else if (requestCode == REQUEST_NPWP && resultCode == RESULT_OK){
            getCamera("REQUEST_NPWP", data, find(R.id.mitraNpwpImg, ImageView.class));
        }else if (requestCode == REQUEST_JAMINAN && resultCode == RESULT_OK){
            getCamera("REQUEST_JAMINAN", data, find(R.id.mitraJaminanImg, ImageView.class));
        }else if (requestCode == REQUEST_SPPI && resultCode == RESULT_OK){
            getCamera("REQUEST_SPPI", data, find(R.id.mitraSppiImg, ImageView.class));
        }else if (requestCode == REQUEST_BUKU && resultCode == RESULT_OK){
            getCamera("REQUEST_BUKU", data, find(R.id.mitraBukuImg, ImageView.class));
        }
    }

    public void showInfoDialogAddr(){
        new AlertDialog.Builder(this)
                .setTitle("Pesan:")
                .setMessage("Alamat Legal Sama Dengan Alamat Residence?")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (find(R.id.mitraAddrInput, EditText.class).getText().length()>0){
                            find(R.id.mitraLAddrInput, EditText.class).setText(
                                    find(R.id.mitraAddrInput, EditText.class).getText().toString()
                            );
                        } else {
                            find(R.id.mitraAddrInput, EditText.class).setError("Mohon Isi Alamat"
                            );
                        }
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action. android.R.string.no
                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //find(R.id.mitraLAddrInput).setFocusableInTouchMode(true);
                        find(R.id.mitraLAddrInput).requestFocus();
                        //InputMethodManager imm = (InputMethodManager)   getSystemService(PartnerAdd.INPUT_METHOD_SERVICE);
                        //imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
        /*if (listener1 != null){
            if (find(R.id.mitraAddrInput, EditText.class).getText().length()>0){
                find(R.id.mitraLAddrInput, EditText.class).setText(
                        find(R.id.mitraAddrInput, EditText.class).getText().toString()
                );
            } else {
                find(R.id.mitraAddrInput, EditText.class).setError("Mohon Isi Alamat"
                );
            }

        } else if (listener2 != null){
            find(R.id.mitraLAddrInput).setFocusable(true);
        }*/

    }



    final int REQUEST_PHT       = 208;
    final int REQUEST_KTP    = 209;
    final int REQUEST_NPWP     = 210;
    final int REQUEST_JAMINAN       = 211;
    final int REQUEST_SPPI     = 212;
    final int REQUEST_BUKU     = 213;

}