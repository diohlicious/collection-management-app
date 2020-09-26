package com.naa.data;

import android.annotation.SuppressLint;
import android.app.AlertDialog.Builder;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources.NotFoundException;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.provider.ContactsContract;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.studioh.cma.AppApplication;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Vector;
import java.util.zip.GZIPInputStream;


@SuppressLint({"SimpleDateFormat", "DefaultLocale"})
public class Utility {
	private static Context defContext;
	private static Context sevContext;
	public static int KeepRatioW;
	public static int flagLogin;
	public static String JOBCODE;
	public static String OFFICE_CODE;
	public static String ttdPath;
	private static int mYear, mMonth, mDay;
	private static String[] arrMonth = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
	private static String[] nameMonth = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
	public static double latitude, longitude, Accuracy;
	public static boolean fromSPK = false;
	public static boolean showservice = false, showNext;
	public static ArrayList<String> phoneValueArr = new ArrayList<String>();
	public static ArrayList<String> nameValueArr = new ArrayList<String>();

	public static String getDisplayNumberForContact(Intent intent) {
		Cursor phoneCursor = getAppContext().getContentResolver().query(intent.getData(), null, null, null, null);
		phoneCursor.moveToFirst();
		int numberid = phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
		String number = phoneCursor.getString(numberid);
		phoneCursor.close();
		return number;
	}

	public static String getDisplayNameForContact(Intent intent) {
		Cursor phoneCursor = getAppContext().getContentResolver().query(intent.getData(), null, null, null, null);
		phoneCursor.moveToFirst();
		int idDisplayName = phoneCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
		String name = phoneCursor.getString(idDisplayName);
		phoneCursor.close();
		return name;
	}

	public static boolean isGPSEnable(Context mContext) {
		boolean isGPSEnabled = false;
		LocationManager locationManager = (LocationManager) mContext
				.getSystemService(mContext.LOCATION_SERVICE);
		isGPSEnabled = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
		return isGPSEnabled;
	}

	public static boolean checkWriteExternalPermission(Context context) {
		String permission = "android.permission.WRITE_EXTERNAL_STORAGE";
		int res = context.checkCallingOrSelfPermission(permission);
		return (res == PackageManager.PERMISSION_GRANTED);
	}

	public static boolean hasPermissionInManifest(Context context, String permissionName) {
		final String packageName = context.getPackageName();
		try {
			final PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
			final String[] declaredPermisisons = packageInfo.requestedPermissions;
			if (declaredPermisisons != null && declaredPermisisons.length > 0) {
				for (String p : declaredPermisisons) {
					if (p.equals(permissionName)) {
						return true;
					}
				}
			}
		} catch (PackageManager.NameNotFoundException e) {

		}
		return false;
	}

	public static String urlEncode(String stringURL) {
		return URLEncoder.encode(stringURL);
	}

	public static double getDouble(Object n) {
		return getNumber(n).doubleValue();
	}

	//	public static float getFloat(String s) {
//		return getNumber(s).floatValue();
//	}
	public static Number getNumber(Object n) {
		if (n instanceof Number) {
			return ((Number) n);

		} else if (isDecimalNumber(String.valueOf(n))) {
			return Double.valueOf(String.valueOf(n));
		} else if (isLongIntegerNumber(String.valueOf(n))) {
			return Long.valueOf(String.valueOf(n));
		}
		return 0;
	}

