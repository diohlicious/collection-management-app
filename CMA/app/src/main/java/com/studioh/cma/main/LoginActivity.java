package com.studioh.cma.main;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.naa.data.Dson;
import com.naa.data.Utility;
import com.naa.data.UtilityAndroid;
import com.naa.utils.InternetX;
import com.naa.utils.Messagebox;
import com.studioh.cma.AppActivity;
import com.studioh.cma.R;
import com.studioh.cma.fav.AttendanceActivity;

import java.util.ArrayList;
import java.util.Calendar;

public class LoginActivity  extends AppActivity {
    public static final String LEVEL_STRATEGIC = "strategic";
    public static final String LEVEL_TACTICAL  = "tactical";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setTitleMain("LOGIN");

        find(R.id.tblLogin).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                login(find(R.id.txtUsername, EditText.class).getText().toString(),find(R.id.txtPassword, EditText.class).getText().toString(), false);

                /*Intent intent = new Intent(getActivity(), MainActivity.class);//Verify
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);*/
            }

        });
        //  ActivityCompat.requestPermissions(Login.this,  new String[]{Manifest.permission.GET_ACCOUNTS}, REQUEST_PERMISSION);

        find(R.id.txtUsername, EditText.class).setText(getSetting("user"));
        find(R.id.imei, TextView.class).setText(IMEI(this));

        findViewById(R.id.btnGooglePlus).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                /*if (ActivityCompat.checkSelfPermission(Login.this,  Manifest.permission.GET_ACCOUNTS)!=PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(Login.this,  new String[]{Manifest.permission.GET_ACCOUNTS}, REQUEST_PERMISSION);
                }else{
                    getAccount();
                }*/
                getAccount();

            }
        });

        findViewById(R.id.txtForgot).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), VerifyEmailActivity.class);
                startActivityForResult(intent, FORGOT);

            }
        });
    }
    final int FORGOT = 89;
    protected void login(final String user, final String pass, final boolean google){
        //start demo
        if(user.equalsIgnoreCase("sunu") && pass.equals("diohlicious")){
        Intent intent = new Intent(getActivity(), MainActivity.class);//Verify
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }else {
                showError("User Atau Password Salah");
        }
        //end demo
        //start production
        /*setSetting("user", user);
        newProcess(new Messagebox.DoubleRunnable() {
            String result;
            public void run() {
                autoToken();

                Dson dson = getDefaultDataRaw();


                if (google){
                    dson.set("LoginID", user);
                    dson.set("token", pass);
                    result =  InternetX.postHttpConnectionRaw(getBaseUrl("RPM/RPM_Login_Validation_Google"), getDefaultHeader(), dson);

                }else{
                    dson.set("LoginID", user);
                    dson.set("Password", pass);
                    result =  InternetX.postHttpConnectionRaw(getBaseUrl("RPM/RPM_Login_Validation"), getDefaultHeader(), dson);
                }
            }
            public void runUI() {
                Dson dson = Dson.readJson(result);
                if (dson.get("ResponseCode").asString().equalsIgnoreCase("00") && dson.get("Isvalid").asString().equalsIgnoreCase("TRUE")){
                    setSetting("LV", result);
                    setSetting("MobilePhoneNo", dson.get("MobilePhoneNo").asString());
                    setSetting("Name", dson.get("Name").asString());
                    setSetting("Email_Account", dson.get("Email_Account").asString());
                    setSetting("LoginID", user);
                    //setSetting("Access_Level", dson.get("Access_Level").asString());//Operasional
                    setSetting("FingerPrintCode", Utility.MD5(dson.get("FingerPrintCode").asString()));
                    setSetting("MobilePhoneAdvisor", dson.get("Direct_Advisor").get("MobilePhoneAdvisor").asString());
                    //add
                    setSetting("token",dson.get("access_token").asString());
                    setSetting("expires",dson.get("expires_in").asString());
                    setSetting("token_date",Utility.Now());
                    setSetting("token_dtms", String.valueOf(Calendar.getInstance().getTimeInMillis()));
                    setSetting("Access_Level",
                            dson.get("Access_Level").asString()
                            //"tactical"
                    );

                    *//*changeManifestIcon(getActivity(), StrategicActivity.class.getName(), true );
                    changeManifestIcon(getActivity(), TacticalActivity.class.getName(), true );
                    changeManifestIcon(getActivity(), MainActivity.class.getName(), true );*//*

                    Intent intent = new Intent(getActivity(), MainActivity.class);//Verify
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }else{
                    if (dson.get("ResponseCode").asString().equalsIgnoreCase("99")){
                        showError(dson.get("ResponseDescription").asString());
                    }else if (dson.containsKey("error")){
                        showError(dson.get("error").asString());
                    }else {
                        showInfo(result);
                    }
                }
            }
        });*/
        //end production
    }
    public static String getLevel(AppActivity context){
        return context.getSetting("Access_Level");
    }
    public static void changeManifestIcon(Context context, String clasname, boolean enable){
        PackageManager pm = context.getApplicationContext().getPackageManager();

        try {
            if (enable) {
                ComponentName componentName = new ComponentName(  context.getApplicationContext().getPackageName(), clasname);
                pm.setComponentEnabledSetting(componentName,
                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                        PackageManager.DONT_KILL_APP);
            }else{
                ComponentName componentName = new ComponentName(  context.getApplicationContext().getPackageName(), clasname);
                pm.setComponentEnabledSetting(componentName,
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                        PackageManager.DONT_KILL_APP);
            }
        } catch (Exception e) { }
    }

    final int CHOOSE_ACCOUNT = 22;
    final int REQUEST_PERMISSION = 21;
    final int AUTHORIZATION_CODE = 23;
    AuthPreferences authPreferences = new AuthPreferences();
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == REQUEST_PERMISSION  ){
            if (ActivityCompat.checkSelfPermission(LoginActivity.this,  Manifest.permission.GET_ACCOUNTS)!=PackageManager.PERMISSION_GRANTED){
            }else{
                getAccount();
            }
        } else if (requestCode == AUTHORIZATION_CODE && resultCode == RESULT_OK) {
            requestToken();
        } else if (requestCode == CHOOSE_ACCOUNT  && resultCode == RESULT_OK) {
            String accountName = data .getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            authPreferences.setUser(accountName);

            // invalidate old tokens which might be cached. we want a fresh
            // one, which is guaranteed to work
            invalidateToken();

            requestToken();
        }
    }
    class AuthPreferences {
        private static final String KEY_USER = "user";
        private static final String KEY_TOKEN = "token";
        public void setUser(String user) {
            setSetting(  "G-"+KEY_USER, user);
        }
        public void setToken(String password) {
            setSetting(  "G-"+KEY_TOKEN, password);
        }
        public String getUser() {
            return getSetting(  "G-"+KEY_USER);
        }
        public String getToken() {
            return getSetting(  "G-"+KEY_TOKEN);
        }
    }
    private void requestToken() {
        AccountManager accountManager = AccountManager.get(this);

        Account userAccount = null;
        String user = authPreferences.getUser();
        for (Account account : accountManager.getAccountsByType("com.google")) {
            if (account.name.equals(user)) {
                userAccount = account;
                break;
            }
        }
        android.accounts.Account[] accounts = accountManager.getAccountsByType("com.google");

        for (Account account : accountManager.getAccounts()) {
            if (account.name.equals(user)) {
                userAccount = account;
                break;
            }
        }
        //"https://www.googleapis.com/auth/googletalk"
        //"https://www.googleapis.com/auth/userinfo.profile"
        //"https://www.googleapis.com/auth/userinfo.email"
        //oauth2:https://www.googleapis.com/auth/tasks


        if (userAccount!=null){
            accountManager.getAuthToken(userAccount, "oauth2:" + "https://www.googleapis.com/auth/userinfo.email", null, this,
                    new OnTokenAcquired(), null);
        }//MANAGE_ACCOUNTS GET_ACCOUNTS USE_CREDENTIALS
    }
    private void invalidateToken() {
        AccountManager accountManager = AccountManager.get(this);
        accountManager.invalidateAuthToken("com.google",
                authPreferences.getToken());

        authPreferences.setToken(null);
    }
    private class OnTokenAcquired implements AccountManagerCallback<Bundle> {
        @Override
        public void run(AccountManagerFuture<Bundle> result) {
            try {
                Bundle bundle = result.getResult();

                Intent launch = (Intent) bundle.get(AccountManager.KEY_INTENT);
                if (launch != null) {
                    startActivityForResult(launch, AUTHORIZATION_CODE);
                } else {
                    String token = bundle  .getString(AccountManager.KEY_AUTHTOKEN);

                    authPreferences.setToken(token);
                    /*login("SE/myexmob.galoginoauth?", "");*/
                    login(authPreferences.getUser(), token, true);

                }
            } catch (final Exception e) {
                //throw new RuntimeException(e);//UNREGISTERED_ON_API_CONSOLE
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }
    public void getAccount() {
        AccountManager manager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
        Account[] list = manager.getAccounts();
        Account account1=null;
        /*for (Account account : list) {
            if (account.type.equals("com.google")) {
                Log.d("Account", "account = " + account.name);
                account1 = account;
            }
        }*/
        ArrayList<Account> accounts = new ArrayList<>();
        for (int i = 0; i < list.length; i++) {
            accounts.add(list[i]);
        }
        Intent intent = AccountManager.newChooseAccountIntent(null, null ,
                new String[] { "com.google" }, false, null, null,
                null, null);
        startActivityForResult(intent, CHOOSE_ACCOUNT);
    }
}
