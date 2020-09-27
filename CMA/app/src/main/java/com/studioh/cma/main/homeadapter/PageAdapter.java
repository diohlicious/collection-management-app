package com.studioh.cma.main.homeadapter;




import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;


public class PageAdapter extends FragmentPagerAdapter {

	private final List<Fragment> fragments;
	public PageAdapter(FragmentManager fm, List<Fragment> fragments) {
		super(fm);
		this.fragments = fragments;
	}

	@Override
	public Fragment getItem(int position) {
		return this.fragments.get(position);
	}

	@Override
	public int getCount() {
		return this.fragments.size();
	}
	

	public CharSequence getPageTitle(int position) {
            return "      " + (position+1) + "      ";
    }

}
