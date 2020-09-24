package com.studioh.cma.fav;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.naa.data.Dson;
import com.naa.data.Utility;
import com.naa.utils.Messagebox;
import com.studioh.cma.AppActivity;
import com.studioh.cma.R;
import com.studioh.cma.main.MainActivity;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.studioh.cma.AppActivity.viewImage;

public class AttendanceActivity extends AppActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);
        setTitle("Attendance");

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat daydate = new SimpleDateFormat("EEE, dd MMMM", new Locale("id","ID","ID"));
        String formattedDate = daydate.format(c);


        find(R.id.date, TextView.class).setText(formattedDate);

        final TextClock tClock = findViewById(R.id.time);
        tClock.setTimeZone ("Asia/Jakarta");

        final Chronometer chrono = (Chronometer) findViewById(R.id.worktime);
        chrono.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                long time = SystemClock.elapsedRealtime() - chronometer.getBase();
                int h = (int) (time / 3600000);
                int m = (int) (time - h * 3600000) / 60000;
                int s = (int) (time - h * 3600000 - m * 60000) / 1000;
                String t = (h < 10 ? "0" + h : h) + ":" + (m < 10 ? "0" + m : m) + ":" + (s < 10 ? "0" + s : s);
                chronometer.setText(t);
            }
        });
        chrono.setBase(SystemClock.elapsedRealtime());
        chrono.setText("00:00:00");

        if (getSetting("atdPht").equals("null")||getSetting("atdPht").length()==0){
            find(R.id.btnclockout, Button.class).setEnabled(false);
            find(R.id.imgTake, ImageView.class).setEnabled(true);
        }else {
            find(R.id.btnclockin, Button.class).setEnabled(false);
            find(R.id.imgTake, ImageView.class).setEnabled(false);
            find(R.id.imgTake, ImageView.class).setTag(getSetting("atdPht"));
            find(R.id.clockin, TextView.class).setText(getSetting("timeStart"));
            @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("kk:mm:ss");

            try {
                Date date1 = df.parse(df.format(new Date()));
                Date date2 = df.parse(getSetting("timeStart"));
                long diff = date1.getTime() - date2.getTime();
                int h = (int) (diff / 3600000);
                int m = (int) (diff - h * 3600000) / 60000;
                int s = (int) (diff - h * 3600000 - m * 60000) / 1000;
                String t = (h < 10 ? "0" + h : h) + ":" + (m < 10 ? "0" + m : m) + ":" + (s < 10 ? "0" + s : s);
                //chrono.setText(t);
                chrono.setBase(SystemClock.elapsedRealtime() - diff);
                chrono.start();
            } catch (ParseException e) {
                e.printStackTrace();

            }

        }
        final Dson ntracer = Dson.newObject();

        find(R.id.btnclockin, Button.class).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (String.valueOf(find(R.id.imgTake, ImageView.class).getTag()).equals("null")) {
                    showInfoDialog("Silahkan Take Photo", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                }else {

                    ntracer.set("clockIn", tClock.getText().toString());
                    ntracer.set("selfie",String.valueOf(find(R.id.imgTake, ImageView.class).getTag()));

                    chrono.setBase(SystemClock.elapsedRealtime());
                    chrono.start();
                    find(R.id.clockin, TextView.class).setText(ntracer.get("clockIn").asString());
                    setSetting("atdPht", String.valueOf(find(R.id.imgTake, ImageView.class).getTag()));
                    setSetting("timeStart", tClock.getText().toString());
                    find(R.id.btnclockin, Button.class).setEnabled(false);
                    find(R.id.btnclockout, Button.class).setEnabled(true);
                    setResult(RESULT_OK);
                    //production start
                    /*simpan(
                            tClock.getText().toString(),
                            "Check-in",
                            String.valueOf(find(R.id.imgTake, ImageView.class).getTag())
                    );*/
                    //demo start

                    //demo end
                    showInfoDialog("Jam Kerja Dimulai ", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            find(R.id.imgTake, ImageView.class).setEnabled(false);
                            dialog.dismiss();
                        }

                    });
                }
            }
        });

        Dson n = Dson.readJson(getSetting("Monthly"));
        for (int i = 0; i < n.size(); i++) {
            if (n.get(i).get("_DAYLY").asString().equalsIgnoreCase(Utility.Now().substring(0, 10))
                    && !n.get(i).containsKey("flag")
            ) {
                nListArray.add(n.get(i));
            }
        }

        find(R.id.btnclockout, Button.class).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInfoDialogyn("Stop Jam Kerja? ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(nListArray.size()>0){
                            showInfoDialog("Mohon Selesaikan Task Hari Ini!", null);
                        }else {
                            chrono.stop();
                            ntracer.set("clockOut", tClock.getText().toString());
                            ntracer.set("selfie", String.valueOf(find(R.id.imgTake, ImageView.class).getTag()));
                            find(R.id.clockout, TextView.class).setText(ntracer.get("clockOut").asString());
                            find(R.id.btnclockout, Button.class).setEnabled(false);
                            find(R.id.imgTake, ImageView.class).setEnabled(false);
                            simpan(
                                    tClock.getText().toString(),
                                    "Check-out",
                                    String.valueOf(find(R.id.imgTake, ImageView.class).getTag())
                            );
                            setSetting("atdPht", "");
                        }
                    }

                });
            }
        });
        findViewById(R.id.imgTake).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showCamera(REQUEST_SELFIE);
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSION);
                }
                else
                {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, REQUEST_SELFIE);
                }

            }
        });
    }

    protected void simpan(final String time,
                          final String tipe,
                          final String photo
    ) {
        newProcess(new Messagebox.DoubleRunnable() {
            String result;
            String sagrno = getSetting("agrno");
            String splate = getSetting("plate");

            public void run() {
                autoToken();
                Dson dson = getDefaultDataRaw();
                dson.set("Attendance_Time", time);
                dson.set("Type", tipe);

                Dson file = Dson.newObject();
                file.set("Photo",       photo);
                result = postHttpRaw("RPM/RPM_User_Attendance_WithSelfie", dson, file);
                autoToken(result);
            }

            public void runUI() {
                Dson dson = Dson.readJson(result);
                if (dson.get("ResponseCode").asString().equalsIgnoreCase("00")) {
                    showInfoDialog(dson.get("ResponseDescription").asString(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //showInfo(getSetting("nnn"));
                            /*Partial.this.finish();
                            dialog.dismiss();*/
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    });
                } else if (!dson.containsKey("ResponseDescription")) {
                    showInfo(result);
                } else {
                    showInfo(dson.get("ResponseDescription").asString());
                }
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SELFIE && resultCode == RESULT_OK) {
            getCamera("REQUEST_SELFIE", data, find(R.id.imgTake, ImageView.class));
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            ImageView imageView = find(R.id.imgTake, ImageView.class);
            imageView.setImageBitmap(photo);
            imageView.setTag(photo);
        }
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
        onCompressImage( pfile , 80,1366,1366 );
        /*if (isRotate_90(photo.getAbsolutePath())){
            rotate(pfile, 90);
        }*/
        rotateCameraAuto(photo.getAbsolutePath(), pfile);

        //imageView.setImageURI(Uri.fromFile(photo));
        viewImage(imageView, pfile, 256);//photo.getAbsolutePath()
        imageView.setTag(pfile);
        //camera

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, REQUEST_SELFIE);
            }
            else
            {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    final int REQUEST_SELFIE = 214;
    final int REQUEST_PERMISSION = 2140;
}
