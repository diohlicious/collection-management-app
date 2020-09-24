package com.studioh.cma;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.naa.data.Dson;
import com.naa.data.Utility;
import com.naa.data.UtilityAndroid;
import com.naa.utils.InternetX;
import com.naa.utils.MessageMsg;
import com.naa.utils.Messagebox;

import org.json.JSONArray;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AppActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        super.onCreate(savedInstanceState);

        JSONArray permissionSet = new JSONArray();
        checkset(permissionSet, Manifest.permission.GET_ACCOUNTS);
        checkset(permissionSet, Manifest.permission.CAMERA);
        checkset(permissionSet, Manifest.permission.CALL_PHONE);
        checkset(permissionSet, Manifest.permission.ACCESS_FINE_LOCATION);
        checkset(permissionSet, Manifest.permission.ACCESS_COARSE_LOCATION);
        checkset(permissionSet, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        checkset(permissionSet, Manifest.permission.READ_EXTERNAL_STORAGE);
        checkset(permissionSet, Manifest.permission.USE_FINGERPRINT);
        checkset(permissionSet, Manifest.permission.READ_PHONE_STATE);

        if (permissionSet.length() >= 1) {
            ActivityCompat.requestPermissions(this, asString(permissionSet), REQUEST_PERMISSION);
        }

    }

    public static String[] asString(JSONArray array) {
        if (array == null) {
            String[] s = new String[0];
            return s;
        }
        String[] arr = new String[array.length()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = array.optString(i);
        }
        return arr;
    }


    public String validateNomorWA(String ph) {
        ph = String.valueOf(ph).trim();
        if (ph.startsWith("+")) {
            return ph.substring(1);
        } else if (ph.startsWith("0")) {
            return "62" + ph.substring(1);
        } else {
            return ph;
        }
    }

    public String viewNomorPh(String ph) {
        ph = String.valueOf(ph).trim();
        if (ph.startsWith("+")) {
            return "0" + ph.substring(3);
        } else if (ph.startsWith("62")) {
            return "0" + ph.substring(2);
        } else {
            return ph;
        }
    }
    //https://apidev.sitama.co.id/api/RPM/RPM_AddDataLog

    public void checkset(JSONArray permissionSet, String permission) {
        if (ActivityCompat.checkSelfPermission(getActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
            permissionSet.put(permission);
        }
    }

    public static boolean isRotate_90(String path) {
        ExifInterface ei;
        try {
            ei = new ExifInterface(path);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90 | ExifInterface.ORIENTATION_ROTATE_270:
                    return true;
                default:
                    return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    public static void rotateCameraAuto(String checkFile, String file) {
        int move = 0;
        ExifInterface ei;
        try {
            ei = new ExifInterface(checkFile);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    move = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    move = -90;
                    break;
                default:
                    move = 0;
            }
        } catch (Exception e) {
            move = 0;
        }
        if (move != 0) {
            try {
                Bitmap bitmap = BitmapFactory.decodeFile(file);
                Matrix matrix = new Matrix();
                matrix.postRotate(move);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

                FileOutputStream fos = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
                fos.close();
            } catch (Exception e) {
            }
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
        onCompressImage(pfile, 80, 1366, 1366);
        /*if (isRotate_90(photo.getAbsolutePath())){
            rotate(pfile, 90);
        }*/
        rotateCameraAuto(photo.getAbsolutePath(), pfile);

        //imageView.setImageURI(Uri.fromFile(photo));
        viewImage(imageView, pfile, 256);//photo.getAbsolutePath()
        imageView.setTag(pfile);
        //camera
    }

    private static Bitmap rotateImageIfRequired(Context context, Bitmap img, Uri selectedImage) throws IOException {

        InputStream input = context.getContentResolver().openInputStream(selectedImage);
        ExifInterface ei;
        if (Build.VERSION.SDK_INT > 23)
            ei = new ExifInterface(input);
        else
            ei = new ExifInterface(selectedImage.getPath());

        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }

    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }

    public Dson distinct(Dson data, String field) {
        Dson keys = Dson.newObject();
        Dson dson = Dson.newArray();
        for (int i = 0; i < data.size(); i++) {
            if (keys.containsKey(data.get(i).get(field).asString())) {
            } else {
                keys.set(data.get(i).get(field).asString(), "");
                dson.add(data.get(i));
            }
        }
        return dson;
    }

    public void setTitle(CharSequence title) {
        super.setTitle(title);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    public final static int REQUEST_PERMISSION = 13;

    public void setTitleMain(CharSequence title) {
        super.setTitle(title);
    }

    public static String getBaseUrl(String name) {
        return AppApplication.getBaseUrl(name);
    }

    public String formatNopol(String s) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == ' ') {
            } else if (i >= 1) {

                if (Utility.isNumeric(stringBuilder.length() >= 1 ? stringBuilder.charAt(stringBuilder.length() - 1) + "" : "") != Utility.isNumeric(s.charAt(i) + "")) {
                    stringBuilder.append(" ");
                }
                stringBuilder.append(s.charAt(i));
            } else {
                stringBuilder.append(s.charAt(i));
            }
        }
        return stringBuilder.toString().trim().toUpperCase();
    }

    public void setSetting(String key, String value) {
        UtilityAndroid.setSetting(getActivity(), key, value);
    }

    public String getSetting(String key) {
        return UtilityAndroid.getSetting(getActivity(), key, "");
    }

    public final Dson getDefaultHeader() {
        Dson dson = Dson.newObject();
        dson.set("Authorization", "Bearer " + getSetting("token"));
        dson.set("FCMID", getSetting("FCMID"));
        return dson;
    }

    public static String IMEI(Context context) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return "";
            }
            String imei = telephonyManager.getDeviceId();
            Log.e("imei", "=" + imei);
            if (imei != null && !imei.isEmpty()) {
                return imei;
            } else {
                return android.os.Build.SERIAL;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "not_found";
    }

    public final Dson getDefaultDataRaw() {
        Dson dson = Dson.newObject();
        dson.set("LoginID", getSetting("LoginID")); //
        dson.set("MobilePhone", getSetting("MobilePhoneNo"));
        dson.set("App_Version", "1.0");
        dson.set("Lat", Utility.getDouble(getSetting("Lat")));
        dson.set("Long", Utility.getDouble(getSetting("Long")));
        dson.set("IMEI",
                IMEI(this)
                //"869792030275921"
        );
        return dson;
    }


    public final String postHttpRaw(String rpmAPI, String rowdata) {
        return InternetX.postHttpConnectionRaw(getBaseUrl(rpmAPI), getDefaultHeader(), rowdata);
    }

    public final String postHttpRaw(String rpmAPI, Dson rowdata) {
        return InternetX.postHttpConnectionRaw(getBaseUrl(rpmAPI), getDefaultHeader(), rowdata);
    }

    public final String postHttpRaw(String rpmAPI, Dson args, Dson fileName) {
        return InternetX.postHttpConnectionMultipart(getBaseUrl(rpmAPI), getDefaultHeader(), args, fileName);
    }

    public String autoToken() {
        return autoToken(null);
    }

    public String autoToken(String res) {
        if (res == null) {
        } else if (res.trim().equalsIgnoreCase("")) {
        } else if (res.trim().contains("Forbidden : Token is already expired.")) {

        } else {
            return "";
        }
        return null;
        /*Dson dson = Dson.newObject();
        dson.set("Username", "andika");
        dson.set("password", "123456789");
        String result =  InternetX.postHttpConnection(getBaseUrl("oauth/token"), dson);
        dson = Dson.readJson(result);


        if (dson.containsKey("access_token")){
            setSetting("token",dson.get("access_token").asString());
            setSetting("expires",dson.get("expires_in").asString());
            setSetting("token_date",Utility.Now());
            setSetting("token_dtms", String.valueOf(Calendar.getInstance().getTimeInMillis()));

            return null;
        }else{
            if (dson.containsKey("error")){
                return dson.get("error").asString();
            }else{
                return result;
            }
        }*/
    }

    protected void GoToURL(String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }


    public void setFilterUcase(EditText editText) {
        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter.AllCaps();
        editText.setFilters(filters);
    }

    public void mailMe(String semail, String sagrno) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("plain/text");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{semail});
        intent.putExtra(Intent.EXTRA_SUBJECT, sagrno);
        intent.putExtra(Intent.EXTRA_TEXT, "");
        startActivity(Intent.createChooser(intent, ""));
    }

    public Activity getActivity() {
        return this;
    }

    public void showInfo(String text) {
        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
    }

    public String getSelectedSpinnerText(int id) {
        View v = find(id, Spinner.class).getSelectedView();
        if (v instanceof TextView) {
            return to(v, TextView.class).getText().toString();
        }
        return "";
    }

    public void showError(String text) {
        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
    }

    public String getIntentStringExtra(String key) {
        return getIntentStringExtra(getIntent(), key);
    }

    public String getIntentStringExtra(Intent intent, String key) {
        if (intent != null && intent.getStringExtra(key) != null) {
            return intent.getStringExtra(key);
        }
        return "";
    }

    public void showInfoDialog(String message, DialogInterface.OnClickListener onClickListener) {
        if (onClickListener == null) {
            onClickListener = onClickListenerDismiss;
        }
        Messagebox.showDialog(getActivity(), "", message, "OK", "", onClickListener, null);
    }

    public void showInfoDialogyn(String message, DialogInterface.OnClickListener onClickListener) {
        if (onClickListener == null) {
            onClickListener = onClickListenerDismiss;
        }
        Messagebox.showDialog(getActivity(), "", message, "OK", "Cancel", onClickListener, onClickListenerDismiss);
    }

    private final DialogInterface.OnClickListener onClickListenerDismiss = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();

        }
    };

    public void textChangeListener(final EditText text, final Runnable runnable) {
        text.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
                runnable.run();
            }
        });
    }

    public void textChangeListenerDelay(final EditText text, final Runnable runnable) {
        text.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            String old = "";

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                old = s.toString();
            }

            final int live = 1000;
            long curr = System.currentTimeMillis();

            public void afterTextChanged(Editable s) {
                if (!old.equals(s.toString())) {
                    curr = System.currentTimeMillis();
                    text.postDelayed(new Runnable() {
                        public void run() {
                            if (Math.abs(System.currentTimeMillis() - curr) >= live) {
                                runnable.run();
                            }
                        }
                    }, live);
                }
            }
        });
    }

    public void currTextFormatOriginal(final EditText text) {
        text.addTextChangedListener(new TextWatcher() {
            boolean busy = false;

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
                if (!false) {
                    busy = true;
                    EditText inputValue = text;
                    inputValue.removeTextChangedListener(this);

                    int sel = inputValue.getSelectionStart();
                    int csel = inputValue.getText().toString().length() - sel;

                    String cur = s.toString();

                    cur = cur.replace(",", "");
                    cur = Utility.formatCurrency(cur);

                    csel = cur.length() - csel;
                    inputValue.setText(cur);
                    if (csel >= inputValue.length()) {
                        inputValue.setSelection(inputValue.length());
                    } else if (csel > cur.length()) {
                        inputValue.setSelection(cur.length());
                    } else {
                        inputValue.setSelection(csel, csel);
                    }
                    inputValue.addTextChangedListener(this);
                }
                busy = false;
            }
        });
    }

    public void currTextFormat(final EditText text) {
        text.addTextChangedListener(new TextWatcher() {
            boolean busy = false;

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
                if (!false) {
                    busy = true;
                    EditText inputValue = text;
                    inputValue.removeTextChangedListener(this);

                    int sel = inputValue.getSelectionStart();
                    int csel = inputValue.getText().toString().length() - sel;

                    String cur = s.toString();

                    cur = cur.replace(".", "");
                    cur = formatCurrencyUs(cur);

                    csel = cur.length() - csel;
                    inputValue.setText(cur);
                    if (csel >= inputValue.length()) {
                        inputValue.setSelection(inputValue.length());
                    } else if (csel > cur.length()) {
                        inputValue.setSelection(cur.length());
                    } else if (csel >= 0) {
                        inputValue.setSelection(csel, csel);
                    }
                    inputValue.addTextChangedListener(this);
                }
                busy = false;
            }
        });
    }

    public static String formatCurrencyUs(String original) {
        if (original.contains(",")) {
            StringBuilder stringBuilder = new StringBuilder();
            int il = original.indexOf(",");
            if (original.startsWith("-")) {
                stringBuilder.append("-");
                original = original.substring(1);
            }
            stringBuilder.append(Utility.insertStringRev(original.substring(0, il), ".", 3));
            stringBuilder.append(original.substring(il));
            return stringBuilder.toString();
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            if (original.startsWith("-")) {
                stringBuilder.append("-");
                original = original.substring(1);
            }
            stringBuilder.append(Utility.insertStringRev(original, ".", 3));
            return stringBuilder.toString();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public static void viewImage(ImageView img, String absolutePath) {
        viewImage(img, absolutePath, 128);
    }

    public Dson nListArray = Dson.newArray();

    public final void runOnActionThread(Runnable action) {
        new Thread(action).start();
    }

    public void notifyDataSetChanged(int id) {
        notifyDataSetChanged(findViewById(id));

    }

    public void notifyDataSetChanged(View view) {
        if (view instanceof ListView) {
            if (((ListView) view).getAdapter() instanceof ArrayAdapter) {
                ((ArrayAdapter) ((ListView) view).getAdapter()).notifyDataSetChanged();
            }
        } else if (view instanceof GridView) {
            if (((GridView) view).getAdapter() instanceof ArrayAdapter) {
                ((ArrayAdapter) ((GridView) view).getAdapter()).notifyDataSetChanged();
            }
        } else if (view instanceof Spinner) {
            if (((Spinner) view).getAdapter() instanceof ArrayAdapter) {
                ((ArrayAdapter) ((Spinner) view).getAdapter()).notifyDataSetChanged();
            }
        } else if (view instanceof RecyclerView) {
            if (((RecyclerView) view).getAdapter() != null) {
                ((RecyclerView) view).getAdapter().notifyDataSetChanged();
            }
        }

    }

    public static void viewImage(ImageView img, String absolutePath, int wmax) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(absolutePath, options);
        int scale = options.outWidth / wmax;


        options = new BitmapFactory.Options();
        options.inSampleSize = scale;
        Bitmap bmp = BitmapFactory.decodeFile(absolutePath, options);

        img.setImageBitmap(bmp);
//
//        try {
//            img.setImageBitmap(rotateImageIfRequired(img.getContext(),bmp, Uri.fromFile(new File(absolutePath))));
//        }catch (IOException e){
//            img.setImageBitmap(bmp);
//        }
        //Picasso.get().load(new File(absolutePath)).into(img);
    }

    public static void onCompressImage(String file, int quality, int width, int maxpx) {
        String format = "jpg";
        width = width <= 10 ? 540 : width;
        maxpx = maxpx <= 10 ? 540 : maxpx;
        quality = 80;//hardocde

        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(file, options);
            int scale = options.outWidth / width;
            if (maxpx > 1) {
                scale = Math.max(options.outWidth, options.outHeight) / maxpx;
            }

            options = new BitmapFactory.Options();
            options.inSampleSize = scale + 1;
            Bitmap bmp = BitmapFactory.decodeFile(file, options);

            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(format.equalsIgnoreCase("jpg") ? Bitmap.CompressFormat.JPEG : Bitmap.CompressFormat.PNG, quality, fos);

            fos.flush();
            fos.close();
        } catch (Exception e) {
        }
    }

    public static void rotate(String file, final int move) {
        //mmust on other thread
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(file);
            Matrix matrix = new Matrix();
            matrix.postRotate(move);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
        }
    }


    public <T extends View> T to(View v, Class<? super T> s) {
        return (T) (v);
    }

    public <T extends View> T find(int id) {
        return (T) findViewById(id);
    }

    public <T extends View> T find(int id, Class<? super T> s) {
        return (T) findViewById(id);
    }

    public <T extends View> T findView(View v, int id, Class<? super T> s) {
        return (T) v.findViewById(id);
    }

    public <T extends View> T findView(int v, int id, Class<? super T> s) {
        return (T) find(v).findViewById(id);
    }


    protected void onCreateA(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler() {
            public void handleMessage(Message msg) {
                if (msg.what == 2) {
                    check(AppActivity.this);
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.rkrzmail.loyalty");
        registerReceiver(receiver, filter);

    }

    protected void onDestroyA() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private Handler handler;
    AlertDialog alertDialog;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction() != null) {
                if (intent.getAction().equalsIgnoreCase("com.rkrzmail.loyalty")) {
                    check(context);
                }
            }
        }
    };

    public void call(String ph) {
        String s = "tel:+" + validateNomorWA(ph);
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(s));
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            getActivity().startActivity(intent);
        } else {
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1);
        }
    }

    private void check(Context context) {
        if (haveNetworkConnection()) {
                        /*findViewById(R.id.navigation).setVisibility(View.VISIBLE);
                        findViewById(R.id.content).setVisibility(View.VISIBLE);*/

            if (alertDialog != null && alertDialog.isShowing()) {
                alertDialog.dismiss();
            }

        } else {
            //log
                        /*findViewById(R.id.navigation).setVisibility(View.INVISIBLE);
                        findViewById(R.id.content).setVisibility(View.INVISIBLE);*/

            if (alertDialog != null && alertDialog.isShowing()) {
            } else if (alertDialog != null) {
                alertDialog.show();
            } else {
                AlertDialog.Builder dlg = new AlertDialog.Builder(context);

                dlg.setTitle("No Internet Connection");
                dlg.setMessage("Please Check your connection ");
                dlg.setCancelable(false);

                alertDialog = dlg.create();
                alertDialog.show();
            }
            handler.removeMessages(2);
            //check jangan jangan 30d lagi conect
            handler.sendEmptyMessageDelayed(2, 30000);

        }
    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    public void onBackPressed() {
        super.onBackPressed();
        if (handler != null) {
            handler.removeMessages(1);
            handler.removeMessages(2);
        }
    }

    public void newTask(Messagebox.DoubleRunnable runnable) {
        MessageMsg.newTask(this, runnable);
    }

    public void newProcess(Messagebox.DoubleRunnable runnable) {
        MessageMsg.showProsesBar(this, runnable);
    }

    /*public void showNotification(Dson dson) {


        //Get an instance of NotificationManager//
        Intent intentAction = new Intent(this, HomeActivity.class);
        intentAction.putExtra("target", "notification");
        intentAction.putExtra("id", dson.get("id").asString());
        intentAction.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);


        PendingIntent pIntentlogin = PendingIntent.getBroadcast(this, 1, intentAction, PendingIntent.FLAG_UPDATE_CURRENT);


        intentAction = new Intent(this, HomeActivity.class);
        intentAction.putExtra("action", "TUNDA");
        intentAction.putExtra("id", dson.get("id").asString());
        PendingIntent pIntentlogin3 = PendingIntent.getBroadcast(this, 2, intentAction, PendingIntent.FLAG_UPDATE_CURRENT);


        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("target", "notification");
        PendingIntent resultIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);


        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_add_data)
                        //.addAction(new NotificationCompat.Action(0, "OK", pIntentlogin))
                        //.addAction(new NotificationCompat.Action(0, "TUNDA", pIntentlogin3))
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(dson.get("msg").asString()))
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setOngoing(dson.get("outgoing").asBoolean())
                        .setContentTitle(dson.get("title").asString())
                        .setContentIntent(resultIntent)
                        .setAutoCancel(true)
                        .setContentText(dson.get("msg").asString());

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        mNotificationManager.notify(dson.get("id").asInteger(), mBuilder.build());
    }*/


    private final int maxTimeOutms = 9000;
    private double gpsLive = 0;
    private Location currlocation;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private long ob = 0;

    public static boolean isGPSEnable(Context mContext) {
        boolean isGPSEnabled = false;
        LocationManager locationManager = (LocationManager) mContext
                .getSystemService(mContext.LOCATION_SERVICE);
        isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isGPSEnabled;
    }

    public void reqGPS() {
        reqGPS(null);
    }

    public void reqGPS(final Runnable onfinish) {
        if (isGPSEnable(getActivity())) {
            //wailt for gps
            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setExpirationDuration(3000);// 1 menit

            if (mFusedLocationClient == null) {
                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
            }
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "Maaf, permision GPS ditolak", Toast.LENGTH_LONG).show();
                //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_PERMISSION);
                finish();
                return;
            }
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mFusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        currlocation = location;
                    }
                }
            });

            gpsLive = System.currentTimeMillis();
            mLocationCallback = new LocationCallback() {
                public void onLocationResult(LocationResult locationResult) {
                    for (Location location : locationResult.getLocations()) {
                        if (location != null) {
                            currlocation = location;

                            if (Math.abs(System.currentTimeMillis() - gpsLive) > maxTimeOutms) {
                                if (mLocationCallback != null && mFusedLocationClient != null) {
                                    mFusedLocationClient.removeLocationUpdates(mLocationCallback);
                                }
                            }

                        }
                    }
                }

                ;
            };
            mFusedLocationClient.requestLocationUpdates(locationRequest,
                    mLocationCallback,
                    null /* Looper */);

            new Thread(new Runnable() {
                public void run() {
                    Location location = null;
                    ob = System.currentTimeMillis();
                    while (Math.abs(System.currentTimeMillis() - ob) < maxTimeOutms) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                if (activity != null && !activity.isFinishing()) {
                                    try {
                                        prb.setMessage("Search Location . . . (" + (int) ((maxTimeOutms - Math.abs(System.currentTimeMillis() - ob)) / 1000) + ")");
                                    } catch (Exception e) {
                                    }
                                }
                            }
                        });
                        try {
                            Thread.sleep(1000);//1 detik
                        } catch (Exception e) {
                        }
                        location = currlocation;
                        try {
                            if (location != null) {
                                if (location.getAccuracy() <= 50) {
                                    break;
                                }
                            }
                        } catch (Exception e) {
                        }
                    }
                    AppApplication.setLastCurrentLocation(location);
                    if (activity != null && !activity.isFinishing()) {
                        activity.runOnUiThread(new Runnable() {
                            public void run() {
                                if (activity != null && !activity.isFinishing()) {
                                    try {
                                        prb.dismiss();
                                    } catch (Exception e) {
                                    }
                                    if (currlocation != null) {
                                        setSetting("Lat", String.valueOf(currlocation.getLatitude()));
                                        setSetting("Long", String.valueOf(currlocation.getLongitude()));
                                    }
                                    if (onfinish != null) {
                                        onfinish.run();
                                    }
                                }
                            }
                        });
                    }
                }

                Activity activity;
                private ProgressDialog prb;

                public Runnable get(Activity activity, ProgressDialog prb) {
                    this.activity = activity;
                    this.prb = prb;
                    return this;
                }
            }.get(getActivity(), showProgresBar(getActivity(), "Search Location . . . "))).start();
        } else {

            Toast.makeText(getActivity(), "GPS belum On, silahakan diaktifkan", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private ProgressDialog showProgresBar(Context context, String message) {
        ProgressDialog mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage(message);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        try {
            // mProgressDialog.show();
        } catch (Exception e) {
        }
        return mProgressDialog;
    }

    public void AddDataLog(final String hdr,
                           final String des) {
        newProcess(new Messagebox.DoubleRunnable() {
            String res;
            String result;

            public void run() {
                Dson dson = getDefaultDataRaw();
                Dson hdrs = getDefaultHeader();
                DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                dson.set("LogHeader", hdr);
                dson.set("LogDesc", des);
                dson.set("LogDate", formatter.format(new Date(System.currentTimeMillis())));

                res = InternetX.postHttpConnectionRaw(getBaseUrl("RPM/RPM_AddDataLog"), hdrs, dson);
                result = dson.toJson();
            }

            public void runUI() {
                Dson dson = Dson.readJson(res);
                if (dson.get("ResponseCode").asString().equalsIgnoreCase("00")) {
                } else {
                    showError(res);
                }
            }
        });
    }
    //Demo use only
    public String isToString(InputStream input) throws IOException {
        final int bufferSize = 1024;
        final char[] buffer = new char[bufferSize];
        final StringBuilder out = new StringBuilder();
        Reader in = new InputStreamReader(input, "UTF-8");
        for (; ; ) {
            int rsz = in.read(buffer, 0, buffer.length);
            if (rsz < 0)
                break;
            out.append(buffer, 0, rsz);
        }
        return out.toString();
    }


    public void ClickLogout(View view) {
        logout(this);
    }

    public static void logout(final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("LOGOUT");
        builder.setMessage("Are You Sure You Want To Logout?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                activity.finishAffinity();
                System.exit(0);
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private String saveToInternalStorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat phtStamp = new SimpleDateFormat("yyMMdd", new Locale("id","ID","ID"));
        String formattedphtStamp = phtStamp.format(c) + "profile.jpg";

        File mypath=new File(directory,formattedphtStamp);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    private void getFotoCam(ImageView view)
    {
        try {
            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            // path to /data/data/yourapp/app_data/imageDir
            File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
            // Create imageDir
            Date c = Calendar.getInstance().getTime();
            SimpleDateFormat phtStamp = new SimpleDateFormat("yyMMdd", new Locale("id","ID","ID"));
            String formattedphtStamp = phtStamp.format(c) + "profile.jpg";

            File f=new File(directory, formattedphtStamp);
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            ImageView img = view;
            img.setImageBitmap(b);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

    }

}

