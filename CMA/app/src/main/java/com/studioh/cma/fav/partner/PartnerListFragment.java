package com.studioh.cma.fav.partner;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.studioh.cma.R;
import com.studioh.cma.infoupd.AddressesAdd;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PartnerListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PartnerListFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PartnerListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PartnerListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PartnerListFragment newInstance(String param1, String param2) {
        PartnerListFragment fragment = new PartnerListFragment();
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
        View view =  inflater.inflate(R.layout.fragment_partner_list, container, false);
        view.findViewById(R.id.containerAdd).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PartnerAddActivity.class);
                startActivityForResult(intent, 12);
            }
        });
        TextView vtHdr = view.findViewById(R.id.container_toolbar_txt);
        vtHdr.setText("Partner List");
        return view;
    }
}