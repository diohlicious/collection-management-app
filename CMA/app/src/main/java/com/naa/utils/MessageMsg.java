package com.naa.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;

public class MessageMsg {

    public static void newTask(Activity activity, Messagebox.DoubleRunnable run){
        new Thread(new Runnable() {
                public void run() {
                    run.run();
                    if (activity!=null  && !activity.isFinishing()){
                        activity.runOnUiThread(new Runnable() {
                            public void run() {
                                if (activity!=null && !activity.isFinishing()){
                                    run.runUI();
                                }
                            }
                        });
                    }
                }
                Activity activity;
                Messagebox.DoubleRunnable run;
                public Runnable get(Activity activity, Messagebox.DoubleRunnable run){
                    this.activity = activity;
                    this.run = run;
                    return this;
                }
        }.get(activity, run)) .start();

    }

    private static ProgressDialog showProgresBar(Context context, String message){
        ProgressDialog mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage(message);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        return mProgressDialog;
    }
    public static AlertDialog DialogBox(Activity context, String title, String message){
        AlertDialog.Builder dlg = new AlertDialog.Builder(context);
        dlg.setTitle(title);
        dlg.setMessage(message);
        return dlg.create();
    }
    public interface DoubleRunnable {
        public void run();
        public void runUI();
    }
    public static void showProsesBar(Activity activity, Messagebox.DoubleRunnable run){
        new Thread(new Runnable() {
            public void run() {
                run.run();
                if (activity!=null  && !activity.isFinishing()){
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            if (activity!=null && !activity.isFinishing()){
                                prb.dismiss();
                                run.runUI();
                            }
                        }
                    });
                }
            }
            Activity activity;
            Messagebox.DoubleRunnable run;
            private ProgressDialog prb;
            public Runnable get(Activity activity, Messagebox.DoubleRunnable run, ProgressDialog prb){
                this.activity = activity;
                this.run = run;
                this.prb = prb;
                return this;
            }
        }.get(activity, run, showProgresBar(activity, "Please Wait . . . "))) .start();
    }
}
