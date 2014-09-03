package com.example.fragmentbasic;

import com.example.fragmentbasic.R;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MailFragment extends Fragment {

	// Returns a new instance of this fragment for the given section number.
	public static MailFragment newInstance() {
		MailFragment fragment = new MailFragment();
		return fragment;
	}

	public MailFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_mail, container, false);
		return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((FragmentMain) activity).onSectionAttached(2);
	}
}
