package com.studioh.cma.main;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import com.naa.data.Dson;
import com.studioh.cma.R;
import com.studioh.cma.fav.*;
import com.studioh.cma.fav.partner.PartnerActivity;
import com.studioh.cma.infoupd.AddressesAdd;
import com.studioh.cma.main.homeadapter.AdvHome;
import com.studioh.cma.main.homeadapter.PageAdapter;
import com.studioh.cma.main.homeadapter.pageindicator.CirclePageIndicator;
import com.studioh.cma.recovery.RecoveryActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import static com.naa.utils.InternetX.getSetting;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        view.findViewById(R.id.attendance).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AttendanceActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
        view.findViewById(R.id.route).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RouteActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
        view.findViewById(R.id.partner).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PartnerActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
        view.findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
        view.findViewById(R.id.appointment).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AppointmentActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
        view.findViewById(R.id.recovery).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RecoveryListActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
        view.findViewById(R.id.archieve).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ArchiveActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
        view.findViewById(R.id.find).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FindActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
        advToday(view);
        return view;

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

    private int count ;
    private Handler handler;
    private Dson dson = new Dson();
    public void advToday(View view){
        ViewPager page = view.findViewById(R.id.pageframe);
        List<Fragment> fragments = new ArrayList<Fragment>();

        //final Dson dson = Dson.readJson(getSetting("Today"));
        //Dson dson = new Dson();
        AssetManager assetManager = getActivity().getAssets();
        try {
            InputStream input = assetManager.open("adhome.json");
            dson = Dson.readDson(isToString(input));
            //
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < dson.size(); i++) {
            Bundle bundle = new Bundle();
            bundle.putString("today", dson.get(i).toJson());

            Fragment fr = Fragment.instantiate(getActivity().getApplicationContext(), AdvHome.class.getName());
            fr.setArguments(bundle);
            fragments.add(fr);
        }

        final int delayms = 5000;
        PageAdapter adapter = new PageAdapter(getActivity().getSupportFragmentManager(), fragments);
        page.setAdapter(adapter);
        final CirclePageIndicator mIndicator = new CirclePageIndicator(getActivity().getApplicationContext(), null);
        mIndicator.setViewPager(page);
        mIndicator.setCurrentItem(1);
        ((FrameLayout)view.findViewById(R.id.indicator)).addView(mIndicator, new FrameLayout.LayoutParams(getResources().getDisplayMetrics().widthPixels, FrameLayout.LayoutParams.MATCH_PARENT));
        mIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageSelected(int arg0) {
                count = arg0;
                if (handler!=null) {
                    handler.removeMessages(1);
                }
            }
            public void onPageScrolled(int arg0, float arg1, int arg2) {}
            public void onPageScrollStateChanged(int arg0) {}
        });
        if (handler!=null){
            handler.removeMessages(1);
        }
        handler = new Handler(){
            public void handleMessage(Message msg) {
                if (msg.what==1) {
                    if (mIndicator!=null) {
                        count++;
                        count = count >= dson.size() ? 0 : count;
                        try {
                            mIndicator.setCurrentItem(count);
                        }catch (Exception e){}
                    }
                }
            }
        };
        handler.sendEmptyMessageDelayed(1, delayms);
    }
}