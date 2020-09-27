package com.studioh.cma.main.homeadapter;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.naa.data.Dson;
import com.naa.data.Utility;
import com.studioh.cma.R;
import com.squareup.picasso.Picasso;


import java.io.File;

public class AdvHome extends Fragment {
    Dson n = Dson.newNull();
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        n = Dson.readJson(getArguments().getString("today")) ;

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_advhome, container, false);
        try {
            findView(v, R.id.textView7hdr, TextView.class).setText(n.get("msgDescription").asString());
            findView(v, R.id.textView7, TextView.class).setText(  n.get("msgDescriptionDesc").asString());
            findView(v, R.id.textView5hdr, TextView.class).setText(n.get("msgTitle").asString());
            findView(v, R.id.textView5, TextView.class).setText(n.get("msgTitleDesc").asString());
            findView(v, R.id.textView6hdr, TextView.class).setText(  n.get("msgValiduntil").asString());
            findView(v, R.id.textView6, TextView.class).setText(n.get("msgValiduntilDesc").asString());
            Picasso.get().load(n.get("msgIcon").asString()).into( findView(v, R.id.imageView3, ImageView.class)  );
            //imageView3
        }catch (Exception e){}
        return v;
    }

    public <T extends View> T findView(View v, int id, Class<? super T> s) {
        return (T) v.findViewById(id);
    }
}