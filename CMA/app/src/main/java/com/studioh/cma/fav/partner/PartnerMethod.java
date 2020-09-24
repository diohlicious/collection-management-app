package com.studioh.cma.fav.partner;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import com.naa.data.Dson;
import com.naa.data.Utility;
import com.naa.utils.InternetX;
import com.naa.utils.Messagebox;
import com.studioh.cma.AppActivity;
import com.studioh.cma.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class PartnerMethod extends AppActivity {

    public static String nFilterTmp(){
        Dson n = new Dson();//Dson.readJson(getSetting("Monthly"));
        final Dson nfiltertmp = Dson.newArray();
        for (int i = 0; i < n.size(); i++) {
            Dson one = n.get(i);
            Dson two = one.get("Address");
            for (int j = 0; j < two.size(); j++) {
                if (two.get(j).get("Priority").asString().equals("1")) {
                    Dson three = two.get(j);
                    Dson four = Dson.newObject();
                    four.set("kota", three.get("City"));
                    four.set("kelurahan", three.get("Kelurahan"));
                    four.set("kecamatan", three.get("Kecamatan"));
                    nfiltertmp.add(four);
                }
            }
        }
        return nfiltertmp.toJson();
    }

    public void performFilter(String filter) throws ParseException {
        nListArray.asArray().clear();
        final Dson nfilter = Dson.readJson(filter);
        Dson n = Dson.readJson(getSetting("Monthly"));
        Calendar newDate = Calendar.getInstance();
        DateFormat ydt = new SimpleDateFormat("yyyy", Locale.US);
        int os0 = Integer.parseInt(nfilter.get("os0").asString().equals("") ? "0" : nfilter.get("os0").asString());
        int os1 = Integer.parseInt(nfilter.get("os1").asString().equals("") ? "999999999" : nfilter.get("os1").asString());
        int yfrom = Integer.parseInt(nfilter.get("yfrom").asString().equals("") ?
                String.valueOf(Calendar.getInstance().get(Calendar.YEAR) - 10) :
                nfilter.get("yfrom").asString()
        );
        int yto = Integer.parseInt(nfilter.get("yto").asString().equals("") ?
                String.valueOf(Calendar.getInstance().get(Calendar.YEAR)) :
                nfilter.get("yto").asString()
        );
        for (int i = 0; i < n.size(); i++) {
            if (n.get(i).get("_A").asString().equalsIgnoreCase("1") ||
                    n.get(i).get("_B").asString().equalsIgnoreCase("1")) {
                if (n.get(i).get("AssetType").asString().contains(nfilter.get("kendaraan").asString())
                        && isWithinRangeOs(n.get(i).get("OSPrincipalAmount").asInteger(), os0, os1)
                        && isWithinRange(n.get(i).get("WODate").asString(),
                        yfrom,
                        yto)
                        && filterAddress(n.get(i).get("Address"),
                        nfilter.get("kota").asString(),
                        nfilter.get("kecamatan").asString(),
                        nfilter.get("kelurahan").asString())
                ) {
                    nListArray.add(n.get(i));
                }
            }
        }

        RecyclerView recyclerView = findViewById(R.id.rview);
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    public void showPopUp(final String aggrno,
                          final String sregional,
                          final String sarea,
                          final String sbranch) {
        final View v = Utility.getInflater(getActivity(), R.layout.popup_assign);
        final Spinner spinner = findView(v, R.id.pesanSpin, Spinner.class);
        final SimpleDateFormat fdt = new SimpleDateFormat("d MMM yyyy", Locale.US);
        final SimpleDateFormat gdt = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        final String[] info = {""};
        Calendar newCalendar = Calendar.getInstance();
        final DatePickerDialog StartTime = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                findView(v, R.id.infoInput, EditText.class).setText(fdt.format(newDate.getTime()));
                info[0] = gdt.format(newDate.getTime());
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        findView(v, R.id.calendar, ImageView.class).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartTime.show();
            }
        });
        final String[] smitra = new String[1];
        newProcess(new Messagebox.DoubleRunnable() {
            public void run() {

                Dson nson = getDefaultDataRaw();
                String res = postHttpRaw("RPM/RPM_GetDataMitra", nson);
                autoToken(res);
                nListArray = Dson.readJson(res).get("data");

            }

            public void runUI() {
                ArrayAdapter<List> adapter = new ArrayAdapter<List>(getActivity(), android.R.layout.simple_spinner_dropdown_item, nListArray.asArray()) {
                    public View getView(int position, @Nullable View convertView, ViewGroup parent) {
                        View v = super.getView(position, convertView, parent);
                        if (v instanceof TextView) {
                            ((TextView) v).setText(
                                    nListArray.get(position).get("MitraName").asString()
                                            + " | "
                                            + nListArray.get(position).get("RegisterNo").asString()
                            );
                        }
                        return v;
                    }

                    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        View v = super.getView(position, convertView, parent);
                        if (v instanceof TextView) {
                            ((TextView) v).setText(
                                    nListArray.get(position).get("MitraName").asString()
                                            + " | "
                                            + nListArray.get(position).get("RegisterNo").asString());
                        }
                        return v;
                    }
                };
                adapter.setDropDownViewResource(R.layout.spinner_item);
                spinner.setAdapter(adapter);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        smitra[0] = nListArray.get(position).get("MitraName").asString();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            }
        });


        AlertDialog.Builder dlg = new AlertDialog.Builder(getActivity());
        dlg.setView(v);
        dlg.setCancelable(false);
        final AlertDialog alertDialog = dlg.create();
        findView(v, R.id.cancel, View.class).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                performSearch();
                alertDialog.dismiss();
            }
        });

        findView(v, R.id.submit, View.class).setOnClickListener(new View.OnClickListener() {
            public void onClick(final View v) {
                newProcess(new Messagebox.DoubleRunnable() {
                    String result;
                    String res;

                    @Override
                    public void run() {
                        autoToken();
                        Dson nson = getDefaultDataRaw();
                        nson.set("AgreementNo", aggrno/*getSetting("ralAggrNo")*/);
                        nson.set("Mitra", smitra[0]);
                        nson.set("PrintDate", info[0]);
                        nson.set("_Regional", sregional);
                        nson.set("_Area", sarea);
                        nson.set("_Branch", sbranch);

                        setSetting("ppp", nson.toJson());
                        res = nson.toJson();
                        //String res = postHttpRaw("RPM/RPM_AssignmentRAL", nson);
                        result = InternetX.postHttpConnectionRaw(getBaseUrl("RPM/RPM_AssignmentRAL"), getDefaultHeader(), nson);
                    }

                    @Override
                    public void runUI() {
                        final Dson nson = Dson.readJson(result);
                        if (nson.get("ResponseCode").asString().equalsIgnoreCase("00")) {
                            Dson n = Dson.readJson(getSetting("Monthly"));
                            for (int i = 0; i < n.size(); i++) {
                                if (n.get(i).get("AgreementNo").asString().equalsIgnoreCase(aggrno)) {
                                    n.get(i).set("_isral", "1");
                                }
                            }
                            setSetting("Monthly", n.toJson());
                            showInfoDialog(nson.get("ResponseDescription").asString(), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    userStory("SubmitRAL", nson.get("ResponseDescription").asString());
                                    performSearch();
                                    dialog.dismiss();
                                }
                            });
                        } else if (nson.get("ResponseCode").asString().equalsIgnoreCase("99")) {
                            AddDataLog("SubmitRAL", nson.get("ResponseDescription").asString());
                            showInfo(nson.get("ResponseDescription").asString());
                        } else {
                            //change header
                            //add to response code 00
                            GoToURL("http://api.whatsapp.com/send?phone=" + "6287886722267" + "&text=" + res);
                            AddDataLog("SubmitRAL", result);
                            showInfo(result);
                        }
                    }
                });
            }
        });
        alertDialog.show();
    }

    public void viewral(final String aggrno) {
        newProcess(new Messagebox.DoubleRunnable() {
            String result;
            String res;

            @Override
            public void run() {
                autoToken();
                Dson nson = getDefaultDataRaw();
                nson.set("_AgreementNo", aggrno);
                res = nson.toJson();
                result = InternetX.postHttpConnectionRaw(getBaseUrl("RPM/RPM_RAL_VIEW"), getDefaultHeader(), nson);
            }

            @Override
            public void runUI() {
                final Dson nson = Dson.readJson(result);
                if (nson.get("ResponseCode").asString().equalsIgnoreCase("00")) {
                    if (nson.get("view").get(0).get("File_URL").asString().length() > 0) {
                        userStory("ViewRAL", nson.get("view").get(0).get("File_URL").asString());
                        GoToURL(nson.get("view").get(0).get("File_URL").asString());
                    } else {
                        showInfoDialog("Document belum tersedia!", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                userStory("ViewRAL", nson.get("view").get(0).get("File_URL").asString());
                                dialog.dismiss();
                            }
                        });
                    }
                } else if (nson.get("ResponseCode").asString().equalsIgnoreCase("99")) {
                    AddDataLog("ViewRAL", nson.get("ResponseDescription").asString());
                    showInfo(nson.get("ResponseDescription").asString());
                } else {
                    //change header
                    //add to response code 00
                    AddDataLog("ViewRAL", res);
                    //GoToURL("http://api.whatsapp.com/send?phone=" + "6287886722267" + "&text=" + res);
                    showInfo(result);
                }/*else {

                            showInfo(result);
                        }*/
            }
        });
    }

    public void performSearch() {
        nListArray.asArray().clear();
        String txt = find(R.id.txtSearch, EditText.class).getText().toString().toLowerCase();
        Dson n = Dson.readJson(getSetting("Monthly"));
        for (int i = 0; i < n.size(); i++) {
            if (n.get(i).get("_A").asString().equalsIgnoreCase("1") || n.get(i).get("_B").asString().equalsIgnoreCase("1")) {
                if (n.get(i).get("CustomerFullName").asString().toLowerCase().contains(txt) ||
                        n.get(i).get("LicensePlate").asString().toLowerCase().contains(txt) ||
                        n.get(i).get("AgreementNo").asString().toLowerCase().contains(txt)) {
                    nListArray.add(n.get(i));
                }
            }
        }
        RecyclerView recyclerView = findViewById(R.id.rview);
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    public String userStory(String hline, String story) {
        SimpleDateFormat gdt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.US);
        autoToken();
        Dson nson = getDefaultDataRaw();
        nson.set("LogHeader", hline);
        nson.set("LogDesc", story);
        nson.set("LogDate", gdt.format(Calendar.getInstance().getTime()));
        String result = InternetX.postHttpConnectionRaw(getBaseUrl("RPM/RPM_AddDataLog"), getDefaultHeader(), nson);
        //autoToken(result);
        return result;
    }

    private void invalidateX() {
        Dson dataH = Dson.newObject();
        Dson nson = Dson.readDson(getSetting("Personal"));

        int quota = nson.get("Monthly_RPK").asInteger();
        double osp = nson.get("Monthly_Recovery_OSP").asDouble();
        int take = 0;
        for (int i = 0; i < nListArray.size(); i++) {
            Dson n = nListArray;
            if (n.get(i).get("_C").asString().equalsIgnoreCase("1")) {
                osp = osp - nListArray.get(i).get("OSPrincipalAmount").asDouble();
                take++;
            }
        }


        dataH.set("wo", nListArray.size());
        dataH.set("quota", quota);
        dataH.set("take", take);
        dataH.set("os", nListArray.size() - take);

        findView(R.id.h_bahan, R.id.txtMessage, TextView.class).setText(String.valueOf(quota));//Kuota Monthly_RPK
        findView(R.id.h_take, R.id.txtMessage, TextView.class).setText(String.valueOf(dataH.get("wo").asInteger()));//WO In
        findView(R.id.h_add, R.id.txtMessage, TextView.class).setText(String.valueOf(take));//Take In
        findView(R.id.h_os, R.id.txtMessage, TextView.class).setText(String.valueOf(dataH.get("os").asInteger()));//OS

        setSetting("dataH", dataH.toJson());
    }

    boolean isWithinRange(String testDate, int fromDate, int toDate) throws ParseException {
        DateFormat ydt = new SimpleDateFormat("yyyy", Locale.US);
        DateFormat ydtinput = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        int date = Integer.parseInt(ydt.format(ydtinput.parse(testDate)));
        return !(date < fromDate || date > toDate);
    }

    boolean isWithinRangeOs(int os, int os0, int os1) {
        return !(os < os0 || os > os1);
    }

    private boolean filterAddress(Dson naddr, String kota, String kecamatan, String kelurahan) {
        for (int i = 0; i < naddr.size(); i++) {
            if (naddr.get(i).get("Priority").asString().equals("1")
                    && naddr.get(i).get("City").asString().contains(kota)
                    && naddr.get(i).get("Kecamatan").asString().contains(kecamatan)
                    && naddr.get(i).get("Kelurahan").asString().contains(kelurahan)) {
                return true;
            }
        }
        return false;
    }
}
