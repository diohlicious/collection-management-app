package com.studioh.cma.fav.partner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import com.studioh.cma.infoupd.AddressesFragment;
import com.studioh.cma.infoupd.AssetFragment;
import com.studioh.cma.infoupd.ContactsFragment;
import com.studioh.cma.infoupd.InfoMitraFragment;

public class PartnerAdapter extends FragmentPagerAdapter {

    private int numOfTabs;

    public PartnerAdapter(FragmentManager fm, int numOfTabs) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.numOfTabs = numOfTabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new PartnerAssignFragment();
            case 1:
                return new PartnerListFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}