	public static String getNumberPointOnly(String s) {
		StringBuilder buf = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			if ("01234567890.".indexOf(s.charAt(i)) != -1) {
				buf.append(s.charAt(i));
			}
		}
		return buf.toString();
	}

	public static String getNumberOnly(String s) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {
			if ("01234567890".indexOf(s.charAt(i)) != -1) {
				buf.append(s.charAt(i));
			}
		}
		return buf.toString();
	}

	public static String getCharOnly(String str, String s) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {
			if (str.indexOf(s.charAt(i)) != -1) {
				buf.append(s.charAt(i));
			}
		}
		return buf.toString();
	}

	public static boolean isNumeric(String str) {
		return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
	}

	public static boolean isDecimalNumber(String str) {
		return str.matches("^[-+]?[0-9]*.?[0-9]+([eE][-+]?[0-9]+)?$");
	}

	public static boolean isLongIntegerNumber(String str) {
		return str.matches("-?\\d+");
	}

	public static String phone;

	public static String findContact(Context context, String number) {
		String nama = "";
		try {

			/*********** Reading Contacts Name And Number **********/
			String phoneNumber = "";
			ContentResolver cr = context.getContentResolver();
			//Query to get contact name
			Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

			// If data data found in contacts
			if (cur.getCount() > 0) {

				/***AutocompleteContacts", "Reading   contacts........***/
				int k = 0;
				String name = "";

				while (cur.moveToNext()) {
					String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
					name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

					//Check contact have phone number
					if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {

						//Create query to get phone number by contact id
						Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID
								+ " = ?", new String[]{id}, null);
						int j = 0;
						while (pCur.moveToNext()) {
							// Sometimes get multiple data
							if (j == 0) {
								// Get Phone number
								phoneNumber = "" + pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

								if (phoneNumber.equalsIgnoreCase(number)) {
									return phoneNumber;
								}
								j++;
								k++;
							}
						}  // End while loop
						pCur.close();
					} // End if

				}  // End while loop

			} // End Cursor value check
			cur.close();

		} catch (Exception e) {
			return "";
		}

		return nama;
	}

	public static ArrayList<String> getAllcontact(Context context) {
		try {

			/*********** Reading Contacts Name And Number **********/
			String phoneNumber = "";
			ContentResolver cr = context.getContentResolver();
			//Query to get contact name
			Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

			// If data data found in contacts
			if (cur.getCount() > 0) {

				/***AutocompleteContacts", "Reading   contacts........***/
				int k = 0;
				String name = "";

				while (cur.moveToNext()) {
					String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
					name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

					//Check contact have phone number
					if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {

						//Create query to get phone number by contact id
						Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID
								+ " = ?", new String[]{id}, null);
						int j = 0;
						while (pCur.moveToNext()) {
							// Sometimes get multiple data
							if (j == 0) {
								// Get Phone number
								phoneNumber = "" + pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

								// Add contacts names to adapter
								// Add ArrayList names to adapter
								phoneValueArr.add(phoneNumber.toString());
								nameValueArr.add(name.toString());
								j++;
								k++;
							}
						}  // End while loop
						pCur.close();
					} // End if

				}  // End while loop

			} // End Cursor value check
			cur.close();

		} catch (Exception e) {
		}
		return null;
	}

	public static String getJobDesc(String jobCode) {
		if (jobCode.equalsIgnoreCase("001")) {
			return "Direksi";
		} else if (jobCode.equalsIgnoreCase("002")) {
			return "Manager";
		} else if (jobCode.equalsIgnoreCase("003")) {
			return "Supervisor";
		} else if (jobCode.equalsIgnoreCase("004")) {
			return "Sales";
		} else if (jobCode.equalsIgnoreCase("005")) {
			return "Admin";
		}
		return "";
	}

	public static ProgressDialog showProgresbar(Context context, String message) {
		ProgressDialog mProgressDialog = new ProgressDialog(context);
		mProgressDialog.setMessage(message);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//		mProgressDialog.setIndeterminate(true);
		mProgressDialog.setCancelable(false);
		if (!mProgressDialog.isShowing()) {
			mProgressDialog.show();
		}
		return mProgressDialog;
	}

	public static void makeToast(Context context, String message) {
		Toast.makeText(context, message, Toast.LENGTH_LONG).show();
	}

	public static boolean isAnEmail(String value) {
		boolean isAnEmail = false;
		return isAnEmail = value.trim().matches("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
	}

	public static boolean isContainSpecialChar(String value) {
		if (value.equals("")) {
			return false;
		}
		for (int i = 0; i < value.length(); i++) {
			if ("qwertyuiopasdfghjklzxcvbnm1234567890QWERTYUIOPASDFGHJKLZXCVBNM ".contains(value.substring(i, i + 1))) {

			} else {
				return true;
			}
		}
		return false;

	}

	public static String getGMT() {
		int i = TimeZone.getDefault().getRawOffset() / 1000;
		return String.valueOf(i / 60 / 60);
		/*try {
			SimpleDateFormat sdfGmt = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
			sdfGmt.setTimeZone(TimeZone.getTimeZone("GMT"));

			SimpleDateFormat sdflocal = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");

			//time in GMT
			String timeZone = "";
			timeZone = sdflocal.parse(sdfGmt.format(new Date())).toString();

			String[] lc = split(timeZone, " ");
//			String ddds = lc[4].substring(lc[4].indexOf("+")+1, lc[4].indexOf(":"));;
//			ddds = ddds.substring(1);
			timeZone = lc[4].substring(lc[4].indexOf("+") + 1, lc[4].indexOf(":"));

			return timeZone.substring(1);

		} catch (ParseException e) {

		}
		return "";*/
	}

	public static String getDefaultSign() {
		return getDefaultPath("Sign");
	}

	public static String getDefaultSignPath(String fname) {
		return getDefaultPath("Sign/") + fname;
	}

	public static boolean isContainChar(String value) {
		if (value.equals("")) {
			return false;
		}
		for (int i = 0; i < value.length(); i++) {
			if ("qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM".contains(value.substring(i, i + 1))) {
				return true;
			}
		}
		return false;

	}
	/*public static boolean isContainSpecialChar(String value){
		String regex1 = "^[a-zA-Z0-9]*$]";
		boolean contain = value.trim().matches(regex1);
		if (contain) {
			return true;
		}
		return false;
//		try {
//			String regex1 = "^[a-zA-Z0-9~@#$^*()_+=[\\]{}|\\,.?: -]*$]";
//			Pattern regex = Pattern.compile(regex1);
//			Matcher matcher = regex.matcher(value);
//			if (matcher.find()){
//			    // Do something
//				return true;
//			}
//		} catch (Exception e1) {
//			// TODO Auto-generated catch block
//			return false;
//		}
//		return false;
	}*/

	public static boolean isOnline(Context context) {
		try {
			ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			return cm.getActiveNetworkInfo().isConnectedOrConnecting();
		} catch (Exception e) {
			return false;
		}
	}

	/*public static boolean wifiActive(Context context) {
		try {
			ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (wifi.isConnected()) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}*/




	public static boolean certificateFIFExist() {
		boolean isCertExist = false;
		try {
			KeyStore ks = KeyStore.getInstance("AndroidCAStore");
			if (ks != null) {
				ks.load(null, null);
				Enumeration aliases = ks.aliases();
				while (aliases.hasMoreElements()) {
					String alias = (String) aliases.nextElement();
					java.security.cert.X509Certificate cert = (java.security.cert.X509Certificate) ks.getCertificate(alias);

					if (cert.getIssuerDN().getName().contains("FIF-Certificate")) {
						isCertExist = true;
						break;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (java.security.cert.CertificateException e) {
			e.printStackTrace();
		}
		return isCertExist;
	}

	public static String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return Formatter.formatIpAddress(inetAddress.hashCode());//inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (Exception ex) {
			Log.e("IP Address", ex.toString());
		}
		return null;
	}

	@SuppressWarnings("static-access")
	public static String getIpAddressWifi(Context context) {
		try {
			WifiManager wm = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
			String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
			return ip;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "0.0.0.0";
	}

	public static boolean cellularNetworkDataActive(Context context) {
		boolean mobileDataEnabled = false; // Assume disabled
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		try {
			Class cmClass = Class.forName(cm.getClass().getName());
			Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
			method.setAccessible(true);
			// Make the method callable
			// get the setting for "mobile data"
			mobileDataEnabled = (Boolean) method.invoke(cm);
		} catch (Exception e) {
			// Some problem accessible private API
			// TODO do whatever error handling you want here
		}
		return mobileDataEnabled;
	}

	public static Calendar convertDate(String strDate) {
		Calendar calendar = Calendar.getInstance();
		try {
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
			date = sdf.parse(strDate);
			calendar.set(date.getYear() + 1900, date.getMonth(), date.getDate(), date.getHours(), date.getMinutes(), 0);

		} catch (Exception e) {
		}
		return calendar;
	}

	public static String getDate2() {
		return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
	}

	public static String changeFormatDate(String strDate, String formatawal, String formatakhir) {
		try {

			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat(formatawal, Locale.ENGLISH);
			SimpleDateFormat sdf2 = new SimpleDateFormat(formatakhir, Locale.ENGLISH);

			date = sdf.parse(strDate);
			return sdf2.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";

	}

	public static String convertDate2(String strDate) {
		try {

			Date date = new Date();

			SimpleDateFormat sdf = new SimpleDateFormat("dd-mm-yyy");
			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-mm-dd");

			date = sdf.parse(strDate);
			return sdf2.format(date);
		} catch (Exception e) {
		}
		return "";

	}

	@SuppressLint("SimpleDateFormat")
	public static String getTodayDate() {
		return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(new Date());
	}

	@SuppressLint("SimpleDateFormat")
	public static String getTodayDateIKB() {
		return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
	}

	@SuppressLint("SimpleDateFormat")
	public static String getDateStandart() {
		return new SimpleDateFormat("dd-MM-yyyy").format(new Date());
	}

	@SuppressLint("SimpleDateFormat")
	public static String getDateDAF() {
		return new SimpleDateFormat("dd-MMM-yyy").format(new Date());
	}


	public static void setDecimalCurr(View view, String param) {

		DecimalFormat formatter = (DecimalFormat) DecimalFormat.getCurrencyInstance();
		DecimalFormatSymbols symbols = new DecimalFormatSymbols();
//		symbols.setCurrencySymbol("IDR ");
		symbols.setCurrencySymbol("");
		symbols.setMonetaryDecimalSeparator(',');
		symbols.setGroupingSeparator('.');
		formatter.setDecimalFormatSymbols(symbols);
		String backCome = "";
//		if (param.contains(".")) {
//			backCome = param.substring(param.indexOf(".")+1);
//			param =  param.substring(0, param.indexOf("."));
//		}

		if (view.getClass() == TextView.class) {
			TextView textView = (TextView) view;
			textView.setText(formatter.format(Double.parseDouble(param)) + backCome);
		} else if (view.getClass() == EditText.class) {
			EditText editText = (EditText) view;
			editText.setText(formatter.format(Double.parseDouble(param)) + backCome);
		}

	}

	public static String getDecimalCurr(String param) {

		try {

			DecimalFormat formatter = (DecimalFormat) DecimalFormat.getCurrencyInstance();
			DecimalFormatSymbols symbols = new DecimalFormatSymbols();
//			symbols.setCurrencySymbol("IDR ");
			symbols.setCurrencySymbol("");
			symbols.setMonetaryDecimalSeparator(',');
			symbols.setGroupingSeparator('.');
			formatter.setDecimalFormatSymbols(symbols);

			return formatter.format(Double.parseDouble(param));
		} catch (Exception e) {
			// TODO: handle exception
			return "0";
		}

	}

	public static String getCurr(String param) {
		if (param.contains(".")) {
			return Utility.formatCurrency(param.substring(0, param.indexOf(".")));
		}
		return Utility.formatCurrency(param);

	}

	@SuppressLint("SimpleDateFormat")
	public static String getTime() {
		return new SimpleDateFormat("HH:mm:ss").format(new Date());
	}

	@SuppressLint("SimpleDateFormat")
	public static String getTime2() {
		return new SimpleDateFormat("HH:mm a").format(new Date());
	}

	public static InputFilter uppercase = new InputFilter() {

		@Override
		public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
			// TODO Auto-generated method stub
			for (int i = start; i < end; i++) {
				if (Character.isLowerCase(source.charAt(i))) {
					return source.toString().toUpperCase(Locale.getDefault());
				}
			}
			return null;

		}
	};



	private static String right(String s, int len) {
		if (s.length() >= len) {
			return s.substring(s.length() - len, s.length());
		}
		return s;
	}

	public static DatePickerDialog showDatePickerLessThan(Context context, EditText v, long minDate) {
		Calendar c = Calendar.getInstance();
		DatePickerDialog datePicker = new DatePickerDialog(context, setDatePickerDialog2(v), c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
		datePicker.getDatePicker().setMinDate(minDate);

		return datePicker;
	}

	public static DatePickerDialog.OnDateSetListener setDatePickerDialog2(final EditText edt) {

		DatePickerDialog.OnDateSetListener mDateSetListenerOrder = new DatePickerDialog.OnDateSetListener() {

			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				String sdate = LPad(dayOfMonth + "", "0", 2) + "-" + arrMonth[monthOfYear] + "-" + year;
				edt.setText(sdate);

			}
		};

		return mDateSetListenerOrder;
	}

	public static DatePickerDialog showDatePicker(Context context, EditText v) {
		Calendar c = Calendar.getInstance();
		return new DatePickerDialog(context, setDatePickerDialog(v), c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
	}

	public static DatePickerDialog showDatePickerLessThanSysdate(Context context, View v) {
		Calendar c = Calendar.getInstance();
		DatePickerDialog datePicker = new DatePickerDialog(context, specialDate(v), c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
		datePicker.getDatePicker().setMaxDate(System.currentTimeMillis());

		return datePicker;
	}

	public static DatePickerDialog.OnDateSetListener setDatePickerDialog(final EditText edt) {

		DatePickerDialog.OnDateSetListener mDateSetListenerOrder = new DatePickerDialog.OnDateSetListener() {

			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				mYear = year;
				mMonth = monthOfYear;
				mDay = dayOfMonth;
				String sdate = LPad(mDay + "", "0", 2) + "-" + arrMonth[mMonth] + "-" + mYear;
				edt.setText(sdate);

			}
		};

		return mDateSetListenerOrder;
	}

	public static String LPad(String schar, String spad, int len) {
		String sret = schar;
		for (int i = sret.length(); i < len; i++) {
			sret = spad + sret;
		}
		return new String(sret);
	}

	public static DatePickerDialog.OnDateSetListener specialDate(final View v) {

		DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				mYear = year;
				mMonth = monthOfYear;
				mDay = dayOfMonth;
//				String sdate = LPad(mDay + "", "0", 2) + "-" + nameMonth[mMonth] + "-" + mYear ;
				String sdate = LPad(dayOfMonth + "", "0", 2) + "-" + nameMonth[monthOfYear] + "-" + year;
				if (v.getClass() == EditText.class) {
					EditText edit = (EditText) v;
					edit.setText(sdate);
				} else if (v.getClass() == TextView.class) {
					TextView text = (TextView) v;
					text.setText(sdate);
				}
			}
		};

		return date;
	}


	public static long getDateYear() {
		Calendar cal = Calendar.getInstance();
		cal.set(cal.get(Calendar.YEAR) - 60, cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
		return cal.getTimeInMillis();
	}

	public static long getDateMin() {
		Calendar cal = Calendar.getInstance();
//		cal.set(date.getYear(), date.getMonth(), date.getDay()-1);
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH) - 1);
//		cal.set(mYear, mMonth, mDay-1);
		return cal.getTimeInMillis();
	}

	public static long getTodayDateMin() {
		Calendar cal = Calendar.getInstance();
//		cal.set(date.getYear(), date.getMonth(), date.getDay()-1);
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
//		cal.set(mYear, mMonth, mDay-1);
		return cal.getTimeInMillis();
	}


	public static InputFilter doubleSpaceNSpecialCharFilter = new InputFilter() {
		@Override
		public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
			// TODO Auto-generated method stub
			for (int i = start; i < end; i++) {
				if ((Character.isSpace(source.charAt(i)) && dend == 0) || (Character.isSpace(source.charAt(i)) &&
						dend > 0 && Character.isSpace(dest.charAt(dend - 1))) || !Character.isLetterOrDigit(source.charAt(i))) {
					// return source.toString().substring(0,
					// source.toString().length() - 2);
					return "";
				}
			}
			return null;

		}
	};

	public static InputFilter specialCharFilter = new InputFilter() {
		@Override
		public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
			// TODO Auto-generated method stub

			boolean isContainSpecialChar = Utility.isContainSpecialChar(source.toString());

			if (source != null && isContainSpecialChar) {
				return "";
			}
			return null;

		}
	};


	public static String replace(String _text, String _searchStr, String _replacementStr) {
		StringBuffer sb = new StringBuffer();
		int searchStringPos = _text.indexOf(_searchStr);
		int startPos = 0;
		int searchStringLength = _searchStr.length();
		while (searchStringPos != -1) {
			sb.append(_text.substring(startPos, searchStringPos)).append(_replacementStr);
			startPos = searchStringPos + searchStringLength;
			searchStringPos = _text.indexOf(_searchStr, startPos);
		}
		sb.append(_text.substring(startPos, _text.length()));
		return sb.toString();
	}

	public static String getDate() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
	}


	public static Context getAppContext() {
		return AppApplication.getInstance();
	}

	public static String getExternalPath() {
		try {
			if (getAppContext().getExternalFilesDir(null).getPath().length() >= 3) {
				return getAppContext().getExternalFilesDir(null).getPath() + "/";
			} else {
				return null;
			}
		} catch (Exception e) {
			return null;
		}
	}

	public static String getDefaultPath() {
     String defpath = getExternalPath();
//		if (defpath!=null) {
//			if (SettingActivity.URL_STORAGE==null || SettingActivity.INTERNAL_STORAGE==null) {
//			}else if (SettingActivity.URL_STORAGE.equals(SettingActivity.INTERNAL_STORAGE)) {
//				//return  appContext.getFilesDir().getPath() + "/";
////				return Environment.getExternalStorageDirectory().toString();
//			}
//			return defpath;
//		}
		//return getAppContext().getExternalFilesDir("").getPath() + "/"; //getAppContext().getFilesDir().getPath() + "/";
//		return "/storage/sdcard0/EXACT/";
		return getAppContext().getFilesDir().getPath() + "/";
	}

	public static String getCacheDir(String fname) {
		//File s = getAppContext().getCacheDir();
        try {
            if (getAppContext().getCacheDir()!=null) {
                return getAppContext().getCacheDir().getPath() + "/" + fname;
            }else if (getAppContext().getFilesDir()!=null){
                return getAppContext().getFilesDir().getPath() + "/" + fname;
            }else if (getAppContext().getExternalFilesDir("")!=null){
                return getAppContext().getExternalFilesDir("").getPath() + "/" + fname;

            }else  {

            }

        } catch (Exception e) {

        }
		return "";
		//return getAppContext().getCacheDir().getPath() + "/" + fname;
		//return getAppContext().getFilesDir().getPath() + "/" + fname;
	}



	public static String getDefaultImagePath() {
		return getDefaultPath("images");
	}

	public static String getDefaultImagePath(String fname) {
		return getDefaultPath("images") + fname;
	}

	public static String getDefaultPath(String fname) {
		return getDefaultPath() + fname;
	}

	public static void createFolderAll(String folder) {
		folder = folder.replace("//", "//");
		String[] s = split(folder, "/");
		String path = "";
		for (int i = 0; i < s.length - ((folder.endsWith("/") ? 0 : 1)); i++) {
			if (!s[i].equals("")) {
				path = path + s[i] + "/";
				createFolder(path);
			}
		}
	}

	public static Vector<Vector<String>> splitVector(String original, String separatorcol, String separatorrow) {
		Vector<Vector<String>> nodes = new Vector<Vector<String>>();
		int index = original.indexOf(separatorrow);
		while (index >= 0) {
			nodes.addElement(splitVector(original.substring(0, index), separatorcol));
			original = original.substring(index + separatorrow.length());
			index = original.indexOf(separatorrow);
		}
		nodes.addElement(splitVector(original, separatorcol));
		return nodes;
	}

	public static Vector<String> splitVector(String original, String separator) {
		Vector<String> nodes = new Vector<String>();
		int index = original.indexOf(separator);
		while (index >= 0) {
			nodes.addElement(original.substring(0, index));
			original = original.substring(index + separator.length());
			index = original.indexOf(separator);
		}
		nodes.addElement(original);
		return nodes;
	}

	public static String getDataSplit(String data, String separator, boolean first) {
		Vector<String> dataString = Utility.splitVector(data, separator);
		if (dataString.size() > 0) {
			if (first) {
				return dataString.elementAt(0);
			} else {
				return dataString.elementAt(1);
			}
		}
		return "";
	}

	public static String[] split(String original, String separator) {
		Vector<String> nodes = splitVector(original, separator);
		String[] result = new String[nodes.size()];
		if (nodes.size() > 0) {
			for (int loop = 0; loop < nodes.size(); loop++) {
				result[loop] = (String) nodes.elementAt(loop);
			}
		}
		nodes.removeAllElements();
		return result;
	}

	/*public static String postHttpConnection(String stringURL, String... param) {
		Hashtable<String, String> arg = new Hashtable<String, String>();
		for (int i = 0; i < param.length; i++) {
			int key = param[i].indexOf("=");
			if (key != -1) {
				arg.put(param[i].substring(0, key), param[i].substring(key + 1));
			}
		}
		return ImagePost.postHttpConnectionWithPicture(stringURL, arg, null, null);
	}*/


	public static final String MD5(final String s) {
		try {
			// Create MD5 Hash
			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.update(s.getBytes());
			byte messageDigest[] = digest.digest();

			// Create Hex String
			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < messageDigest.length; i++) {
				String h = Integer.toHexString(0xFF & messageDigest[i]);
				while (h.length() < 2)
					h = "0" + h;
				hexString.append(h);
			}
			return hexString.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return "";
	}

	public static void openPDF(Context context, String filename) {
		File pdfFile = new File(filename);
		if (pdfFile.exists()) {
			Uri path = Uri.fromFile(pdfFile);
			Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
			pdfIntent.setDataAndType(path, "application/pdf");
			pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

			try {
				context.startActivity(pdfIntent);
			} catch (ActivityNotFoundException e) {
				Toast.makeText(context, "No Application available to view pdf", Toast.LENGTH_LONG).show();
			}
		}
	}

	public static void openSignature(ImageView context, String path) {
		openSignature(context, path, 0);
	}

	public static void OpenImage(ImageView context, String path) {
		OpenImage(context, path, 0);
	}

	public static void openSignature(ImageView context, String path, int defImage){
		try {
			Utility.KeepRatioW=128;

			if (path.trim().equals("")) {
				context.setImageResource(defImage);
				return;
			} else if (!new File(path).exists()) {
				context.setImageResource(defImage);
				return;
			}
			int scale = 1;// int height=1;
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(path, options);

			if (options.outWidth > context.getResources().getDisplayMetrics().widthPixels || options.outHeight > context.getResources().getDisplayMetrics().heightPixels) {
				scale = options.outWidth / Utility.KeepRatioW;// 960px
				scale = scale + (((options.outWidth - Utility.KeepRatioW * scale) >= 1) ? 1 : 0);
				scale = (scale % 2 != 0 ? (scale + 1) : scale);
				// height=options.outHeight*context.getResources().getDisplayMetrics().widthPixels/options.outWidth;
			} else {
				// height=context.getResources().getDisplayMetrics().heightPixels;
			}


			/*if (options.outWidth > Utility.KeepRatioW || options.outHeight > Utility.KeepRatioW) {
				scale = options.outWidth / Utility.KeepRatioW;// 960px
				//scale = scale + (((options.outWidth - Utility.KeepRatioW * scale) >= 1) ? 1 : 0);
				scale = (scale % 2 != 0 ? (scale + 1) : scale);
				// height=options.outHeight*context.getResources().getDisplayMetrics().widthPixels/options.outWidth;
			} else {
				// height=context.getResources().getDisplayMetrics().heightPixels;
			}*/

			// ((LayoutParams)context.getLayoutParams()).height=height;
			// ((LayoutParams)context.getLayoutParams()).width=getResources().getDisplayMetrics().widthPixels;
			options = new BitmapFactory.Options();
			options.inSampleSize = scale;
			context.setImageBitmap(BitmapFactory.decodeFile(path, options));
		} catch (Exception e) {
			context.setImageResource(defImage);
		}
	}

	public static void OpenImage(ImageView context, String path, int defImage) {
		try {
			Utility.KeepRatioW=128;

			if (path.trim().equals("")) {
				context.setImageResource(defImage);
				return;
			} else if (!new File(path).exists()) {
				context.setImageResource(defImage);
				return;
			}
			int scale = 1;// int height=1;
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(path, options);
			/*
			if (options.outWidth > context.getResources().getDisplayMetrics().widthPixels || options.outHeight > context.getResources().getDisplayMetrics().heightPixels) {
				scale = options.outWidth / Utility.KeepRatioW;// 960px
				//scale = scale + (((options.outWidth - Utility.KeepRatioW * scale) >= 1) ? 1 : 0);
				scale = (scale % 2 != 0 ? (scale + 1) : scale);
				// height=options.outHeight*context.getResources().getDisplayMetrics().widthPixels/options.outWidth;
			} else {
				// height=context.getResources().getDisplayMetrics().heightPixels;
			}
			*/

			if (options.outWidth > Utility.KeepRatioW || options.outHeight > Utility.KeepRatioW) {
				scale = options.outWidth / Utility.KeepRatioW;// 960px
				//scale = scale + (((options.outWidth - Utility.KeepRatioW * scale) >= 1) ? 1 : 0);
				scale = (scale % 2 != 0 ? (scale + 1) : scale);
				// height=options.outHeight*context.getResources().getDisplayMetrics().widthPixels/options.outWidth;
			} else {
				// height=context.getResources().getDisplayMetrics().heightPixels;
			}

			// ((LayoutParams)context.getLayoutParams()).height=height;
			// ((LayoutParams)context.getLayoutParams()).width=getResources().getDisplayMetrics().widthPixels;
			options = new BitmapFactory.Options();
			options.inSampleSize = scale;
			context.setImageBitmap(BitmapFactory.decodeFile(path, options));
		} catch (Exception e) {
			context.setImageResource(defImage);
		}
	}



	public static int getInt(String s) {
		try {
			return Integer.parseInt(s);
		} catch (Exception e) {
			return 0;
		}
	}

	public static long getLong(String s) {
		try {
			return Long.parseLong(s);
		} catch (Exception e) {
			return 0;
		}
	}

	public static View getInflater(Context context, int layout) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		return inflater.inflate(layout, null, false);
	}

	public static void showDialogSingleChoiceItems(Context context, String title, String[] data, int i, DialogInterface.OnClickListener listener) {
		Builder dlg = new Builder(context);
		dlg.setSingleChoiceItems(data, i, listener);
		if (title != null) {
			if (title.trim().length() >= 1) {
				dlg.setTitle(title);
			}
		}
		dlg.create().show();
	}

	public static void createFolder(String folder) {
		buildResource(new String[] { "mkdir", folder });
	}

	private static void buildResource(String[] str) {
		try {
			Process ps = Runtime.getRuntime().exec(str);
			try {
				ps.waitFor();
//				Log.v("Directory", "Directory");
			} catch (InterruptedException e) {
//				Log.v("Directory", "Directory ." + e.getMessage());
			}
		} catch (IOException e) {
//			Log.v("Directory", "Directory .." + e.getMessage());
		}
	}

	public static void removeFolder(String folder) {
		try {
			Process ps = Runtime.getRuntime().exec("rm -rf " + folder);
			try {
				ps.waitFor();
//				Log.v("rDirectory", "Directory " + folder);
			} catch (InterruptedException e) {
//				Log.v("rDirectory", "Directory ." + e.getMessage());
			}
		} catch (Exception e) {
		}

	}

	public static void deleteFileAll(String folder) {
		deleteFolderAll(new File(folder));
	}

	public static void deleteFile(String path){
		File file = new File(path);
		if (file.exists()) {
			file.delete();
		}
	}

	public static void deleteFolderAll(File dir) {
		try {
			for (File file : dir.listFiles()) {
				if (file.isFile()) {
					file.delete();
				} else {
					deleteFolderAll(file);
					file.delete();
				}
			}
		} catch (Exception e) {
		}
		dir.delete();
	}

	public static void copyFile(String origin, String destination) {
		try {
			copyFile(new FileInputStream(origin), destination);
		} catch (Exception e) { 	}
	}
	public static String readFile(InputStream is) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try {

			byte[] buffer = new byte[1024];
			int length;
			while ((length = is.read(buffer)) > 0) {
				byteArrayOutputStream.write(buffer, 0, length);
			}
			is.close();
		} catch (Exception e) {
		}
		return new String(byteArrayOutputStream.toByteArray());
	}
	public static void copyFile(InputStream is, String destination) {
		try {
			OutputStream os = new FileOutputStream(destination);

			byte[] buffer = new byte[1024];
			int length;
			while ((length = is.read(buffer)) > 0) {
				os.write(buffer, 0, length);
			}
			os.flush();
			os.close();
			is.close();
		} catch (Exception e) {
		}
	}

	public static int getNumberOnlyInt(String s) {
		return getInt(getNumberOnly(s));
	}
	public static int getInt2(String s) {
		return getNumber2(s).intValue();
	}
	public static long getLong2(String s) {
		return getNumber2(s).longValue();
	}
	public static double getDouble2(Object n) {
		return getNumber2(n).doubleValue();
	}
	public static float getFloat(String s) {
		return getNumber2(s).floatValue();
	}

	public static Number getNumber2(Object n) {
		if (n instanceof Number) {
			return ((Number)n);

		}else if (isDecimalNumber(String.valueOf(n))){
			return Double.valueOf(String.valueOf(n));
		}else if (isLongIntegerNumber(String.valueOf(n))){
			return Long.valueOf(String.valueOf(n));
		}
		return 0;
	}
	public static int getNumber(String s) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {
			if ("01234567890".indexOf(s.charAt(i)) != -1) {
				buf.append(s.charAt(i));
			}
		}
		try {
			return Integer.parseInt(buf.toString());
		} catch (Exception e) {
		}
		return 0;
	}

	public static String insertString(String original, String sInsert, int igroup) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < original.length(); i++) {
			if ((i % igroup) == 0 && igroup != 0 && i != 0) {
				sb.append(sInsert + original.substring(i, i + 1));
			} else {
				sb.append(original.substring(i, i + 1));
			}
		}
		return sb.toString();
	}

	public static String Now() {
		Calendar calendar = Calendar.getInstance();

		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime());
	}
	public static String formatTrack(String str) {
		if (str == null) {
			return "";
		}
		str = str.trim();
		if (str.startsWith(" ")){
			return str;
		}else if (str.startsWith("CON")){
			return "CON"+insertString(str.substring(3)," ", 4);
		}else if (str.startsWith("REF")){
			return "REF"+insertString(str.substring(3)," ", 4);
		}else{
			return insertString(str," ", 4);
		}
	}
	public static String formatCurrencyRound(double original) {
		double d = Math.round(original);
		return formatCurrency(String.valueOf(d));
	}

	public static String formatCurrencyBulat(double original) {
		return formatCurrencyBulat(String.valueOf(original));
	}
	public static String formatCurrencyBulat(String original) {
		if (original.contains(".")) {
			return formatCurrency(original.substring(0, original.indexOf(".")));
		}else{
			return formatCurrency(original);
		}
	}
    public static String formatCurrency(String original) {
        if (original.contains(".")) {
            StringBuilder stringBuilder = new StringBuilder();
            int il = original.indexOf(".");
            if (original.startsWith("-")){
                stringBuilder.append("-");
                original = original.substring(1);
            }
            stringBuilder.append(insertStringRev(original.substring(0, il), ",", 3));
            stringBuilder.append(original.substring(il));
            return  stringBuilder.toString();
        }else{
            StringBuilder stringBuilder = new StringBuilder();
            if (original.startsWith("-")){
                stringBuilder.append("-");
                original = original.substring(1);
            }
            stringBuilder.append(insertStringRev(original, ",", 3));
            return  stringBuilder.toString();
        }
    }

	public static String formatCurrencyA(String original) {
		return insertStringMax(insertStringRev(original, ",", 3), " ", 11);
	}

	public static String insertStringMax(String original, String sInsert, int max) {
		StringBuffer sb = new StringBuffer();
		for (int i = original.length(); i < max; i++) {
			sb.append(sInsert);
		}
		sb.append(original);
		return sb.toString();
	}

	public static String insertStringRev(String original, String sInsert, int igroup) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < original.length(); i++) {

			if (((original.length() - i) % igroup) == 0 && igroup != 0 && i != 0) {
				sb.append(sInsert + original.substring(i, i + 1));
			} else {
				sb.append(original.substring(i, i + 1));
			}
		}
		return sb.toString();
	}

	public static String notNull(String s) {
		if (s != null) {
			return s;
		}
		return "";
	}

	public static boolean isExit;

	public static void setExit(boolean isExit) {
		Utility.isExit = isExit;
	}

	public static boolean isExit() {
		return isExit;
	}

	public static long converDateToLong(String date) {

		SimpleDateFormat df = null;

		if (date.length() == 10) {
			if (date.contains("-")) {
				df = new SimpleDateFormat("dd-MM-yyyy");
			} else {
				df = new SimpleDateFormat("dd/MM/yyyy");
			}
		} else if (date.length() == 16) {
			if (date.contains("-")) {
				df = new SimpleDateFormat("dd-MM-yyyy HH:mm");
			} else {
				df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			}
		} else if (date.length() == 19) {
			if (date.contains("-")) {
				df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			} else {
				df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			}
		} else {
			if (date.contains("-")) {
				df = new SimpleDateFormat("dd-MM-yyyy");
			} else {
				df = new SimpleDateFormat("dd/MM/yyyy");
			}
		}

		try {
			Date dt = df.parse(date);
			long hasil = dt.getTime();
			return hasil;
		} catch (Exception e) {
		}

		return 0;
	}

	@SuppressLint("DefaultLocale")
	public static boolean containsChar(String kalimat, char character) {
		boolean isContain = false;
		String charLower = kalimat.toLowerCase();
		char[] charArray = charLower.toCharArray();
		for (int i = 0; i < charLower.length(); i++) {
			if (charArray[i] == character) {
				isContain = true;
				return isContain;
			}
		}
		return isContain;
	}

	// untuk mendapatkan font dari asset
	public static Typeface getFonttype(Context context) {
		return Typeface.createFromAsset(context.getAssets(), "BentonSansComp-Bold.ttf");
	}

	// untuk mendapatkan path dari images
	public static String getPathTheme(String nameFile) {
		return getDefaultPath() + "themes/" + nameFile;
	}



	// untuk menyimpan bitmap kedalam file di sdcard


	public static boolean isContainImages(String path) {
		boolean isThere = false;
		File file = new File(path);
		if (file.exists()) {
			isThere = true;
		} else {
			isThere = false;
		}
		return isThere;
	}

	public static void CopyStream(InputStream is, OutputStream os) {
		final int buffer_size = 1024;
		try {
			byte[] bytes = new byte[buffer_size];
			for (;;) {
				int count = is.read(bytes, 0, buffer_size);
				if (count == -1)
					break;
				os.write(bytes, 0, count);
			}
		} catch (Exception ex) {
		}
	}

	public static void i(String tag, String msg) {
//		Log.i(tag, msg);
	}

	/**
	 * checking contex application if null application close by system
	 *
	 */
	/*public static void checkingAppContex(Activity activity) {

		if (Utility.getAppContext() == null) {
			Utility.setExit(true);
			Intent intent = new Intent(activity, ma.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			activity.startActivity(intent);
		}
	}*/

	public static boolean isHaveExternalStorage() {

		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}

		return false;
	}

	public static void changeColorRowTable(int position, LinearLayout layout) {
		try {
			if (position != 0) {
				int sum = position % 2;
				if (sum != 0) {
					layout.setBackgroundColor(Color.parseColor("#c9c9ca"));
				}else {
					layout.setBackgroundColor(Color.parseColor("#ebeaea"));
				}
			}else {
				layout.setBackgroundColor(Color.parseColor("#ebeaea"));
			}
		} catch (NotFoundException e) {
			e.printStackTrace();
		}
	}

	/*public static void setImageTouch(final ImageView img) {
		img.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					img.setBackgroundResource(R.drawable.select);
				} else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
					img.setBackgroundResource(0);
				}
				return false;
			}
		});
	}*/

	public static void imagePost(String URL, String fileName) {

	}

	/*public static boolean isDuplicateImage(String Url) {
		boolean isDuplicate = false;
		Recordset server = Syncronizer.getHttpStream(Url);
		Vector<String> record = server.getRecordFromHeader("");
		for (int i = 0; i < record.size(); i++) {

		}
		return isDuplicate;
	}

	// untuk mengecek apakah record disuatu tabel sudah ada
	public static boolean isAvailableRecordTable(String table, String field, String record) {
		boolean isThere = false;
		try {
			Cursor c = Connection.DBexecute("select " + field + " from " + table + " where " + field + " = '" + record + "'");
			if (c.getCount() > 0) {
				c.moveToFirst();
				do {
					isThere = true;
				} while (c.moveToNext());
				c.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isThere;
	}
*/
	public static Vector<LinkedHashMap<String, String>> getRecordWithSeparate(Vector<String> data, String separate) {
		Vector<LinkedHashMap<String, String>> v = new Vector<LinkedHashMap<String, String>>();
		for (int i = 0; i < data.size(); i++) {
			if (data.elementAt(i).contains(separate)) {
				LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
				map.put(data.elementAt(i).substring(0, data.elementAt(i).indexOf(separate)), data.elementAt(i).substring(data.elementAt(i).indexOf(separate) + 1));
				v.add(map);
			}
		}
		return v;
	}


	public static int pxToDp(Context context, int px) {
		return (int)  ( px/context.getResources().getDisplayMetrics().density);
	}
	public static int dpToPx(Context context, int dp) {
		return (int)  (dp*context.getResources().getDisplayMetrics().density);
	}



	public static void downloadDB(String url, File directory){
		try {

			URL urlx = new URL(url);
			HttpURLConnection httpConn = (HttpURLConnection) urlx.openConnection();

			httpConn.getHeaderFields();
			String contentencode = httpConn.getContentEncoding()!=null?httpConn.getContentEncoding().toLowerCase():"";
			if (httpConn.getResponseCode()!=304) {

				InputStream is = httpConn.getInputStream();
				FileOutputStream fileOutputStream = new FileOutputStream(directory);


				byte[] buffer = new byte[1024*1024];
				int bufferLength = 0;

				if (contentencode.contains("gzip")) {
					GZIPInputStream gzipInputStream = new GZIPInputStream(is);

					while((bufferLength = gzipInputStream.read(buffer))>0 ){
						fileOutputStream.write(buffer, 0, bufferLength);
					}
				}else{
					while((bufferLength = is.read(buffer))>0 ){
						fileOutputStream.write(buffer, 0, bufferLength);
					}
				}


				fileOutputStream.close();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void downloadDBGzip(String url, File directory){
		try {

			URL urlx = new URL(url);
			HttpURLConnection httpConn = (HttpURLConnection) urlx.openConnection();
			InputStream is = httpConn.getInputStream();
			FileOutputStream fileOutputStream = new FileOutputStream(directory);
			int totalSize = httpConn.getContentLength();

			byte[] buffer = new byte[1024*1024];
			int bufferLength = 0;
			while((bufferLength = is.read(buffer))>0 ){
				fileOutputStream.write(buffer, 0, bufferLength);
			}
			fileOutputStream.close();

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void decompressGzipFile(String gzipFile, String newFile) {
		try {
        	/*ZipInputStream zis = new ZipInputStream(new FileInputStream(gzipFile));
        	byte[] buffer = new byte[1024];
        	ZipEntry ze = zis.getNextEntry();

        	while(ze!=null){

        	   String fileName = ze.getName();
               File newFile = new File(Utility.getDefaultPath() + File.separator + fileName);
//        	   File newFile = new File(newFiles);

               System.out.println("file unzip : "+ newFile.getAbsoluteFile());

                //create all non exists folders
                //else you will hit FileNotFoundException for compressed folder
                new File(newFile.getParent()).mkdirs();

                FileOutputStream fos = new FileOutputStream(newFile);

                int len;
                while ((len = zis.read(buffer)) > 0) {
                	fos.write(buffer, 0, len);
                }

                fos.close();
                ze = zis.getNextEntry();
        	}

            zis.closeEntry();
        	zis.close();*/

//            FileInputStream fis = new FileInputStream(gzipFile);
			GZIPInputStream gis = new GZIPInputStream(new FileInputStream(gzipFile));
			FileOutputStream fos = new FileOutputStream(newFile);
			byte[] buffer = new byte[1024];
			int len;
			while((len = gis.read(buffer)) != -1){
				fos.write(buffer, 0, len);
			}
			//close resources
			fos.close();
			gis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
